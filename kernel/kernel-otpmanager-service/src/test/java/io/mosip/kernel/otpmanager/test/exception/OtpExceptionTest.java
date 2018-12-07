
package io.mosip.kernel.otpmanager.test.exception;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.otpmanager.OtpmanagerBootApplication;
import io.mosip.kernel.otpmanager.constant.OtpErrorConstants;
import io.mosip.kernel.otpmanager.exception.OtpInvalidArgumentException;
import io.mosip.kernel.otpmanager.service.impl.OtpGeneratorServiceImpl;
import io.mosip.kernel.otpmanager.service.impl.OtpValidatorServiceImpl;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest(classes = OtpmanagerBootApplication.class)
public class OtpExceptionTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private OtpGeneratorServiceImpl service;

	@MockBean
	private OtpValidatorServiceImpl validatorService;

	@Test
	public void testForExceptionWhenKeyIsNull() throws Exception {
		when(service.getOtp(Mockito.any())).thenThrow(OtpInvalidArgumentException.class);
		String json = "{\"key\":null}";
		mockMvc.perform(post("/v1.0/otp/generate").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isNotAcceptable()).andExpect(jsonPath("$.errors[0].errorCode", is("KER-OTG-001")));
	}

	@Test
	public void testForExceptionWhenKeyNotFound() throws Exception {
		List<ServiceError> validationErrorsList = new ArrayList<>();
		validationErrorsList.add(new ServiceError(OtpErrorConstants.OTP_VAL_INVALID_KEY_INPUT.getErrorCode(),
				OtpErrorConstants.OTP_VAL_INVALID_KEY_INPUT.getErrorMessage()));
		when(validatorService.validateOtp(Mockito.any(), Mockito.any()))
				.thenThrow(new OtpInvalidArgumentException(validationErrorsList));
		mockMvc.perform(get("/v1.0/otp/validate?key=test&otp=3212").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotAcceptable());
	}

	@Test
	public void testForExceptionWhenKeyLengthInvalid() throws Exception {
		List<ServiceError> validationErrorsList = new ArrayList<>();
		validationErrorsList.add(new ServiceError(OtpErrorConstants.OTP_VAL_INVALID_KEY_INPUT.getErrorCode(),
				OtpErrorConstants.OTP_VAL_INVALID_KEY_INPUT.getErrorMessage()));
		when(validatorService.validateOtp(Mockito.any(), Mockito.any()))
				.thenThrow(new OtpInvalidArgumentException(validationErrorsList));
		mockMvc.perform(get("/v1.0/otp/validate?key=sa&otp=3212").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotAcceptable());
	}

}
