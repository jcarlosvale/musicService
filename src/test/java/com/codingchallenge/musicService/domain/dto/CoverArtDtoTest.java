package com.codingchallenge.musicService.domain.dto;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class CoverArtDtoTest {

    @Test
    void constructor() {
        //GIVEN
        final var imageDtoList = List.of(mock(ImageDto.class));

        //WHEN
        final var dto = new CoverArtDto(imageDtoList);

        //THEN
        assertThat(dto.getImageDtoList())
                .isEqualTo(imageDtoList);
    }
}