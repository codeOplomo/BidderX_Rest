package org.anas.bidderx_rest.service.implementations;

import org.anas.bidderx_rest.domain.AppUser;
import org.anas.bidderx_rest.domain.Transaction;
import org.anas.bidderx_rest.domain.Wallet;
import org.anas.bidderx_rest.exceptions.WalletNotFoundException;
import org.anas.bidderx_rest.repository.WalletRepository;
import org.anas.bidderx_rest.service.TransactionService;
import org.anas.bidderx_rest.service.WalletService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Service
public class WalletServiceImpl implements WalletService {
    private final WalletRepository walletRepository;
    private final TransactionService transactionService;

    public WalletServiceImpl(WalletRepository walletRepository, TransactionService transactionService) {
        this.walletRepository = walletRepository;
        this.transactionService = transactionService;
    }

    public Wallet depositFunds(AppUser user, BigDecimal amount) {
        // Retrieve the wallet associated with the user
        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found for user with id: " + user.getId()));

        // Update the wallet balance
        wallet.setBalance(wallet.getBalance().add(amount));

        // Record the deposit as a transaction
        Transaction depositTransaction = new Transaction();
        depositTransaction.setWallet(wallet);
        depositTransaction.setAmount(amount);
        depositTransaction.setTransactionDate(LocalDateTime.now());
        depositTransaction.setDescription("Wallet deposit");

        wallet.getTransactions().add(depositTransaction);
        transactionService.save(depositTransaction);

        // Save and return the updated wallet
        return walletRepository.save(wallet);
    }
}
