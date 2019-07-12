package io.mosip.admin.uinmgmt.service;

import java.util.List;

import io.mosip.admin.uinmgmt.dto.UinGenerationStatusDto;
import io.mosip.kernel.core.http.ResponseWrapper;

/**
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
public interface UinGenerationStatusService {

	/**
	 * Service class to fetch packet status based on rid
	 * 
	 * @param rid
	 *            input from user
	 * @return DTO containing packet status of entered RID
	 * 
	 */
	public ResponseWrapper<List<UinGenerationStatusDto>> getPacketStatus(String rid);

}
