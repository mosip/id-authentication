package org.mosip.registration.dao;

import java.util.List;

import org.mosip.registration.entity.Registration;
import org.mosip.registration.exception.RegBaseCheckedException;
import org.mosip.registration.entity.RegistrationTransaction;

/**
 * The DAO interface for {@link RegistrationTransaction}
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
public interface RegTransactionDAO {

	/**
	 * This method saves the {@link RegistrationTransaction}
	 * 
	 * @param id
	 *            the id of the {@link Registration} entity for which
	 *            {@link RegistrationTransaction} entity had be created
	 * @return the created {@link RegistrationTransaction} entity
	 * @throws RegBaseCheckedException
	 */
	RegistrationTransaction save(String id) throws RegBaseCheckedException;

	/**
	 * This method updates the {@link RegistrationTransaction} entity
	 * 
	 * @param id
	 *            the id of the {@link RegistrationTransaction} entity to be updated
	 * @return the status of the update
	 */
	int update(String id);

	/**
	 * This method inserts the {@link RegistrationTransaction} entity
	 * 
	 * @param object
	 *            the object to be inserted
	 * @return the status of the insertion
	 */
	boolean upload(Object object);

	/**
	 * This method gets the list of object
	 * 
	 * @param id
	 *            the id of the entity
	 * @return the list of object
	 */
	List<String> view(String id);
	
	/**
	 * This method is used to insert the packet details to the transaction table.
	 * 
	 * @param registrationTransactions
	 * @return
	 */
	List<RegistrationTransaction> insertPacketTransDetails(List<RegistrationTransaction> registrationTransactions);
}
