package org.anas.bidderx_rest.validators;

import io.micrometer.common.util.StringUtils;
import org.anas.bidderx_rest.domain.AppUser;
import org.anas.bidderx_rest.domain.enums.WalletType;
import org.anas.bidderx_rest.exceptions.InvalidAmountException;
import org.anas.bidderx_rest.exceptions.InvalidWalletTypeException;
import org.anas.bidderx_rest.exceptions.WalletAlreadyExistsException;
import org.anas.bidderx_rest.repository.WalletRepository;
import org.anas.bidderx_rest.web.vm.WalletVM;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;


@Component
public class WalletValidator {

    public void validateDepositAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Deposit amount must be greater than zero");
        }
    }

    public void validateWalletTypeAndRequirements(WalletVM request) {
        // Validate wallet type
        WalletType walletType;
        try {
            walletType = WalletType.valueOf(request.getType().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new InvalidWalletTypeException("Invalid wallet type: " + request.getType());
        }

        // Validate type-specific requirements
        if (walletType == WalletType.FIAT) {
            validateFiatWalletRequirements(request);
        } else {
            validateNonFiatWalletRequirements(request);
        }
    }

    private void validateFiatWalletRequirements(WalletVM request) {
        if (request.getDepositAmount() == null || request.getDepositAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Positive deposit amount is required for FIAT wallets");
        }

        if (StringUtils.isBlank(request.getCurrencyCode())) {
            throw new InvalidWalletTypeException("Currency code is required for FIAT wallets");
        }
    }

    private void validateNonFiatWalletRequirements(WalletVM request) {
        if (request.getDepositAmount() != null || request.getCurrencyCode() != null) {
            throw new InvalidWalletTypeException("Deposit fields only allowed for FIAT wallets");
        }
    }
}
