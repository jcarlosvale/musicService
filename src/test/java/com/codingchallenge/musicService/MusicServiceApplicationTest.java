package com.codingchallenge.musicService;

import static org.assertj.core.api.Assertions.assertThat;

import com.codingchallenge.musicService.domain.dto.ArtistDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(webEnvironment= SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
class MusicServiceApplicationTest {

    private static final String ARTIST_ID = "e7273fcd-7530-48ba-bd11-1879f4a8920f";

    private RestTemplate restTemplate;
    private String url;

    @LocalServerPort
    private int randomServerPort = 0;

    @BeforeEach
    void beforeTest() {
        restTemplate = new RestTemplate();
        url = "http://localhost:" + randomServerPort;
    }

    @Test
    void infoAboutArtist() {
        //GIVEN WHEN
        final var actual = restTemplate.getForEntity(url+  "/artist/" + ARTIST_ID, ArtistDto.class).getBody();

        //THEN
        assertThat(actual).isNotNull();
        assertThat(actual.getAlbums().size()).isGreaterThan(0);
    }

}