package com.everis.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/account")
public class AccountController {
	 @GetMapping("/hola")
	 public Mono<ResponseEntity<String>> hola(){
		 return Mono.just(ResponseEntity
				 .ok()
				 .contentType(MediaType.APPLICATION_JSON)
				 .body("hola cuenta"));
	 }
}