package io.mosip.kernel.masterdata.test.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import io.mosip.kernel.masterdata.entity.ReasonCategory;
import io.mosip.kernel.masterdata.entity.ReasonList;
import io.mosip.kernel.masterdata.repository.ReasonRepository;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class PacketRejectionReasonIntegrationTest {

	@Autowired
	MockMvc mockMvc;

	@MockBean
	ReasonRepository reasonRepository;

	private List<ReasonCategory> reasoncategories;

	@Before
	public void setUp() {
		ReasonCategory reasonCategory = new ReasonCategory();
		ReasonList reasonList = new ReasonList();
		Set<ReasonList> reasonListSet = new HashSet<>();
		reasonList.setCode("RL1");
		reasonList.setLangCode("ENG");
		reasonList.setDescription("reasonList");
		reasonListSet.add(reasonList);
		reasonCategory.setReasons(reasonListSet);
		reasonCategory.setCode("RC1");
		reasonCategory.setName("reasonCategory");
		reasonCategory.setDescription("reason_category");
		reasonCategory.setLanguageCode("ENG");
		reasonCategory.setIsActive(true);
		reasonCategory.setIsDeleted(false);
		reasoncategories = new ArrayList<>();
		reasoncategories.add(reasonCategory);

	}

	@Test
	public void getAllRjectionReasonTest() throws Exception {
		Mockito.when(reasonRepository.findReasonCategoryByIsActiveTrueAndIsDeletedFalse()).thenReturn(reasoncategories);
		mockMvc.perform(get("/packetRejectionReasons")).andExpect(status().isOk());
	}

	@Test
	public void getAllRejectionReasonByCodeAndLangCodeTest() throws Exception {
		Mockito.when(reasonRepository.findReasonCategoryByCodeAndLanguageCodeAndIsActiveTrueAndIsDeletedFalse(ArgumentMatchers.any(),
				ArgumentMatchers.any())).thenReturn(reasoncategories);
		mockMvc.perform(get("/packetRejectionReasons/{code}/{languageCode}", "RC1", "ENG")).andExpect(status().isOk());
	}
}
