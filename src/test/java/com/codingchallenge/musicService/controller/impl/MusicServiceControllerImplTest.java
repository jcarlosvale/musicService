package com.codingchallenge.musicService.controller.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.codingchallenge.musicService.domain.dto.ArtistDto;
import com.codingchallenge.musicService.domain.exception.CoverArtException;
import com.codingchallenge.musicService.domain.exception.CoverArtNotFoundException;
import com.codingchallenge.musicService.domain.exception.MusicBrainzException;
import com.codingchallenge.musicService.domain.exception.MusicBrainzNotFoundException;
import com.codingchallenge.musicService.domain.exception.WikidataException;
import com.codingchallenge.musicService.domain.exception.WikidataNotFoundException;
import com.codingchallenge.musicService.domain.exception.WikidataUrlNotPresentException;
import com.codingchallenge.musicService.domain.exception.WikipediaException;
import com.codingchallenge.musicService.domain.exception.WikipediaNotFoundException;
import com.codingchallenge.musicService.service.MusicService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

@WebMvcTest(MusicServiceControllerImpl.class)
class MusicServiceControllerImplTest {

    private static final String ARTIST_ID = "e7273fcd-7530-48ba-bd11-1879f4a8920f";

    @MockBean
    private MusicService service;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void infoAboutArtistOk() throws Exception {
        //GIVEN
        ArtistDto artistDto =
                ArtistDto.builder()
                        .mbid(ARTIST_ID)
                        .build();

        given(service.getArtist(ARTIST_ID))
            .willReturn(artistDto);

        //WHEN
        MvcResult result = mockMvc
                .perform(get( "/artist/" + ARTIST_ID))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        final var actual = objectMapper.readValue(result.getResponse().getContentAsString(), ArtistDto.class);

        //THEN
        assertThat(actual).isEqualTo(artistDto);
    }

    @ParameterizedTest
    @MethodSource("generateExceptions")
    void controllerThrowsException(final Exception exception, final int expectedHttpResponseCode) throws Exception {
        //GIVEN
        given(service.getArtist(ARTIST_ID))
                .willThrow(exception);

        //WHEN THEN
        mockMvc
                .perform(get( "/artist/" + ARTIST_ID))
                .andDo(print())
                .andExpect(status().is(expectedHttpResponseCode));
    }

    private static Stream<Arguments> generateExceptions() {
        return Stream.of(
                arguments(new CoverArtException(), HttpStatus.CONFLICT.value()),
                arguments(new CoverArtNotFoundException(), HttpStatus.NOT_FOUND.value()),
                arguments(new MusicBrainzException(), HttpStatus.CONFLICT.value()),
                arguments(new MusicBrainzNotFoundException(), HttpStatus.NOT_FOUND.value()),
                arguments(new WikidataException(), HttpStatus.CONFLICT.value()),
                arguments(new WikidataNotFoundException(), HttpStatus.NOT_FOUND.value()),
                arguments(new WikidataUrlNotPresentException(), HttpStatus.NOT_FOUND.value()),
                arguments(new WikipediaException(), HttpStatus.CONFLICT.value()),
                arguments(new WikipediaNotFoundException(), HttpStatus.NOT_FOUND.value())
        );
    }

}