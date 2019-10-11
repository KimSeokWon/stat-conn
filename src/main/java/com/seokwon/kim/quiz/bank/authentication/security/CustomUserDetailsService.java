package com.seokwon.kim.quiz.bank.authentication.security;

import com.seokwon.kim.quiz.bank.authentication.repository.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Hashtable;
import java.util.Map;

@Component
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    private Map<String, UserDetails> userDetailsMap = new Hashtable<>();

    @Override
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        if ( userDetailsMap.containsKey(username) ) {
            return userDetailsMap.get(username);
        }
        UserDetails userDetails = UserPrincipal.create(userRepository.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException("Username not found error. username: " + username)));
        userDetailsMap.put(username, userDetails);
        return userDetails;
    }

    @Transactional
    public UserDetails loadUserById(final ObjectId id) {
        return UserPrincipal.create(userRepository.findById(id).orElseThrow(() ->
                new UsernameNotFoundException("Id not found error. id: " + id.toHexString())));
    }
}
