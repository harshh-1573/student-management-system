package com.student.management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.student.management.dto.request.CourseRequest;
import com.student.management.dto.response.CourseResponse;
import com.student.management.security.CustomUserDetailsService;
import com.student.management.security.JwtAuthenticationEntryPoint;
import com.student.management.security.JwtAuthenticationFilter;
import com.student.management.security.JwtTokenProvider;
import com.student.management.service.CourseService;
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

@WebMvcTest(CourseController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(locations = "classpath:application-test.properties")
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CourseService courseService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @DisplayName("POST /api/courses should return 201 Created when course payload is valid")
    void testCreateCourse() throws Exception {
        CourseRequest request = CourseRequest.builder()
                .code("CS201")
                .name("Operating Systems")
                .credits(4)
                .semester("Semester 4")
                .departmentId(1L)
                .build();

        CourseResponse response = CourseResponse.builder()
                .id(10L)
                .code("CS201")
                .name("Operating Systems")
                .credits(4)
                .semester("Semester 4")
                .build();

        when(courseService.createCourse(any(CourseRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/courses")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.code").value("CS201"));
    }

    @Test
    @DisplayName("GET /api/courses/10 should return 200 OK with course details")
    void testGetCourseById() throws Exception {
        CourseResponse response = CourseResponse.builder()
                .id(10L)
                .code("CS201")
                .name("Operating Systems")
                .credits(4)
                .build();

        when(courseService.getCourseById(10L)).thenReturn(response);

        mockMvc.perform(get("/api/courses/10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.name").value("Operating Systems"));
    }
}
