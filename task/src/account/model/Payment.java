package account.model;

import lombok.Data;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

@Entity
@Data
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String employee;

    @Pattern(regexp = "^(0?[1-9]|1[0-2])-([1-9]\\d{3})$")
    private String period;

    @Min(value = 0)
    private long salary;
}
