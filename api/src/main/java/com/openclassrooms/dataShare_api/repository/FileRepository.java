package com.openclassrooms.dataShare_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.openclassrooms.dataShare_api.model.DSFile;

@Repository
public interface FileRepository extends JpaRepository<DSFile, Long> {
    public List<DSFile> findAllByOwnerId(Long ownerId);
}
