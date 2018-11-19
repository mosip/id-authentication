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
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import io.mosip.kernel.masterdata.entity.ReasonCategory;
import io.mosip.kernel.masterdata.entity.ReasonList;
import io.mosip.kernel.masterdata.repository.ReasonRepository;
import io.mosip.kernel.masterdata.utils.ObjectMapperUtil;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public class PacketRjectionReasonExceptionTest {

	@Autowired
	MockMvc mockMvc;

	@MockBean
	ReasonRepository reasonRepository;

	@MockBean
	ObjectMapperUtil mapperUtil;

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
	public void getAllRjectionReasonFetchExceptionTest() throws Exception {
		Mockito.when(reasonRepository.findReasonCategoryByIsActiveTrueAndIsDeletedFalse())
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/packetRejectionReasons")).andExpect(status().isInternalServerError());
	}

	@Test
	public void getAllRejectionReasonByCodeAndLangCodeFetchExceptionTest() throws Exception {
		Mockito.when(reasonRepository.findReasonCategoryByCodeAndLanguageCodeAndIsActiveTrueAndIsDeletedFalse(
				ArgumentMatchers.any(), ArgumentMatchers.any())).thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/packetRejectionReasons/{code}/{languageCode}", "RC1", "ENG"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void getAllRjectionReasonRecordsNotFoundTest() throws Exception {
		Mockito.when(reasonRepository.findReasonCategoryByIsActiveTrueAndIsDeletedFalse()).thenReturn(null);
		mockMvc.perform(get("/packetRejectionReasons")).andExpect(status().isNotFound());
	}

	@Test
	public void getRjectionReasonByCodeAndLangCodeRecordsNotFoundExceptionTest() throws Exception {
		Mockito.when(reasonRepository.findReasonCategoryByCodeAndLanguageCodeAndIsActiveTrueAndIsDeletedFalse(
				ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(null);
		mockMvc.perform(get("/packetRejectionReasons/{code}/{languageCode}", "RC1", "ENG"))
				.andExpect(status().isNotFound());
	}

	@Test
	public void getRjectionReasonByCodeAndLangCodeRecordsEmptyExceptionTest() throws Exception {
		Mockito.when(reasonRepository.findReasonCategoryByCodeAndLanguageCodeAndIsActiveTrueAndIsDeletedFalse(
				ArgumentMatchers.any(), ArgumentMatchers.any())).thenReturn(new ArrayList<ReasonCategory>());
		mockMvc.perform(get("/packetRejectionReasons/{code}/{languageCode}", "RC1", "ENG"))
				.andExpect(status().isNotFound());
	}

	@Test
	public void getAllRjectionReasonRecordsEmptyExceptionTest() throws Exception {
		Mockito.when(reasonRepository.findReasonCategoryByIsActiveTrueAndIsDeletedFalse())
				.thenReturn(new ArrayList<ReasonCategory>());
		mockMvc.perform(get("/packetRejectionReasons")).andExpect(status().isNotFound());
	}

}
