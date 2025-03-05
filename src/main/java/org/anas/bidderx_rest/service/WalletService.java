package org.anas.bidderx_rest.service;

import org.anas.bidderx_rest.domain.AppUser;
import org.anas.bidderx_rest.domain.Wallet;
import org.anas.bidderx_rest.service.dto.WalletDTO;
import org.anas.bidderx_rest.web.vm.WalletVM;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;


public interface WalletService {
    WalletDTO findByUser(AppUser user);

    Wallet findById(UUID walletId);

    WalletDTO depositFunds(UUID walletId, BigDecimal amount, String currency, String sessionId);

    WalletDTO depositFundsForUser(AppUser user, BigDecimal amount);

    WalletDTO connectWallet(AppUser user, WalletVM request);
}
