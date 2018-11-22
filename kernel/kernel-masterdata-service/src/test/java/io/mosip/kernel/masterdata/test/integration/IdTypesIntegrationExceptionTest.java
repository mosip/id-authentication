package io.mosip.kernel.masterdata.test.integration;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.masterdata.entity.IdType;
import io.mosip.kernel.masterdata.repository.IdTypeRepository;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class IdTypesIntegrationExceptionTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	ModelMapper modelMapper;

	@MockBean
	IdTypeRepository idTypeRepository;

	IdType idType;

	@Before
	public void setInitials() {
		idType = new IdType();
		idType.setActive(true);
		idType.setCrBy("testCreation");
		idType.setLangCode("ENG");
		idType.setCode("POA");
		idType.setDescr("Proof Of Address");
	}

	@Test
	public void getIdTypesByLanguageCodeFetchExceptionTest() throws Exception {
		when(idTypeRepository.findByLangCodeAndIsActiveTrueAndIsDeletedFalse("ENG"))
				.thenThrow(DataAccessLayerException.class);
		mockMvc.perform(get("/idtypes/ENG").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void getIdTypesByLanguageCodeNotFoundExceptionTest() throws Exception {
		List<IdType> idTypeList = new ArrayList<>();
		idTypeList.add(idType);
		when(idTypeRepository.findByLangCodeAndIsActiveTrueAndIsDeletedFalse("ENG")).thenReturn(idTypeList);
		mockMvc.perform(get("/idtypes/HIN").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
	}

}
