package io.mosip.kernel.otpmanager.test.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertThat;
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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.otpmanager.dto.OtpGeneratorResponseDto;
import io.mosip.kernel.otpmanager.entity.OtpEntity;
import io.mosip.kernel.otpmanager.repository.OtpRepository;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class OtpGeneratorServiceTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	OtpRepository repository;

	@Test
	public void testOtpGeneratorServicePositiveCase() throws Exception {
		String json = "{\"key\":\"testKey\"}";
		MvcResult result = mockMvc
				.perform(post("/v1.0/otp/generate").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isCreated()).andReturn();
		ObjectMapper objectMapper = new ObjectMapper();
		OtpGeneratorResponseDto response = objectMapper.readValue(result.getResponse().getContentAsString(),
				OtpGeneratorResponseDto.class);
		assertThat(response.getOtp(), isA(String.class));
	}

	@Test
	public void testOtpGenerationFreezedCase() throws Exception {
		String json = "{\"key\":\"testKey\"}";
		OtpEntity entity = new OtpEntity();
		entity.setOtp("1234");
		entity.setId("testKey");
		entity.setValidationRetryCount(0);
		entity.setStatusCode("KEY_FREEZED");
		entity.setUpdatedDtimes(LocalDateTime.now());
		when(repository.findById(OtpEntity.class, "testKey")).thenReturn(entity);
		MvcResult result = mockMvc
				.perform(post("/v1.0/otp/generate").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isCreated()).andReturn();
		ObjectMapper mapper = new ObjectMapper();
		OtpGeneratorResponseDto returnResponse = mapper.readValue(result.getResponse().getContentAsString(),
				OtpGeneratorResponseDto.class);
		assertThat(returnResponse.getStatus(), is("USER_BLOCKED"));
	}
}
