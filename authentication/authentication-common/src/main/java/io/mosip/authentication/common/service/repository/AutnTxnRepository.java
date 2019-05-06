package io.mosip.authentication.common.service.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import io.mosip.authentication.common.service.entity.AutnTxn;
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
	 * @param txnId                       the txn id
	 * @param addMinutesInOtpRequestDTime
	 * @param requestTime
	 * @param type 
	 * @param refId                       the ref id
	 * @return the list
	 */
	@Query("Select requestTrnId from AutnTxn where requestTrnId=:txnId AND (refId = :uin OR refId= :vid) AND authTypeCode=:authtypecode")
	public List<String> findByUinorVid(@Param("txnId") String txnId, @Param("uin") String uin, @Param("vid") String vid,
			Pageable pagaeable, @Param("authtypecode")String authtypecode);

	/**
	 * Obtain the number of count of request_dTtimes for particular UIN(uniqueId)
	 * with within the otpRequestDTime and oneMinuteBeforeTime.
	 *
	 * @param otpRequestDTime     the otp request D time
	 * @param oneMinuteBeforeTime the one minute before time
	 * @param refId               the ref id
	 * @return the int
	 */
	@Query("Select count(requestDTtimes) from AutnTxn  where request_dtimes <= :otpRequestDTime and "
			+ "request_dtimes >= :oneMinuteBeforeTime and refId=:refId")
	public int countRequestDTime(@Param("otpRequestDTime") LocalDateTime otpRequestDTime,
			@Param("oneMinuteBeforeTime") LocalDateTime expiryTime, @Param("refId") String refId);

}
