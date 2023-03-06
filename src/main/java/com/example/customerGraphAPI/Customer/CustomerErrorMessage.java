package com.example.customerGraphAPI.Customer;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CustomerErrorMessage {
    DATE_ERROR("Date format must be dd-MM-yyy example 20-03-1991"),
    UUID_ERROR("Not a valid UUid "),
    ID_NOT_FOUND("Id does not exist "),

    DELETE_ERROR ("Id does not exist cannotDelete ");
    private final String message;
 
}
