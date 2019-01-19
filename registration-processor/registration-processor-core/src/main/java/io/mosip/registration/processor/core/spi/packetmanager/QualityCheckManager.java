package io.mosip.registration.processor.core.spi.packetmanager;
	
import java.util.List;

import org.springframework.stereotype.Service;

/**
 * QualityCheckManger class for assigning  packets to QCUsers  for quality checking.
 *
 * @author Jyoti Prakash Nayak M1030448
 * @author M1048399
 * @param <U> the generic type
 * @param <Q> the generic type
 */
@Service
public interface QualityCheckManager<U,Q>{
	
	
	/**
	 * Method to assign  packets to QCUsers.
	 *
	 * @param applicantRegistrationId the applicant registration id
	 * @return the q
	 */
	public Q assignQCUser(U applicantRegistrationId);
	
	/**
	 * Send and verify.
	 *
	 * @param qcUserDtos the qc user dtos
	 * @return the list
	 */
	public List<Q> updateQCUserStatus(List<Q> qcUserDtos);


	

}
