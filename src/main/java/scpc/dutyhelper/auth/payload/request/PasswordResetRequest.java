package scpc.dutyhelper.auth.payload.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class PasswordResetRequest {
    private String confirmationCode;
    private String password;
}
