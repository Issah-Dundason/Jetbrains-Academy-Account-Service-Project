package account.security;

import account.service.SecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {
    private final SecurityService securityService;

    @Autowired
    public RestAccessDeniedHandler(SecurityService securityService) {
        this.securityService = securityService;
    }

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.sendError(HttpStatus.FORBIDDEN.value(), "Access Denied!");
        String username = request.getUserPrincipal().getName();
        String path = request.getRequestURI();
        securityService.logDeniedAccess(username, path);
    }
}
