package io.mosip.registration.processor.status.service;

import java.util.List;

import org.springframework.stereotype.Service;

import io.mosip.registration.processor.status.entity.SyncRegistrationEntity;

/**
 * The Interface SyncRegistrationService.
 *
 * @author M1047487
 * @param <T>
 *            the generic type
 */
@Service
public interface SyncRegistrationService<T,U> {

	/**
	 * Sync.
	 *
	 * @param syncResgistrationdto
	 *            the sync resgistrationdto
	 * @return the list
	 */
	public List<T> sync(List<U> syncResgistrationdto);

	/**
	 * Checks if is present.
	 *
	 * @param resgistrationId
	 *            the sync registration id
	 * @return true, if is present
	 */
	public boolean isPresent(String resgistrationId);

	/**
	 * Find by registration id.
	 *
	 * @param resgistrationId
	 *            the resgistration id
	 * @return the sync registration entity
	 */
	public SyncRegistrationEntity findByRegistrationId(String resgistrationId);

}
