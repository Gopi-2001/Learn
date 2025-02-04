package com.main.OnboardingService.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    String name;

    @Column(name = "email", unique = true)
    String email;

    @Column(name = "phone", unique = true)
    String phone;

    String password;

    @Enumerated(EnumType.STRING)
    UserIdentifier userIdentifier;

    @Column(name = "userIdentifierValue", unique = true)
    String userIdentifierValue;

    String dob;

    @Enumerated(EnumType.STRING)
    UserRole role;

    @Enumerated(EnumType.STRING)
    UserStatus  userStatus;

    String address;

    @CreationTimestamp
    Date createdOn;

    @UpdateTimestamp
    Date updatedOn;




}
