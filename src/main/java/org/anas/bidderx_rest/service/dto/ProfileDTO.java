package org.anas.bidderx_rest.service.dto;

import org.anas.bidderx_rest.domain.enums.OwnerRequestStatus;
import org.anas.bidderx_rest.domain.enums.Role;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProfileDTO {
    private String email;
    private String profileIdentifier;
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private String imageUrl;
    private String coverImageUrl;
    private WalletDTO wallet;
    private List<CollectionDTO> collections;
    private boolean hasWallet;
    private List<String> roles;
    private OwnerRequestStatus ownerRequestStatus;


    public ProfileDTO() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfileIdentifier() {
        return profileIdentifier;
    }

    public void setProfileIdentifier(String profileIdentifier) {
        this.profileIdentifier = profileIdentifier;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public WalletDTO getWallet() {
        return wallet;
    }

    public void setWallet(WalletDTO wallet) {
        this.wallet = wallet;
    }

    public List<CollectionDTO> getCollections() {
        return collections;
    }

    public void setCollections(List<CollectionDTO> collections) {
        this.collections = collections;
    }

    public boolean isHasWallet() {
        return hasWallet;
    }

    public void setHasWallet(boolean hasWallet) {
        this.hasWallet = hasWallet;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public OwnerRequestStatus getOwnerRequestStatus() {
        return ownerRequestStatus;
    }

    public void setOwnerRequestStatus(OwnerRequestStatus ownerRequestStatus) {
        this.ownerRequestStatus = ownerRequestStatus;
    }
}
