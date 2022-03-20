package com.codingchallenge.musicService.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT, reason = "Error processing Wikipedia Service")
public class WikipediaException extends BusinessException {
}
