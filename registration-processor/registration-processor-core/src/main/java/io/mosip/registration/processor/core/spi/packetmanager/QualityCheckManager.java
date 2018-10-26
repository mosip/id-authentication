package io.mosip.registration.processor.core.spi.packetmanager;

import java.util.List;


/**
 * QualityCheckManger class for assigning  packets to QCUsers  for quality checking
 * @author Jyoti Prakash Nayak M1030448
 *
 * @param <U>
 */
public interface QualityCheckManager<U,T>{
	
	
	/**
	 * Method to assign  packets to QCUsers
	 * @param applicantRegistrationId
	 */
	public void assignQCUser(U applicantRegistrationId);
	
	/**
	 * Send and verify.
	 *
	 * @param qcUserId the qc user id
	 * @param param the param
	 */
	public void sendAndVerify(U qcUserId,Object... param);

	/**
	 * Gets the packetsfor QC user.
	 *
	 * @param qcuserId the qcuser id
	 * @return the packetsfor QC user
	 */
	public List<T> getPacketsforQCUser(String qcuserId);
	

}
