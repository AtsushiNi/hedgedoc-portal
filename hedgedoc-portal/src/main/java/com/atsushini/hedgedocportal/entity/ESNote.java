package com.atsushini.hedgedocportal.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import lombok.Data;

@Document(indexName = "note")
@Data
public class ESNote {
    @Id
    String id;
    String content;
}
