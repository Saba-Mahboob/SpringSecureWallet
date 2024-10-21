package com.example.wallet.Servises;

import com.example.wallet.Entities.Person;
import com.example.wallet.Repositoreis.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Optional;


@Service
public class UserDetailService implements UserDetailsService {
    @Autowired
    PersonRepository personRepository;
    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        Optional<Person> personOptional = personRepository.findById(id);
        Person person = personOptional.orElseThrow(() ->
                new UsernameNotFoundException("User not found with ID: " + id)
        );
        return new org.springframework.security.core.userdetails.User(
                person.getId(),
                person.getPassword(),
                new ArrayList<>()
        );
    }
}
