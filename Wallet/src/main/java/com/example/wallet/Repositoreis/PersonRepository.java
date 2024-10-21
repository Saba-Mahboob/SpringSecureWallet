package com.example.wallet.Repositoreis;

import com.example.wallet.Entities.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PersonRepository extends CrudRepository<Person,String> {
    Optional<Person> findById(String id);
}
