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
	 * @param txnId        the txn id
	 * @param pagaeable    the pagaeable
	 * @param authtypecode the authtypecode
	 * @return the list
	 */
	@Query(value = "Select new AutnTxn(token, refIdType, entityId) from AutnTxn where requestTrnId=:txnId AND authTypeCode=:authtypecode ORDER BY cr_dtimes DESC")
	public List<AutnTxn> findByTxnId(@Param("txnId") String txnId, Pageable pagaeable,
			@Param("authtypecode") String authtypecode);

	@Query(value = "Select new AutnTxn( requestTrnId, requestDTtimes, authTypeCode, statusCode, statusComment, refId, refIdType, entityName, requestSignature, responseSignature ) from AutnTxn where token=:token ORDER BY crDTimes DESC")
	public List<AutnTxn> findByToken(@Param("token") String token, Pageable pagaeable);

	/**
	 * Obtain the number of count of request_dTtimes for particular UIN(uniqueId)
	 * with within the otpRequestDTime and oneMinuteBeforeTime.
	 *
	 * @param otpRequestDTime     the otp request D time
	 * @param oneMinuteBeforeTime the one minute before time
	 * @param refId               the ref id
	 * @return the int
	 */
	@Query("Select count(1) from AutnTxn  where requestDTtimes <= :otpRequestDTime and "
			+ "requestDTtimes >= :oneMinuteBeforeTime and token=:token")
	public int countRequestDTime(@Param("otpRequestDTime") LocalDateTime otpRequestDTime,
			@Param("oneMinuteBeforeTime") LocalDateTime oneMinuteBeforeTime, @Param("token") String token);
	
	Long countByRefIdAndRequestDTtimesAfter(String refId, LocalDateTime afterRequestTime);
	
	Long countByEntityIdAndRequestDTtimesAfter(String entityId, LocalDateTime afterRequestTime);

}
