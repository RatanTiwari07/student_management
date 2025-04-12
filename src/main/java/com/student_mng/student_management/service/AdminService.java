package com.student_mng.student_management.service;

import com.student_mng.student_management.dto.AdminDTO;
import com.student_mng.student_management.dto.ClubHeadDTO;
import com.student_mng.student_management.dto.StudentDTO;
import com.student_mng.student_management.dto.TeacherDTO;
import com.student_mng.student_management.entity.*;
import com.student_mng.student_management.enums.Role;
import com.student_mng.student_management.exception.ResourceNotFoundException;
import com.student_mng.student_management.exception.ValidationException;
import com.student_mng.student_management.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

@Service
@Validated
@Slf4j
public class AdminService {

    @Autowired
    private SubjectRepository subjectRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TeacherRepository teacherRepository;
    @Autowired
    private TeacherAssignmentRepository teacherAssignmentRepository;
    @Autowired
    private ClassEntityRepository classRepository;
    @Autowired
    private ClubHeadRepository clubHeadRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private StudentRepository studentRepository;

    //  Register Subject (Direct Save)
    public Subject registerSubject(Subject subject) {
        return subjectRepository.save(subject);
    }

    //  Register Admin (Encrypt Password)
    public User registerAdmin(AdminDTO adminDTO) {
        User admin = new User();
        admin.setUsername(adminDTO.username());
        admin.setEmail(adminDTO.email());
        admin.setPassword(passwordEncoder.encode(adminDTO.password()));
        admin.setRole(Role.ADMIN);
        return userRepository.save(admin);
    }

    //  Register Teacher (Encrypt Password)
    public Teacher registerTeacher(TeacherDTO teacherDTO) {
        Teacher teacher = new Teacher();
        teacher.setUsername(teacherDTO.username());
        teacher.setEmail(teacherDTO.email());
        teacher.setPassword(passwordEncoder.encode(teacherDTO.password()));
        teacher.setRole(Role.TEACHER);
        return teacherRepository.save(teacher);
    }

    //  Assign Teacher (Direct Save)
    public TeacherAssignment assignTeacher(TeacherAssignment teacherAssignment) {
        return teacherAssignmentRepository.save(teacherAssignment);
    }

    //  Register Class (Direct Save)
    public ClassEntity registerClass(ClassEntity classEntity) {
        return classRepository.save(classEntity);
    }

    //  Register ClubHead (Encrypt Password)
    public ClubHead registerClubHead(ClubHeadDTO clubHeadDTO) {
        ClubHead clubHead = new ClubHead();
        clubHead.setUsername(clubHeadDTO.username());
        clubHead.setEmail(clubHeadDTO.email());
        clubHead.setPassword(passwordEncoder.encode(clubHeadDTO.password()));
        clubHead.setRole(Role.CLUBHEAD);
        return clubHeadRepository.save(clubHead);
    }

    public Student registerStudent(StudentDTO studentDTO) {
        // Validate roll number
        if (studentDTO.rollNumber() == null || studentDTO.rollNumber().trim().isEmpty()) {
            throw new ValidationException("Roll number cannot be empty");
        }
        
        // Validate class existence
        ClassEntity studentClass = classRepository.findById(studentDTO.classId())
                .orElseThrow(() -> new ResourceNotFoundException("Class not found with id: " + studentDTO.classId()));
        
        // Check for duplicate email
        if (userRepository.existsByEmail(studentDTO.email())) {
            throw new ValidationException("Email already exists");
        }
        
        Student student = new Student();
        student.setUsername(studentDTO.username());
        student.setEmail(studentDTO.email());
        student.setPassword(passwordEncoder.encode(studentDTO.password()));
        student.setRole(Role.STUDENT);
        student.setRollNumber(studentDTO.rollNumber());
        student.setStudentClass(studentClass);
        return studentRepository.save(student);
    }

    public List<Student> assignStudentsToClass(List<String> studentIds, String classId) {
        ClassEntity studentClass = classRepository.findById(classId)
                .orElseThrow(() -> new RuntimeException("Class not found"));

        List<Student> students = studentRepository.findAllById(studentIds);

        for (Student student : students) {
            student.setStudentClass(studentClass);
        }

        return studentRepository.saveAll(students);
    }

    public List<Teacher> getAllTeachers() {
        return teacherRepository.findAll();
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public List<ClassEntity> getAllClasses() {
        return classRepository.findAll();
    }

    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    public List<ClubHead> getAllClubHeads() {
        return clubHeadRepository.findAll();
    }

    public Optional<Teacher> getTeacherById(String id) {
        return teacherRepository.findById(id);
    }

    public Optional<Student> getStudentById(String id) {
        return studentRepository.findById(id);
    }

    public Optional<ClassEntity> getClassById(String id) {
        return classRepository.findById(id);
    }

    public List<Student> getUnassignedStudents() {
        return studentRepository.findByStudentClassIsNull();
    }

    private void validateStudentData(StudentDTO studentDTO) {
        if (studentDTO.rollNumber() == null || studentDTO.rollNumber().trim().isEmpty()) {
            throw new ValidationException("Roll number cannot be empty");
        }
        // Add more validation rules
    }
}
