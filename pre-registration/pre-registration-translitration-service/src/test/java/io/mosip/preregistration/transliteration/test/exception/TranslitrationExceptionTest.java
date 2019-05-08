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

import io.mosip.preregistration.core.common.dto.MainRequestDTO;
import io.mosip.preregistration.core.exception.InvalidRequestParameterException;
import io.mosip.preregistration.transliteration.dto.TransliterationRequestDTO;
import io.mosip.preregistration.transliteration.dto.TransliterationResponseDTO;
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
		TransliterationRequestDTO mandatoryFielddto = new TransliterationRequestDTO();
		mandatoryFielddto.setFromFieldLang("English");
		mandatoryFielddto.setFromFieldValue("Kishan");

		MainRequestDTO<TransliterationRequestDTO> mandatoryFieldRequest = new MainRequestDTO<>();
		mandatoryFieldRequest.setId("mosip.pre-registration.transliteration.transliterate");
		mandatoryFieldRequest.setRequesttime(new Timestamp(System.currentTimeMillis()));
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

		TransliterationRequestDTO mandatoryFielddto = new TransliterationRequestDTO();
		mandatoryFielddto.setFromFieldLang("");
		mandatoryFielddto.setFromFieldValue("Kishan");
		mandatoryFielddto.setToFieldLang("Arabic");
		

		MainRequestDTO<TransliterationRequestDTO> mandatoryFieldRequest = new MainRequestDTO<>();
		mandatoryFieldRequest.setId("mosip.pre-registration.transliteration.transliterate");
		mandatoryFieldRequest.setVersion("1.0");
		mandatoryFieldRequest.setRequesttime(new Timestamp(System.currentTimeMillis()));
		mandatoryFieldRequest.setRequest(mandatoryFielddto);

		Mockito.when(serviceImpl2.translitratorService(mandatoryFieldRequest)).thenThrow(ex);

	}

	/**
	 * Throws the MissingRequestParameterException.
	 */
	@Test
	public void illegalParamTest() {

		MissingRequestParameterException ex = new MissingRequestParameterException("MISSING_PARAM",null);
		TransliterationRequestDTO mandatoryFielddto = new TransliterationRequestDTO();
		mandatoryFielddto.setFromFieldLang("");
		mandatoryFielddto.setFromFieldValue("Kishan");
		mandatoryFielddto.setToFieldLang("Arabic");
		MainRequestDTO<TransliterationRequestDTO> mandatoryFieldRequest = new MainRequestDTO<>();
		mandatoryFieldRequest.setId("mosip.pre-registration.transliteration.transliterate");
		Mockito.when(serviceImpl2.translitratorService(mandatoryFieldRequest)).thenThrow(ex);

	}

}
