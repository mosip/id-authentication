package io.mosip.authentication.common.service.repository;

import io.mosip.authentication.common.service.entity.OtpTransaction;
import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;

public interface OtpTxnRepository extends BaseRepository<OtpTransaction, String> {
	
	Boolean existsByOtpHash(String otpHash);
	
	OtpTransaction findByOtpHash(String otpHash);
}
