package account.security;

import account.model.User;
import account.repo.UserRepo;
import account.service.LoginAttemptService;
import account.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFailureListener implements
        ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    private final LoginAttemptService loginAttemptService;
    private final SecurityService service;
    private final UserRepo userRepo;
    private final SecurityService securityService;

    @Autowired
    public AuthenticationFailureListener(LoginAttemptService loginAttemptService, SecurityService service, UserRepo userRepo, SecurityService securityService) {
        this.loginAttemptService = loginAttemptService;
        this.service = service;
        this.userRepo = userRepo;
        this.securityService = securityService;
    }

    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
        String user = ((String) event.getAuthentication().getPrincipal()).toLowerCase();
        loginAttemptService.handleLoginFailedForAddress(user.toLowerCase());
        service.logFailedLogin(user);
        boolean shouldBlockAddress = loginAttemptService.shouldBlockUser(user);
        if(shouldBlockAddress) {
            User dbUser = userRepo.findUserByEmailIgnoreCase(user)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found!"));
            if(dbUser.getRoles().contains("ROLE_ADMINISTRATOR"))
                return;
            securityService.logBruteForce(user);
            loginAttemptService.invalidateAllSavedDataFor(user);
            dbUser.setBlocked(true);
            userRepo.save(dbUser);
            securityService.logLockUser(dbUser.getEmail());
        }
    }

}
