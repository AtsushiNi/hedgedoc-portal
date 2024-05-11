package com.atsushini.hedgedocportal.service;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.atsushini.hedgedocportal.dto.FolderDto;
import com.atsushini.hedgedocportal.dto.NoteDto;
import com.atsushini.hedgedocportal.entity.Folder;
import com.atsushini.hedgedocportal.repository.FolderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FolderService {
    
    private final FolderRepository folderRepository;

    private final RestTemplate restTemplate;

    public List<FolderDto> getFolderTree(String userId) {
        List<Folder> rootFolders = folderRepository.findByUserHedgedocIdAndParentFolderIsNull(userId);

        return rootFolders.stream()
            .map(this::convertToDtoRecursively)
            .toList();
    }

    public FolderDto getById(Long id) {
        Optional<Folder> optionalFolder = folderRepository.findById(id);
        Folder folder = optionalFolder.orElseThrow(() -> new RuntimeException("folder not found. id: " + id));

        FolderDto folderDto = convertToDto(folder);

        // サブフォルダ
        List<FolderDto> subFolders = folder.getSubFolders().stream().map(subFolder -> {
            FolderDto dto = new FolderDto();
            dto.setId(subFolder.getId());
            dto.setTitle(subFolder.getTitle());
            return dto;
        }).toList();
        folderDto.setSubFolders(subFolders);

        // HedgeDocノート
        List<NoteDto> notes = folder.getNotes().stream().map(note -> {
            String url = "http://localhost:3000/" + note.getHedgedocId() + "/info";
            ResponseEntity<NoteDto> response = restTemplate.getForEntity(url, NoteDto.class);
            System.out.println(response.getBody());
            NoteDto dto = response.getBody();
            dto.setId(note.getId());
            dto.setHedgedocId(note.getHedgedocId());
            return dto;
        }).toList();
        folderDto.setNotes(notes);

        return folderDto;
    }

    private FolderDto convertToDto(Folder entity) {
        FolderDto folderDto = new FolderDto();
        folderDto.setId(entity.getId());
        folderDto.setTitle(entity.getTitle());

        return folderDto;
    }

    private FolderDto convertToDtoRecursively(Folder entity) {
        FolderDto folderDto = convertToDto(entity);
        List<FolderDto> subFolderDtos = entity.getSubFolders()
            .stream()
            .map(this::convertToDtoRecursively)
            .toList();
        folderDto.setSubFolders(subFolderDtos);

        return folderDto;
    }
}
