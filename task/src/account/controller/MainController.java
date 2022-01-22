package account.controller;

import account.model.AccessRequest;
import account.model.RoleRequest;
import account.model.SecurityEvent;
import account.model.User;
import account.repo.UserRepo;
import account.service.AdminService;
import account.service.AuthService;
import account.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.Map;
import java.util.Optional;

@RestController
public class MainController {
    public static final String NEW_PASSWORD = "new_password";
    private final AuthService authService;
    private final AdminService adminService;
    private final SecurityService securityService;

    @Autowired
    public MainController(AuthService authService,
                          AdminService adminService,
                          SecurityService securityService) {
        this.authService = authService;
        this.adminService = adminService;
        this.securityService = securityService;
    }

    @PostMapping("/api/auth/signup")
    public User signup(@Valid @RequestBody User user) {
        return authService.signup(user);
    }

    @PostMapping("/api/auth/changepass")
    public Map<String, String> changePassword(@RequestBody Map<String, String> reqBody,
                                              @AuthenticationPrincipal UserDetails details) {
        String newPassword = reqBody.getOrDefault(NEW_PASSWORD, "");
        String oldPassword = details.getPassword();
        String email = details.getUsername();
        authService.changePassword(email, newPassword, oldPassword);
        return Map.of("email", details.getUsername().toLowerCase(), "status",
                "The password has been updated successfully");
    }

    @GetMapping("/api/admin/user")
    public Iterable<User> getUsers() {
        return adminService.getUser();
    }

    @DeleteMapping(value = {"/api/admin/user", "/api/admin/user/{userEmail}"})
    public Map<String, String> deleteUser(@PathVariable(required = false) Optional<String> userEmail) {
        String email = userEmail.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
        adminService.deleteUser(email);
        return Map.of("user", email, "status", "Deleted successfully!");
    }

    @PutMapping("api/admin/user/role")
    public User updateRole(@Valid @RequestBody RoleRequest request) {
        return adminService.updateRole(request);
    }

    @PutMapping("api/admin/user/access")
    public Map<String, String> updateUserAccess(@Valid @RequestBody AccessRequest request) {
        adminService.updateUserAccess(request);
        return Map.of("status", "User " + request.getUser().toLowerCase() + " "
                + request.getOperation().toLowerCase() + "ed!");
    }

    @GetMapping("api/security/events")
    public Iterable<SecurityEvent> getSecurityEvents() {
        return securityService.getAllEvents();
    }
}
