package com.student.management.service.impl;

import com.student.management.dto.request.CourseRequest;
import com.student.management.dto.response.CourseResponse;
import com.student.management.dto.response.PageResponse;
import com.student.management.entity.Course;
import com.student.management.entity.Department;
import com.student.management.entity.Teacher;
import com.student.management.exception.DuplicateResourceException;
import com.student.management.exception.ResourceNotFoundException;
import com.student.management.repository.CourseRepository;
import com.student.management.repository.DepartmentRepository;
import com.student.management.repository.TeacherRepository;
import com.student.management.service.CourseService;
import com.student.management.util.EnrollmentStatus;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl implements CourseService {

    private static final Logger log = LoggerFactory.getLogger(CourseServiceImpl.class);

    private final CourseRepository courseRepository;
    private final DepartmentRepository departmentRepository;
    private final TeacherRepository teacherRepository;
    private final ModelMapper modelMapper;

    public CourseServiceImpl(CourseRepository courseRepository,
                             DepartmentRepository departmentRepository,
                             TeacherRepository teacherRepository,
                             ModelMapper modelMapper) {
        this.courseRepository = courseRepository;
        this.departmentRepository = departmentRepository;
        this.teacherRepository = teacherRepository;
        this.modelMapper = modelMapper;
    }

    private CourseResponse mapToResponse(Course course) {
        CourseResponse res = modelMapper.map(course, CourseResponse.class);
        if (course.getDepartment() != null) {
            res.setDepartmentId(course.getDepartment().getId());
            res.setDepartmentName(course.getDepartment().getName());
        }
        if (course.getTeacher() != null) {
            res.setTeacherId(course.getTeacher().getId());
            res.setTeacherName(course.getTeacher().getName());
        }
        long activeEnrolled = course.getEnrollments() != null ?
                course.getEnrollments().stream().filter(e -> e.getStatus() == EnrollmentStatus.ACTIVE).count() : 0;
        res.setEnrolledStudentCount((int) activeEnrolled);
        return res;
    }

    @Override
    @Transactional
    public CourseResponse createCourse(CourseRequest request) {
        log.info("Creating course with code: {}", request.getCode());
        if (courseRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Course with code already exists: " + request.getCode());
        }

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", request.getDepartmentId()));

        Teacher teacher = null;
        if (request.getTeacherId() != null) {
            teacher = teacherRepository.findById(request.getTeacherId())
                    .orElseThrow(() -> new ResourceNotFoundException("Teacher", "id", request.getTeacherId()));
        }

        Course course = Course.builder()
                .code(request.getCode().toUpperCase())
                .name(request.getName())
                .credits(request.getCredits())
                .semester(request.getSemester())
                .department(department)
                .teacher(teacher)
                .build();

        Course saved = courseRepository.save(course);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public CourseResponse updateCourse(Long id, CourseRequest request) {
        log.info("Updating course with ID: {}", id);
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));

        if (!course.getCode().equalsIgnoreCase(request.getCode()) &&
                courseRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Course with code already exists: " + request.getCode());
        }

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", request.getDepartmentId()));

        Teacher teacher = null;
        if (request.getTeacherId() != null) {
            teacher = teacherRepository.findById(request.getTeacherId())
                    .orElseThrow(() -> new ResourceNotFoundException("Teacher", "id", request.getTeacherId()));
        }

        course.setCode(request.getCode().toUpperCase());
        course.setName(request.getName());
        course.setCredits(request.getCredits());
        course.setSemester(request.getSemester());
        course.setDepartment(department);
        course.setTeacher(teacher);

        Course updated = courseRepository.save(course);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void deleteCourse(Long id) {
        log.info("Deleting course with ID: {}", id);
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));
        courseRepository.delete(course);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponse getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", "id", id));
        return mapToResponse(course);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CourseResponse> getAllCourses(int page, int size, String sortBy, String sortDir, String search) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Course> coursePage;
        if (StringUtils.hasText(search)) {
            coursePage = courseRepository.findByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(
                    search, search, pageable);
        } else {
            coursePage = courseRepository.findAll(pageable);
        }

        List<CourseResponse> content = coursePage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return PageResponse.of(coursePage, content);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponse> getCoursesByTeacher(String teacherEmail) {
        Teacher teacher = teacherRepository.findByEmail(teacherEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", "email", teacherEmail));

        return courseRepository.findByTeacherId(teacher.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponse> getAllCoursesList() {
        return courseRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
}
