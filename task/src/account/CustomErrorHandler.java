package account;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.ServletWebRequest;

import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.io.IOException;

@ControllerAdvice
public class CustomErrorHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public void handleConstraintViolationException(ConstraintViolationException exception,
                                                   ServletWebRequest webRequest) throws IOException {
        webRequest
                .getResponse()
                .sendError(HttpStatus.BAD_REQUEST.value(),
                        exception.getMessage().replaceAll("\\w+\\.\\w+:\\s", ""));
    }

}
