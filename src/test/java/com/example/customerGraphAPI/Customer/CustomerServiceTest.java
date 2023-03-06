package com.example.customerGraphAPI.Customer;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @InjectMocks
    private CustomerService customerServiceUnderTest;
    @Mock
    private CustomerRepository customerRepositoryMock;

    @Captor
    private ArgumentCaptor<Customer> customerArgumentCaptor;

    private LocalDateTime dob;

    @BeforeEach
    void setUp() {

        dob = getTestDob();
    }

    @Test
    void itShouldFindAllCustomer() {
        // Given
        CustomerRepository mockCustomerRepository = Mockito.mock(CustomerRepository.class);
        // When
        when(mockCustomerRepository.findAll()).thenReturn(Lists.newArrayList(new Customer("test", "test", LocalDateTime.now())));
        // Then
        assertEquals(mockCustomerRepository.findAll().size(), 1);
    }

    @Test
    void itShouldThrowADateErrorAddNewCustomer() {
        //Given
        CustomerInput customerInput = new CustomerInput("test", "test", "1991-03-20");
        //When
        CustomException customException = assertThrows(CustomException.class, () -> customerServiceUnderTest.addNewCustomer(customerInput));

        //Then
        assertEquals(CustomerErrorMessage.DATE_ERROR.getMessage(), customException.getMessage());
        verify(customerRepositoryMock, never()).save(any());

    }

    @Test
    void itShouldAddNewCustomerWithCorrectDate() {
        //Given

        CustomerInput customerInput = new CustomerInput("test", "test", "20-03-1991");

        customerServiceUnderTest.addNewCustomer(customerInput);
        //When

        verify(customerRepositoryMock).save(customerArgumentCaptor.capture());
        Customer capturedCustomerValues = customerArgumentCaptor.getValue();

        assertEquals(capturedCustomerValues.getFirstname(), customerInput.getFirstname());
        assertEquals(capturedCustomerValues.getLastname(), customerInput.getLastname());
        assertNotNull(capturedCustomerValues.getDob());
    }

    @Test
    void itShouldThrowUUIDExceptionForUpdateCustomer() {
        //Given
        String uidTest = "12345";
        String firstNameTest = "Bob";
        String lastNameTest = "Test";
        //When
        CustomException customException = assertThrows(CustomException.class, () -> customerServiceUnderTest.updateCustomerName(uidTest, firstNameTest, lastNameTest));
        //Then
        assertEquals(CustomerErrorMessage.UUID_ERROR.getMessage() + uidTest, customException.getMessage());
        verify(customerRepositoryMock, never()).save(any());
    }


    @Test
    void itShouldThrowIdNotFoundExceptionForUpdateCustomer() {
        //Given
        UUID uidTest = UUID.randomUUID();
        String firstNameTest = "Bob";
        String lastNameTest = "Test";
        //When
        when(customerRepositoryMock.findById(uidTest)).thenReturn(Optional.empty());
        CustomException customException = assertThrows(CustomException.class, () -> customerServiceUnderTest.updateCustomerName(uidTest.toString(), firstNameTest, lastNameTest));
        //Then
        assertEquals(CustomerErrorMessage.ID_NOT_FOUND.getMessage() + uidTest, customException.getMessage());
        verify(customerRepositoryMock, never()).save(any());
    }

    @Test
    void itShouldUpdateCustomerName() {
        //Given
        UUID uidTest = UUID.randomUUID();
        String firstNameTest = "Bob";
        String lastNameTest = "Test";
        //When
        when(customerRepositoryMock.findById(uidTest)).thenReturn(Optional.of(new Customer("jim", "Mathew", dob)));
        customerServiceUnderTest.updateCustomerName(uidTest.toString(), firstNameTest, lastNameTest);
        //Then
        verify(customerRepositoryMock).save(customerArgumentCaptor.capture());
        Customer capturedCustomerValues = customerArgumentCaptor.getValue();
        assertEquals(capturedCustomerValues.getLastname(), lastNameTest);
        assertEquals(capturedCustomerValues.getFirstname(), firstNameTest);
    }

    @Test
    void itShouldNotDeleteIfCustomerIsNotFound() {
        //Given
        UUID uidTest = UUID.randomUUID();
        //When
        when(customerRepositoryMock.findById(any())).thenReturn(Optional.empty());
        //Then
        CustomException customException = assertThrows(CustomException.class, () -> customerServiceUnderTest.deleteCustomerById(uidTest.toString()));
        assertEquals(CustomerErrorMessage.ID_NOT_FOUND.getMessage() + uidTest, customException.getMessage());
        verify(customerRepositoryMock, never()).deleteById(any());
    }

    @Test
    void itShouldDeleteCustomerByID (){
        //Given
        UUID uidTest = UUID.randomUUID();
        //When
        when(customerRepositoryMock.findById(uidTest)).thenReturn(Optional.of(new Customer("jim", "Mathew", dob)));
        UUID idReturned = customerServiceUnderTest.deleteCustomerById(uidTest.toString());
        //Then
        verify(customerRepositoryMock).deleteById(uidTest);
        assertEquals(uidTest,idReturned);
    }
    LocalDateTime getTestDob() {
        LocalDateTime dob;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.UK);
        dob = LocalDate.parse("20-03-1991", formatter).atStartOfDay();
        return dob;
    }
}