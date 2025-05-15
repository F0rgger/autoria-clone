package com.autoria.clone.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "dealerships")
@Data
public class Dealership {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String address;

    @ManyToMany
    @JoinTable(
            name = "dealership_users",
            joinColumns = @JoinColumn(name = "dealership_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> users = new ArrayList<>(); // Добавлена инициализация

    @ElementCollection
    @CollectionTable(name = "dealership_roles", joinColumns = @JoinColumn(name = "dealership_id"))
    @MapKeyColumn(name = "user_id")
    @Column(name = "role")
    private Map<Long, String> userRoles = new HashMap<>();
}