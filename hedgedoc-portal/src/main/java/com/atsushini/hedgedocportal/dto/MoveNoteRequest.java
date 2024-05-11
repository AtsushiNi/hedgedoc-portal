package com.atsushini.hedgedocportal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MoveNoteRequest {
    
    private Long noteId;
    private Long fromFolderId;
    private Long toFolderId;
}
