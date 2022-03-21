package com.codingchallenge.musicService.controller.impl;

import com.codingchallenge.musicService.controller.MusicServiceController;
import com.codingchallenge.musicService.domain.dto.ArtistDto;
import com.codingchallenge.musicService.domain.exception.BusinessException;
import com.codingchallenge.musicService.service.MusicService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MusicServiceControllerImpl implements MusicServiceController {

    private final MusicService musicService;

    @Operation(summary = "Collect info about an artist by MBID")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Artist information",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ArtistDto.class))}),
            @ApiResponse(
                    responseCode = "404",
                    description = "Information not found.",
                    content = @Content),
            @ApiResponse(
                    responseCode = "409",
                    description = "Third service error.",
                    content = @Content)})
    @GetMapping(path="/artist/{id}", produces = "application/json")
    public ResponseEntity<ArtistDto> infoAboutArtist(
            @Parameter(description = "artist MBID")
            @PathVariable("id") String id)  throws BusinessException {
        return ResponseEntity.ok(musicService.getArtist(id));
    }
}
