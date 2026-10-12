package com.student.management.util;

/**
 * Standard Grading System and 10-point Scale GPA Calculation.
 */
public enum Grade {
    A_PLUS("A+", 10.0),
    A("A", 9.0),
    B("B", 8.0),
    C("C", 7.0),
    D("D", 6.0),
    F("F", 0.0);

    private final String label;
    private final double gradePoint;

    Grade(String label, double gradePoint) {
        this.label = label;
        this.gradePoint = gradePoint;
    }

    public String getLabel() {
        return label;
    }

    public double getGradePoint() {
        return gradePoint;
    }

    public static Grade fromPercentage(double percentage) {
        if (percentage >= 90.0) {
            return A_PLUS;
        } else if (percentage >= 80.0) {
            return A;
        } else if (percentage >= 70.0) {
            return B;
        } else if (percentage >= 60.0) {
            return C;
        } else if (percentage >= 50.0) {
            return D;
        } else {
            return F;
        }
    }
}
