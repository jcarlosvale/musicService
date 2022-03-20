package com.codingchallenge.musicService.service;

import com.codingchallenge.musicService.domain.dto.*;
import com.codingchallenge.musicService.domain.exception.*;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Log4j2
@Service
@RequiredArgsConstructor
public class MusicService {

    private final static String MUSIC_BRAINZ_URL =
            "http://musicbrainz.org/ws/2/artist/" + "%s" + "?&fmt=json&inc=url-rels+release-groups";

    private final RestTemplate restTemplate;

    @Cacheable("Artists")
    public ArtistDto getArtist(@NonNull final String id) throws BusinessException {

        log.info("Retrieving artist {}", id);

        final MusicBrainzDto musicBrainzDto = getArtistInfoFromMusicBrainz(id);
        final String wikidataUrl = extractWikiDataIdFrom(musicBrainzDto);
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

    private List<AlbumDto> getAlbumsFrom(final MusicBrainzDto musicBrainzDto) throws CoverArtNotFoundException, CoverArtException {

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

    private String getCoverArt(String id) throws CoverArtNotFoundException, CoverArtException {
        try {

            final String url = "https://coverartarchive.org/release-group/"+id;
            final ResponseEntity<CoverArtDto> response = restTemplate.getForEntity(url, CoverArtDto.class);

            if (Objects.isNull(response.getBody()) || Objects.isNull(response.getBody().getImageDtoList())) {
                return null;
            }
            CoverArtDto coverArtDto = response.getBody();
            for(ImageDto imageDto: coverArtDto.getImageDtoList()) {
                if (imageDto.isFront()) {
                    return imageDto.getImage();
                }
            }
            return null;
        } catch (Exception e) {

            if (e instanceof HttpStatusCodeException) {
                HttpStatusCodeException httpStatusCodeException = (HttpStatusCodeException) e;
                log.error("Response from CoverArt: [{} - {}]", httpStatusCodeException.getStatusCode(),
                        httpStatusCodeException.getResponseBodyAsString());
                if (httpStatusCodeException.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                    return null;
                } else {
                    throw new CoverArtException();
                }
            } else {
                log.error("Response from CoverArt: [{}]", e.getMessage());
                throw new CoverArtException();
            }

        }
    }

    private String getWikipediaDescription(final String wikipediaTitle) throws WikipediaNotFoundException, WikipediaException {
        try {
            final String url = "https://en.wikipedia.org/api/rest_v1/page/summary/"+wikipediaTitle;
            final ResponseEntity<WikipediaDto> response = restTemplate.getForEntity(url, WikipediaDto.class);
            if (Objects.isNull(response.getBody())) {
                throw new WikipediaNotFoundException();
            }
            return response.getBody().getDescription();
        } catch (HttpStatusCodeException httpStatusCodeException) {
            log.error("Response from Wikipedia: [{} - {}]", httpStatusCodeException.getStatusCode(),
                    httpStatusCodeException.getResponseBodyAsString());
            if (httpStatusCodeException.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                throw new WikipediaNotFoundException();
            } else {
                throw new WikipediaException();
            }
        }
    }

    private String getWikipediaTitle(final String wikidataUrl) throws WikidataNotFoundException, WikidataException {
        try {
            final String wikidataId = wikidataUrl.replace("https://www.wikidata.org/wiki/", "").trim();
            final String url = "https://www.wikidata.org/wiki/Special:EntityData/" + wikidataId + ".json";
            final ResponseEntity<WikidataDto> response = restTemplate.getForEntity(url, WikidataDto.class);
            final WikidataDto wikidataDto = response.getBody();
            if(Objects.isNull(wikidataDto) || Objects.isNull(wikidataDto.getEntitiesMap())) {
                throw new WikidataNotFoundException();
            }
            return wikidataDto.getEntitiesMap().get(wikidataId);
        } catch (HttpStatusCodeException httpStatusCodeException) {
            log.error("Response from Wikidata: [{} - {}]", httpStatusCodeException.getStatusCode(), httpStatusCodeException.getResponseBodyAsString());
            if (httpStatusCodeException.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                throw new WikidataNotFoundException();
            } else {
                throw new WikidataException();
            }
        }
    }

    private String extractWikiDataIdFrom(final MusicBrainzDto musicBrainzDto) throws WikidataUrlNotPresentException {

        for(RelationDto relationDto : musicBrainzDto.getRelations()) {
            if (relationDto.getType().equals("wikidata")) {
                return relationDto.getUrl().getResource();
            }
        }

        log.error("WikiData not present for Artist - {}, mbid - {}", musicBrainzDto.getName(), musicBrainzDto.getMbid());
        throw new WikidataUrlNotPresentException();
    }

    private MusicBrainzDto getArtistInfoFromMusicBrainz(final String id) throws MusicBrainzNotFoundException, MusicBrainzException {
        try {
            final String url = String.format(MUSIC_BRAINZ_URL, id);
            ResponseEntity<MusicBrainzDto> response = restTemplate.getForEntity(url, MusicBrainzDto.class);
            if (Objects.isNull(response.getBody())) {
                throw new MusicBrainzNotFoundException();
            }
            return response.getBody();
        } catch (HttpStatusCodeException httpStatusCodeException) {
            log.error("Response from MusicBrainz: [{} - {}]", httpStatusCodeException.getStatusCode(),
                    httpStatusCodeException.getResponseBodyAsString());
            if (httpStatusCodeException.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                throw new MusicBrainzNotFoundException();
            } else {
                throw new MusicBrainzException();
            }
        }
    }

    @CacheEvict(allEntries = true, cacheNames = { "Artists" })
    @Scheduled(fixedDelay = 5000)
    public void cacheEvict() {
    }
}
