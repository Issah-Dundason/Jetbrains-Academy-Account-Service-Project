package account.model;

import lombok.Data;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

@Data
public class PaymentResponse {
    private String name;
    private String lastname;
    private String period;
    private String salary;

    public PaymentResponse(User user, Payment payment) {
        this.name = user.getName();
        this.lastname = user.getLastname();
        this.salary = String.format("%d dollar(s) %d cent(s)",
                payment.getSalary() / 100, payment.getSalary() % 100);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-yyyy");
        YearMonth yearMonth = YearMonth.parse(payment.getPeriod(), formatter);
        String month = yearMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
        period = String.format("%s-%d", month, yearMonth.getYear());
    }
}
