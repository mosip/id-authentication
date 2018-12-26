package io.mosip.preregistration.translitration.service.impl;

import java.sql.Timestamp;

import org.springframework.stereotype.Component;

import com.ibm.icu.text.Transliterator;

import io.mosip.preregistration.translitration.dto.CreateTranslitrationRequest;
import io.mosip.preregistration.translitration.dto.ResponseDTO;
import io.mosip.preregistration.translitration.dto.TranslitrationRequestDTO;
import io.mosip.preregistration.translitration.errorcode.ErrorMessage;
import io.mosip.preregistration.translitration.exception.FailedToTranslitrateException;
import io.mosip.preregistration.translitration.exception.MandatoryFieldRequiredException;
import io.mosip.preregistration.translitration.service.TranslitrationService;

@Component
public class TranslitrationServiceImpl implements TranslitrationService {

	protected String value = null;

	protected String trueStatus = "true";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.preregistration.translitration.service.impl.TranslitrationService#
	 * translitrator(io.mosip.preregistration.translitration.dto.
	 * TranslitrationRequestDTO)
	 */
	@Override
	public ResponseDTO<String> translitrator(TranslitrationRequestDTO<CreateTranslitrationRequest> requestDTO) {

		ResponseDTO<String> response = new ResponseDTO<>();

		CreateTranslitrationRequest requestCheck=(CreateTranslitrationRequest)requestDTO.getRequest();
		
		if (!isEntryFilled(requestCheck)) {
			throw new MandatoryFieldRequiredException(ErrorMessage.MANDATORY_FIELDS_NOT_FILLED.toString());
		}

		
		Transliterator translitratedLanguage = Transliterator.getInstance(requestDTO.getRequest().getLangCode());
		value = translitratedLanguage.transliterate(requestDTO.getRequest().getKey());

		if (value != null) {
			response.setResponse(value);
			response.setResTime(new Timestamp(System.currentTimeMillis()));
			response.setStatus(trueStatus);
		} else {
			throw new FailedToTranslitrateException(ErrorMessage.TRANSLITRATION_FAILED.toString());
		}

		return response;

	}

	protected boolean isEntryFilled(CreateTranslitrationRequest request) {

		if (!(request.getKey().equals(null)) || !(request.getLangCode().equals(null))) {
			return true;
		} else {
			return false;
		}

	}

}
