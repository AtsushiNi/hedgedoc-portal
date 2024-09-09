package com.atsushini.hedgedocportal.service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.BufferedInputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.zeroturnaround.zip.ZipUtil;

import com.atsushini.hedgedocportal.dto.CurrentUserDto;
import com.atsushini.hedgedocportal.dto.HistoryDto;
import com.atsushini.hedgedocportal.dto.NoteDto;
import com.atsushini.hedgedocportal.exception.HedgedocApiException;
import com.atsushini.hedgedocportal.exception.HedgedocForbiddenException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HistoryService {
    
    private final RestTemplate restTemplate;

    @Value("${hedgedoc.url}")
    private String hedgedocUrl;

    // HedgeDocの履歴を返す
    public HistoryDto getHistory(CurrentUserDto currentUserDto) {
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

            return response.getBody();
        } catch (Exception e) {
            // HedgeDocの認証が失敗すると、ステータス200でログインページが表示される。
            // レスポンスがHistortyDtoに変換失敗すると、認証失敗と判定する。
            System.out.println("Failed to parse data from external API. Maybe forbidden.");
            throw new HedgedocForbiddenException("Failed to parse data from external API. Maybe forbidden.");
        }

    }

    // HedgeDocのノート内容エクスポートデータを取得する
    public List<NoteDto> getExportData(CurrentUserDto currentUserDto) {
        try {
            String apiUrl = hedgedocUrl + "/me/export";

            // HedgeDocのCookieをセット
            HttpHeaders headers = new HttpHeaders();
            headers.add("Cookie", currentUserDto.getCookie());

            // HedgeDocから履歴を検索
            ResponseEntity<byte[]> response = restTemplate.exchange(apiUrl, HttpMethod.GET, new HttpEntity<String>(headers), byte[].class);
            byte[] zipData = response.getBody();
            System.out.println(zipData);
            System.out.println("~~~~~~~~~~~~~~~~~");

            try (OutputStream outputStream = new FileOutputStream("download.zip")) {
                outputStream.write(zipData);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            File tempDir = Files.createTempDirectory("unzipped").toFile();
            ZipUtil.unpack(new File("download.zip"), tempDir);
            File[] files = tempDir.listFiles((dir, name) -> name.endsWith(".md"));
            for (File file : files) {
                System.out.println("File: " + file.getName());
                Files.lines(file.toPath()).forEach(System.out::println);
            }

            // ZIPを解凍
            try (InputStream fileStream = new FileInputStream("download.zip");
                BufferedInputStream stream = new BufferedInputStream(fileStream);
                ArchiveInputStream zipStream = new ArchiveStreamFactory().createArchiveInputStream(stream)) {
                
                ArchiveEntry entry;
                while ((entry = zipStream.getNextEntry()) != null) {
                    if (!entry.isDirectory() && entry.getName().endsWith(".md")) {
                        System.out.println("Found md file: " + entry.getName());
                    }
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new ArrayList<NoteDto>(Arrays.asList());
    }
}
