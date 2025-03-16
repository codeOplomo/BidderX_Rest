package org.anas.bidderx_rest.service.implementations;


import org.anas.bidderx_rest.domain.Wallet;
import org.anas.bidderx_rest.exceptions.InsufficientFundsException;
import org.anas.bidderx_rest.repository.WalletRepository;
import org.anas.bidderx_rest.service.PaymentService;
import org.anas.bidderx_rest.service.TransactionService;
import org.anas.bidderx_rest.service.dto.WalletDTO;
import org.anas.bidderx_rest.service.dto.mapper.WalletMapper;
import org.anas.bidderx_rest.validators.WalletValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletServiceImplTest {

    @InjectMocks
    private WalletServiceImpl walletService;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private WalletValidator walletValidator;

    @Mock
    private TransactionService transactionService;

    @Mock
    private WalletMapper walletMapper;

    @Mock
    private PaymentService paymentService;

    private UUID walletId;
    private Wallet wallet;

    @BeforeEach
    void setUp() {
        walletId = UUID.randomUUID();
        wallet = new Wallet();
        wallet.setId(walletId);
        // Set a default balance (this may be adjusted in individual tests)
        wallet.setBalance(new BigDecimal("100.00"));
    }

    @Test
    void testWithdrawFunds_SufficientFunds() {
        // Define withdrawal amount and expected remaining balance
        BigDecimal withdrawAmount = new BigDecimal("40.00");
        BigDecimal expectedBalance = new BigDecimal("60.00");

        // Prepare the expected WalletDTO (simulate the mapper conversion)
        WalletDTO expectedWalletDTO = new WalletDTO();
        expectedWalletDTO.setId(walletId);
        expectedWalletDTO.setBalance(expectedBalance);

        // Stub repository and mapper behavior
        when(walletRepository.findById(eq(walletId))).thenReturn(Optional.of(wallet));
        // When walletRepository.save is called, simply return the wallet passed in.
        when(walletRepository.save(any(Wallet.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(walletMapper.toDto(wallet)).thenReturn(expectedWalletDTO);

        // Execute the withdrawal
        WalletDTO result = walletService.withdrawFunds(walletId, withdrawAmount);

        // Verify that the wallet's balance is updated correctly
        assertEquals(expectedBalance, wallet.getBalance());
        // Verify that the returned DTO is as expected
        assertEquals(expectedWalletDTO, result);
    }

    @Test
    void testWithdrawFunds_InsufficientFunds() {
        // Set wallet balance lower than the withdrawal amount
        wallet.setBalance(new BigDecimal("50.00"));
        BigDecimal withdrawAmount = new BigDecimal("100.00");

        // Stub repository behavior
        when(walletRepository.findById(eq(walletId))).thenReturn(Optional.of(wallet));

        // Assert that an InsufficientFundsException is thrown
        assertThrows(InsufficientFundsException.class,
                () -> walletService.withdrawFunds(walletId, withdrawAmount));

        // Optionally, verify that walletRepository.save is never called since withdrawal fails
        verify(walletRepository, never()).save(any(Wallet.class));
    }
}
