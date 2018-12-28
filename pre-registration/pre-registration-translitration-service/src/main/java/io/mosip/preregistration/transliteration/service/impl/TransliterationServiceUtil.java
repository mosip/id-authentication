package io.mosip.preregistration.transliteration.service.impl;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import io.mosip.preregistration.transliteration.code.RequestCodes;
import io.mosip.preregistration.transliteration.dto.CreateTransliterationRequest;
import io.mosip.preregistration.transliteration.dto.TransliterationRequestDTO;

@Component
public class TransliterationServiceUtil {
	
	public Map<String, String> prepareRequestParamMap(
			TransliterationRequestDTO<CreateTransliterationRequest> demographicRequestDTO) {
		Map<String, String> inputValidation = new HashMap<>();
		inputValidation.put(RequestCodes.id.toString(), demographicRequestDTO.getId());
		inputValidation.put(RequestCodes.ver.toString(), demographicRequestDTO.getVer());
		inputValidation.put(RequestCodes.reqTime.toString(),
				new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(demographicRequestDTO.getReqTime()));
		inputValidation.put(RequestCodes.request.toString(), demographicRequestDTO.getRequest().toString());
		return inputValidation;
	}

}
