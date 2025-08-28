package com.student_mng.student_management.service;

import com.student_mng.student_management.dto.*;
import com.student_mng.student_management.entity.*;
import com.student_mng.student_management.enums.Role;
import com.student_mng.student_management.exception.*;
import com.student_mng.student_management.repository.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

@Service
@Validated
@Slf4j
@Transactional
@AllArgsConstructor
public class AdminService {

    private SubjectRepository subjectRepository;
    private UserRepository userRepository;
    private AdminRepository adminRepository;
    private TeacherRepository teacherRepository;
    private TeacherAssignmentRepository teacherAssignmentRepository;
    private ClassEntityRepository classRepository;
    private ClubHeadRepository clubHeadRepository;
    private PasswordEncoder passwordEncoder;
    private StudentRepository studentRepository;

    //  Register Subject (Direct Save)
    @Transactional
    public Subject registerSubject(Subject subject) {
        try {
            validateSubjectInput(subject);
            log.info("Registering new subject: {}", subject.getSubjectName());
            return subjectRepository.save(subject);
        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation while registering subject: {}", subject.getSubjectName(), e);
            throw new DuplicateResourceException("Subject with this name already exists", e);
        } catch (DataAccessException e) {
            log.error("Database error while registering subject: {}", subject.getSubjectName(), e);
            throw new DatabaseOperationException("Failed to register subject", e);
        }
    }

    //  Register Admin (Encrypt Password)
    @Transactional
    public Admin registerAdmin(AdminDTO adminDTO) {
        try {
            validateAdminInput(adminDTO);

            if (userRepository.existsByEmail(adminDTO.email())) {
                throw new DuplicateResourceException("Email already exists");
            }

            Admin admin = new Admin();
            admin.setUsername(adminDTO.username());
            admin.setEmail(adminDTO.email());
            admin.setDepartment(adminDTO.dept());
            admin.setPassword(passwordEncoder.encode(adminDTO.password()));
            admin.setRole(Role.ADMIN);

            log.info("Registering new admin: {}", adminDTO.username());
            return adminRepository.save(admin);
        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation while registering admin: {}", adminDTO.username(), e);
            throw new DuplicateResourceException("Admin with this username or email already exists", e);
        } catch (DataAccessException e) {
            log.error("Database error while registering admin: {}", adminDTO.username(), e);
            throw new DatabaseOperationException("Failed to register admin", e);
        }
    }

    //  Register Teacher (Encrypt Password)
    @Transactional
    public Teacher registerTeacher(TeacherDTO teacherDTO) {
        try {
            validateTeacherInput(teacherDTO);

            if (userRepository.existsByEmail(teacherDTO.email())) {
                throw new DuplicateResourceException("Email already exists");
            }

            Teacher teacher = new Teacher();
            teacher.setUsername(teacherDTO.username());
            teacher.setEmail(teacherDTO.email());
            teacher.setPassword(passwordEncoder.encode(teacherDTO.password()));
            teacher.setRole(Role.TEACHER);

            log.info("Registering new teacher: {}", teacherDTO.username());
            return teacherRepository.save(teacher);
        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation while registering teacher: {}", teacherDTO.username(), e);
            throw new DuplicateResourceException("Teacher with this username or email already exists", e);
        } catch (DataAccessException e) {
            log.error("Database error while registering teacher: {}", teacherDTO.username(), e);
            throw new DatabaseOperationException("Failed to register teacher", e);
        }
    }

    //  Assign Teacher (Direct Save)
    @Transactional
    public TeacherAssignment assignTeacher(TeacherAssignment teacherAssignment) {
        try {
            validateTeacherAssignment(teacherAssignment);
            log.info("Assigning teacher {} to class {} for subject {}",
                teacherAssignment.getTeacher().getUsername(),
                teacherAssignment.getAssignedClass().getClassName(),
                teacherAssignment.getSubject().getSubjectName());
            return teacherAssignmentRepository.save(teacherAssignment);
        } catch (DataAccessException e) {
            log.error("Database error while assigning teacher", e);
            throw new DatabaseOperationException("Failed to assign teacher", e);
        }
    }

    //  Register Class (Direct Save)
    @Transactional
    public ClassEntity registerClass(ClassDTO classDTO) {
        try {
            validateClassInput(classDTO);

            ClassEntity classEntity = new ClassEntity();
            classEntity.setClassName(classDTO.className());

            if (classDTO.subjectIds() != null && !classDTO.subjectIds().isEmpty()) {
                List<Subject> subjects = subjectRepository.findAllById(classDTO.subjectIds());
                if (subjects.size() != classDTO.subjectIds().size()) {
                    throw new ResourceNotFoundException("One or more subjects not found");
                }
                classEntity.setSubjects(subjects);
            }

            log.info("Registering new class: {}", classDTO.className());
            return classRepository.save(classEntity);
        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation while registering class: {}", classDTO.className(), e);
            throw new DuplicateResourceException("Class with this name already exists", e);
        } catch (DataAccessException e) {
            log.error("Database error while registering class: {}", classDTO.className(), e);
            throw new DatabaseOperationException("Failed to register class", e);
        }
    }

    //  Register ClubHead (Encrypt Password)
    @Transactional
    public ClubHead registerClubHead(ClubHeadDTO clubHeadDTO) {
        try {
            validateClubHeadInput(clubHeadDTO);

            if (userRepository.existsByEmail(clubHeadDTO.email())) {
                throw new DuplicateResourceException("Email already exists");
            }

            ClubHead clubHead = new ClubHead();
            clubHead.setUsername(clubHeadDTO.username());
            clubHead.setEmail(clubHeadDTO.email());
            clubHead.setPassword(passwordEncoder.encode(clubHeadDTO.password()));
            clubHead.setRole(Role.CLUBHEAD);

            log.info("Registering new club head: {}", clubHeadDTO.username());
            return clubHeadRepository.save(clubHead);
        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation while registering club head: {}", clubHeadDTO.username(), e);
            throw new DuplicateResourceException("Club head with this username or email already exists", e);
        } catch (DataAccessException e) {
            log.error("Database error while registering club head: {}", clubHeadDTO.username(), e);
            throw new DatabaseOperationException("Failed to register club head", e);
        }
    }

    // New method to assign a student as club head
    @Transactional
    public ClubHead assignStudentAsClubHead(AssignClubHeadDTO assignDTO) {
        try {
            validateAssignClubHeadInput(assignDTO);

            // Find the student
            Student student = studentRepository.findById(assignDTO.studentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + assignDTO.studentId()));

            // Check if student is already a club head
            if (clubHeadRepository.existsByStudent(student)) {
                throw new BusinessLogicException("Student is already assigned as a club head");
            }

            // Create club head entity
            ClubHead clubHead = new ClubHead();
            clubHead.setUsername(student.getUsername() + "_clubhead");
            clubHead.setEmail(student.getEmail());
            clubHead.setPassword(student.getPassword()); // Use same password as student
            clubHead.setRole(Role.CLUBHEAD);
            clubHead.setClubName(assignDTO.clubName());
            clubHead.setStudent(student);

            log.info("Assigning student {} as club head for club: {}", student.getUsername(), assignDTO.clubName());
            return clubHeadRepository.save(clubHead);
        } catch (DataAccessException e) {
            log.error("Database error while assigning student as club head: {}", assignDTO.studentId(), e);
            throw new DatabaseOperationException("Failed to assign student as club head", e);
        }
    }

    // Method to remove club head status from a student
    @Transactional
    public void removeClubHeadStatus(String studentId) {
        try {
            validateStudentId(studentId);

            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

            ClubHead clubHead = clubHeadRepository.findByStudent(student)
                    .orElseThrow(() -> new ResourceNotFoundException("Student is not assigned as a club head"));

            log.info("Removing club head status from student: {}", student.getUsername());
            clubHeadRepository.delete(clubHead);
        } catch (DataAccessException e) {
            log.error("Database error while removing club head status: {}", studentId, e);
            throw new DatabaseOperationException("Failed to remove club head status", e);
        }
    }

    // Method to update club name for a student club head
    @Transactional
    public ClubHead updateStudentClubHeadInfo(String studentId, String newClubName) {
        try {
            validateStudentId(studentId);
            validateClubName(newClubName);

            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

            ClubHead clubHead = clubHeadRepository.findByStudent(student)
                    .orElseThrow(() -> new ResourceNotFoundException("Student is not assigned as a club head"));

            clubHead.setClubName(newClubName);
            log.info("Updating club name to {} for student: {}", newClubName, student.getUsername());
            return clubHeadRepository.save(clubHead);
        } catch (DataAccessException e) {
            log.error("Database error while updating club head info: {}", studentId, e);
            throw new DatabaseOperationException("Failed to update club head information", e);
        }
    }

    // Method to get all students who are club heads
    @Transactional(readOnly = true)
    public List<Student> getStudentsWhoAreClubHeads() {
        try {
            return clubHeadRepository.findAll().stream()
                    .map(ClubHead::getStudent)
                    .toList();
        } catch (DataAccessException e) {
            log.error("Database error while fetching students who are club heads", e);
            throw new DatabaseOperationException("Failed to fetch club head students", e);
        }
    }

    // Method to check if a student is a club head
    @Transactional(readOnly = true)
    public boolean isStudentClubHead(String studentId) {
        try {
            validateStudentId(studentId);
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
            return clubHeadRepository.existsByStudent(student);
        } catch (DataAccessException e) {
            log.error("Database error while checking if student is club head: {}", studentId, e);
            throw new DatabaseOperationException("Failed to check club head status", e);
        }
    }

    @Transactional
    public Student registerStudent(StudentDTO studentDTO) {
        try {
            validateStudentInput(studentDTO);

            // Check for duplicate email
            if (userRepository.existsByEmail(studentDTO.email())) {
                throw new DuplicateResourceException("Email already exists");
            }

            // Check for duplicate roll number
            if (studentRepository.existsByRollNumber(studentDTO.rollNumber())) {
                throw new DuplicateResourceException("Roll number already exists");
            }

            Student student = new Student();
            student.setUsername(studentDTO.username());
            student.setEmail(studentDTO.email());
            student.setPassword(passwordEncoder.encode(studentDTO.password()));
            student.setRole(Role.STUDENT);
            student.setRollNumber(studentDTO.rollNumber());
            student.setFirstName(studentDTO.firstName());
            student.setLastName(studentDTO.lastName());
            student.setContactNumber(studentDTO.contactNumber());
            student.setParentContactNumber(studentDTO.parentContactNumber());
            student.setParentEmail(studentDTO.parentEmail());
            student.setBatch(studentDTO.batch());

            log.info("Registering new student: {}", studentDTO.username());
            return studentRepository.save(student);
        } catch (DataIntegrityViolationException e) {
            log.error("Data integrity violation while registering student: {}", studentDTO.username(), e);
            throw new DuplicateResourceException("Student with this username, email, or roll number already exists", e);
        } catch (DataAccessException e) {
            log.error("Database error while registering student: {}", studentDTO.username(), e);
            throw new DatabaseOperationException("Failed to register student", e);
        }
    }

    @Transactional
    public List<Student> assignStudentsToClass(List<String> studentIds, String classId) {
        try {
            validateStudentIds(studentIds);
            validateClassId(classId);

            ClassEntity studentClass = classRepository.findById(classId)
                    .orElseThrow(() -> new ResourceNotFoundException("Class not found"));

            List<Student> students = studentRepository.findAllById(studentIds);
            if (students.size() != studentIds.size()) {
                throw new ResourceNotFoundException("One or more students not found");
            }

            for (Student student : students) {
                student.setStudentClass(studentClass);
            }

            log.info("Assigning {} students to class: {}", students.size(), studentClass.getClassName());
            return studentRepository.saveAll(students);
        } catch (DataAccessException e) {
            log.error("Database error while assigning students to class: {}", classId, e);
            throw new DatabaseOperationException("Failed to assign students to class", e);
        }
    }

    // Read-only methods with exception handling
    @Transactional(readOnly = true)
    public List<Teacher> getAllTeachers() {
        try {
            return teacherRepository.findAll();
        } catch (DataAccessException e) {
            log.error("Database error while fetching all teachers", e);
            throw new DatabaseOperationException("Failed to fetch teachers", e);
        }
    }

    @Transactional(readOnly = true)
    public List<Student> getAllStudents() {
        try {
            return studentRepository.findAll();
        } catch (DataAccessException e) {
            log.error("Database error while fetching all students", e);
            throw new DatabaseOperationException("Failed to fetch students", e);
        }
    }

    @Transactional(readOnly = true)
    public List<ClassEntity> getAllClasses() {
        try {
            return classRepository.findAll();
        } catch (DataAccessException e) {
            log.error("Database error while fetching all classes", e);
            throw new DatabaseOperationException("Failed to fetch classes", e);
        }
    }

    @Transactional(readOnly = true)
    public List<Subject> getAllSubjects() {
        try {
            return subjectRepository.findAll();
        } catch (DataAccessException e) {
            log.error("Database error while fetching all subjects", e);
            throw new DatabaseOperationException("Failed to fetch subjects", e);
        }
    }

    @Transactional(readOnly = true)
    public List<ClubHead> getAllClubHeads() {
        try {
            return clubHeadRepository.findAll();
        } catch (DataAccessException e) {
            log.error("Database error while fetching all club heads", e);
            throw new DatabaseOperationException("Failed to fetch club heads", e);
        }
    }

    @Transactional(readOnly = true)
    public Optional<Teacher> getTeacherById(String id) {
        try {
            validateId(id);
            return teacherRepository.findById(id);
        } catch (DataAccessException e) {
            log.error("Database error while fetching teacher by id: {}", id, e);
            throw new DatabaseOperationException("Failed to fetch teacher", e);
        }
    }

    @Transactional(readOnly = true)
    public Optional<Student> getStudentById(String id) {
        try {
            validateId(id);
            return studentRepository.findById(id);
        } catch (DataAccessException e) {
            log.error("Database error while fetching student by id: {}", id, e);
            throw new DatabaseOperationException("Failed to fetch student", e);
        }
    }

    @Transactional(readOnly = true)
    public Optional<ClassEntity> getClassById(String id) {
        try {
            validateId(id);
            return classRepository.findById(id);
        } catch (DataAccessException e) {
            log.error("Database error while fetching class by id: {}", id, e);
            throw new DatabaseOperationException("Failed to fetch class", e);
        }
    }

    @Transactional(readOnly = true)
    public List<Student> getUnassignedStudents() {
        try {
            return studentRepository.findByStudentClassIsNull();
        } catch (DataAccessException e) {
            log.error("Database error while fetching unassigned students", e);
            throw new DatabaseOperationException("Failed to fetch unassigned students", e);
        }
    }

    // Validation methods
    private void validateSubjectInput(Subject subject) {
        if (subject == null) {
            throw new ValidationException("Subject cannot be null");
        }
        if (subject.getSubjectName() == null || subject.getSubjectName().trim().isEmpty()) {
            throw new ValidationException("Subject name cannot be empty");
        }
    }

    private void validateAdminInput(AdminDTO adminDTO) {
        if (adminDTO == null) {
            throw new ValidationException("Admin data cannot be null");
        }
        if (adminDTO.username() == null || adminDTO.username().trim().isEmpty()) {
            throw new ValidationException("Username cannot be empty");
        }
        if (adminDTO.email() == null || adminDTO.email().trim().isEmpty()) {
            throw new ValidationException("Email cannot be empty");
        }
        if (adminDTO.password() == null || adminDTO.password().length() < 6) {
            throw new ValidationException("Password must be at least 6 characters long");
        }
        if (adminDTO.dept() == null || adminDTO.dept().trim().isEmpty()) {
            throw new ValidationException("Department cannot be empty");
        }
    }

    private void validateTeacherInput(TeacherDTO teacherDTO) {
        if (teacherDTO == null) {
            throw new ValidationException("Teacher data cannot be null");
        }
        if (teacherDTO.username() == null || teacherDTO.username().trim().isEmpty()) {
            throw new ValidationException("Username cannot be empty");
        }
        if (teacherDTO.email() == null || teacherDTO.email().trim().isEmpty()) {
            throw new ValidationException("Email cannot be empty");
        }
        if (teacherDTO.password() == null || teacherDTO.password().length() < 6) {
            throw new ValidationException("Password must be at least 6 characters long");
        }
    }

    private void validateClubHeadInput(ClubHeadDTO clubHeadDTO) {
        if (clubHeadDTO == null) {
            throw new ValidationException("Club head data cannot be null");
        }
        if (clubHeadDTO.username() == null || clubHeadDTO.username().trim().isEmpty()) {
            throw new ValidationException("Username cannot be empty");
        }
        if (clubHeadDTO.email() == null || clubHeadDTO.email().trim().isEmpty()) {
            throw new ValidationException("Email cannot be empty");
        }
        if (clubHeadDTO.password() == null || clubHeadDTO.password().length() < 6) {
            throw new ValidationException("Password must be at least 6 characters long");
        }
    }

    private void validateStudentInput(StudentDTO studentDTO) {
        if (studentDTO == null) {
            throw new ValidationException("Student data cannot be null");
        }
        if (studentDTO.rollNumber() == null || studentDTO.rollNumber().trim().isEmpty()) {
            throw new ValidationException("Roll number cannot be empty");
        }
        if (studentDTO.username() == null || studentDTO.username().trim().isEmpty()) {
            throw new ValidationException("Username cannot be empty");
        }
        if (studentDTO.email() == null || studentDTO.email().trim().isEmpty()) {
            throw new ValidationException("Email cannot be empty");
        }
        if (studentDTO.password() == null || studentDTO.password().length() < 6) {
            throw new ValidationException("Password must be at least 6 characters long");
        }
        if (studentDTO.firstName() == null || studentDTO.firstName().trim().isEmpty()) {
            throw new ValidationException("First name cannot be empty");
        }
        if (studentDTO.lastName() == null || studentDTO.lastName().trim().isEmpty()) {
            throw new ValidationException("Last name cannot be empty");
        }
    }

    private void validateClassInput(ClassDTO classDTO) {
        if (classDTO == null) {
            throw new ValidationException("Class data cannot be null");
        }
        if (classDTO.className() == null || classDTO.className().trim().isEmpty()) {
            throw new ValidationException("Class name cannot be empty");
        }
    }

    private void validateAssignClubHeadInput(AssignClubHeadDTO assignDTO) {
        if (assignDTO == null) {
            throw new ValidationException("Assignment data cannot be null");
        }
        validateStudentId(assignDTO.studentId());
        validateClubName(assignDTO.clubName());
    }

    private void validateTeacherAssignment(TeacherAssignment teacherAssignment) {
        if (teacherAssignment == null) {
            throw new ValidationException("Teacher assignment cannot be null");
        }
        if (teacherAssignment.getTeacher() == null) {
            throw new ValidationException("Teacher cannot be null");
        }
        if (teacherAssignment.getAssignedClass() == null) {
            throw new ValidationException("Assigned class cannot be null");
        }
        if (teacherAssignment.getSubject() == null) {
            throw new ValidationException("Subject cannot be null");
        }
        if (teacherAssignment.getLectureType() == null) {
            throw new ValidationException("Lecture type cannot be null");
        }
        if (teacherAssignment.getWeekDay() == null) {
            throw new ValidationException("Week day cannot be null");
        }
        if (teacherAssignment.getSlotNumber() == null) {
            throw new ValidationException("Slot number cannot be null");
        }
    }

    private void validateStudentId(String studentId) {
        if (studentId == null || studentId.trim().isEmpty()) {
            throw new ValidationException("Student ID cannot be empty");
        }
    }

    private void validateClassId(String classId) {
        if (classId == null || classId.trim().isEmpty()) {
            throw new ValidationException("Class ID cannot be empty");
        }
    }

    private void validateId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new ValidationException("ID cannot be empty");
        }
    }

    private void validateClubName(String clubName) {
        if (clubName == null || clubName.trim().isEmpty()) {
            throw new ValidationException("Club name cannot be empty");
        }
    }

    private void validateStudentIds(List<String> studentIds) {
        if (studentIds == null || studentIds.isEmpty()) {
            throw new ValidationException("Student IDs list cannot be empty");
        }
        for (String id : studentIds) {
            validateStudentId(id);
        }
    }
}
