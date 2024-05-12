package com.atsushini.hedgedocportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.atsushini.hedgedocportal.entity.FolderNote;

public interface FolderNoteRepository extends JpaRepository<FolderNote, Long> {
    public FolderNote findByFolderIdAndNoteId(Long folderId, Long noteId);

    @Query("SELECT fn FROM FolderNote fn JOIN fn.folder f WHERE fn.note.id = :noteId AND f.user.id = :userId")
    public List<FolderNote> findByNoteIdAndUserId(Long noteId, Long userId);
}
