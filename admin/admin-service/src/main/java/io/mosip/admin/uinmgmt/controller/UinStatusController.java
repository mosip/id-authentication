package io.mosip.admin.uinmgmt.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.admin.uinmgmt.dto.UinDetailResponseDto;
import io.mosip.admin.uinmgmt.dto.UinResponseWrapperDto;
import io.mosip.admin.uinmgmt.service.impl.UinStatusServiceImpl;
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
@RequestMapping("/uinmgmt")
@CrossOrigin
@Api(value = "Operation related to Uin Active/Deactive", tags = { "UIN_Status" })
public class UinStatusController {

	/*
	 * @Autowired private RestTemplate restTemplate;
	 */
	/**
	 * Reference to UinServiceImpl.
	 */
	@Autowired
	private UinStatusServiceImpl uinService;

	@Value("${uin.status.search}")
	String uinStatusUrl;

	/**
	 * Function to get status of the given UIN
	 * 
	 * @param uin
	 *            pass uin as String
	 * 
	 * @return UinResponseWrapperDto return UIN status for the given uin
	 * 
	 */
	@GetMapping(value = "/status/{uin}")
	public UinResponseWrapperDto getUinStatus(@PathVariable("uin") String uin) {

		return uinService.getUinStatus(uin);

	}

	/*
	 * @SuppressWarnings("unchecked")
	 * 
	 * @GetMapping(value = "/status/{uin}") public ResponseWrapper<UinResponseDto>
	 * getUinStatus(@PathVariable("uin") String uin) {
	 * restTemplate.getForObject(uinStatusUrl, ResponseWrapper.class,
	 * uin).getResponse();
	 * 
	 * return null; }
	 */

	/**
	 * Function to fetch details of the given UIN
	 * 
	 * @param uin
	 *            pass uin as String
	 * 
	 * @return UinResponseWrapperDto return UIN status for the given uin
	 * 
	 */
	@GetMapping(value = "/details/{uin}/{langCode}")
	public ResponseWrapper<UinDetailResponseDto> getUinDetails(@PathVariable("uin") String uin,
			@PathVariable("langCode") String langCode) {
		UinDetailResponseDto response = uinService.getUinDetails(uin, langCode);
		ResponseWrapper<UinDetailResponseDto> responseWrapper=new ResponseWrapper<>();
		responseWrapper.setResponse(response);
		return responseWrapper;

	}

}
