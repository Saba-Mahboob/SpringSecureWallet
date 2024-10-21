package com.example.wallet.Servises;

import com.example.wallet.Entities.Account;
import com.example.wallet.Entities.Enums.TransactionType;
import com.example.wallet.Entities.Person;
import com.example.wallet.Entities.Transaction;
import com.example.wallet.Repositoreis.AccountRepository;
import com.example.wallet.Repositoreis.PersonRepository;
import com.example.wallet.Repositoreis.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {
    @Autowired
    AccountService accountService;
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private TransactionRepository transactionRepository;
    private static final double DAILY_WITHDRAWAL_LIMIT = 10000000;
    public Transaction withdraw(int accountId, Double amount) {

        //Find account
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        //Find the owner of the account
        Person person=personRepository.findById(account.getPerson().getId()).orElse(null);

       //Amount must less than credit in Account Entity
        if (amount.compareTo(account.getCredit()) > 0) {
            throw new IllegalArgumentException("WITHDRAWAL amount exceeds available credit");
        }
        // Validate amount
        if (amount.compareTo(0.0) <= 0) {
            throw new IllegalArgumentException("WITHDRAWAL amount must be greater than zero.");
        }
        // Check daily withdrawal limit
        double totalWithdrawnToday = getTotalWithdrawnToday(person.getId());
        if (DAILY_WITHDRAWAL_LIMIT -(totalWithdrawnToday+amount)< 0) {
            throw new IllegalArgumentException("Daily withdrawal limit exceeded.");
        }
        //Counting new remain
        double remain= (account.getCredit()-amount);

        //update credit value in Account
        account.setCredit(remain);
        accountService.updateAccount(accountId,account);


        //Setting transaction in Transaction Entity
        Transaction transaction = new Transaction();
        transaction.setPerson(person);
        transaction.setAccount(account);
        transaction.setAmount(amount);
        transaction.setLocalDateTime(LocalDateTime.now());
        transaction.setTransactionType(TransactionType.WITHDRAWAL);
        transaction.setRemain(remain);

        // Save transaction
        return transactionRepository.save(transaction);
    }

    public Transaction deposit(int accountId, Double amount) {

        //Validate amount
        if (amount.compareTo(0.0) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be greater than zero.");
        }
        //Check if the account exist
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));

        //Find the owner of the account
        Person person=personRepository.findById(account.getPerson().getId()).orElse(null);

        //Counting new remain
        double remain=account.getCredit()+amount;

        //update credit value in Account
        account.setCredit(remain);
        accountService.updateAccount(accountId,account);

        //Setting the transaction in Transaction Entity
        Transaction transaction = new Transaction();
        transaction.setPerson(person);
        transaction.setAccount(account);
        transaction.setAmount(amount);
        transaction.setLocalDateTime(LocalDateTime.now());
        transaction.setTransactionType(TransactionType.DEPOSIT);
        transaction.setRemain(remain);

        // Save transaction
        return transactionRepository.save(transaction);
    }


    private double getTotalWithdrawnToday(String personId) {
        LocalDateTime localDateTime = LocalDateTime.now();
        List<Transaction> transactions = transactionRepository.findByPersonIdAndLocalDateTime(personId, localDateTime);

        return transactions.stream()
                .mapToDouble(Transaction::getAmount)
                .sum();
    }

    public List<Transaction> getTransactionsByPersonId(String personId) {
        return transactionRepository.findByPerson_Id(personId);
    }
}
