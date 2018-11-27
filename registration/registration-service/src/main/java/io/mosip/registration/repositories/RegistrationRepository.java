package io.mosip.registration.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.mosip.kernel.core.dataaccess.spi.repository.BaseRepository;
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
	@Query("select reg from Registration reg where reg.clientStatusCode= :syncStatus and (reg.serverStatusCode=:resendStatus or reg.serverStatusCode IS NULL) or reg.fileUploadStatus=:fileUploadStatus")
	List<Registration> findByStatusCodes(@Param("syncStatus") String clientstatusCode,
			@Param("resendStatus") String serverStatusCode, @Param("fileUploadStatus") String fileUploadStatus);

	/**
	 * This method returns the list of {@link Registration} based on status code
	 * 
	 * @param statusCode
	 *            the status code
	 * @return the list of {@link Registration}
	 */
	List<Registration> findByclientStatusCode(String statusCode);

	/**
	 * This method fetches the registration packets based on given client status
	 * codes.
	 *
	 * @param statusCodes
	 *            the status codes
	 * @return List of registration packets
	 */
	List<Registration> findByClientStatusCodeIn(List<String> statusCodes);

	/**
	 * Fetching all the re registration records
	 * 
	 * @param status
	 * @return
	 */
	List<Registration> findByClientStatusCodeAndServerStatusCode(String clientStatus, String serverStatus);
}
