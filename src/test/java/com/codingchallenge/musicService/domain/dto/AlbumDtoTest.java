package com.codingchallenge.musicService.domain.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AlbumDtoTest {

    @Test
    void constructor() {
        //GIVEN
        final var id = "some id";
        final var title = "some title";
        final var imageUrl = "some imageUrl";

        //WHEN
        final var dto = new AlbumDto(id, title, imageUrl);

        //THEN
        assertThat(dto.getId())
                .isEqualTo(id);
        assertThat(dto.getTitle())
                .isEqualTo(title);
        assertThat(dto.getImageUrl())
                .isEqualTo(imageUrl);
    }
}