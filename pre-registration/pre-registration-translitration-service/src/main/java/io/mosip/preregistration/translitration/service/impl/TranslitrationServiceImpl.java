package io.mosip.preregistration.translitration.service.impl;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.mosip.kernel.jsonvalidator.impl.JsonValidatorImpl;
import io.mosip.preregistration.core.util.ValidationUtil;
import io.mosip.preregistration.translitration.dto.CreateTranslitrationRequest;
import io.mosip.preregistration.translitration.dto.ResponseDTO;
import io.mosip.preregistration.translitration.dto.TranslitrationRequestDTO;
import io.mosip.preregistration.translitration.errorcode.ErrorMessage;
import io.mosip.preregistration.translitration.exception.MandatoryFieldRequiredException;
import io.mosip.preregistration.translitration.exception.util.TranslitrationExceptionCatcher;
import io.mosip.preregistration.translitration.repository.LanguageIdRepository;
import io.mosip.preregistration.translitration.util.PreRegistrationTranslitrator;

@Service
public class TranslitrationServiceImpl {

	@Autowired
	private LanguageIdRepository idRepository;

	@Autowired
	private PreRegistrationTranslitrator translitrator;

	@Autowired
	private TranslitrationServiceUtil serviceUtil;

	/**
	 * Autowired reference for {@link #JsonValidatorImpl}
	 */
	@Autowired
	private JsonValidatorImpl jsonValidator;

	/**
	 * Reference for ${id} from property file
	 */
	@Value("${id}")
	private String id;

	/**
	 * Reference for ${ver} from property file
	 */
	@Value("${ver}")
	private String ver;

	/**
	 * Request map to store the id and version and this is to be passed to request
	 * validator method.
	 */
	Map<String, String> requiredRequestMap = new HashMap<>();

	/**
	 * This method acts as a post constructor to initialize the required request
	 * parameters.
	 */
	@PostConstruct
	public void setup() {
		requiredRequestMap.put("id", id);
		requiredRequestMap.put("ver", ver);
	}

	protected String trueStatus = "true";

	public ResponseDTO<CreateTranslitrationRequest> translitratorService(
			TranslitrationRequestDTO<CreateTranslitrationRequest> requestDTO) {

		ResponseDTO<CreateTranslitrationRequest> response = new ResponseDTO<>();
		try {
			if (ValidationUtil.requestValidator(serviceUtil.prepareRequestParamMap(requestDTO),
					requiredRequestMap)) {

				CreateTranslitrationRequest requestFields = requestDTO.getRequest();

				CreateTranslitrationRequest responseFields = new CreateTranslitrationRequest();

				String toFieldName = null;

				if (isEntryFieldsNull(requestFields)) {

					String languageId = idRepository
							.findByFromLangAndToLang(requestFields.getFromFieldLang(), requestFields.getToFieldLang())
							.getLanguageId();

					toFieldName = translitrator.translitrator(languageId, requestFields.getFromFieldValue());
					responseFields.setFromFieldName(requestFields.getFromFieldName());
					responseFields.setFromFieldValue(requestFields.getFromFieldValue());
					responseFields.setFromFieldLang(requestFields.getFromFieldLang());
					responseFields.setToFieldName(requestFields.getToFieldName());
					responseFields.setToFieldValue(toFieldName);
					responseFields.setToFieldLang(requestFields.getToFieldLang());

					response.setResponse(responseFields);
					response.setResTime(new Timestamp(System.currentTimeMillis()));
					response.setStatus(trueStatus);

				} else {

					throw new MandatoryFieldRequiredException(ErrorMessage.MANDATORY_FIELDS_NOT_FILLED.toString());
				}

			}
		} catch (Exception e) {

			new TranslitrationExceptionCatcher().handle(e);
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
