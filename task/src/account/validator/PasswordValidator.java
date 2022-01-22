package account.validator;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Component
@Validated
public class PasswordValidator {
    private final List<String> breachedPasswords =
            List.of("PasswordForJanuary", "PasswordForFebruary", "PasswordForMarch", "PasswordForApril",
                    "PasswordForMay", "PasswordForJune", "PasswordForJuly", "PasswordForAugust",
                    "PasswordForSeptember", "PasswordForOctober", "PasswordForNovember", "PasswordForDecember");

    public void verify(@NotBlank
                       @Size(min = 12, message = "Password length must be 12 chars minimum!")
                               String password) throws ResponseStatusException {
        if (breachedPasswords.contains(password)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The password is in the hacker's database!");
        }
    }
}
