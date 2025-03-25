package org.anas.bidderx_rest.web.api;


import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import jakarta.servlet.http.HttpServletRequest;
import org.anas.bidderx_rest.domain.AppUser;
import org.anas.bidderx_rest.domain.Wallet;
import org.anas.bidderx_rest.exceptions.PaymentVerificationException;
import org.anas.bidderx_rest.service.TransactionService;
import org.anas.bidderx_rest.service.UserService;
import org.anas.bidderx_rest.service.WalletService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping("/api/stripe")
public class StripeController {
    private final WalletService walletService;
    private final TransactionService transactionService;
    private final UserService userService;

    public StripeController(WalletService walletService, TransactionService transactionService, UserService userService) {
        this.walletService = walletService;
        this.transactionService = transactionService;
        this.userService = userService;
    }

    @PreAuthorize("!principal.banned")
    @PostMapping("/confirm-payment")
    public ResponseEntity<?> confirmPayment(@RequestParam String sessionId) {
        try {
            Session session = Session.retrieve(sessionId);

            if ("paid".equals(session.getPaymentStatus())) {
                processCompletedSession(session);
                return ResponseEntity.ok(Map.of(
                        "status", "success",
                        "message", "Payment confirmed"
                ));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Payment not completed"));

        } catch (StripeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Payment verification failed"));
        }
    }


    private void processCompletedSession(Session session) {
        String sessionId = session.getId();
        if (transactionService.existsBySessionId(sessionId)) {
            return;
        }

        UUID walletId = UUID.fromString(session.getClientReferenceId());
        BigDecimal amount = BigDecimal.valueOf(session.getAmountTotal())
                .divide(BigDecimal.valueOf(100));
        String currency = session.getCurrency().toUpperCase();

        walletService.depositFunds(walletId, amount, currency, sessionId);

        Wallet wallet = walletService.findById(walletId);
        AppUser user = wallet.getUser();
        if (!user.isHasWallet()) {
            user.setHasWallet(true);
            userService.save(user);
        }
    }


//    @PostMapping("/webhook")
//    public ResponseEntity<String> handleStripeWebhook(HttpServletRequest request, @RequestBody String payload) {
//
//        System.out.println("Received webhook payload: " + payload);
//        String sigHeader = request.getHeader("Stripe-Signature");
//        Event event;
//
//        try {
//            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
//        } catch (SignatureVerificationException e) {
//            System.out.println("Invalid webhook signature");
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
//        }
//
//        try {
//            processWebhookEvent(event);
//            return ResponseEntity.ok().build();
//        } catch (Exception e) {
//            System.out.println("Error processing webhook event");
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Processing error");
//        }
//    }
//
//    private void processWebhookEvent(Event event) {
//        switch (event.getType()) {
//            case "checkout.session.completed":
//                handleCheckoutSessionCompleted(event);
//                break;
//            case "charge.succeeded":
//                handleChargeSucceeded(event);
//                break;
//            case "payment_intent.succeeded":
//                handlePaymentIntentSucceeded(event);
//                break;
//            case "refund.created":
//                handleRefundCreated(event);
//                break;
//            case "payment_intent.payment_failed":
//                handlePaymentFailed(event);
//                break;
//            default:
//                System.out.println("Unhandled event type: " + event.getType());
//        }
//    }
//
//    private void handleCheckoutSessionCompleted(Event event) {
//        Session session = (Session) event.getDataObjectDeserializer().getObject().orElseThrow();
//
//        if ("complete".equals(session.getStatus()) &&
//                "paid".equals(session.getPaymentStatus())) {
//
//            String sessionId = session.getId();
//            UUID walletId = extractWalletId(session);
//            BigDecimal amount = convertAmountFromStripe(session.getAmountTotal());
//            String currency = session.getCurrency().toUpperCase();
//
//            processDeposit(walletId, amount, currency, sessionId);
//        }
//    }
//
//    private void handleChargeSucceeded(Event event) {
//        Charge charge = (Charge) event.getDataObjectDeserializer().getObject().orElseThrow();
//
//        UUID walletId = findWalletIdFromCharge(charge);
//        if (walletId != null) {
//            BigDecimal amount = convertAmountFromStripe(charge.getAmount());
//            String currency = charge.getCurrency().toUpperCase();
//            String sessionId = charge.getPaymentIntent();
//
//            processDeposit(walletId, amount, currency, sessionId);
//        }
//    }
//
//    private void handlePaymentIntentSucceeded(Event event) {
//        PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer().getObject().orElseThrow();
//
//        UUID walletId = findWalletIdFromPaymentIntent(paymentIntent);
//        if (walletId != null) {
//            BigDecimal amount = convertAmountFromStripe(paymentIntent.getAmount());
//            String currency = paymentIntent.getCurrency().toUpperCase();
//            String sessionId = paymentIntent.getId();
//
//            processDeposit(walletId, amount, currency, sessionId);
//        }
//    }
//
//    private void handleRefundCreated(Event event) {
//        Refund refund = (Refund) event.getDataObjectDeserializer().getObject().orElseThrow();
//
//        UUID walletId = findWalletIdFromRefund(refund);
//        if (walletId != null) {
//            BigDecimal amount = convertAmountFromStripe(refund.getAmount());
//            String currency = refund.getCurrency().toUpperCase();
//
//            processRefund(walletId, amount, currency, refund.getId());
//        }
//    }
//
//    private void handlePaymentFailed(Event event) {
//        PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer().getObject().orElseThrow();
//
////        logger.warn("Payment failed for payment intent: {}", paymentIntent.getId());
//        // Optionally log failed payment or trigger additional handling
//    }
//
//    private UUID extractWalletId(Session session) {
//        // Extract wallet ID from client reference ID or metadata
//        return UUID.fromString(session.getClientReferenceId());
//    }
//
//    private UUID findWalletIdFromCharge(Charge charge) {
//        // Implement wallet ID extraction logic
//        // Could check payment intent or checkout session metadata
//        return null;
//    }
//
//    private UUID findWalletIdFromPaymentIntent(PaymentIntent paymentIntent) {
//        // Implement wallet ID extraction logic for payment intents
//        return null;
//    }
//
//    private UUID findWalletIdFromRefund(Refund refund) {
//        // Implement wallet ID extraction logic for refunds
//        return null;
//    }
//
//    private BigDecimal convertAmountFromStripe(Long stripeAmount) {
//        return BigDecimal.valueOf(stripeAmount)
//                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
//    }
//
//    private void processDeposit(UUID walletId, BigDecimal amount, String currency, String sessionId) {
//        if (transactionService.existsBySessionId(sessionId)) {
////            logger.info("Transaction already processed for session: {}", sessionId);
//            return;
//        }
//
//        try {
//            walletService.depositFunds(walletId, amount, currency, sessionId);
////            logger.info("Successfully processed deposit for wallet: {}", walletId);
//        } catch (Exception e) {
////            logger.error("Failed to process deposit for wallet: {}", walletId, e);
//            // Consider implementing retry mechanism or error tracking
//        }
//    }
//
//    private void processRefund(UUID walletId, BigDecimal amount, String currency, String refundId) {
////        if (transactionService.existsByRefundId(refundId)) {
//////            logger.info("Refund already processed for ID: {}", refundId);
////            return;
////        }
//
//        try {
////            walletService.refundFunds(walletId, amount, currency, refundId);
////            logger.info("Successfully processed refund for wallet: {}", walletId);
//        } catch (Exception e) {
////            logger.error("Failed to process refund for wallet: {}", walletId, e);
//            // Consider implementing retry mechanism or error tracking
//        }
//    }
//    @PostMapping("/deposits/confirm")
//    public ResponseEntity<?> confirmDeposit(
//            @RequestParam("session_id") String sessionId) {
//
//        try {
//            // 1. Verify payment with Stripe
//            Session session = Session.retrieve(sessionId);
//
//            if (!"paid".equals(session.getPaymentStatus())) {
//                throw new PaymentVerificationException("Payment not completed");
//            }
//
//            // 2. Update wallet
//            UUID walletId = UUID.fromString(session.getClientReferenceId());
//            BigDecimal amount = BigDecimal.valueOf(session.getAmountTotal())
//                    .divide(BigDecimal.valueOf(100));
//
//            walletService.depositFunds(
//                    walletId,
//                    amount,
//                    session.getCurrency().toUpperCase()
//            );
//
//            return ResponseEntity.ok().build();
//
//        } catch (StripeException e) {
//            throw new PaymentVerificationException("Failed to verify payment");
//        }
//    }
//    @PostMapping("/webhook")
//    public ResponseEntity<String> handleWebhook(
//            @RequestBody String payload,
//            @RequestHeader("Stripe-Signature") String sigHeader) {
//
//        System.out.println("Received webhook payload: " + payload);
//        System.out.println("Received webhook signature: " + sigHeader);
//        try {
//            Event event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
//
//            System.out.println("Received event: " + event.getType());
//
//            // Updated webhook handler section
//            if ("checkout.session.completed".equals(event.getType())) {
//                // Use raw JSON instead of deserializer
//                JsonObject sessionJson = event.getData().getObject().getAsJsonObject();
//
//                Session session = Session.fromJson(sessionJson.toString());
//
//                System.out.println("Manual session parsing:");
//                System.out.println("ID: " + session.getId());
//                System.out.println("Payment Status: " + session.getPaymentStatus());
//
//                handleSuccessfulPayment(session);
//            }
//
//            return ResponseEntity.ok().build();
//
//        } catch (SignatureVerificationException e) {
//            System.err.println("Webhook signature verification failed");
//            return ResponseEntity.status(403).build();
//        } catch (Exception e) {
//            System.err.println("Failed to handle webhook event" + e.getMessage());
//            return ResponseEntity.internalServerError().build();
//        }
//    }
//
//    private void handleSuccessfulPayment(Session session) {
//        try {
//            System.out.println("=== STARTING PAYMENT HANDLING ===");
//
//            // 1. Log full session details
//            System.out.println("Session ID: " + session.getId());
//            System.out.println("Client Ref: " + session.getClientReferenceId());
//            System.out.println("Amount Total: " + session.getAmountTotal());
//            System.out.println("Currency: " + session.getCurrency());
//            System.out.println("Metadata: " + session.getMetadata());
//
//            // 2. Validate client reference ID
//            if (session.getClientReferenceId() == null) {
//                throw new IllegalArgumentException("Client reference ID is missing");
//            }
//
//            UUID walletId = UUID.fromString(session.getClientReferenceId());
//            System.out.println("Parsed Wallet ID: " + walletId);
//
//            // 3. Validate amount
//            if (session.getAmountTotal() == null) {
//                throw new IllegalArgumentException("Amount total is missing");
//            }
//
//            BigDecimal amount = BigDecimal.valueOf(session.getAmountTotal())
//                    .divide(BigDecimal.valueOf(100));
//            System.out.println("Converted Amount: " + amount + " " + session.getCurrency());
//
//            // 4. Check wallet existence
//            Wallet wallet = walletService.findById(walletId)
//                    .orElseThrow(() -> new WalletNotFoundException("Wallet not found: " + walletId));
//            System.out.println("Found Wallet: " + wallet.getId());
//
//            // 5. Attempt deposit
//            walletService.depositFunds(walletId, amount, session.getCurrency());
//            System.out.println("=== PAYMENT PROCESSED SUCCESSFULLY ===");
//
//        } catch (Exception e) {
//            System.err.println("!!! PAYMENT HANDLING FAILED !!!");
//            e.printStackTrace(); // This will show the exact error line
//            throw e; // Re-throw to trigger 500 response
//        }
//    }
}
