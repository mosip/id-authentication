package io.mosip.registration.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.registration.code.StatusCodes;
import io.mosip.registration.dto.DocumentDto;
import io.mosip.registration.exception.DocumentNotValidException;
import io.mosip.registration.service.DocumentUploadService;
import io.swagger.annotations.Api;

@RestController
@RequestMapping("/v0.1/pre-registration/registration/")
@Api(tags = "document Handler")
public class DocumentUploader {

	@Autowired
	private DocumentUploadService documentUploadService;

	@PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<StatusCodes> fileUpload(@RequestParam(value = "file", required = true) MultipartFile file,
			@RequestBody(required = true) DocumentDto documentDto) throws Throwable {

		if (file.getOriginalFilename().endsWith(".pdf") || file.getOriginalFilename().endsWith(".PDF")) {

			if (documentUploadService.uploadDoucment(file, documentDto)) {

				return ResponseEntity.ok().body(StatusCodes.DOCUMENT_UPLOADED_TO_DATABASE);

			}

		} else {

			return ResponseEntity.ok().body(StatusCodes.DOCUMENT_INVALID_FORMAT);
		}
		throw new DocumentNotValidException(StatusCodes.DOCUMENT_SIZE_GREATER_THAN_LIMIT.toString());

	}

}
