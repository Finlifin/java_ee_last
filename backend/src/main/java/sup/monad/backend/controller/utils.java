package sup.monad.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import sup.monad.backend.exception.CustomException;

public class utils {
    public static ResponseEntity<Object> wrap(SupplierWithException<Object> supplier,
            HttpStatus successStatus) {
        try {
            Object result = supplier.get();
            return ResponseEntity.ok(result);
        } catch (CustomException e) {
            return new ResponseEntity<Object>(e.getMessage(), e.getHttpStatus());
        }
    }

    @FunctionalInterface
    public interface SupplierWithException<T> {
        T get() throws CustomException;
    }
}
