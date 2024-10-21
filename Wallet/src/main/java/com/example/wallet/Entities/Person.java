package com.example.wallet.Entities;

import com.example.wallet.Entities.Enums.Gender;
import com.example.wallet.Entities.Enums.MilitaryServiceStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
//This entity also use during registration
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person {
    @Id
    @Column(name = "national-id")
    @Pattern(regexp = "^\\d{10}$", message = "National ID must be exactly 10 digits.")
    private String id;
    @NotBlank
    @Size(min = 3,max = 50)
    private String name;
    @NotBlank
    @Size(min = 3,max = 50)
    private String lastName;
    @NotBlank
    @Pattern(regexp = "^09\\d{9}$", message = "Phone number must start with 09 and be exactly 11 digits long")
    private String phoneNumber;
    @NotBlank
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$", message = "Invalid email format")
    private String email;
    @NotBlank
    @Size(min = 8)
    private String password;
    @NotNull
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @NotNull
    private Integer age;
    @Enumerated(EnumType.STRING)
    private MilitaryServiceStatus militaryServiceStatus;
//    @Transient
//    @OneToMany(mappedBy = "person",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
//    private List<Account> accounts;
//    @Transient
//    @OneToMany(mappedBy = "person", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<Transaction> transactions;
    @Transient
    @AssertTrue(message = "Military status must not be null if gender is male and age>=18.")
    public boolean isMilitaryStatusValid() {
        return !("MALE".equalsIgnoreCase(gender.toString()) && age >= 18 && militaryServiceStatus == null);
    }
}
