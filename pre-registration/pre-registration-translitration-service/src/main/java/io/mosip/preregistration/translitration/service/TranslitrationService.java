package io.mosip.preregistration.translitration.service;

import org.springframework.stereotype.Service;

import io.mosip.preregistration.translitration.dto.CreateTranslitrationRequest;
import io.mosip.preregistration.translitration.dto.ResponseDTO;
import io.mosip.preregistration.translitration.dto.TranslitrationRequestDTO;

@Service
public interface TranslitrationService {

	ResponseDTO<String> translitrator(TranslitrationRequestDTO<CreateTranslitrationRequest> requestDTO);

}