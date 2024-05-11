package com.atsushini.hedgedocportal.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.atsushini.hedgedocportal.dto.HistoryDto;
import com.atsushini.hedgedocportal.dto.NoteDto;
import com.atsushini.hedgedocportal.entity.Note;
import com.atsushini.hedgedocportal.exception.HedgedocApiException;
import com.atsushini.hedgedocportal.exception.HedgedocForbiddenException;
import com.atsushini.hedgedocportal.repository.NoteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoteService {
    
    private final NoteRepository noteRepository;
    private final RestTemplate restTemplate;

    public List<NoteDto> getHistory(String cookie) {

        HistoryDto historyDto;
        try {
            String apiUrl = "http://localhost:3000/history";

            // HedgeDocのCookieをセット
            HttpHeaders headers = new HttpHeaders();
            headers.add("Cookie", cookie);

            // HedgeDocから履歴を検索
            ResponseEntity<HistoryDto> response = restTemplate.exchange(apiUrl, HttpMethod.GET, new HttpEntity<String>(headers), HistoryDto.class);
            System.out.println("HedgeDoc /history response: ");
            System.out.println(response);

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

        List<NoteDto> noteDtos = historyDto.getHistory().stream().map(this::convertToNoteDto).toList();
        System.out.println(noteDtos);
        return noteDtos;
    }

    // HedgeDocの履歴情報と、DBのNoteエンティティをマージし、Dtoに変換する
    private NoteDto convertToNoteDto(HistoryDto.HistoryItem historyItem) {
        // 検索
        Note note = noteRepository.findByHedgedocId(historyItem.getId());
        // DBになければ作成する
        if (note == null) {
            Note newNote = new Note();
            newNote.setHedgedocId(historyItem.getId());
            note = noteRepository.save(newNote);
        }

        // Dtoに変換
        NoteDto noteDto = new NoteDto();
        noteDto.setId(note.getId());
        noteDto.setHedgedocId(note.getHedgedocId());

        noteDto.setTitle(historyItem.getText());
        noteDto.setUpdatetime(historyItem.getTime().toLocalDateTime());

        return noteDto; 
    }
}
