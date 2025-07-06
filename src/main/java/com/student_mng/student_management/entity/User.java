package com.student_mng.student_management.entity;

import com.student_mng.student_management.enums.Role;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.Objects;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) // Single table for all users
@DiscriminatorColumn(name = "role", discriminatorType = DiscriminatorType.STRING)
public class User {

    @Id
    @UuidGenerator
    @Column(updatable = false, nullable = false, unique = true)
    private String id;

    @Column(unique = true, nullable = false)
    private String username;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String email;

    public User(String username, String password, Role role, String email) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, password, role);
    }

    public User () {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() { return this.email; }
}




/*

event details

 */


