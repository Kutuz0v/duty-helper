package scpc.dutyhelper.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import static org.springframework.util.StringUtils.capitalize;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;

@Slf4j
@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(value = {BadRequestException.class})
    public ResponseEntity<Object> badRequestException(BadRequestException e) {
        return baseResponseEntity(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseEntity<Object> badRequestException(MethodArgumentNotValidException e) {
        String message = e.getFieldError().getField() + " " + e.getFieldError().getDefaultMessage();
        return baseResponseEntity(new BadRequestException(message), HttpStatus.BAD_REQUEST);
    }

    /** Not valid (or empty) fields */
    @ExceptionHandler(value = {ConstraintViolationException.class})
    public ResponseEntity<Object> badRequestException(ConstraintViolationException e) {

        String message = e.getConstraintViolations().stream()
                .map(it -> it.getPropertyPath() + " " +it.getMessage()).reduce("", String::concat);
        return baseResponseEntity(new BadRequestException(message), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {UnauthorizedException.class})
    public ResponseEntity<Object> unauthorizedException(UnauthorizedException e) {
        return baseResponseEntity(new UnauthorizedException(e.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = {ForbiddenException.class})
    public ResponseEntity<Object> forbiddenException(ForbiddenException e) {
        return baseResponseEntity(e, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = {NotAcceptableException.class})
    public ResponseEntity<Object> notAcceptableException(NotAcceptableException e) {
        return baseResponseEntity(e, HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(value = {ConflictException.class})
    public ResponseEntity<Object> conflictException(ConflictException e) {
        return baseResponseEntity(e, HttpStatus.CONFLICT);
    }

    /** User not found */
    @ExceptionHandler(value = {NotFoundException.class})
    public ResponseEntity<Object> notFoundException(NotFoundException e) {
        return baseResponseEntity(e, HttpStatus.NOT_FOUND);
    }


    private <T extends Exception> ResponseEntity<Object> baseResponseEntity(T e, HttpStatus status) {
        String message = capitalize(e.getMessage());
        /*Arrays.stream(e.getStackTrace()).filter(it -> it.toString().contains("dutyhelper"))
                .forEach(it -> log.error(it.toString()));*/
        ApiException apiException = new ApiException(
                message,
                status,
                ZonedDateTime.now(ZoneId.of("+2"))
        );
        log.warn(message);
        return new ResponseEntity<>(apiException, status);
    }
}
