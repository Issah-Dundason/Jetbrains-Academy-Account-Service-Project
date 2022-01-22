package account.validator;

import account.model.Payment;
import account.model.User;
import account.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Component
public class PayRollValidator {
    private final UserRepo userRepo;

    @Autowired
    public PayRollValidator(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    public void check(List<Payment> data) throws ResponseStatusException{
        checkDuplicate(data);
        checkEmployees(data);
    }

    private void checkEmployees(List<Payment> data) {
        data.forEach(model -> {
            Optional<User> user = userRepo
                    .findUserByEmailIgnoreCase(model.getEmployee());
            user.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST));
        });
    }

    private void checkDuplicate(List<Payment> data) {
        int count = getCount(data);
        if(count < data.size())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    private int getCount(List<Payment> data) {
        return (int) data
                .stream()
                .map(model -> model.getEmployee() + " " + model.getPeriod())
                .distinct()
                .count();
    }
}
