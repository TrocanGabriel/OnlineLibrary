package com.online.library.controller;

import com.online.library.exception.RecordNotFoundException;
import com.online.library.model.Customer;
import com.online.library.model.dto.LoginRequestDTO;
import com.online.library.model.dto.MessageResponseDTO;
import com.online.library.model.dto.RegisterRequestDTO;
import com.online.library.repository.CustomerRepository;
import com.online.library.utils.JWTUtils;
import com.online.library.utils.Constants;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Api(value = "Auth Rest Controller")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JWTUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {

        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequestDTO.getUsername(), loginRequestDTO.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString())
                .body(new MessageResponseDTO("Welcome " + userDetails.getUsername()));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDTO registerRequestDTO) {
        if (customerRepository.existsByEmail(registerRequestDTO.getEmail())) {
            throw new RecordNotFoundException(Constants.RECORD_NOT_FOUND_EXCEPTION);
        }

        Customer customer = new Customer(registerRequestDTO.getEmail(),
                registerRequestDTO.getName(),
                encoder.encode(registerRequestDTO.getPassword()));

        customerRepository.save(customer);

        return ResponseEntity.ok(new MessageResponseDTO("Registration complete!"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new MessageResponseDTO("Signed out!"));
    }
}
