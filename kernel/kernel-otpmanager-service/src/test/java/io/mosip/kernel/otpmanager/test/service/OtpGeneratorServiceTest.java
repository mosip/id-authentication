package io.mosip.kernel.otpmanager.test.service;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.otpmanager.dto.OtpGeneratorRequestDto;
import io.mosip.kernel.otpmanager.entity.OtpEntity;
import io.mosip.kernel.otpmanager.repository.OtpRepository;
import io.mosip.kernel.otpmanager.test.OtpmanagerTestBootApplication;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest(classes = OtpmanagerTestBootApplication.class)
public class OtpGeneratorServiceTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	OtpRepository repository;

	@WithUserDetails("individual")
	@Test
	public void testOtpGeneratorServicePositiveCase() throws Exception {
		OtpGeneratorRequestDto otpGeneratorRequestDto = new OtpGeneratorRequestDto();
		otpGeneratorRequestDto.setKey("testKey");
		RequestWrapper<OtpGeneratorRequestDto> reqWrapperDTO = new RequestWrapper<>();
		reqWrapperDTO.setId("ID");
		reqWrapperDTO.setMetadata(null);
		reqWrapperDTO.setRequest(otpGeneratorRequestDto);
		reqWrapperDTO.setRequesttime(LocalDateTime.now());
		reqWrapperDTO.setVersion("v1.0");
		String json = objectMapper.writeValueAsString(reqWrapperDTO);
		mockMvc.perform(post("/otp/generate").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk()).andReturn();
	}

	@WithUserDetails("individual")
	@Test
	public void testOtpGenerationFreezedCase() throws Exception {
		OtpGeneratorRequestDto otpGeneratorRequestDto = new OtpGeneratorRequestDto();
		otpGeneratorRequestDto.setKey("testKey");
		RequestWrapper<OtpGeneratorRequestDto> reqWrapperDTO = new RequestWrapper<>();
		reqWrapperDTO.setId("ID");
		reqWrapperDTO.setMetadata(null);
		reqWrapperDTO.setRequest(otpGeneratorRequestDto);
		reqWrapperDTO.setRequesttime(LocalDateTime.now());
		reqWrapperDTO.setVersion("v1.0");
		String json = objectMapper.writeValueAsString(reqWrapperDTO);
		OtpEntity entity = new OtpEntity();
		entity.setOtp("1234");
		entity.setId("testKey");
		entity.setValidationRetryCount(0);
		entity.setStatusCode("KEY_FREEZED");
		entity.setUpdatedDtimes(LocalDateTime.now());
		when(repository.findById(OtpEntity.class, "testKey")).thenReturn(entity);
		mockMvc.perform(post("/otp/generate").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk()).andReturn();
	}
}
