package com.codingchallenge.musicService.domain.dto;

import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;

class WikidataDtoTest {
    @Test
    void constructor() {
        //GIVEN
        final var enwiki = new HashMap<String, String>();
        enwiki.put("title", "some title");

        final var sitelinks = new HashMap<>();
        sitelinks.put("enwiki", enwiki);

        final var entity = new HashMap<>();
        entity.put("sitelinks", sitelinks);

        final var entities = new HashMap<>();
        entities.put("some id", entity);

        final var map = new HashMap<String, Object>();
        map.put("entities", entities);

        //WHEN
        final var dto = new WikidataDto(map);

        //THEN
        assertThat(dto.getEntitiesMap())
                .isNotNull();
        assertThat(dto.getEntitiesMap().get("some id"))
                .isEqualTo("some title");
    }
}