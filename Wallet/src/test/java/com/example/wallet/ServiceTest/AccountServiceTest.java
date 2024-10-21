package com.example.wallet.ServiceTest;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.wallet.Entities.Account;
import com.example.wallet.Entities.Person;
import com.example.wallet.Repositoreis.AccountRepository;
import com.example.wallet.Repositoreis.PersonRepository;
import com.example.wallet.Servises.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

public class AccountServiceTest {

    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private PersonRepository personRepository;

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
    public void testSaveAccount_PersonDoesNotExist() {
        when(personRepository.existsById(person.getId())).thenReturn(false);

        String result = accountService.saveAccount(account);

        assertEquals("Person with ID 1 does not exist.", result);
        verify(accountRepository, never()).save(account);
    }

    @Test
    public void testSaveAccount_Success() {
        when(personRepository.existsById(person.getId())).thenReturn(true);

        String result = accountService.saveAccount(account);

        assertEquals("Account created successfully.", result);
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    public void testGetAccountById() {
        when(accountRepository.findById(1)).thenReturn(Optional.of(account));

        Optional<Account> foundAccount = accountService.getAccountById(1);

        assertTrue(foundAccount.isPresent());
        assertEquals(account.getId(), foundAccount.get().getId());
    }

    @Test
    public void testGetAllAccounts() {
        when(accountRepository.findAll()).thenReturn(List.of(account));

        List<Account> accounts = accountService.getAllAccounts();

        assertEquals(1, accounts.size());
        assertEquals(account.getId(), accounts.get(0).getId());
    }

    @Test
    public void testUpdateAccount_Success() {
        when(accountRepository.findById(1)).thenReturn(Optional.of(account));

        Account updatedAccount = new Account();
        updatedAccount.setCredit(2000.0);

        Account result = accountService.updateAccount(1, updatedAccount);

        assertEquals(updatedAccount.getCredit(), result.getCredit());
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    public void testDeleteAccount() {
        doNothing().when(accountRepository).deleteById(1);

        accountService.deleteAccount(1);

        verify(accountRepository, times(1)).deleteById(1);
    }

    @Test
    public void testIsAccountBelongsToPerson_True() {
        when(accountRepository.findById(1)).thenReturn(Optional.of(account));

        boolean result = accountService.isAccountBelongsToPerson(1, person.getId());

        assertTrue(result);
    }

    @Test
    public void testIsAccountBelongsToPerson_False() {
        when(accountRepository.findById(1)).thenReturn(Optional.of(account));

        boolean result = accountService.isAccountBelongsToPerson(1, "2");

        assertFalse(result);
    }
}

