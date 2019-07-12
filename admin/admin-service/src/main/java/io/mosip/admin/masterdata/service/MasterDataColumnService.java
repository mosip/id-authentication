package io.mosip.admin.masterdata.service;

import java.util.List;

import io.mosip.admin.masterdata.dto.MasterDataColumnDto;
import io.mosip.kernel.core.http.ResponseWrapper;

public interface MasterDataColumnService {
	
	ResponseWrapper<MasterDataColumnDto> getMasterDataColumns(String resource);

}
