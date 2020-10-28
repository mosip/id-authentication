package io.mosip.authentication.common.service.repository;

import io.mosip.authentication.common.service.entity.OtpTransaction;
import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;

/**
 * @author Manoj SP
 *
 */
public interface OtpTxnRepository extends BaseRepository<OtpTransaction, String> {
	
	Boolean existsByOtpHashAndStatusCode(String otpHash, String statusCode);
	
	OtpTransaction findByOtpHashAndStatusCode(String otpHash, String statusCode);
	
	OtpTransaction findByRefIdAndStatusCode(String refId, String statusCode);
}
