package com.atsushini.hedgedocportal.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class NoteDto extends ItemDto {
    
    private String hedgedocId;
    private String title;
    private LocalDateTime updatetime;
}
