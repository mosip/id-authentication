package io.mosip.resident.service.impl;

import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.kernel.core.util.JsonUtils;
import io.mosip.kernel.core.util.exception.JsonProcessingException;
import io.mosip.resident.config.LoggerConfiguration;
import io.mosip.resident.constant.ApiName;
import io.mosip.resident.constant.LoggerFileConstant;
import io.mosip.resident.constant.ResidentErrorCode;
import io.mosip.resident.dto.*;
import io.mosip.resident.exception.ApisResourceAccessException;
import io.mosip.resident.exception.OtpValidationFailedException;
import io.mosip.resident.exception.VidCreationException;
import io.mosip.resident.service.IdAuthService;
import io.mosip.resident.service.ResidentVidService;
import io.mosip.resident.util.ResidentServiceRestClient;
import io.mosip.resident.util.TokenGenerator;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class ResidentVidServiceImpl implements ResidentVidService {

    private static final Logger logger = LoggerConfiguration.logConfig(ResidentVidServiceImpl.class);

    @Value("${vid.create.id}")
    private String id;

    @Value("${vid.create.version}")
    private String version;

    @Autowired
    private Environment env;

    @Autowired
    private ResidentServiceRestClient residentServiceRestClient;

    @Autowired
    private TokenGenerator tokenGenerator;

    @Autowired
    private IdAuthService idAuthService;

    @Override
    public VidResponseDto generateVid(VidRequestDto requestDto) {

        boolean isAuthenticated = idAuthService.validateOtp(requestDto.getTransactionID(),
                requestDto.getIndividualId(), requestDto.getIndividualIdType(), requestDto.getOtp());

        if (!isAuthenticated)
            throw new OtpValidationFailedException();

        try {
            vidGenerator(requestDto);
        } catch (JsonProcessingException e) {
            throw new VidCreationException(e.getErrorCode(), e.getErrorText());
        }

        return null;

    }

    private boolean vidGenerator(VidRequestDto requestDto) throws JsonProcessingException {
        VidGeneratorRequestDto vidRequestDto = new VidGeneratorRequestDto();
        RequestWrapper<VidGeneratorRequestDto> request = new RequestWrapper<>();
        ResponseWrapper<VidGeneratorResponseDto> response = null;

        vidRequestDto.setUIN(requestDto.getIndividualId());
        vidRequestDto.setVidType(requestDto.getVidType());
        request.setId(id);
        request.setVersion(version);
        request.setRequest(vidRequestDto);
        request.setRequesttime(LocalDateTime.now(ZoneId.of("UTC")));

        logger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
                requestDto.getIndividualIdType(),
                "ResidentVidServiceImpl::vidGenerator():: post CREATEVID service call started with request data : "
                        + JsonUtils.javaObjectToJsonString(request));

        try {
            response = (ResponseWrapper<VidGeneratorResponseDto>) residentServiceRestClient
                    .postApi(env.getProperty(ApiName.IDAUTHCREATEVID.name()), MediaType.APPLICATION_JSON, request, ResponseWrapper.class, tokenGenerator.getToken());
        } catch (Exception e) {
            logger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
                    requestDto.getIndividualIdType(), ResidentErrorCode.API_RESOURCE_UNAVAILABLE.getErrorCode() + e.getMessage()
                            + ExceptionUtils.getStackTrace(e));
            throw new ApisResourceAccessException("Unable to create vid");
        }

        logger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
                requestDto.getIndividualIdType(),
                "ResidentVidServiceImpl::vidGenerator():: create Vid response :: "+ JsonUtils.javaObjectToJsonString(response));

        if (!response.getErrors().isEmpty()) {
            throw new VidCreationException(ResidentErrorCode.VID_CREATION_EXCEPTION.getErrorCode(),
                    ResidentErrorCode.VID_CREATION_EXCEPTION.getErrorMessage());

        }

        return response != null && response.getResponse().getVID() != null;
    }
}
