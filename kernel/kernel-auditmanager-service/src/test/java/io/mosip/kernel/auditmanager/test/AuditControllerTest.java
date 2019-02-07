package io.mosip.kernel.auditmanager.test;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.auditmanager.dto.AuditResponseDto;
import io.mosip.kernel.auditmanager.request.AuditRequestDto;
import io.mosip.kernel.auditmanager.service.impl.AuditManagerServiceImpl;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class AuditControllerTest {

	@MockBean
	private AuditManagerServiceImpl service;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	public void generateOtpTest() throws Exception {

		AuditRequestDto auditRequestDto = new AuditRequestDto();
		auditRequestDto.setActionTimeStamp(LocalDateTime.now());
		auditRequestDto.setApplicationId("applicationId");
		auditRequestDto.setApplicationName("applicationName");
		auditRequestDto.setCreatedBy("createdBy");
		auditRequestDto.setDescription("description");
		auditRequestDto.setEventId("eventId");
		auditRequestDto.setEventName("eventName");
		auditRequestDto.setEventType("eventType");
		auditRequestDto.setHostIp("hostIp");
		auditRequestDto.setHostName("hostName");
		auditRequestDto.setId("id");
		auditRequestDto.setIdType("idType");
		auditRequestDto.setModuleId("moduleId");
		auditRequestDto.setModuleName("moduleName");
		auditRequestDto.setSessionUserId("sessionUserId");
		auditRequestDto.setSessionUserName("sessionUserName");

		AuditResponseDto auditResponseDto = new AuditResponseDto();
		auditResponseDto.setStatus(true);
		when(service.addAudit(ArgumentMatchers.any())).thenReturn(auditResponseDto);

		mockMvc.perform(post("/v1.0/audits").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(auditRequestDto))).andExpect(status().isOk())
				.andExpect(jsonPath("$.status", is(true)));


	}

}
