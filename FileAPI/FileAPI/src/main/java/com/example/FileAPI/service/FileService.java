package com.example.FileAPI.service;

import com.example.FileAPI.model.File;
import com.example.FileAPI.repository.FileRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class FileService {

    private static final long MAX_FILE_SIZE = 5*1024*1024;
    private static final String[] ALLOWED_EXTENSIONS = new String[]{"png", "jpeg", "jpg", "docx", "pdf", "xlsx"};
    private final Path fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();

    @Autowired
    private FileRepository fileRepository;

    public FileService() {
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception e) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", e);
        }
    }

    @Transactional
    public File saveFile(MultipartFile file) throws Exception {
        validate(file);

        String fileName = file.getOriginalFilename();
        String fileExtension = getFileExtension(fileName);
        Path filePath = this.fileStorageLocation.resolve(fileName);
        Files.copy(file.getInputStream(), filePath);

        File fileEntity = new File();
        fileEntity.setName(fileName);
        fileEntity.setExtension(fileExtension);
        fileEntity.setPath(filePath.toString());
        fileEntity.setSize(file.getSize());
        fileEntity.setUploadTime(LocalDateTime.now());

        return fileRepository.save(fileEntity);
    }

    @Transactional
    public void deleteFile(Long id) throws IOException {
        Optional<File> fileEntity = fileRepository.findById(id);
        if (fileEntity.isPresent()) {
            Files.deleteIfExists(Paths.get(fileEntity.get().getPath()));
            fileRepository.deleteById(id);
        }
    }

    private void validate(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("File is too large. Max size is " + MAX_FILE_SIZE);
        }

        String fileExtension = getFileExtension(file.getOriginalFilename());

        boolean isValidExtension = Arrays.asList(ALLOWED_EXTENSIONS).contains(fileExtension);
        if (!isValidExtension) {
            throw new RuntimeException("File extension is not valid");
        }
    }

    private String getFileExtension(String fileName) {
        int i = fileName.lastIndexOf('.');
        if (i > 1) {
            return fileName.substring(i+1);
        }
        return "";
    }

    public List<File> getAllFiles() {
        return fileRepository.findAll();
    }

    public Optional<File> getFileById(Long id) {
        return fileRepository.findById(id);
    }

}
