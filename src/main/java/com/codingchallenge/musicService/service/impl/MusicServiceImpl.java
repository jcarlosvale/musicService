package com.codingchallenge.musicService.service.impl;

import com.codingchallenge.musicService.domain.dto.AlbumDto;
import com.codingchallenge.musicService.domain.dto.ArtistDto;
import com.codingchallenge.musicService.domain.dto.CoverArtDto;
import com.codingchallenge.musicService.domain.dto.ImageDto;
import com.codingchallenge.musicService.domain.dto.MusicBrainzDto;
import com.codingchallenge.musicService.domain.dto.RelationDto;
import com.codingchallenge.musicService.domain.dto.ReleaseDto;
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
import com.codingchallenge.musicService.service.MusicService;
import io.github.resilience4j.retry.annotation.Retry;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Log4j2
@Service
@RequiredArgsConstructor
public class MusicServiceImpl implements MusicService {

    private static final String MUSIC_BRAINZ_URL =
            "http://musicbrainz.org/ws/2/artist/" + "%s" + "?&fmt=json&inc=url-rels+release-groups";

    private final RestTemplate restTemplate;

    @Cacheable("Artists")
    @Retry(name = "artistSearch")
    public ArtistDto getArtist(@NonNull final String id) throws BusinessException {

        log.info("Retrieving artist {}", id);

        final MusicBrainzDto musicBrainzDto = getArtistInfoFromMusicBrainz(id);
        final String wikidataUrl = extractWikiDataUrlFrom(musicBrainzDto);
        final String wikipediaTitle = getWikipediaTitle(wikidataUrl);
        final String wikipediaDescription = getWikipediaDescription(wikipediaTitle);
        final List<AlbumDto> albumDtos = getAlbumsFrom(musicBrainzDto);

        return ArtistDto.builder()
                .mbid(musicBrainzDto.getMbid())
                .name(musicBrainzDto.getName())
                .gender(musicBrainzDto.getGender())
                .country(musicBrainzDto.getCountry())
                .disambiguation(musicBrainzDto.getDisambiguation())
                .description(wikipediaDescription)
                .albums(albumDtos)
                .build();

    }

    private List<AlbumDto> getAlbumsFrom(final MusicBrainzDto musicBrainzDto) throws BusinessException {

        final List<AlbumDto> albumDtoList = new ArrayList<>();
        for (int i = 0; i < musicBrainzDto.getReleases().size(); i++) {
            ReleaseDto releaseDto = musicBrainzDto.getReleases().get(i);
            log.info("Retrieving album {} / {}", i+1, musicBrainzDto.getReleases().size());
            if (releaseDto.getPrimaryType().equals("Album")) {
                String imageUrl = getCoverArt(releaseDto.getId());
                albumDtoList.add(
                        AlbumDto.builder()
                                .id(releaseDto.getId())
                                .title(releaseDto.getTitle())
                                .imageUrl(imageUrl)
                                .build());
            }
        }
        return albumDtoList;
    }

    private String getCoverArt(String id) throws BusinessException {
        final String url = "https://coverartarchive.org/release-group/"+id;
        final ResponseEntity<CoverArtDto> response = performCall(url, CoverArtDto.class, new CoverArtException());

        if (Objects.isNull(response) || Objects.isNull(response.getBody()) || Objects.isNull(response.getBody().getImageDtoList())) {
            return null;
        }

        CoverArtDto coverArtDto = response.getBody();
        for(ImageDto imageDto: coverArtDto.getImageDtoList()) {
            if (imageDto.isFront()) {
                return imageDto.getImage();
            }
        }
        return null;
    }

    private String getWikipediaDescription(final String wikipediaTitle) throws BusinessException {
        final String url = "https://en.wikipedia.org/api/rest_v1/page/summary/"+wikipediaTitle;
        final ResponseEntity<WikipediaDto> response = performCall(url, WikipediaDto.class, new WikipediaException());
        if (Objects.isNull(response.getBody())) {
            throw new WikipediaNotFoundException();
        }
        return response.getBody().getDescription();
    }

    private String getWikipediaTitle(final String wikidataUrl) throws BusinessException {

        final String wikidataId = wikidataUrl.replace("https://www.wikidata.org/wiki/", "").trim();
        final String url = "https://www.wikidata.org/wiki/Special:EntityData/" + wikidataId + ".json";
        final ResponseEntity<WikidataDto> response = performCall(url, WikidataDto.class, new WikidataException());
        if (!Objects.isNull(response) && !Objects.isNull(response.getBody()) && !Objects.isNull(response.getBody().getEntitiesMap())) {
            return response.getBody().getEntitiesMap().get(wikidataId);
        } else {
            throw new WikidataNotFoundException();
        }
    }

    private String extractWikiDataUrlFrom(@NonNull final MusicBrainzDto musicBrainzDto) throws WikidataUrlNotPresentException {

        for(RelationDto relationDto : musicBrainzDto.getRelations()) {
            if (relationDto.getType().equals("wikidata")) {
                return relationDto.getUrl().getResource();
            }
        }

        log.error("WikiData not present for Artist - {}, mbid - {}", musicBrainzDto.getName(), musicBrainzDto.getMbid());
        throw new WikidataUrlNotPresentException();
    }

    private MusicBrainzDto getArtistInfoFromMusicBrainz(final String id) throws BusinessException {
        final String url = String.format(MUSIC_BRAINZ_URL, id);
        ResponseEntity<MusicBrainzDto> response = performCall(url, MusicBrainzDto.class, new MusicBrainzException());
        if (Objects.isNull(response) || Objects.isNull(response.getBody())) {
            throw new MusicBrainzNotFoundException();
        }
        return response.getBody();
    }

    private <T> ResponseEntity<T> performCall(String url, Class<T> clazz, BusinessException serviceException) throws BusinessException {
        try {
            return restTemplate.getForEntity(url, clazz);
        } catch (Exception e) {
            log.error("Exception error: [{}]", e.getMessage());
            if (e instanceof HttpStatusCodeException) {
                HttpStatusCodeException httpStatusCodeException = (HttpStatusCodeException) e;
                log.error("Response from {}: [{} - {}]", url, httpStatusCodeException.getStatusCode(),
                        httpStatusCodeException.getResponseBodyAsString());
                if (httpStatusCodeException.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                    return null;
                }
            }
            throw serviceException;
        }
    }
}
