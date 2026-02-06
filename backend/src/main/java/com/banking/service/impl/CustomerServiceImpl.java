package com.banking.service.impl;

import com.banking.dto.CustomerRequest;
import com.banking.dto.CustomerResponse;
import com.banking.exception.CustomerNotFoundException;
import com.banking.model.Customer;
import com.banking.repository.CustomerRepository;
import com.banking.service.CustomerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public CustomerResponse createCustomer(CustomerRequest request) {
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + request.getEmail());
        }

        Customer customer = new Customer(request.getFirstName(), request.getLastName(), request.getEmail());
        customer.setPhone(request.getPhone());

        Customer savedCustomer = customerRepository.save(customer);
        return mapToResponse(savedCustomer);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerResponse getCustomerById(Long id) {
        Customer customer = findCustomerById(id);
        return mapToResponse(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponse> searchCustomers(String query) {
        return customerRepository
            .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(query, query)
            .stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Override
    public CustomerResponse updateCustomer(Long id, CustomerRequest request) {
        Customer customer = findCustomerById(id);

        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setPhone(request.getPhone());

        if (!customer.getEmail().equals(request.getEmail())) {
            if (customerRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("Email already exists: " + request.getEmail());
            }
            customer.setEmail(request.getEmail());
        }

        Customer savedCustomer = customerRepository.save(customer);
        return mapToResponse(savedCustomer);
    }

    @Override
    public void deactivateCustomer(Long id) {
        Customer customer = findCustomerById(id);
        customer.setActive(false);
        customerRepository.save(customer);
    }

    private Customer findCustomerById(Long id) {
        return customerRepository.findById(id)
            .orElseThrow(() -> CustomerNotFoundException.withId(id));
    }

    private CustomerResponse mapToResponse(Customer customer) {
        CustomerResponse response = new CustomerResponse();
        response.setId(customer.getId());
        response.setFirstName(customer.getFirstName());
        response.setLastName(customer.getLastName());
        response.setFullName(customer.getFullName());
        response.setEmail(customer.getEmail());
        response.setPhone(customer.getPhone());
        response.setRole(customer.getRole().name());
        response.setActive(customer.isActive());
        response.setCreatedAt(customer.getCreatedAt());
        return response;
    }
}
