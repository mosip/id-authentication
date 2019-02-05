package io.mosip.demo.authentication.service.impl.indauth.controller;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author M1046368
 * The Class Decode.
 */
@RestController
public class Decode {

	/**
	 * Decode.
	 *
	 * @param stringToDecode the string to decode
	 * @return the string
	 */
	@PostMapping(path = "/decode", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public String decode(@RequestBody String stringToDecode) {
		return new String(Base64.getDecoder().decode(stringToDecode), StandardCharsets.UTF_8);
	}
}
