package io.mosip.registration.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import io.mosip.registration.dto.DocumentDto;



@Service
public interface DocumentUploadService {

	Boolean uploadDoucment(MultipartFile file,DocumentDto documentDto) throws Exception;

}