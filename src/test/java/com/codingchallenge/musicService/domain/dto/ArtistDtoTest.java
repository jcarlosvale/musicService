package com.codingchallenge.musicService.domain.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ArtistDtoTest {

    @Test
    void constructor() {
        //GIVEN
        final var mbid = "some mbid";
        final var name = "some name";
        final var gender = "some gender";
        final var country = "some country";
        final var disambiguation = "some disambiguation";
        final var description = "some description";
        final var albums = List.of(mock(AlbumDto.class));

        //WHEN
        final var dto = new ArtistDto(mbid, name, gender, country, disambiguation, description, albums);

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
        assertThat(dto.getDescription())
                .isEqualTo(description);
        assertThat(dto.getAlbums())
                .isEqualTo(albums);
    }
}