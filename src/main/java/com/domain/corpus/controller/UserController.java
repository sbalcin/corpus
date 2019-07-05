package com.domain.corpus.controller;

import com.domain.corpus.model.user.User;
import com.domain.corpus.payload.UserIdentityAvailability;
import com.domain.corpus.security.CurrentUser;
import com.domain.corpus.security.UserPrincipal;
import com.domain.corpus.payload.UserSummary;
import com.domain.corpus.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public UserSummary getCurrentUser(@CurrentUser UserPrincipal currentUser){
        return userService.getCurrentUser(currentUser);
    }

    @GetMapping("/checkUsernameAvailability")
    public UserIdentityAvailability checkUsernameAvailability(@RequestParam(value = "username") String username){
        return userService.checkUsernameAvailability(username);
    }

    @GetMapping("/checkEmailAvailability")
    public UserIdentityAvailability checkEmailAvailability(@RequestParam(value = "email") String email){
        return userService.checkEmailAvailability(email);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addUser(@Valid @RequestBody User user){
        return userService.addUser(user);
    }

    @PutMapping("/{username}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> updateUser(@Valid @RequestBody User newUser, @PathVariable(value = "username") String username, @CurrentUser UserPrincipal currentUser){
        return userService.updateUser(newUser, username, currentUser);
    }

    @DeleteMapping("/{username}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable(value = "username") String username, @CurrentUser UserPrincipal currentUser){
        return userService.deleteUser(username, currentUser);
    }

    @PutMapping("/{username}/giveAdmin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> giveAdmin(@PathVariable(name = "username") String username){
        return userService.giveAdmin(username);
    }

    @PutMapping("/{username}/takeAdmin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> takeAdmin(@PathVariable(name = "username") String username){
        return userService.takeAdmin(username);
    }



}
