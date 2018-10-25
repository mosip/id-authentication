package io.mosip.registration.processor.core.spi.packetmanager;

/**
 * QualityCheckManger class for assigning  packets to QCUsers  for quality checking
 * @author Jyoti Prakash Nayak M1030448
 *
 * @param <U>
 */
public interface QualityCheckManger<U>{
	
	
	/**
	 * Method to assign  packets to QCUsers
	 * @param applicantRegistrationId
	 */
	public void assignQCUser(U applicantRegistrationId);

}
