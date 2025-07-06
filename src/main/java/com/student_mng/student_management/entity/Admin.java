package com.student_mng.student_management.entity;

import com.student_mng.student_management.enums.Role;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;


@Entity
@DiscriminatorValue("ADMIN")
public class Admin extends User {

    private String department;

    public Admin(){}

    public Admin(String username, String password, String email, Role role, String department) {
        super(username, password, role, email);
        this.department = department;
    }

    public Admin(String department) {
        this.department = department;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }
}

