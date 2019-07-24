package io.mosip.registration.processor.request.handler.service.impl;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.registration.processor.core.code.ApiName;
import io.mosip.registration.processor.core.constant.LoggerFileConstant;
import io.mosip.registration.processor.core.exception.ApisResourceAccessException;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.core.http.RequestWrapper;
import io.mosip.registration.processor.core.http.ResponseWrapper;
import io.mosip.registration.processor.core.logger.RegProcessorLogger;
import io.mosip.registration.processor.core.spi.restclient.RegistrationProcessorRestClientService;
import io.mosip.registration.processor.core.util.JsonUtil;
import io.mosip.registration.processor.request.handler.service.dto.VidRequestDto;
import io.mosip.registration.processor.request.handler.service.dto.VidResponseDTO;
import io.mosip.registration.processor.request.handler.service.exception.VidCreationException;

/**
 * The Class ResidentServiceRePrintServiceImpl.
 */
public class UinCardRePrintServiceImpl {

	@Autowired
	private Environment env;

	@Autowired
	private RegistrationProcessorRestClientService<Object> restClientService;

	@Value("${registration.processor.id.repo.vidType}")
	private String vidType;

	public static final String VID_CREATE_ID = "registration.processor.id.repo.generate";

	public static final String REG_PROC_APPLICATION_VERSION = "registration.processor.id.repo.vidVersion";

	public static final String DATETIME_PATTERN = "mosip.registration.processor.datetime.pattern";

	private static Logger regProcLogger = RegProcessorLogger.getLogger(UinCardRePrintServiceImpl.class);

	@SuppressWarnings("unchecked")
	private void check(String requestUin, String requestVid, String cardType, String registrationId)
			throws ApisResourceAccessException, IOException, VidCreationException {

		String uin = requestUin;
		String vid = requestVid;

		if (cardType == "vid" && vid == null) {

			VidRequestDto vidRequestDto = new VidRequestDto();
			RequestWrapper<VidRequestDto> request = new RequestWrapper<>();
			ResponseWrapper<VidResponseDTO> response = new ResponseWrapper<>();
			vidRequestDto.setUIN(requestUin);
			vidRequestDto.setVidType(vidType);
			request.setId(env.getProperty(VID_CREATE_ID));
			request.setRequest(vidRequestDto);
			DateTimeFormatter format = DateTimeFormatter.ofPattern(env.getProperty(DATETIME_PATTERN));
			LocalDateTime localdatetime = LocalDateTime
					.parse(DateUtils.getUTCCurrentDateTimeString(env.getProperty(DATETIME_PATTERN)), format);
			request.setRequesttime(localdatetime);
			request.setVersion(env.getProperty(REG_PROC_APPLICATION_VERSION));

			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, "Stage::methodName():: post CREATEVID service call started with request data : "
							+ JsonUtil.objectMapperObjectToJson(vidRequestDto));

			response = (ResponseWrapper<VidResponseDTO>) restClientService.postApi(ApiName.CREATEVID, "", "", request,
					ResponseWrapper.class);

			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, "Stage::methodName():: post CREATEVID service call ended successfully");

			if (!response.getErrors().isEmpty()) {
				throw new VidCreationException(PlatformErrorMessages.RPR_PGS_VID_EXCEPTION.getMessage(),
						"VID creation exception");

			} else {
				vid = response.getResponse().getVid().toString();
			}

		} else if (cardType == "uin" && uin == null) {
			List<String> pathSegments = new ArrayList<>();
			pathSegments.add(vid);

			ResponseWrapper<VidResponseDTO> response = new ResponseWrapper<>();
			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
					registrationId, "Stage::methodname():: RETRIEVEIUINBYVID GET service call Started");

			response = (ResponseWrapper<VidResponseDTO>) restClientService.getApi(ApiName.GETUINBYVID, pathSegments, "",
					"", ResponseWrapper.class);
			regProcLogger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.UIN.toString(), "",
					"Stage::methodname():: RETRIEVEIUINBYVID GET service call ended successfully");

			if (!response.getErrors().isEmpty()) {
				throw new VidCreationException(PlatformErrorMessages.RPR_PGS_VID_EXCEPTION.getMessage(),
						"VID creation exception");

			} else {
				uin = response.getResponse().getUin().toString();
			}

		}

	}
}
