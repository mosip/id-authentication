
package io.mosip.kernel.otpmanager.test.exception;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
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
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.exception.ServiceError;
import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.otpmanager.constant.OtpErrorConstants;
import io.mosip.kernel.otpmanager.dto.OtpGeneratorRequestDto;
import io.mosip.kernel.otpmanager.exception.OtpInvalidArgumentException;
import io.mosip.kernel.otpmanager.service.impl.OtpGeneratorServiceImpl;
import io.mosip.kernel.otpmanager.service.impl.OtpValidatorServiceImpl;
import io.mosip.kernel.otpmanager.test.OtpmanagerTestBootApplication;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest(classes = OtpmanagerTestBootApplication.class)
public class OtpExceptionTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private OtpGeneratorServiceImpl service;

	@MockBean
	private OtpValidatorServiceImpl validatorService;

	@WithUserDetails("individual")
	@Test
	public void testForExceptionWhenKeyIsNull() throws Exception {
		List<ServiceError> validationErrorsList = new ArrayList<>();
		validationErrorsList.add(new ServiceError(OtpErrorConstants.OTP_VAL_INVALID_KEY_INPUT.getErrorCode(),
				OtpErrorConstants.OTP_VAL_INVALID_KEY_INPUT.getErrorMessage()));
		when(service.getOtp(Mockito.any())).thenThrow(new OtpInvalidArgumentException(validationErrorsList));
		OtpGeneratorRequestDto otpGeneratorRequestDto = new OtpGeneratorRequestDto();
		otpGeneratorRequestDto.setKey(null);
		RequestWrapper<OtpGeneratorRequestDto> reqWrapperDTO = new RequestWrapper<>();
		reqWrapperDTO.setId("ID");
		reqWrapperDTO.setMetadata(null);
		reqWrapperDTO.setRequest(otpGeneratorRequestDto);
		reqWrapperDTO.setRequesttime(LocalDateTime.now());
		reqWrapperDTO.setVersion("v1.0");
		String json = objectMapper.writeValueAsString(reqWrapperDTO);
		mockMvc.perform(post("/otp/generate").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk());
	}

	@WithUserDetails("individual")
	@Test
	public void testForExceptionWhenKeyNotFound() throws Exception {
		List<ServiceError> validationErrorsList = new ArrayList<>();
		validationErrorsList.add(new ServiceError(OtpErrorConstants.OTP_VAL_INVALID_KEY_INPUT.getErrorCode(),
				OtpErrorConstants.OTP_VAL_INVALID_KEY_INPUT.getErrorMessage()));
		when(validatorService.validateOtp(Mockito.any(), Mockito.any()))
				.thenThrow(new OtpInvalidArgumentException(validationErrorsList));
		mockMvc.perform(get("/otp/validate?key=test&otp=3212").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@WithUserDetails("individual")
	@Test
	public void testForExceptionWhenKeyLengthInvalid() throws Exception {
		List<ServiceError> validationErrorsList = new ArrayList<>();
		validationErrorsList.add(new ServiceError(OtpErrorConstants.OTP_VAL_INVALID_KEY_INPUT.getErrorCode(),
				OtpErrorConstants.OTP_VAL_INVALID_KEY_INPUT.getErrorMessage()));
		when(validatorService.validateOtp(Mockito.any(), Mockito.any()))
				.thenThrow(new OtpInvalidArgumentException(validationErrorsList));
		mockMvc.perform(get("/otp/validate?key=sa&otp=3212").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

}
