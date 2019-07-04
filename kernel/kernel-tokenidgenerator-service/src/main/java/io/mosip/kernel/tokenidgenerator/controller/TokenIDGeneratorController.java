package io.mosip.kernel.tokenidgenerator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.http.ResponseFilter;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.tokenidgenerator.dto.TokenIDResponseDto;
import io.mosip.kernel.tokenidgenerator.service.TokenIDGeneratorService;
import io.swagger.annotations.ApiParam;

@RestController
public class TokenIDGeneratorController {

	@Autowired
	private TokenIDGeneratorService tokenIDGeneratorService;

	
	@ResponseFilter
	@GetMapping(value = "/{uin}/{partnercode}")
	@PreAuthorize("hasRole('ID_AUTHENTICATION')")
	public ResponseWrapper<TokenIDResponseDto> generateTokenID(@ApiParam("uin of user") @PathVariable("uin") String uin,
			@ApiParam("Partner Code") @PathVariable("partnercode") String partnerCode) {
		ResponseWrapper<TokenIDResponseDto> response = new ResponseWrapper<>();
		response.setResponse(tokenIDGeneratorService.generateTokenID(uin.trim(), partnerCode.trim()));
		return response;
	}

}
