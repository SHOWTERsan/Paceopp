package ru.santurov.paceopp.models;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;


@Data
@Entity
@Table(name="users")
public class User {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "social_id")
    private Integer social_id;

    @Column(name = "social_name")
    private String social_name;

    @Column(name = "role")
    private String role;

    @Column(name = "uuid", unique=true)
    private String uuid;

    @Column(name = "is_verified")
    private boolean isVerified;

    @Column(name = "temp_email")
    private String tempEmail;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<VerificationToken> tokens;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Order> orders;
}
