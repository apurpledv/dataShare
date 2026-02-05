package com.openclassrooms.dataShare_api.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.openclassrooms.dataShare_api.config.StorageProperties;
import com.openclassrooms.dataShare_api.dto.DSFileDTO;
import com.openclassrooms.dataShare_api.model.DSFile;
import com.openclassrooms.dataShare_api.repository.FileRepository;

@Service
public class FileService {
    @Autowired
    FileRepository fileRepository;

    private final Path rootLocation;

    public FileService(StorageProperties properties) {
        this.rootLocation = properties.getLocation();
    }

    public String store(MultipartFile file, String userId) throws RuntimeException {
        if (file.isEmpty()) {
            throw new RuntimeException("Cannot store empty file");
        }

        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path userDir = rootLocation.resolve(userId);

        try {
            //Upload file
            Files.createDirectories(userDir);
            Path destinationFile = userDir.resolve(filename).normalize();

            Files.copy(file.getInputStream(), destinationFile);

            //Extract metadata and store it
            File uploadedFile = destinationFile.toFile();

            DSFile dsFile = new DSFile();
                dsFile.setPath(uploadedFile.getName());
                dsFile.setOwnerId(Long.valueOf(userId));
                dsFile.setName(file.getOriginalFilename());
                dsFile.setSize(file.getSize());
                dsFile.setType(FilenameUtils.getExtension(file.getOriginalFilename()));

            fileRepository.save(dsFile);

            return filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    public Resource loadAsResource(Long userId, String filename) throws RuntimeException {
        try {
            Path file = rootLocation.resolve(String.valueOf(userId)).resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            }
            throw new RuntimeException("File not found");
        } catch (MalformedURLException e) {
            throw new RuntimeException("File not found", e);
        }
    }

    public List<DSFileDTO> getFiles(Long userId) {
        List<DSFileDTO> validFiles = new ArrayList<>();
        List<DSFile> expiredFiles = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        
        for(DSFile file : fileRepository.findAllByOwnerId(userId)) {
            if (now.isBefore(file.getExpirationDate())) {
                validFiles.add(new DSFileDTO(
                    file.getId(), file.getPath(), file.getOwnerId(), file.getName(), 
                    file.getUploadDate(), file.getExpirationDate(), 
                    file.getType(), file.getSize()
                ));
            } else {
                expiredFiles.add(file);
                try {
                    Files.deleteIfExists(rootLocation
                        .resolve(String.valueOf(file.getOwnerId()))
                        .resolve(file.getPath()).normalize()
                    );
                } catch (IOException e) {
                    throw new RuntimeException("Could not delete file.");
                }
            }
        }

        //Delete expired files from DB
        fileRepository.deleteAll(expiredFiles);

        //Return valid files
        return validFiles;
    }
}
