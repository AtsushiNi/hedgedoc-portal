package com.atsushini.hedgedocportal.service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.atsushini.hedgedocportal.authentication.AuthenticationUtil;
import com.atsushini.hedgedocportal.dto.ESNoteDto;
import com.atsushini.hedgedocportal.dto.HistoryDto;
import com.atsushini.hedgedocportal.dto.NoteDto;
import com.atsushini.hedgedocportal.dto.UserDto;
import com.atsushini.hedgedocportal.entity.ESNote;
import com.atsushini.hedgedocportal.repository.ESNoteRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ESNoteService {
    
    private final ESNoteRepository esNoteRepository;
    private final UserService userService;
    private final NoteService noteService;
    private final RestTemplate restTemplate;

    private String hedgedocUrl = "http://localhost:3000";

    public List<NoteDto> searchNotesByContent(String content) {
        // elasticsearchから検索
        List<ESNote> notes = esNoteRepository.searchByContent(content);
        Stream<ESNoteDto> noteDtoList = notes.stream().map(this::convertToESNoteDto);
        List<String> searchResultIds = noteDtoList.map(ESNoteDto::getId).toList();

        // Hedgedocから履歴を検索
        List<NoteDto> history = noteService.getHistory();

        // 履歴の中から、検索にヒットしたものをフィルター
        return history.stream().filter(note -> searchResultIds.contains(note.getHedgedocId())).toList();
    }

    public void synchronize() {
        // Hedgedocから履歴を検索
        UserDto currentUserDto = AuthenticationUtil.getCurrentUser();
        HttpEntity<String> httpEntity = new HttpEntity<>(getHedgeDocHttpHeaders(currentUserDto));
        HistoryDto historyDto = restTemplate.exchange(hedgedocUrl + "/history", HttpMethod.GET, httpEntity, HistoryDto.class).getBody();

        historyDto.getHistory().stream().forEach(history -> {
            // Hedgedocのnote内容をダウンロード
            String hedgedocNoteId = history.getId();
            String downloadUrl = hedgedocUrl + "/" + hedgedocNoteId + "/download";
            ResponseEntity<byte[]> response = restTemplate.exchange(downloadUrl, HttpMethod.GET, httpEntity, byte[].class);
            if (response.getBody() == null) {
                System.out.println("hedgedoc note content is null. skip save to elasticsearch.");
                return;
            }

            // elasticsearchの予約語を" "に置換
            String specialCharsPattern = "[+\\-!(){}\\[\\]^\"~*?:\\\\/\\n\\r]|&&|\\|\\||AND|OR|NOT";
            Pattern pattern = Pattern.compile(specialCharsPattern);
            String content = pattern.matcher(new String(response.getBody(), StandardCharsets.UTF_8)).replaceAll(" ");

            // elasticsearchに保存
            ESNote note = new ESNote();
            note.setId(hedgedocNoteId);
            note.setContent(content);
            esNoteRepository.save(note);
        });
        System.out.println("======================================");
        System.out.println("finish synchronize");
    }

    private ESNoteDto convertToESNoteDto(ESNote note) {
        ESNoteDto dto = new ESNoteDto();
        dto.setId(note.getId());
        dto.setContent(note.getContent());
        return dto;
    }

    private NoteDto convertToNoteDto(ESNoteDto note) {
        NoteDto dto = new NoteDto();
        dto.setHedgedocId(note.getId());
        dto.setContent(note.getContent());
        return dto;
    }

    private HttpHeaders getHedgeDocHttpHeaders(UserDto currentUserDto) {
        // HedgeDocのCookieをセット
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", String.join("; ", currentUserDto.getHedgedocCookies()));
        return headers;
    }
}
