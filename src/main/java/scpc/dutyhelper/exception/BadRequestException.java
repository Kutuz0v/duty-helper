package scpc.dutyhelper.exception;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
