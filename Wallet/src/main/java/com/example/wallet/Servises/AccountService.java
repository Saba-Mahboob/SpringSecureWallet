package com.example.wallet.Servises;

import com.example.wallet.Entities.Account;
import com.example.wallet.Entities.Person;
import com.example.wallet.Repositoreis.AccountRepository;
import com.example.wallet.Repositoreis.PersonRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private PersonRepository personRepository;

    // Create
    public String saveAccount(Account account) {
        if (!personRepository.existsById(account.getPerson().getId())) {
            return "Person with ID " + account.getPerson().getId() + " does not exist.";
        }
        accountRepository.save(account);
        return "Account created successfully.";
    }

    // Read
    public Optional<Account> getAccountById(int id) {
        return accountRepository.findById(id);
    }

    public List<Account> getAllAccounts() {
        return (List<Account>) accountRepository.findAll();
    }

    // Update
    public Account updateAccount(int accountId, Account accountDetails) {
        log.info("Updating account with ID: {}", accountId);

        // Fetch the existing account
        Account existingAccount = accountRepository.findById(accountId)
                .orElseThrow(() -> {
                    log.warn("Account not found with ID: {}", accountId);
                    return new RuntimeException("Account not found");
                });

        // Update fields as necessary
        existingAccount.setCredit(accountDetails.getCredit());
        // Add more fields to update as needed

        // Save the updated account
        Account updatedAccount = accountRepository.save(existingAccount);
        log.info("Account with ID: {} successfully updated", accountId);
        return updatedAccount;
    }

    public Optional<Account> findById(int accountId) {
        return accountRepository.findById(accountId);
    }

    public void deleteAccount(int accountId) {
        log.info("Deleting account with ID: {}", accountId);
        accountRepository.deleteById(accountId);
        log.info("Account with ID: {} successfully deleted", accountId);
    }

    public boolean isAccountBelongsToPerson(int accountId, String personId) {
        Account account = accountRepository.findById(accountId).orElse(null);
        return account != null && account.getPerson().getId().equals(personId);
    }

}
