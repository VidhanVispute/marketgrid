package com.marketgrid.auth.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "password") // avoid logging sensitive data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;  // stored as BCrypt hash

    @Column(nullable = false)
    private String role;  // possible values: ADMIN, VENDOR, CUSTOMER

    @Column(nullable = false, updatable = false)
private LocalDateTime createdAt;

@PrePersist
protected void onCreate() {
    this.createdAt = LocalDateTime.now();
}


    @Column(nullable = false)
    private boolean enabled = true;
}
