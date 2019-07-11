package io.mosip.registration.processor.status.service;

import java.util.List;

import org.springframework.stereotype.Service;

import io.mosip.registration.processor.status.dto.InternalRegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusSubRequestDto;

// TODO: Auto-generated Javadoc
/**
 * This service is used to perform crud operations(get/add/update) on registration
 * status table.
 *
 * @param <T> the generic type
 * @param <U> the generic type
 * @param <D> the generic type
 */
/**
 * @author Shashank Agrawal
 * @author Sowmya Goudar
 *
 * @param <T>
 * @param <U>
 */
@Service
public interface RegistrationStatusService<T, U, D> {

	/**
	 * Gets the registration status.
	 *
	 * @param enrolmentId
	 *            the enrolment id
	 * @return the registration status
	 */
	public U getRegistrationStatus(T enrolmentId);

	/**
	 * Adds the registration status.
	 *
	 * @param registrationStatusDto
	 *            the registration status dto
	 */
	public void addRegistrationStatus(U registrationStatusDto);

	/**
	 * Update registration status.
	 *
	 * @param registrationStatusDto
	 *            the registration status dto
	 */
	public void updateRegistrationStatus(U registrationStatusDto);

	/**
	 * Gets the by status.
	 *
	 * @param status
	 *            the status
	 * @return the by status
	 */
	public List<U> getByStatus(String status);

	/**
	 * Gets the by ids.
	 *
	 * @param requestIds the request ids
	 * @return the list of Registrations for the given ids.
	 */
	public List<D> getByIds(List<RegistrationStatusSubRequestDto> requestIds);
	
	/**
	 * Gets the un processed packets.
	 *
	 * @param fetchSize the fetch size
	 * @param elapseTime the elapse time
	 * @param reprocessCount the reprocess count
	 * @param status the status
	 * @return the un processed packets
	 */
	public List<U> getUnProcessedPackets(Integer fetchSize, long elapseTime,
			Integer reprocessCount, List<String> status);
	
	/**
	 * Gets the un processed packets count.
	 *
	 * @param elapseTime the elapse time
	 * @param reprocessCount the reprocess count
	 * @param status the status
	 * @return the un processed packets count
	 */
	public Integer getUnProcessedPacketsCount(long elapseTime,
			Integer reprocessCount, List<String> status);
	
	/**
	 * Check Rid if uin is available
	 *
	 * 
	 */
	public Boolean checkUinAvailabilityForRid(String rid);
}
