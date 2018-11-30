package io.mosip.kernel.otpmanager.test.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

import io.mosip.kernel.otpmanager.dto.OtpValidatorResponseDto;
import io.mosip.kernel.otpmanager.entity.OtpEntity;
import io.mosip.kernel.otpmanager.repository.OtpRepository;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class OtpValidationsTest {

	@Autowired
	MockMvc mockMvc;

	@MockBean
	private OtpRepository otpRepository;

	@Test
	public void testNullKey() throws Exception {
		when(otpRepository.findById(OtpEntity.class, "testKey")).thenReturn(null);
		mockMvc.perform(get("/otp/validate?key=testKey&otp=1234").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotAcceptable()).andReturn();
	}

	@Test
	public void testOtpValidatorServiceExpiredOTPCase() throws Exception {
		OtpEntity entity = new OtpEntity();
		entity.setOtp("1234");
		entity.setId("testKey");
		entity.setValidationRetryCount(0);
		entity.setStatusCode("OTP_UNUSED");
		entity.setGeneratedDtimes(LocalDateTime.now().minusMinutes(3));
		when(otpRepository.findById(OtpEntity.class, "testKey")).thenReturn(entity);
		MvcResult result = mockMvc
				.perform(get("/otp/validate?key=testKey&otp=1234").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotAcceptable()).andReturn();
		ObjectMapper mapper = new ObjectMapper();
		OtpValidatorResponseDto returnResponse = mapper.readValue(result.getResponse().getContentAsString(),
				OtpValidatorResponseDto.class);
		assertThat(returnResponse.getStatus(), is("failure"));
	}

}
