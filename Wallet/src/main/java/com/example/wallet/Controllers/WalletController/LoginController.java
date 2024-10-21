package com.example.wallet.Controllers.WalletController;

import com.example.wallet.Entities.LoginRequest;
import com.example.wallet.Entities.Person;
import com.example.wallet.Servises.JwtService;
import com.example.wallet.Servises.PersonService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api")
public class LoginController {
    @Autowired
    PersonService personService;
    @Autowired
    JwtService jwtService;

    @Autowired
    PasswordEncoder passwordEncoder;


    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            // Authenticate user
            Person person = personService.getPersonById(loginRequest.getId());
            if (person == null) {
                log.warn("Login failed for id: {}", loginRequest.getId());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not exist");
            }

//            System.out.println(person.getPassword()+"    "+loginRequest.getPassword());
//            String encodedPass=passwordEncoder.encode(loginRequest.getPassword());
//            System.out.println(encodedPass);
//            System.out.println(matches(loginRequest.getPassword(), person.getPassword()));

            if (!matches(loginRequest.getPassword(), person.getPassword())){
                log.warn("Login failed for id: {}", loginRequest.getId());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password");
            }

            // Generate JWT token
            String token = jwtService.generateToken(person);
            log.info("Successfully logged in for person with ID: {}", person.getId());
            return ResponseEntity.ok(token);
        } catch (Exception e) {
            log.error("Error during login: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }
}
