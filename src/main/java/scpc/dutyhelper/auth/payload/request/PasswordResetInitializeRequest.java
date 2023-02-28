package scpc.dutyhelper.auth.payload.request;

import lombok.Data;

@Data
public class PasswordResetInitializeRequest {
    private String email;
    private String redirectUrl;
}
