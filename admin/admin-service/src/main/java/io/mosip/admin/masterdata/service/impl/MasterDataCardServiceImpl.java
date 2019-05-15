package io.mosip.admin.masterdata.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mosip.admin.masterdata.constant.MasterDataErrorConstant;
import io.mosip.admin.masterdata.dto.MasterDataCardDto;
import io.mosip.admin.masterdata.dto.MasterDataCardResponseDto;
import io.mosip.admin.masterdata.exception.MasterDataCardException;
import io.mosip.admin.masterdata.service.MasterDataCardService;
import io.mosip.admin.masterdata.utils.MasterDataCardUtil;

/**
 * Masterdata card service implementation
 * 
 * @author Abhishek Kumar
 * @since 1.0.0
 *
 */
@Service
public class MasterDataCardServiceImpl implements MasterDataCardService {
	@Autowired
	private MasterDataCardUtil masterDataCardUtil;

	@Override
	public MasterDataCardResponseDto getMasterdataCards(String langCode) {
		MasterDataCardResponseDto responseDto = null;
		List<MasterDataCardDto> cards = new ArrayList<>();
		Map<String, String> map = masterDataCardUtil.getMasterDataCards(langCode);
		if (map != null && !map.isEmpty()) {
			Set<String> keySet = map.keySet();
			for (String key : keySet) {
				cards.add(new MasterDataCardDto(key, map.get(key)));
			}
		} else {
			throw new MasterDataCardException(MasterDataErrorConstant.DATANOTFOUND.errorCode(),MasterDataErrorConstant.DATANOTFOUND.errorMessage());
		}

		responseDto = new MasterDataCardResponseDto();
		responseDto.setMasterdata(cards);
		return responseDto;
	}

}
