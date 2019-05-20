/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.transliteration.service.util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.util.DateUtils;
import io.mosip.preregistration.core.common.dto.MainRequestDTO;
import io.mosip.preregistration.transliteration.code.RequestCodes;
import io.mosip.preregistration.transliteration.dto.TransliterationRequestDTO;
import io.mosip.preregistration.transliteration.dto.TransliterationResponseDTO;

/**
 * This class provides the utility methods for Transliteration application.
 * 
 * @author Kishan Rathore
 * @since 1.0.0
 *
 */
@Component
public class TransliterationServiceUtil {
	
	
	@Value("${mosip.utc-datetime-pattern}")
	private String utcDateTimePattern;
	
	@Value("#{'${mosip.supported-languages}'.split(',')}")
	private List<String> supportedLang;
	
	/**
	 * This method is used to add the initial request values into a map for input
	 * validations.
	 * 
	 * @param MainRequestDTO 
	 * 						pass requestDTO
	 * @return a map for request input validation
	 */
	public Map<String, String> prepareRequestParamMap(
			MainRequestDTO<TransliterationRequestDTO> requestDTO) {
		Map<String, String> inputValidation = new HashMap<>();
		inputValidation.put(RequestCodes.ID.getCode(), requestDTO.getId());
		inputValidation.put(RequestCodes.VER.getCode(), requestDTO.getVersion());
		if(!(requestDTO.getRequesttime()==null || requestDTO.getRequesttime().toString().isEmpty())) {
			LocalDate date = requestDTO.getRequesttime().toInstant().atZone(ZoneId.of("UTC")).toLocalDate();
			inputValidation.put(RequestCodes.REQ_TIME.getCode(), date.toString());
		}
		else {
			inputValidation.put(RequestCodes.REQ_TIME.getCode(),null);
		}
		inputValidation.put(RequestCodes.REQUEST.getCode(), requestDTO.getRequest().toString());
		return inputValidation;
	}
	
	public boolean isEntryFieldsNull(TransliterationRequestDTO requestFields) {
		return (!requestFields.getFromFieldLang().equals("") && !requestFields.getFromFieldValue().equals("")
				 && !requestFields.getToFieldLang().equals(""));
	}
	
	/**
	 * @return date.
	 */
	public String getCurrentResponseTime() {
		return DateUtils.formatDate(new Date(System.currentTimeMillis()), utcDateTimePattern);
	}

	/**
	 * @param date
	 * @return date in string.
	 */
	public String getDateString(Date date) {
		return DateUtils.formatDate(date, utcDateTimePattern);
	}

	/**
	 * This setter method is used to assign the initial language entity values to
	 * the TransliterationDTO.
	 * 
	 * @param value
	 * @param transliterationRequestDTO
	 * @return transliterationResponseDTO with values
	 */
	public TransliterationResponseDTO responseSetter(String value,
			TransliterationRequestDTO transliterationRequestDTO) {
		TransliterationResponseDTO transliterationResponseDTO = new TransliterationResponseDTO();
		transliterationResponseDTO.setFromFieldValue(transliterationRequestDTO.getFromFieldValue());
		transliterationResponseDTO.setFromFieldLang(transliterationRequestDTO.getFromFieldLang());
		transliterationResponseDTO.setToFieldValue(value);
		transliterationResponseDTO.setToFieldLang(transliterationRequestDTO.getToFieldLang());
		return transliterationResponseDTO;
	}
	
	/**
	 * @param dto
	 * @return true if dto contains supported languages.
	 */
	public boolean supportedLanguageCheck(TransliterationRequestDTO dto) {
		return supportedLang.contains(dto.getFromFieldLang())&&supportedLang.contains(dto.getToFieldLang());
	}
	


}
