package com.banking.service;

import com.banking.dto.InterestCalculationResponse;

import java.util.List;

public interface InterestService {

    InterestCalculationResponse applyInterest(Long accountId);

    InterestCalculationResponse calculateInterest(Long accountId);

    List<InterestCalculationResponse> applyMonthlyInterestToAllAccounts();

    List<InterestCalculationResponse> triggerInterestApplication();
}
