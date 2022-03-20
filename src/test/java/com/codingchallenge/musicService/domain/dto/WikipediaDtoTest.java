package com.codingchallenge.musicService.domain.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WikipediaDtoTest {
    @Test
    void constructor() {
        //GIVEN
        final var description = "some description";

        //WHEN
        final var dto = new WikipediaDto(description);

        //THEN
        assertThat(dto.getDescription())
                .isEqualTo(description);
    }
}