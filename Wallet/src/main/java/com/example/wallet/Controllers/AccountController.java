package com.example.wallet.Controllers;

import com.example.wallet.Entities.Account;
import com.example.wallet.Entities.Person;
import com.example.wallet.Servises.AccountService;
import com.example.wallet.Servises.JwtService;
import com.example.wallet.Servises.PersonService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/accounts")
public class AccountController {
    @Autowired
    private AccountService accountService;
    @Autowired
    private JwtService jwtService;

    @Autowired
    PersonService personService;

    // Create
    @PostMapping
    public ResponseEntity<String> createAccount(@Valid @RequestBody Account account) {
        String responseMessage = accountService.saveAccount(account);
        if (responseMessage.startsWith("Account created")) {
            return ResponseEntity.status(HttpStatus.CREATED).body(responseMessage);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseMessage);
    }

    // Read
    @GetMapping("/get")
    public ResponseEntity<Account> getAccount(
            @RequestHeader("Authorization") String token,
            @RequestParam("accountId") int accountId){
            // Extract and validate the JWT token
            String jwt = token.substring(7).trim();
            String personId=jwtService.extractPersonId(jwt);
            if (jwtService.isTokenExpired(jwt)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // Check if the person exists
            Person personOptional = personService.getPersonById(personId);
            if  (personOptional==null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            // Check if the account exists and belongs to the person
            Account account = accountService.getAccountById(accountId).orElse(null);
            if (account==null || !account.getPerson().getId().equals(personId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.ok(account);
    }

    //Update
    @PutMapping("/update")
    public ResponseEntity<Account> updateAccount(
            @RequestHeader("Authorization") String token,
            @RequestParam("accountId") int accountId,
            @RequestBody Account accountDetails) {

        // Extract and validate the JWT token
        String jwt = token.substring(7).trim();
        String personId=jwtService.extractPersonId(jwt);
        if (jwtService.isTokenExpired(jwt)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Check if the person exists
        Person personOptional = personService.getPersonById(personId);
        if (personOptional==null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        // Check if the account exists and belongs to the person
        Optional<Account> accountOptional = accountService.getAccountById(accountId);
        if (accountOptional.isEmpty() || !accountOptional.get().getPerson().getId().equals(personId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Update the account
        Account updatedAccount = accountService.updateAccount(accountId, accountDetails);
        return ResponseEntity.ok(updatedAccount);
    }
    //Delete
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteAccount(
            @RequestHeader("Authorization") String token,
            @RequestParam("accountId") int accountId) {

        // Extract and validate the JWT token
        String jwt = token.substring(7).trim();
        String personId=jwtService.extractPersonId(jwt);
        if (jwtService.isTokenExpired(jwt)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Check if the person exists
        Person personOptional = personService.getPersonById(personId);
        if (personOptional==null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        // Check if the account exists and belongs to the person
        Optional<Account> accountOptional = accountService.findById(accountId);
        if (accountOptional.isEmpty() || !accountOptional.get().getPerson().getId().equals(personId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Delete the account
        accountService.deleteAccount(accountId);
        return ResponseEntity.noContent().build();
    }



}
