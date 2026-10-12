package com.student.management.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String code;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false)
    private Integer credits;

    @Column(nullable = false, length = 20)
    private String semester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Enrollment> enrollments = new ArrayList<>();

    public Course() {
    }

    public Course(Long id, String code, String name, Integer credits, String semester, Department department, Teacher teacher, List<Enrollment> enrollments) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.credits = credits;
        this.semester = semester;
        this.department = department;
        this.teacher = teacher;
        this.enrollments = enrollments != null ? enrollments : new ArrayList<>();
    }

    public static CourseBuilder builder() {
        return new CourseBuilder();
    }

    public static class CourseBuilder {
        private Long id;
        private String code;
        private String name;
        private Integer credits;
        private String semester;
        private Department department;
        private Teacher teacher;
        private List<Enrollment> enrollments = new ArrayList<>();

        public CourseBuilder id(Long id) { this.id = id; return this; }
        public CourseBuilder code(String code) { this.code = code; return this; }
        public CourseBuilder name(String name) { this.name = name; return this; }
        public CourseBuilder credits(Integer credits) { this.credits = credits; return this; }
        public CourseBuilder semester(String semester) { this.semester = semester; return this; }
        public CourseBuilder department(Department department) { this.department = department; return this; }
        public CourseBuilder teacher(Teacher teacher) { this.teacher = teacher; return this; }
        public CourseBuilder enrollments(List<Enrollment> enrollments) { this.enrollments = enrollments; return this; }

        public Course build() {
            return new Course(id, code, name, credits, semester, department, teacher, enrollments);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getCredits() { return credits; }
    public void setCredits(Integer credits) { this.credits = credits; }

    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }

    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }

    public Teacher getTeacher() { return teacher; }
    public void setTeacher(Teacher teacher) { this.teacher = teacher; }

    public List<Enrollment> getEnrollments() { return enrollments; }
    public void setEnrollments(List<Enrollment> enrollments) { this.enrollments = enrollments; }
}
