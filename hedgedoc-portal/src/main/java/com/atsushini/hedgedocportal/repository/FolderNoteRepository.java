package com.atsushini.hedgedocportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.atsushini.hedgedocportal.entity.FolderNote;

public interface FolderNoteRepository extends JpaRepository<FolderNote, Long> {
    public FolderNote findByFolderIdAndNoteId(Long folderId, Long noteId);
}
