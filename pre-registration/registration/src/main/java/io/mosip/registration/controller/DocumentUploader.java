package io.mosip.registration.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.kernel.core.util.JsonUtils;
import io.mosip.kernel.core.util.exception.MosipIOException;
import io.mosip.kernel.core.util.exception.MosipJsonMappingException;
import io.mosip.kernel.core.util.exception.MosipJsonParseException;
import io.mosip.registration.code.StatusCodes;
import io.mosip.registration.dto.DocumentDto;
import io.mosip.registration.service.DocumentUploadService;
import io.swagger.annotations.Api;

/**
 * Document upload controller
 * 
 * @author M1043008
 *
 */
@RestController
@RequestMapping("/v0.1/pre-registration/registration/")
@Api(tags = "document Handler")
public class DocumentUploader {

	@Autowired
	private DocumentUploadService documentUploadService;

	@PostMapping(path = "/upload", consumes = { "multipart/form-data" })
	@ResponseBody
	public ResponseEntity<StatusCodes> fileUpload(
			@RequestPart(value = "documentString", required = true) String documentString,
			@RequestPart(value = "file", required = true) MultipartFile file)
			throws MosipJsonParseException, MosipJsonMappingException, MosipIOException {

		DocumentDto documentDto = (DocumentDto) JsonUtils.jsonStringToJavaObject(DocumentDto.class, documentString);

		documentUploadService.uploadDoucment(file, documentDto);

		return ResponseEntity.ok().body(StatusCodes.DOCUMENT_UPLOADED_TO_DATABASE);

	}

}
