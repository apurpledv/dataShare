package com.openclassrooms.dataShare_api.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.openclassrooms.dataShare_api.dto.LoginDTO;
import com.openclassrooms.dataShare_api.model.User;
import com.openclassrooms.dataShare_api.repository.UserRepository;

import jakarta.persistence.EntityExistsException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtService jwtService;

    public User getUser(Long id) {
        return userRepository.findById(id).get();
    }

    public User getUser(String email) {
        return userRepository.findByEmail(email).get();
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User updateUser(Long userId, User user) throws NoSuchElementException {
        Optional<User> userFound = userRepository.findById(userId);
        if (userFound.isEmpty())
            throw new NoSuchElementException("User not found.");

        User updatedUser = userFound.get();
        updatedUser.setEmail(user.getEmail());
        updatedUser.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(updatedUser);
    }

    public void deleteUser(Long userId) throws NoSuchElementException {
        Optional<User> userFound = userRepository.findById(userId);
        if (userFound.isEmpty())
            throw new NoSuchElementException("User not found.");

        userRepository.delete(userFound.get());
    }

    public String login(LoginDTO loginDto) throws NoSuchElementException, BadCredentialsException {
        Optional<User> userFound = userRepository.findByEmail(loginDto.getEmail());
        if (userFound.isEmpty())
            throw new NoSuchElementException("Account doesn't exist.");
        
        if (passwordEncoder.matches(loginDto.getPassword(), userFound.get().getPassword())) {
            UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(loginDto.getEmail())
                .password(passwordEncoder.encode(loginDto.getPassword()))
                .build();

            return jwtService.generateToken(userDetails);
        } else {
            throw new BadCredentialsException("Email or password is incorrect.");
        }
    }

    public User register(User user) throws IllegalArgumentException, EntityExistsException {
        if (user.getEmail().isBlank() || user.getPassword().isBlank())
            throw new IllegalArgumentException("Email or Password is invalid.");

        if (userRepository.findByEmail(user.getEmail()).isPresent())
            throw new EntityExistsException("User already exists.");

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }
}
