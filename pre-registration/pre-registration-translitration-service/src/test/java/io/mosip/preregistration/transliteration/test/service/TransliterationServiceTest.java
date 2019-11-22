/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.transliteration.test.service;


import static org.junit.Assert.assertEquals;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.core.util.DateUtils;
import io.mosip.preregistration.core.common.dto.MainRequestDTO;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.transliteration.dto.TransliterationRequestDTO;
import io.mosip.preregistration.transliteration.dto.TransliterationResponseDTO;
import io.mosip.preregistration.transliteration.entity.LanguageIdEntity;
import io.mosip.preregistration.transliteration.errorcode.ErrorCodes;
import io.mosip.preregistration.transliteration.errorcode.ErrorMessage;
import io.mosip.preregistration.transliteration.exception.IllegalParamException;
import io.mosip.preregistration.transliteration.exception.MandatoryFieldRequiredException;
import io.mosip.preregistration.transliteration.exception.UnSupportedLanguageException;
import io.mosip.preregistration.transliteration.repository.LanguageIdRepository;
import io.mosip.preregistration.transliteration.service.TransliterationService;
import io.mosip.preregistration.transliteration.service.util.TransliterationServiceUtil;
import io.mosip.preregistration.transliteration.util.PreRegistrationTransliterator;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TransliterationServiceTest {
	
	@MockBean
	private LanguageIdRepository idRepository;
	
	@Autowired
	private TransliterationServiceUtil  serviceUtil;
	
	@Autowired
	private PreRegistrationTransliterator transliterator;
	
	@Autowired
	private TransliterationService transliterationServiceImpl;
	
	private LanguageIdEntity idEntity;
	
	JSONParser parser = new JSONParser();
	
	private JSONObject jsonObject;
	private JSONObject jsonTestObject;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private MainRequestDTO< TransliterationRequestDTO> requestDto=null;
	private TransliterationRequestDTO transliterationRequest=null;
	boolean requestValidatorFlag = false;
	Map<String, String> requestMap = new HashMap<>();
	Map<String, String> requiredRequestMap = new HashMap<>();
	String times = null;
	MainResponseDTO<TransliterationResponseDTO> responseDTO = null;
	
	@Value("${version}")
	String versionUrl;

	@Value("${mosip.pre-registration.transliteration.transliterate.id}")
	String idUrl;

	private Map<String, String> reqDateRange = new HashMap<>();
	
	@Before
	public void setUp()  {
		String dateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
		idEntity=new LanguageIdEntity();
		times = DateUtils.formatDate(new Date(), dateTimeFormat);
		idEntity.setFromLang("English");
		idEntity.setLanguageId("Latin-Arabic");
		idEntity.setToLang("Arabic");
		
		transliterationRequest=new TransliterationRequestDTO();
		transliterationRequest.setFromFieldLang("eng");
		transliterationRequest.setFromFieldValue("Kishan");
		transliterationRequest.setToFieldLang("ara");
		
		
		responseDTO = new MainResponseDTO<TransliterationResponseDTO>();
		responseDTO.setResponsetime(times);
		responseDTO.setErrors(null);
	}
	
	@Test
	public void successTest() {
		Mockito.when(idRepository.findByFromLangAndToLang(Mockito.any(), Mockito.any())).thenReturn(idEntity);
		TransliterationResponseDTO transliterationRequest2=new TransliterationResponseDTO();
		transliterationRequest2.setFromFieldLang("eng");
		transliterationRequest2.setFromFieldValue("Kishan");
		transliterationRequest2.setToFieldLang("ara");
		transliterationRequest2.setToFieldValue("كِسهَن");
		MainRequestDTO<TransliterationRequestDTO> requestDto=new MainRequestDTO<TransliterationRequestDTO>();
		requestDto.setId("mosip.pre-registration.transliteration.transliterate");
		requestDto.setRequesttime(new Timestamp(System.currentTimeMillis()));
		requestDto.setVersion("1.0");
		requestDto.setRequest(transliterationRequest);
		responseDTO.setResponse(transliterationRequest2);
		
		MainResponseDTO<TransliterationResponseDTO> result=transliterationServiceImpl.translitratorService(requestDto);
		assertEquals(result.getResponse().getToFieldValue(), responseDTO.getResponse().getToFieldValue());
		
	}
	
	//@Test(expected = IllegalParamException.class)
	public void failureTest() throws Exception{
		
		IllegalParamException exception = new IllegalParamException(ErrorCodes.PRG_TRL_APP_002.getCode(),
				ErrorMessage.INCORRECT_MANDATORY_FIELDS.toString(), null);
		Mockito.when(idRepository.findByFromLangAndToLang(Mockito.any(), Mockito.any())).thenThrow(exception);
		TransliterationResponseDTO transliterationRequest2=new TransliterationResponseDTO();
		transliterationRequest2.setFromFieldLang("eng");
		transliterationRequest2.setFromFieldValue("Kishan");
		transliterationRequest2.setToFieldLang("ara");
		transliterationRequest2.setToFieldValue("كِسهَن");
		requestDto=new MainRequestDTO<TransliterationRequestDTO>();
		requestDto.setRequest(transliterationRequest);
		transliterationServiceImpl.translitratorService(requestDto);
	}
	
	@Test(expected=MandatoryFieldRequiredException.class)
	public void mandatoryFieldFailTest() {
		MandatoryFieldRequiredException exception=new MandatoryFieldRequiredException(ErrorCodes.PRG_TRL_APP_002.getCode()
				, ErrorMessage.INCORRECT_MANDATORY_FIELDS.getMessage(),null);
		
		Mockito.when(idRepository.findByFromLangAndToLang(Mockito.any(), Mockito.any())).thenReturn(idEntity);
		
		TransliterationResponseDTO transliterationRequest2=new TransliterationResponseDTO();
		TransliterationRequestDTO request=new TransliterationRequestDTO();
		request.setFromFieldLang("");
		request.setFromFieldValue("Kishan");
		request.setToFieldLang("ara");
		requestDto=new MainRequestDTO<TransliterationRequestDTO>();
		requestDto.setId("mosip.pre-registration.transliteration.transliterate");
		requestDto.setRequesttime(new Timestamp(System.currentTimeMillis()));
		requestDto.setVersion("1.0");
		requestDto.setRequest(request);
		responseDTO.setResponse(transliterationRequest2);
		MainResponseDTO<TransliterationResponseDTO> result=transliterationServiceImpl.translitratorService(requestDto);
		
	}
	@Test(expected=UnSupportedLanguageException.class)
	public void unSupportedLangTest() {
		UnSupportedLanguageException exception=new UnSupportedLanguageException(ErrorCodes.PRG_TRL_APP_008.getCode(), 
				ErrorMessage.UNSUPPORTED_LANGUAGE.getMessage(),null);
		Mockito.when(idRepository.findByFromLangAndToLang(Mockito.any(), Mockito.any())).thenReturn(idEntity);
		TransliterationRequestDTO transliterationRequest=new TransliterationRequestDTO();
		transliterationRequest=new TransliterationRequestDTO();
		transliterationRequest.setFromFieldLang("enl");
		transliterationRequest.setFromFieldValue("Kishan");
		transliterationRequest.setToFieldLang("ara");
		requestDto=new MainRequestDTO<TransliterationRequestDTO>();
		requestDto.setId("mosip.pre-registration.transliteration.transliterate");
		requestDto.setRequesttime(new Timestamp(System.currentTimeMillis()));
		requestDto.setVersion("1.0");
		requestDto.setRequest(transliterationRequest);
		
		MainResponseDTO<TransliterationResponseDTO> result=transliterationServiceImpl.translitratorService(requestDto);
	}

	
}

