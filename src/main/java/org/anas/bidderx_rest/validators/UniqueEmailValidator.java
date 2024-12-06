package org.anas.bidderx_rest.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.anas.bidderx_rest.annotations.UniqueEmail;
import org.anas.bidderx_rest.repository.AppUserRepository;
import org.springframework.stereotype.Component;

@Component
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {
    private final AppUserRepository userRepository;

    public UniqueEmailValidator(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        return email != null && userRepository.findByEmail(email).isEmpty();
    }
}
