package io.mosip.registration.processor.status.service;

import java.util.List;

import org.springframework.stereotype.Service;

/**
 * This service is used to perform crud operations(get/add/update) on registration
 * status table.
 *
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

	public U getRegistrationStatus(T enrolmentId);

	public void addRegistrationStatus(U registrationStatusDto);

	public void updateRegistrationStatus(U registrationStatusDto);

	public List<U> findbyfilesByThreshold(String statusCode);

	public List<U> getByStatus(String status);

	/**
	 * 
	 * @param ids
	 * 
	 * @return the list of Registrations for the given ids.
	 */
	public List<D> getByIds(String ids);
}
