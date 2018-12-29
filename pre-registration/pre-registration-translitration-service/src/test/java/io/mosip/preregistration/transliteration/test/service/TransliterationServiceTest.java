package io.mosip.preregistration.transliteration.test.service;


import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
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

import io.mosip.kernel.jsonvalidator.impl.JsonValidatorImpl;
import io.mosip.preregistration.transliteration.dto.TransliterationApplicationDTO;
import io.mosip.preregistration.transliteration.dto.RequestDTO;
import io.mosip.preregistration.transliteration.dto.ResponseDTO;
import io.mosip.preregistration.transliteration.entity.LanguageIdEntity;
import io.mosip.preregistration.transliteration.errorcode.ErrorCodes;
import io.mosip.preregistration.transliteration.errorcode.ErrorMessage;
import io.mosip.preregistration.transliteration.exception.IllegalParamException;
import io.mosip.preregistration.transliteration.repository.LanguageIdRepository;
import io.mosip.preregistration.transliteration.service.impl.TransliterationServiceImpl;
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
	private TransliterationServiceImpl transliterationServiceImpl;
	
	@MockBean
	private JsonValidatorImpl jsonValidatorImpl;
	
	private LanguageIdEntity idEntity;
	
	JSONParser parser = new JSONParser();
	
	private JSONObject jsonObject;
	private JSONObject jsonTestObject;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private RequestDTO< TransliterationApplicationDTO> requestDto=null;
	private TransliterationApplicationDTO transliterationRequest=null;
	boolean requestValidatorFlag = false;
	Map<String, String> requestMap = new HashMap<>();
	Map<String, String> requiredRequestMap = new HashMap<>();
	Timestamp times = null;
	ResponseDTO<TransliterationApplicationDTO> responseDTO = null;
	
	@Value("${ver}")
	String versionUrl;

	@Value("${id}")
	String idUrl;

	private Map<String, String> reqDateRange = new HashMap<>();
	
	@Before
	public void setUp()  {
		
		
		
		idEntity=new LanguageIdEntity();
		times = new Timestamp(System.currentTimeMillis());
		idEntity.setFromLang("English");
		idEntity.setLanguageId("Latin-Arabic");
		idEntity.setToLang("Arabic");
		
		transliterationRequest=new TransliterationApplicationDTO();
		transliterationRequest.setFromFieldLang("English");
		transliterationRequest.setFromFieldName("Name1");
		transliterationRequest.setFromFieldValue("Kishan");
		transliterationRequest.setToFieldLang("Arabic");
		transliterationRequest.setToFieldName("Name2");
		transliterationRequest.setToFieldValue("");
		
		responseDTO = new ResponseDTO<TransliterationApplicationDTO>();
		responseDTO.setStatus("true");
		responseDTO.setResTime(times);
		responseDTO.setErr(null);
	}
	
	@Test
	public void successTest() {
		Mockito.when(idRepository.findByFromLangAndToLang(Mockito.any(), Mockito.any())).thenReturn(idEntity);
		TransliterationApplicationDTO transliterationRequest2=new TransliterationApplicationDTO();
		transliterationRequest2.setFromFieldLang("English");
		transliterationRequest2.setFromFieldName("Name1");
		transliterationRequest2.setFromFieldValue("Kishan");
		transliterationRequest2.setToFieldLang("Arabic");
		transliterationRequest2.setToFieldName("Name2");
		transliterationRequest2.setToFieldValue("كِسهَن");
		requestDto=new RequestDTO<TransliterationApplicationDTO>();
		requestDto.setId("mosip.pre-registration.transliteration.transliterate");
		requestDto.setReqTime(new Timestamp(System.currentTimeMillis()));
		requestDto.setVer("1.0");
		requestDto.setRequest(transliterationRequest);
		responseDTO.setResponse(transliterationRequest2);
		
		ResponseDTO<TransliterationApplicationDTO> result=transliterationServiceImpl.translitratorService(requestDto);
		assertEquals(result.getResponse().getToFieldValue(), responseDTO.getResponse().getToFieldValue());
		
	}
	
	@Test(expected = IllegalParamException.class)
	public void failureTest() throws Exception{
		
		IllegalParamException exception = new IllegalParamException(ErrorCodes.PRG_TRL_002.toString(),
				ErrorMessage.INCORRECT_MANDATORY_FIELDS.toString(), null);
		Mockito.when(idRepository.findByFromLangAndToLang(Mockito.any(), Mockito.any())).thenThrow(exception);
		TransliterationApplicationDTO transliterationRequest2=new TransliterationApplicationDTO();
		transliterationRequest2.setFromFieldLang("Hindi");
		transliterationRequest2.setFromFieldName("Name1");
		transliterationRequest2.setFromFieldValue("Kishan");
		transliterationRequest2.setToFieldLang("Arabic");
		transliterationRequest2.setToFieldName("Name2");
		transliterationRequest2.setToFieldValue("كِسهَن");
		requestDto=new RequestDTO<TransliterationApplicationDTO>();
		requestDto.setRequest(transliterationRequest);
		transliterationServiceImpl.translitratorService(requestDto);
	}

	
}
