package com.atsushini.hedgedocportal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ESNoteDto {
    
    private String id;
    private String title;
    private String content;
}
