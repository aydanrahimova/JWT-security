package com.example.securityjwt.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "token")
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String accessToken;
    String refreshToken;
    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;
    Boolean isLoggedOut;

    @PostPersist
    public void setIsLoggedOut() {
        if (this.isLoggedOut == null) {
            this.isLoggedOut = Boolean.FALSE;
        }
    }
}
