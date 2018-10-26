package io.mosip.registration.processor.core.spi.packetmanager;

import java.util.List;

import org.springframework.stereotype.Service;


/**
 * QualityCheckManger class for assigning  packets to QCUsers  for quality checking
 * @author Jyoti Prakash Nayak M1030448
 *
 * @param <U>
 */
@Service
public interface QualityCheckManager<U,T,Q>{
	
	
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
	public void updateQCUserStatus(List<Q> qcUserDtos);

	/**
	 * Gets the packetsfor QC user.
	 *
	 * @param qcuserId the qcuser id
	 * @return the packetsfor QC user
	 */
	public List<T> getPacketsforQCUser(U qcuserId);
	

}
