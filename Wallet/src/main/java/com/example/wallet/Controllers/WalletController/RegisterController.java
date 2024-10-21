package com.example.wallet.Controllers.WalletController;

import com.example.wallet.Entities.Person;
import com.example.wallet.Servises.PersonService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@Slf4j
@RestController
@RequestMapping("/api")
public class RegisterController {
    @Autowired
    PersonService personService;



    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody Person person) {
        try {
            Person savedPerson = personService.createPerson(person);
            if (savedPerson == null) {
                log.warn("Creation failed for person: {}", person);
                return ResponseEntity.badRequest().body("registration failed");
            }
            log.info("Successfully register  with ID: {}", savedPerson.getId());
            return ResponseEntity.ok("Person registered successfully");
        } catch (Exception e) {
            log.error("Error register : {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }
}
