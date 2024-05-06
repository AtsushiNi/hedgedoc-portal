package com.atsushini.hedgedocportal.dto;

import java.util.List;

import lombok.Data;

@Data
public class ItemDto {
    protected Long id;
    protected FolderDto parentFolder;
}
