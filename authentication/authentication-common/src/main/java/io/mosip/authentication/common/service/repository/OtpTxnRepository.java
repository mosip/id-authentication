package io.mosip.authentication.common.service.repository;

import java.util.List;
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
	 * Find first element by ref_id ordered by generated_dtimes in descending order and for the given status codes.
	 *
	 * @param refIdHash the ref id hash
	 * @return the optional
	 */
	Optional<OtpTransaction> findFirstByRefIdAndStatusCodeInAndGeneratedDtimesNotNullOrderByGeneratedDtimesDesc(String refIdHash, List<String> statusCodes);
	
}
