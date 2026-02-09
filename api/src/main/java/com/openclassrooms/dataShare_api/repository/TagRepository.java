package com.openclassrooms.dataShare_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.openclassrooms.dataShare_api.model.Tag;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long>  {
    
}
