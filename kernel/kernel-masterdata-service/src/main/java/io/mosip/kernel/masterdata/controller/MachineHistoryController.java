package io.mosip.kernel.masterdata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.masterdata.dto.getresponse.MachineHistoryResponseDto;
import io.mosip.kernel.masterdata.service.MachineHistoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * Controller with api to get Machine History Details
 * 
 * @author Megha Tanga
 * @since 1.0.0
 *
 */

@RestController
@Api(tags = { "MachineHistory" })
@RequestMapping(value = "/v1.0/machineshistories")
public class MachineHistoryController {

	/**
	 * Reference to MachineHistroyService.
	 */
	@Autowired
	private MachineHistoryService macHistoryService;

	/**
	 * Get api to fetch a machine history details based on given Machine ID,
	 * Language code and effective date time
	 * 
	 * @param id
	 *            input machine Id from User
	 * @param langCode
	 *            input Language Code from user
	 * @param dateAndTime
	 *            input effective date and time from user
	 * 
	 * @return MachineHistoryResponseDto
	 * 			returning machine history detail based on given Machine ID, Language
	 *         code and effective date time 
	 */
	@GetMapping(value = "/{id}/{langcode}/{effdatetimes}")
	@ApiOperation(value = "Retrieve all Machine History Details for the given Languge Code, ID and Effective date time", notes = "Retrieve all Machine Detail for given Languge Code and ID", response = MachineHistoryResponseDto.class)
	@ApiResponses({
			@ApiResponse(code = 200, message = "When Machine History Details retrieved from database for the given Languge Code, ID and Effective date time", response = MachineHistoryResponseDto.class),
			@ApiResponse(code = 404, message = "When No Machine History Details found for the given Languge Code, ID and Effective date time"),
			@ApiResponse(code = 500, message = "While retrieving Machine History Details any error occured") })
	public MachineHistoryResponseDto getMachineHistoryIdLangEff(@PathVariable("id") String id,
			@PathVariable("langcode") String langCode, @PathVariable("effdatetimes") String dateAndTime) {

		return macHistoryService.getMachineHistroyIdLangEffDTime(id, langCode, dateAndTime);
	}
}