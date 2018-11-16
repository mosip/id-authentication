package io.mosip.kernel.masterdata.test.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.orm.hibernate5.HibernateObjectRetrievalFailureException;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.masterdata.dto.LanguageDto;
import io.mosip.kernel.masterdata.dto.LanguageResponseDto;
import io.mosip.kernel.masterdata.entity.Language;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.LanguageRepository;
import io.mosip.kernel.masterdata.service.LanguageService;
import io.mosip.kernel.masterdata.utils.ObjectMapperUtil;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class LanguageServiceImplTest {

	@Autowired
	private LanguageService languageService;

	@MockBean
	private LanguageRepository languageRepository;

	@MockBean
	private ObjectMapperUtil mapper;

	private List<Language> languages;
	private LanguageResponseDto resp;
	private List<LanguageDto> languageDtos;
	private Language hin;
	private Language eng;
	private LanguageDto hinDto;
	private LanguageDto engDto;

	@Before
	public void loadSuccessData() {
		languages = new ArrayList<>();

		// creating language
		hin = new Language();
		hin.setLanguageCode("hin");
		hin.setLanguageName("hindi");
		hin.setLanguageFamily("hindi");
		hin.setNativeName("hindi");
		hin.setIsActive(Boolean.TRUE);

		eng = new Language();
		eng.setLanguageCode("en");
		eng.setLanguageName("english");
		eng.setLanguageFamily("english");
		eng.setNativeName("english");
		eng.setIsActive(Boolean.TRUE);

		// adding language to list
		languages.add(hin);
		languages.add(eng);

		languageDtos = new ArrayList<>();
		// creating language
		hinDto = new LanguageDto();
		hinDto.setLanguageCode("hin");
		hinDto.setLanguageName("hindi");
		hinDto.setLanguageFamily("hindi");
		hinDto.setNativeName("hindi");

		engDto = new LanguageDto();
		engDto.setLanguageCode("en");
		engDto.setLanguageName("english");
		engDto.setLanguageFamily("english");
		engDto.setNativeName("english");

		languageDtos.add(hinDto);
		languageDtos.add(engDto);

		resp = new LanguageResponseDto();
		resp.setLanguages(languageDtos);

	}

	@Test
	public void testSucessGetAllLaguages() {
		Mockito.when(languageRepository.findAll(Language.class)).thenReturn(languages);
		Mockito.when(mapper.mapAll(languages, LanguageDto.class)).thenReturn(languageDtos);
		LanguageResponseDto dto = languageService.getAllLaguages();
		assertNotNull(dto);
		assertEquals(2, dto.getLanguages().size());
	}

	@Test(expected = DataNotFoundException.class)
	public void testLanguageNotFoundException() {
		Mockito.when(languageRepository.findAll(Language.class)).thenReturn(null);
		languageService.getAllLaguages();
	}

	@Test(expected = DataNotFoundException.class)
	public void testLanguageNotFoundExceptionWhenNoLanguagePresent() {
		Mockito.when(languageRepository.findAll(Language.class)).thenReturn(new ArrayList<Language>());
		languageService.getAllLaguages();
	}

	@Test(expected = MasterDataServiceException.class)
	public void testLanguageFetchException() {
		Mockito.when(languageRepository.findAll(Language.class))
				.thenThrow(HibernateObjectRetrievalFailureException.class);
		languageService.getAllLaguages();
	}

}
