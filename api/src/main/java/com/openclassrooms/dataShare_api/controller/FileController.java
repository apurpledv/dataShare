package com.openclassrooms.dataShare_api.controller;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.openclassrooms.dataShare_api.dto.DSFileDTO;
import com.openclassrooms.dataShare_api.service.FileService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;

/**
 * FileController is an Entity that handles incoming HTTP Requests targeting Files
 */
@Slf4j
@RestController
@RequestMapping("/api/file")
public class FileController {
    @Autowired
    private FileService fileService;

    /**
     * Attempts to upload a MultipartFile into the app
     * @param file the file to upload
     * @param userId the file's owner
     * @param expirationDays how many days til it is expired (ie: 1 day = tomorrow)
     * @return [the stored file's unique identifier, 200 OK]; 400 BAD_REQUEST if an error occurred during the file's upload into the system; 500 INTERNAL_SERVER_ERROR otherwise
     */
    @Operation(
        summary = "Uploads a File"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "File uploaded"),
        @ApiResponse(responseCode = "400", description = "File incorrectly sent"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "500", description = "Incorrect JWT provided")
    })
    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file, @RequestParam("expirationDays") Long expirationDays, @AuthenticationPrincipal Jwt jwt) {
        Long userId = jwt.getClaim("userId");
        
        try {
            String storedFilename = fileService.store(file, String.valueOf(userId), expirationDays);
            log.info("[POST] /api/file/upload" + " [" + HttpStatus.OK +"]");
            return ResponseEntity.ok(Map.of("filename", storedFilename));
        } catch (RuntimeException e) {
            log.error("[POST] /api/file/upload" + " [" + HttpStatus.BAD_REQUEST +"] " + e.toString());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("[POST] /api/file/upload" + " [" + HttpStatus.INTERNAL_SERVER_ERROR +"] " + e.toString());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Attempts to download a file from the server
     * @param userId the owner's id used to search only their specific folder
     * @param filename the file's unique identifier
     * @return the fetched File as a downloadable Resource
     */
    @Operation(
        summary = "Downloads a File"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "File download should start"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "File not found (or found but doesn't belong to User)"),
        @ApiResponse(responseCode = "500", description = "Incorrect JWT provided")
    })
    @GetMapping("/download/{filename}")
    public ResponseEntity<Resource> download(@PathVariable String filename, @AuthenticationPrincipal Jwt jwt) {
        Long userId = jwt.getClaim("userId");

        try {
            Resource file = fileService.loadAsResource(userId, filename);
            log.info("[GET] /api/file/download/" + userId + "/" + filename + " [" + HttpStatus.OK +"]");
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
        } catch (RuntimeException e) {
            log.error("[GET] /api/file/download/" + userId + "/" + filename + " [" + HttpStatus.NOT_FOUND +"] " + e.toString());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("[GET] /api/file/download/" + userId + "/" + filename + " [" + HttpStatus.INTERNAL_SERVER_ERROR +"] " + e.toString());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Fetches a file's metadata as a DTO
     * @param fileId
     * @return the file's metadata as a DTO
     */
    @Operation(
        summary = "Fetches a File's metadata"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "File metadata found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "File not found (or found but doesn't belong to User)"),
        @ApiResponse(responseCode = "500", description = "Incorrect JWT provided")
    })
    @GetMapping("/{fileId}")
    public ResponseEntity<DSFileDTO> getFile(@PathVariable Long fileId, @AuthenticationPrincipal Jwt jwt) {
        Long ownerId = jwt.getClaim("userId");

        try {
            DSFileDTO fileData = fileService.getFileDTO(fileId, ownerId);
            log.info("[GET] /api/file/" + fileId + " [" + HttpStatus.OK +"]");
            return ResponseEntity.ok(fileData);
        } catch (Exception e) {
            log.error("[GET] /api/file/" + fileId + " [" + HttpStatus.INTERNAL_SERVER_ERROR +"] " + e.toString());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Fetches a the metadata of every file belonging to a given user as DTOs
     * @param userId
     * @return the metadata of every file belonging to a given user as DTOs
     */
    @Operation(
        summary = "Fetches the metadata of every File belonging to a given User"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Files' metadata found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "500", description = "Incorrect JWT provided")
    })
    @GetMapping("/list/{userId}")
    public ResponseEntity<List<DSFileDTO>> listFiles(@PathVariable Long userId, @AuthenticationPrincipal Jwt jwt) {
        Long ownerId = jwt.getClaim("userId");

        try {
            if (!userId.equals(ownerId))
                throw new Exception("Incorrect user token provided.");

            List<DSFileDTO> filesList = fileService.getFilesDTO(userId);
            log.info("[GET] /api/file/list/" + userId + " [" + HttpStatus.OK +"]");
            return ResponseEntity.ok(filesList);
        } catch (Exception e) {
            log.error("[GET] /api/file/list/" + userId + " [" + HttpStatus.INTERNAL_SERVER_ERROR +"] " + e.toString());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Attempts to delete a given file from the db and the system
     * @param fileId
     * @return 200 OK if successful; 404 NOT_FOUND if the file is not found; 500 INTERNAL_SERVER_ERROR if an error occurred during the physical file's deletion
     */
    @Operation(
        summary = "Deletes a File"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "File deleted"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "404", description = "File not found (or found but doesn't belong to User)"),
        @ApiResponse(responseCode = "500", description = "Incorrect JWT provided")
    })
    @DeleteMapping("/{fileId}")
    public ResponseEntity<HttpStatusCode> deleteFile(@PathVariable Long fileId, @AuthenticationPrincipal Jwt jwt) {
        Long ownerId = jwt.getClaim("userId");

        try {
            if (!ownerId.equals(fileService.getFileDTO(fileId, ownerId).getOwnerId()))
                throw new Exception("You are not allowed to delete this file.");

            fileService.deleteFile(fileId);
            log.info("[DELETE] /api/file/" + fileId + " (" + HttpStatus.OK + ")");
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NoSuchElementException e) {
            log.error("[DELETE] /api/file/" + fileId + " (" + HttpStatus.NOT_FOUND + "): " + e.toString());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (RuntimeException e) {
            log.error("[DELETE] /api/file/" + fileId + " (" + HttpStatus.INTERNAL_SERVER_ERROR + "): " + e.toString());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            log.error("[DELETE] /api/file/" + fileId + " (" + HttpStatus.INTERNAL_SERVER_ERROR + "): " + e.toString());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
