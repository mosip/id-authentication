/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.transliteration.test.exception;

import java.sql.Timestamp;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.preregistration.core.exception.InvalidRequestParameterException;
import io.mosip.preregistration.transliteration.dto.MainRequestDTO;
import io.mosip.preregistration.transliteration.dto.TransliterationDTO;
import io.mosip.preregistration.transliteration.entity.LanguageIdEntity;
import io.mosip.preregistration.transliteration.exception.MandatoryFieldRequiredException;
import io.mosip.preregistration.transliteration.exception.MissingRequestParameterException;
import io.mosip.preregistration.transliteration.service.TransliterationService;

/**
 * Test class to test the transliteration application exceptions
 * 
 * @author Kishan Rathore
 * @since 1.0.0
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TranslitrationExceptionTest {

	/**
	 * MockBean reference for $link{LanguageIdRepository}
	 */
	/*
	 * @MockBean private LanguageIdRepository idRepository;
	 */

	/**
	 * Mock reference for $link{TransliterationService}
	 */
	@Mock
	private TransliterationService serviceImpl2;

	/**
	 * Autowired reference for $link{TransliterationService}
	 */
	@Autowired
	private TransliterationService serviceImpl;

	private LanguageIdEntity idEntity;

	/**
	 * Throws the InvalidRequestParameterException.
	 */
	@Test(expected = InvalidRequestParameterException.class)
	public void mandetoryRequestFieldsTest() {
		TransliterationDTO mandatoryFielddto = new TransliterationDTO();
		mandatoryFielddto.setFromFieldLang("English");
		mandatoryFielddto.setFromFieldName("Name1");
		mandatoryFielddto.setFromFieldValue("Kishan");
		mandatoryFielddto.setToFieldName("");

		MainRequestDTO<TransliterationDTO> mandatoryFieldRequest = new MainRequestDTO<>();
		mandatoryFieldRequest.setId("mosip.pre-registration.transliteration.transliterate");
		mandatoryFieldRequest.setReqTime(new Timestamp(System.currentTimeMillis()));
		mandatoryFieldRequest.setRequest(mandatoryFielddto);
		Mockito.when(serviceImpl.translitratorService(mandatoryFieldRequest))
				.thenThrow(InvalidRequestParameterException.class);

	}

	/**
	 * Throws the MandatoryFieldRequiredException.
	 */
	@Test
	public void mandetoryDtoFieldsTest() {
		MandatoryFieldRequiredException ex = new MandatoryFieldRequiredException();

		TransliterationDTO mandatoryFielddto = new TransliterationDTO();
		mandatoryFielddto.setFromFieldLang("");
		mandatoryFielddto.setFromFieldName("Name1");
		mandatoryFielddto.setFromFieldValue("Kishan");
		mandatoryFielddto.setToFieldLang("Arabic");
		mandatoryFielddto.setToFieldValue("");
		mandatoryFielddto.setToFieldName("Name2");

		MainRequestDTO<TransliterationDTO> mandatoryFieldRequest = new MainRequestDTO<>();
		mandatoryFieldRequest.setId("mosip.pre-registration.transliteration.transliterate");
		mandatoryFieldRequest.setVer("1.0");
		mandatoryFieldRequest.setReqTime(new Timestamp(System.currentTimeMillis()));
		mandatoryFieldRequest.setRequest(mandatoryFielddto);

		Mockito.when(serviceImpl2.translitratorService(mandatoryFieldRequest)).thenThrow(ex);

	}

	/**
	 * Throws the MissingRequestParameterException.
	 */
	@Test
	public void illegalParamTest() {
		MissingRequestParameterException ex = new MissingRequestParameterException("MISSING_PARAM");
		TransliterationDTO mandatoryFielddto = new TransliterationDTO();
		mandatoryFielddto.setFromFieldLang("");
		mandatoryFielddto.setFromFieldName("Name1");
		mandatoryFielddto.setFromFieldValue("Kishan");
		mandatoryFielddto.setToFieldLang("Arabic");
		mandatoryFielddto.setToFieldName("");
		MainRequestDTO<TransliterationDTO> mandatoryFieldRequest = new MainRequestDTO<>();
		mandatoryFieldRequest.setId("mosip.pre-registration.transliteration.transliterate");
		Mockito.when(serviceImpl2.translitratorService(mandatoryFieldRequest)).thenThrow(ex);

	}

}
