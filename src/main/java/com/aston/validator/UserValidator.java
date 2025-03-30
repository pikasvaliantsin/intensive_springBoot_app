package com.aston.validator;

public class UserValidator {
    public static void validateId(Long id) {
        if (id == null)
            throw new IllegalArgumentException("User id cannot be null");
    }

    public static void validateName(String name) {
        if (name == null)
            throw new IllegalArgumentException("User name cannot be null");
        if (name.isBlank())
            throw new IllegalArgumentException("User name cannot be empty");
    }

    public static void validateAge(Integer age) {
        if (age < 0)
            throw new IllegalArgumentException("User age cannot be negative");
    }
}