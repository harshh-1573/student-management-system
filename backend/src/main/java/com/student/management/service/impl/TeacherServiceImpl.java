package com.student.management.service.impl;

import com.student.management.dto.request.TeacherRequest;
import com.student.management.dto.response.PageResponse;
import com.student.management.dto.response.TeacherResponse;
import com.student.management.entity.Department;
import com.student.management.entity.Teacher;
import com.student.management.entity.User;
import com.student.management.exception.DuplicateResourceException;
import com.student.management.exception.ResourceNotFoundException;
import com.student.management.repository.DepartmentRepository;
import com.student.management.repository.TeacherRepository;
import com.student.management.repository.UserRepository;
import com.student.management.service.TeacherService;
import com.student.management.util.RoleType;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeacherServiceImpl implements TeacherService {

    private static final Logger log = LoggerFactory.getLogger(TeacherServiceImpl.class);

    private final TeacherRepository teacherRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    public TeacherServiceImpl(TeacherRepository teacherRepository,
                              DepartmentRepository departmentRepository,
                              UserRepository userRepository,
                              PasswordEncoder passwordEncoder,
                              ModelMapper modelMapper) {
        this.teacherRepository = teacherRepository;
        this.departmentRepository = departmentRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.modelMapper = modelMapper;
    }

    private TeacherResponse mapToResponse(Teacher teacher) {
        TeacherResponse res = modelMapper.map(teacher, TeacherResponse.class);
        if (teacher.getDepartment() != null) {
            res.setDepartmentId(teacher.getDepartment().getId());
            res.setDepartmentName(teacher.getDepartment().getName());
            res.setDepartmentCode(teacher.getDepartment().getCode());
        }
        if (teacher.getUser() != null) {
            res.setUserId(teacher.getUser().getId());
        }
        return res;
    }

    @Override
    @Transactional
    public TeacherResponse createTeacher(TeacherRequest request) {
        log.info("Creating teacher with email: {}", request.getEmail());
        if (teacherRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Teacher with email already exists: " + request.getEmail());
        }

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", request.getDepartmentId()));

        User user = userRepository.findByEmail(request.getEmail()).orElse(null);
        if (user == null) {
            String rawPassword = StringUtils.hasText(request.getPassword()) ? request.getPassword() : "Teacher@123";
            user = User.builder()
                    .fullName(request.getName())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(rawPassword))
                    .role(RoleType.ROLE_TEACHER)
                    .build();
            user = userRepository.save(user);
        }

        Teacher teacher = Teacher.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .qualification(request.getQualification())
                .department(department)
                .user(user)
                .build();

        Teacher saved = teacherRepository.save(teacher);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public TeacherResponse updateTeacher(Long id, TeacherRequest request) {
        log.info("Updating teacher with ID: {}", id);
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", "id", id));

        if (!teacher.getEmail().equalsIgnoreCase(request.getEmail()) &&
                teacherRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Teacher with email already exists: " + request.getEmail());
        }

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", request.getDepartmentId()));

        teacher.setName(request.getName());
        teacher.setEmail(request.getEmail());
        teacher.setPhone(request.getPhone());
        teacher.setQualification(request.getQualification());
        teacher.setDepartment(department);

        if (teacher.getUser() != null) {
            teacher.getUser().setFullName(request.getName());
            teacher.getUser().setEmail(request.getEmail());
            if (StringUtils.hasText(request.getPassword())) {
                teacher.getUser().setPassword(passwordEncoder.encode(request.getPassword()));
            }
        }

        Teacher updated = teacherRepository.save(teacher);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void deleteTeacher(Long id) {
        log.info("Deleting teacher with ID: {}", id);
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", "id", id));
        teacherRepository.delete(teacher);
    }

    @Override
    @Transactional(readOnly = true)
    public TeacherResponse getTeacherById(Long id) {
        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", "id", id));
        return mapToResponse(teacher);
    }

    @Override
    @Transactional(readOnly = true)
    public TeacherResponse getTeacherByEmail(String email) {
        Teacher teacher = teacherRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher", "email", email));
        return mapToResponse(teacher);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<TeacherResponse> getAllTeachers(int page, int size, String sortBy, String sortDir, String search) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Teacher> teacherPage;
        if (StringUtils.hasText(search)) {
            teacherPage = teacherRepository.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                    search, search, pageable);
        } else {
            teacherPage = teacherRepository.findAll(pageable);
        }

        List<TeacherResponse> content = teacherPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return PageResponse.of(teacherPage, content);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TeacherResponse> getAllTeachersList() {
        return teacherRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
}
