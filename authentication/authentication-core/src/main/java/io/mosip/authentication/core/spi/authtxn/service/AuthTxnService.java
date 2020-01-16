package io.mosip.authentication.core.spi.authtxn.service;

import java.util.List;

import org.springframework.stereotype.Service;

import io.mosip.authentication.core.autntxn.dto.AutnTxnDto;
import io.mosip.authentication.core.autntxn.dto.AutnTxnRequestDto;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;

@Service
public interface AuthTxnService {

	public List<AutnTxnDto> fetchAuthTxnDetails(AutnTxnRequestDto authtxnrequestdto)
			throws IdAuthenticationBusinessException;

}
