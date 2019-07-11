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
	 * @param pagaeable the pagaeable
	 * @param authtypecode the authtypecode
	 * @return the list
	 */
	@Query(value="Select * from ida.auth_transaction where request_trn_id=:txnId AND auth_type_code=:authtypecode ORDER BY cr_dtimes DESC", nativeQuery = true)
	public List<AutnTxn> findByUinorVid(@Param("txnId") String txnId,
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
			@Param("oneMinuteBeforeTime") LocalDateTime oneMinuteBeforeTime, @Param("refId") String refId);

}
