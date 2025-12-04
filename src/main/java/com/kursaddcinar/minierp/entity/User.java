package com.kursaddcinar.minierp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "username", length = 50, nullable = false, unique = true)
    private String username;

    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "first_name", length = 50)
    private String firstName;

    @Column(name = "last_name", length = 50)
    private String lastName;

    @Column(name = "is_active")
    private boolean isActive = true;

    @Column(name = "last_login_date")
    private LocalDateTime lastLoginDate;

    // Many-to-Many ilişkiyi UserRole entity'si üzerinden değil,
    // direkt @ManyToMany ile yapmak Spring Security'de daha pratiktir.
    // Ancak senin mevcut yapın "UserRole" tablosu (entity) kullanıyor.
    // O yüzden senin yapına sadık kalarak @OneToMany kullanacağız.
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<UserRole> userRoles = new ArrayList<>();
}