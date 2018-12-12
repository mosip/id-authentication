package io.mosip.demo.authentication.service.impl.indauth.controller;

import java.util.Base64;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

//	@RestController
public class Decode {

	@PostMapping(path = "/decode", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public String decode(@RequestBody String stringToDecode) {
		return new String(Base64.getDecoder().decode(stringToDecode));
	}
}
