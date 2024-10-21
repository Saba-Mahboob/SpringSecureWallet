package com.example.wallet.ControllersTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import com.example.wallet.Controllers.PersonController;
import com.example.wallet.Entities.Person;
import com.example.wallet.Servises.JwtService;
import com.example.wallet.Servises.PersonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class PersonControllerTest {

    @InjectMocks
    private PersonController personController;

    @Mock
    private PersonService personService;

    @Mock
    private JwtService jwtService;

    private String token = "Bearer some.jwt.token";
    private String personId = "0987654321";
    private Person person;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        person = new Person();
        person.setId(personId);
        person.setName("Saba");
        person.setLastName("Mahboob");
        // Set other fields as needed
    }

    @Test
    public void testGetPersonById_Success() {
        when(jwtService.extractPersonId(anyString())).thenReturn(personId);
        when(jwtService.isTokenExpired(anyString())).thenReturn(false);
        when(personService.getPersonById(personId)).thenReturn(person);

        ResponseEntity<Person> response = personController.getPersonById(personId, token);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(person, response.getBody());
    }

    @Test
    public void testGetPersonById_PersonNotFound() {
        when(jwtService.extractPersonId(anyString())).thenReturn(personId);
        when(jwtService.isTokenExpired(anyString())).thenReturn(false);
        when(personService.getPersonById(personId)).thenReturn(null);

        ResponseEntity<Person> response = personController.getPersonById(personId, token);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testCreatePerson_Success() {
        when(personService.createPerson(any())).thenReturn(person);

        ResponseEntity<String> response = personController.createPerson(person);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Person created successfully", response.getBody());
    }

    @Test
    public void testUpdatePerson_Success() {
        when(jwtService.extractPersonId(anyString())).thenReturn(personId);
        when(jwtService.isTokenExpired(anyString())).thenReturn(false);
        when(personService.getPersonById(personId)).thenReturn(person);
        when(personService.updatePerson(anyString(), any())).thenReturn(person);

        ResponseEntity<Person> response = personController.updatePerson(personId, person, token);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(person, response.getBody());
    }

    @Test
    public void testUpdatePerson_NotFound() {
        when(jwtService.extractPersonId(anyString())).thenReturn(personId);
        when(jwtService.isTokenExpired(anyString())).thenReturn(false);
        when(personService.getPersonById(personId)).thenReturn(null);

        ResponseEntity<Person> response = personController.updatePerson(personId, person, token);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testDeletePerson_Success() {
        when(jwtService.extractPersonId(anyString())).thenReturn(personId);
        when(personService.getPersonById(personId)).thenReturn(person);

        ResponseEntity<Void> response = personController.deletePerson(personId, token);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(personService).deleteUser(personId);
    }

    @Test
    public void testDeletePerson_NotFound() {
        when(jwtService.extractPersonId(anyString())).thenReturn(personId);
        when(personService.getPersonById(personId)).thenReturn(null);

        ResponseEntity<Void> response = personController.deletePerson(personId, token);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
