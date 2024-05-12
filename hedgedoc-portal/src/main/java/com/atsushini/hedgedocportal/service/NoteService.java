package com.atsushini.hedgedocportal.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.atsushini.hedgedocportal.dto.CurrentUserDto;
import com.atsushini.hedgedocportal.dto.HistoryDto;
import com.atsushini.hedgedocportal.dto.NoteDto;
import com.atsushini.hedgedocportal.entity.Folder;
import com.atsushini.hedgedocportal.entity.FolderNote;
import com.atsushini.hedgedocportal.entity.Note;
import com.atsushini.hedgedocportal.exception.HedgedocApiException;
import com.atsushini.hedgedocportal.exception.HedgedocForbiddenException;
import com.atsushini.hedgedocportal.exception.NotFoundException;
import com.atsushini.hedgedocportal.repository.FolderNoteRepository;
import com.atsushini.hedgedocportal.repository.FolderRepository;
import com.atsushini.hedgedocportal.repository.NoteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoteService {
    
    private final FolderRepository folderRepository;
    private final NoteRepository noteRepository;
    private final FolderNoteRepository folderNoteRepository;
    private final RestTemplate restTemplate;

    @Value("${hedgedoc.url}")
    private String hedgedocUrl;

    public List<NoteDto> getHistory(CurrentUserDto currentUserDto) {

        HistoryDto historyDto;
        try {
            String apiUrl = hedgedocUrl + "/history";

            // HedgeDocのCookieをセット
            HttpHeaders headers = new HttpHeaders();
            headers.add("Cookie", currentUserDto.getCookie());

            // HedgeDocから履歴を検索
            ResponseEntity<HistoryDto> response = restTemplate.exchange(apiUrl, HttpMethod.GET, new HttpEntity<String>(headers), HistoryDto.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                System.out.println("Failed to fetch data from external API");
                throw new HedgedocApiException("Failed to fetch data from external API");
            }

            historyDto = response.getBody();
        } catch (Exception e) {
            // HedgeDocの認証が失敗すると、ステータス200でログインページが表示される。
            // レスポンスがHistortyDtoに変換失敗すると、認証失敗と判定する。
            System.out.println("Failed to parse data from external API. Maybe forbidden.");
            throw new HedgedocForbiddenException("Failed to parse data from external API. Maybe forbidden.");
        }

        // HedgeDocの履歴情報と、DBのNoteエンティティをマージし、Dtoに変換する
        List<NoteDto> noteDtoList = historyDto.getHistory().stream().map(historyItem -> {
            // 検索
            Note note = noteRepository.findByHedgedocId(historyItem.getId());
            // DBになければ作成する
            if (note == null) {
                Note newNote = new Note();
                newNote.setHedgedocId(historyItem.getId());
                note = noteRepository.save(newNote);
            }

            // HedgeDocの履歴情報とDBのNoteエンティティをDtoに変換
            NoteDto dto = convertToNoteDto(note, historyItem);

            return dto;
        }).toList();

        // 既にフォルダ分けされているノートはトップページの履歴に表示しない
        List<Note> notesInFolders = noteRepository.findByUserId(currentUserDto.getId());
        List<Long> noteIdListInFolders = notesInFolders.stream().map(Note::getId).toList();
        List<NoteDto> noteDtoListUnfoldered = noteDtoList
            .stream()
            .filter(note -> !noteIdListInFolders.contains(note.getId()))
            .toList();
        
        return noteDtoListUnfoldered;
    }

    public void moveNote(Long noteId, Long fromFolderId, Long toFolderId) {
        // 移動対象のノート
        Note note;

        if (fromFolderId == null) { // noteがフォルダ分けされていなかった場合
            Optional<Note> optionalNote = noteRepository.findById(noteId); 
            note = optionalNote.orElseThrow(() -> new NotFoundException("Note not found with ID: " + noteId));
        } else {
            FolderNote fromFolderNote = folderNoteRepository.findByFolderIdAndNoteId(fromFolderId, noteId);
            if (fromFolderNote == null) {
                throw new NotFoundException("Note not found with ID: " + noteId + " in folder with ID: " + fromFolderId);
            }
            note = fromFolderNote.getNote();
        }

        Optional<Folder> optionalToFolder = folderRepository.findById(toFolderId);
        Folder toFolder = optionalToFolder.orElseThrow(() -> new NotFoundException("Folder not found with ID: " + toFolderId));

        FolderNote toFolderNote = new FolderNote();
        toFolderNote.setFolder(toFolder);
        toFolderNote.setNote(note);

        folderNoteRepository.save(toFolderNote);
    }

    // HedgeDocの履歴情報とDBのNoteエンティティをDtoに変換
    private NoteDto convertToNoteDto(Note note, HistoryDto.HistoryItem historyItem) {
        NoteDto noteDto = new NoteDto();
        noteDto.setId(note.getId());
        noteDto.setHedgedocId(note.getHedgedocId());

        noteDto.setTitle(historyItem.getText());
        noteDto.setUpdatetime(historyItem.getTime().toLocalDateTime());

        return noteDto; 
    }
}
