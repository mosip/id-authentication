package io.mosip.authentication.core.spi.hotlist.service;

import java.time.LocalDateTime;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;

public interface HotlistService {

	void block(String id, String idType, String status, LocalDateTime expiryTimestamp) throws IdAuthenticationBusinessException;
	
	void unblock(String id, String idType) throws IdAuthenticationBusinessException;

}
