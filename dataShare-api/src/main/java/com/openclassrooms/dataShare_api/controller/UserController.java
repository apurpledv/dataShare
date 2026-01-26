package com.openclassrooms.dataShare_api.controller;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.openclassrooms.dataShare_api.model.User;
import com.openclassrooms.dataShare_api.service.UserService;

import jakarta.persistence.EntityExistsException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping("/api/user/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        log.info("[GET] /api/user/" + id + " (" + HttpStatus.OK + ")");
        return ResponseEntity.ok(userService.getUser(id));
    }

    @GetMapping("/api/users")
    public ResponseEntity<List<User>> getUsers() {
        log.info("[GET] /api/users (" + HttpStatus.OK + ")");
        return ResponseEntity.ok(userService.getUsers());
    }

    @PostMapping("/api/user")
    public ResponseEntity<HttpStatusCode> addUser(@Validated @RequestBody User user) {
        try {
            //user.setId(null);
            userService.addUser(user);
            log.info("[POST] /api/user (" + HttpStatus.CREATED + ")");
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            log.error("[POST] /api/user (" + HttpStatus.BAD_REQUEST + "): " + e.toString());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (EntityExistsException e) {
            log.error("[POST] /api/user (" + HttpStatus.BAD_REQUEST + "): " + e.toString());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("[POST] /api/user (" + HttpStatus.INTERNAL_SERVER_ERROR + "): " + e.toString());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/api/user/{id}")
    public ResponseEntity<HttpStatusCode> updateUser(@PathVariable Long id, @Validated @RequestBody User user) {
        try {
            userService.updateUser(id, user);
            log.info("[PUT] /api/user (" + HttpStatus.OK + ")");
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NoSuchElementException e) {
            log.error("[PUT] /api/user (" + HttpStatus.NOT_FOUND + "): " + e.toString());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("[PUT] /api/user (" + HttpStatus.INTERNAL_SERVER_ERROR + "): " + e.toString());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/api/user/{id}")
    public ResponseEntity<HttpStatusCode> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            log.info("[DELETE] /api/user (" + HttpStatus.OK + ")");
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NoSuchElementException e) {
            log.error("[DELETE] /api/user (" + HttpStatus.NOT_FOUND + "): " + e.toString());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("[DELETE] /api/user (" + HttpStatus.INTERNAL_SERVER_ERROR + "): " + e.toString());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
