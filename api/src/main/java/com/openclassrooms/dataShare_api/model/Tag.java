package com.openclassrooms.dataShare_api.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="tags")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tag;
    private Long fileId;

    public Tag() {

    }

    public Tag(String tag, Long fileId) {
        this.tag = tag;
        this.fileId = fileId;
    }
}
