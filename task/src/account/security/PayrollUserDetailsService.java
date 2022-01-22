package account.security;

import account.model.User;
import account.repo.UserRepo;
import account.service.LoginAttemptService;
import account.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;

@Service
public class PayrollUserDetailsService implements UserDetailsService {
    private final UserRepo userRepo;

    @Autowired
    public PayrollUserDetailsService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.findUserByEmailIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));
        if(user.isBlocked()) {
            throw new LockedException("User account is locked");
        }
        return new PayrollUserDetails(user);
    }



}
