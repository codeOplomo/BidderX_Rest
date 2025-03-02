package org.anas.bidderx_rest.service;

import org.anas.bidderx_rest.domain.AppUser;
import org.anas.bidderx_rest.domain.Wallet;

import java.math.BigDecimal;

public interface WalletService {
    Wallet depositFunds(AppUser user, BigDecimal amount);
}
