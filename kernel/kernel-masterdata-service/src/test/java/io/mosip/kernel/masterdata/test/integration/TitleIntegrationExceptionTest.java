package io.mosip.kernel.masterdata.test.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.masterdata.entity.Title;
import io.mosip.kernel.masterdata.entity.TitleId;
import io.mosip.kernel.masterdata.repository.TitleRepository;
import io.mosip.kernel.masterdata.utils.ObjectMapperUtil;

/**
 * Test class for testing exceptions for fetching titles from master data
 * 
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TitleIntegrationExceptionTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private TitleRepository titleRepository;

	@MockBean
	ModelMapper modelMapper;

	@MockBean
	ObjectMapperUtil mapperUtil;

	private List<Title> titleList;

	private List<Title> titlesNull;

	private TitleId titleId;

	@Before
	public void prepareData() {
		titleList = new ArrayList<>();
		Title title = new Title();
		titleId = new TitleId();
		titleId.setLanguageCode("ENG");
		titleId.setTitleCode("ABC");
		title.setIsActive(true);
		title.setCreatedBy("Ajay");
		title.setCreatedtimes(null);
		title.setId(titleId);
		title.setTitleDescription("AAAAAAAAAAAA");
		title.setTitleName("HELLO");
		title.setUpdatedBy("XYZ");
		title.setUpdatedtimes(null);
		titleList.add(title);

	}

	@Test
	public void testGetTitleByLanguageCodeFetchException() throws Exception {

		Mockito.when(titleRepository.getThroughLanguageCode("ENG")).thenThrow(DataAccessLayerException.class);

		mockMvc.perform(get("/title/ENG").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError());

	}

	@Test
	public void testGetTitleByLanguageCodeNotFoundException() throws Exception {

		titlesNull = new ArrayList<>();

		Mockito.when(titleRepository.getThroughLanguageCode("ENG")).thenReturn(titlesNull);

		mockMvc.perform(get("/title/ENG").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());

	}

	@Test
	public void testGetAllTitleFetchException() throws Exception {

		Mockito.when(titleRepository.findAll(Title.class)).thenThrow(DataAccessLayerException.class);

		mockMvc.perform(get("/title").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError());

	}

	@Test
	public void testGetAllTitleNotFoundException() throws Exception {

		titlesNull = new ArrayList<>();

		Mockito.when(titleRepository.findAll(Title.class)).thenReturn(titlesNull);

		mockMvc.perform(get("/title").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());

	}

}
