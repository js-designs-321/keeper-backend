package com.notekeeper.restapi.controller;

import com.notekeeper.restapi.payload.request.LoginRequest;
import com.notekeeper.restapi.payload.request.SignupRequest;
import com.notekeeper.restapi.payload.response.AuthenticationResponse;
import com.notekeeper.restapi.service.UserAuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@CrossOrigin
@RestController
@RequestMapping("/notes/v1/user")
public class UserController {

    private UserAuthenticationService userAuthenticationService;

    @Autowired
    public UserController(UserAuthenticationService userAuthenticationService) {
        this.userAuthenticationService = userAuthenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody SignupRequest request
    ){
        return ResponseEntity.ok(userAuthenticationService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @RequestBody LoginRequest request
    ){
        return ResponseEntity.ok(userAuthenticationService.login(request));
    }

    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        userAuthenticationService.refreshToken(request,response);
    }

}