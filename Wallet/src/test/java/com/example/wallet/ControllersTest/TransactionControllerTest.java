package com.example.wallet.ControllersTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.example.wallet.Controllers.TransactionsController;
import com.example.wallet.Entities.Transaction;
import com.example.wallet.Servises.AccountService;
import com.example.wallet.Servises.JwtService;
import com.example.wallet.Servises.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class TransactionControllerTest {

    @InjectMocks
    private TransactionsController transactionsController;

    @Mock
    private TransactionService transactionService;

    @Mock
    private AccountService accountService;

    @Mock
    private JwtService jwtService;

    private String token = "Bearer some.jwt.token";
    private String personId = "0987654321";
    private Transaction transaction;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        transaction = new Transaction();
        transaction.setAmount(300.0);
        // Set other fields as needed
    }

    @Test
    public void testDeposit_Success() {
        when(jwtService.extractPersonId(anyString())).thenReturn(personId);
        when(accountService.isAccountBelongsToPerson(anyInt(), anyString())).thenReturn(true);
        when(transactionService.deposit(anyInt(), anyDouble())).thenReturn(transaction);

        ResponseEntity<String> response = transactionsController.deposit(token, 1, 300.0);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Deposit successful.", response.getBody());
    }

    @Test
    public void testDeposit_AccountAccessDenied() {
        when(jwtService.extractPersonId(anyString())).thenReturn(personId);
        when(accountService.isAccountBelongsToPerson(anyInt(), anyString())).thenReturn(false);

        ResponseEntity<String> response = transactionsController.deposit(token, 1, 300.0);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Access denied to this account.", response.getBody());
    }

    @Test
    public void testDeposit_BadRequest() {
        when(jwtService.extractPersonId(anyString())).thenReturn(personId);
        when(accountService.isAccountBelongsToPerson(anyInt(), anyString())).thenReturn(true);
        doThrow(new IllegalArgumentException("Invalid amount")).when(transactionService).deposit(anyInt(), anyDouble());

        ResponseEntity<String> response = transactionsController.deposit(token, 1, 300.0);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid amount", response.getBody());
    }

    @Test
    public void testWithdraw_Success() {
        when(jwtService.extractPersonId(anyString())).thenReturn(personId);
        when(accountService.isAccountBelongsToPerson(anyInt(), anyString())).thenReturn(true);
        when(transactionService.withdraw(anyInt(), anyDouble())).thenReturn(transaction);

        ResponseEntity<String> response = transactionsController.withdraw(token, 1, 300.0);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Withdrawal successful.", response.getBody());
    }

    @Test
    public void testWithdraw_AccountAccessDenied() {
        when(jwtService.extractPersonId(anyString())).thenReturn(personId);
        when(accountService.isAccountBelongsToPerson(anyInt(), anyString())).thenReturn(false);

        ResponseEntity<String> response = transactionsController.withdraw(token, 1, 300.0);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("Access denied to this account.", response.getBody());
    }

    @Test
    public void testWithdraw_BadRequest() {
        when(jwtService.extractPersonId(anyString())).thenReturn(personId);
        when(accountService.isAccountBelongsToPerson(anyInt(), anyString())).thenReturn(true);
        doThrow(new IllegalArgumentException("Invalid amount")).when(transactionService).withdraw(anyInt(), anyDouble());

        ResponseEntity<String> response = transactionsController.withdraw(token, 1, 300.0);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid amount", response.getBody());
    }

    @Test
    public void testGetTransactionsByPersonId_Success() {
        when(jwtService.extractPersonId(anyString())).thenReturn(personId);
        when(transactionService.getTransactionsByPersonId(anyString())).thenReturn(Arrays.asList(transaction));

        ResponseEntity<List<Transaction>> response = transactionsController.getTransactionsByPersonId(token);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    public void testGetTransactionsByPersonId_NoContent() {
        when(jwtService.extractPersonId(anyString())).thenReturn(personId);
        when(transactionService.getTransactionsByPersonId(anyString())).thenReturn(Collections.emptyList());

        ResponseEntity<List<Transaction>> response = transactionsController.getTransactionsByPersonId(token);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}
