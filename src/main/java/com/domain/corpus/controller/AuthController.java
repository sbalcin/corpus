package com.domain.corpus.controller;

import com.domain.corpus.exception.AppException;
import com.domain.corpus.model.role.Role;
import com.domain.corpus.model.role.RoleName;
import com.domain.corpus.model.user.User;
import com.domain.corpus.repository.RoleRepository;
import com.domain.corpus.repository.UserRepository;
import com.domain.corpus.security.JwtTokenProvider;
import com.domain.corpus.payload.ApiResponse;
import com.domain.corpus.payload.JwtAuthenticationResponse;
import com.domain.corpus.payload.LoginRequest;
import com.domain.corpus.payload.SignUpRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest){
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = jwtTokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest){
        if(userRepository.existsByUsername(signUpRequest.getUsername())){
            return new ResponseEntity<>(new ApiResponse(false, "Username is already taken"), HttpStatus.BAD_REQUEST);
        }

        if(userRepository.existsByEmail(signUpRequest.getEmail())){
            return new ResponseEntity<>(new ApiResponse(false, "Email is already taken"), HttpStatus.BAD_REQUEST);
        }
        String firstName = signUpRequest.getFirstName().substring(0, 1).toUpperCase() + signUpRequest.getFirstName().substring(1).toLowerCase();

        String lastName = signUpRequest.getLastName().substring(0, 1).toUpperCase() + signUpRequest.getLastName().substring(1).toLowerCase();

        String username = signUpRequest.getUsername().toLowerCase();

        String email = signUpRequest.getEmail().toLowerCase();

        User user = new User(firstName, lastName, username, signUpRequest.getPassword(), email);

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        List<Role> roles = new ArrayList<>();
        if(userRepository.count() == 0){
            roles.add(roleRepository.findByName(RoleName.ROLE_USER).orElseThrow(() -> new AppException("User role not set")));
            roles.add(roleRepository.findByName(RoleName.ROLE_ADMIN).orElseThrow(() -> new AppException("User role not set")));
        } else{
            roles.add(roleRepository.findByName(RoleName.ROLE_USER).orElseThrow(() -> new AppException("User role not set")));
        }

        user.setRoles(roles);

        User result = userRepository.save(user);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/api/v1/users/{userId}")
                .buildAndExpand(result.getId()).toUri();

        return ResponseEntity.created(location).body(new ApiResponse(true, "User registered successfully"));
    }
}
