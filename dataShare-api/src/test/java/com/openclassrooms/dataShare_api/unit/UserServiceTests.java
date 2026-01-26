package com.openclassrooms.dataShare_api.unit;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
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

import com.openclassrooms.dataShare_api.model.User;
import com.openclassrooms.dataShare_api.repository.UserRepository;
import com.openclassrooms.dataShare_api.service.UserService;

import jakarta.persistence.EntityExistsException;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {
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
    public void testUserService_Create() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        assertTrue(userService.addUser(mockUser) instanceof User);
    }

    @Test
    public void testUserService_Read() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(mockUser));
        assertTrue(userService.getUser(0L) instanceof User);
        assertTrue(userService.getUsers() instanceof List<User>);
    }

    @Test
    public void testUserService_Update() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        assertTrue(userService.updateUser(0L, mockUser) instanceof User);
    }

    @Test
    public void testUserService_Delete() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(mockUser));
        userService.deleteUser(0L);
        verify(userRepository).delete(any(User.class));
    }

    @Test
    public void testUserService_Create_Invalid() {
        // null, null
        mockUser = new User("", "");
        assertThrows(IllegalArgumentException.class, () -> { 
            userService.addUser(mockUser);
        });

        // email, null
        mockUser.setEmail("notNull");
        assertThrows(IllegalArgumentException.class, () -> { 
            userService.addUser(mockUser); 
        });

        // email, password BUT already exists
        mockUser.setPassword("notNull");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(mockUser));
        assertThrows(EntityExistsException.class, () -> { 
            userService.addUser(mockUser); 
        });
    }

    @Test
    public void testUserService_Update_Invalid() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> { 
            userService.updateUser(0L, mockUser); 
        });
    }

    @Test
    public void testUserService_Delete_Invalid() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> { 
            userService.deleteUser(0L); 
        });
    }
}
