package io.mosip.demo.authentication.service.impl.indauth.controller;

import org.apache.commons.codec.binary.Base64;

//import java.util.Base64;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Encode {

	@PostMapping(path = "/encode", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
	public String encode(@RequestBody String stringToEncode) {
		System.out.println("encode: "+stringToEncode.getBytes());
		return Base64.encodeBase64URLSafeString(stringToEncode.getBytes());
	}
}
