package com.example.wallet.Repositoreis;

import com.example.wallet.Entities.Account;
import com.example.wallet.Entities.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AccountRepository extends CrudRepository<Account,Integer> {
    //Optional<Person>findByAccountId(int id);
}
