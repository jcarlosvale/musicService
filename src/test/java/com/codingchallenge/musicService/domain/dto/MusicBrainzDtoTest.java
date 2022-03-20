package com.codingchallenge.musicService.domain.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class MusicBrainzDtoTest {

    @Test
    void constructor() {
        //GIVEN
        final var mbid = "some mbid";
        final var name = "some name";
        final var gender = "some gender";
        final var country = "some country";
        final var disambiguation = "some disambiguation";
        final var relations = List.of(mock(RelationDto.class));
        final var releases = List.of(mock(ReleaseDto.class));

        //WHEN
        final var dto = new MusicBrainzDto(mbid, name, gender, country, disambiguation, relations, releases);

        //THEN
        assertThat(dto.getMbid())
                .isEqualTo(mbid);
        assertThat(dto.getName())
                .isEqualTo(name);
        assertThat(dto.getGender())
                .isEqualTo(gender);
        assertThat(dto.getCountry())
                .isEqualTo(country);
        assertThat(dto.getDisambiguation())
                .isEqualTo(disambiguation);
        assertThat(dto.getRelations())
                .isEqualTo(relations);
        assertThat(dto.getReleases())
                .isEqualTo(releases);
    }
}