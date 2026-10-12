package com.student.management.entity;

import com.student.management.util.ExamType;
import jakarta.persistence.*;

@Entity
@Table(name = "marks", uniqueConstraints = {
    @UniqueConstraint(name = "uk_enrollment_exam_type", columnNames = {"enrollment_id", "exam_type"})
})
public class Mark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id", nullable = false)
    private Enrollment enrollment;

    @Enumerated(EnumType.STRING)
    @Column(name = "exam_type", nullable = false, length = 30)
    private ExamType examType;

    @Column(name = "marks_obtained", nullable = false)
    private Double marksObtained;

    @Column(name = "max_marks", nullable = false)
    private Double maxMarks;

    public Mark() {
    }

    public Mark(Long id, Enrollment enrollment, ExamType examType, Double marksObtained, Double maxMarks) {
        this.id = id;
        this.enrollment = enrollment;
        this.examType = examType;
        this.marksObtained = marksObtained;
        this.maxMarks = maxMarks;
    }

    public static MarkBuilder builder() {
        return new MarkBuilder();
    }

    public static class MarkBuilder {
        private Long id;
        private Enrollment enrollment;
        private ExamType examType;
        private Double marksObtained;
        private Double maxMarks;

        public MarkBuilder id(Long id) { this.id = id; return this; }
        public MarkBuilder enrollment(Enrollment enrollment) { this.enrollment = enrollment; return this; }
        public MarkBuilder examType(ExamType examType) { this.examType = examType; return this; }
        public MarkBuilder marksObtained(Double marksObtained) { this.marksObtained = marksObtained; return this; }
        public MarkBuilder maxMarks(Double maxMarks) { this.maxMarks = maxMarks; return this; }

        public Mark build() {
            return new Mark(id, enrollment, examType, marksObtained, maxMarks);
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Enrollment getEnrollment() { return enrollment; }
    public void setEnrollment(Enrollment enrollment) { this.enrollment = enrollment; }

    public ExamType getExamType() { return examType; }
    public void setExamType(ExamType examType) { this.examType = examType; }

    public Double getMarksObtained() { return marksObtained; }
    public void setMarksObtained(Double marksObtained) { this.marksObtained = marksObtained; }

    public Double getMaxMarks() { return maxMarks; }
    public void setMaxMarks(Double maxMarks) { this.maxMarks = maxMarks; }
}
