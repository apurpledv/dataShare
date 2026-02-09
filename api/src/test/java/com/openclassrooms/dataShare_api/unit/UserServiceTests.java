package com.openclassrooms.dataShare_api.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.openclassrooms.dataShare_api.dto.LoginDTO;
import com.openclassrooms.dataShare_api.model.User;
import com.openclassrooms.dataShare_api.repository.UserRepository;
import com.openclassrooms.dataShare_api.service.JwtService;
import com.openclassrooms.dataShare_api.service.UserService;

import jakarta.persistence.EntityExistsException;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {
    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    JwtService jwtService;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    UserService userService;

    private User mockUser;
    
    @BeforeEach
    public void setup() {
        mockUser = new User("testUser", "testUser");
        mockUser.setId(0L);
    }

    @Test
    public void testUserService_Read() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(mockUser));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));

        assertTrue(userService.getUsers() instanceof List<User>);

        // using id
        assertTrue(userService.getUser(0L) instanceof User);

        // using email
        assertTrue(userService.getUser(mockUser.getEmail()) instanceof User);
    }

    @Test
    public void testUserService_Update() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        assertTrue(userService.updateUser(0L, mockUser) instanceof User);
    }

    @Test
    public void testUserService_Update_Invalid() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> { 
            userService.updateUser(0L, mockUser); 
        });
    }

    @Test
    public void testUserService_Delete() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(mockUser));
        userService.deleteUser(0L);
        verify(userRepository).delete(any(User.class));
    }

    @Test
    public void testUserService_Delete_Invalid() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> { 
            userService.deleteUser(0L); 
        });
    }

    @Test
    public void testUserService_Register() {
        User mockReturnUser = mockUser;
        mockReturnUser.setPassword("notTestUser");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(eq(mockUser))).thenReturn(mockReturnUser);
        when(passwordEncoder.encode(anyString())).thenReturn(mockReturnUser.getPassword());
        
        User user = userService.register(mockUser);
        assertTrue(user instanceof User);
        assertNotEquals("testUser", user.getPassword());
    }

    @Test
    public void testUserService_Register_Invalid() {
        // null, null
        mockUser = new User("", "");
        assertThrows(IllegalArgumentException.class, () -> { 
            userService.register(mockUser);
        });

        // email, null
        mockUser.setEmail("notNull");
        assertThrows(IllegalArgumentException.class, () -> { 
            userService.register(mockUser); 
        });

        // email, password BUT already exists
        mockUser.setPassword("notNull");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));
        assertThrows(EntityExistsException.class, () -> { 
            userService.register(mockUser); 
        });
    }

    @Test
    public void testUserService_Login() {
        LoginDTO mockLoginDTO = new LoginDTO();
        mockLoginDTO.setEmail(mockUser.getEmail());
        mockLoginDTO.setPassword(mockUser.getPassword());

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(any(CharSequence.class), anyString())).thenReturn(true);
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("mockToken");

        String mockToken = userService.login(mockLoginDTO);
        assertEquals("mockToken", mockToken);
    }
   
    @Test
    public void testUserService_Login_Invalid() {
        LoginDTO mockLoginDTO = new LoginDTO();
        mockLoginDTO.setEmail(mockUser.getEmail());
        mockLoginDTO.setPassword(mockUser.getPassword());

        // user not found
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> userService.login(mockLoginDTO));

        // wrong password
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(any(CharSequence.class), anyString())).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> userService.login(mockLoginDTO));
    }
}
