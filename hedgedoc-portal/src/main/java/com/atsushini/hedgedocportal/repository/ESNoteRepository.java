package com.atsushini.hedgedocportal.repository;

import java.util.List;

import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import com.atsushini.hedgedocportal.entity.ESNote;

@Repository
public interface ESNoteRepository extends ElasticsearchRepository<ESNote, String> {
    @Query("{\"match\": {\"content\": {\"query\": \"?0\"}}}")
    List<ESNote> searchByContent(String content);
}
