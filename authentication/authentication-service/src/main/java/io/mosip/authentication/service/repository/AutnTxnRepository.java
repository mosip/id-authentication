package io.mosip.authentication.service.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.mosip.authentication.service.entity.AutnTxn;
import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;

/**
 * This is a repository class for entity {@link AutnTxn}.
 * 
 * @author Rakesh Roshan
 */
@Repository
public interface AutnTxnRepository extends BaseRepository<AutnTxn, Integer> {

	/**
	 * Obtain all Authentication Transaction for particular TxnId and UIN.
	 *
	 * @param txnId the txn id
	 * @param refId the ref id
	 * @return the list
	 */
	public List<AutnTxn> findAllByRequestTrnIdAndRefId(String txnId, String refId);

	/**
	 * Obtain the number of count of request_dTtimes for particular UIN(uniqueId)
	 * with within the otpRequestDTime and oneMinuteBeforeTime.
	 *
	 * @param otpRequestDTime the otp request D time
	 * @param oneMinuteBeforeTime the one minute before time
	 * @param refId the ref id
	 * @return the int
	 */
	@Query("Select count(requestDTtimes) from AutnTxn  where requestDTtimes <= :otpRequestDTime and "
			+ "request_dtimes >= :oneMinuteBeforeTime and refId=:refId")
	public int countRequestDTime(@Param("otpRequestDTime") Date otpRequestDTime,
			@Param("oneMinuteBeforeTime") Date oneMinuteBeforeTime, @Param("refId") String refId);

}
