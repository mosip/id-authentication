package io.mosip.authentication.internal.service.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.authentication.common.service.helper.WebSubSubscriptionHelper;
import io.mosip.authentication.core.exception.IdAuthenticationAppException;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
public class WebSubController {
	
	@Autowired
	private WebSubSubscriptionHelper webSubSubscriptionHelper;

	@PreAuthorize("hasAnyRole('ID_AUTHENTICATION')")
	@ApiOperation(value = "Websub subscription initialization", response = IdAuthenticationAppException.class)
	@PostMapping(path = "/initSubscriptions")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Request processed successfully"),
			@ApiResponse(code = 400, message = "Error processing request") })
	public ResponseEntity<?> initSubscriptions() {
		webSubSubscriptionHelper.initInternalAuthSubsriptions();
		return ResponseEntity.ok().build();
	}

}
