package io.mosip.kernel.otpmanager.test.service;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.otpmanager.entity.OtpEntity;
import io.mosip.kernel.otpmanager.exception.OtpInvalidArgumentException;
import io.mosip.kernel.otpmanager.repository.OtpRepository;
import io.mosip.kernel.otpmanager.test.OtpmanagerTestBootApplication;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest(classes = OtpmanagerTestBootApplication.class)
public class OtpValidationsTest {

	@Autowired
	MockMvc mockMvc;

	@Value("${mosip.kernel.otp.default-length}")
	double otpLength;

	@MockBean
	private OtpRepository otpRepository;

	@WithUserDetails("individual")
	@Test
	public void testNullKey() throws Exception {
		when(otpRepository.findById(OtpEntity.class, "testKey")).thenReturn(null);
		mockMvc.perform(get("/otp/validate?key=testKey&otp=1234").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
	}

	@WithUserDetails("individual")
	@Ignore
	@Test
	public void testOtpValidatorServiceExpiredOTPCase() throws Exception {
		Random randomKey = new Random();
		double id = Math.pow(10, otpLength) + randomKey.nextInt((int) (9 * Math.pow(10, otpLength)));
		OtpEntity entity = new OtpEntity();
		entity.setOtp("1234");
		entity.setId(Double.toString(id));
		entity.setValidationRetryCount(0);
		entity.setStatusCode("OTP_UNUSED");
		entity.setGeneratedDtimes(LocalDateTime.now(ZoneId.of("UTC")).minusMinutes(3));
		when(otpRepository.findById(OtpEntity.class, "testKey")).thenReturn(entity);
		mockMvc.perform(get("/otp/validate?key=testKey&otp=1234").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.response.status", is("failure")));

	}

	@WithUserDetails("individual")
	@Test
	public void testOtpValidatorServiceKeyEmptyCase() throws Exception {
		ServiceError serviceError = new ServiceError("TESTCODE", "TESTMESSAGE");
		List<ServiceError> validationErrorsList = new ArrayList<>();
		validationErrorsList.add(serviceError);
		when(otpRepository.findById(OtpEntity.class, "testKey"))
				.thenThrow(new OtpInvalidArgumentException(validationErrorsList));
		mockMvc.perform(get("/otp/validate?key=&otp=1234").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
	}

	@WithUserDetails("individual")
	@Test
	public void testOtpValidatorServiceOtpEmptyCase() throws Exception {
		ServiceError serviceError = new ServiceError("TESTCODE", "TESTMESSAGE");
		List<ServiceError> validationErrorsList = new ArrayList<>();
		validationErrorsList.add(serviceError);
		when(otpRepository.findById(OtpEntity.class, "testKey"))
				.thenThrow(new OtpInvalidArgumentException(validationErrorsList));
		mockMvc.perform(get("/otp/validate?key=testkey&otp=").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
	}

	@WithUserDetails("individual")
	@Test
	public void testOtpValidatorServiceKeyLengthLessThanRequiredCase() throws Exception {
		ServiceError serviceError = new ServiceError("TESTCODE", "TESTMESSAGE");
		List<ServiceError> validationErrorsList = new ArrayList<>();
		validationErrorsList.add(serviceError);
		when(otpRepository.findById(OtpEntity.class, "testKey"))
				.thenThrow(new OtpInvalidArgumentException(validationErrorsList));
		mockMvc.perform(
				get("/otp/validate").param("key", "sa").param("otp", "123456").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn();
	}

	@WithUserDetails("individual")
	@Test
	public void testOtpValidatorServiceKeyLengthMoreThanRequiredCase() throws Exception {
		ServiceError serviceError = new ServiceError("TESTCODE", "TESTMESSAGE");
		List<ServiceError> validationErrorsList = new ArrayList<>();
		validationErrorsList.add(serviceError);
		when(otpRepository.findById(OtpEntity.class, "testKey"))
				.thenThrow(new OtpInvalidArgumentException(validationErrorsList));
		mockMvc.perform(get("/otp/validate").param("key",
				"ykbbgyhogsmziqozetsyexoazpqhcpqywqmuyyijaweoswjlvhemamrmbuorixvnwlrhgfbnrmoorscjkllmgzqxtauoolvhoiyxfwoiotkvimcqshxvxplrqsfxmlmroyxcphstayxnowmjsnwdwhazpotqqrafuvpcaccaxneavptzwwsukhjqzwhjpdgrbqfybsyyryqlbrpdakuvtvswcwpzvkkaonblwlkjvytiodlnvsodsxkkgbbzvxkjbgbhnnvpkohydywdaudekflgbvbkeqwrekdgsneomyovczvnqhuitmr")
				.param("otp", "123456").contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();
	}

	@WithUserDetails("individual")
	@Test
	public void testOtpValidatorServiceOtpIsCharacter() throws Exception {
		ServiceError serviceError = new ServiceError("TESTCODE", "TESTMESSAGE");
		List<ServiceError> validationErrorsList = new ArrayList<>();
		validationErrorsList.add(serviceError);
		when(otpRepository.findById(OtpEntity.class, "testKey"))
				.thenThrow(new OtpInvalidArgumentException(validationErrorsList));
		mockMvc.perform(get("/otp/validate").param("key", "test").param("otp", "INVALID-TYPE")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();
	}
}
