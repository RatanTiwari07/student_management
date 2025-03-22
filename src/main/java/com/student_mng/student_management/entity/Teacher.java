package com.student_mng.student_management.entity;

import com.student_mng.student_management.enums.Role;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;

import java.util.List;

@Entity
@DiscriminatorValue("TEACHER")
public class Teacher extends User {

    @OneToMany(mappedBy = "teacher", cascade = CascadeType.ALL)
    private List<TeacherAssignment> assignments;

    public Teacher () {}

    public Teacher(String username, String password, Role role, List<TeacherAssignment> assignments) {
        super(username, password, role);
        this.assignments = assignments;
    }

    public Teacher(List<TeacherAssignment> assignments) {
        this.assignments = assignments;
    }

    public List<TeacherAssignment> getAssignments() {
        return assignments;
    }

    public void setAssignments(List<TeacherAssignment> assignments) {
        this.assignments = assignments;
    }
}

