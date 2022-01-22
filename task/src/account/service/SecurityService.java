package account.service;

import account.model.SecurityAction;
import account.model.SecurityEvent;
import account.repo.SecurityRepo;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {
    private final SecurityRepo securityRepo;

    public SecurityService(SecurityRepo securityRepo) {
        this.securityRepo = securityRepo;
    }

    public Iterable<SecurityEvent> getAllEvents() {
        return securityRepo.findAll();
    }

    public void logCreateUser(String email) {
        SecurityEvent event = new SecurityEvent();
        event.setAction(SecurityAction.CREATE_USER);
        event.setSubject("Anonymous");
        event.setObject(email);
        event.setPath("/api/auth/signup");
        securityRepo.save(event);
    }

    public void logFailedLogin(String failedEmail) {
        SecurityEvent event = new SecurityEvent();
        event.setAction(SecurityAction.LOGIN_FAILED);
        event.setSubject(failedEmail);
        event.setObject("/api/empl/payment");
        event.setPath("/api/empl/payment");
        securityRepo.save(event);
    }

    public void logGrantRole(String doer, String email, String role) {
        SecurityEvent event = new SecurityEvent();
        event.setAction(SecurityAction.GRANT_ROLE);
        event.setSubject(doer);
        event.setObject("Grant role " + role.replace("ROLE_", "") + " to " + email);
        event.setPath("/api/admin/user/role");
        securityRepo.save(event);
    }

    public void logRemoveRole(String doer, String email, String role) {
        SecurityEvent event = new SecurityEvent();
        event.setAction(SecurityAction.REMOVE_ROLE);
        event.setSubject(doer);
        event.setObject("Remove role " + role.replace("ROLE_", "")  + " from " + email);
        event.setPath("/api/admin/user/role");
        securityRepo.save(event);
    }

    public void logDeleteUser(String doer, String email) {
        SecurityEvent event = new SecurityEvent();
        event.setAction(SecurityAction.DELETE_USER);
        event.setSubject(doer);
        event.setObject(email);
        event.setPath("/api/admin/user");
        securityRepo.save(event);
    }

    public void logPasswordChange(String email) {
        SecurityEvent event = new SecurityEvent();
        event.setAction(SecurityAction.CHANGE_PASSWORD);
        event.setSubject(email);
        event.setObject(email);
        event.setPath("/api/auth/changepass");
        securityRepo.save(event);
    }

    public void logDeniedAccess(String email, String path) {
        SecurityEvent event = new SecurityEvent();
        event.setAction(SecurityAction.ACCESS_DENIED);
        event.setSubject(email);
        event.setObject(path);
        event.setPath(path);
        securityRepo.save(event);
    }

    public void logBruteForce(String email) {
        SecurityEvent event = new SecurityEvent();
        event.setAction(SecurityAction.BRUTE_FORCE);
        event.setSubject(email);
        event.setObject("/api/empl/payment");
        event.setPath("/api/empl/payment");
        securityRepo.save(event);
    }

    public void logLockUser(String email) {
        SecurityEvent event = new SecurityEvent();
        event.setAction(SecurityAction.LOCK_USER);
        event.setSubject(email);
        event.setObject("Lock user " + email);
        event.setPath("/api/admin/user/access");
        securityRepo.save(event);
    }

    public void logUnlockUser(String whoLogged, String unlockedUser) {
        SecurityEvent event = new SecurityEvent();
        event.setAction(SecurityAction.UNLOCK_USER);
        event.setSubject(whoLogged);
        event.setObject("Unlock user " + unlockedUser);
        event.setPath("/api/admin/user/access");
        securityRepo.save(event);
    }

}
