package com.example.wallet.Entities;

import com.example.wallet.Entities.Enums.TransactionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotNull
    private LocalDateTime localDateTime;
    @NotNull
    private Double remain;
    @NotNull
    private Double amount;

    @Enumerated(EnumType.STRING)
    @NotNull
    TransactionType transactionType;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

}
