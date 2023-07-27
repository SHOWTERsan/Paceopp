package ru.santurov.paceopp.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name="\"user\"")
public class User {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Size(min = 2,max = 30,message = "Имя должно иметь от 2 до 30 символов")
    @Column(name = "username")
    private String username;

    @NotEmpty(message = "Почту нужно ввести обязательно")
    @Email(message = "Неверный формат почты")
    @Column(name = "email")
    private String email;

    @Size(min = 6,max = 255,message = "Пароль должен содержать от 6 символов.")
    @Column(name = "password")
    private String password;

    @Column(name = "social_id")
    private Integer social_id;

    @Column(name = "social_name")
    private String social_name;

    @Column(name = "role")
    private String role;


    public User() {
    }

    public User(int id,String username, String email, String password, int social_id, String social_name, String role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.social_id = social_id;
        this.social_name = social_name;
        this.role = role;
    }
}
