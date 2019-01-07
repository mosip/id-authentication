/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.transliteration.service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.mosip.preregistration.core.util.ValidationUtil;
import io.mosip.preregistration.transliteration.dto.MainRequestDTO;
import io.mosip.preregistration.transliteration.dto.MainResponseDTO;
import io.mosip.preregistration.transliteration.dto.TransliterationDTO;
import io.mosip.preregistration.transliteration.errorcode.ErrorMessage;
import io.mosip.preregistration.transliteration.exception.MandatoryFieldRequiredException;
import io.mosip.preregistration.transliteration.exception.util.TransliterationExceptionCatcher;
import io.mosip.preregistration.transliteration.service.util.TransliterationServiceUtil;
import io.mosip.preregistration.transliteration.util.PreRegistrationTransliterator;

/**
 * This class provides the service implementation for Transliteration application.
 * 
 * @author Kishan Rathore
 * @since 1.0.0
 *
 */
@Service
public class TransliterationService {

	/**
	 * Autowired reference for {@link #LanguageIdRepository}
	 */
	/*@Autowired
	private LanguageIdRepository idRepository;*/

	/**
	 * Autowired reference for {@link #translitrator}
	 */
	@Autowired
	private PreRegistrationTransliterator translitrator;

	/**
	 * Autowired reference for {@link #serviceUtil}
	 */
	@Autowired
	private TransliterationServiceUtil serviceUtil;

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

	protected boolean trueStatus = true;

	
	/**
	 * 
	 * This method is used to transliterate the given data.
	 * 
	 * @param requestDTO
	 * @return responseDto with transliterated value
	 */
	public MainResponseDTO<TransliterationDTO> translitratorService(
		MainRequestDTO<TransliterationDTO> requestDTO) {
		MainResponseDTO<TransliterationDTO> responseDTO = new MainResponseDTO<>();
		try {
			if (ValidationUtil.requestValidator(serviceUtil.prepareRequestParamMap(requestDTO), requiredRequestMap)) {
				TransliterationDTO transliterationRequestDTO = requestDTO.getRequest();
				if (serviceUtil.isEntryFieldsNull(transliterationRequestDTO)) {
//					String languageId = idRepository
//							.findByFromLangAndToLang(transliterationRequestDTO.getFromFieldLang(), transliterationRequestDTO.getToFieldLang())
//							.getLanguageId();
					String toFieldValue = translitrator.translitrator("Latin-Arabic", transliterationRequestDTO.getFromFieldValue());
					responseDTO.setResponse(serviceUtil.responseSetter(toFieldValue,transliterationRequestDTO));
					responseDTO.setResTime(serviceUtil.getCurrentResponseTime());
					responseDTO.setStatus(trueStatus);
				} else {
					throw new MandatoryFieldRequiredException(ErrorMessage.INCORRECT_MANDATORY_FIELDS.getCode());
				}
			}
		} catch (Exception e) {
			new TransliterationExceptionCatcher().handle(e);
		}
		return responseDTO;
	}
}
