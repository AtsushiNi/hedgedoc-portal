package com.atsushini.hedgedocportal.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.atsushini.hedgedocportal.authentication.AuthenticationUtil;
import com.atsushini.hedgedocportal.dto.HistoryDto;
import com.atsushini.hedgedocportal.dto.NoteDto;
import com.atsushini.hedgedocportal.dto.UserDto;
import com.atsushini.hedgedocportal.entity.Folder;
import com.atsushini.hedgedocportal.entity.FolderNote;
import com.atsushini.hedgedocportal.entity.Note;
import com.atsushini.hedgedocportal.entity.Rule;
import com.atsushini.hedgedocportal.exception.ForbiddenException;
import com.atsushini.hedgedocportal.exception.HedgedocApiException;
import com.atsushini.hedgedocportal.exception.NotFoundException;
import com.atsushini.hedgedocportal.repository.FolderNoteRepository;
import com.atsushini.hedgedocportal.repository.FolderRepository;
import com.atsushini.hedgedocportal.repository.NoteRepository;
import com.atsushini.hedgedocportal.repository.RuleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoteService {
    
    private final FolderRepository folderRepository;
    private final NoteRepository noteRepository;
    private final FolderNoteRepository folderNoteRepository;
    private final RuleRepository ruleRepository;
    private final RestTemplate restTemplate;

    @Value("${hedgedoc.url}")
    private String hedgedocUrl;

    public List<NoteDto> getHomeNotes() {
        // HedgeDocの履歴を取得
        List<NoteDto> noteDtoList = getHistory();

        // 既にフォルダ分けされているノートはトップページの履歴に表示しない
        UserDto currentUserDto = AuthenticationUtil.getCurrentUser();
        List<Note> notesInFolders = noteRepository.findByUserId(currentUserDto.getId());
        List<Long> noteIdListInFolders = notesInFolders.stream().map(Note::getId).toList();
        List<NoteDto> noteDtoListUnfoldered = noteDtoList
            .stream()
            .filter(note -> !noteIdListInFolders.contains(note.getId()))
            .toList();

        // 振り分けルールの取得
        List<Rule> rules = ruleRepository.findByUserId(currentUserDto.getId());

        // 振り分けルールに該当するノートはフォルダに移動する
        List<Long> movedNoteIdList = new ArrayList<>();
        for (NoteDto note : noteDtoListUnfoldered) {
            for (Rule rule : rules) {
                // ルールの正規表現をコンパイル
                Pattern pattern = Pattern.compile(rule.getRegularExpression());
                if (pattern.matcher(note.getTitle()).matches()) {
                    // ノートがルールにマッチする場合、対応するフォルダに移動
                    moveNote(note.getId(), null, rule.getFolder().getId());
                    movedNoteIdList.add(note.getId());
                    break; // 一つのルールにマッチしたら次のノートに進む
                }
            }
        }
        noteDtoListUnfoldered = noteDtoListUnfoldered
            .stream()
            .filter(note -> !movedNoteIdList.contains(note.getId()))
            .sorted((note1, note2) -> {
                // isPinnedがtrueのものを前に
                int pinnedComparison = Boolean.compare(note2.getPinned(), note1.getPinned());
                if (pinnedComparison != 0) {
                    return pinnedComparison;
                }
                // 両者のisPinnedが同じなら、updateTimeで降順に並び替え
                return note2.getUpdatetime().compareTo(note1.getUpdatetime());
            })
            .toList();

        return noteDtoListUnfoldered;
    }

    // HedgeDocの履歴を返す
    public List<NoteDto> getHistory() {

        String apiUrl = hedgedocUrl + "/history";

        // HedgeDocから履歴を検索
        UserDto currentUserDto = AuthenticationUtil.getCurrentUser();
        HttpEntity<String> httpEntity = new HttpEntity<>(getHedgeDocHttpHeaders(currentUserDto));
        ResponseEntity<HistoryDto> response = restTemplate.exchange(apiUrl, HttpMethod.GET, httpEntity, HistoryDto.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            System.out.println("Failed to fetch data from external API");
            throw new HedgedocApiException("Failed to fetch data from external API");
        }

        HistoryDto historyDto = response.getBody();

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

        return noteDtoList;
    }

    public String createNote(Long parentFolderId) {
        Folder parentFolder = folderRepository.findById(parentFolderId).orElse(null);
        if (parentFolder == null) throw new NotFoundException("Folder not found with ID: " + parentFolderId);        

        UserDto currentUserDto = AuthenticationUtil.getCurrentUser();
        if (!parentFolder.getUser().getId().equals(currentUserDto.getId())) {
            throw new ForbiddenException();
        }

        HttpHeaders headers = getHedgeDocHttpHeaders(currentUserDto);
        headers.setContentType(MediaType.TEXT_MARKDOWN);

        // HedgeDocの新規ノートを作成し、URLを取得する
        ResponseEntity<String> response = restTemplate.exchange(hedgedocUrl + "/new", HttpMethod.POST, new HttpEntity<>(headers), String.class);
        String newNoteUrl = response.getHeaders().getLocation().toString();

        // URLからHedgeDocのIDを取得する
        String[] segment = newNoteUrl.split("/");
        String newNoteHedgedocId = segment[segment.length - 1];

        // Noteエンティティを作成
        Note note = new Note();
        note.setHedgedocId(newNoteHedgedocId);
        noteRepository.save(note);

        // FolderNoteエンティティを作成
        FolderNote folderNote = new FolderNote();
        folderNote.setFolder(parentFolder);
        folderNote.setNote(note);
        folderNoteRepository.save(folderNote);

        return newNoteUrl;
    }

    public void moveNote(Long noteId, Long fromFolderId, Long toFolderId) {
        // 移動対象のノート
        Note note;
        FolderNote fromFolderNote = null;

        UserDto currentUserDto = AuthenticationUtil.getCurrentUser();

        if (fromFolderId == null) { // noteがフォルダ分けされていなかった場合
            Optional<Note> optionalNote = noteRepository.findById(noteId); 
            note = optionalNote.orElseThrow(() -> new NotFoundException("Note not found with ID: " + noteId));
        } else {
            fromFolderNote = folderNoteRepository.findByFolderIdAndNoteId(fromFolderId, noteId);
            if (fromFolderNote == null) {
                throw new NotFoundException("Note not found with ID: " + noteId + " in folder with ID: " + fromFolderId);
            }
            // これnotじゃなくて大丈夫？ gitlabのコードも確認
            if (fromFolderNote.getFolder().getUser().getId().equals(currentUserDto.getId())) {
                throw new ForbiddenException();
            }
            note = fromFolderNote.getNote();
        }

        Optional<Folder> optionalToFolder = folderRepository.findById(toFolderId);
        Folder toFolder = optionalToFolder.orElseThrow(() -> new NotFoundException("Folder not found with ID: " + toFolderId));
        if (!toFolder.getUser().getId().equals(currentUserDto.getId())) {
            throw new ForbiddenException();
        }

        FolderNote toFolderNote = new FolderNote();
        toFolderNote.setFolder(toFolder);
        toFolderNote.setNote(note);

        if (fromFolderNote != null) {
            folderNoteRepository.delete(fromFolderNote);
        }
        folderNoteRepository.save(toFolderNote);
    }

    public void deleteNote(Long noteId) {
        // 対象ユーザーの全フォルダから対象ノートを削除
        UserDto currentUserDto = AuthenticationUtil.getCurrentUser();
        List<FolderNote> folderNotes = folderNoteRepository.findByNoteIdAndUserId(noteId, currentUserDto.getId());
        folderNoteRepository.deleteAll(folderNotes);

        Note note = noteRepository.findById(noteId).orElse(null);
        if (note == null) {
            throw new NotFoundException("note not found. id: " + noteId);
        }
        String apiUrl = hedgedocUrl + "/history/" + note.getHedgedocId();

        // HedgeDocの閲覧履歴からノートを削除
        HttpHeaders headers = getHedgeDocHttpHeaders(currentUserDto);
        ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.DELETE, new HttpEntity<String>(headers), String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("failed to delete HedgeDoc history. id: " + note.getHedgedocId());
        }
    }

    // HedgeDocの履歴情報とDBのNoteエンティティをDtoに変換
    private NoteDto convertToNoteDto(Note note, HistoryDto.HistoryItem historyItem) {
        NoteDto noteDto = new NoteDto();
        noteDto.setId(note.getId());
        noteDto.setHedgedocId(note.getHedgedocId());

        noteDto.setTitle(historyItem.getText());
        noteDto.setUpdatetime(historyItem.getTime().toLocalDateTime());
        noteDto.setPinned(historyItem.isPinned());

        return noteDto; 
    }

    private HttpHeaders getHedgeDocHttpHeaders(UserDto currentUserDto) {
        // HedgeDocのCookieをセット
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", String.join("; ", currentUserDto.getHedgedocCookies()));
        return headers;
    }
}
