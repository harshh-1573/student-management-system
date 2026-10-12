package com.student.management.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "departments")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @Column(length = 500)
    private String description;

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Student> students = new ArrayList<>();

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Teacher> teachers = new ArrayList<>();

    @OneToMany(mappedBy = "department", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Course> courses = new ArrayList<>();

    public Department() {
    }

    public Department(Long id, String name, String code, String description, List<Student> students, List<Teacher> teachers, List<Course> courses) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.description = description;
        this.students = students != null ? students : new ArrayList<>();
        this.teachers = teachers != null ? teachers : new ArrayList<>();
        this.courses = courses != null ? courses : new ArrayList<>();
    }

    public static DepartmentBuilder builder() {
        return new DepartmentBuilder();
    }

    public static class DepartmentBuilder {
        private Long id;
        private String name;
        private String code;
        private String description;
        private List<Student> students = new ArrayList<>();
        private List<Teacher> teachers = new ArrayList<>();
        private List<Course> courses = new ArrayList<>();

        public DepartmentBuilder id(Long id) { this.id = id; return this; }
        public DepartmentBuilder name(String name) { this.name = name; return this; }
        public DepartmentBuilder code(String code) { this.code = code; return this; }
        public DepartmentBuilder description(String description) { this.description = description; return this; }
        public DepartmentBuilder students(List<Student> students) { this.students = students; return this; }
        public DepartmentBuilder teachers(List<Teacher> teachers) { this.teachers = teachers; return this; }
        public DepartmentBuilder courses(List<Course> courses) { this.courses = courses; return this; }

        public Department build() {
            return new Department(id, name, code, description, students, teachers, courses);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public List<Student> getStudents() { return students; }
    public void setStudents(List<Student> students) { this.students = students; }

    public List<Teacher> getTeachers() { return teachers; }
    public void setTeachers(List<Teacher> teachers) { this.teachers = teachers; }

    public List<Course> getCourses() { return courses; }
    public void setCourses(List<Course> courses) { this.courses = courses; }
}
