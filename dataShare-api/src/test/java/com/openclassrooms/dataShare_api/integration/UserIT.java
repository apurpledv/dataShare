package com.openclassrooms.dataShare_api.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.dataShare_api.dto.LoginDTO;
import com.openclassrooms.dataShare_api.model.User;
import com.openclassrooms.dataShare_api.repository.UserRepository;
import com.openclassrooms.dataShare_api.service.JwtService;
import com.openclassrooms.dataShare_api.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = true)
public class UserIT {
    @Autowired private MockMvc mockMvc;
    @Autowired private UserService userService;
    @Autowired private UserRepository userRepository;
    @Autowired private JwtService jwtService;
    @Autowired private PasswordEncoder passwordEncoder;

    @Value("${jwt.secret.key}")
    private String SECRET;
    private String TOKEN;
    private final String EMAIL = "email";
    private final String PASSWORD = "password";
    private User USER;

    static final private PostgreSQLContainer postgres = new PostgreSQLContainer(DockerImageName.parse("postgres:17"));
    private final ObjectMapper objectMapper = new ObjectMapper();

	@DynamicPropertySource
    static void configureTestProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> postgres.getJdbcUrl());
        registry.add("spring.datasource.username", () -> postgres.getUsername());
        registry.add("spring.datasource.password", () -> postgres.getPassword());
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create");

		postgres.start();
    }

    @BeforeEach
    public void setup() {
        // for auth-needed endpoint testings, make a custom one
        TOKEN = jwtService.generateToken(org.springframework.security.core.userdetails.User.builder()
            .username(EMAIL)
            .password(passwordEncoder.encode(PASSWORD))
            .build());
        
        USER = new User(EMAIL, PASSWORD);
    }

    @AfterEach
    public void afterEach() {
        userRepository.deleteAll();
    }

    @Test
    public void testUserIT_Read() throws Exception {
        // Save a test User into DB
        USER = userService.register(USER);
        Long userId = USER.getId();

        // TEST: Get our test User
        String responseGet = mockMvc.perform(MockMvcRequestBuilders.get("/api/user/" + userId)
                .header("Authorization", "Bearer " + TOKEN))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn().getResponse().getContentAsString();

        assertEquals(EMAIL, objectMapper.readTree(responseGet).get("email").asText());

        // TEST: Get all users (so technically just our User)
        String responseGetAll = mockMvc.perform(MockMvcRequestBuilders.get("/api/users")
                .header("Authorization", "Bearer " + TOKEN))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn().getResponse().getContentAsString();
        
        assertEquals(EMAIL, objectMapper.readTree(responseGetAll).path(0).get("email").asText());
    }

    @Test
    public void testUserIT_Read_Unauthorized() throws Exception {
        // TEST: Get one
        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/" + 0L))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized());

        // TEST: Get all
        mockMvc.perform(MockMvcRequestBuilders.get("/api/users"))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void testUserIT_Register() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/register")
                .content(objectMapper.writeValueAsString(USER))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isCreated());
        
        assertNotNull(userRepository.findByEmail(EMAIL));
        assertNotEquals(USER.getPassword(), userRepository.findByEmail(EMAIL).get().getPassword());
    }

    @Test
    public void testUserIT_Login() throws Exception {
        LoginDTO loginDTO = new LoginDTO(EMAIL, PASSWORD);

        // Save a test User into DB
        USER = userService.register(USER);

        String response = mockMvc.perform(MockMvcRequestBuilders.post("/api/login")
                .content(objectMapper.writeValueAsString(loginDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn().getResponse().getContentAsString();
        
        assertNotNull(objectMapper.readTree(response).get("token"));
    }

    @Test
    public void testUserIT_Update() throws Exception {
        User updatedUser = new User("newEmail", PASSWORD);

        // Save a test User into DB
        USER = userService.register(USER);
        Long userId = USER.getId();

        mockMvc.perform(MockMvcRequestBuilders.put("/api/user/" + userId)
                .header("Authorization", "Bearer " + TOKEN)
                .content(objectMapper.writeValueAsString(updatedUser))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk());
        
        assertEquals(updatedUser.getEmail(), userRepository.findById(userId).get().getEmail());
    }

    @Test
    public void testUserIT_Update_Unauthorized() throws Exception {
        User updatedUser = new User("newEmail", PASSWORD);

        // No token
        mockMvc.perform(MockMvcRequestBuilders.put("/api/user/" + 0L)
                .content(objectMapper.writeValueAsString(updatedUser))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void testUserIT_Delete() throws Exception {
        // Save a test User into DB
        USER = userService.register(USER);
        Long userId = USER.getId();

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/user/" + userId)
                .header("Authorization", "Bearer " + TOKEN))
            .andExpect(MockMvcResultMatchers.status().isOk());
        
        assertTrue(userRepository.findById(userId).isEmpty());
    }

    @Test
    public void testUserIT_Delete_Unauthorized() throws Exception {
        // No token
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/user/" + 0L))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
}
