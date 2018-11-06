package io.mosip.kernel.masterdata.test.integration;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ConfigurationException;
import org.modelmapper.MappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import io.mosip.kernel.masterdata.dto.BlacklistedWordsDto;
import io.mosip.kernel.masterdata.entity.BlacklistedWords;
import io.mosip.kernel.masterdata.repository.BlacklistedWordsRepository;
import io.mosip.kernel.masterdata.utils.ObjectMapperUtil;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class BlacklistedWordsExceptionTest {
	@Autowired
	private MockMvc mockMvc;
	@MockBean
	private BlacklistedWordsRepository wordsRepository;

	@MockBean
	private ObjectMapperUtil mapperUtil;

	List<BlacklistedWords> words;

	@Before
	public void setUp() {
		words = new ArrayList<>();

		BlacklistedWords blacklistedWords = new BlacklistedWords();
		blacklistedWords.setWord("abc");
		blacklistedWords.setLangCode("ENG");
		blacklistedWords.setDescription("no description available");

		words.add(blacklistedWords);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetAllWordsBylangCodeMappingException() throws Exception {
		when(wordsRepository.findAllByLangCode(anyString())).thenReturn(words);
		when(mapperUtil.mapAll(words, BlacklistedWordsDto.class)).thenThrow(MappingException.class,
				ConfigurationException.class, IllegalArgumentException.class);
		mockMvc.perform(get("/blacklistedwords/{langcode}", "ENG")).andExpect(status().isNotAcceptable());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testGetAllWordsBylangCodeInvalidArgException() throws Exception {
		when(wordsRepository.findAllByLangCode(anyString())).thenReturn(words);
		when(mapperUtil.mapAll(words, BlacklistedWordsDto.class)).thenThrow(MappingException.class,
				ConfigurationException.class, IllegalArgumentException.class);
		mockMvc.perform(get("/blacklistedwords/{langcode}", " ")).andExpect(status().isNotAcceptable());
	}

	@Test
	public void testGetAllWordsBylangCodeNullArgException() throws Exception {
		mockMvc.perform(get("/blacklistedwords/{langcode}", " ")).andExpect(status().isNotAcceptable());
	}
}
