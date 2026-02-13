package com.openclassrooms.dataShare_api.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.openclassrooms.dataShare_api.config.file.LocalFileStorage;
import com.openclassrooms.dataShare_api.config.file.StorageProperties;
import com.openclassrooms.dataShare_api.model.DSFile;
import com.openclassrooms.dataShare_api.repository.FileRepository;
import com.openclassrooms.dataShare_api.service.FileService;

@ExtendWith(MockitoExtension.class)
public class FileServiceTests {
    @Mock
    private LocalFileStorage fileStorage;

    @Mock
    private FileRepository fileRepository;

    @Mock
    private StorageProperties storageProperties;

    @TempDir
    private Path tempDir;

    private FileService fileService;

    private String mockUserId = "1";
    private String mockFileName = "filename.txt";
    private Resource mockResource;
    private MockMultipartFile mockFile;
    private DSFile mockDSFile;
    
    @BeforeEach
    public void setup() {
        when(storageProperties.getLocation()).thenReturn(tempDir);

        fileService = new FileService(fileStorage, fileRepository, storageProperties);
        
        mockResource = mock(Resource.class);
        mockFile = new MockMultipartFile("data", "filename.txt", "text/plain", "some text".getBytes());
        mockDSFile = new DSFile();
            mockDSFile.setOwnerId(0L);
            mockDSFile.setPath("/path/to/filename.txt");
            mockDSFile.setExpirationDate(LocalDateTime.now().plusHours(1));
    }

    @Test
    public void testFileService_Store() throws IOException {
        when(fileRepository.save(any(DSFile.class))).thenReturn(mockDSFile);

        String fileName = fileService.store(mockFile, mockUserId, 7L);

        assertTrue(fileName.endsWith("_filename.txt"));
    }

    @Test
    public void testFileService_Store_Invalid() throws IOException {
        // empty file
        MultipartFile emptyFile = mock(MultipartFile.class);
        when(emptyFile.isEmpty()).thenReturn(true);
        assertThrows(RuntimeException.class, () -> fileService.store(emptyFile, mockUserId, 7L));

        // IOException -> RuntimeException
        doThrow(new IOException()).when(fileStorage).copy(any(InputStream.class), any(Path.class));
        assertThrows(RuntimeException.class, () -> fileService.store(mockFile, mockUserId, 7L));
    }

    @Test
    public void testFileService_Load() throws IOException {
        when(mockResource.exists()).thenReturn(true);
        when(mockResource.isReadable()).thenReturn(true);
        when(fileStorage.load(any())).thenReturn(mockResource);

        assertEquals(mockResource, fileService.loadAsResource(Long.valueOf(mockUserId), mockFileName));
    }

    @Test
    public void testFileService_Load_Invalid() throws MalformedURLException {
        when(fileStorage.load(any())).thenReturn(mockResource);

        // file doesn't exist
        when(mockResource.exists()).thenReturn(false);
        assertThrows(RuntimeException.class, () -> fileService.loadAsResource(Long.valueOf(mockUserId), mockFileName));

        // file isn't readable
        when(mockResource.exists()).thenReturn(true);
        when(mockResource.isReadable()).thenReturn(false);
        assertThrows(RuntimeException.class, () -> fileService.loadAsResource(Long.valueOf(mockUserId), mockFileName));

        // bad url (MalformedURLException -> RuntimeException)
        doThrow(new MalformedURLException()).when(fileStorage).load(any());
        assertThrows(RuntimeException.class, () -> fileService.loadAsResource(Long.valueOf(mockUserId), mockFileName));
    }

    @Test
    public void testFileService_GetFiles() throws IOException {
        assertTrue(fileService.getFiles(Long.valueOf(mockUserId)) instanceof List<DSFile>);
    }

    @Test
    public void testFileService_GetFilesDTO() throws IOException {
        DSFile mockExpiredFile = new DSFile();
            mockExpiredFile.setId(9999L);
            mockExpiredFile.setPath("/path/to/otherFile.txt");
            mockExpiredFile.setExpirationDate(LocalDateTime.now().minusHours(1));

        List<DSFile> filesList = new ArrayList<>();
            filesList.add(mockDSFile);
            filesList.add(mockExpiredFile);

        when(fileRepository.findAllByOwnerId(anyLong())).thenReturn(filesList);
        when(fileStorage.deleteIfExists(any(Path.class))).thenReturn(true);

        fileService.getFilesDTO(Long.valueOf(mockUserId));
        verify(fileRepository).deleteAll(anyList());
    }

    @Test
    public void testFileService_Delete() throws IOException {
        when(fileRepository.findById(anyLong())).thenReturn(Optional.of(mockDSFile));
        when(fileStorage.deleteIfExists(any(Path.class))).thenReturn(true);

        fileService.deleteFile(Long.valueOf(mockUserId));
        verify(fileRepository).delete(any(DSFile.class));
    }

    @Test
    public void testFileService_Delete_Invalid() throws IOException {
        // NoSuchElementException
        when(fileRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> fileService.deleteFile(Long.valueOf(mockUserId)));
        
        // IOException -> RuntimeException
        when(fileRepository.findById(anyLong())).thenReturn(Optional.of(mockDSFile));
        when(fileStorage.deleteIfExists(any(Path.class))).thenThrow(new IOException());

        assertThrows(RuntimeException.class, () -> fileService.deleteFile(Long.valueOf(mockUserId)));
    }
}
