package io.mosip.registration.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.registration.code.StatusCodes;
import io.mosip.registration.dto.DocumentDto;
import io.mosip.registration.service.DocumentUploadService;
import io.swagger.annotations.Api;

@RestController
@RequestMapping("/v0.1/pre-registration/registration/document")
@Api(tags = "document Handler")
public class DocumentUploader {

	@Autowired
	private DocumentUploadService documentUploadService;

	@PostMapping(path = "/upload", consumes = { "multipart/form-data" })
	@ResponseBody
	public ResponseEntity<StatusCodes> fileUpload(
			@RequestPart(value = "documentString", required = true) String documentString,
			@RequestPart(value = "file", required = true) MultipartFile file) 
					throws JsonParseException, JsonMappingException, IOException {

		ObjectMapper mapper = new ObjectMapper();
		DocumentDto documentDto = mapper.readValue(documentString, DocumentDto.class);

		documentUploadService.uploadDoucment(file, documentDto);

		return ResponseEntity.ok().body(StatusCodes.DOCUMENT_UPLOADED_TO_DATABASE);

	}

}
