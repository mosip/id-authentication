package io.mosip.kernel.otpmanager.test.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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

import io.mosip.kernel.otpmanager.dto.OtpValidatorResponseDto;
import io.mosip.kernel.otpmanager.entity.OtpEntity;
import io.mosip.kernel.otpmanager.repository.OtpRepository;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class OtpValidatorServiceTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	OtpRepository repository;

	@Test
	public void testOtpValidatorServicePositiveCase() throws Exception {
		OtpEntity entity = new OtpEntity();
		entity.setOtp("1234");
		entity.setId("testKey");
		entity.setValidationRetryCount(0);
		entity.setStatusCode("OTP_UNUSED");
		entity.setUpdatedDtimes(LocalDateTime.now().plusSeconds(10));
		when(repository.findById(OtpEntity.class, "testKey")).thenReturn(entity);
		MvcResult result = mockMvc
				.perform(get("/v1.0/otp/validate?key=testKey&otp=1234").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		ObjectMapper mapper = new ObjectMapper();
		OtpValidatorResponseDto returnResponse = mapper.readValue(result.getResponse().getContentAsString(),
				OtpValidatorResponseDto.class);
		assertThat(returnResponse.getStatus(), is("success"));
	}

	@Test
	public void testOtpValidatorServiceNegativeCase() throws Exception {
		OtpEntity entity = new OtpEntity();
		entity.setOtp("1234");
		entity.setId("testKey");
		entity.setValidationRetryCount(0);
		entity.setStatusCode("OTP_UNUSED");
		entity.setUpdatedDtimes(LocalDateTime.now());
		when(repository.findById(OtpEntity.class, "testKey")).thenReturn(entity);
		MvcResult result = mockMvc
				.perform(get("/v1.0/otp/validate?key=testKey&otp=5431").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotAcceptable()).andReturn();
		ObjectMapper mapper = new ObjectMapper();
		OtpValidatorResponseDto returnResponse = mapper.readValue(result.getResponse().getContentAsString(),
				OtpValidatorResponseDto.class);
		assertNotEquals(returnResponse.getStatus(), is("success"));
	}

	@Test
	public void testOtpValidatorServiceWhenMaxAttemptReached() throws Exception {
		OtpEntity entity = new OtpEntity();
		entity.setOtp("1234");
		entity.setId("testKey");
		entity.setValidationRetryCount(3);
		entity.setStatusCode("OTP_UNUSED");
		entity.setUpdatedDtimes(LocalDateTime.now());
		when(repository.findById(OtpEntity.class, "testKey")).thenReturn(entity);
		MvcResult result = mockMvc
				.perform(get("/v1.0/otp/validate?key=testKey&otp=5431").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotAcceptable()).andReturn();
		ObjectMapper mapper = new ObjectMapper();
		OtpValidatorResponseDto returnResponse = mapper.readValue(result.getResponse().getContentAsString(),
				OtpValidatorResponseDto.class);
		assertNotEquals(returnResponse.getStatus(), is("success"));
	}

	@Test
	public void testOtpValidatorServiceWhenKeyFreezedPositiveCase() throws Exception {
		OtpEntity entity = new OtpEntity();
		entity.setOtp("1234");
		entity.setId("testKey");
		entity.setValidationRetryCount(0);
		entity.setStatusCode("KEY_FREEZED");
		entity.setUpdatedDtimes(LocalDateTime.now().minus(1, ChronoUnit.MINUTES));
		when(repository.findById(OtpEntity.class, "testKey")).thenReturn(entity);
		MvcResult result = mockMvc
				.perform(get("/v1.0/otp/validate?key=testKey&otp=1234").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
		ObjectMapper mapper = new ObjectMapper();
		OtpValidatorResponseDto returnResponse = mapper.readValue(result.getResponse().getContentAsString(),
				OtpValidatorResponseDto.class);
		assertThat(returnResponse.getStatus(), is("success"));
	}

	@Test
	public void testOtpValidatorServiceWhenKeyFreezedNegativeCase() throws Exception {
		OtpEntity entity = new OtpEntity();
		entity.setOtp("1234");
		entity.setId("testKey");
		entity.setValidationRetryCount(0);
		entity.setStatusCode("KEY_FREEZED");
		entity.setUpdatedDtimes(LocalDateTime.now().minus(20, ChronoUnit.SECONDS));
		when(repository.findById(OtpEntity.class, "testKey")).thenReturn(entity);
		MvcResult result = mockMvc
				.perform(get("/v1.0/otp/validate?key=testKey&otp=1234").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotAcceptable()).andReturn();
		ObjectMapper mapper = new ObjectMapper();
		OtpValidatorResponseDto returnResponse = mapper.readValue(result.getResponse().getContentAsString(),
				OtpValidatorResponseDto.class);
		assertThat(returnResponse.getStatus(), is("failure"));
	}

}
