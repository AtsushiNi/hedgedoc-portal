package com.atsushini.hedgedocportal.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FolderDto extends ItemDto {
    
    private Long id;
    private String title;
    private String userId;
    private List<FolderDto> subFolders;
    private List<NoteDto> notes;
}
