package com.example.customerGraphAPI.Customer;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureGraphQlTester;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.graphql.test.tester.HttpGraphQlTester;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.BDDMockito.*;

import static org.junit.jupiter.api.Assertions.*;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureHttpGraphQlTester
class CustomerControllerIntTest {
    @Autowired
    HttpGraphQlTester graphQlTester;

    @InjectMocks
    CustomerService customerService;
    @MockBean
    CustomerRepository customerRepository;

    @Captor
    private ArgumentCaptor<Customer> customerArgumentCaptor;

    @Test
    void itShouldFindAllCustomersAsUser() {
        // Given

        given(customerRepository.findAll()).willReturn(getCustomers());

        String document = """
                query   {
                    findAllCustomers{
                        firstname,
                        lastname
                    }
                }
                """;
        // When
        HttpGraphQlTester httpGraphQlTester = asUser(graphQlTester);
        httpGraphQlTester
                .document(document)
                .execute()
                .path("findAllCustomers")
                .entityList(Customer.class)
                .hasSize(2);
    }


    @Test
    void itShouldFindCustomerByIdWhenUsedByUser() {
        // Given
        UUID uuid = UUID.randomUUID();
        Customer customer = new Customer("Bob", "Joe", LocalDateTime.of(1993, 03, 22, 00, 00));
        given(customerRepository.findById(any())).willReturn(Optional.of(customer));
        String document = """
                query findCustomerById($uid: String)  {
                    findCustomerById (uid: $uid ){
                       firstname,
                       lastname
                    }
                }
                """;
        // When
        HttpGraphQlTester httpGraphQlTesterUser = asUser(graphQlTester);
        httpGraphQlTesterUser.document(document)
                .variable("uid", uuid)
                .execute()
                .path("findCustomerById")
                .entity(Customer.class)
                .satisfies(customer1 -> {
                    assertEquals("Bob", customer1.getFirstname());
                    assertEquals("Joe", customer1.getLastname());
                });


        then(customerRepository).should().findById(uuid);

    }

    @Test
    void itShouldFindCustomerByIdWhenUsedByAdmin() {
        // Given
        UUID uuid = UUID.randomUUID();
        Customer customer = new Customer("Bob", "Joe", LocalDateTime.of(1993, 03, 22, 00, 00));
        given(customerRepository.findById(any())).willReturn(Optional.of(customer));
        String document = """
                query findCustomerById($uid: String)  {
                    findCustomerById (uid: $uid ){
                       firstname,
                       lastname
                    }
                }
                """;
        // When
        HttpGraphQlTester httpGraphQlTesterUser = asUser(graphQlTester);
        httpGraphQlTesterUser.document(document)
                .variable("uid", uuid)
                .execute()
                .path("findCustomerById")
                .entity(Customer.class)
                .satisfies(customer1 -> {
                    assertEquals("Bob", customer1.getFirstname());
                    assertEquals("Joe", customer1.getLastname());
                });


        then(customerRepository).should().findById(uuid);

    }

    @Test
    void itShouldAddNewCustomerAsAdmin() {
        // Given
        Customer customer = getCustomer();
        given(customerRepository.save(any())).willReturn(customer);
        String document = """
                mutation{
                  addNewCustomer(customerInput:{
                    firstname: "bob",
                    lastname: "Test"
                    dob: "20-02-1993"
                  })
                }
                """;
        // When
        HttpGraphQlTester httpGraphQlTester = asAdmin(graphQlTester);
        httpGraphQlTester
                .document(document)
                .execute()
                .path("addNewCustomer")
                .entity(String.class)
                .satisfies(id -> assertEquals(id, customer.getUid().toString()));
        // Then
        then(customerRepository).should().save(customerArgumentCaptor.capture());
        Customer customerArgumentCaptorValue = customerArgumentCaptor.getValue();
        assertThat(customerArgumentCaptorValue.getFirstname()).isEqualTo(customer.getFirstname());
        assertThat(customerArgumentCaptorValue.getLastname()).isEqualTo(customer.getLastname());

    }


    @Test
    void itShouldUpdateCustomerNames() {
        // Given

        Customer customer = getCustomer();
        given(customerRepository.findById(any())).willReturn(Optional.ofNullable(customer));
        String firstname = "Jim";
        String lastname = "tes2";
        String document = """
                mutation updateCustomerNames($id: String, $firstname: String, $lastname: String) {
                    updateCustomerNames(id: $id ,firstname: $firstname, lastname: $lastname)
                }
                """;
        // When
        HttpGraphQlTester httpGraphQlTester = asAdmin(graphQlTester);
        httpGraphQlTester
                .document(document)
                .variable("id", customer.getUid().toString())
                .variable("firstname", firstname)
                .variable("lastName", lastname)
                .execute()
                .path("updateCustomerNames")
                .entity(String.class)
                .satisfies(id -> assertEquals(id, customer.getUid().toString()));
        // Then
        then(customerRepository).should().save(customerArgumentCaptor.capture());
        Customer customerArgumentCaptorValue = customerArgumentCaptor.getValue();
        assertThat(customerArgumentCaptorValue.getFirstname()).isEqualTo(firstname);
        assertThat(customerArgumentCaptorValue.getFirstname()).isEqualTo(lastname);
    }

    @Test
    void itShouldDeleteCustomerById() {
        // Given
        // When
        // Then

    }

    List<Customer> getCustomers() {
        return List.of(new Customer("Bob", "Joe", LocalDateTime.of(1993, 03, 22, 00, 00)),
                new Customer("James", "Mathew", LocalDateTime.of(1998, 03, 22, 00, 00)));
    }

    private HttpGraphQlTester asAdmin(HttpGraphQlTester graphQlTester) {
        return graphQlTester.mutate()
                .webTestClient((httpClient) -> httpClient.defaultHeaders((headers) -> headers.setBasicAuth("admin", "password")))
                .build();
    }

    private HttpGraphQlTester asUser(HttpGraphQlTester graphQlTester) {
        return graphQlTester.mutate()
                .webTestClient((httpClient) -> httpClient.defaultHeaders((headers) -> headers.setBasicAuth("user", "password")))
                .build();
    }

    Customer getCustomer() {
        UUID uid = UUID.fromString("3593839e-9f37-4fdb-82f3-e18ef18891b3");
        Customer customer = new Customer("bob", "Test", null);
        customer.setUid(uid);
        return customer;
    }
}