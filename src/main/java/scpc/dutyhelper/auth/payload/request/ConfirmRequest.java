package scpc.dutyhelper.auth.payload.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class ConfirmRequest {
    private String confirmationCode;
}
