package com.openclassrooms.dataShare_api.unit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.multipart.MultipartFile;

import com.openclassrooms.dataShare_api.controller.FileController;
import com.openclassrooms.dataShare_api.dto.DSFileDTO;
import com.openclassrooms.dataShare_api.service.FileService;

@WebMvcTest(FileController.class)
@AutoConfigureMockMvc
public class FileControllerTests {
    @Autowired
	private MockMvc mockMvc;

    @MockitoBean
    private FileService fileService;

    @Test
    public void testFileController_Read() throws Exception {
        List<DSFileDTO> filesList = new ArrayList<>();
            filesList.add(new DSFileDTO(null, null, null, null, null, null, null, null));

        when(fileService.getFilesDTO(anyLong())).thenReturn(filesList);
        when(fileService.getFileDTO(anyLong(), anyLong())).thenReturn(new DSFileDTO(null, null, null, null, null, null, null, null));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/file/list/1")
            .with(jwt().jwt(jwt -> jwt.claim("userId", 1L))))
            .andExpect(MockMvcResultMatchers.status().isOk());
        
        mockMvc.perform(MockMvcRequestBuilders.get("/api/file/1")
            .with(jwt().jwt(jwt -> jwt.claim("userId", 1L))))
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testFileController_Upload() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile("data", "filename.txt", "text/plain", "some text".getBytes());

        when(fileService.store(any(MultipartFile.class), anyString(), anyLong())).thenReturn("fileName");

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/file/upload")
                .with(jwt().jwt(jwt -> jwt.claim("userId", 1L)))
                .file("file", mockFile.getBytes())
                .param("userId", "0")
                .param("expirationDays", "7")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testFileController_Upload_Invalid() throws Exception {
        MockMultipartFile mockFile = new MockMultipartFile("data", "filename.txt", "text/plain", "some text".getBytes());

        // RuntimeException
        when(fileService.store(any(MultipartFile.class), anyString(), anyLong())).thenThrow(new RuntimeException());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/file/upload")
                .with(jwt().jwt(jwt -> jwt.claim("userId", 1L)))
                .file("file", mockFile.getBytes())
                .param("userId", "0")
                .param("expirationDays", "7")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void testFileController_Download() throws Exception {
        Resource mockResource = new ByteArrayResource("testResource".getBytes()) {
            @Override
            public String getFilename() {return "test.txt";}
        };

        when(fileService.loadAsResource(anyLong(), anyString()))
            .thenReturn(mockResource);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/file/download/1")
                .with(jwt().jwt(jwt -> jwt.claim("userId", 1L))))
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testFileController_Download_Invalid() throws Exception {
        when(fileService.loadAsResource(anyLong(), anyString()))
            .thenThrow(new RuntimeException());

        mockMvc.perform(MockMvcRequestBuilders.get("/api/file/download/1")
                .with(jwt().jwt(jwt -> jwt.claim("userId", 1L))))
            .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void testFileController_Delete() throws Exception {
        when(fileService.getFileDTO(anyLong(), anyLong())).thenReturn(new DSFileDTO(
            null, null, 1L, null, null, null, null, null)
        );

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/file/1")
                .with(jwt().jwt(jwt -> jwt.claim("userId", 1L))))
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testFileController_Delete_Invalid() throws Exception {
        when(fileService.getFileDTO(anyLong(), anyLong())).thenReturn(new DSFileDTO(
            null, null, 1L, null, null, null, null, null)
        );
        
        // NoSuchElementException
        doThrow(NoSuchElementException.class).when(fileService).deleteFile(anyLong());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/file/1")
                .with(jwt().jwt(jwt -> jwt.claim("userId", 1L))))
            .andExpect(MockMvcResultMatchers.status().isNotFound());

        // RuntimeException
        doThrow(RuntimeException.class).when(fileService).deleteFile(anyLong());

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/file/1")
                .with(jwt().jwt(jwt -> jwt.claim("userId", 1L))))
            .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }
}
