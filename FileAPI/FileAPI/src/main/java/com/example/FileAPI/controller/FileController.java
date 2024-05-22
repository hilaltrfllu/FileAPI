package com.example.FileAPI.controller;

import com.example.FileAPI.model.File;
import com.example.FileAPI.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/files")
public class FileController {

    @Autowired
    private FileService fileService;

    @Operation(summary = "Upload a file")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "File uploaded successfully",
                    content = { @Content(schema = @Schema(implementation = File.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid file format or size",
                    content = @Content) })
    @PostMapping("/upload")
    public ResponseEntity<File> uploadFile(@Parameter(description = "File to be uploaded", required = true,
            content = @Content(mediaType = "multipart/form-data")) @RequestParam("file") MultipartFile file) {
        try {
            File savedFile = fileService.saveFile(file);
            return new ResponseEntity<>(savedFile, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Get all files", description = "Retrieves metadata for all uploaded files")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fetched all files",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = File.class))) })
    @GetMapping
    public ResponseEntity<List<File>> getAllFiles() {
        List<File> files = fileService.getAllFiles();
        return new ResponseEntity<>(files, HttpStatus.OK);
    }

    @Operation(summary = "Get file by ID", description = "Retrieves metadata for a specific file by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File fetched successfully",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = File.class)) }),
            @ApiResponse(responseCode = "404", description = "File not found",
                    content = @Content) })
    @GetMapping("/{id}")
    public ResponseEntity<File> getFileById(@PathVariable Long id) {
        Optional<File> fileEntity = fileService.getFileById(id);
        return fileEntity.map(entity -> new ResponseEntity<>(entity, HttpStatus.OK))
                         .orElseGet(() -> new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Download file by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File downloaded successfully",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "File not found",
                    content = @Content) })
    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadFileById(@PathVariable Long id) {
        Optional<File> fileEntity = fileService.getFileById(id);
        if (fileEntity.isPresent()) {
            Path filePath = Path.of(fileEntity.get().getPath());
            try {
                byte[] fileContent = Files.readAllBytes(filePath);
                HttpHeaders headers = new HttpHeaders();
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileEntity.get().getName() + "\"");
                return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
            } catch (IOException e) {
                return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Delete file by ID", description = "Deletes a specific file by ID and removes its metadata from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "File deleted successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content) })
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteFileById(@PathVariable Long id) {
        try {
            fileService.deleteFile(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
