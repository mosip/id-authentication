package io.mosip.registration.processor.status.service;

import java.util.List;

import org.springframework.stereotype.Service;

import io.mosip.registration.processor.status.dto.RegistrationStatusDto;
import io.mosip.registration.processor.status.dto.RegistrationStatusSubRequestDto;

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
	 * @param ids
	 *            the ids
	 * @return the list of Registrations for the given ids.
	 */
	public List<D> getByIds(List<RegistrationStatusSubRequestDto> requestIds);

	List<RegistrationStatusDto> getById(String requestId);
}
