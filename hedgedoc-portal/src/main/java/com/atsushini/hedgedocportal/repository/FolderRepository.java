package com.atsushini.hedgedocportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.atsushini.hedgedocportal.entity.Folder;

public interface FolderRepository extends JpaRepository<Folder, Long> {
    List<Folder> findByUserHedgedocIdAndParentFolderIsNull(String userId);
    
}
