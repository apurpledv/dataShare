package com.openclassrooms.dataShare_api.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.dataShare_api.model.DSFile;
import com.openclassrooms.dataShare_api.model.User;
import com.openclassrooms.dataShare_api.repository.FileRepository;
import com.openclassrooms.dataShare_api.repository.UserRepository;
import com.openclassrooms.dataShare_api.service.FileService;
import com.openclassrooms.dataShare_api.service.JwtService;
import com.openclassrooms.dataShare_api.service.UserService;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = true)
public class FileIT {
    @Autowired private MockMvc mockMvc;
    @Autowired private UserService userService;
    @Autowired private FileService fileService;
    @Autowired private UserRepository userRepository;
    @Autowired private FileRepository fileRepository;
    @Autowired private JwtService jwtService;
    @Autowired private PasswordEncoder passwordEncoder;

    @Value("${jwt.secret.key}")
    private String SECRET;
    private String TOKEN;
    private Long USERID;
    private DSFile DSFILE;
    private String EXPIRATIONDAYS;
    
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
        USERID = userService.register(new User("EMAIL", "PASSWORD")).getId();
        EXPIRATIONDAYS = "7";
        DSFILE = new DSFile();
            DSFILE.setOwnerId(USERID);
            DSFILE.setPath("path/to/file.txt");

        // for auth-needed endpoint testings, make a custom one
        TOKEN = jwtService.generateToken(org.springframework.security.core.userdetails.User.builder()
            .username("EMAIL")
            .password(passwordEncoder.encode("PASSWORD"))
            .build(), USERID);
    }

    @AfterEach
    public void afterEach() {
        userRepository.deleteAll();
        fileRepository.deleteAll();
    }

    @Test
    public void testFileIT_GetAllFilesOfUser() throws Exception {
        String fileName = "filename.txt";
        MockMultipartFile mockFile = new MockMultipartFile("data.txt", fileName, "text/plain", "Some text".getBytes());

        fileService.store(mockFile, String.valueOf(USERID), 7L);

        String responseGet = mockMvc.perform(MockMvcRequestBuilders.get("/api/file/list/" + USERID)
                .header("Authorization", "Bearer " + TOKEN))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn().getResponse().getContentAsString();
        
        assertEquals(fileName, objectMapper.readTree(responseGet).path(0).get("name").asText());

        // clean-up physical file
        fileService.deleteFile(fileRepository.findAllByOwnerId(USERID).get(0).getId());
    }

    @Test
    public void testFileIT_GetAllFilesOfUser_Unauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/file/list/" + USERID))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void testFileIT_Upload() throws Exception {
        String fileName = "filename.txt";
        MockMultipartFile mockFile = new MockMultipartFile("data.txt", fileName, "text/plain", "Some text".getBytes());
        System.out.println(mockFile.getOriginalFilename());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/file/upload")
                .header("Authorization", "Bearer " + TOKEN)
                .file("file", mockFile.getBytes())
                .param("expirationDays", String.valueOf(EXPIRATIONDAYS))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk());

        assertFalse(fileRepository.findAllByOwnerId(USERID).isEmpty());

        // clean-up physical file
        fileService.deleteFile(fileRepository.findAllByOwnerId(USERID).get(0).getId());
    }

    @Test
    public void testFileIT_Upload_Unauthorized() throws Exception {
        String fileName = "filename.txt";
        MockMultipartFile mockFile = new MockMultipartFile("data.txt", fileName, "text/plain", "Some text".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/file/upload")
                .file("file", mockFile.getBytes())
                .param("userId", String.valueOf(USERID))
                .param("expirationDays", String.valueOf(EXPIRATIONDAYS))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void testFileIT_Download() throws Exception {
        String fileName = "filename.txt";
        MockMultipartFile mockFile = new MockMultipartFile("data.txt", fileName, "text/plain", "Some text".getBytes());
        String fileToken = fileService.store(mockFile, String.valueOf(USERID), 7L);

        String downloadedFileContent = mockMvc.perform(MockMvcRequestBuilders.get("/api/file/download/" + fileToken)
                .header("Authorization", "Bearer " + TOKEN))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andReturn().getResponse().getContentAsString();

        String mockFileContent = new String(mockFile.getBytes());
        assertEquals(mockFileContent, downloadedFileContent);

        // clean-up physical file
        fileService.deleteFile(fileRepository.findAllByOwnerId(USERID).get(0).getId());
    }

    @Test
    public void testFileIT_Download_Unauthorized() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/file/download/" + "fileToken"))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void testFileIT_Delete() throws Exception {
        String fileName = "filename.txt";
        MockMultipartFile mockFile = new MockMultipartFile("data.txt", fileName, "text/plain", "Some text".getBytes());

        fileService.store(mockFile, String.valueOf(USERID), 7L);
        Long fileId = fileRepository.findAllByOwnerId(USERID).get(0).getId();

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/file/" + fileId)
                .header("Authorization", "Bearer " + TOKEN))
            .andExpect(MockMvcResultMatchers.status().isOk());

        assertTrue(fileRepository.findById(fileId).isEmpty());
    }
    
    @Test
    public void testFileIT_Delete_Unauthorized() throws Exception {
        Long fileId = 0L;

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/file/" + fileId))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }
}
