package io.mosip.authentication.core.spi.hotlist.service;

import java.time.LocalDateTime;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.hotlist.dto.HotlistDTO;

public interface HotlistService {

	void block(String id, String idType, String status, LocalDateTime expiryTimestamp) throws IdAuthenticationBusinessException;
	
	void unblock(String id, String idType) throws IdAuthenticationBusinessException;

	HotlistDTO getHotlistStatus(String id, String idType) throws IdAuthenticationBusinessException;
}
