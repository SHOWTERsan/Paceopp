package ru.santurov.paceopp.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
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

    @Size(min = 2,max = 30,message = "Имя должно иметь от 2 до 30 символов")
    @Column(name = "username")
    private String username;

    @NotEmpty(message = "Почту нужно ввести обязательно")
    @Email(message = "Неверный формат почты")
    @Column(name = "email")
    private String email;

    @Size(min = 6,max = 255,message = "Пароль должен содержать от 8 символов до 255 символов.")
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
