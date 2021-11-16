package io.mosip.authentication.common.service.repository;

import java.util.Optional;

import io.mosip.authentication.common.service.entity.OtpTransaction;
import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;

/**
 * The Interface OtpTxnRepository.
 *
 * @author Manoj SP
 */
public interface OtpTxnRepository extends BaseRepository<OtpTransaction, String> {
	
	/**
	 * Find by otp hash and status code.
	 *
	 * @param otpHash the otp hash
	 * @param statusCode the status code
	 * @return the optional
	 */
	Optional<OtpTransaction> findByOtpHashAndStatusCode(String otpHash, String statusCode);
	
}
