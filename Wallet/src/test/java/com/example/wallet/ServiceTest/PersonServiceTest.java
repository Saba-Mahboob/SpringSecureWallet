package com.example.wallet.ServiceTest;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.example.wallet.Entities.Person;
import com.example.wallet.Repositoreis.PersonRepository;
import com.example.wallet.Servises.PersonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

public class PersonServiceTest {

    @InjectMocks
    private PersonService personService;

    @Mock
    private PersonRepository personRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private Person person;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        person = new Person();
        person.setId("1");
        person.setPassword("plainPassword");
    }

    @Test
    public void testGetPersonById() {
        when(personRepository.findById("1")).thenReturn(Optional.of(person));

        Person foundPerson = personService.getPersonById("1");

        assertNotNull(foundPerson);
        assertEquals(person.getId(), foundPerson.getId());
    }

    @Test
    public void testCreatePerson() {
        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");
        when(personRepository.save(any(Person.class))).thenReturn(person);

        Person createdPerson = personService.createPerson(person);

        assertNotNull(createdPerson);
        assertEquals("encodedPassword", createdPerson.getPassword());
        verify(personRepository, times(1)).save(person);
    }

    @Test
    public void testUpdatePerson() {
        when(personRepository.findById("1")).thenReturn(Optional.of(person));
        when(passwordEncoder.encode("newPassword")).thenReturn("newEncodedPassword");

        person.setPassword("newPassword");
        Person updatedPerson = personService.updatePerson("1", person);

        assertNotNull(updatedPerson);
        assertEquals("newEncodedPassword", updatedPerson.getPassword());
        verify(personRepository, times(1)).save(person);
    }

    @Test
    public void testDeleteUser() {
        personService.deleteUser("1");
        verify(personRepository, times(1)).deleteById("1");
    }
}

