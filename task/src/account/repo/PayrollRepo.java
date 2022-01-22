package account.repo;

import account.model.Payment;
import org.springframework.data.repository.CrudRepository;
import java.util.List;
import java.util.Optional;

public interface PayrollRepo extends CrudRepository<Payment, Integer> {
    Optional<Payment> findByEmployeeIgnoreCaseAndPeriod(String empl, String period);
    Optional<List<Payment>> findAllByEmployeeIgnoreCase(String empl);
}
