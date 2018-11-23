package io.mosip.demo.authentication.service.impl.indauth.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PDFGenerator {

	private static final String PDF_PATH = "e-KYC.pdf";

	@PostMapping(path = "/decodeToFile", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_PDF_VALUE)
	public ResponseEntity<InputStreamResource> decode(@RequestBody String stringToDecode) throws IOException {
		byte[] decode = Base64.getDecoder().decode(stringToDecode);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
		headers.add("Pragma", "no-cache");
		headers.add("Content-Disposition", "attachment; filename=" + PDF_PATH);
		headers.add("Expires", "0");
		InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(decode));
		return ResponseEntity.ok().headers(headers).contentLength(decode.length)
				.contentType(MediaType.parseMediaType("application/octet-stream")).body(resource);
	}
}
