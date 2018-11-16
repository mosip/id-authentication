
package io.mosip.kernel.masterdata.test.controller;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import io.mosip.kernel.masterdata.dto.LanguageDto;
import io.mosip.kernel.masterdata.dto.LanguageResponseDto;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;

import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.service.LanguageService;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class LanguageControllerTest {

	@Autowired
	MockMvc mockMvc;

	@MockBean
	private LanguageService languageService;

	private static final String RESPONSE_BODY_ALL = "{ \"languages\": [   {      \"languageCode\": \"hin\", \"languageName\": \"hindi\",      \"languageFamily\": \"hindi\",   \"nativeName\": \"hindi\" } ]}";

	private LanguageResponseDto respDto;
	private List<LanguageDto> languages;
	private LanguageDto hin;

	@Test
	public void testGetAllLanguages() throws Exception {
		loadSuccessData();
		Mockito.when(languageService.getAllLaguages()).thenReturn(respDto);

		mockMvc.perform(MockMvcRequestBuilders.get("/languages"))
				.andExpect(MockMvcResultMatchers.content().json(RESPONSE_BODY_ALL))
				.andExpect(MockMvcResultMatchers.status().isOk());

	}

	@Test
	public void testGetAllLanguagesForLanguageNotFoundException() throws Exception {
		Mockito.when(languageService.getAllLaguages())
				.thenThrow(new DataNotFoundException("KER-MAS-0987", "No Language found"));
		mockMvc.perform(MockMvcRequestBuilders.get("/languages"))
				.andExpect(MockMvcResultMatchers.status().isNotFound());

	}

	@Test
	public void testGetAllLanguagesForLanguageFetchException() throws Exception {
		Mockito.when(languageService.getAllLaguages())
				.thenThrow(new MasterDataServiceException("KER-MAS-0988", "Error occured while fetching language"));
		mockMvc.perform(MockMvcRequestBuilders.get("/languages"))
				.andExpect(MockMvcResultMatchers.status().isInternalServerError());
	}

	private void loadSuccessData() {
		respDto = new LanguageResponseDto();
		languages = new ArrayList<>();

		// creating language
		hin = new LanguageDto();
		hin.setLanguageCode("hin");
		hin.setLanguageName("hindi");
		hin.setLanguageFamily("hindi");
		hin.setNativeName("hindi");

		// adding language to list
		languages.add(hin);

		respDto.setLanguages(languages);

	}

}
