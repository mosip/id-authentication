package io.mosip.admin.uinmgmt.service;

import java.util.List;

import io.mosip.admin.uinmgmt.dto.UinGenerationStatusDto;
import io.mosip.kernel.core.exception.IOException;
import io.mosip.kernel.core.http.ResponseWrapper;
import io.mosip.kernel.core.util.exception.JsonMappingException;
import io.mosip.kernel.core.util.exception.JsonParseException;

public interface UinGenerationStatusService {
	
	public ResponseWrapper<List<UinGenerationStatusDto>> getPacketStatus(String rid) throws JsonParseException, JsonMappingException, IOException;

}
