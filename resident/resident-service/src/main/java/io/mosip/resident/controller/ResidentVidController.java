package io.mosip.resident.controller;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.resident.config.LoggerConfiguration;
import io.mosip.resident.dto.RequestWrapper;
import io.mosip.resident.dto.ResidentVidRequestDto;
import io.mosip.resident.dto.ResponseWrapper;
import io.mosip.resident.dto.VidResponseDto;
import io.mosip.resident.dto.VidRevokeRequestDTO;
import io.mosip.resident.dto.VidRevokeResponseDTO;
import io.mosip.resident.exception.OtpValidationFailedException;
import io.mosip.resident.exception.ResidentServiceCheckedException;
import io.mosip.resident.service.ResidentVidService;
import io.mosip.resident.validator.RequestValidator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Resident VID controller class.
 * @Author : Monobikash Das
 */
@RefreshScope
@RestController
@Api(tags = "Resident Service")
public class ResidentVidController {

    Logger logger = LoggerConfiguration.logConfig(ResidentVidController.class);

    @Autowired
    private ResidentVidService residentVidService;

    @Autowired
    private RequestValidator validator;

    @PostMapping(path = "/vid", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Generate new VID", response = ResidentVidRequestDto.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "VID successfully generated"),
            @ApiResponse(code = 400, message = "Unable to generate VID") })
    public ResponseEntity<Object> generateVid(@RequestBody(required = true) ResidentVidRequestDto requestDto) throws OtpValidationFailedException, ResidentServiceCheckedException {
        validator.validateVidCreateRequest(requestDto);
        ResponseWrapper<VidResponseDto> vidResponseDto = residentVidService.generateVid(requestDto.getRequest());
        return ResponseEntity.ok().body(vidResponseDto);
    }
    
    @PatchMapping(path = "/vid/{vid}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Revoke VID", response = ResponseWrapper.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "VID successfully revoked"),
            @ApiResponse(code = 400, message = "Unable to revoke VID") })
    public ResponseEntity<Object> revokeVid(@RequestBody(required = true) RequestWrapper<VidRevokeRequestDTO> requestDto, @PathVariable String vid) throws OtpValidationFailedException, ResidentServiceCheckedException {
        validator.validateVidRevokeRequest(requestDto);
        ResponseWrapper<VidRevokeResponseDTO> vidResponseDto = residentVidService.revokeVid(requestDto.getRequest(),vid);
        return ResponseEntity.ok().body(vidResponseDto);
    }
}
