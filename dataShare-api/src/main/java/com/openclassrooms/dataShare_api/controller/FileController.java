package com.openclassrooms.dataShare_api.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.openclassrooms.dataShare_api.dto.DSFileDTO;
import com.openclassrooms.dataShare_api.service.FileService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/file")
public class FileController {
    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file, @RequestParam("userId") String userId) {
        try {
            String storedFilename = fileService.store(file, userId);
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

    @GetMapping("/download/{userId}/{filename}")
    public ResponseEntity<Resource> download(@PathVariable Long userId, @PathVariable String filename) {
        try {
            Resource file = fileService.loadAsResource(userId, filename);
            log.info("[GET] /api/file/download/" + userId + "/" + filename + " [" + HttpStatus.OK +"]");
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                .body(file);
        } catch (RuntimeException e) {
            log.error("[GET] /api/file/download/" + userId + "/" + filename + " [" + HttpStatus.BAD_REQUEST +"] " + e.toString());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            log.error("[GET] /api/file/download/" + userId + "/" + filename + " [" + HttpStatus.INTERNAL_SERVER_ERROR +"] " + e.toString());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/list/{userId}")
    public ResponseEntity<List<DSFileDTO>> listFiles(@PathVariable Long userId) {
        try {
            List<DSFileDTO> filesList = fileService.getFiles(userId);
            log.info("[GET] /api/file/list/" + userId + " [" + HttpStatus.OK +"]");
            return ResponseEntity.ok(filesList);
        } catch (Exception e) {
            log.error("[GET] /api/file/list/" + userId + " [" + HttpStatus.INTERNAL_SERVER_ERROR +"] " + e.toString());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
