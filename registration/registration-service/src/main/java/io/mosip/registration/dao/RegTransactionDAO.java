package io.mosip.registration.dao;

import java.util.List;

import io.mosip.registration.entity.RegistrationTransaction;

/**
 * The DAO interface for {@link RegistrationTransaction}
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
public interface RegTransactionDAO {
	
	/**
	 * This method is used to insert the packet details to the transaction table.
	 * 
	 * @param registrationTransactions
	 * @return
	 */
	List<RegistrationTransaction> insertPacketTransDetails(List<RegistrationTransaction> registrationTransactions);
	
	RegistrationTransaction buildRegTrans(String regId, String statusCode);
}
