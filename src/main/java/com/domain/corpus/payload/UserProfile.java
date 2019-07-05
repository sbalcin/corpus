package com.domain.corpus.payload;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class UserProfile {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private Instant joinedAt;
    private String email;
    private String phone;

}
