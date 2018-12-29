package io.mosip.preregistration.transliteration.service.util;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import io.mosip.preregistration.transliteration.code.RequestCodes;
import io.mosip.preregistration.transliteration.dto.TransliterationApplicationDTO;
import io.mosip.preregistration.transliteration.dto.RequestDTO;

@Component
public class TransliterationServiceUtil {
	
	public Map<String, String> prepareRequestParamMap(
			RequestDTO<TransliterationApplicationDTO> requestDTO) {
		Map<String, String> inputValidation = new HashMap<>();
		inputValidation.put(RequestCodes.id.toString(), requestDTO.getId());
		inputValidation.put(RequestCodes.ver.toString(), requestDTO.getVer());
		inputValidation.put(RequestCodes.reqTime.toString(),
				new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(requestDTO.getReqTime()));
		inputValidation.put(RequestCodes.request.toString(), requestDTO.getRequest().toString());
		return inputValidation;
	}

}
