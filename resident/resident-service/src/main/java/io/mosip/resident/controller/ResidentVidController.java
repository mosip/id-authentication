package io.mosip.resident.controller;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.resident.config.LoggerConfiguration;
import io.mosip.resident.dto.ResidentVidRequestDto;
import io.mosip.resident.dto.VidResponseDto;
import io.mosip.resident.service.ResidentVidService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    //@PreAuthorize("hasAnyRole('INDIVIDUAL')")
    @PostMapping(path = "/vid", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(value = "Get the registration entity", response = ResidentVidRequestDto.class)
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Registration Entity successfully fetched"),
            @ApiResponse(code = 400, message = "Unable to fetch the Registration Entity") })
    public ResponseEntity<Object> generateVid(@RequestBody(required = true) ResidentVidRequestDto requestDto) {
        VidResponseDto vidResponseDto = residentVidService.generateVid(requestDto.getRequest());
        return ResponseEntity.ok().body(vidResponseDto);
    }
}
