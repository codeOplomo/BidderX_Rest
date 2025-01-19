package org.anas.bidderx_rest.service;

import org.anas.bidderx_rest.domain.AppUser;
import org.anas.bidderx_rest.service.dto.ProfileDTO;
import org.anas.bidderx_rest.web.vm.PasswordUpdateVM;
import org.anas.bidderx_rest.web.vm.ProfileUpdateVM;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    void uploadProfileImage(AppUser user, String imageUrl);

    void uploadCoverImage(AppUser user, String imageUrl);

    ProfileDTO getUserProfile(String email);

    ProfileDTO updateUserProfile(String email, ProfileUpdateVM profileUpdate);

    void updateUserPassword(String email, PasswordUpdateVM passwordUpdate);

}
