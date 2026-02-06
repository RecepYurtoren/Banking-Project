package com.banking.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class CreateSavingsAccountRequest {

    @NotBlank(message = "Account holder name is required")
    @Size(max = 100, message = "Account holder name must not exceed 100 characters")
    private String accountHolderName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @PositiveOrZero(message = "Initial balance must be zero or positive")
    private BigDecimal initialBalance = BigDecimal.ZERO;

    @PositiveOrZero(message = "Minimum balance must be zero or positive")
    private BigDecimal minimumBalance;

    @PositiveOrZero(message = "Interest rate must be zero or positive")
    @Max(value = 100, message = "Interest rate cannot exceed 100%")
    private BigDecimal interestRate;
}
