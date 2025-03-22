package com.student_mng.student_management.service;

import com.student_mng.student_management.dto.AdminDTO;
import com.student_mng.student_management.dto.ClubHeadDTO;
import com.student_mng.student_management.dto.StudentDTO;
import com.student_mng.student_management.dto.TeacherDTO;
import com.student_mng.student_management.entity.*;
import com.student_mng.student_management.enums.Role;
import com.student_mng.student_management.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
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
        ClassEntity studentClass = classRepository.findById(studentDTO.classId())
                .orElseThrow(() -> new RuntimeException("Class not found"));

        Student student = new Student();
        student.setUsername(studentDTO.username());
        student.setEmail(studentDTO.email());
        student.setPassword(passwordEncoder.encode(studentDTO.password()));
        student.setRole(Role.STUDENT);
        student.setRollNumber(studentDTO.rollNumber());
        student.setStudentClass(studentClass); // Assigning class
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

}
