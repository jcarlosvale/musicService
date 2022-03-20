package com.codingchallenge.musicService.controller;

import com.codingchallenge.musicService.domain.dto.ArtistDto;
import com.codingchallenge.musicService.domain.exception.BusinessException;
import org.springframework.http.ResponseEntity;

public interface MusicServiceController {

    ResponseEntity<ArtistDto> infoAboutArtist(String id) throws BusinessException;

}
