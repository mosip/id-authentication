package io.mosip.kernel.masterdata.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.core.http.ResponseFilter;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.masterdata.dto.MachineDto;
import io.mosip.kernel.masterdata.dto.MachineRegistrationCenterDto;
import io.mosip.kernel.masterdata.dto.PageDto;
import io.mosip.kernel.masterdata.dto.getresponse.MachineResponseDto;
import io.mosip.kernel.masterdata.dto.getresponse.extn.MachineExtnDto;
import io.mosip.kernel.masterdata.dto.postresponse.IdResponseDto;
import io.mosip.kernel.masterdata.dto.request.FilterValueDto;
import io.mosip.kernel.masterdata.dto.request.SearchDto;
import io.mosip.kernel.masterdata.dto.response.FilterResponseDto;
import io.mosip.kernel.masterdata.dto.response.PageResponseDto;
import io.mosip.kernel.masterdata.entity.id.IdAndLanguageCodeID;
import io.mosip.kernel.masterdata.service.MachineService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * 
 * This class provide services to do CRUD operations on Machine.
 * 
 * @author Megha Tanga
 * @author Ritesh Sinha
 * @since 1.0.0
 *
 */

@RestController
@Api(tags = { "Machine" })
public class MachineController {

	/**
	 * Reference to MachineService.
	 */
	@Autowired
	private MachineService machineService;

	/**
	 * 
	 * Function to fetch machine detail based on given Machine ID and Language code.
	 * 
	 * @param machineId
	 *            pass Machine ID as String
	 * @param langCode
	 *            pass language code as String
	 * @return MachineResponseDto machine detail based on given Machine ID and
	 *         Language code {@link MachineResponseDto}
	 */
	@ResponseFilter
	@GetMapping(value = "/machines/{id}/{langcode}")
	@ApiOperation(value = "Retrieve all Machine Details for given Languge Code", notes = "Retrieve all Machine Detail for given Languge Code and ID")
	@ApiResponses({
			@ApiResponse(code = 200, message = "When Machine Details retrieved from database for the given Languge Code and ID"),
			@ApiResponse(code = 404, message = "When No Machine Details found for the given Languge Code and ID"),
			@ApiResponse(code = 500, message = "While retrieving Machine Details any error occured") })
	public ResponseWrapper<MachineResponseDto> getMachineIdLangcode(@PathVariable("id") String machineId,
			@PathVariable("langcode") String langCode) {

		ResponseWrapper<MachineResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(machineService.getMachine(machineId, langCode));
		return responseWrapper;
	}

	/**
	 * 
	 * Function to fetch machine detail based on given Language code
	 * 
	 * @param langCode
	 *            pass language code as String
	 * 
	 * @return MachineResponseDto machine detail based on given Language code
	 *         {@link MachineResponseDto}
	 */
	@ResponseFilter
	@GetMapping(value = "/machines/{langcode}")
	@ApiOperation(value = "Retrieve all Machine Details for given Languge Code", notes = "Retrieve all Machine Detail for given Languge Code")
	@ApiResponses({
			@ApiResponse(code = 200, message = "When Machine Details retrieved from database for the given Languge Code"),
			@ApiResponse(code = 404, message = "When No Machine Details found for the given Languge Code"),
			@ApiResponse(code = 500, message = "While retrieving Machine Details any error occured") })
	public ResponseWrapper<MachineResponseDto> getMachineLangcode(@PathVariable("langcode") String langCode) {
		ResponseWrapper<MachineResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(machineService.getMachine(langCode));
		return responseWrapper;
	}

	/**
	 * Function to fetch a all machines details
	 * 
	 * @return MachineResponseDto all machines details {@link MachineResponseDto}
	 */
	// @PreAuthorize("hasAnyRole('ZONAL_ADMIN','ZONAL_APPROVER')")
	@ResponseFilter
	@GetMapping(value = "/machines")
	@ApiOperation(value = "Retrieve all Machine Details", notes = "Retrieve all Machine Detail")
	@ApiResponses({ @ApiResponse(code = 200, message = "When all Machine retrieved from database"),
			@ApiResponse(code = 404, message = "When No Machine found"),
			@ApiResponse(code = 500, message = "While retrieving Machine any error occured") })
	public ResponseWrapper<MachineResponseDto> getMachineAll() {
		ResponseWrapper<MachineResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(machineService.getMachineAll());
		return responseWrapper;
	}

	/**
	 * Post API to deleted a row of Machine data
	 * 
	 * @param id
	 *            input from user Machine id
	 * 
	 * @return ResponseEntity Machine Id which is deleted successfully
	 *         {@link ResponseEntity}
	 */
	@ResponseFilter
	@DeleteMapping("/machines/{id}")
	@ApiOperation(value = "Service to delete Machine ", notes = "Delete Machine  and return Machine  Id ")
	@ApiResponses({ @ApiResponse(code = 200, message = "When Machine successfully deleted"),
			@ApiResponse(code = 400, message = "When Request body passed  is null or invalid"),
			@ApiResponse(code = 404, message = "When No Machine found"),
			@ApiResponse(code = 500, message = "While deleting Machine any error occured") })
	public ResponseWrapper<IdResponseDto> deleteMachine(@Valid @PathVariable("id") String id) {

		ResponseWrapper<IdResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(machineService.deleteMachine(id));
		return responseWrapper;
	}

	/**
	 * Post API to insert a new row of Machine data
	 * 
	 * @param machine
	 *            input from user Machine DTO
	 * 
	 * @return ResponseEntity Machine Id which is inserted successfully
	 *         {@link ResponseEntity}
	 */
	@ResponseFilter
	// @PreAuthorize("hasAnyRole('ZONAL_ADMIN')")
	@PostMapping("/machines")
	@ApiOperation(value = "Service to save Machine", notes = "Saves Machine Detail and return Machine id")
	@ApiResponses({ @ApiResponse(code = 201, message = "When Machine successfully created"),
			@ApiResponse(code = 400, message = "When Request body passed  is null or invalid"),
			@ApiResponse(code = 404, message = "When No Machine found"),
			@ApiResponse(code = 500, message = "While creating Machine any error occured") })
	public ResponseWrapper<IdAndLanguageCodeID> createMachine(@Valid @RequestBody RequestWrapper<MachineDto> machine) {
		ResponseWrapper<IdAndLanguageCodeID> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(machineService.createMachine(machine.getRequest()));
		return responseWrapper;
	}

	/**
	 * Post API to update a row of Machine data
	 * 
	 * @param machine
	 *            input from user Machine DTO
	 * 
	 * @return ResponseEntity Machine Id which is update successfully
	 *         {@link ResponseEntity}
	 */
	@ResponseFilter
	// @PreAuthorize("hasAnyRole('ZONAL_ADMIN')")
	@PutMapping("/machines")
	@ApiOperation(value = "Service to update Machine", notes = "update Machine Detail and return Machine id")
	@ApiResponses({ @ApiResponse(code = 200, message = "When Machine successfully udated"),
			@ApiResponse(code = 400, message = "When Request body passed  is null or invalid"),
			@ApiResponse(code = 404, message = "When No Machine found"),
			@ApiResponse(code = 500, message = "While updating Machine any error occured") })
	public ResponseWrapper<IdAndLanguageCodeID> updateMachine(@Valid @RequestBody RequestWrapper<MachineDto> machine) {

		ResponseWrapper<IdAndLanguageCodeID> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(machineService.updateMachine(machine.getRequest()));
		return responseWrapper;
	}

	/**
	 * 
	 * Function to fetch machine detail those are mapped with given registration Id
	 * 
	 * @param regCenterId
	 *            pass registration Id as String
	 * 
	 * @return MachineResponseDto all machines details those are mapped with given
	 *         registration Id {@link MachineResponseDto}
	 */
	// @PreAuthorize("hasAnyRole('ZONAL_ADMIN')")
	@ResponseFilter
	@GetMapping(value = "/machines/mappedmachines/{regCenterId}")
	@ApiOperation(value = "Retrieve all Machines which are mapped to given Registration Center Id", notes = "Retrieve all Machines which are mapped to given Registration Center Id")
	@ApiResponses({
			@ApiResponse(code = 200, message = "When Machine Details retrieved from database for the given Registration Center Id"),
			@ApiResponse(code = 404, message = "When No Machine Details not mapped with the Given Registation Center ID"),
			@ApiResponse(code = 500, message = "While retrieving Machine Detail any error occured") })
	public ResponseWrapper<PageDto<MachineRegistrationCenterDto>> getMachinesByRegistrationCenter(
			@PathVariable("regCenterId") String regCenterId,
			@RequestParam(value = "pageNumber", defaultValue = "0") @ApiParam(value = "page number for the requested data", defaultValue = "0") int page,
			@RequestParam(value = "pageSize", defaultValue = "1") @ApiParam(value = "page size for the request data", defaultValue = "1") int size,
			@RequestParam(value = "orderBy", defaultValue = "cr_dtimes") @ApiParam(value = "sort the requested data based on param value", defaultValue = "createdDateTime") String orderBy,
			@RequestParam(value = "direction", defaultValue = "DESC") @ApiParam(value = "order the requested data based on param", defaultValue = "DESC") String direction) {

		ResponseWrapper<PageDto<MachineRegistrationCenterDto>> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(
				machineService.getMachinesByRegistrationCenter(regCenterId, page, size, orderBy, direction));
		return responseWrapper;

	}

	/**
	 * Api to search Machine based on filters provided.
	 * 
	 * @param request
	 *            the request DTO.
	 * @return the pages of {@link MachineExtnDto}.
	 */
	@ResponseFilter
	@PostMapping("/machines/search")
	public ResponseWrapper<PageResponseDto<MachineExtnDto>> searchMachine(
			@RequestBody @Valid RequestWrapper<SearchDto> request) {
		ResponseWrapper<PageResponseDto<MachineExtnDto>> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(machineService.searchMachine(request.getRequest()));
		return responseWrapper;
	}

	/**
	 * Api to filter Machine based on column and type provided.
	 * 
	 * @param request
	 *            the request DTO.
	 * @return the {@link FilterResponseDto}.
	 */
	@ResponseFilter
	@PostMapping("/machines/filtervalues")
	public ResponseWrapper<FilterResponseDto> machineFilterValues(
			@RequestBody @Valid RequestWrapper<FilterValueDto> request) {
		ResponseWrapper<FilterResponseDto> responseWrapper = new ResponseWrapper<>();
		responseWrapper.setResponse(machineService.machineFilterValues(request.getRequest()));
		return responseWrapper;
	}
}
