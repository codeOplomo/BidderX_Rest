package org.anas.bidderx_rest.service;

import org.anas.bidderx_rest.domain.AppUser;

import java.math.BigDecimal;
import java.util.UUID;

public interface PaymentService {

    String createDepositSession(UUID walletId, BigDecimal amount, String currency) throws Exception;
}
