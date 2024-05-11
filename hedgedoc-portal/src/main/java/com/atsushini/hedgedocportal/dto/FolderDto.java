package com.atsushini.hedgedocportal.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FolderDto {
    
    private Long id;
    private String title;
    private String userId;
    // パンクズリストを表示するため
    private List<FolderDto> parentFolders;
    private List<FolderDto> subFolders;
    private List<NoteDto> notes;

    public FolderDto copy() {
        return new FolderDto(
            this.getId(),
            this.getTitle(),
            this.getUserId(),
            this.getParentFolders(),
            this.getSubFolders(),
            this.getNotes()
        );
    }
}
