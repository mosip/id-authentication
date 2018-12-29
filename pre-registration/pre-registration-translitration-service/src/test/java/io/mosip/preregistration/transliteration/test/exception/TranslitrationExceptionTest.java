package io.mosip.preregistration.transliteration.test.exception;

import java.sql.Timestamp;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.preregistration.core.exception.InvalidRequestParameterException;
import io.mosip.preregistration.transliteration.dto.RequestDTO;
import io.mosip.preregistration.transliteration.dto.ResponseDTO;
import io.mosip.preregistration.transliteration.dto.TransliterationApplicationDTO;
import io.mosip.preregistration.transliteration.entity.LanguageIdEntity;
import io.mosip.preregistration.transliteration.exception.IllegalParamException;
import io.mosip.preregistration.transliteration.exception.MandatoryFieldRequiredException;
import io.mosip.preregistration.transliteration.exception.MissingRequestParameterException;
import io.mosip.preregistration.transliteration.repository.LanguageIdRepository;
import io.mosip.preregistration.transliteration.service.impl.TransliterationServiceImpl;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TranslitrationExceptionTest {
	
	@MockBean
	private LanguageIdRepository idRepository;
	
	@Mock
	private TransliterationServiceImpl serviceImpl2;
	
	@Autowired
	private TransliterationServiceImpl serviceImpl;
	
	private LanguageIdEntity idEntity;
	
	@Test(expected=InvalidRequestParameterException.class)
	public void mandetoryRequestFieldsTest() {
		TransliterationApplicationDTO mandatoryFielddto=new TransliterationApplicationDTO();
		mandatoryFielddto.setFromFieldLang("English");
		mandatoryFielddto.setFromFieldName("Name1");
		mandatoryFielddto.setFromFieldValue("Kishan");
		mandatoryFielddto.setToFieldName("");
		
		RequestDTO<TransliterationApplicationDTO> mandatoryFieldRequest=new RequestDTO<>();
		mandatoryFieldRequest.setId("mosip.pre-registration.transliteration.transliterate");
		mandatoryFieldRequest.setReqTime(new Timestamp(System.currentTimeMillis()));
		mandatoryFieldRequest.setRequest(mandatoryFielddto);
		Mockito.when(serviceImpl.translitratorService(mandatoryFieldRequest)).thenThrow(InvalidRequestParameterException.class);
		
	}
	
	@Test
	public void mandetoryDtoFieldsTest() {
		MandatoryFieldRequiredException ex=new MandatoryFieldRequiredException();
		
		TransliterationApplicationDTO mandatoryFielddto=new TransliterationApplicationDTO();
		mandatoryFielddto.setFromFieldLang("");
		mandatoryFielddto.setFromFieldName("Name1");
		mandatoryFielddto.setFromFieldValue("Kishan");
		mandatoryFielddto.setToFieldLang("Arabic");
		mandatoryFielddto.setToFieldValue("");
		mandatoryFielddto.setToFieldName("Name2");
		
		RequestDTO<TransliterationApplicationDTO> mandatoryFieldRequest=new RequestDTO<>();
		mandatoryFieldRequest.setId("mosip.pre-registration.transliteration.transliterate");
		mandatoryFieldRequest.setVer("1.0");
		mandatoryFieldRequest.setReqTime(new Timestamp(System.currentTimeMillis()));
		mandatoryFieldRequest.setRequest(mandatoryFielddto);
		
		Mockito.when(serviceImpl2.translitratorService(mandatoryFieldRequest)).thenThrow(ex);
		
	}
	
	@Test
	public void illegalParamTest() {
		MissingRequestParameterException ex=new MissingRequestParameterException("MISSING_PARAM") ;
		TransliterationApplicationDTO mandatoryFielddto=new TransliterationApplicationDTO();
		mandatoryFielddto.setFromFieldLang("");
		mandatoryFielddto.setFromFieldName("Name1");
		mandatoryFielddto.setFromFieldValue("Kishan");
		mandatoryFielddto.setToFieldLang("Arabic");
		mandatoryFielddto.setToFieldName("");
		RequestDTO<TransliterationApplicationDTO> mandatoryFieldRequest=new RequestDTO<>();
		mandatoryFieldRequest.setId("mosip.pre-registration.transliteration.transliterate");
		Mockito.when(serviceImpl2.translitratorService(mandatoryFieldRequest)).thenThrow(ex);
		
	}


}
