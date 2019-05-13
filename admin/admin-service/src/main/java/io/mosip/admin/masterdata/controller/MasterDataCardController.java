package io.mosip.admin.masterdata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.admin.masterdata.dto.MasterDataCardResponseDto;
import io.mosip.admin.masterdata.service.MasterDataCardService;
import io.mosip.kernel.core.http.ResponseWrapper;

@RestController
@RequestMapping("/mastercards")
public class MasterDataCardController {

	@Autowired
	private MasterDataCardService masterDataCardService;

	@GetMapping("/{langCode}")
	public ResponseWrapper<MasterDataCardResponseDto> getMasterCards(@PathVariable("langCode") String langCode) {
		ResponseWrapper<MasterDataCardResponseDto> responseWrapper = new ResponseWrapper<>();
		MasterDataCardResponseDto masterDataCards = masterDataCardService.getMasterdataCards(langCode);
		responseWrapper.setId("mosip.admin.mastercards");
		responseWrapper.setMetadata("masterdata cards");
		responseWrapper.setResponse(masterDataCards);
		responseWrapper.setVersion("1.0");
		return responseWrapper;
	}

}
