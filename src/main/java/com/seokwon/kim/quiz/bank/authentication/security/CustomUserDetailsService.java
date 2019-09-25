package com.seokwon.kim.quiz.bank.authentication.security;

import com.seokwon.kim.quiz.bank.authentication.model.User;
import com.seokwon.kim.quiz.bank.authentication.repository.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override @Transactional
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        return UserPrincipal.create(userRepository.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("Username not found error. username: " + username)));
    }

    @Transactional
    public UserDetails loadUserById(final ObjectId id) {
        return UserPrincipal.create(userRepository.findById(id).orElseThrow(() ->
                new UsernameNotFoundException("Id not found error. id: " + id.toHexString())));
    }
}
