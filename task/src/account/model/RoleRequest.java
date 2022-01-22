package account.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class RoleRequest {
    @NotBlank
    private String user;

    @NotBlank
    private String role;

    @NotBlank
    private String operation;
}
