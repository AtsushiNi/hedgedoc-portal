package com.atsushini.hedgedocportal.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.atsushini.hedgedocportal.dto.ESNoteDto;
import com.atsushini.hedgedocportal.entity.ESNote;
import com.atsushini.hedgedocportal.repository.ESNoteRepository;

@Service
public class ESNoteService {
    
    @Autowired
    private ESNoteRepository esNoteRepository;
    // private ElasticsearchOperations operations;

    public List<ESNoteDto> searchNotesByContent(String content) {
        List<ESNote> notes = esNoteRepository.searchByContent(content);
        return notes.stream().map(this::convertToNoteDto).toList();
    }

    private ESNoteDto convertToNoteDto(ESNote note) {
        ESNoteDto dto = new ESNoteDto();
        dto.setId(note.getId());
        dto.setTitle(note.getTitle());
        dto.setContent(note.getContent());
        return dto;
    }
}
