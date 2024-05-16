package com.atsushini.hedgedocportal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RuleDto {
    
    private Long id;
    private String title;
    private String regularExpression;
    private FolderDto folder;
}
