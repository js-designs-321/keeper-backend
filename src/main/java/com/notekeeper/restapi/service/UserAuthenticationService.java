package com.notekeeper.restapi.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.notekeeper.restapi.enums.Role;
import com.notekeeper.restapi.model.User;
import com.notekeeper.restapi.payload.request.LoginRequest;
import com.notekeeper.restapi.payload.request.SignupRequest;
import com.notekeeper.restapi.payload.response.AuthenticationResponse;
import com.notekeeper.restapi.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

@Service
public class UserAuthenticationService {

    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;

    private JwtService jwtService;

    private AuthenticationManager authenticationManager;

    @Autowired
    public UserAuthenticationService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(UserAuthenticationService.class);

    public AuthenticationResponse register(SignupRequest request) {
        if(userRepository.existsByUsername(request.getUsername())){
            return new AuthenticationResponse(null,null,"Error: Username is already taken!");
        }
        var user = new User(request.getUsername(),request.getEmail(), passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        var savedUser = userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(savedUser, jwtToken);
        return new AuthenticationResponse(jwtToken,refreshToken,"Successfully Registered");
    }

    public AuthenticationResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(user, jwtToken);
        return new AuthenticationResponse(jwtToken,refreshToken,"Successfully logged in");
    }

    private void saveUserToken(User user, String jwtToken) {
        user.setToken(jwtToken);
        user.setExpired(false);
        user.setRevoked(false);
        userRepository.save(user);
    }


    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    )  throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String username;
        if (authHeader == null ||!authHeader.startsWith("Bearer ")) {
            return;
        }
        refreshToken = authHeader.substring(7);
        username = jwtService.extractUsername(refreshToken);
        if (username != null) {
            var user = this.userRepository.findByUsername(username)
                    .orElseThrow();
            if (jwtService.isTokenValid(refreshToken, user) && !user.isRevoked()) {
                var accessToken = jwtService.generateToken(user);
                saveUserToken(user, accessToken);
                var authResponse = new AuthenticationResponse(accessToken,refreshToken,"Token successfully regenerated");
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }else {
                var authResponse = new AuthenticationResponse(null,null,"Error! Invalid Request");
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }

    public User getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User Not Found"));
        return user;
    }

}