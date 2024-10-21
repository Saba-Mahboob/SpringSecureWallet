package com.example.wallet.Repositoreis;

import com.example.wallet.Entities.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction,Integer> {
    List<Transaction> findByPersonIdAndLocalDateTime(String personId, LocalDateTime localDateTime);
    List<Transaction> findByPerson_Id(String personId);
}
