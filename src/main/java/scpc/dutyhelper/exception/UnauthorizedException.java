package scpc.dutyhelper.exception;

//@Construct
public class UnauthorizedException extends RuntimeException{
    public UnauthorizedException(String message) {
        super(message);
    }
}
