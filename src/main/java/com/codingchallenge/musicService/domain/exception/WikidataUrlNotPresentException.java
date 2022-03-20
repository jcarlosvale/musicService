package com.codingchallenge.musicService.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "WikiData Url not present in Artist Info")
public class WikidataUrlNotPresentException extends BusinessException {
}
