package org.anas.bidderx_rest.service.implementations;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import org.anas.bidderx_rest.domain.AppUser;
import org.anas.bidderx_rest.exceptions.PaymentProcessingException;
import org.anas.bidderx_rest.service.PaymentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;


    @Service
    public class PaymentServiceImpl implements PaymentService {

        @Value("${stripe.success-url}")
        private String successUrl;

        @Value("${stripe.cancel-url}")
        private String cancelUrl;

        @Value("${stripe.secret-key}")
        private String stripeSecretKey;

    public String createDepositSession(UUID walletId, BigDecimal amount, String currency)
            throws StripeException {  // Add throws declaration
        Stripe.apiKey = stripeSecretKey;

        Map<String, Object> params = new HashMap<>();
        params.put("mode", "payment");
        params.put("success_url", successUrl + "?sessionId={CHECKOUT_SESSION_ID}");
        params.put("cancel_url", cancelUrl);
        params.put("client_reference_id", walletId.toString());
        params.put("metadata", Map.of("purpose", "wallet_deposit"));

        params.put("line_items", List.of(Map.of(
                "price_data", Map.of(
                        "currency", currency.toLowerCase(),
                        "product_data", Map.of("name", "Wallet Deposit"),
                        "unit_amount", amount.multiply(BigDecimal.valueOf(100)).longValueExact()
                ),
                "quantity", 1
        )));

        try {
            return Session.create(params).getUrl();
        } catch (StripeException e) {
            // Log the exception
            // You might want to use a proper logging framework
            System.err.println("Stripe API call failed: " + e.getMessage());
            throw e; // Re-throw or handle as appropriate for your application
        }
    }
}