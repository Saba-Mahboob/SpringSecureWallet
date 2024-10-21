package com.example.wallet.Controllers;

import com.example.wallet.Entities.Transaction;
import com.example.wallet.Servises.AccountService;
import com.example.wallet.Servises.JwtService;
import com.example.wallet.Servises.TransactionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Slf4j
@RestController
@RequestMapping("/api/transactions")
public class TransactionsController {
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private AccountService accountService;
    @Autowired
    JwtService jwtService;

    @PostMapping("/deposit")
    public ResponseEntity<String> deposit(
            @RequestHeader("Authorization") String token,
            @RequestParam("accountId") int accountId,
            @RequestParam("amount") double amount) {

        // Extract personId from token
        String personId = extractPersonIdFromToken(token);
        log.info("Deposit request for accountId: {}, personId: {}, amount: {}", accountId, personId, amount);

        // Check if the account belongs to the person
        if (!accountService.isAccountBelongsToPerson(accountId, personId)) {
            log.warn("Access denied for accountId: {} by personId: {}", accountId, personId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied to this account.");
        }

        try {
            transactionService.deposit(accountId, amount);
            log.info("Deposit successful for accountId: {}, amount: {}", accountId, amount);
            return ResponseEntity.ok("Deposit successful.");
        } catch (IllegalArgumentException e) {
            log.error("Deposit failed for accountId: {}, amount: {} - {}", accountId, amount, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/withdraw")
    public ResponseEntity<String> withdraw(
            @RequestHeader("Authorization") String token,
            @RequestParam("accountId") int accountId,
            @RequestParam("amount") double amount) {

        // Extract personId from token
        String personId = extractPersonIdFromToken(token);
        log.info("Withdrawal request for accountId: {}, personId: {}, amount: {}", accountId, personId, amount);

        // Check if the account belongs to the person
        if (!accountService.isAccountBelongsToPerson(accountId, personId)) {
            log.warn("Access denied for accountId: {} by personId: {}", accountId, personId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied to this account.");
        }

        try {
            transactionService.withdraw(accountId, amount);
            log.info("Withdrawal successful for accountId: {}, amount: {}", accountId, amount);
            return ResponseEntity.ok("Withdrawal successful.");
        } catch (IllegalArgumentException e) {
            log.error("Withdrawal failed for accountId: {}, amount: {} - {}", accountId, amount, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/get")
    public ResponseEntity<List<Transaction>> getTransactionsByPersonId(
            @RequestHeader("Authorization") String token) {

        // Extract personId from token
        String personId = extractPersonIdFromToken(token);
        log.info("Retrieving transactions for personId: {}", personId);

        List<Transaction> transactions = transactionService.getTransactionsByPersonId(personId);
        if (transactions.isEmpty()) {
            log.info("No transactions found for personId: {}", personId);
            return ResponseEntity.noContent().build();
        }

        log.info("Found {} transactions for personId: {}", transactions.size(), personId);
        return ResponseEntity.ok(transactions);
    }

    private String extractPersonIdFromToken(String token) {
        return jwtService.extractPersonId(token.substring(7).trim());
    }

}
