package com.openclassrooms.dataShare_api.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.openclassrooms.dataShare_api.model.User;
import com.openclassrooms.dataShare_api.repository.UserRepository;

import jakarta.persistence.EntityExistsException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserService {
    @Autowired
    UserRepository userRepository;

    public User getUser(Long id) {
        return userRepository.findById(id).get();
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User addUser(User user) throws IllegalArgumentException, EntityExistsException {
        if (user.getEmail().isBlank() || user.getPassword().isBlank())
            throw new IllegalArgumentException("Email or Password is invalid.");

        if (userRepository.findByEmail(user.getEmail()).isPresent())
            throw new EntityExistsException("User already exists.");

        return userRepository.save(user);
    }

    public User updateUser(Long userId, User user) throws NoSuchElementException {
        Optional<User> studentFound = userRepository.findById(userId);
        if (studentFound.isEmpty())
            throw new NoSuchElementException("User not found.");

        User updatedUser = studentFound.get();
        updatedUser.setEmail(user.getEmail());
        updatedUser.setPassword(user.getPassword());

        return userRepository.save(updatedUser);
    }

    public void deleteUser(Long userId) throws NoSuchElementException {
        Optional<User> studentFound = userRepository.findById(userId);
        if (studentFound.isEmpty())
            throw new NoSuchElementException("User not found.");

        userRepository.delete(studentFound.get());
    }
}
