package com.codingchallenge.musicService.domain.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ImageDtoTest {
    @Test
    void constructor() {
        //GIVEN
        final var front = true;
        final var image = "some image";

        //WHEN
        final var dto = new ImageDto(front, image);

        //THEN
        assertThat(dto.isFront())
                .isEqualTo(front);
        assertThat(dto.getImage())
                .isEqualTo(image);
    }
}