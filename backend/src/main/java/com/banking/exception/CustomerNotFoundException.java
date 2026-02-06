package com.banking.exception;

public class CustomerNotFoundException extends RuntimeException {

    public CustomerNotFoundException(String message) {
        super(message);
    }

    public static CustomerNotFoundException withId(Long customerId) {
        return new CustomerNotFoundException("Customer not found with ID: " + customerId);
    }
}
