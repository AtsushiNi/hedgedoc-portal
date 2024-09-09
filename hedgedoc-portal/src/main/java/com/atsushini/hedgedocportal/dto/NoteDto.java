package com.atsushini.hedgedocportal.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class NoteDto {
    
    private Long id;
    private String hedgedocId;
    private String title;
    private String content;
    private Boolean pinned;
    private LocalDateTime updatetime;
}
