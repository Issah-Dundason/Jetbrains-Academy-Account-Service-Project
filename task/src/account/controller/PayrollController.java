package account.controller;

import account.model.Payment;
import account.model.PaymentResponse;
import account.service.PayrollService;
import account.validator.PayRollValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@Validated
public class PayrollController {
    private final PayrollService service;
    private final PayRollValidator validator;

    @Autowired
    public PayrollController(PayrollService service, PayRollValidator validator) {
        this.service = service;
        this.validator = validator;
    }

    @RequestMapping(value = "api/acct/payments", method = {RequestMethod.POST})
    public Map<String, String> acceptPayroll(@RequestBody List<@Valid Payment> payments) {
        validator.check(payments);
        service.save(payments);
        return Map.of("status", "Added successfully!");
    }

    @GetMapping("api/empl/payment")
    public Object getDetail(@Valid @RequestParam Optional<@Pattern(regexp = "^(0?[1-9]|1[0-2])-([1-9]\\d{3})$") String> period,
                            @AuthenticationPrincipal UserDetails details) {
        String value = period.orElse("");
        if(value.isBlank()) {
            return service.findAllPayrolls(details.getUsername());
        }

        Optional<PaymentResponse> payroll = service.findPayrollByPeriod(details.getUsername(), value);
        if(payroll.isEmpty()) {
            return "{}";
        }
        return payroll.get();
    }

    @RequestMapping(value = "api/acct/payments", method = {RequestMethod.PUT})
    public Map<String, String> updateEmployeePayroll(@Valid @RequestBody Payment payment) {
        service.updatePayroll(payment);
        return Map.of("status", "Updated successfully!");
    }

}
