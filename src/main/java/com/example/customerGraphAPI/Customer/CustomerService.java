package com.example.customerGraphAPI.Customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;


//TODO add caching

@Service

public class CustomerService {

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> findAllCustomer() {
        return customerRepository.findAll();
    }


    public Optional<Customer> findCustomerById(final String id) {
        UUID uuid = convertStringToUUID(id);

        return Optional.ofNullable(customerRepository.findById(uuid).orElseThrow(() -> new CustomException(CustomerErrorMessage.ID_NOT_FOUND.getMessage() + id)));
    }

    public Customer addNewCustomer(final CustomerInput customerInput) {
        LocalDateTime dob;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.UK);
            dob = LocalDate.parse(customerInput.getDob(), formatter).atStartOfDay();
        } catch (Exception e) {
            throw new CustomException(CustomerErrorMessage.DATE_ERROR.getMessage());
        }
        Customer customerToSave = new Customer(customerInput.getFirstname(), customerInput.getLastname(), dob);
        customerRepository.save(customerToSave);
        return customerToSave;
    }

    public UUID updateCustomerName(final String id, final String firstname, final String lastname) {
        UUID uuid = convertStringToUUID(id);

        Optional<Customer> customer = customerRepository.findById(uuid);

        if (customer.isEmpty()) {
            throw new CustomException(CustomerErrorMessage.ID_NOT_FOUND.getMessage() + id);
        }

        customer.ifPresent(customer1 -> {
            customer1.setFirstname(firstname);
            customer1.setLastname(lastname);
            customerRepository.save(customer1);
        });


        return uuid;

    }

    public UUID deleteCustomerById(final String id) {
        Optional<Customer> customerById = this.findCustomerById(id);
        customerById.ifPresent(customer -> customerRepository.deleteById(convertStringToUUID(id)));
        return convertStringToUUID(id);
    }

    public UUID convertStringToUUID(final String id) {
        try {
            return UUID.fromString(id);
        } catch (Exception e) {
            throw new CustomException(CustomerErrorMessage.UUID_ERROR.getMessage() + id);
        }
    }

}
