package org.anas.bidderx_rest.domain;

import jakarta.persistence.*;
import org.anas.bidderx_rest.domain.enums.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "\"users\"")
public class AppUser implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name="first_name", nullable = false)
    private String firstName;

    @Column(name="last_name", nullable = false)
    private String lastName;

    @Column(name="phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "is_banned", nullable = false)
    private boolean isBanned;

    // One-to-Many relationship with Collection
    @OneToMany(mappedBy = "appUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AppCollection> collections = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bid> bids = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AuctionReaction> auctionReactions = new ArrayList<>();

    @Column(name = "verification_code")
    private String verificationCode;
    @Column(name = "verification_expiration")
    private LocalDateTime verificationCodeExpiresAt;
    private boolean enabled;

    // Constructors
    public AppUser() {}

    public AppUser(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    // Optional: Helper method to add a collection
    public void addCollection(AppCollection collection) {
        if (collection != null) {
            this.collections.add(collection);
            collection.setAppUser(this);
        }
    }

    // Helper method to remove a collection
    public void removeCollection(AppCollection collection) {
        if (collection != null) {
            this.collections.remove(collection);
            collection.setAppUser(null);
        }
    }

    // Helper method to add a bid
    public void addBid(Bid bid) {
        if (bid != null) {
            this.bids.add(bid);
            bid.setUser(this);
        }
    }

    // Helper method to remove a bid
    public void removeBid(Bid bid) {
        if (bid != null) {
            this.bids.remove(bid);
            bid.setUser(null);
        }
    }

    // Helper method to add an auction reaction
    public void addAuctionReaction(AuctionReaction reaction) {
        if (reaction != null) {
            this.auctionReactions.add(reaction);
            reaction.setUser(this);
        }
    }

    // Helper method to remove an auction reaction
    public void removeAuctionReaction(AuctionReaction reaction) {
        if (reaction != null) {
            this.auctionReactions.remove(reaction);
            reaction.setUser(null);
        }
    }

    // Helper method to check if user is verified
    public boolean isVerified() {
        return this.enabled && this.verificationCode == null &&
                (this.verificationCodeExpiresAt == null ||
                        this.verificationCodeExpiresAt.isBefore(LocalDateTime.now()));
    }

    // Helper method to reset verification details
    public void resetVerification() {
        this.verificationCode = null;
        this.verificationCodeExpiresAt = null;
        this.enabled = false;
    }

    // Helper method to check if user can bid (active and not banned)
//    public boolean canBid() {
//        return isActive && !isBanned;
//    }

    // Helper method to get full name
    public String getFullName() {
        return firstName + " " + lastName;
    }

    // Helper method to update user details
    public void updateUserDetails(String firstName, String lastName, String email, String phoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public LocalDateTime getVerificationCodeExpiresAt() {
        return verificationCodeExpiresAt;
    }

    public void setVerificationCodeExpiresAt(LocalDateTime verificationCodeExpiresAt) {
        this.verificationCodeExpiresAt = verificationCodeExpiresAt;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isBanned() {
        return isBanned;
    }

    public void setBanned(boolean banned) {
        isBanned = banned;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<AppCollection> getCollections() {
        return collections;
    }

    public void setCollections(List<AppCollection> collections) {
        this.collections = collections;
    }

    public List<Bid> getBids() {
        return bids;
    }

    public void setBids(List<Bid> bids) {
        this.bids = bids;
    }

    public List<AuctionReaction> getAuctionReactions() {
        return auctionReactions;
    }

    public void setAuctionReactions(List<AuctionReaction> auctionReactions) {
        this.auctionReactions = auctionReactions;
    }

}
