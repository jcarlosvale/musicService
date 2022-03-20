package com.codingchallenge.musicService.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MusicBrainzDto {
    @JsonProperty("id")
    private String mbid;

    @JsonProperty("name")
    private String name;

    @JsonProperty("gender")
    private String gender;

    @JsonProperty("country")
    private String country;

    @JsonProperty("disambiguation")
    private String disambiguation;

    @JsonProperty("relations")
    private List<RelationDto> relations;

    @JsonProperty("release-groups")
    private List<ReleaseDto> releases;
}
