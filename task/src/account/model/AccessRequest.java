package account.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class AccessRequest {
    @NotBlank
    private String user;

    @Pattern(regexp = "LOCK|UNLOCK")
    @NotBlank
    private String operation;
}
