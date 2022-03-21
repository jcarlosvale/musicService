package com.codingchallenge.musicService.service;

import com.codingchallenge.musicService.domain.dto.ArtistDto;
import com.codingchallenge.musicService.domain.exception.BusinessException;

public interface MusicService {

    ArtistDto getArtist(String id) throws BusinessException;
}
