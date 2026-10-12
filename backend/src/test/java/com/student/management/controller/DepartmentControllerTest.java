package com.student.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.student.management.dto.request.DepartmentRequest;
import com.student.management.dto.response.DepartmentResponse;
import com.student.management.security.CustomUserDetailsService;
import com.student.management.security.JwtAuthenticationEntryPoint;
import com.student.management.security.JwtAuthenticationFilter;
import com.student.management.security.JwtTokenProvider;
import com.student.management.service.DepartmentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DepartmentController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(locations = "classpath:application-test.properties")
class DepartmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DepartmentService departmentService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @DisplayName("POST /api/departments should return 201 Created")
    void testCreateDepartment() throws Exception {
        DepartmentRequest request = new DepartmentRequest("Civil Engineering", "CIVIL", "Civil Engg Dept");
        DepartmentResponse response = DepartmentResponse.builder()
                .id(1L)
                .name("Civil Engineering")
                .code("CIVIL")
                .description("Civil Engg Dept")
                .build();

        when(departmentService.createDepartment(any(DepartmentRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/departments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.code").value("CIVIL"));
    }

    @Test
    @DisplayName("GET /api/departments/1 should return 200 OK with department data")
    void testGetDepartmentById() throws Exception {
        DepartmentResponse response = DepartmentResponse.builder()
                .id(1L)
                .name("Civil Engineering")
                .code("CIVIL")
                .build();

        when(departmentService.getDepartmentById(1L)).thenReturn(response);

        mockMvc.perform(get("/api/departments/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Civil Engineering"));
    }
}
