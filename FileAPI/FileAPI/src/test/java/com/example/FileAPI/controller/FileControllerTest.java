package com.example.FileAPI.controller;

import com.example.FileAPI.model.File;
import com.example.FileAPI.service.FileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class FileControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private FileController fileController;

    @Mock
    private FileService fileService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(true);
        mockMvc = MockMvcBuilders.standaloneSetup(fileController).build();
    }

    @Test
    void uploadFile_success() throws Exception {
        MockMultipartFile multipartFile = new MockMultipartFile("file", "testPdf.pdf"
                                                    , MediaType.APPLICATION_PDF_VALUE, "test content".getBytes());
        File fileEntity = new File();
        fileEntity.setId(1L);
        fileEntity.setName("testPdf.pdf");

        when(fileService.saveFile(any())).thenReturn(fileEntity);

        mockMvc.perform(multipart("api/files/upload").file(multipartFile)).
               andExpect(status().isCreated()).andExpect(jsonPath("$.id").value(1L))
               .andExpect(jsonPath("$.name").value("testPdf.pdf"));
    }

    @Test
    void getAllFiles_success() throws Exception {
        File fileEntity = new File();
        fileEntity.setId(1L);
        fileEntity.setName("testPdf.pdf");

        File fileEntity2 = new File();
        fileEntity2.setId(2L);
        fileEntity2.setName("testPdf2.pdf");

        when(fileService.getAllFiles()).thenReturn(Arrays.asList(fileEntity, fileEntity2));

        mockMvc.perform(get("/api/files")).andExpect(status().isOk())
               .andExpect(jsonPath("$.length()").value(2))
               .andExpect(jsonPath("$[0].id").value(1L))
               .andExpect(jsonPath("$[1].id").value(2L));
    }

    @Test
    void getFileById_success() throws Exception {
        Long fileId = 1L;
        File fileEntity = new File();
        fileEntity.setId(1L);
        fileEntity.setName("testPdf.pdf");

        when(fileService.getFileById(fileId)).thenReturn(Optional.of(fileEntity));

        mockMvc.perform(get("/api/files/{id}", fileId))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(fileId))
               .andExpect(jsonPath("$.name").value("testPdf.pdf"));
    }

    @Test
    void deleteFile_success() throws Exception {
        Long fileId = 1L;
        doNothing().when(fileService).deleteFile(fileId);

        mockMvc.perform(delete("/api/files/{id}", fileId)).andExpect(status().isNoContent());
    }
}
