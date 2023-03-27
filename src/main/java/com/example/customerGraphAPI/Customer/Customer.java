package com.example.customerGraphAPI.Customer;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uid;
    private String firstname;
    private String lastname;
    private LocalDateTime dob;

    public Customer(String firstname, String lastname, LocalDateTime dob) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.dob = dob;
    }
}
