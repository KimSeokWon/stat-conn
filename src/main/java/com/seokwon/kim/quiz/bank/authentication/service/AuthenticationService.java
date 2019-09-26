package com.seokwon.kim.quiz.bank.authentication.service;

import com.seokwon.kim.quiz.bank.authentication.model.Role;
import com.seokwon.kim.quiz.bank.authentication.model.SignInRequest;
import com.seokwon.kim.quiz.bank.authentication.model.SignUpRequest;
import com.seokwon.kim.quiz.bank.authentication.model.User;
import com.seokwon.kim.quiz.bank.authentication.repository.RoleRepository;
import com.seokwon.kim.quiz.bank.authentication.repository.UserRepository;
import com.seokwon.kim.quiz.bank.authentication.security.JwtTokenProvider;
import com.seokwon.kim.quiz.bank.exception.BadRequestException;
import com.seokwon.kim.quiz.bank.exception.DeviceStatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Collections;

@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider  jwtTokenProvider;
    private final UserRepository    userRepository;
    private final RoleRepository    roleRepository;

    @Autowired private PasswordEncoder  passwordEncoder;

    @PostConstruct
    public void initialize() {
        if ( roleRepository.count() == 0 ) {
            roleRepository.save(new Role(Role.RoleName.ROLE_USER));
            roleRepository.save(new Role(Role.RoleName.ROLE_ADMIN));
        }
    }

    @Autowired
    public AuthenticationService(final AuthenticationManager authenticationManager,
                                 final JwtTokenProvider  jwtTokenProvider,
                                 final RoleRepository    roleRepository,
                                 final UserRepository    userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }
    public String authenticate(SignInRequest user) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken( user.getUsername(), user.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtTokenProvider.generateToken(authentication);
    }

    public String refresh() {

        UsernamePasswordAuthenticationToken authentication =
                (UsernamePasswordAuthenticationToken)SecurityContextHolder.getContext().getAuthentication();
        return jwtTokenProvider.generateToken(authentication);
    }

    public User registerUser(final SignUpRequest user) {
        if ( userRepository.findByUsername(user.getUsername()).isPresent() ) {
            throw new BadRequestException("Username is exist already.");
        }

        User newUser = new User(
                user.getUsername(),
                passwordEncoder.encode(user.getPassword())
        );
        Role userRole = roleRepository.findByRoleName(Role.RoleName.ROLE_USER)
                .orElseThrow(() -> new DeviceStatException(DeviceStatException.AUTENTICATION_ERR, "User role is not set."));
        newUser.setRoles(Collections.singleton(userRole));

        return userRepository.save(newUser);
    }
}
