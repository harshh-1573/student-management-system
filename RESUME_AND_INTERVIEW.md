# Student Management System — Resume & Interview Preparation Guide

This guide is designed for freshers and junior full-stack developers preparing for technical interviews and crafting resumes around this production-grade **Student Management System (SMS)** project.

---

## 1. Resume Content

### One-Line Project Description
> **Student Management System**: A full-stack academic administration platform with role-based access control, automated GPA calculation, course enrollment workflows, and real-time attendance analytics built using Spring Boot 3, Spring Security 6, MySQL, and React 18.

### LaTeX Resume Tech Stack Line
```latex
\textbf{Skills & Technologies:} Java 17, Spring Boot 3.2, Spring Security 6, Spring Data JPA, Hibernate, JWT, MySQL 8, React 18, Vite, Tailwind CSS, RESTful APIs, JUnit 5, Mockito, Swagger/OpenAPI.
```

### 3 High-Impact Resume Bullet Points
- **Architected and delivered an enterprise-grade Student Management System** servicing 3 distinct roles (Admin, Teacher, Student) across 35+ RESTful endpoints, achieving sub-50ms API responses using Spring Boot 3.2, Hibernate fetch joins to eliminate N+1 queries, and MySQL indexing.
- **Engineered a stateless JWT security architecture with Spring Security 6**, implementing BCrypt password hashing, custom filter chains, and fine-grained method-level RBAC (`@PreAuthorize`) paired with service-layer data ownership validation to enforce strict multi-tenant boundary isolation.
- **Developed a responsive React 18 SPA (Vite, Tailwind CSS, Recharts)** featuring dynamic role-based dashboards, automated 10-point GPA calculations, attendance percentage tracking, and integrated full test coverage via 22 JUnit 5, Mockito, and `@WebMvcTest` test suites.

---

## 2. 25 Core Technical Interview Questions & Answers

### Q1: Explain the JWT authentication flow implemented in this project.
**Answer:**
1. The client sends a `POST /api/auth/login` request with email and password.
2. Spring Security's `AuthenticationManager` authenticates credentials using `CustomUserDetailsService` and hashes the password with BCrypt.
3. Upon authentication, `JwtTokenProvider` generates a signed JSON Web Token with HMAC-SHA256 (`HS256`), embedding user ID, email, role (`ROLE_ADMIN`, `ROLE_TEACHER`, or `ROLE_STUDENT`), issue time, and expiration time (24 hours).
4. The client stores the JWT in `localStorage` and attaches it to the `Authorization: Bearer <token>` header on subsequent requests via an Axios request interceptor.
5. On incoming requests, `JwtAuthenticationFilter` intercepts the request, validates the token signature and expiration, extracts the user details, creates a `UsernamePasswordAuthenticationToken`, and loads it into the `SecurityContextHolder`.

---

### Q2: What is the Spring Security 6 filter chain, and how did you configure it?
**Answer:**
In Spring Boot 3 / Spring Security 6, `WebSecurityConfigurerAdapter` is deprecated. Security is configured using a `@Bean` returning a `SecurityFilterChain`. We configured:
- `csrf(AbstractHttpConfigurer::disable)`: Disabled because JWT is stateless and not susceptible to cookie-based CSRF attacks.
- `sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))`: No HTTP sessions are maintained on the server.
- `cors(...)`: Allowed origins configured via environment variables (`${CORS_ORIGIN}`).
- `authorizeHttpRequests(...)`: Whitelisted public endpoints (`/api/auth/**`, Swagger UI `/swagger-ui/**`, `/v3/api-docs/**`) and secured all other endpoints with `authenticated()`.
- `exceptionHandling(...)`: Custom `AuthenticationEntryPoint` (returns JSON 401) and `AccessDeniedHandler` (returns JSON 403).
- `addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)`: Injects our JWT validation before standard username/password authentication.

---

### Q3: How did you implement Role-Based Access Control (RBAC)?
**Answer:**
We enabled method security using `@EnableMethodSecurity`. Endpoints are protected with annotations like `@PreAuthorize("hasRole('ADMIN')")` or `@PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")`. Furthermore, we enforced **data ownership checks** in the service layer: for example, a Teacher can only record marks or attendance for courses specifically assigned to them, and a Student can only view their own grades and transcripts, preventing IDOR (Insecure Direct Object Reference) vulnerabilities.

---

### Q4: How is Many-to-Many implemented between Student and Course?
**Answer:**
Instead of a simple `@ManyToMany` with a hidden join table, we modeled enrollment with a first-class JPA entity: **`Enrollment`**.
- `Student` has `@OneToMany(mappedBy = "student")` to `Enrollment`.
- `Course` has `@OneToMany(mappedBy = "course")` to `Enrollment`.
- `Enrollment` holds attributes: `enrollmentDate`, `status` (`ACTIVE` or `DROPPED`), and a unique constraint on `(student_id, course_id)`.
- This enables cascading attendance records and marks directly to an individual enrollment lifecycle rather than a detached join table.

---

### Q5: What is the N+1 SELECT problem in JPA, and how did you solve it?
**Answer:**
The N+1 problem occurs when querying a parent entity (e.g., retrieving $N$ courses) triggers 1 initial query for the list, followed by $N$ separate SQL queries to fetch each lazily-loaded relationship (e.g., Department and Teacher). We resolved this in `CourseRepository` and `StudentRepository` by using:
1. `@EntityGraph(attributePaths = {"department", "teacher"})` on `findAll()` queries.
2. Explicit JPQL `JOIN FETCH` queries (`SELECT c FROM Course c JOIN FETCH c.department LEFT JOIN FETCH c.teacher`).
This generates a single SQL `INNER/LEFT JOIN`, reducing database roundtrips from $N+1$ to 1.

---

### Q6: Why did you use DTOs instead of exposing Entities directly?
**Answer:**
1. **Security & Data Hiding:** Prevents leaking sensitive fields (e.g., `password` hash).
2. **Decoupling:** Decouples internal database schemas from external API contracts. Database column changes don't break external clients.
3. **Circular Reference Prevention:** Entities with bidirectional relationships cause Jackson serialization infinite recursion (`StackOverflowError`).
4. **Validation:** Request DTOs carry validation constraints (`@NotBlank`, `@Email`, `@Min`) tailored to specific operations without polluting DB models.

---

### Q7: How does `@RestControllerAdvice` work for global exception handling?
**Answer:**
`GlobalExceptionHandler` is annotated with `@RestControllerAdvice`. It catches exceptions thrown by controllers or services:
- `MethodArgumentNotValidException`: Formats Jakarta Bean Validation errors into a field-by-field error map (`{ "email": "Must be a valid email address" }`).
- `ResourceNotFoundException`: Returns HTTP 404 with a structured `ApiResponse`.
- `DuplicateResourceException`: Returns HTTP 409 (Conflict).
- `UnauthorizedException` / `AccessDeniedException`: Returns HTTP 401 / 403.
- `Exception`: Generic fallback returning HTTP 500 without leaking stack traces.

---

### Q8: What does `@Transactional` do, and where should it be applied?
**Answer:**
`@Transactional` manages database transactions declaratively using Spring AOP proxies. It should be placed at the **Service layer** where business transactions execute:
- When a method succeeds, the proxy commits the transaction and flushes dirty entity changes to the database.
- If an unchecked exception (`RuntimeException`) occurs, the transaction automatically rolls back, preserving database integrity.
- For read-only operations, we use `@Transactional(readOnly = true)`, which optimizes Hibernate performance by disabling dirty checking and enabling database read-replica optimizations.

---

### Q9: Why shouldn't you use `data.sql` for production seed data?
**Answer:**
1. `data.sql` runs raw SQL before password hashing libraries or domain business logic can execute, making BCrypt hashes brittle or plain-text.
2. Hardcoded insert statements can crash on restarts if primary keys collide.
3. Instead, we used a Spring `CommandLineRunner` (`DataInitializer`). It programmatically checks whether admin/departments already exist (`repository.existsByEmail(...)`), encodes passwords with `PasswordEncoder`, maintains relational consistency, and runs idempotently without duplicate key errors.

---

### Q10: How does BCrypt password hashing work?
**Answer:**
BCrypt is an adaptive cryptographic hash function based on the Blowfish cipher. It incorporates:
1. **Salt:** A cryptographically random 16-byte salt generated per password, stored as part of the hash string. This defeats rainbow table attacks.
2. **Work Factor (Cost):** Configurable number of hashing rounds ($2^{10} = 1024$ rounds by default). As CPU speeds increase, the cost factor can be increased to resist brute-force attacks.
3. In Spring Security, `BCryptPasswordEncoder.matches(rawPassword, encodedPassword)` re-extracts the salt from the stored hash and compares the derived hashes in constant time to prevent timing attacks.

---

### Q11: What is the Open Session in View (OSIV) pattern, and why disable it?
**Answer:**
By default, Spring Boot sets `spring.jpa.open-in-view=true`, keeping the Hibernate `EntityManager`/database connection open during the entire web request, including JSON serialization.
- **Drawbacks:** Holds database connection pool connections open for too long (hurting concurrency) and masks lazy loading issues outside of transactions.
- We set `spring.jpa.open-in-view=false` in `application.properties`. This forces all database fetching to occur explicitly within `@Transactional` service methods, catching lazy initialization errors early and maximizing connection pool efficiency.

---

### Q12: How is pagination and sorting implemented in the backend?
**Answer:**
We use Spring Data's `Pageable` and `PageRequest`:
- Controllers accept `@RequestParam(defaultValue = "0") int page`, `@RequestParam(defaultValue = "10") int size`, and `sortBy` / `sortDir`.
- Services construct `PageRequest.of(page, size, Sort.by(direction, sortBy))` and pass it to `repository.findAll(pageable)`.
- The `Page<T>` result is mapped to a reusable `PageResponse<T>` DTO containing: `content`, `pageNumber`, `pageSize`, `totalElements`, `totalPages`, and `last`.

---

### Q13: How does the attendance percentage calculation work?
**Answer:**
For any course:
$$\text{Attendance \%} = \frac{\text{Present Count} + (0.5 \times \text{Late Count})}{\text{Total Sessions Recorded}} \times 100$$
If a student attended 18 sessions and was late 2 times out of 20 total lectures:
$$\text{Score} = \frac{18 + (0.5 \times 2)}{20} \times 100 = \frac{19}{20} \times 100 = 95.0\%$$
Students with $< 75\%$ attendance trigger visual warning badges across both teacher registers and student transcripts.

---

### Q14: How is GPA calculated on the 10-point scale?
**Answer:**
Each course's total exam score percentage maps to a 10-point grade scale:
- $\ge 90\% \implies \text{A+} \ (10.0)$
- $\ge 80\% \implies \text{A} \ (9.0)$
- $\ge 70\% \implies \text{B} \ (8.0)$
- $\ge 60\% \implies \text{C} \ (7.0)$
- $\ge 50\% \implies \text{D} \ (6.0)$
- $< 50\% \implies \text{F} \ (0.0)$

The cumulative GPA (CGPA) is computed as the credit-weighted average:
$$\text{CGPA} = \frac{\sum (\text{Course Grade Point} \times \text{Course Credits})}{\sum \text{Course Credits}}$$

---

### Q15: How does CORS work, and how is it configured in Spring Boot 3?
**Answer:**
Cross-Origin Resource Sharing (CORS) is a browser security mechanism that restricts HTTP requests made by JavaScript to a different domain, port, or protocol than the serving origin.
- We configured a `CorsConfigurationSource` bean in `SecurityConfig`:
  - `setAllowedOrigins(List.of(corsOrigin))` (defaults to `http://localhost:5173`).
  - `setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"))`.
  - `setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"))`.
  - `setAllowCredentials(true)`.
This allows the React Vite frontend to communicate seamlessly with the backend while blocking untrusted origins.

---

### Q16: How do Axios interceptors handle authentication and session expiry?
**Answer:**
In `src/api/axios.js`:
1. **Request Interceptor:** Reads the JWT from `localStorage.getItem('sms_token')` and appends `Authorization: Bearer <token>` to all outgoing HTTP requests.
2. **Response Interceptor:** Listens for responses. If the backend returns HTTP 401 (Unauthorized - invalid or expired token):
   - Clears `sms_token` and `sms_user` from `localStorage`.
   - Dispatches a toast notification.
   - Redirects the browser window to `/login`.

---

### Q17: What are the benefits of React Context API for authentication state?
**Answer:**
1. **Global Accessibility:** Eliminates prop-drilling by providing `user`, `token`, `isAuthenticated`, `login`, and `logout` across the entire component tree.
2. **Simplicity:** For authentication and user profile data, Redux or external state managers add unnecessary boilerplate (reducers, actions, slices). Context API combined with `localStorage` persistence is lightweight, clean, and idiomatic for React 18.

---

### Q18: What is the difference between `@Mock` and `@InjectMocks` in Mockito?
**Answer:**
- `@Mock`: Creates a mock instance of a dependency (e.g., `@Mock private StudentRepository studentRepository`). Method calls return default mock values unless stubbed with `when(...).thenReturn(...)`.
- `@InjectMocks`: Instantiates the actual class under test (e.g., `@InjectMocks private StudentServiceImpl studentService`) and automatically injects all declared `@Mock` fields into its constructor or fields.

---

### Q19: What does `@WebMvcTest` test, and how does it differ from `@SpringBootTest`?
**Answer:**
- `@WebMvcTest(CourseController.class)` is a focused slice test. It only spins up the Spring MVC infrastructure (controllers, filters, message converters, Jackson, security), without loading the full application context, Hibernate, or database repositories. Service dependencies are mocked using `@MockBean`. It is fast and specifically tests HTTP status codes, JSON serialization, and validation.
- `@SpringBootTest` loads the entire Spring ApplicationContext, including all beans, configurations, and database connections. It is slower and used for full end-to-end integration tests.

---

### Q20: Why do we avoid using Lombok in this project?
**Answer:**
In newer JDK releases (JDK 21, 23, 25), Lombok's private compiler internals (`com.sun.tools.javac.code.TypeTag`) frequently crash during javac AST manipulation (`ExceptionInInitializerError`). Writing explicit standard Java POJOs (getters, setters, constructors, builders) guarantees 100% build compatibility on any Java version from JDK 17 to JDK 25+, makes bytecode completely transparent, and is easier for a fresher to explain in code walkthroughs.

---

### Q21: What is a Composite Unique Constraint in JPA?
**Answer:**
A unique constraint applied across multiple columns simultaneously. In our `Enrollment` entity:
```java
@Table(name = "enrollments", uniqueConstraints = {
    @UniqueConstraint(name = "uk_student_course", columnNames = {"student_id", "course_id"})
})
```
This guarantees that a student can only be enrolled in a particular course once at the database engine level, preventing race conditions or duplicate class entries.

---

### Q22: Why use `LocalDate` instead of `java.util.Date`?
**Answer:**
`java.util.Date` and `Calendar` (Java 1.0/1.1) are mutable, not thread-safe, and bundle time and timezone information when only a date is needed. Java 8 introduced the `java.time` package (`LocalDate`, `LocalDateTime`):
- `LocalDate` is immutable and thread-safe.
- Represents a calendar date (e.g. `2024-10-12`) without timezone distortion.
- Fully supported by JPA 2.2+ and Hibernate 6+ mapping directly to SQL `DATE`.

---

### Q23: How do `ModelMapper` configurations prevent mapping errors?
**Answer:**
In `ModelMapperConfig.java`, we configure:
```java
modelMapper.getConfiguration()
    .setMatchingStrategy(MatchingStrategies.STRICT)
    .setSkipNullEnabled(true);
```
- `STRICT` matching ensures source properties only map to destination properties with exact matching names and types, preventing ambiguous or partial matches.
- Null properties in requests do not accidentally overwrite valid database fields during update operations.

---

### Q24: What database indexes did you design for query optimization?
**Answer:**
1. Unique index on `users(email)`.
2. Unique index on `students(roll_number)`.
3. Unique composite index on `enrollments(student_id, course_id)`.
4. Unique composite index on `attendances(enrollment_id, attendance_date)`.
5. Foreign key indexes on `students(department_id)`, `courses(teacher_id)`, and `courses(department_id)` to speed up `JOIN` queries.

---

### Q25: How would you scale this system for 100,000 active students in production?
**Answer:**
1. **Caching:** Integrate Redis with Spring Cache (`@Cacheable`) on frequently read, slow-changing data (departments, course catalogs, teacher lists).
2. **Read/Write Replica Splitting:** Configure MySQL master-slave replication. Route write queries to the primary master and read queries to read replicas.
3. **Database Migration:** Integrate Flyway or Liquibase for version-controlled, zero-downtime database schema migrations.
4. **Asynchronous Messaging:** Offload email notification dispatching (e.g., enrollment confirmation, low attendance warnings) to RabbitMQ or Apache Kafka with Spring `@Async`.
5. **Containerization & Auto-scaling:** Package backend and frontend into Docker containers and deploy behind an Nginx reverse proxy or Kubernetes cluster with horizontal pod autoscaling (HPA).
