package com.codingchallenge.musicService.controller.impl;

import com.codingchallenge.musicService.controller.MusicServiceController;
import com.codingchallenge.musicService.domain.dto.ArtistDto;
import com.codingchallenge.musicService.domain.exception.BusinessException;
import com.codingchallenge.musicService.service.MusicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MusicServiceControllerImpl implements MusicServiceController {

    private final MusicService musicService;

    @GetMapping(path="/artist/{id}")
    public ResponseEntity<ArtistDto> infoAboutArtist(@PathVariable("id") String id)  throws BusinessException {
        return ResponseEntity.ok(musicService.getArtist(id));
    }
}
