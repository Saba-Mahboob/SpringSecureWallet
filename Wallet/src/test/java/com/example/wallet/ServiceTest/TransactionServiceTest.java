package com.example.wallet.ServiceTest;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.wallet.Entities.Account;
import com.example.wallet.Entities.Person;
import com.example.wallet.Entities.Transaction;
import com.example.wallet.Repositoreis.AccountRepository;
import com.example.wallet.Repositoreis.PersonRepository;
import com.example.wallet.Repositoreis.TransactionRepository;
import com.example.wallet.Servises.AccountService;
import com.example.wallet.Servises.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class TransactionServiceTest {

    @InjectMocks
    private TransactionService transactionService;

    @Mock
    private AccountService accountService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private TransactionRepository transactionRepository;

    private Account account;
    private Person person;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        person = new Person();
        person.setId("1");

        account = new Account();
        account.setId(1);
        account.setCredit(1000.0);
        account.setPerson(person);
    }

    @Test
    public void testWithdraw_Success() {
        double amount = 500.0;
        when(accountRepository.findById(1)).thenReturn(Optional.of(account));
        when(personRepository.findById(person.getId())).thenReturn(Optional.of(person));
        when(transactionRepository.findByPersonIdAndLocalDateTime(anyString(), any(LocalDateTime.class)))
                .thenReturn(List.of()); // No transactions today

        Transaction transaction = transactionService.withdraw(1, amount);

        assertEquals(amount, transaction.getAmount());
        assertEquals(500.0, account.getCredit());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    public void testWithdraw_ExceedsCredit() {
        double amount = 1500.0;
        when(accountRepository.findById(1)).thenReturn(Optional.of(account));

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.withdraw(1, amount);
        });

        assertEquals("WITHDRAWAL amount exceeds available credit", exception.getMessage());
    }

    @Test
    public void testWithdraw_NegativeAmount() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.withdraw(1, -100.0);
        });

        assertEquals("WITHDRAWAL amount must be greater than zero.", exception.getMessage());
    }

    @Test
    public void testDeposit_Success() {
        double amount = 500.0;
        when(accountRepository.findById(1)).thenReturn(Optional.of(account));
        when(personRepository.findById(person.getId())).thenReturn(Optional.of(person));

        Transaction transaction = transactionService.deposit(1, amount);

        assertEquals(amount, transaction.getAmount());
        assertEquals(1500.0, account.getCredit());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    public void testDeposit_NegativeAmount() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.deposit(1, -100.0);
        });

        assertEquals("Deposit amount must be greater than zero.", exception.getMessage());
    }

    @Test
    public void testGetTransactionsByPersonId() {
        Transaction transaction = new Transaction();
        transaction.setAmount(200.0);
        transaction.setPerson(person);

        when(transactionRepository.findByPerson_Id(person.getId())).thenReturn(List.of(transaction));

        List<Transaction> transactions = transactionService.getTransactionsByPersonId(person.getId());

        assertEquals(1, transactions.size());
        assertEquals(200.0, transactions.get(0).getAmount());
    }
}

