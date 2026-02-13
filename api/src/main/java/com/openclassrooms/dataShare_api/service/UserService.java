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

/**
 * UserService is an Entity that handles basic logic to manage User Entities (CRUD)
 */
@Service
@Transactional
public class UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtService jwtService;

    /**
     * Fetches a single user using his id
     * @param id
     * @return the user
     */
    public User getUser(Long id) {
        return userRepository.findById(id).get();
    }

    /**
     * Fetches a single user using his email
     * @param email
     * @return the user
     */
    public User getUser(String email) {
        return userRepository.findByEmail(email).get();
    }

    /**
     * Fetches every user from the db
     * @return a list of every user
     */
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    /**
     * Updates a given user with new data
     * @param userId
     * @param user new user data
     * @return the updated user
     * @throws NoSuchElementException if the user is not found
     */
    public User updateUser(Long userId, User user) throws NoSuchElementException {
        Optional<User> userFound = userRepository.findById(userId);
        if (userFound.isEmpty())
            throw new NoSuchElementException("User not found.");

        User updatedUser = userFound.get();
        updatedUser.setEmail(user.getEmail());
        updatedUser.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(updatedUser);
    }

    /**
     * Deletes a given user
     * @param userId
     * @throws NoSuchElementException if the user is not found
     */
    public void deleteUser(Long userId) throws NoSuchElementException {
        Optional<User> userFound = userRepository.findById(userId);
        if (userFound.isEmpty())
            throw new NoSuchElementException("User not found.");

        userRepository.delete(userFound.get());
    }

    /**
     * Attempts to log in a user, using the provided email and password
     * @param loginDto the login details (email + password)
     * @return a valid Jwt if successful
     * @throws NoSuchElementException if the user doesn't exist (email not found)
     * @throws BadCredentialsException if the user's password doesn't match the one in the db
     */
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

    /**
     * Attempts to register a new user
     * @param user the user data (email + password)
     * @return the registered user, if successful
     * @throws IllegalArgumentException if the email or password is not valid
     * @throws EntityExistsException if a user with this email already exists
     */
    public User register(User user) throws IllegalArgumentException, EntityExistsException {
        if (user.getEmail().isBlank() || user.getPassword().isBlank())
            throw new IllegalArgumentException("Email or Password is invalid.");

        if (userRepository.findByEmail(user.getEmail()).isPresent())
            throw new EntityExistsException("User already exists.");

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }
}
