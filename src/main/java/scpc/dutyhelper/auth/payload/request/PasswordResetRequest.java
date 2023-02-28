package scpc.dutyhelper.auth.payload.request;

import lombok.Data;

@Data
public class PasswordResetRequest {
    private String confirmationCode;
    private String password;
}
