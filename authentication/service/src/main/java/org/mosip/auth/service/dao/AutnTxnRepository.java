package org.mosip.auth.service.dao;

import org.mosip.auth.service.entity.AutnTxn;
import org.mosip.kernel.core.dao.repository.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * This class module is to store OTP requested attributes in Table "autn_txn".
 * 
 * @author Rakesh Roshan
 */
@Repository
public interface AutnTxnRepository extends BaseRepository<AutnTxn, Integer> {

	/**
	 * Obtain all Authentication Transaction for particular TxnId and UIN.
	 * 
	 * @param TxnId
	 * @param UIN
	 * @return
	 */
	public AutnTxn findByRequestTxnIdAndUin(String TxnId, String UIN);

	/**
	 * Obtain the List of all request_dTtimes for particular UIN(uniqueId)
	 * 
	 * @param UIN
	 * @return
	 */
	/*@Query("Select txn.requestDTtimes from ida.autn_txn txn where txn.uin=:UIN")
	public List<AutnTxn> findAllRequestDTtimesByUIN(@Param("UIN") String UIN);*/

	/**
	 * Obtain the number of count of request_dTtimes for particular UIN(uniqueId)
	 * with within the otpRequestDTime and oneMinuteBeforeTime.
	 * 
	 * @param otpRequestDTime
	 * @param oneMinuteBeforeTime
	 * @param UIN
	 * @return
	 */
	// @Query("Select count(txn.requestDTtimes) from ida.autn_txn txn where
	// txn.responseDTimes>=:responseDTimes and txn.responseDTimes<=:nowTime and
	// txn.uin=:UIN")
	@Query("Select count(txn.request_dtimes) from ida.autn_txn txn where txn.request_dtimes <= :otpRequestDTime and txn.request_dtimes >= :oneMinuteBeforeTime and txn.uin=:UIN")
	public int countRequestDTime(@Param("otpRequestDTime") String otpRequestDTime,
			@Param("oneMinuteBeforeTime") String oneMinuteBeforeTime, @Param("UIN") String UIN);

}
