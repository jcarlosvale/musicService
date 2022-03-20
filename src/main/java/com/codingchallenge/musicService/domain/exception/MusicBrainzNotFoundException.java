package com.codingchallenge.musicService.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Artist not found in Music Brainz Service.")
public class MusicBrainzNotFoundException extends BusinessException {
}
