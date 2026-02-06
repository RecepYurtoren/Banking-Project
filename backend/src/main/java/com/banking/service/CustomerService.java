package com.banking.service;

import com.banking.dto.CustomerRequest;
import com.banking.dto.CustomerResponse;

import java.util.List;

public interface CustomerService {

    CustomerResponse createCustomer(CustomerRequest request);
    CustomerResponse getCustomerById(Long id);
    List<CustomerResponse> getAllCustomers();
    List<CustomerResponse> searchCustomers(String query);
    CustomerResponse updateCustomer(Long id, CustomerRequest request);
    void deactivateCustomer(Long id);
}
