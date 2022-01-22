package account.model;


import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Data
public class SecurityEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private SecurityAction action;

    private String subject;

    private String object;

    private String path;

    @PrePersist
    public void addDate() {
        this.date = LocalDate.now();
    }
}
