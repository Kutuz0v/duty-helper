package scpc.dutyhelper.auth.payload.request;

import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest {

    @NotBlank
    private String firstName, lastName;

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

//    private Set<String> role;

    @NotBlank
    @Size(min = 8, max = 128)
    private String password;

    private String redirectUrl;
}
