package com.openclassrooms.dataShare_api.controller;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.persistence.EntityExistsException;
import lombok.extern.slf4j.Slf4j;

/**
 * UserController is an Entity that handles incoming HTTP Requests targeting Users
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    FileService fileService;

    /**
     * Fetches a given User
     * @param id
     * @return [the User, 200 OK]
     */
    @Operation(
        summary = "Fetches a given User"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User found"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "500", description = "Incorrect JWT provided")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/user/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        Long ownerId = jwt.getClaim("userId");

        try {
            if (!id.equals(ownerId))
                throw new Exception("Incorrect user token provided.");

            log.info("[GET] /api/user/" + id + " (" + HttpStatus.OK + ")");
            return ResponseEntity.ok(userService.getUser(id));
        } catch (Exception e) {
            log.error("[GET] /api/user/" + id + "(" + HttpStatus.INTERNAL_SERVER_ERROR + "): " + e.toString());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Fetches every User
     * @return [a list of every User, 200 OK]
     */
    @Operation(
        summary = "Fetches every registered User"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Users found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @SecurityRequirement(name = "bearerAuth")
    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers() {
        log.info("[GET] /api/users (" + HttpStatus.OK + ")");
        return ResponseEntity.ok(userService.getUsers());
    }

    /**
     * Updates a given User
     * @param id the updated User'd id
     * @param user the new data
     * @return 200 OK if successful; 404 NOT_FOUND if the User is not found; 500 INTERNAL_SERVER_ERROR otherwise
     */
    @Operation(
        summary = "Updates a given User"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User updated"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "500", description = "Incorrect JWT provided")
    })
    @SecurityRequirement(name = "bearerAuth")
    @PutMapping("/user/{id}")
    public ResponseEntity<HttpStatusCode> updateUser(@PathVariable Long id, @Validated @RequestBody User user, @AuthenticationPrincipal Jwt jwt) {
        Long ownerId = jwt.getClaim("userId");
        
        try {
            if (!id.equals(ownerId))
                throw new Exception("Incorrect user token provided");

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

    /**
     * Deletes a given User
     * @param id
     * @return 200 OK if successful; 404 NOT_FOUND if the User is not found; 500 INTERNAL_SERVER_ERROR if another error occurred (for instance, during the User's files deletion process)
     */
    @Operation(
        summary = "Deletes a given User"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User deleted"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "500", description = "Incorrect JWT provided; Error during a User's Files deletion process")
    })
    @SecurityRequirement(name = "bearerAuth")
    @DeleteMapping("/user/{id}")
    public ResponseEntity<HttpStatusCode> deleteUser(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        Long ownerId = jwt.getClaim("userId");

        try {
            if (!id.equals(ownerId))
                throw new Exception("Incorrect user token provided");

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

    /**
     * Attempts to log a User in
     * @param loginDTO the login information (email + password)
     * @return [a valid Jwt, 200 OK]; 404 NOT_FOUND if the User is not found; 401 UNAUTHORIZED if the password is incorrect; 500 INTERNAL_SERVER_ERROR otherwise
     */
    @Operation(
        summary = "Logs in a given User"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Credentials validated"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "401", description = "Credentials invalid")
    })
    @PostMapping("/login")
    public ResponseEntity<TokenDTO> login(@RequestBody LoginDTO loginDTO) {
        try {
            String jwt = userService.login(loginDTO);
            Long userId = userService.getUser(loginDTO.getEmail()).getId();
            log.info("[POST] /api/login (" + HttpStatus.OK + ")");
            return ResponseEntity.ok(new TokenDTO(jwt, userId));
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

    /**
     * Attempts to register a User
     * @param user the register information (email + password)
     * @return 201 CREATED if successful; 400 BAD_REQUEST if a user with that email already exists or the provided information is invalid; 500 INTERNAL_SERVER_ERROR otherwise
     */
    @Operation(
        summary = "Registers a new User"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Account created"),
        @ApiResponse(responseCode = "400", description = "Email/password invalid; Account with same email already exists"),
        @ApiResponse(responseCode = "500", description = "Incorrect JWT provided")
    })
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
