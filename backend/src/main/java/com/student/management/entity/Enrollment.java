package com.student.management.entity;

import com.student.management.util.EnrollmentStatus;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "enrollments", uniqueConstraints = {
    @UniqueConstraint(name = "uk_student_course", columnNames = {"student_id", "course_id"})
})
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "enrollment_date", nullable = false)
    private LocalDate enrollmentDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EnrollmentStatus status;

    @OneToMany(mappedBy = "enrollment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Attendance> attendances = new ArrayList<>();

    @OneToMany(mappedBy = "enrollment", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Mark> marks = new ArrayList<>();

    public Enrollment() {
    }

    public Enrollment(Long id, Student student, Course course, LocalDate enrollmentDate, EnrollmentStatus status, List<Attendance> attendances, List<Mark> marks) {
        this.id = id;
        this.student = student;
        this.course = course;
        this.enrollmentDate = enrollmentDate != null ? enrollmentDate : LocalDate.now();
        this.status = status != null ? status : EnrollmentStatus.ACTIVE;
        this.attendances = attendances != null ? attendances : new ArrayList<>();
        this.marks = marks != null ? marks : new ArrayList<>();
    }

    @PrePersist
    protected void onCreate() {
        if (this.enrollmentDate == null) {
            this.enrollmentDate = LocalDate.now();
        }
        if (this.status == null) {
            this.status = EnrollmentStatus.ACTIVE;
        }
    }

    public static EnrollmentBuilder builder() {
        return new EnrollmentBuilder();
    }

    public static class EnrollmentBuilder {
        private Long id;
        private Student student;
        private Course course;
        private LocalDate enrollmentDate = LocalDate.now();
        private EnrollmentStatus status = EnrollmentStatus.ACTIVE;
        private List<Attendance> attendances = new ArrayList<>();
        private List<Mark> marks = new ArrayList<>();

        public EnrollmentBuilder id(Long id) { this.id = id; return this; }
        public EnrollmentBuilder student(Student student) { this.student = student; return this; }
        public EnrollmentBuilder course(Course course) { this.course = course; return this; }
        public EnrollmentBuilder enrollmentDate(LocalDate enrollmentDate) { this.enrollmentDate = enrollmentDate; return this; }
        public EnrollmentBuilder status(EnrollmentStatus status) { this.status = status; return this; }
        public EnrollmentBuilder attendances(List<Attendance> attendances) { this.attendances = attendances; return this; }
        public EnrollmentBuilder marks(List<Mark> marks) { this.marks = marks; return this; }

        public Enrollment build() {
            return new Enrollment(id, student, course, enrollmentDate, status, attendances, marks);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }

    public LocalDate getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(LocalDate enrollmentDate) { this.enrollmentDate = enrollmentDate; }

    public EnrollmentStatus getStatus() { return status; }
    public void setStatus(EnrollmentStatus status) { this.status = status; }

    public List<Attendance> getAttendances() { return attendances; }
    public void setAttendances(List<Attendance> attendances) { this.attendances = attendances; }

    public List<Mark> getMarks() { return marks; }
    public void setMarks(List<Mark> marks) { this.marks = marks; }
}
