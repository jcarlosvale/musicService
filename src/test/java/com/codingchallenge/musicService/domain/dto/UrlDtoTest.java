package com.codingchallenge.musicService.domain.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UrlDtoTest {
    @Test
    void constructor() {
        //GIVEN
        final var resource = "some resource";

        //WHEN
        final var dto = new UrlDto(resource);

        //THEN
        assertThat(dto.getResource())
                .isEqualTo(resource);
    }

}