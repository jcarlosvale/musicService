package com.codingchallenge.musicService.service.impl;

import com.codingchallenge.musicService.domain.dto.MusicBrainzDto;
import com.codingchallenge.musicService.domain.dto.RelationDto;
import com.codingchallenge.musicService.domain.dto.UrlDto;
import com.codingchallenge.musicService.domain.dto.WikidataDto;
import com.codingchallenge.musicService.domain.exception.MusicBrainzException;
import com.codingchallenge.musicService.domain.exception.MusicBrainzNotFoundException;
import com.codingchallenge.musicService.domain.exception.WikidataNotFoundException;
import com.codingchallenge.musicService.domain.exception.WikidataUrlNotPresentException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class MusicServiceImplTest {

    private static final String ARTIST_ID = "SOME ARTIST ID";
    private static final String WIKIDATA_ID = "SOME WIKI DATA ID";

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private HttpServerErrorException httpServerErrorException;

    @Mock
    private MusicBrainzDto musicBrainzDto;

    @Mock
    private RelationDto relationDto;

    @Mock
    private UrlDto urlDto;

    @InjectMocks
    private MusicServiceImpl service;


    @Test
    void getArtistNotAllowedIdNull() {
        //GIVEN WHEN THEN
        assertThatThrownBy(() -> service.getArtist(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    void getArtistMusicBrainzNotFound() {
        //GIVEN
        String url = "http://musicbrainz.org/ws/2/artist/" + ARTIST_ID + "?&fmt=json&inc=url-rels+release-groups";
        given(restTemplate.getForEntity(url, MusicBrainzDto.class))
                .willReturn(ResponseEntity.of(Optional.empty()));

        //WHEN THEN
        assertThatThrownBy(() -> service.getArtist(ARTIST_ID))
                .isInstanceOf(MusicBrainzNotFoundException.class);
    }

    @Test
    void getArtistMusicBrainzRaiseError() {
        //GIVEN
        String url = "http://musicbrainz.org/ws/2/artist/" + ARTIST_ID + "?&fmt=json&inc=url-rels+release-groups";

        given(httpServerErrorException.getStatusCode())
                .willReturn(HttpStatus.REQUEST_TIMEOUT);
        given(httpServerErrorException.getResponseBodyAsString())
                .willReturn("some body");
        given(restTemplate.getForEntity(url, MusicBrainzDto.class))
                .willThrow(httpServerErrorException);

        //WHEN THEN
        assertThatThrownBy(() -> service.getArtist(ARTIST_ID))
                .isInstanceOf(MusicBrainzException.class);
    }

    @Test
    void getArtistWikiDataNotPresent() {
        //GIVEN
        String url = "http://musicbrainz.org/ws/2/artist/" + ARTIST_ID + "?&fmt=json&inc=url-rels+release-groups";
        given(musicBrainzDto.getRelations())
                .willReturn(List.of());
        given(restTemplate.getForEntity(url, MusicBrainzDto.class))
                .willReturn(ResponseEntity.ok(musicBrainzDto));

        //WHEN THEN
        assertThatThrownBy(() -> service.getArtist(ARTIST_ID))
                .isInstanceOf(WikidataUrlNotPresentException.class);
    }

    @Test
    void getArtistWikipediaTitleNotFound() {
        //GIVEN
        String urlMusicBrainz = "http://musicbrainz.org/ws/2/artist/" + ARTIST_ID + "?&fmt=json&inc=url-rels+release-groups";
        String wikidataUrl = "https://www.wikidata.org/wiki/" + WIKIDATA_ID;
        String urlWikidata = "https://www.wikidata.org/wiki/Special:EntityData/" + WIKIDATA_ID + ".json";

        given(relationDto.getType())
                .willReturn("wikidata");
        given(urlDto.getResource())
                .willReturn(wikidataUrl);
        given(relationDto.getUrl())
                .willReturn(urlDto);
        given(musicBrainzDto.getRelations())
                .willReturn(List.of(relationDto));
        given(restTemplate.getForEntity(urlMusicBrainz, MusicBrainzDto.class))
                .willReturn(ResponseEntity.ok(musicBrainzDto));
        given(restTemplate.getForEntity(urlWikidata, WikidataDto.class))
                .willReturn(ResponseEntity.of(Optional.empty()));

        //WHEN THEN
        assertThatThrownBy(() -> service.getArtist(ARTIST_ID))
                .isInstanceOf(WikidataNotFoundException.class);
    }
}