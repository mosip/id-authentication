package io.mosip.registration.repositories;

import java.sql.Timestamp;
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
	 * This method returns the list of {@link Registration} based on provided id's.
	 *
	 * @param clientstatusCode 
	 * 				the clientstatus code
	 * @param exportstatusCode 
	 * 				the exportstatus code
	 * @param serverStatusCode 
	 * 				the server status code
	 * @param fileUploadStatus 
	 * 				the file upload status
	 * @return the list of {@link Registration}
	 */
	@Query("select reg from Registration reg where reg.clientStatusCode= :syncStatus or reg.clientStatusCode= :exportStatus and (reg.serverStatusCode=:resendStatus or reg.serverStatusCode IS NULL) or reg.fileUploadStatus=:fileUploadStatus")
	List<Registration> findByStatusCodes(@Param("syncStatus") String clientstatusCode, @Param("exportStatus") String exportstatusCode,
			@Param("resendStatus") String serverStatusCode, @Param("fileUploadStatus") String fileUploadStatus);

	/**
	 * This method returns the list of {@link Registration} based on status code
	 * 
	 * @param statusCode
	 *            the status code
	 * @return the list of {@link Registration}
	 */
	List<Registration> findByclientStatusCodeOrderByCrDtime(String statusCode);

	/**
	 * This method fetches the registration packets based on given client status
	 * codes.
	 *
	 * @param statusCodes
	 *            the status codes
	 * @return List of registration packets
	 */
	List<Registration> findByClientStatusCodeInOrderByUpdDtimesDesc(List<String> statusCodes);
	
	/**
	 * To fetch the records for Packet Upload.
	 *
	 * @param statusCodes 
	 * 				the status codes
	 * @param serverStatus 
	 * 				the server status
	 * @return List of registration packets
	 */
	List<Registration> findByClientStatusCodeInOrServerStatusCodeOrderByUpdDtimesDesc(List<String> statusCodes,String serverStatus);

	/**
	 * Fetching all the re registration records.
	 *
	 * @param clientStatus 
	 * 				the client status
	 * @param serverStatus 
	 * 				the server status
	 * @return List of registration packets
	 */
	List<Registration> findByClientStatusCodeAndServerStatusCode(String clientStatus, String serverStatus);
	
	/**
	 * Find by CrDtimes and client status code.
	 *
	 * @param crDtimes 
	 * 				the date upto packets to be deleted
	 * @return list of registrations
	 */
	List<Registration> findByCrDtimeBefore(Timestamp crDtimes);
	
	/**
	 * This method returns the list of {@link Registration} based on status code.
	 *
	 * @param statusCode            
	 * 				the status code
	 * @return the list of {@link Registration}
	 */
	List<Registration> findByclientStatusCodeOrderByCrDtimeAsc(String statusCode);

	/**
	 * Find by client status code and id.
	 *
	 * @param clientStatusCode 
	 * 				the client status code
	 * @param id 
	 * 				the registration id
	 * @return the registration
	 */
	Registration findByClientStatusCodeAndId(String clientStatusCode,String id);
	
	/**
	 * Find by CrDtimes and client status code.
	 *
	 * @param crDtimes 
	 * 				the date upto packets to be deleted
	 * @param clientStatus 
	 * 				status of resgistrationPacket
	 * @return list of registrations
	 */
	List<Registration> findByCrDtimeBeforeAndServerStatusCode(Timestamp crDtimes, String clientStatus);
	
	/**
	 * fetches all the Registration records which is having the given server status
	 * codes.
	 *
	 * @param statusCodes 
	 * 				the status codes
	 * @return the list of registrations
	 */
	List<Registration> findByServerStatusCodeIn(List<String> statusCodes);

	/**
	 * fetches all the Registration records which is not having the given server
	 * status codes.
	 *
	 * @param statusCodes 
	 * 				the status codes
	 * @return the list of registrations
	 */
	List<Registration> findByServerStatusCodeNotInOrServerStatusCodeIsNull(List<String> statusCodes);
}
