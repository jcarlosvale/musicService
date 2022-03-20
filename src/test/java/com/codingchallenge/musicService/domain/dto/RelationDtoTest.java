package com.codingchallenge.musicService.domain.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class RelationDtoTest {

    @Test
    void constructor() {
        //GIVEN
        final var type = "some type";
        final var url = mock(UrlDto.class);

        //WHEN
        final var dto = new RelationDto(type,url);

        //THEN
        assertThat(dto.getType())
                .isEqualTo(type);
        assertThat(dto.getUrl())
                .isEqualTo(url);
    }
}