package account.service;

import account.model.AccessRequest;
import account.model.RoleRequest;
import account.model.User;
import account.repo.UserRepo;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Service
public class AdminService {
    private static final String OP_REMOVE = "REMOVE";
    private static final String OP_GRANT = "GRANT";
    private final UserRepo userRepo;
    private final SecurityService securityService;

    public AdminService(UserRepo userRepo, SecurityService securityService) {
        this.userRepo = userRepo;
        this.securityService = securityService;
    }

    public User updateRole(RoleRequest request) {
        User user = checkIfUserExists(request.getUser());
        if (request.getOperation().equals(OP_REMOVE)) {
            return deleteRole(user, "ROLE_" + request.getRole());
        } else if (request.getOperation().equals(OP_GRANT)) {
            return grantRole(user, "ROLE_" + request.getRole());
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "unrecognised operation");
        }
    }

    private User checkIfUserExists(String email) {
        return userRepo.findUserByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!"));
    }

    private User grantRole(User user, String grantRole) {
        if (roleIsNotAvailable(grantRole)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found!");
        }
        if (user.getRoles().contains("ROLE_ADMINISTRATOR") &&
                (Set.of("ROLE_ACCOUNTANT", "ROLE_USER", "ROLE_AUDITOR").contains(grantRole))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The user cannot combine administrative and business roles!");
        }
        if (grantRole.equals("ROLE_ADMINISTRATOR") &&
                (user.getRoles().contains("ROLE_ACCOUNTANT") || user.getRoles().contains("ROLE_USER")
                        || user.getRoles().contains("ROLE_AUDITOR"))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The user cannot combine administrative and business roles!");
        }
        user.getRoles().add(grantRole);
        Collections.sort(user.getRoles(), Comparator.naturalOrder());
        userRepo.save(user);
        UserDetails doer = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        securityService.logGrantRole(doer.getUsername(), user.getEmail(), grantRole);
        return user;
    }

    private boolean roleIsNotAvailable(String role) {
        return !List.of("ROLE_ADMINISTRATOR", "ROLE_ACCOUNTANT", "ROLE_USER", "ROLE_AUDITOR").contains(role);
    }

    private User deleteRole(User user, String delRole) {
        if (roleIsNotAvailable(delRole)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found!");
        }
        if (!user.getRoles().contains(delRole)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user does not have a role!");
        }
        if (delRole.equals("ROLE_ADMINISTRATOR")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR role!");
        }
        if (user.getRoles().size() < 2) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user must have at least one role!");
        }
        user.getRoles().remove(delRole);
        User dbUser = userRepo.save(user);
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        securityService.logRemoveRole(principal.getUsername(), user.getEmail(), delRole);
        return dbUser;
    }

    public void deleteUser(String email) {
        User user = checkIfUserExists(email);
        boolean hasAdminRole = userHasAdminRole(user);
        if (hasAdminRole)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR role!");
        userRepo.delete(user);
        UserDetails principal = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        securityService.logDeleteUser(principal.getUsername(), email);
    }

    private boolean userHasAdminRole(User user) {
        return user.getRoles().contains("ROLE_ADMINISTRATOR");
    }

    public Iterable<User> getUser() {
        return userRepo.findAll();
    }

    public void updateUserAccess(AccessRequest request) {
        User user = checkIfUserExists(request.getUser());
        System.out.println(request.getOperation());
        if(request.getOperation().equals("LOCK")) {
            lockUser(user);
            return;
        }
        unlockUser(user);
    }

    private void lockUser(User user) {
        if(user.getRoles().contains("ROLE_ADMINISTRATOR"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't lock the ADMINISTRATOR!");
        user.setBlocked(true);
        userRepo.save(user);
        securityService.logLockUser(user.getEmail());
    }

    private void unlockUser(User user) {
        user.setBlocked(false);
        userRepo.save(user);
        securityService.logUnlockUser(getUsername(), user.getEmail());
    }

    private String getUsername() {
       Authentication auth = SecurityContextHolder.getContext().getAuthentication();
       return auth.getName();
    }
}
