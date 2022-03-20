package com.codingchallenge.musicService.domain.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WikidataDto {

    private Map<String, String> entitiesMap;

    @JsonCreator
    @SuppressWarnings({"unchecked", "rawtypes"})
    public WikidataDto(final Map<String, Object> map) {

        entitiesMap = new HashMap<>();
        HashMap<String, Object> entities = (HashMap) map.get("entities");

        entities.keySet().forEach(id -> {
            HashMap<String, Object> entity = (HashMap) entities.get(id);
            if (Objects.nonNull(entity)) {
                HashMap<String, Object> siteLinks = (HashMap) entity.get("sitelinks");
                if(Objects.nonNull(siteLinks)) {
                    HashMap<String, String> enwiki = (HashMap) siteLinks.get("enwiki");
                    if (Objects.nonNull(enwiki)) {
                        String title = enwiki.get("title");
                        if (Objects.nonNull(title)) {
                            entitiesMap.put(id, title);
                        }
                    }
                }
            }
        });

    }

}
