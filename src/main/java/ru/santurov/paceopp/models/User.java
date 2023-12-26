package ru.santurov.paceopp.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<VerificationToken> tokens;
}
