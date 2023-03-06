package com.example.customerGraphAPI.Customer;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

//TODO write integration test
//TODO concert CUstomer to CUstomer input DTO pattern
//TODO add secrity
//TODO move cahc into service convert into cache config

@Controller
public class CustomerController {

    private final CustomerService customerService;


    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @QueryMapping
    public List<Customer> findAllCustomers() {
        return customerService.findAllCustomer();
    }

    @QueryMapping
    public Optional<Customer> findCustomerById(@Argument String uid) {
        return customerService.findCustomerById(uid);
    }

    @MutationMapping
    public Customer addNewCustomer(@Argument CustomerInput customerInput) {
        return customerService.addNewCustomer(customerInput);
    }

    @MutationMapping
    public UUID updateCustomerNames(@Argument String id, @Argument String firstname, @Argument String lastname) {

        return customerService.updateCustomerName(id, firstname, lastname);
    }

    @MutationMapping
    public UUID deleteCustomerById(@Argument String id) {
        return customerService.deleteCustomerById(id);
    }
}
