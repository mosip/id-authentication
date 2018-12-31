package io.mosip.kernel.masterdata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.getresponse.DeviceHistoryResponseDto;
import io.mosip.kernel.masterdata.service.DeviceHistoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Controller with api to get Device History Details
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */

@RestController
@Api(tags = { "DeviceHistory" })
@RequestMapping(value = "/v1.0/deviceshistories")
public class DeviceHistoryController {

	/**
	 * 
	 * Reference to DeviceHistroyService.
	 */
	@Autowired
	private DeviceHistoryService devHistoryService;

	/**
	 * Get api to fetch a device history details based on given Device ID,
	 * Language code and effective date time
	 * 
	 * @param id
	 *            input device Id from User
	 * @param langCode
	 *            input Language Code from user
	 * @param dateAndTime
	 *            input effective date and time from user
	 * 
	 * @return DeviceHistoryResponseDto returning device history detail based on
	 *         given Device ID, Language code and effective date time
	 */
	@GetMapping(value = "/{id}/{langcode}/{effdatetimes}")
	@ApiOperation(value = "Retrieve all Device History Details for the given Languge Code, ID and Effective date time", notes = "Retrieve all Device Detail for given Languge Code and ID", response = DeviceHistoryResponseDto.class)
	@ApiResponses({
			@ApiResponse(code = 200, message = "When Device History Details retrieved from database for the given Languge Code, ID and Effective date time", response = DeviceHistoryResponseDto.class),
			@ApiResponse(code = 404, message = "When No Device History Details found for the given Languge Code, ID and Effective date time"),
			@ApiResponse(code = 500, message = "While retrieving Device History Details any error occured") })
	public DeviceHistoryResponseDto getDeviceHistoryIdLangEff(@PathVariable("id") String id,
			@PathVariable("langcode") String langCode, @PathVariable("effdatetimes") String dateAndTime) {

		return devHistoryService.getDeviceHistroyIdLangEffDTime(id, langCode, dateAndTime);
	}
}
