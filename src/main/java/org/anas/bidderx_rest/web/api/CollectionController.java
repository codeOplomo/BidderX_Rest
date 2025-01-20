package org.anas.bidderx_rest.web.api;

import jakarta.validation.Valid;
import org.anas.bidderx_rest.domain.AppCollection;
import org.anas.bidderx_rest.service.CollectionService;
import org.anas.bidderx_rest.service.dto.ApiResponse;
import org.anas.bidderx_rest.service.dto.CollectionDTO;
import org.anas.bidderx_rest.web.vm.CollectionVM;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/collections")
public class CollectionController {
    private final CollectionService collectionService;

    public CollectionController(CollectionService collectionService) {
        this.collectionService = collectionService;
    }

    @GetMapping("/user")
    public ResponseEntity<ApiResponse<List<CollectionDTO>>> getCollectionsByEmail(@RequestParam String email) {
        List<CollectionDTO> collections = collectionService.getCollectionsByEmail(email);
        return ResponseEntity.ok(new ApiResponse<>("Collections retrieved successfully", collections));
    }


    @PostMapping("/create")
    public ResponseEntity<ApiResponse<CollectionDTO>> createCollection(
            @RequestBody @Valid CollectionVM createVM,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        CollectionDTO collectionDTO = collectionService.createCollection(userDetails.getUsername(), createVM);
        return ResponseEntity.ok(new ApiResponse<>("Collection created successfully", collectionDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CollectionDTO>> getCollectionById(@PathVariable UUID id) {
        CollectionDTO collectionDTO = collectionService.getCollectionById(id);
        return ResponseEntity.ok(new ApiResponse<>("Collection retrieved successfully", collectionDTO));
    }

}
