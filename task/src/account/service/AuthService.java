package account.service;

import account.model.User;
import account.repo.UserRepo;
import account.validator.PasswordValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@Service
public class AuthService {
    private final UserRepo userRepo;
    private final PasswordEncoder encoder;
    private final PasswordValidator passwordValidator;
    private final SecurityService securityService;

    @Autowired
    public AuthService(UserRepo userRepo,
                       PasswordEncoder encoder,
                       PasswordValidator passwordValidator, SecurityService securityService) {
        this.userRepo = userRepo;
        this.encoder = encoder;
        this.passwordValidator = passwordValidator;
        this.securityService = securityService;
    }

    public User signup(User user) {
        ensureUserIsNotAlreadyRegistered(user);
        passwordValidator.verify(user.getPassword());
        user.setEmail(user.getEmail().toLowerCase());
        user.setPassword(encoder.encode(user.getPassword()));
        assignRole(user);
        User savedUser = userRepo.save(user);
        securityService.logCreateUser(user.getEmail());
        return savedUser;
    }

    public void changePassword(String email, String newPassword, String oldPassword) {
        passwordValidator.verify(newPassword);
        checkPasswordIsDifferent(newPassword, oldPassword);
        updatePassword(email, newPassword);
        securityService.logPasswordChange(email);
    }

    private void checkPasswordIsDifferent(String newPassword, String oldPassword) {
        if (encoder.matches(newPassword, oldPassword)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The passwords must be different!");
        }
    }

    private void updatePassword(String email, String password) {
        userRepo.findUserByEmailIgnoreCase(email).ifPresent(user -> {
            user.setPassword(encoder.encode(password));
            userRepo.save(user);
        });
    }


    private void ensureUserIsNotAlreadyRegistered(User user) {
        userRepo.findUserByEmailIgnoreCase(user.getEmail()).ifPresent((u) -> {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User exist!");
        });
    }

    private void assignRole(User user) {
        if(userRepo.count() > 0) {
            user.setRoles(List.of("ROLE_USER"));
            return;
        }
        user.setRoles(List.of("ROLE_ADMINISTRATOR"));
    }
}
