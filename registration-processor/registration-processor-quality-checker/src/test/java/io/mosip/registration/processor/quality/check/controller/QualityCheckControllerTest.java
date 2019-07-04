package io.mosip.registration.processor.quality.check.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import io.mosip.registration.processor.core.spi.packetmanager.QualityCheckManager;
import io.mosip.registration.processor.quality.check.dto.DecisionStatus;
import io.mosip.registration.processor.quality.check.dto.QCUserDto;

@RunWith(SpringRunner.class)
@WebMvcTest(QualityCheckerController.class)
public class QualityCheckControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private QualityCheckManager<String, QCUserDto> qualityCheckManger;
	List<QCUserDto> dtolist1;
	List<QCUserDto> dtolist2;
	String arrayToJson;

	@Before
	public void setup() throws JsonProcessingException {
		QCUserDto dto1 = new QCUserDto();
		dto1.setQcUserId("qc001");
		dto1.setRegId("123456");
		dto1.setDecisionStatus(DecisionStatus.PENDING);
		QCUserDto dto2 = new QCUserDto();
		dto1.setQcUserId("qc001");
		dto1.setRegId("1234567");
		dto1.setDecisionStatus(DecisionStatus.PENDING);
		QCUserDto dto01 = new QCUserDto();
		dto1.setQcUserId("qc001");
		dto1.setRegId("123456");
		dto1.setDecisionStatus(DecisionStatus.ACCEPTED);
		QCUserDto dto02 = new QCUserDto();
		dto1.setQcUserId("qc001");
		dto1.setRegId("1234567");
		dto1.setDecisionStatus(DecisionStatus.REJECTED);
		dtolist1 = new ArrayList<>();
		dtolist2 = new ArrayList<>();
		dtolist1.add(dto1);
		dtolist1.add(dto2);
		dtolist2.add(dto01);
		dtolist2.add(dto02);
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
		arrayToJson = objectMapper.writeValueAsString(dtolist1);
		Mockito.when(qualityCheckManger.updateQCUserStatus(ArgumentMatchers.any())).thenReturn(dtolist2);

	}

	@Test
	public void updateQCUserStatusControllerSuccessTest() throws Exception {

		mockMvc.perform(post("/v0.1/registration-processor/quality-checker/decisionStatus")
				.accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON).content(arrayToJson))
				.andExpect(status().isOk());
	}

	@Test
	public void updateQCUserStatusControllerFailureTest() throws Exception {

		mockMvc.perform(post("/v0.1/registration-processor/quality-checker/decisionStatus")
				.accept(MediaType.APPLICATION_JSON_VALUE).contentType(MediaType.APPLICATION_JSON).content(""))
				.andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

}
