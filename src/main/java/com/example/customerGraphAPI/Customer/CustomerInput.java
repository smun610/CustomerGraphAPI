package com.example.customerGraphAPI.Customer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerInput {
    private String firstname;
    private String lastname;
    private String dob;
}
