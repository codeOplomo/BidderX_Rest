package org.anas.bidderx_rest.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
//import org.anas.hunters_league.annotations.UniqueCin;
import org.anas.bidderx_rest.repository.AppUserRepository;
import org.springframework.stereotype.Component;

//@Component
//public class UniqueCinValidator implements ConstraintValidator<UniqueCin, String> {
////    private final AppUserRepository userRepository;
////
////    public UniqueCinValidator(AppUserRepository userRepository) {
////        this.userRepository = userRepository;
////    }
////
////    @Override
////    public boolean isValid(String cin, ConstraintValidatorContext context) {
////        return cin != null && userRepository.findByCin(cin).isEmpty();
////    }
//}
