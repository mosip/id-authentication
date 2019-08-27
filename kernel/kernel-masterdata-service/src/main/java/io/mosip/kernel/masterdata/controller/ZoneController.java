package io.mosip.kernel.masterdata.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.masterdata.dto.getresponse.ZoneNameResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.ZoneExtnDto;
import io.mosip.kernel.masterdata.service.ZoneService;
import io.mosip.kernel.masterdata.validator.ValidLangCode;

/**
 * Controller to handle api request for the zones
 * 
 * @author Abhishek Kumar
 * @since 1.0.0
 */
@RestController
@RequestMapping("/zones")
@Validated
public class ZoneController {

	@Autowired
	private ZoneService zoneService;

	/**
	 * api to fetch the logged-in user zone hierarchy
	 * 
	 * @param langCode
	 *            input language code
	 * @return {@link List} of {@link ZoneExtnDto}
	 */
	@PreAuthorize("hasRole('ZONAL_ADMIN')")
	@GetMapping("/hierarchy/{langCode}")
	public ResponseWrapper<List<ZoneExtnDto>> getZoneHierarchy(
			@PathVariable("langCode") @ValidLangCode @Valid String langCode) {
		ResponseWrapper<List<ZoneExtnDto>> response = new ResponseWrapper<>();
		response.setResponse(zoneService.getUserZoneHierarchy(langCode));
		return response;
	}

	/**
	 * api to fetch the logged-in user zone hierarchy leaf zones
	 * 
	 * @param langCode
	 *            input language code
	 * @return {@link List} of {@link ZoneExtnDto}
	 */
	@PreAuthorize("hasRole('ZONAL_ADMIN')")
	@GetMapping("/leafs/{langCode}")
	public ResponseWrapper<List<ZoneExtnDto>> getLeafZones(
			@PathVariable("langCode") @Valid @ValidLangCode String langCode) {
		ResponseWrapper<List<ZoneExtnDto>> response = new ResponseWrapper<>();
		response.setResponse(zoneService.getUserLeafZone(langCode));
		return response;
	}

	@GetMapping("/zonename")
	public ResponseWrapper<ZoneNameResponseDto> getZoneNameBasedOnUserIDAndLangCode(
			@RequestParam("userID") String userID, @RequestParam("langCode") String langCode) {
		ResponseWrapper<ZoneNameResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(zoneService.getZoneNameBasedOnLangCodeAndUserID(userID, langCode));
		return responseWrapper;
	}

}
