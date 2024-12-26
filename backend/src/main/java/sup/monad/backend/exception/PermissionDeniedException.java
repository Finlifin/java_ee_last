package sup.monad.backend.exception;

import org.springframework.http.HttpStatus;

public class PermissionDeniedException extends CustomException {
    public PermissionDeniedException(String message, HttpStatus status) {
        super(message, HttpStatus.FORBIDDEN);
    }
}
