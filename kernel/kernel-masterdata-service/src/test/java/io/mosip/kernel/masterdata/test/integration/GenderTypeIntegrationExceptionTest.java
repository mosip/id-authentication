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
import io.mosip.kernel.masterdata.entity.GenderType;
import io.mosip.kernel.masterdata.entity.GenderTypeId;
import io.mosip.kernel.masterdata.repository.GenderTypeRepository;
import io.mosip.kernel.masterdata.utils.ObjectMapperUtil;

/**
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class GenderTypeIntegrationExceptionTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private GenderTypeRepository genderTypeRepository;

	@MockBean
	ModelMapper modelMapper;

	@MockBean
	ObjectMapperUtil mapperUtil;

	private List<GenderType> genderTypes;

	private List<GenderType> genderTypesNull;

	private GenderTypeId genderId;

	@Before
	public void prepareData() {
		genderTypes = new ArrayList<>();
		genderTypesNull = new ArrayList<>();
		GenderType genderType = new GenderType();
		genderId = new GenderTypeId();
		genderId.setGenderCode("123");
		genderId.setGenderName("Raj");
		genderType.setIsActive(true);
		genderType.setCreatedBy("John");
		genderType.setCreatedtimes(null);
		genderType.setIsDeleted(true);
		genderType.setDeletedtimes(null);
		genderType.setId(genderId);
		genderType.setLanguageCode("ENG");
		genderType.setUpdatedBy("Dom");
		genderType.setUpdatedtimes(null);
		genderTypes.add(genderType);

	}

	@Test
	public void testGetGenderByLanguageCodeFetchException() throws Exception {

		Mockito.when(genderTypeRepository.findGenderByLanguageCode("ENG")).thenThrow(DataAccessLayerException.class);

		mockMvc.perform(get("/gendertype/ENG").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError());

	}

	@Test
	public void testGetGenderByLanguageCodeNotFoundException() throws Exception {

		Mockito.when(genderTypeRepository.findGenderByLanguageCode("ENG")).thenReturn(genderTypesNull);

		mockMvc.perform(get("/gendertype/ENG").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());

	}

	@Test
	public void testGetAllGenderFetchException() throws Exception {

		Mockito.when(genderTypeRepository.findAll(GenderType.class)).thenThrow(DataAccessLayerException.class);

		mockMvc.perform(get("/gendertype").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError());

	}

	@Test
	public void testGetAllGenderNotFoundException() throws Exception {

		Mockito.when(genderTypeRepository.findAll(GenderType.class)).thenReturn(genderTypesNull);

		mockMvc.perform(get("/gendertype").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());

	}

}
