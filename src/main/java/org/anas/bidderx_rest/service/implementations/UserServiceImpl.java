package org.anas.bidderx_rest.service.implementations;

import jakarta.transaction.Transactional;
import org.anas.bidderx_rest.domain.AppUser;
import org.anas.bidderx_rest.exceptions.*;
import org.anas.bidderx_rest.repository.AppUserRepository;
import org.anas.bidderx_rest.service.UserService;
import org.anas.bidderx_rest.service.dto.ProfileDTO;
import org.anas.bidderx_rest.service.dto.mapper.AppUserMapper;
import org.anas.bidderx_rest.web.vm.PasswordUpdateVM;
import org.anas.bidderx_rest.web.vm.ProfileUpdateVM;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
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


    public AppUser findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }
    @Override
    public ProfileDTO getUserProfile(String email) {
        return appUserMapper.toProfileDTO(findUserByEmail(email));
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
