package com.example.wallet.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotBlank
    @Column(unique = true)
    @Size(max = 16,min = 12)
    private String accountNumber;
    @NotNull
    @Min(value = 10000)
    private Double credit;
    @NotBlank
    private String openDate;
    @Column(unique = true)
    @NotBlank
    @Pattern(regexp = "^IR\\d{24}$", message = "The ID must start with 'IR' followed by exactly 24 digits.")
    private String sheba;
    @ManyToOne
    @JoinColumn(name = "person_id",nullable = false )
    private Person person;
//    @Transient
//    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<Transaction> transactions;
}
