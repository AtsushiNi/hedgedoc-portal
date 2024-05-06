package com.atsushini.hedgedocportal.dto;

import java.sql.Timestamp;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class HistoryDto {
    
    private List<HistoryItem> history;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    private static class HistoryItem {
        private String id;
        private String text;
        private Timestamp time;
        private List<String> tags;
    }
}
