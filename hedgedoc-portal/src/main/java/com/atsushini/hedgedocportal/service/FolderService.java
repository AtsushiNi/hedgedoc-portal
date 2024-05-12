package com.atsushini.hedgedocportal.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.atsushini.hedgedocportal.dto.CurrentUserDto;
import com.atsushini.hedgedocportal.dto.FolderDto;
import com.atsushini.hedgedocportal.dto.NoteDto;
import com.atsushini.hedgedocportal.entity.Folder;
import com.atsushini.hedgedocportal.entity.FolderNote;
import com.atsushini.hedgedocportal.entity.Note;
import com.atsushini.hedgedocportal.exception.NotFoundException;
import com.atsushini.hedgedocportal.repository.FolderNoteRepository;
import com.atsushini.hedgedocportal.repository.FolderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FolderService {
    
    private final FolderNoteRepository folderNoteRepository;
    private final FolderRepository folderRepository;

    private final RestTemplate restTemplate;

    @Value("${hedgedoc.url}")
    private String hedgedocUrl;

    public List<FolderDto> getFolderTree(CurrentUserDto userDto) {
        List<Folder> rootFolders = folderRepository.findByUserHedgedocIdAndParentFolderIsNull(userDto.getHedgedocId());

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

        // パンクズリスト用の親フォルダリスト
        List<FolderDto> parentFolders = new ArrayList<>();
        parentFolders.add(folderDto.copy());
        parentFolders = getParentFolderTree(parentFolders);
        folderDto.setParentFolders(parentFolders);

        // HedgeDocノート
        List<NoteDto> notes = folder.getFolderNotes().stream().map(folderNote -> {
            Note note = folderNote.getNote();
            String url = hedgedocUrl + "/" + note.getHedgedocId() + "/info";
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

    // フォルダーの所有ユーザーIDを返す
    public Long getOwnerId(Long folderId) {
        Folder folder = folderRepository.findById(folderId).orElse(null);
        if (folder == null) {
            throw new NotFoundException("folder not found. id: " + folderId);
        }
        
        return folder.getUser().getId();
    }

    // フォルダーからノートを削除する
    public void deleteNoteFromFolder(Long folderId, Long noteId) {
        FolderNote folderNote = folderNoteRepository.findByFolderIdAndNoteId(folderId, noteId);
        if (folderNote == null) {
            throw new NotFoundException("note not found. folderId: " + folderId + ", noteId: " + noteId);
        }
        folderNoteRepository.delete(folderNote);
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

    // 再起的に親フォルダを検索する
    private List<FolderDto> getParentFolderTree(List<FolderDto> tree) {
        Folder folder = folderRepository.findById(tree.get(0).getId()).get();
        if (folder.getParentFolder() != null) {
            FolderDto parentFolderDto = new FolderDto();
            parentFolderDto.setId(folder.getParentFolder().getId());
            parentFolderDto.setTitle(folder.getParentFolder().getTitle());

            tree.add(0, parentFolderDto);

            tree = getParentFolderTree(tree);
        }
        return tree;
    }
}
