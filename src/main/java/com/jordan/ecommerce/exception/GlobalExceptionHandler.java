package com.jordan.ecommerce.exception;

import com.jordan.ecommerce.dto.error.ErrorResponse;
import org.hibernate.dialect.lock.OptimisticEntityLockException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidOrderStatusException.class)
    public ResponseEntity<ErrorResponse> handleInvalidOrderStatus(
            InvalidOrderStatusException ex
    ) {
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse(400, ex.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(InsuficientStockException.class)
    public ResponseEntity<ErrorResponse> handleInsuficientStockException(
            InsuficientStockException ex
    ) {
        return ResponseEntity
                .badRequest()
                .body(new ErrorResponse(
                        400,
                        ex.getMessage(),
                        LocalDateTime.now()
                ));
    }

    @ExceptionHandler(OptimisticEntityLockException.class)
    public ResponseEntity<ErrorResponse> handleObjectOptimisticLockingFailure(
            OptimisticEntityLockException ex
    ) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(
                        409,
                        "El producto fue modificado por otra operación. Intenta nuevamente",
                        LocalDateTime.now()
                ));
    }
}
