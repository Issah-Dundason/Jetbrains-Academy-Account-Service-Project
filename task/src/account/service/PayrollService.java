package account.service;

import account.model.Payment;
import account.model.PaymentResponse;
import account.model.User;
import account.repo.PayrollRepo;
import account.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PayrollService {
    private final PayrollRepo repo;
    private final UserRepo userRepo;

    @Autowired
    public PayrollService(PayrollRepo repo, UserRepo userRepo) {
        this.repo = repo;
        this.userRepo = userRepo;
    }

    public void save(List<Payment> data) {
        repo.saveAll(data);
    }

    public Optional<PaymentResponse> findPayrollByPeriod(String employeeEmail, String period) {
        Optional<Payment> payroll = repo.findByEmployeeIgnoreCaseAndPeriod(employeeEmail, period);
        if(payroll.isEmpty()) {
            return Optional.empty();
        }
        User user = userRepo.findUserByEmailIgnoreCase(employeeEmail).get();
        return Optional.of(new PaymentResponse(user, payroll.get()));
    }

    public List<PaymentResponse> findAllPayrolls(String email) {
        List<Payment> payments = repo.findAllByEmployeeIgnoreCase(email).orElse(List.of());
        final User user = userRepo.findUserByEmailIgnoreCase(email).get();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-yyyy");
        if(!payments.isEmpty()) {
            payments.sort((a, b) -> {
                YearMonth ymA = YearMonth.parse(a.getPeriod(), formatter);
                YearMonth ymB = YearMonth.parse(b.getPeriod(), formatter);
                return ymB.compareTo(ymA);
            });
        }
        List<PaymentResponse> dtos = payments.stream().map(data -> new PaymentResponse(user, data)).collect(Collectors.toList());
        return dtos;
    }

    public void updatePayroll(Payment payment) {
        Optional<Payment> dbData = repo.findByEmployeeIgnoreCaseAndPeriod(payment.getEmployee(),
                payment.getPeriod());
        dbData.ifPresentOrElse(data -> {
            data.setSalary(payment.getSalary());
            Payment saved = repo.save(data);
        }, () -> {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        });
    }
}
