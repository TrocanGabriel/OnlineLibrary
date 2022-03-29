package com.online.library;

import com.online.library.controller.AuthController;
import com.online.library.model.Book;
import com.online.library.model.dto.BookDTO;
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

import static com.online.library.utils.Constants.BOOK_COPIES_NOT_FOUND_EXCEPTION;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ActiveProfiles(value = "test")
@SpringBootTest(classes = OnlineLibraryApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
public class BookControllerIntegrationTest {

    public static final String API_BOOK_LOAN = "/api/book/loan/";
    public static final String API_BOOK_RETURN = "/api/book/return/";
    public static final String API_BOOK = "/api/book/";
    public static final String API_BOOK1 = "/api/book";
    public static final String API_AUTH_LOGIN = "/api/auth/login";
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
            RequestSpecification req = given().basePath(API_AUTH_LOGIN).contentType(ContentType.JSON)
                    .body(request).when();
            Response response = req.post();
            token = response.getCookie("jwt-cookie");
        }
    }


    @Test
    public void getBook() {
        Book book = Book.builder().title("title").author("author").build();
        Book savedBook = bookRepository.save(book);

        RequestSpecification req = given().basePath(API_BOOK + savedBook.getId()).contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .header("Cookie", "jwt-cookie=" + token)
                .when();
        Response response = req.get();
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getBody().prettyPrint()).contains(savedBook.getTitle(), savedBook.getAuthor(), savedBook.getId().toString());
    }

    @Test
    public void getBooks() {
        Book book = Book.builder().title("title").author("author").build();
        bookRepository.save(book);

        RequestSpecification req = given().basePath(API_BOOK1).contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .header("Cookie", "jwt-cookie=" + token)
                .when();
        Response response = req.get();
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getBody().prettyPrint()).contains(book.getTitle(), book.getAuthor());
    }

    @Test
    public void createBook() throws Exception {
        BookDTO book = BookDTO.builder().title("title2").author("author").build();

        RequestSpecification req = given().basePath(API_BOOK1)
                .header("Authorization", "Bearer " + token)
                .header("Cookie", "jwt-cookie=" + token)
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(book))
                .when();
        Response response = req.post();
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getBody().prettyPrint()).contains(book.getTitle(), book.getAuthor());
    }

    @Test
    public void updateBook() throws Exception {
        Book book = Book.builder().title("title2").author("author").build();
        Book updateBook = bookRepository.save(book);
        updateBook.setAuthor("author2");
        BookDTO bookToBeUpdated = new BookDTO();
        BeanUtils.copyProperties(updateBook, bookToBeUpdated);

        RequestSpecification req = given().basePath(API_BOOK + bookToBeUpdated.getId())
                .header("Authorization", "Bearer " + token)
                .header("Cookie", "jwt-cookie=" + token)
                .contentType(ContentType.JSON)
                .body(objectMapper.writeValueAsString(bookToBeUpdated))
                .when();
        Response response = req.put();
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getBody().prettyPrint()).contains(bookToBeUpdated.getTitle(), bookToBeUpdated.getAuthor());
    }

    @Test
    public void deleteBook() {
        Book book = Book.builder().title("title2").author("author").build();
        Book updateBook = bookRepository.save(book);

        RequestSpecification req = given().basePath(API_BOOK + updateBook.getId())
                .header("Authorization", "Bearer " + token)
                .header("Cookie", "jwt-cookie=" + token)
                .when();
        Response response = req.delete();
        assertThat(response.getStatusCode()).isEqualTo(200);
    }

    @Test
    public void loanBook() {
        Book book = Book.builder().title("title2").author("author").build();
        Book updateBook = bookRepository.save(book);

        RequestSpecification req = given().basePath(API_BOOK + updateBook.getId())
                .header("Authorization", "Bearer " + token)
                .header("Cookie", "jwt-cookie=" + token)
                .when();
        Response response = req.get();
        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getBody().prettyPrint()).contains(updateBook.getTitle(), updateBook.getAuthor());
    }

    @Test
    public void loanUnavailableBook() {
        Book book = Book.builder().title("title2").author("author").build();
        Book updateBook = bookRepository.save(book);

        RequestSpecification req = given().basePath(API_BOOK_LOAN + updateBook.getId())
                .header("Authorization", "Bearer " + token)
                .header("Cookie", "jwt-cookie=" + token)
                .when();
        Response response = req.get();
        assertThat(response.getStatusCode()).isEqualTo(200);
        RequestSpecification reqTwo = given().basePath(API_BOOK_LOAN + updateBook.getId())
                .header("Authorization", "Bearer " + token)
                .header("Cookie", "jwt-cookie=" + token)
                .when();
        Response responseTwo = reqTwo.get();
        assertThat(responseTwo.getStatusCode()).isEqualTo(404);
        assertThat(responseTwo.getBody().prettyPrint()).contains(BOOK_COPIES_NOT_FOUND_EXCEPTION);

    }

    @Test
    public void returnBook() {
        Book book = Book.builder().title("title2").author("author").build();
        Book updateBook = bookRepository.save(book);

        RequestSpecification req = given().basePath(API_BOOK_RETURN + updateBook.getId())
                .header("Authorization", "Bearer " + token)
                .header("Cookie", "jwt-cookie=" + token)
                .when();
        Response response = req.get();
        assertThat(response.getStatusCode()).isEqualTo(200);
    }

}
