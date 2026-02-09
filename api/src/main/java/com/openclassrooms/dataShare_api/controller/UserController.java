package com.openclassrooms.dataShare_api.controller;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.openclassrooms.dataShare_api.dto.LoginDTO;
import com.openclassrooms.dataShare_api.dto.TokenDTO;
import com.openclassrooms.dataShare_api.model.DSFile;
import com.openclassrooms.dataShare_api.model.User;
import com.openclassrooms.dataShare_api.service.FileService;
import com.openclassrooms.dataShare_api.service.UserService;

import jakarta.persistence.EntityExistsException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    FileService fileService;

    @GetMapping("/user/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        log.info("[GET] /api/user/" + id + " (" + HttpStatus.OK + ")");
        return ResponseEntity.ok(userService.getUser(id));
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers() {
        log.info("[GET] /api/users (" + HttpStatus.OK + ")");
        return ResponseEntity.ok(userService.getUsers());
    }

    @PutMapping("/user/{id}")
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

    @DeleteMapping("/user/{id}")
    public ResponseEntity<HttpStatusCode> deleteUser(@PathVariable Long id) {
        try {
            //To delete the User, we have to first delete his files
            for (DSFile file : fileService.getFiles(id))
                fileService.deleteFile(file.getId());

            //If all his files are purged, then delete him
            userService.deleteUser(id);

            log.info("[DELETE] /api/user (" + HttpStatus.OK + ")");
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NoSuchElementException e) {
            log.error("[DELETE] /api/user (" + HttpStatus.NOT_FOUND + "): " + e.toString());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (RuntimeException e) {
            log.error("[DELETE] /api/user (" + HttpStatus.INTERNAL_SERVER_ERROR + "): " + e.toString());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            log.error("[DELETE] /api/user (" + HttpStatus.INTERNAL_SERVER_ERROR + "): " + e.toString());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody LoginDTO loginDTO) {
        try {
            String jwt = userService.login(loginDTO);
            log.info("[POST] /api/login (" + HttpStatus.OK + ")");
            return ResponseEntity.ok(new TokenDTO(jwt));
        } catch (NoSuchElementException e) {
            log.error("[POST] /api/login (" + HttpStatus.NOT_FOUND + "): " + e.toString());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (BadCredentialsException e) {
            log.error("[POST] /api/login (" + HttpStatus.NOT_FOUND + "): " + e.toString());
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            log.error("[POST] /api/login (" + HttpStatus.INTERNAL_SERVER_ERROR + "): " + e.toString());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<HttpStatusCode> register(@Validated @RequestBody User user) {
        try {
            userService.register(user);
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
}
