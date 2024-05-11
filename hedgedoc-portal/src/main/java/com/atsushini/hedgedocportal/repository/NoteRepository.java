package com.atsushini.hedgedocportal.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.atsushini.hedgedocportal.entity.Note;

public interface NoteRepository extends JpaRepository<Note, Long> {
    public Note findByHedgedocId(String hedgedocId);    
}
