package sup.monad.backend.exception;

import org.springframework.http.HttpStatus;

public class SignUpException extends RuntimeException {
    private final HttpStatus httpStatus;

    public SignUpException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
