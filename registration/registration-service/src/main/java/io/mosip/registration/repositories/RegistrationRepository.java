package io.mosip.registration.repositories;

import java.util.List;

import  io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
import io.mosip.registration.entity.Registration;


/**
 * The repository interface for {@link Registration}
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 *
 */
public interface RegistrationRepository extends BaseRepository<Registration, String> {

	/**
	 * This method returns the list of {@link Registration} based on provided id's
	 * 
	 * @param idList
	 *            the list of entity id's
	 * @return the list of {@link Registration}
	 */
	List<Registration> findByClientStatusCodeAndServerStatusCodeOrServerStatusCodeIsNullOrFileUploadStatusOrderByCrDtimeAsc(String clientstatusCode, String serverStatusCode, String fileUploadStatus);

	/**
	 * This method returns the list of {@link Registration} based on status code
	 * 
	 * @param statusCode
	 *            the status code
	 * @return the list of {@link Registration}
	 */
	List<Registration> findByclientStatusCode(String statusCode);

	/**
	 * This method fetches the registration packets based on given client status codes.
	 *
	 * @param statusCodes 
	 * 				the status codes
	 * @return List of registration packets
	 */
	List<Registration> findByClientStatusCodeIn(List<String> statusCodes);
	
	/**
	 * Fetching all the re registration records
	 * @param status
	 * @return
	 */
	List<Registration> findByClientStatusCodeAndServerStatusCode(String clientStatus, String serverStatus);
}
