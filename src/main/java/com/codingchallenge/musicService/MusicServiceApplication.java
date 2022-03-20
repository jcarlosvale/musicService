package com.codingchallenge.musicService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class MusicServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(MusicServiceApplication.class, args);
	}
}
