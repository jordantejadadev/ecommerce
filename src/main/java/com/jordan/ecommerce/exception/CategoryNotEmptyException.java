package com.jordan.ecommerce.exception;

public class CategoryNotEmptyException extends RuntimeException {
    public CategoryNotEmptyException(String message) {
        super(message);
    }
}
