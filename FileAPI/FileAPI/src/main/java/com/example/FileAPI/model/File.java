package com.example.FileAPI.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "files")
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String extension;

    @Column(nullable = false)
    private String path;

    @Column(nullable = false)
    private long size;

    @Column(nullable = false)
    @CreationTimestamp
    private LocalDateTime uploadTime;

    public Long getId() {return id;}

    public void setId(Long id) {this.id = id;}

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    public String getExtension() {return extension;}

    public void setExtension(String extension) {this.extension = extension;}

    public String getPath() {return path;}

    public void setPath(String path) {this.path = path;}

    public long getSize() {return size;}

    public void setSize(long size) {this.size = size;}

    public LocalDateTime getUploadTime() {return uploadTime;}

    public void setUploadTime(LocalDateTime uploadTime) {this.uploadTime = uploadTime;}
}
