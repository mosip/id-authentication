package io.mosip.kernel.masterdata.test.integration;

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

import io.mosip.kernel.masterdata.entity.GenderType;
import io.mosip.kernel.masterdata.entity.GenderTypeId;
import io.mosip.kernel.masterdata.repository.GenderTypeRepository;

/**
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class GenderTypeIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private GenderTypeRepository genderRepository;

	private List<GenderType> genderTypes;
	private GenderTypeId genderId;

	@Before
	public void prepareData() {
		genderTypes = new ArrayList<>();
		GenderType genderType = new GenderType();
		genderId = new GenderTypeId();
		genderId.setGenderCode("123");
		genderId.setGenderName("David");
		genderType.setIsActive(true);
		genderType.setCreatedBy("Ajay");
		genderType.setCreatedtimes(null);
		genderType.setIsDeleted(true);
		genderType.setDeletedtimes(null);
		genderType.setId(genderId);
		genderType.setLanguageCode("ENG");
		genderType.setUpdatedBy("Vijay");
		genderType.setUpdatedtimes(null);
		genderTypes.add(genderType);

	}

	@Test
	public void testGetGenderByLanguageCode() throws Exception {

		Mockito.when(genderRepository.findGenderByLanguageCode(Mockito.anyString())).thenReturn(genderTypes);
		mockMvc.perform(get("/gendertype/{languageCode}", "ENG")).andExpect(status().isOk());

	}

	@Test
	public void testGetAllGenders() throws Exception {
		Mockito.when(genderRepository.findAll(GenderType.class)).thenReturn(genderTypes);
		mockMvc.perform(get("/gendertype")).andExpect(status().isOk());

	}

}
