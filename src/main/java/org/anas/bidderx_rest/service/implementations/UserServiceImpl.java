package org.anas.bidderx_rest.service.implementations;

import jakarta.transaction.Transactional;
import org.anas.bidderx_rest.domain.AppUser;
import org.anas.bidderx_rest.exceptions.EmailAlreadyExistException;
import org.anas.bidderx_rest.exceptions.InvalidPasswordException;
import org.anas.bidderx_rest.exceptions.UserNotFoundException;
import org.anas.bidderx_rest.repository.AppUserRepository;
import org.anas.bidderx_rest.service.UserService;
import org.anas.bidderx_rest.service.dto.ProfileDTO;
import org.anas.bidderx_rest.service.dto.mapper.AppUserMapper;
import org.anas.bidderx_rest.web.vm.PasswordUpdateVM;
import org.anas.bidderx_rest.web.vm.ProfileUpdateVM;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class UserServiceImpl implements UserService {
    private final AppUserRepository userRepository;
    private final AppUserMapper appUserMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(AppUserRepository userRepository, AppUserMapper appUserMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.appUserMapper = appUserMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public ProfileDTO uploadUserProfileImage(Authentication authentication, MultipartFile imageFile) {
        // Get the current authenticated user
        String currentUsername = authentication.getName();
        AppUser currentUser = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Validate image file
        String contentType = imageFile.getContentType();
        if (contentType == null || (!contentType.startsWith("image/"))) {
            throw new IllegalArgumentException("Uploaded file is not a valid image");
        }

        // Store image details
//            currentUser.setImageName(imageFile.getOriginalFilename());
//            currentUser.setImageType(imageFile.getContentType());
//            currentUser.setImageData(imageFile.getBytes());

        // Save the updated user
        AppUser updatedUser = userRepository.save(currentUser);
        return appUserMapper.toProfileDTO(updatedUser);
    }

    @Override
    public ProfileDTO getUserProfile(String email) {
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        return appUserMapper.toProfileDTO(user);
    }

    @Transactional
    public ProfileDTO updateUserProfile(
            Authentication authentication,
            ProfileUpdateVM profileUpdateVM
    ) {
        // Get the current authenticated user
        String currentUsername = authentication.getName();
        AppUser currentUser = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Update user details
        currentUser.setProfileIdentifier(profileUpdateVM.getProfileIdentifier());
        currentUser.setFirstName(profileUpdateVM.getFirstName());
        currentUser.setLastName(profileUpdateVM.getLastName());
        currentUser.setPhoneNumber(profileUpdateVM.getPhoneNumber());

        AppUser updatedUser = userRepository.save(currentUser);
        return appUserMapper.toProfileDTO(updatedUser);
    }

    @Transactional
    public void updateUserPassword(
            Authentication authentication,
            PasswordUpdateVM passwordUpdateVM
    ) {
        // Get the current authenticated user
        String currentUsername = authentication.getName();
        AppUser currentUser = userRepository.findByEmail(currentUsername)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Verify the old password
        if (!passwordEncoder.matches(passwordUpdateVM.getOldPassword(), currentUser.getPassword())) {
            throw new InvalidPasswordException("Current password is incorrect");
        }

        // Encode and set the new password
        String encodedNewPassword = passwordEncoder.encode(passwordUpdateVM.getNewPassword());
        currentUser.setPassword(encodedNewPassword);

        userRepository.save(currentUser);
    }
}
