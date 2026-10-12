package com.student.management.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "teachers")
public class Teacher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(length = 100)
    private String qualification;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @OneToMany(mappedBy = "teacher")
    private List<Course> courses = new ArrayList<>();

    public Teacher() {
    }

    public Teacher(Long id, String name, String email, String phone, String qualification, Department department, User user, List<Course> courses) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.qualification = qualification;
        this.department = department;
        this.user = user;
        this.courses = courses != null ? courses : new ArrayList<>();
    }

    public static TeacherBuilder builder() {
        return new TeacherBuilder();
    }

    public static class TeacherBuilder {
        private Long id;
        private String name;
        private String email;
        private String phone;
        private String qualification;
        private Department department;
        private User user;
        private List<Course> courses = new ArrayList<>();

        public TeacherBuilder id(Long id) { this.id = id; return this; }
        public TeacherBuilder name(String name) { this.name = name; return this; }
        public TeacherBuilder email(String email) { this.email = email; return this; }
        public TeacherBuilder phone(String phone) { this.phone = phone; return this; }
        public TeacherBuilder qualification(String qualification) { this.qualification = qualification; return this; }
        public TeacherBuilder department(Department department) { this.department = department; return this; }
        public TeacherBuilder user(User user) { this.user = user; return this; }
        public TeacherBuilder courses(List<Course> courses) { this.courses = courses; return this; }

        public Teacher build() {
            return new Teacher(id, name, email, phone, qualification, department, user, courses);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getQualification() { return qualification; }
    public void setQualification(String qualification) { this.qualification = qualification; }

    public Department getDepartment() { return department; }
    public void setDepartment(Department department) { this.department = department; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public List<Course> getCourses() { return courses; }
    public void setCourses(List<Course> courses) { this.courses = courses; }
}
