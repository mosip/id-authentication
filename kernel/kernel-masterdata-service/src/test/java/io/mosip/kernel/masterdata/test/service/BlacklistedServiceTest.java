package io.mosip.kernel.masterdata.test.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.masterdata.dto.BlacklistedWordsResponseDto;
import io.mosip.kernel.masterdata.entity.BlacklistedWords;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.exception.RequestException;
import io.mosip.kernel.masterdata.repository.BlacklistedWordsRepository;
import io.mosip.kernel.masterdata.service.BlacklistedWordsService;

@SpringBootTest
@RunWith(SpringRunner.class)
public class BlacklistedServiceTest {
	@Autowired
	private BlacklistedWordsService blacklistedWordsService;
	@MockBean
	private BlacklistedWordsRepository wordsRepository;

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

	@Test(expected = RequestException.class)
	public void testGetAllBlacklistedWordsNullvalue() {
		blacklistedWordsService.getAllBlacklistedWordsBylangCode(null);
	}

	@Test(expected = RequestException.class)
	public void testGetAllBlacklistedWordsEmptyvalue() {
		blacklistedWordsService.getAllBlacklistedWordsBylangCode("");
	}

	@Test
	public void testGetAllBlackListedWordsSuccess() {
		int expected = 1;
		when(wordsRepository.findAllByLangCode(Mockito.anyString())).thenReturn(words);
		BlacklistedWordsResponseDto actual = blacklistedWordsService.getAllBlacklistedWordsBylangCode("ENG");
		assertEquals(actual.getBlacklistedwords().size(), expected);
	}

	@Test(expected = MasterDataServiceException.class)
	public void testGetAllBlackListedWordsFetchException() {
		when(wordsRepository.findAllByLangCode(Mockito.anyString())).thenThrow(DataRetrievalFailureException.class);
		blacklistedWordsService.getAllBlacklistedWordsBylangCode("ENG");
	}

	@Test(expected = DataNotFoundException.class)
	public void testGetAllBlackListedWordsNoDataFound() {
		when(wordsRepository.findAllByLangCode(Mockito.anyString())).thenReturn(null);
		blacklistedWordsService.getAllBlacklistedWordsBylangCode("ENG");
	}

	@Test(expected = DataNotFoundException.class)
	public void testGetAllBlackListedWordsEmptyData() {
		when(wordsRepository.findAllByLangCode(Mockito.anyString())).thenReturn(new ArrayList<>());
		blacklistedWordsService.getAllBlacklistedWordsBylangCode("ENG");
	}

}
