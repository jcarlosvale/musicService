package com.codingchallenge.musicService.domain.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ReleaseDtoTest {
    @Test
    void constructor() {
        //GIVEN
        final var id = "some id";
        final var title = "some title";
        final var primaryType = "some primaryType";

        //WHEN
        final var dto = new ReleaseDto(id, title, primaryType);

        //THEN
        assertThat(dto.getId())
                .isEqualTo(id);
        assertThat(dto.getTitle())
                .isEqualTo(title);
        assertThat(dto.getPrimaryType())
                .isEqualTo(primaryType);
    }
}