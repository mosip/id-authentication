package io.mosip.registration.processor.core.util;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.core.util.DateUtils;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.digital.signature.dto.SignRequestDto;
import io.mosip.registration.processor.core.digital.signature.dto.SignResponseDto;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.http.RequestWrapper;
import io.mosip.registration.processor.core.http.ResponseWrapper;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.core.util.exception.DigitalSignatureException;

@Component
public class DigitalSignatureUtility {

	@Autowired
	private RegistrationProcessorRestClientService<Object> registrationProcessorRestService;

	@Autowired
	private Environment env;

	@Autowired
	ObjectMapper mapper;

	private static final String DIGITAL_SIGNATURE_ID = "mosip.registration.processor.digital.signature.id";
	private static final String DATETIME_PATTERN = "mosip.registration.processor.datetime.pattern";
	private static final String REG_PROC_APPLICATION_VERSION = "mosip.registration.processor.application.version";

	public String getDigitalSignature(String data) {
		SignRequestDto dto=new SignRequestDto();
		dto.setData(data);
		RequestWrapper<SignRequestDto> request=new RequestWrapper<>();
		request.setRequest(dto);
		request.setId(env.getProperty(DIGITAL_SIGNATURE_ID));
		request.setMetadata(null);
		DateTimeFormatter format = DateTimeFormatter.ofPattern(env.getProperty(DATETIME_PATTERN));
		LocalDateTime localdatetime = LocalDateTime
				.parse(DateUtils.getUTCCurrentDateTimeString(env.getProperty(DATETIME_PATTERN)), format);
		request.setRequesttime(localdatetime);
		request.setVersion(env.getProperty(REG_PROC_APPLICATION_VERSION));

		try {
			ResponseWrapper<SignResponseDto> response = (ResponseWrapper) registrationProcessorRestService.postApi(ApiName.DIGITALSIGNATURE, "", "", request, ResponseWrapper.class);
			SignResponseDto signResponseDto = mapper.readValue(mapper.writeValueAsString(response.getResponse()), SignResponseDto.class);
			return signResponseDto.getSignature();
		} catch (ApisResourceAccessException | IOException e) {
			throw new DigitalSignatureException(e.getMessage(), e);
		}

	}
}

