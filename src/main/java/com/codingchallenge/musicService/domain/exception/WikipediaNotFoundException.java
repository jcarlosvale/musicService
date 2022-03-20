package com.codingchallenge.musicService.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Wikipedia data not found.")
public class WikipediaNotFoundException extends BusinessException {
}
