package io.mosip.kernel.masterdata.test.service;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modelmapper.ConfigurationException;
import org.modelmapper.MappingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.core.exception.IllegalArgumentException;
import io.mosip.kernel.masterdata.dto.BlacklistedWordsDto;
import io.mosip.kernel.masterdata.entity.BlacklistedWords;
import io.mosip.kernel.masterdata.exception.BlacklistedWordsMappingException;
import io.mosip.kernel.masterdata.repository.BlacklistedWordsRepository;
import io.mosip.kernel.masterdata.service.BlacklistedWordsService;
import io.mosip.kernel.masterdata.utils.ObjectMapperUtil;

@SpringBootTest
@RunWith(SpringRunner.class)
public class BlacklistedServiceExceptionTest {
	@Autowired
	private BlacklistedWordsService blacklistedWordsService;
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
	@Test(expected = BlacklistedWordsMappingException.class)
	public void testGetAllBlackListedWordsMappingException() {
		when(wordsRepository.findAllByLangCode(Mockito.anyString())).thenReturn(words);
		when(mapperUtil.mapAll(words, BlacklistedWordsDto.class)).thenThrow(MappingException.class,
				ConfigurationException.class, IllegalArgumentException.class);
		blacklistedWordsService.getAllBlacklistedWordsBylangCode("ENG");
	}

}
