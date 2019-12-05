package io.mosip.resident.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mosip.kernel.core.exception.ServiceError;
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
import io.mosip.resident.exception.VidAlreadyPresentException;
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

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ResidentVidServiceImpl implements ResidentVidService {

    private static final Logger logger = LoggerConfiguration.logConfig(ResidentVidServiceImpl.class);

    private static final String VID_ALREADY_EXISTS_ERROR_CODE = "IDR-VID-003";

    @Value("${resident.vid.id}")
    private String id;

    @Value("${resident.vid.version}")
    private String version;

    @Value("${vid.create.id}")
    private String vidCreateId;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private Environment env;

    @Autowired
    private ResidentServiceRestClient residentServiceRestClient;

    @Autowired
    private TokenGenerator tokenGenerator;

    @Autowired
    private IdAuthService idAuthService;

    @Override
    public ResponseWrapper<VidResponseDto> generateVid(VidRequestDto requestDto) {

        ResponseWrapper<VidResponseDto> responseDto = new ResponseWrapper<>();

        boolean isAuthenticated = idAuthService.validateOtp(requestDto.getTransactionID(),
                requestDto.getIndividualId(), requestDto.getIndividualIdType(), requestDto.getOtp());

        if (!isAuthenticated)
            throw new OtpValidationFailedException();

        try {
            VidGeneratorResponseDto vidResponse = vidGenerator(requestDto);
            VidResponseDto vidResponseDto = new VidResponseDto();
            vidResponseDto.setVid(vidResponse.getVID());
            vidResponseDto.setMessage("Notification has been sent to the provided contact detail(s)");
            responseDto.setResponse(vidResponseDto);
        } catch (JsonProcessingException e) {
            throw new VidCreationException(e.getErrorText());
        } catch (IOException e) {
            throw new VidCreationException(e.getMessage());
        }

        responseDto.setId(id);
        responseDto.setVersion(version);
        responseDto.setResponsetime(DateUtils.getUTCCurrentDateTimeString());

        return responseDto;

    }

    private VidGeneratorResponseDto vidGenerator(VidRequestDto requestDto) throws JsonProcessingException, IOException {
        VidGeneratorRequestDto vidRequestDto = new VidGeneratorRequestDto();
        RequestWrapper<VidGeneratorRequestDto> request = new RequestWrapper<>();
        ResponseWrapper<VidGeneratorResponseDto> response = null;

        vidRequestDto.setUIN(requestDto.getIndividualId());
        vidRequestDto.setVidType(requestDto.getVidType());
        request.setId(vidCreateId);
        request.setVersion(version);
        request.setRequest(vidRequestDto);
        request.setRequesttime(DateUtils.getUTCCurrentDateTimeString());

        logger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
                requestDto.getIndividualIdType(),
                "ResidentVidServiceImpl::vidGenerator():: post CREATEVID service call started with request data : "
                        + JsonUtils.javaObjectToJsonString(request));

        try {
            response = (ResponseWrapper) residentServiceRestClient
                    .postApi(env.getProperty(ApiName.IDAUTHCREATEVID.name()), MediaType.APPLICATION_JSON, request, ResponseWrapper.class, tokenGenerator.getRegprocToken());
        } catch (Exception e) {
            logger.error(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
                    requestDto.getIndividualIdType(), ResidentErrorCode.API_RESOURCE_UNAVAILABLE.getErrorCode() + e.getMessage()
                            + ExceptionUtils.getStackTrace(e));
            throw new ApisResourceAccessException("Unable to create vid");
        }

        logger.debug(LoggerFileConstant.SESSIONID.toString(), LoggerFileConstant.REGISTRATIONID.toString(),
                requestDto.getIndividualIdType(),
                "ResidentVidServiceImpl::vidGenerator():: create Vid response :: "+ JsonUtils.javaObjectToJsonString(response));

        if (response.getErrors() != null && !response.getErrors().isEmpty()) {
            List<ServiceError> list =  response.getErrors().stream().filter(err -> err.getErrorCode().equalsIgnoreCase(VID_ALREADY_EXISTS_ERROR_CODE)).collect(Collectors.toList());
            throw (list.size() == 1) ?
                new VidAlreadyPresentException(ResidentErrorCode.VID_ALREADY_PRESENT.getErrorCode(),
                        ResidentErrorCode.VID_ALREADY_PRESENT.getErrorMessage())
            :
            new VidCreationException(response.getErrors().get(0).getMessage());

        }

        VidGeneratorResponseDto vidResponse = mapper.readValue(mapper.writeValueAsString(response.getResponse()),
                VidGeneratorResponseDto.class);

        return vidResponse;
    }
}
