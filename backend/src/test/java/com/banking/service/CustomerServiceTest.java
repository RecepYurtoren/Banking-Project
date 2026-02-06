package com.banking.service;

import com.banking.dto.CustomerRequest;
import com.banking.dto.CustomerResponse;
import com.banking.exception.CustomerNotFoundException;
import com.banking.model.Customer;
import com.banking.model.Role;
import com.banking.repository.CustomerRepository;
import com.banking.service.impl.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Customer Service Unit Tests")
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    private CustomerService customerService;

    @BeforeEach
    void setUp() {
        customerService = new CustomerServiceImpl(customerRepository);
    }

    // Yeni bir müşteri oluşturma işlemi
    @Test
    void testCreateCustomerSuccess() {
        CustomerRequest request = new CustomerRequest();
        request.setFirstName("Recep");
        request.setLastName("Yurtören");
        request.setEmail("recep@email.com");
        request.setPhone("05413671894");

        when(customerRepository.existsByEmail("recep@email.com")).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenAnswer(inv -> {
            Customer c = inv.getArgument(0);
            c.setId(1L);
            return c;
        });

        CustomerResponse response = customerService.createCustomer(request);

        assertNotNull(response);
        assertEquals("Recep", response.getFirstName());
        assertEquals("recep@email.com", response.getEmail());
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    // Zaten kaydı bulunan mail adresiyle kayıt denemesi 
    @Test
    void testCreateCustomerDuplicateEmail() {
        
        CustomerRequest request = new CustomerRequest();
        request.setEmail("exists@email.com");

        when(customerRepository.existsByEmail("exists@email.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            customerService.createCustomer(request);
        });

        verify(customerRepository, never()).save(any());
    }

    // Tüm müşterilerin get edilmesi 
    void testGetAllCustomers() {
        Customer c1 = new Customer("Recep", "Yurtören", "recep@email.com");
        Customer c2 = new Customer("Ayse", "Demir", "ayse@email.com");

        when(customerRepository.findAll()).thenReturn(Arrays.asList(c1, c2));

        List<CustomerResponse> result = customerService.getAllCustomers();

        assertEquals(2, result.size());
        assertEquals("Recep", result.get(0).getFirstName());
        assertEquals("Ayse", result.get(1).getFirstName());
    }

    // Bir müşterinin sistemde pasif hale getirilmesi
    @Test
    void testDeactivateCustomer() {
        Customer customer = new Customer("Recep", "Yurtören", "recep@email.com");
        customer.setId(1L);
        customer.setActive(true);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        customerService.deactivateCustomer(1L);

        assertFalse(customer.isActive());
        verify(customerRepository).save(customer);
    }
}