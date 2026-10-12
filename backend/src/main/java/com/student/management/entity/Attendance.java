package com.student.management.entity;

import com.student.management.util.AttendanceStatus;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "attendances", uniqueConstraints = {
    @UniqueConstraint(name = "uk_enrollment_date", columnNames = {"enrollment_id", "attendance_date"})
})
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id", nullable = false)
    private Enrollment enrollment;

    @Column(name = "attendance_date", nullable = false)
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AttendanceStatus status;

    public Attendance() {
    }

    public Attendance(Long id, Enrollment enrollment, LocalDate date, AttendanceStatus status) {
        this.id = id;
        this.enrollment = enrollment;
        this.date = date;
        this.status = status;
    }

    public static AttendanceBuilder builder() {
        return new AttendanceBuilder();
    }

    public static class AttendanceBuilder {
        private Long id;
        private Enrollment enrollment;
        private LocalDate date;
        private AttendanceStatus status;

        public AttendanceBuilder id(Long id) { this.id = id; return this; }
        public AttendanceBuilder enrollment(Enrollment enrollment) { this.enrollment = enrollment; return this; }
        public AttendanceBuilder date(LocalDate date) { this.date = date; return this; }
        public AttendanceBuilder status(AttendanceStatus status) { this.status = status; return this; }

        public Attendance build() {
            return new Attendance(id, enrollment, date, status);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Enrollment getEnrollment() { return enrollment; }
    public void setEnrollment(Enrollment enrollment) { this.enrollment = enrollment; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public AttendanceStatus getStatus() { return status; }
    public void setStatus(AttendanceStatus status) { this.status = status; }
}
