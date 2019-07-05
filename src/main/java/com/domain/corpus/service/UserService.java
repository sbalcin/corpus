package com.domain.corpus.service;

import com.domain.corpus.exception.AppException;
import com.domain.corpus.exception.ResourceNotFoundException;
import com.domain.corpus.model.role.Role;
import com.domain.corpus.model.role.RoleName;
import com.domain.corpus.model.user.User;
import com.domain.corpus.payload.ApiResponse;
import com.domain.corpus.payload.UserIdentityAvailability;
import com.domain.corpus.payload.UserSummary;
import com.domain.corpus.repository.RoleRepository;
import com.domain.corpus.repository.UserRepository;
import com.domain.corpus.security.UserPrincipal;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;


    public UserSummary getCurrentUser(UserPrincipal currentUser){
        return new UserSummary(currentUser.getId(), currentUser.getUsername(), currentUser.getFirstName(), currentUser.getLastName());
    }

    public UserIdentityAvailability checkUsernameAvailability(String username){
        Boolean isAvailable = !userRepository.existsByUsername(username);
        return new UserIdentityAvailability(isAvailable);
    }

    public UserIdentityAvailability checkEmailAvailability(String email){
        Boolean isAvailable = !userRepository.existsByEmail(email);
        return new UserIdentityAvailability(isAvailable);
    }

    public ResponseEntity<?> addUser(User user){
        if(userRepository.existsByUsername(user.getUsername())){
            return new ResponseEntity<>(new ApiResponse(false, "Username is already taken"), HttpStatus.BAD_REQUEST);
        }

        if(userRepository.existsByEmail(user.getEmail())){
            return new ResponseEntity<>(new ApiResponse(false, "Email is already taken"), HttpStatus.BAD_REQUEST);
        }

        List<Role> roles = new ArrayList<>();
        roles.add(roleRepository.findByName(RoleName.ROLE_USER).orElseThrow(() -> new AppException("User role not set")));
        user.setRoles(roles);

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User result =  userRepository.save(user);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    public ResponseEntity<?> updateUser(User newUser, String username, UserPrincipal currentUser){
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        if(user.getId().equals(currentUser.getId()) || currentUser.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.toString()))){
            user.setFirstName(newUser.getFirstName());
            user.setLastName(newUser.getLastName());
            user.setPassword(passwordEncoder.encode(newUser.getPassword()));
            user.setEmail(newUser.getEmail());

            User updatedUser =  userRepository.save(user);
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);

        }

        return new ResponseEntity<>(new ApiResponse(false, "You don't have permission to update profile of: " + username), HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity<?> deleteUser(String username, UserPrincipal currentUser){
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User", "id", username));
        if(!user.getId().equals(currentUser.getId())){
            return new ResponseEntity<>(new ApiResponse(false, "You don't have permission to delete profile of: " + username), HttpStatus.UNAUTHORIZED);
        }
        userRepository.deleteById(user.getId());

        return new ResponseEntity<>(new ApiResponse(true, "You successfully deleted profile of: " + username), HttpStatus.OK);
    }

    public ResponseEntity<?> giveAdmin(String username){
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        List<Role> roles = new ArrayList<>();
        roles.add(roleRepository.findByName(RoleName.ROLE_ADMIN).orElseThrow(() -> new AppException("User role not set")));
        roles.add(roleRepository.findByName(RoleName.ROLE_USER).orElseThrow(() -> new AppException("User role not set")));
        user.setRoles(roles);
        userRepository.save(user);
        return new ResponseEntity<>(new ApiResponse(true, "You gave ADMIN role to user: " + username), HttpStatus.OK);
    }

    public ResponseEntity<?> takeAdmin(String username){
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        List<Role> roles = new ArrayList<>();
        roles.add(roleRepository.findByName(RoleName.ROLE_USER).orElseThrow(() -> new AppException("User role not set")));
        user.setRoles(roles);
        userRepository.save(user);
        return new ResponseEntity<>(new ApiResponse(true, "You took ADMIN role from user: " + username), HttpStatus.OK);
    }
}
