package com.example.wallet.Controllers;

import com.example.wallet.Entities.Person;
import com.example.wallet.Servises.JwtService;
import com.example.wallet.Servises.PersonService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@Slf4j
@RestController
@RequestMapping("/api/persons")
public class PersonController {
    @Autowired
    PersonService personService;
    @Autowired
    JwtService jwtService;


    @GetMapping("/{id}")
    public ResponseEntity<Person> getPersonById(@PathVariable String id,
                                                @RequestHeader("Authorization") String token) {
        try {
            // Check if the person exists
            Person person = personService.getPersonById(id);
            if (person == null) {
                log.warn("Person not found with ID: {}", id);
                return ResponseEntity.notFound().build();
            }

            // Validate the token
            if (token == null || !token.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }

            String jwt = token.substring(7).trim();

            if (jwtService.isTokenExpired(jwt)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }

            String personIdFromToken = jwtService.extractPersonId(jwt);

            // Check if the IDs match
            if (!id.equals(personIdFromToken)) {
                log.warn("Unauthorized access attempt for ID: {}", id);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }

            return ResponseEntity.ok(person);
        } catch (Exception e) {
            log.error("Error retrieving person: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @PostMapping
    public ResponseEntity<String> createPerson(@Valid @RequestBody Person person) {
        try {
            Person savedPerson = personService.createPerson(person);
            if (savedPerson == null) {
                log.warn("Creation failed for person: {}", person);
                return ResponseEntity.badRequest().body("Creation failed");
            }
            log.info("Successfully created person with ID: {}", savedPerson.getId());
            return ResponseEntity.ok("Person created successfully");
        } catch (Exception e) {
            log.error("Error creating person: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Person> updatePerson(@PathVariable String id,
                                               @RequestBody Person personDetails,
                                               @RequestHeader("Authorization") String token) {
        try {
            // Validate the token and extract the person ID
            if (jwtService.isTokenExpired(token.substring(7))) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }

            String personIdFromToken = jwtService.extractPersonId(token.substring(7));

            // Ensure the person ID from the token matches the ID in the path
            if (!personIdFromToken.equals(id)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }

            // Check if the person exists before attempting to update
            Person existingPerson = personService.getPersonById(id);
            if (existingPerson == null) {
                log.warn("Person not found with ID: {}", id);
                return ResponseEntity.notFound().build();
            }

            // Proceed to update if the person exists
            Person updatedPerson = personService.updatePerson(id, personDetails);
            return ResponseEntity.ok(updatedPerson);
        } catch (Exception e) {
            log.error("Error updating person with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerson(@PathVariable String id,
                                             @RequestHeader("Authorization") String token) {
        try {
            // Validate the token and extract the person ID from the token
            String personIdFromToken = jwtService.extractPersonId(token.substring(7));

            // Check if the token ID matches the path ID
            if (!id.equals(personIdFromToken)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            // Check if the person exists before attempting to delete
            Person existingPerson = personService.getPersonById(id);
            if (existingPerson == null) {
                log.warn("Person not found with ID: {}", id);
                return ResponseEntity.notFound().build();
            }

            // Proceed to delete if the person exists
            personService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error deleting person with ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
