package com.example.wallet.ControllersTest;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.wallet.Controllers.AccountController;
import com.example.wallet.Entities.Account;
import com.example.wallet.Servises.AccountService;
import com.example.wallet.Servises.JwtService;
import com.example.wallet.Servises.PersonService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

public class AccountControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AccountService accountService;

    @Mock
    private JwtService jwtService;

    @Mock
    private PersonService personService;

    @InjectMocks
    private AccountController accountController;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void createAccount_ShouldReturnCreated_WhenAccountIsValid() throws Exception {
        Account account = new Account();
        account.setAccountNumber("ACC123456789");
        account.setCredit(15000.75);
        account.setOpenDate(("2024-10-19"));
        account.setSheba("IR123456789012345678901212");
        account.setPerson(personService.getPersonById("0987654321"));

        when(accountService.saveAccount(any(Account.class))).thenReturn("Account created successfully");

        String accountJson = objectMapper.writeValueAsString(account);

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(accountJson))
                .andExpect(status().isCreated())
                .andExpect(content().string("Account created successfully"));
    }

    @Test
    public void getAccount_ShouldReturnOk_WhenAccountExists() throws Exception {
        String token = "Bearer valid.jwt.token";
        int accountId = 1;
        String personId = "0987654321";
        Account account = new Account();
        account.setAccountNumber("ACC123456789");
        account.setCredit(15000.75);
        account.setOpenDate("2024-10-19");
        account.setSheba("IR123456789012345678901212");
        account.setPerson(personService.getPersonById("0987654321"));

        when(jwtService.extractPersonId(anyString())).thenReturn(personId);
        when(jwtService.isTokenExpired(anyString())).thenReturn(false);
        when(personService.getPersonById(personId)).thenReturn(personService.getPersonById("0987654321"));
        when(accountService.getAccountById(accountId)).thenReturn(Optional.of(account));

        mockMvc.perform(get("/accounts/get")
                        .header("Authorization", token)
                        .param("accountId", String.valueOf(accountId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value("ACC123456789"));
    }

    @Test
    public void updateAccount_ShouldReturnOk_WhenUpdateIsSuccessful() throws Exception {
        String token = "Bearer valid.jwt.token";
        int accountId = 1;
        Account accountDetails = new Account();
        accountDetails.setAccountNumber("ACC123456789");
        accountDetails.setCredit(20000.00);

        Account existingAccount = new Account();
        existingAccount.setAccountNumber("ACC123456789");
        existingAccount.setCredit(15000.75);

        when(jwtService.extractPersonId(anyString())).thenReturn("0987654321");
        when(jwtService.isTokenExpired(anyString())).thenReturn(false);
        when(personService.getPersonById(anyString())).thenReturn(personService.getPersonById("0987654321"));
        when(accountService.getAccountById(accountId)).thenReturn(Optional.of(existingAccount));
        when(accountService.updateAccount(eq(accountId), any(Account.class))).thenReturn(existingAccount);

        String accountJson = objectMapper.writeValueAsString(accountDetails);

        mockMvc.perform(put("/accounts/update")
                        .header("Authorization", token)
                        .param("accountId", String.valueOf(accountId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(accountJson))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteAccount_ShouldReturnNoContent_WhenDeletionIsSuccessful() throws Exception {
        String token = "Bearer valid.jwt.token";
        int accountId = 1;

        when(jwtService.extractPersonId(anyString())).thenReturn("0987654321");
        when(jwtService.isTokenExpired(anyString())).thenReturn(false);
        when(personService.getPersonById(anyString())).thenReturn(personService.getPersonById("0987654321"));
        when(accountService.findById(accountId)).thenReturn(Optional.of(new Account()));

        mockMvc.perform(delete("/accounts/delete")
                        .header("Authorization", token)
                        .param("accountId", String.valueOf(accountId)))
                .andExpect(status().isNoContent());
    }
}

