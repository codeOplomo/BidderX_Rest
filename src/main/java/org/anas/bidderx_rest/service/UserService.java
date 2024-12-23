package org.anas.bidderx_rest.service;

import org.anas.bidderx_rest.domain.AppUser;
import org.anas.bidderx_rest.service.dto.ProfileDTO;
import org.anas.bidderx_rest.web.vm.PasswordUpdateVM;
import org.anas.bidderx_rest.web.vm.ProfileUpdateVM;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    ProfileDTO uploadUserProfileImage(Authentication authentication, MultipartFile imageFile);

    ProfileDTO getUserProfile(String email);

    ProfileDTO updateUserProfile(Authentication authentication, ProfileUpdateVM profileUpdateVM);

    void updateUserPassword(Authentication authentication, PasswordUpdateVM passwordUpdateVM);
}
