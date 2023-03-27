# CustomerGraphAPI
Customer Graph API is a proof of Concept (POC),  API was created to demonstrate my skills as a backend developer.

The API uses H2 in-memory database for CRUD operations. The API's purpose is to demonstrate a possible Customer microservice, that is used to store customer information. 

This API uses the following technologies:
Java 17
Spring boot 3
Spring for GraphQL
Spring Data JPA
Spring Security 
H2 in-memory database 
Lombok 
The project follows Spring recommended layout  [https://docs.spring.io/spring-boot/docs/current/reference/html/using.html#using.structuring-your-code]
com
 +- example
     +- customerGraphAPI
         +- CustomerGraphApiApplication.java
         |
         +- customer
         |   +- Customer.java
         |   +- CustomerController.java
         |   +- CustomerService.java
         |   +- CustomerRepository.java

