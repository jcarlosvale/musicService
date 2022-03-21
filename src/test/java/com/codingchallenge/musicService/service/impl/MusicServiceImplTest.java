package com.codingchallenge.musicService.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.codingchallenge.musicService.domain.dto.CoverArtDto;
import com.codingchallenge.musicService.domain.dto.ImageDto;
import com.codingchallenge.musicService.domain.dto.MusicBrainzDto;
import com.codingchallenge.musicService.domain.dto.RelationDto;
import com.codingchallenge.musicService.domain.dto.ReleaseDto;
import com.codingchallenge.musicService.domain.dto.UrlDto;
import com.codingchallenge.musicService.domain.dto.WikidataDto;
import com.codingchallenge.musicService.domain.dto.WikipediaDto;
import com.codingchallenge.musicService.domain.exception.BusinessException;
import com.codingchallenge.musicService.domain.exception.CoverArtException;
import com.codingchallenge.musicService.domain.exception.MusicBrainzException;
import com.codingchallenge.musicService.domain.exception.MusicBrainzNotFoundException;
import com.codingchallenge.musicService.domain.exception.WikidataException;
import com.codingchallenge.musicService.domain.exception.WikidataNotFoundException;
import com.codingchallenge.musicService.domain.exception.WikidataUrlNotPresentException;
import com.codingchallenge.musicService.domain.exception.WikipediaException;
import com.codingchallenge.musicService.domain.exception.WikipediaNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class MusicServiceImplTest {

    private static final String ARTIST_ID = "SOME ARTIST ID";
    private static final String WIKIDATA_ID = "SOME WIKI DATA ID";
    private static final String WIKIPEDIA_TITLE = "SOME WIKIPEDIA TITLE";
    private static final String RELEASE_ID_1 = "SOME RELEASE ID 1";
    private static final String RELEASE_ID_2 = "SOME RELEASE ID 2";
    private static final String RELEASE_ID_3 = "SOME RELEASE ID 3";
    private static final String WIKIPEDIA_DESCRIPTION = "SOME WIKIPEDIA DESCRIPTION";

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

    @Mock
    private WikidataDto wikidataDto;

    @Mock
    private WikipediaDto wikipediaDto;

    @Mock
    private ReleaseDto releaseDto1;

    @Mock
    private ReleaseDto releaseDto2;

    @Mock
    private CoverArtDto coverArtDto1;

    @Mock
    private CoverArtDto coverArtDto2;

    @Mock
    private CoverArtDto coverArtDto3;

    @Mock
    private ImageDto imageDto1;

    @Mock
    private ImageDto imageDto2;

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

    @Test
    void getArtistWikipediaTitleRaisesError() {
        //GIVEN
        String urlMusicBrainz = "http://musicbrainz.org/ws/2/artist/" + ARTIST_ID + "?&fmt=json&inc=url-rels+release-groups";
        String wikidataUrl = "https://www.wikidata.org/wiki/" + WIKIDATA_ID;
        String urlWikidata = "https://www.wikidata.org/wiki/Special:EntityData/" + WIKIDATA_ID + ".json";

        given(httpServerErrorException.getStatusCode())
                .willReturn(HttpStatus.REQUEST_TIMEOUT);
        given(httpServerErrorException.getResponseBodyAsString())
                .willReturn("some body");
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
                .willThrow(httpServerErrorException);

        //WHEN THEN
        assertThatThrownBy(() -> service.getArtist(ARTIST_ID))
                .isInstanceOf(WikidataException.class);
    }

    @Test
    void getArtistWikipediaDescriptionNotFound() {
        //GIVEN
        String urlMusicBrainz = "http://musicbrainz.org/ws/2/artist/" + ARTIST_ID + "?&fmt=json&inc=url-rels+release-groups";
        String wikidataUrl = "https://www.wikidata.org/wiki/" + WIKIDATA_ID;
        String urlWikidata = "https://www.wikidata.org/wiki/Special:EntityData/" + WIKIDATA_ID + ".json";
        String urlWikipedia = "https://en.wikipedia.org/api/rest_v1/page/summary/" + WIKIPEDIA_TITLE;

        given(relationDto.getType())
                .willReturn("wikidata");
        given(urlDto.getResource())
                .willReturn(wikidataUrl);
        given(relationDto.getUrl())
                .willReturn(urlDto);
        given(wikidataDto.getEntitiesMap())
                .willReturn(Map.of(WIKIDATA_ID, WIKIPEDIA_TITLE));
        given(musicBrainzDto.getRelations())
                .willReturn(List.of(relationDto));
        given(restTemplate.getForEntity(urlMusicBrainz, MusicBrainzDto.class))
                .willReturn(ResponseEntity.ok(musicBrainzDto));
        given(restTemplate.getForEntity(urlWikidata, WikidataDto.class))
                .willReturn(ResponseEntity.ok(wikidataDto));
        given(restTemplate.getForEntity(urlWikipedia, WikipediaDto.class))
                .willReturn(ResponseEntity.of(Optional.empty()));

        //WHEN THEN
        assertThatThrownBy(() -> service.getArtist(ARTIST_ID))
                .isInstanceOf(WikipediaNotFoundException.class);
    }

    @Test
    void getArtistWikipediaDescriptionRaisesError() {
        //GIVEN
        String urlMusicBrainz = "http://musicbrainz.org/ws/2/artist/" + ARTIST_ID + "?&fmt=json&inc=url-rels+release-groups";
        String wikidataUrl = "https://www.wikidata.org/wiki/" + WIKIDATA_ID;
        String urlWikidata = "https://www.wikidata.org/wiki/Special:EntityData/" + WIKIDATA_ID + ".json";
        String urlWikipedia = "https://en.wikipedia.org/api/rest_v1/page/summary/" + WIKIPEDIA_TITLE;

        given(httpServerErrorException.getStatusCode())
                .willReturn(HttpStatus.REQUEST_TIMEOUT);
        given(httpServerErrorException.getResponseBodyAsString())
                .willReturn("some body");
        given(relationDto.getType())
                .willReturn("wikidata");
        given(urlDto.getResource())
                .willReturn(wikidataUrl);
        given(relationDto.getUrl())
                .willReturn(urlDto);
        given(wikidataDto.getEntitiesMap())
                .willReturn(Map.of(WIKIDATA_ID, WIKIPEDIA_TITLE));
        given(musicBrainzDto.getRelations())
                .willReturn(List.of(relationDto));
        given(restTemplate.getForEntity(urlMusicBrainz, MusicBrainzDto.class))
                .willReturn(ResponseEntity.ok(musicBrainzDto));
        given(restTemplate.getForEntity(urlWikidata, WikidataDto.class))
                .willReturn(ResponseEntity.ok(wikidataDto));
        given(restTemplate.getForEntity(urlWikipedia, WikipediaDto.class))
                .willThrow(httpServerErrorException);

        //WHEN THEN
        assertThatThrownBy(() -> service.getArtist(ARTIST_ID))
                .isInstanceOf(WikipediaException.class);
    }

    @Test
    void getArtistCoverArtsRaisesError() {
        //GIVEN
        String urlMusicBrainz = "http://musicbrainz.org/ws/2/artist/" + ARTIST_ID + "?&fmt=json&inc=url-rels+release-groups";
        String wikidataUrl = "https://www.wikidata.org/wiki/" + WIKIDATA_ID;
        String urlWikidata = "https://www.wikidata.org/wiki/Special:EntityData/" + WIKIDATA_ID + ".json";
        String urlWikipedia = "https://en.wikipedia.org/api/rest_v1/page/summary/" + WIKIPEDIA_TITLE;
        String urlCoverArt1 = "https://coverartarchive.org/release-group/"+RELEASE_ID_1;
        String urlCoverArt2 = "https://coverartarchive.org/release-group/"+RELEASE_ID_2;

        List<ReleaseDto> releaseDtoList = List.of(releaseDto1, releaseDto2);

        given(httpServerErrorException.getStatusCode())
                .willReturn(HttpStatus.REQUEST_TIMEOUT);
        given(httpServerErrorException.getResponseBodyAsString())
                .willReturn("some body");
        given(relationDto.getType())
                .willReturn("wikidata");
        given(urlDto.getResource())
                .willReturn(wikidataUrl);
        given(relationDto.getUrl())
                .willReturn(urlDto);
        given(wikidataDto.getEntitiesMap())
                .willReturn(Map.of(WIKIDATA_ID, WIKIPEDIA_TITLE));
        given(wikipediaDto.getDescription())
                .willReturn(WIKIPEDIA_DESCRIPTION);
        given(musicBrainzDto.getRelations())
                .willReturn(List.of(relationDto));
        given(musicBrainzDto.getReleases())
                .willReturn(releaseDtoList);
        given(releaseDto1.getId())
                .willReturn(RELEASE_ID_1);
        given(releaseDto1.getPrimaryType())
                .willReturn("Album");
        given(releaseDto1.getTitle())
                .willReturn("Title 1");
        given(releaseDto2.getId())
                .willReturn(RELEASE_ID_2);
        given(releaseDto2.getPrimaryType())
                .willReturn("Album");
        given(restTemplate.getForEntity(urlMusicBrainz, MusicBrainzDto.class))
                .willReturn(ResponseEntity.ok(musicBrainzDto));
        given(restTemplate.getForEntity(urlWikidata, WikidataDto.class))
                .willReturn(ResponseEntity.ok(wikidataDto));
        given(restTemplate.getForEntity(urlWikipedia, WikipediaDto.class))
                .willReturn(ResponseEntity.ok(wikipediaDto));
        given(restTemplate.getForEntity(urlCoverArt1, CoverArtDto.class))
                .willReturn(ResponseEntity.ok(coverArtDto1));
        given(restTemplate.getForEntity(urlCoverArt2, CoverArtDto.class))
                .willThrow(httpServerErrorException);

        //WHEN THEN
        assertThatThrownBy(() -> service.getArtist(ARTIST_ID))
                .isInstanceOf(CoverArtException.class);
    }

    @Test
    void getArtistOk() throws BusinessException {
        //GIVEN
        String urlMusicBrainz = "http://musicbrainz.org/ws/2/artist/" + ARTIST_ID + "?&fmt=json&inc=url-rels+release-groups";
        String wikidataUrl = "https://www.wikidata.org/wiki/" + WIKIDATA_ID;
        String urlWikidata = "https://www.wikidata.org/wiki/Special:EntityData/" + WIKIDATA_ID + ".json";
        String urlWikipedia = "https://en.wikipedia.org/api/rest_v1/page/summary/" + WIKIPEDIA_TITLE;
        String urlCoverArt1 = "https://coverartarchive.org/release-group/"+RELEASE_ID_1;
        String urlCoverArt2 = "https://coverartarchive.org/release-group/"+RELEASE_ID_2;
        String urlCoverArt3 = "https://coverartarchive.org/release-group/"+RELEASE_ID_3;

        String image1 = "image 1";
        String image2 = "image 2";

        ReleaseDto releaseDto3 = mock(ReleaseDto.class);
        List<ReleaseDto> releaseDtoList = List.of(releaseDto1, releaseDto2, releaseDto3);

        given(relationDto.getType())
                .willReturn("wikidata");
        given(urlDto.getResource())
                .willReturn(wikidataUrl);
        given(relationDto.getUrl())
                .willReturn(urlDto);
        given(wikidataDto.getEntitiesMap())
                .willReturn(Map.of(WIKIDATA_ID, WIKIPEDIA_TITLE));
        given(wikipediaDto.getDescription())
                .willReturn(WIKIPEDIA_DESCRIPTION);
        given(musicBrainzDto.getRelations())
                .willReturn(List.of(relationDto));
        given(musicBrainzDto.getReleases())
                .willReturn(releaseDtoList);
        given(releaseDto1.getId())
                .willReturn(RELEASE_ID_1);
        given(releaseDto1.getPrimaryType())
                .willReturn("Album");
        given(releaseDto1.getTitle())
                .willReturn("Title 1");
        given(releaseDto2.getId())
                .willReturn(RELEASE_ID_2);
        given(releaseDto2.getPrimaryType())
                .willReturn("Album");
        given(releaseDto2.getTitle())
                .willReturn("Title 2");
        given(releaseDto3.getPrimaryType())
                .willReturn("Not Album");
        given(imageDto1.isFront())
                .willReturn(true);
        given(imageDto1.getImage())
                .willReturn(image1);
        given(imageDto2.isFront())
                .willReturn(true);
        given(imageDto2.getImage())
                .willReturn(image2);
        given(coverArtDto1.getImageDtoList())
                .willReturn(List.of(imageDto1));
        given(coverArtDto2.getImageDtoList())
                .willReturn(List.of(imageDto2));
        given(restTemplate.getForEntity(urlMusicBrainz, MusicBrainzDto.class))
                .willReturn(ResponseEntity.ok(musicBrainzDto));
        given(restTemplate.getForEntity(urlWikidata, WikidataDto.class))
                .willReturn(ResponseEntity.ok(wikidataDto));
        given(restTemplate.getForEntity(urlWikipedia, WikipediaDto.class))
                .willReturn(ResponseEntity.ok(wikipediaDto));
        given(restTemplate.getForEntity(urlCoverArt1, CoverArtDto.class))
                .willReturn(ResponseEntity.ok(coverArtDto1));
        given(restTemplate.getForEntity(urlCoverArt2, CoverArtDto.class))
                .willReturn(ResponseEntity.ok(coverArtDto2));

        //WHEN
        final var dto = service.getArtist(ARTIST_ID);

        //THEN
        assertThat(dto.getMbid())
                .isEqualTo(musicBrainzDto.getMbid());
        assertThat(dto.getName())
                .isEqualTo(musicBrainzDto.getName());
        assertThat(dto.getGender())
                .isEqualTo(musicBrainzDto.getGender());
        assertThat(dto.getCountry())
                .isEqualTo(musicBrainzDto.getCountry());
        assertThat(dto.getDisambiguation())
                .isEqualTo(musicBrainzDto.getDisambiguation());
        assertThat(dto.getDescription())
                .isEqualTo(WIKIPEDIA_DESCRIPTION);
        assertThat(dto.getAlbums().size())
                .isEqualTo(2);
        assertThat(dto.getAlbums().get(0).getId())
                .isEqualTo(releaseDto1.getId());
        assertThat(dto.getAlbums().get(0).getTitle())
                .isEqualTo(releaseDto1.getTitle());
        assertThat(dto.getAlbums().get(0).getImageUrl())
                .isEqualTo(image1);
        assertThat(dto.getAlbums().get(1).getId())
                .isEqualTo(releaseDto2.getId());
        assertThat(dto.getAlbums().get(1).getTitle())
                .isEqualTo(releaseDto2.getTitle());
        assertThat(dto.getAlbums().get(1).getImageUrl())
                .isEqualTo(image2);
    }
}
