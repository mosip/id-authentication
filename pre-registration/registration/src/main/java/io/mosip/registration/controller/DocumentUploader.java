package io.mosip.registration.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.util.JsonUtils;
import io.mosip.registration.code.StatusCodes;
import io.mosip.registration.dto.DocumentDto;
import io.mosip.registration.exception.DocumentNotValidException;
import io.mosip.registration.service.DocumentUploadService;
import io.swagger.annotations.Api;

@RestController
@RequestMapping("/v0.1/pre-registration/registration")
@Api(tags = "document Handler")
public class DocumentUploader {

	@Autowired
	private DocumentUploadService documentUploadService;

	@PostMapping(path = "/upload", consumes = { "multipart/form-data" })
	@ResponseBody
	public ResponseEntity<StatusCodes> fileUpload(@RequestParam(value = "documentString", required = true) String documentString,
			                    @RequestParam(value = "file", required = true) MultipartFile file
			) throws Throwable {

		System.out.println(documentString);
		/*String xv=JsonUtils.javaObjectToJsonString(new DocumentDto());
		if(xv.equals(documentString)) {
			System.out.println("$$$$$$$###########################");
		}
		System.out.println(JsonUtils.javaObjectToJsonString(new DocumentDto()));
		DocumentDto documentDto = (DocumentDto) JsonUtils.jsonStringToJavaObject(DocumentDto.class, documentString);
		System.out.println(documentDto);*/
		ObjectMapper mapper=new ObjectMapper();
		DocumentDto documentDto=mapper.readValue(documentString, DocumentDto.class);
		
		
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
