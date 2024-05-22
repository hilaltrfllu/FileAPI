package com.example.FileAPI.service;

import com.example.FileAPI.model.File;
import com.example.FileAPI.repository.FileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FileServiceTest {

    @InjectMocks
    private FileService fileService;

    @Mock
    private FileRepository fileRepository;

    @Mock
    private MultipartFile multipartFile;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveFile_success() throws Exception {
        String fileName = "testPdf.pdf";
        String fileExtension = "pdf";
        Path filePath = Paths.get("uploads", fileName);
        long fileSize = 1024;

        when(multipartFile.getOriginalFilename()).thenReturn(fileName);
        when(multipartFile.getInputStream()).thenReturn(InputStream.nullInputStream());
        when(multipartFile.getSize()).thenReturn(fileSize);

        File fileEntity = new File();
        fileEntity.setName(fileName);
        fileEntity.setExtension(fileExtension);
        fileEntity.setPath(filePath.toString());
        fileEntity.setSize(fileSize);

        when(fileRepository.save(any(File.class))).thenReturn(fileEntity);


        File savedFile = fileService.saveFile(multipartFile);

        assertNotNull(savedFile);
        assertEquals(fileName, savedFile.getName());
        verify(fileRepository, times(1)).save(any(File.class));
        Files.deleteIfExists(filePath);

    }

    @Test
    void saveFile_invalidExtension_throwsException() {
        String fileName = "testPdf.pdf";
        when(multipartFile.getOriginalFilename()).thenReturn(fileName);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> fileService.saveFile(multipartFile));
        assertEquals("Invalid file format.", exception.getMessage());
    }

    @Test
    void getFileById_success() {
        Long fileId = 1L;
        File fileEntity = new File();
        fileEntity.setId(fileId);

        when(fileRepository.findById(fileId)).thenReturn(Optional.of(fileEntity));

        Optional<File> foundFile = fileService.getFileById(fileId);

        assertTrue(foundFile.isPresent());
        assertEquals(fileEntity, foundFile.get().getId());
    }

    @Test
    void deleteFile_success() throws IOException {
        Long fileId = 1L;
        String filePath = "uploads/testPdf.pdf";
        File fileEntity = new File();
        fileEntity.setId(fileId);
        fileEntity.setPath(filePath);

        when(fileRepository.findById(fileId)).thenReturn(Optional.of(fileEntity));
        doNothing().when(fileRepository).deleteById(fileId);

        Files.createFile(Paths.get(filePath));

        fileService.deleteFile(fileId);

        verify(fileRepository, times(1)).deleteById(fileId);
        assertFalse(Files.exists(Paths.get(filePath)));
    }


}
