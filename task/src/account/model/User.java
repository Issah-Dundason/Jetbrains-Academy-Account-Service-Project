package account.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @NotBlank(message = "Name is mandatory")
    private String name;

    @NotBlank(message = "LastName is mandatory")
    private String lastname;

    @NotBlank
    @Pattern(regexp = "[a-zA-z0-9]+@acme\\.com", message = "Correct format should be provided")
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank(message = "Password is mandatory")
    @Size(min=12, message = "Password length must be 12 chars minimum!")
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    protected List<String> roles = new ArrayList<>();

    @JsonIgnore
    @ColumnDefault("false")
    private boolean blocked = false;
}
