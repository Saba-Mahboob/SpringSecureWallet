package com.example.wallet.Servises;

import com.example.wallet.Entities.Person;
import com.example.wallet.Repositoreis.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonService {
    @Autowired
    PersonRepository personRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    public Person getPersonById(String id) {
        return personRepository.findById(id).orElse(null);
    }

    public Person createPerson(Person person) {
        String encryptedPassword = passwordEncoder.encode(person.getPassword());
        person.setPassword(encryptedPassword);

        return personRepository.save(person);
    }

    public Person updatePerson(String id, Person personDetails) {
        Person person = getPersonById(id);
        if (personDetails.getPassword() != null) {
            String encryptedPassword = passwordEncoder.encode(personDetails.getPassword());
            person.setPassword(encryptedPassword);
        }
        return personRepository.save(person);
    }

    public void deleteUser(String personId) {
        personRepository.deleteById(personId);
    }
}
