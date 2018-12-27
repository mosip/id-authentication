package io.mosip.preregistration.translitration.service.impl;

import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import io.mosip.preregistration.translitration.dto.CreateTranslitrationRequest;
import io.mosip.preregistration.translitration.dto.ResponseDTO;
import io.mosip.preregistration.translitration.dto.TranslitrationRequestDTO;
import io.mosip.preregistration.translitration.errorcode.ErrorMessage;
import io.mosip.preregistration.translitration.exception.MandatoryFieldRequiredException;
import io.mosip.preregistration.translitration.repository.LanguageIdRepository;
import io.mosip.preregistration.translitration.util.PreRegistrationTranslitrator;

@Service
public class TranslitrationServiceImpl {

	@Autowired
	private LanguageIdRepository idRepository;

	@Autowired
	private PreRegistrationTranslitrator translitrator;

	protected String trueStatus = "true";

	public ResponseDTO<CreateTranslitrationRequest> translitratorService(
			TranslitrationRequestDTO<CreateTranslitrationRequest> requestDTO) {

		ResponseDTO<CreateTranslitrationRequest> response = new ResponseDTO<>();

		CreateTranslitrationRequest requestFields = requestDTO.getRequest();

		CreateTranslitrationRequest responseFields = new CreateTranslitrationRequest();

		String toFieldName = null;

		if (isEntryFieldsNull(requestFields)) {

			String languageId = idRepository
					.findByFromLangAndToLang(requestFields.getFromFieldLang(), requestFields.getToFieldLang())
					.getLanguageId();
			System.out.println(languageId);

			try {
				toFieldName = translitrator.translitrator(languageId, requestFields.getFromFieldValue());
			} catch (Exception e) {

			}
			responseFields.setFromFieldName(requestFields.getFromFieldName());
			responseFields.setFromFieldValue(requestFields.getFromFieldValue());
			responseFields.setFromFieldLang(requestFields.getFromFieldLang());
			responseFields.setToFieldName(requestFields.getToFieldName());
			responseFields.setToFieldValue(toFieldName);
			responseFields.setToFieldLang(requestFields.getToFieldLang());

			response.setResponse(responseFields);
			;
			response.setResTime(new Timestamp(System.currentTimeMillis()));
			response.setStatus(trueStatus);

		} else {

			throw new MandatoryFieldRequiredException(ErrorMessage.MANDATORY_FIELDS_NOT_FILLED.toString());
		}

		return response;

	}

	protected boolean isEntryFieldsNull(CreateTranslitrationRequest requestFields) {

		if (!requestFields.getFromFieldLang().equals("") && !requestFields.getFromFieldValue().equals("")
				&& !requestFields.getFromFieldName().equals("") && !requestFields.getToFieldLang().equals("")
				&& !requestFields.getToFieldName().equals("")) {

			return true;

		} else {
			return false;
		}

	}

}
