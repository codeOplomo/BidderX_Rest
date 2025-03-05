package org.anas.bidderx_rest.service.implementations;

import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;
import org.anas.bidderx_rest.domain.AppUser;
import org.anas.bidderx_rest.domain.Transaction;
import org.anas.bidderx_rest.domain.Wallet;
import org.anas.bidderx_rest.domain.enums.TransactionType;
import org.anas.bidderx_rest.domain.enums.WalletType;
import org.anas.bidderx_rest.exceptions.*;
import org.anas.bidderx_rest.repository.WalletRepository;
import org.anas.bidderx_rest.service.PaymentService;
import org.anas.bidderx_rest.service.TransactionService;
import org.anas.bidderx_rest.service.WalletService;
import org.anas.bidderx_rest.service.dto.WalletDTO;
import org.anas.bidderx_rest.service.dto.mapper.WalletMapper;
import org.anas.bidderx_rest.validators.WalletValidator;
import org.anas.bidderx_rest.web.vm.WalletVM;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;


@Service
public class WalletServiceImpl implements WalletService {
    private final WalletRepository walletRepository;
    private final WalletValidator walletValidator;
    private final TransactionService transactionService;
    private final WalletMapper walletMapper;
    private final PaymentService paymentService;

    public WalletServiceImpl(WalletRepository walletRepository, TransactionService transactionService, WalletMapper walletMapper, PaymentService paymentService, WalletValidator walletValidator) {
        this.walletRepository = walletRepository;
        this.paymentService = paymentService;
        this.transactionService = transactionService;
        this.walletMapper = walletMapper;
        this.walletValidator = walletValidator;
    }

    @Override
    public WalletDTO findByUser(AppUser user) {
        return walletRepository.findByUser(user)
                .map(walletMapper::toDto)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found for user with id: " + user.getId()));
    }

    @Override
    public Wallet findById(UUID walletId) {
        return walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found with id: " + walletId));
    }

    @Override
    @Transactional
    public WalletDTO connectWallet(AppUser user, WalletVM request) {

        Optional<Wallet> existingWallet = walletRepository.findByUser(user);
        if (existingWallet.isPresent()) {
            return walletMapper.toDto(existingWallet.get());
        }

        // Continue with validation for new wallet
        walletValidator.validateWalletTypeAndRequirements(request);

        // Parse wallet type (already validated)
        WalletType walletType = WalletType.valueOf(request.getType().toUpperCase());

        // Create and save wallet
        Wallet newWallet = createWallet(user, walletType, request.getCurrencyCode());

        // Handle FIAT wallet checkout if needed
        String checkoutUrl = null;
        if (walletType == WalletType.FIAT) {
            checkoutUrl = createFiatWalletCheckout(newWallet, request);
        }

        // Map to DTO
        WalletDTO walletDto = walletMapper.toDto(newWallet);
        walletDto.setCheckoutUrl(checkoutUrl);
        return walletDto;
    }


    @Override
    @Transactional
    public WalletDTO depositFunds(UUID walletId, BigDecimal amount, String currency, String sessionId) {
        walletValidator.validateDepositAmount(amount);
        Wallet wallet = findById(walletId);

        if (wallet.getType() != WalletType.FIAT) {
            throw new InvalidWalletTypeException("Deposit only allowed for FIAT wallets");
        }

        Wallet updatedWallet = updateWalletBalanceWithDeposit(wallet, amount, currency, sessionId);
        return walletMapper.toDto(updatedWallet);
    }

    @Override
    @Transactional
    public WalletDTO depositFundsForUser(AppUser user, BigDecimal amount) {
        WalletDTO walletDto = findByUser(user);
        if (walletDto.getType() != WalletType.FIAT) {
            throw new InvalidWalletTypeException("Deposits only allowed for FIAT wallets");
        }
        if (walletDto.getCurrencyCode() == null) {
            throw new IllegalStateException("Wallet currency not configured");
        }
        walletValidator.validateDepositAmount(amount);

        String checkoutUrl = createCheckoutSession(walletDto.getId(), amount, walletDto.getCurrencyCode());
        walletDto.setCheckoutUrl(checkoutUrl);
        return walletDto;
    }

    private Wallet createWallet(AppUser user, WalletType walletType, String currency) {
        Wallet newWallet = new Wallet();
        newWallet.setUser(user);
        newWallet.setType(walletType);
        newWallet.setBalance(BigDecimal.ZERO);
        newWallet.setCurrency(currency);
        return walletRepository.save(newWallet);
    }

    private String createCheckoutSession(UUID walletId, BigDecimal amount, String currency) {
        try {
            return paymentService.createDepositSession(walletId, amount, currency);
        } catch (Exception e) {
            throw new PaymentProcessingException("Failed to create payment session: " + e.getMessage(), e);
        }
    }

    private String createFiatWalletCheckout(Wallet wallet, WalletVM request) {
        try {
            return paymentService.createDepositSession(
                    wallet.getId(),
                    request.getDepositAmount(),
                    request.getCurrencyCode()
            );
        } catch (Exception e) {
//            log.error("Failed to create payment session for wallet {}: {}", wallet.getId(), e.getMessage());
            throw new PaymentProcessingException("Failed to create payment session: " + e.getMessage(), e);
        }

    }

    private Wallet updateWalletBalanceWithDeposit(Wallet wallet, BigDecimal amount, String currency, String sessionId) {
        Transaction depositTransaction = createDepositTransaction(wallet, amount, sessionId);
        wallet.setBalance(wallet.getBalance().add(amount));
        wallet.getTransactions().add(depositTransaction);
        return walletRepository.save(wallet);
    }

    private Transaction createDepositTransaction(Wallet wallet, BigDecimal amount, String sessionId) {
        Transaction depositTransaction = new Transaction();
        depositTransaction.setWallet(wallet);
        depositTransaction.setAmount(amount);
        depositTransaction.setTransactionDate(LocalDateTime.now());
        depositTransaction.setDescription("Wallet deposit");
        depositTransaction.setSessionId(sessionId);
        depositTransaction.setType(TransactionType.DEPOSIT);

        // Save transaction
        return transactionService.save(depositTransaction);
    }

}
