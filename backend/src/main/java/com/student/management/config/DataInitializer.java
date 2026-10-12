package com.student.management.config;

import com.student.management.entity.*;
import com.student.management.repository.*;
import com.student.management.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final AttendanceRepository attendanceRepository;
    private final MarkRepository markRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           DepartmentRepository departmentRepository,
                           TeacherRepository teacherRepository,
                           StudentRepository studentRepository,
                           CourseRepository courseRepository,
                           EnrollmentRepository enrollmentRepository,
                           AttendanceRepository attendanceRepository,
                           MarkRepository markRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.attendanceRepository = attendanceRepository;
        this.markRepository = markRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.existsByEmail("admin@sms.com")) {
            log.info("Database already seeded. Skipping DataInitializer.");
            return;
        }

        log.info("Starting DataInitializer: Seeding initial departments, users, courses, and academic data...");

        // 1. Seed Admin
        User adminUser = User.builder()
                .fullName("System Administrator")
                .email("admin@sms.com")
                .password(passwordEncoder.encode("Admin@123"))
                .role(RoleType.ROLE_ADMIN)
                .build();
        userRepository.save(adminUser);
        log.info("Seeded Admin: admin@sms.com / Admin@123");

        // 2. Seed Departments
        Department deptCse = departmentRepository.save(Department.builder()
                .name("Computer Science & Engineering")
                .code("CSE")
                .description("Department of Computer Science and Engineering")
                .build());

        Department deptIt = departmentRepository.save(Department.builder()
                .name("Information Technology")
                .code("IT")
                .description("Department of Information Technology and Systems")
                .build());

        Department deptEce = departmentRepository.save(Department.builder()
                .name("Electronics & Communication")
                .code("ECE")
                .description("Department of Electronics and Communication Engineering")
                .build());

        // 3. Seed Teachers
        User teacherUser1 = userRepository.save(User.builder()
                .fullName("Prof. Alan Turing")
                .email("alan.turing@sms.com")
                .password(passwordEncoder.encode("Teacher@123"))
                .role(RoleType.ROLE_TEACHER)
                .build());

        Teacher teacher1 = teacherRepository.save(Teacher.builder()
                .name("Prof. Alan Turing")
                .email("alan.turing@sms.com")
                .phone("+1 555-0101")
                .qualification("Ph.D. in Computer Science")
                .department(deptCse)
                .user(teacherUser1)
                .build());

        User teacherUser2 = userRepository.save(User.builder()
                .fullName("Prof. Ada Lovelace")
                .email("ada.lovelace@sms.com")
                .password(passwordEncoder.encode("Teacher@123"))
                .role(RoleType.ROLE_TEACHER)
                .build());

        Teacher teacher2 = teacherRepository.save(Teacher.builder()
                .name("Prof. Ada Lovelace")
                .email("ada.lovelace@sms.com")
                .phone("+1 555-0102")
                .qualification("M.Tech in Software Systems")
                .department(deptIt)
                .user(teacherUser2)
                .build());

        // 4. Seed Courses
        Course courseCs101 = courseRepository.save(Course.builder()
                .code("CS101")
                .name("Data Structures & Algorithms")
                .credits(4)
                .semester("Semester 3")
                .department(deptCse)
                .teacher(teacher1)
                .build());

        Course courseCs102 = courseRepository.save(Course.builder()
                .code("CS102")
                .name("Database Management Systems")
                .credits(4)
                .semester("Semester 4")
                .department(deptCse)
                .teacher(teacher1)
                .build());

        Course courseCs103 = courseRepository.save(Course.builder()
                .code("CS103")
                .name("Computer Networks")
                .credits(3)
                .semester("Semester 5")
                .department(deptCse)
                .teacher(teacher1)
                .build());

        Course courseIt201 = courseRepository.save(Course.builder()
                .code("IT201")
                .name("Web Technologies & Frameworks")
                .credits(3)
                .semester("Semester 3")
                .department(deptIt)
                .teacher(teacher2)
                .build());

        Course courseIt202 = courseRepository.save(Course.builder()
                .code("IT202")
                .name("Cloud Computing & DevOps")
                .credits(4)
                .semester("Semester 6")
                .department(deptIt)
                .teacher(teacher2)
                .build());

        Course courseEc301 = courseRepository.save(Course.builder()
                .code("EC301")
                .name("Digital Signal Processing")
                .credits(4)
                .semester("Semester 5")
                .department(deptEce)
                .teacher(teacher2)
                .build());

        // 5. Seed Students
        String studentPassword = passwordEncoder.encode("Student@123");

        List<Student> students = new ArrayList<>();
        String[][] studentData = {
                {"John Doe", "john.doe@sms.com", "STU-2024-001", "+1 555-0201", "2002-05-14", "123 Elm St, Tech City", "2024"},
                {"Jane Smith", "jane.smith@sms.com", "STU-2024-002", "+1 555-0202", "2003-08-22", "456 Oak Ave, Innovation Hub", "2024"},
                {"Alice Wong", "alice.wong@sms.com", "STU-2024-003", "+1 555-0203", "2002-11-10", "789 Pine Rd, Metro City", "2024"},
                {"Bob Ross", "bob.ross@sms.com", "STU-2024-004", "+1 555-0204", "2001-04-03", "321 Cedar Blvd, Green Valley", "2024"},
                {"Charlie Brown", "charlie.brown@sms.com", "STU-2024-005", "+1 555-0205", "2003-01-19", "654 Maple Dr, Hillside", "2024"}
        };

        Department[] studentDepts = {deptCse, deptCse, deptIt, deptIt, deptEce};

        for (int i = 0; i < studentData.length; i++) {
            String[] data = studentData[i];
            User sUser = userRepository.save(User.builder()
                    .fullName(data[0])
                    .email(data[1])
                    .password(studentPassword)
                    .role(RoleType.ROLE_STUDENT)
                    .build());

            Student s = studentRepository.save(Student.builder()
                    .name(data[0])
                    .email(data[1])
                    .rollNumber(data[2])
                    .phone(data[3])
                    .dateOfBirth(LocalDate.parse(data[4]))
                    .address(data[5])
                    .enrollmentYear(Integer.parseInt(data[6]))
                    .department(studentDepts[i])
                    .user(sUser)
                    .build());
            students.add(s);
        }

        // 6. Seed Enrollments
        List<Enrollment> enrollments = new ArrayList<>();
        enrollments.add(enrollmentRepository.save(Enrollment.builder().student(students.get(0)).course(courseCs101).enrollmentDate(LocalDate.now().minusMonths(2)).status(EnrollmentStatus.ACTIVE).build()));
        enrollments.add(enrollmentRepository.save(Enrollment.builder().student(students.get(0)).course(courseCs102).enrollmentDate(LocalDate.now().minusMonths(2)).status(EnrollmentStatus.ACTIVE).build()));
        enrollments.add(enrollmentRepository.save(Enrollment.builder().student(students.get(1)).course(courseCs101).enrollmentDate(LocalDate.now().minusMonths(2)).status(EnrollmentStatus.ACTIVE).build()));
        enrollments.add(enrollmentRepository.save(Enrollment.builder().student(students.get(1)).course(courseCs103).enrollmentDate(LocalDate.now().minusMonths(2)).status(EnrollmentStatus.ACTIVE).build()));
        enrollments.add(enrollmentRepository.save(Enrollment.builder().student(students.get(2)).course(courseIt201).enrollmentDate(LocalDate.now().minusMonths(2)).status(EnrollmentStatus.ACTIVE).build()));
        enrollments.add(enrollmentRepository.save(Enrollment.builder().student(students.get(2)).course(courseIt202).enrollmentDate(LocalDate.now().minusMonths(2)).status(EnrollmentStatus.ACTIVE).build()));
        enrollments.add(enrollmentRepository.save(Enrollment.builder().student(students.get(3)).course(courseIt201).enrollmentDate(LocalDate.now().minusMonths(2)).status(EnrollmentStatus.ACTIVE).build()));
        enrollments.add(enrollmentRepository.save(Enrollment.builder().student(students.get(4)).course(courseEc301).enrollmentDate(LocalDate.now().minusMonths(2)).status(EnrollmentStatus.ACTIVE).build()));
        enrollments.add(enrollmentRepository.save(Enrollment.builder().student(students.get(4)).course(courseCs101).enrollmentDate(LocalDate.now().minusMonths(2)).status(EnrollmentStatus.ACTIVE).build()));

        // 7. Seed Attendance records (simulate past 5 class days)
        LocalDate today = LocalDate.now();
        for (Enrollment enrollment : enrollments) {
            for (int dayOffset = 10; dayOffset >= 2; dayOffset -= 2) {
                LocalDate attDate = today.minusDays(dayOffset);
                AttendanceStatus status = (dayOffset == 4) ? AttendanceStatus.LATE :
                        (dayOffset == 8 && enrollment.getStudent().getId() % 2 == 0) ? AttendanceStatus.ABSENT : AttendanceStatus.PRESENT;

                attendanceRepository.save(Attendance.builder()
                        .enrollment(enrollment)
                        .date(attDate)
                        .status(status)
                        .build());
            }
        }

        // 8. Seed Marks
        for (int i = 0; i < enrollments.size(); i++) {
            Enrollment e = enrollments.get(i);
            // Midterm
            double midtermScore = 40.0 + (i % 8);
            markRepository.save(Mark.builder()
                    .enrollment(e)
                    .examType(ExamType.MIDTERM)
                    .marksObtained(midtermScore)
                    .maxMarks(50.0)
                    .build());

            // Assignment
            markRepository.save(Mark.builder()
                    .enrollment(e)
                    .examType(ExamType.ASSIGNMENT)
                    .marksObtained(18.0 + (i % 3))
                    .maxMarks(20.0)
                    .build());

            // Final
            double finalScore = 78.0 + (i % 18);
            markRepository.save(Mark.builder()
                    .enrollment(e)
                    .examType(ExamType.FINAL)
                    .marksObtained(finalScore)
                    .maxMarks(100.0)
                    .build());
        }

        log.info("DataInitializer completed successfully: 1 Admin, 2 Teachers, 5 Students, 3 Departments, 6 Courses seeded with attendance and marks.");
    }
}
