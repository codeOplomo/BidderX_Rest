package org.anas.bidderx_rest.web.api;

import org.anas.bidderx_rest.domain.AppUser;
import org.anas.bidderx_rest.domain.Wallet;
import org.anas.bidderx_rest.service.WalletService;
import org.anas.bidderx_rest.service.dto.ApiResponse;
import org.anas.bidderx_rest.service.dto.WalletDTO;
import org.anas.bidderx_rest.web.vm.DepositVM;
import org.anas.bidderx_rest.web.vm.WalletVM;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/wallets")
public class WalletController {
    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PreAuthorize("!principal.banned")
    @PostMapping("/connect")
    public ResponseEntity<ApiResponse<WalletDTO>> connectWallet(@AuthenticationPrincipal AppUser currentUser, @RequestBody WalletVM request) {
        WalletDTO newWallet = walletService.connectWallet(currentUser, request);
        return ResponseEntity.ok(new ApiResponse<>("Wallet connected", newWallet));
    }

    @PreAuthorize("!principal.banned")
    @GetMapping
    public ResponseEntity<ApiResponse<WalletDTO>> getWallet(@AuthenticationPrincipal AppUser currentUser) {
        WalletDTO wallet = walletService.findByUser(currentUser);
        return ResponseEntity.ok(new ApiResponse<>("Wallet retrieved", wallet));
    }


    @PreAuthorize("!principal.banned")
    @PostMapping("/deposit")
    public ResponseEntity<ApiResponse<WalletDTO>> userDeposit(
            @AuthenticationPrincipal AppUser currentUser,
            @RequestBody DepositVM request
    ) {
        WalletDTO walletDto = walletService.depositFundsForUser(currentUser, request.getAmount());
        return ResponseEntity.ok(new ApiResponse<>("Checkout session created", walletDto));
    }
}
