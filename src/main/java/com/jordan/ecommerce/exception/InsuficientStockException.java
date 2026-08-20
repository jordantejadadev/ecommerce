package com.jordan.ecommerce.exception;

public class InsuficientStockException extends RuntimeException {
    public InsuficientStockException(String message) {
        super(message);
    }
}
