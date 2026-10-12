package com.student.management.service.impl;

import com.student.management.dto.request.DepartmentRequest;
import com.student.management.dto.response.DepartmentResponse;
import com.student.management.dto.response.PageResponse;
import com.student.management.entity.Department;
import com.student.management.exception.DuplicateResourceException;
import com.student.management.exception.ResourceNotFoundException;
import com.student.management.repository.DepartmentRepository;
import com.student.management.service.DepartmentService;
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
public class DepartmentServiceImpl implements DepartmentService {

    private static final Logger log = LoggerFactory.getLogger(DepartmentServiceImpl.class);

    private final DepartmentRepository departmentRepository;
    private final ModelMapper modelMapper;

    public DepartmentServiceImpl(DepartmentRepository departmentRepository, ModelMapper modelMapper) {
        this.departmentRepository = departmentRepository;
        this.modelMapper = modelMapper;
    }

    private DepartmentResponse mapToResponse(Department dept) {
        DepartmentResponse res = modelMapper.map(dept, DepartmentResponse.class);
        res.setStudentCount(dept.getStudents() != null ? dept.getStudents().size() : 0);
        res.setCourseCount(dept.getCourses() != null ? dept.getCourses().size() : 0);
        res.setTeacherCount(dept.getTeachers() != null ? dept.getTeachers().size() : 0);
        return res;
    }

    @Override
    @Transactional
    public DepartmentResponse createDepartment(DepartmentRequest request) {
        log.info("Creating department: {}", request.getName());
        if (departmentRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Department with name already exists: " + request.getName());
        }
        if (departmentRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Department with code already exists: " + request.getCode());
        }

        Department department = Department.builder()
                .name(request.getName())
                .code(request.getCode().toUpperCase())
                .description(request.getDescription())
                .build();

        Department saved = departmentRepository.save(department);
        return mapToResponse(saved);
    }

    @Override
    @Transactional
    public DepartmentResponse updateDepartment(Long id, DepartmentRequest request) {
        log.info("Updating department with ID: {}", id);
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", id));

        if (!department.getName().equalsIgnoreCase(request.getName()) &&
                departmentRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Department with name already exists: " + request.getName());
        }
        if (!department.getCode().equalsIgnoreCase(request.getCode()) &&
                departmentRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException("Department with code already exists: " + request.getCode());
        }

        department.setName(request.getName());
        department.setCode(request.getCode().toUpperCase());
        department.setDescription(request.getDescription());

        Department updated = departmentRepository.save(department);
        return mapToResponse(updated);
    }

    @Override
    @Transactional
    public void deleteDepartment(Long id) {
        log.info("Deleting department with ID: {}", id);
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", id));
        departmentRepository.delete(department);
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentResponse getDepartmentById(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", id));
        return mapToResponse(department);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<DepartmentResponse> getAllDepartments(int page, int size, String sortBy, String sortDir, String search) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ?
                Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Department> departmentPage;
        if (StringUtils.hasText(search)) {
            departmentPage = departmentRepository.findByNameContainingIgnoreCaseOrCodeContainingIgnoreCase(
                    search, search, pageable);
        } else {
            departmentPage = departmentRepository.findAll(pageable);
        }

        List<DepartmentResponse> content = departmentPage.getContent().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return PageResponse.of(departmentPage, content);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DepartmentResponse> getAllDepartmentsList() {
        return departmentRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
}
