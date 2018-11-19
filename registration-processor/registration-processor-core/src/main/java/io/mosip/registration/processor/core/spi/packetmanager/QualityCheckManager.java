package io.mosip.registration.processor.core.spi.packetmanager;

import java.util.List;

import org.springframework.stereotype.Service;


/**
 * QualityCheckManger class for assigning  packets to QCUsers  for quality checking.
 *
 * @author Jyoti Prakash Nayak M1030448
 * @author M1048399
 * @param <U> the generic type
 * @param <T> the generic type
 * @param <Q> the generic type
 */
@Service
public interface QualityCheckManager<U,Q>{
	
	
	/**
	 * Method to assign  packets to QCUsers
	 * @param applicantRegistrationId
	 */
	public Q assignQCUser(U applicantRegistrationId);
	
	/**
	 * Send and verify.
	 *
	 * @param qcUserId the qc user id
	 * @param param the param
	 */
	public List<Q> updateQCUserStatus(List<Q> qcUserDtos);


	

}
