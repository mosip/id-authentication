package io.mosip.preregistration.transliteration.service.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import io.mosip.kernel.core.util.DateUtils;
import io.mosip.preregistration.transliteration.code.RequestCodes;
import io.mosip.preregistration.transliteration.dto.MainRequestDTO;
import io.mosip.preregistration.transliteration.dto.TransliterationDTO;

@Component
public class TransliterationServiceUtil {
	
	private String dateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	
	public Map<String, String> prepareRequestParamMap(
			MainRequestDTO<TransliterationDTO> requestDTO) {
		Map<String, String> inputValidation = new HashMap<>();
		inputValidation.put(RequestCodes.ID.getCode(), requestDTO.getId());
		inputValidation.put(RequestCodes.VER.getCode(), requestDTO.getVer());
		inputValidation.put(RequestCodes.REQ_TIME.getCode(),
				getDateString(requestDTO.getReqTime()));
		inputValidation.put(RequestCodes.REQUEST.getCode(), requestDTO.getRequest().toString());
		return inputValidation;
	}
	
	public boolean isEntryFieldsNull(TransliterationDTO requestFields) {
		return (!requestFields.getFromFieldLang().equals("") && !requestFields.getFromFieldValue().equals("")
				&& !requestFields.getFromFieldName().equals("") && !requestFields.getToFieldLang().equals("")
				&& !requestFields.getToFieldName().equals(""));
	}
	
	public String getCurrentResponseTime() {
		return DateUtils.formatDate(new Date(System.currentTimeMillis()), dateTimeFormat);
	}

	public String getDateString(Date date) {
		return DateUtils.formatDate(date, dateTimeFormat);
	}

	public TransliterationDTO responseSetter(String value,
			TransliterationDTO transliterationRequestDTO) {
		TransliterationDTO transliterationResponseDTO = new TransliterationDTO();
		transliterationResponseDTO.setFromFieldName(transliterationRequestDTO.getFromFieldName());
		transliterationResponseDTO.setFromFieldValue(transliterationRequestDTO.getFromFieldValue());
		transliterationResponseDTO.setFromFieldLang(transliterationRequestDTO.getFromFieldLang());
		transliterationResponseDTO.setToFieldName(transliterationRequestDTO.getToFieldName());
		transliterationResponseDTO.setToFieldValue(value);
		transliterationResponseDTO.setToFieldLang(transliterationRequestDTO.getToFieldLang());
		return transliterationResponseDTO;
	}

}
