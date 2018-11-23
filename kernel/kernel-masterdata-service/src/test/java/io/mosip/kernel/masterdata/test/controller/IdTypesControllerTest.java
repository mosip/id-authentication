package io.mosip.kernel.masterdata.test.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import io.mosip.kernel.masterdata.entity.IdType;
import io.mosip.kernel.masterdata.repository.IdTypeRepository;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class IdTypesControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private IdTypeRepository repository;

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
	public void testIdTypeController() throws Exception {
		List<IdType> idTypeList = new ArrayList<>();
		idTypeList.add(idType);
		Mockito.when(repository.findByLangCodeAndIsActiveTrueAndIsDeletedFalse(anyString())).thenReturn(idTypeList);
		mockMvc.perform(get("/idtypes/{languagecode}", "ENG")).andExpect(status().isOk());
	}

}
