package scpc.dutyhelper.auth.payload.request;

import lombok.Data;

@Data
public class ConfirmRequest {
    private String confirmationCode;
}
