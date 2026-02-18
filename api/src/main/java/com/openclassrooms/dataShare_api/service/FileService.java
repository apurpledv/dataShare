package com.openclassrooms.dataShare_api.service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.openclassrooms.dataShare_api.config.file.LocalFileStorage;
import com.openclassrooms.dataShare_api.config.file.StorageProperties;
import com.openclassrooms.dataShare_api.dto.DSFileDTO;
import com.openclassrooms.dataShare_api.model.DSFile;
import com.openclassrooms.dataShare_api.repository.FileRepository;

/**
 * FileService is an Entity that handles basic I/O interactions between the local storage system, the database, and File/DSFile Entities
 */
@Service
public class FileService {
    private final LocalFileStorage fileStorage;
    private final FileRepository fileRepository;
    private final Path rootLocation;

    public FileService(LocalFileStorage fileStorage, FileRepository fileRepository, StorageProperties properties) {
        this.fileStorage = fileStorage;
        this.fileRepository = fileRepository;
        this.rootLocation = properties.getLocation();
    }

    /**
     * Stores a given MultipartFile into the local storage system, creating a unique identifier using the User's ID and the File's name
     * @param file the file to store
     * @param userId the file's owner's id
     * @param expirationDays the number of days until the file should be automatically removed
     * @return the stored file's identifier
     * @throws RuntimeException
     */
    public String store(MultipartFile file, String userId, Long expirationDays) throws RuntimeException {
        if (file.isEmpty()) {
            throw new RuntimeException("Cannot store empty file");
        }

        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path userDir = rootLocation.resolve(userId);

        try {
            //Upload file
            fileStorage.createDirectories(userDir);
            Path destinationFile = userDir.resolve(filename).normalize();

            fileStorage.copy(file.getInputStream(), destinationFile);

            //Extract metadata and store it
            File uploadedFile = destinationFile.toFile();

            DSFile dsFile = new DSFile();
                dsFile.setPath(uploadedFile.getName());
                dsFile.setOwnerId(Long.valueOf(userId));
                dsFile.setName(file.getOriginalFilename());
                dsFile.setSize(file.getSize());
                dsFile.setType(FilenameUtils.getExtension(file.getOriginalFilename()));
            
            if (expirationDays != null)
                dsFile.setExpirationDate(LocalDateTime.now().plusDays(expirationDays));

            fileRepository.save(dsFile);

            return filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    /**
     * Fetches a file from the local storage system using the user's id for the folder, and the file's name
     * @param userId the user's id
     * @param filename the unique identifier of the file
     * @return the physical file in Resource format
     * @throws RuntimeException if the file is not found in the local storage system
     */
    public Resource loadAsResource(Long userId, String filename) throws RuntimeException {
        try {
            Path file = rootLocation.resolve(String.valueOf(userId)).resolve(filename);
            Resource resource = fileStorage.load(file);

            if (resource.exists() && resource.isReadable()) {
                return resource;
            }
            throw new RuntimeException("File not found");
        } catch (MalformedURLException e) {
            throw new RuntimeException("File not found", e);
        }
    }

    /**
     * Fetches the metadata of every file belonging to a given user
     * @param userId the user's id
     * @return a list of all his files' metadata (expiration date, type, size...)
     */
    public List<DSFile> getFiles(Long userId) {
        return fileRepository.findAllByOwnerId(userId);
    }

    /**
     * Fetches the metadata of every file belonging to a given user as a DTO
     * @param userId the user's id
     * @return a list of all his files' metadata (expiration date, type, size...) as a DTO
     */
    public List<DSFileDTO> getFilesDTO(Long userId) {
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
                deleteFile(file);
                expiredFiles.add(file);
            }
        }

        //Delete expired files from DB
        fileRepository.deleteAll(expiredFiles);

        //Return valid files
        return validFiles;
    }

    /**
     * Fetches a single file's metadata belonging to a given user
     * @param fileId the file's id (not its unique identifier)
     * @return the file's metadata as a DTO
     * @throws NoSuchElementException if the file is not found in the db
     */
    public DSFileDTO getFileDTO(Long fileId) throws NoSuchElementException {
        Optional<DSFile> file = fileRepository.findById(fileId);
        if (file.isEmpty())
            throw new NoSuchElementException();

        DSFile fileFound = file.get();
        
        return new DSFileDTO(
            fileFound.getId(), fileFound.getPath(), fileFound.getOwnerId(), fileFound.getName(), 
            fileFound.getUploadDate(), fileFound.getExpirationDate(), 
            fileFound.getType(), fileFound.getSize()
        );
    }

    /**
     * Deletes a given file from the system and the db
     * @param fileId the file's id (not its unique identifier)
     * @throws NoSuchElementException if the file is not found in the db
     * @throws RuntimeException if something goes wrong during the physical file's deletion
     */
    public void deleteFile(Long fileId) throws NoSuchElementException, RuntimeException {
        Optional<DSFile> fileFound = fileRepository.findById(fileId);
        if (fileFound.isEmpty())
            throw new NoSuchElementException("User not found.");
        
        deleteFile(fileFound.get());
    }

    /**
     * Deletes a given file from the system and the db
     * @param file the file Entity to delete
     * @throws RuntimeException if an IOException occurs
     */
    private void deleteFile(DSFile file) throws RuntimeException {
        try {
            fileStorage.deleteIfExists(rootLocation
                .resolve(String.valueOf(file.getOwnerId()))
                .resolve(file.getPath()).normalize()
            );
            fileRepository.delete(file);
        } catch (IOException e) {
            throw new RuntimeException("Could not delete file.");
        }
    }
}
