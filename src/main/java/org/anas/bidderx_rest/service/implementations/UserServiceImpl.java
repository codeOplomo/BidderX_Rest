package org.anas.bidderx_rest.service.implementations;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.anas.bidderx_rest.domain.AppUser;
import org.anas.bidderx_rest.domain.enums.OwnerRequestStatus;
import org.anas.bidderx_rest.domain.enums.Role;
import org.anas.bidderx_rest.exceptions.*;
import org.anas.bidderx_rest.repository.AppUserRepository;
import org.anas.bidderx_rest.service.UserService;
import org.anas.bidderx_rest.service.WalletService;
import org.anas.bidderx_rest.service.dto.ProfileDTO;
import org.anas.bidderx_rest.service.dto.mapper.AppUserMapper;
import org.anas.bidderx_rest.web.vm.PasswordUpdateVM;
import org.anas.bidderx_rest.web.vm.ProfileUpdateVM;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final AppUserRepository userRepository;
    private final AppUserMapper appUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final WalletService walletService;

    public UserServiceImpl(AppUserRepository userRepository, AppUserMapper appUserMapper, PasswordEncoder passwordEncoder, WalletService walletService) {
        this.userRepository = userRepository;
        this.walletService = walletService;
        this.appUserMapper = appUserMapper;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public void banUserByEmail(String email) {
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
        user.setBanned(true);
        userRepository.save(user);
    }

    @Override
    public void unbanUserByEmail(String email) {
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));
        user.setBanned(false);
        userRepository.save(user);
    }

    @Override
    public List<ProfileDTO> getAllUsers() {
        List<AppUser> users = userRepository.findAll();
        return users.stream()
                .map(appUserMapper::toProfileDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void approveOwnerRequestByEmail(String email) {
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));

        user.getRoles().add(Role.OWNER);
        user.setOwnerRequestStatus(OwnerRequestStatus.APPROVED);
        userRepository.save(user);
    }

    @Override
    public void rejectOwnerRequestByEmail(String email) {
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + email));

        user.setOwnerRequestStatus(OwnerRequestStatus.REJECTED);
        userRepository.save(user);
    }

    @Override
    public AppUser findUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
    }

    @Override
    public AppUser save(AppUser user) {
        return userRepository.save(user);
    }

    public AppUser findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }

    public ProfileDTO getUserProfileByEmail(String email) {
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        ProfileDTO profile = appUserMapper.toProfileDTO(user);
        profile.setRoles(user.getRoles().stream().map(Enum::name).collect(Collectors.toList()));
        return profile;
    }

    @Override
    public ProfileDTO getUserProfile(String email) {
        AppUser user = findUserByEmail(email);
        ProfileDTO profileDTO = appUserMapper.toProfileDTO(user);
        if (profileDTO.isHasWallet()) {
            profileDTO.setWallet(walletService.findByUser(user));
        }
        return profileDTO;
    }

    @Override
    @Transactional
    public void uploadProfileImage(AppUser user, String imageUrl) {
        user.setImageUrl(imageUrl);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void uploadCoverImage(AppUser user, String imageUrl) {
        user.setCoverImageUrl(imageUrl);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public ProfileDTO updateUserProfile(String email, ProfileUpdateVM profileUpdate) {
        AppUser user = findUserByEmail(email);

        user.setProfileIdentifier(profileUpdate.getProfileIdentifier());
        user.setFirstName(profileUpdate.getFirstName());
        user.setLastName(profileUpdate.getLastName());
        user.setPhoneNumber(profileUpdate.getPhoneNumber());

        return appUserMapper.toProfileDTO(userRepository.save(user));
    }


    @Override
    @Transactional
    public void updateUserPassword(String email, PasswordUpdateVM passwordUpdate) {
        AppUser user = findUserByEmail(email);

        if (!passwordEncoder.matches(passwordUpdate.getOldPassword(), user.getPassword())) {
            throw new InvalidPasswordException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(passwordUpdate.getNewPassword()));
        userRepository.save(user);
    }


    private void validateImageFile(MultipartFile imageFile) {
        if (imageFile == null || imageFile.isEmpty()) {
            throw new InvalidFileException("Image file is required");
        }

        String contentType = imageFile.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new InvalidFileException("Uploaded file is not a valid image");
        }

        // Add additional validation if needed (file size, dimensions, etc.)
    }

}
