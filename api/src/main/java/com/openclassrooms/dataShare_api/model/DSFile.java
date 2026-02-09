package com.openclassrooms.dataShare_api.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="files")
public class DSFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String path;

    @Column(name="owner_id")
    Long ownerId;
    String name;

    @Column(name="upload_date")
    LocalDateTime uploadDate;
    
    @Column(name="expiration_date")
    LocalDateTime expirationDate;

    String type;
    Long size;

    @PrePersist
    void onCreate() {
        if (uploadDate == null)
            uploadDate = LocalDateTime.now();

        if (expirationDate == null)
            expirationDate = uploadDate.plusMinutes(10);
    }
}
