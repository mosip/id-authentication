package io.mosip.admin.masterdata.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.mosip.admin.masterdata.constant.MasterDataCardConstant;
import io.mosip.admin.masterdata.dto.MasterDataCardResponseDto;
import io.mosip.admin.masterdata.service.MasterDataCardService;
import io.mosip.kernel.core.http.ResponseWrapper;

@RestController
@RequestMapping("/mastercards")
public class MasterDataCardController {

	@Autowired
	private MasterDataCardService masterDataCardService;

	@GetMapping("/{langCode}")
	//@PreAuthorize("hasRole('ZONAL_ADMIN')")
	public ResponseWrapper<MasterDataCardResponseDto> getMasterCards(@PathVariable("langCode") String langCode) {
		ResponseWrapper<MasterDataCardResponseDto> responseWrapper = new ResponseWrapper<>();
		MasterDataCardResponseDto masterDataCards = masterDataCardService.getMasterdataCards(langCode);
		responseWrapper.setResponse(masterDataCards);
		responseWrapper.setId(MasterDataCardConstant.MASTERDATA_CARD_ID);
		responseWrapper.setMetadata(MasterDataCardConstant.MASTERDATA_CARD_METADATA);
		responseWrapper.setVersion(MasterDataCardConstant.MASTERDATA_CARD_VERSION);
		return responseWrapper;
	}

}
