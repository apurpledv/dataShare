package com.openclassrooms.dataShare_api.unit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.dataShare_api.controller.UserController;
import com.openclassrooms.dataShare_api.dto.LoginDTO;
import com.openclassrooms.dataShare_api.model.DSFile;
import com.openclassrooms.dataShare_api.model.User;
import com.openclassrooms.dataShare_api.service.FileService;
import com.openclassrooms.dataShare_api.service.UserService;

import jakarta.persistence.EntityExistsException;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc
public class UserControllerTests {
    @Autowired
	private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private FileService fileService;

    @Test
    public void testUserController_Read() throws Exception {
        when(userService.getUser(anyLong())).thenReturn(new User());
        when(userService.getUsers()).thenReturn(new ArrayList<User>());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/user/9999")
                .with(jwt().jwt(jwt -> jwt.claim("userId", 9999L))))
            .andExpect(MockMvcResultMatchers.status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/users")
            .with(jwt()))
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testUserController_Register() throws Exception {
        when(userService.register(any(User.class))).thenReturn(new User());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/register")
                .with(jwt())
                .content(objectMapper.writeValueAsString(new User("testUser", "testUser")))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    @Test
    public void testUserController_Register_Invalid() throws Exception {
        // IllegalArgumentException
        when(userService.register(any(User.class))).thenThrow(new IllegalArgumentException());
        
        mockMvc.perform(MockMvcRequestBuilders.post("/api/register")
                .with(jwt())
                .content(objectMapper.writeValueAsString(new User("testUser", "testUser")))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isBadRequest());

        // EntityExistsException
        when(userService.register(any(User.class))).thenThrow(new EntityExistsException());
        
        mockMvc.perform(MockMvcRequestBuilders.post("/api/register")
                .with(jwt())
                .content(objectMapper.writeValueAsString(new User("testUser", "testUser")))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testUserController_Login() throws Exception {
        when(userService.getUser(anyString())).thenReturn(new User("testUser", "testUser"));
        when(userService.login(any(LoginDTO.class))).thenReturn("mockToken");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/login")
                .with(jwt())
                .content(objectMapper.writeValueAsString(new LoginDTO("testUser", "testUser")))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testUserController_Login_Invalid() throws Exception {
        LoginDTO mockLoginDTO = new LoginDTO("testUser", "testUser");

        // NoSuchElementException
        when(userService.getUser(anyString())).thenReturn(new User("testUser", "testUser"));
        when(userService.login(any(LoginDTO.class))).thenThrow(new NoSuchElementException());

        mockMvc.perform(MockMvcRequestBuilders.post("/api/login")
                .with(jwt())
                .content(objectMapper.writeValueAsString(mockLoginDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isNotFound());
        
        // BadCredentialsException
        when(userService.login(any(LoginDTO.class))).thenThrow(new BadCredentialsException(""));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/login")
                .with(jwt())
                .content(objectMapper.writeValueAsString(mockLoginDTO))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isUnauthorized());
    }

    @Test
    public void testUserController_Update() throws Exception {
        when(userService.updateUser(anyLong(), any(User.class))).thenReturn(new User());

        mockMvc.perform(MockMvcRequestBuilders.put("/api/user/9999")
                .with(jwt().jwt(jwt -> jwt.claim("userId", 9999L)))
                .content(objectMapper.writeValueAsString(new User("testUser", "testUser")))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testUserController_Update_Invalid() throws Exception {
        // NoSuchElementException
        when(userService.updateUser(anyLong(), any(User.class))).thenThrow(new NoSuchElementException());
        
        mockMvc.perform(MockMvcRequestBuilders.put("/api/user/9999")
                .with(jwt().jwt(jwt -> jwt.claim("userId", 9999L)))
                .content(objectMapper.writeValueAsString(new User("testUser", "testUser")))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testUserController_Delete() throws Exception {
        List<DSFile> filesList = new ArrayList<>();
            filesList.add(new DSFile());

        when(fileService.getFiles(anyLong())).thenReturn(filesList);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/user/9999")
            .with(jwt().jwt(jwt -> jwt.claim("userId", 9999L))))
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testUserController_Delete_Invalid() throws Exception {
        // NoSuchElementException
        doThrow(NoSuchElementException.class).when(userService).deleteUser(anyLong());
        
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/user/9999")
            .with(jwt().jwt(jwt -> jwt.claim("userId", 9999L))))
            .andExpect(MockMvcResultMatchers.status().isNotFound());
        
        // RuntimeException
        doThrow(RuntimeException.class).when(userService).deleteUser(anyLong());
        
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/user/9999")
            .with(jwt().jwt(jwt -> jwt.claim("userId", 9999L))))
            .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }
}
