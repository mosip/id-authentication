package io.mosip.admin.uinmgmt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.admin.uinmgmt.dto.UinDetailResponseDto;
import io.mosip.admin.uinmgmt.service.impl.UinDetailServiceImpl;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.swagger.annotations.Api;

/**
 * 
 * UIN Status controller with api to get status of the given UIN
 * 
 * @author Megha Tanga
 * 
 *
 */
@RestController
@RequestMapping(value = "/uin")
@CrossOrigin
@Api(value = "Operation related to Uin Active/Deactive", tags = { "UIN_Status" })
public class UinDetailController {

	/**
	 * Reference to UinServiceImpl.
	 */
	@Autowired
	private UinDetailServiceImpl uinService;

	/**
	 * Function to get complete detail of the given UIN
	 * 
	 * @param uin
	 *            pass uin as String
	 * 
	 * @return ResponseWrapper return complete detail of the given uin
	 * 
	 */
	//@PreAuthorize("hasAnyRole('ZONAL_ADMIN','CENTRAL_ADMIN','ID_AUTHENTICATION')")
	@GetMapping(value = "/detail/{uin}")
	public ResponseWrapper<UinDetailResponseDto> getUinDetailsNew(@PathVariable("uin") String uin) {
		ResponseWrapper<UinDetailResponseDto> response = new ResponseWrapper<>();
		response.setId("mosip.id.read");
		response.setVersion("v1");
		response.setResponse(uinService.getUinDetails(uin));
		return response;

	}

}
