package org.example.HW19.service;

import org.example.HW19.entity.Customer;
import org.example.HW19.exceptions.UserNotFoundException;
import org.example.HW19.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer save(Customer customer) {
        return customerRepository.save(customer);
    }

    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    public Customer findById(long id) {
        return customerRepository.findById(id).orElseThrow(() -> new UserNotFoundException("no customer found with this ID"));
    }
}
