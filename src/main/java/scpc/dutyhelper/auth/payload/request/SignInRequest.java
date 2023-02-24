package scpc.dutyhelper.auth.payload.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Data
public class SignInRequest {

    @NotBlank
    private String email;

    @NotBlank
    private String password;
}
