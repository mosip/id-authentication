package io.mosip.preregistration.transliteration.service.impl;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.mosip.kernel.jsonvalidator.impl.JsonValidatorImpl;
import io.mosip.preregistration.core.util.ValidationUtil;
import io.mosip.preregistration.transliteration.dto.TransliterationApplicationDTO;
import io.mosip.preregistration.transliteration.dto.ResponseDTO;
import io.mosip.preregistration.transliteration.dto.RequestDTO;
import io.mosip.preregistration.transliteration.errorcode.ErrorMessage;
import io.mosip.preregistration.transliteration.exception.MandatoryFieldRequiredException;
import io.mosip.preregistration.transliteration.exception.util.TransliterationExceptionCatcher;
import io.mosip.preregistration.transliteration.repository.LanguageIdRepository;
import io.mosip.preregistration.transliteration.service.util.TransliterationServiceUtil;
import io.mosip.preregistration.transliteration.util.PreRegistrationTransliterator;

@Service
public class TransliterationServiceImpl {

	@Autowired
	private LanguageIdRepository idRepository;

	@Autowired
	private PreRegistrationTransliterator translitrator;

	@Autowired
	private TransliterationServiceUtil serviceUtil;

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

	public ResponseDTO<TransliterationApplicationDTO> translitratorService(
			RequestDTO<TransliterationApplicationDTO> requestDTO) {

		ResponseDTO<TransliterationApplicationDTO> response = new ResponseDTO<>();
		try {
			if (ValidationUtil.requestValidator(serviceUtil.prepareRequestParamMap(requestDTO),
					requiredRequestMap)) {

				TransliterationApplicationDTO requestFields = requestDTO.getRequest();

				TransliterationApplicationDTO responseFields = new TransliterationApplicationDTO();

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

					throw new MandatoryFieldRequiredException(ErrorMessage.INCORRECT_MANDATORY_FIELDS.toString());
				}

			}
		} catch (Exception e) {

			new TransliterationExceptionCatcher().handle(e);
		}

		return response;

	}

	protected boolean isEntryFieldsNull(TransliterationApplicationDTO requestFields) {

		if (!requestFields.getFromFieldLang().equals("") && !requestFields.getFromFieldValue().equals("")
				&& !requestFields.getFromFieldName().equals("") && !requestFields.getToFieldLang().equals("")
				&& !requestFields.getToFieldName().equals("")) {

			return true;

		} else {
			return false;
		}

	}

}
