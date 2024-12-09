package org.anas.bidderx_rest.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.anas.bidderx_rest.annotations.UniqueUsername;
import org.anas.bidderx_rest.repository.AppUserRepository;
import org.springframework.stereotype.Component;

@Component
public class UniqueUsernameValidator implements ConstraintValidator<UniqueUsername, String> {
    private final AppUserRepository userRepository;

    public UniqueUsernameValidator(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        return username != null && userRepository.findByEmail(username).isEmpty();
    }
}
