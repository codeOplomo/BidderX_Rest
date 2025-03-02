package org.anas.bidderx_rest.web.api;

import org.anas.bidderx_rest.domain.AppUser;
import org.anas.bidderx_rest.domain.Wallet;
import org.anas.bidderx_rest.service.WalletService;
import org.anas.bidderx_rest.service.dto.ApiResponse;
import org.anas.bidderx_rest.web.vm.DepositVM;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/wallets")
public class WalletController {
    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping("/deposit")
    public ResponseEntity<ApiResponse<Wallet>> depositFunds(
            @RequestBody DepositVM request,
            @AuthenticationPrincipal AppUser currentUser) {
        Wallet updatedWallet = walletService.depositFunds(currentUser, request.getAmount());
        return ResponseEntity.ok(new ApiResponse<>("Deposit successful", updatedWallet));
    }
}
