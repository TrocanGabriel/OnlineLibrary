package com.online.library;

import com.online.library.controller.AuthController;
import com.online.library.model.Customer;
import com.online.library.model.dto.CustomerDTO;
import com.online.library.model.dto.LoginRequestDTO;
import com.online.library.model.dto.RegisterRequestDTO;
import com.online.library.repository.BookRepository;
import com.online.library.repository.CustomerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@ActiveProfiles(value = "test")
@SpringBootTest(classes = OnlineLibraryApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
public class CustomerControllerIntegrationTest {

    public static final String API_AUTH_LOGIN = "/api/auth/login";
    public static final String API_CUSTOMER = "/api/customer/";
    public static final String API_CUSTOMER1 = "/api/customer";
    @Value("${library.app.testUser}")
    private String testUser;
    @Value("${library.app.testUserName}")
    private String testuserName;
    @Value("${library.app.testUserPassword}")
    private String testUserPassword;

    @LocalServerPort
    int port;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AuthController authController;

    @Autowired
    private ObjectMapper objectMapper;

    private static String token;

    @Before
    public void init() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        generateToken();
    }

    @After
    public void tearDown() {
        bookRepository.deleteAll();
    }

    private void generateToken() {
        if (!customerRepository.existsByEmail(testUser)) {
            authController.register(new RegisterRequestDTO(testUser, testuserName, testUserPassword));
        }
        if (token == null) {
            LoginRequestDTO request = new LoginRequestDTO().builder().username(testUser).password(testUserPassword).build();
            Response response = given().basePath(API_AUTH_LOGIN).contentType(ContentType.JSON)
                    .body(request).when().post();
            token = response.getCookie("jwt-cookie");
        }
    }

    @Test
    public void getCustomer() {
        Optional<Customer> customer = customerRepository.findByEmail(testUser);
        assertTrue(customer.isPresent());

        RequestSpecification req = given().basePath(API_CUSTOMER + customer.get().getId()).contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .header("Cookie", "jwt-cookie=" + token)
                .when();
        Response response = req.get();
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getBody().prettyPrint()).contains(customer.get().getEmail(), customer.get().getName());
    }

    @Test
    public void getCustomers() {
        RequestSpecification req = given().basePath(API_CUSTOMER1).contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .header("Cookie", "jwt-cookie=" + token)
                .when();
        Response response = req.get();
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getBody().prettyPrint()).contains(testUser, testuserName);
    }


    @Test
    public void updateCustomer() throws Exception {
        Optional<Customer> customer = customerRepository.findByEmail(testUser);
        CustomerDTO customerDTO = new CustomerDTO();
        BeanUtils.copyProperties(customer.get(), customerDTO);
        customerDTO.setName("newName");

        RequestSpecification req = given().basePath(API_CUSTOMER + customerDTO.getId())
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .header("Cookie", "jwt-cookie=" + token)
                .body(objectMapper.writeValueAsString(customerDTO))
                .when();
        Response response = req.put();
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getBody().prettyPrint()).contains(customerDTO.getName(), customerDTO.getEmail());
    }

    @Test
    public void deleteCustomer() {
        String testEmail = "test2@gmail.com";
        authController.register(new RegisterRequestDTO(testEmail, testuserName, testUserPassword));
        Optional<Customer> customer = customerRepository.findByEmail(testEmail);
        assertTrue(customer.isPresent());

        RequestSpecification req = given().basePath(API_CUSTOMER + customer.get().getId())
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .header("Cookie", "jwt-cookie=" + token)
                .when();
        Response response = req.delete();
        assertThat(response.getStatusCode()).isEqualTo(200);
    }

}
