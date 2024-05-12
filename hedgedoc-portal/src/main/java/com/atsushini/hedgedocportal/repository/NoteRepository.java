package com.atsushini.hedgedocportal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.atsushini.hedgedocportal.entity.Note;

public interface NoteRepository extends JpaRepository<Note, Long> {
    public Note findByHedgedocId(String hedgedocId);    

    @Query("SELECT n FROM Note n JOIN n.folderNotes fn JOIN fn.folder f JOIN f.user u WHERE u.id = :userId")
    public List<Note> findByUserId(Long userId);
}
