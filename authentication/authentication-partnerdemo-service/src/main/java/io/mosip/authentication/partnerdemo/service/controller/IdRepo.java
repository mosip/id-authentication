package io.mosip.authentication.partnerdemo.service.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.node.ObjectNode;

import io.mosip.kernel.core.exception.BaseUncheckedException;
import io.mosip.kernel.core.jsonvalidator.exception.FileIOException;
import io.mosip.kernel.core.jsonvalidator.exception.JsonIOException;
import io.mosip.kernel.core.jsonvalidator.exception.JsonSchemaIOException;
import io.mosip.kernel.core.jsonvalidator.exception.JsonValidationProcessingException;
import io.mosip.kernel.core.jsonvalidator.spi.JsonValidator;
import io.mosip.kernel.core.util.CryptoUtil;

/**
 * The Class IdRepo.
 * @author Manoj S.P
 */
@RestController
public class IdRepo {

	/** The json validator. */
	@Autowired
	private JsonValidator jsonValidator;

	/**
	 * This method is used to validate the IdRepo Json format.
	 * @param object the object
	 * @return the string
	 */
	@PostMapping(path = "/validateJson", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
	public String jsonSchemaValidator(@RequestBody ObjectNode object) {
		try {
			if (jsonValidator.validateJson(object.toString()).isValid()) {
				return "success";
			} else {
				return "failed";
			}
		} catch (BaseUncheckedException | JsonValidationProcessingException | JsonIOException | JsonSchemaIOException
				| FileIOException e) {
			return e.getMessage();
		} 
	}

	/**
	 * Encodes the contents of cpeff file.
	 *
	 * @param file the file
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@PostMapping(value = "/encodeFile", produces = MediaType.TEXT_PLAIN_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public String encodeFile(@RequestPart MultipartFile file) throws IOException {
		return CryptoUtil.encodeBase64(file.getBytes());
	}

	/**
	 *Encodes the contents of cpeff file.
	 *
	 * @param stringToDecode the string to decode
	 * @param fileName the file name
	 * @return the response entity
	 */
	@PostMapping(path = "/decodeFile", consumes = MediaType.TEXT_PLAIN_VALUE, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	public ResponseEntity<InputStreamResource> decodeToFile(@RequestBody String stringToDecode,
			@RequestParam String fileName) {
		byte[] decodedFileData = CryptoUtil.decodeBase64(stringToDecode);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
		headers.add("Pragma", "no-cache");
		headers.add("Content-Disposition", "attachment; filename=" + fileName);
		headers.add("Expires", "0");
		InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(decodedFileData));
		return ResponseEntity.ok().headers(headers).contentLength(decodedFileData.length)
				.contentType(MediaType.parseMediaType("application/octet-stream")).body(resource);
	}

	
}
