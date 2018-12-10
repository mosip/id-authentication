package io.mosip.registration.processor.status.service;

import java.util.List;

import org.springframework.stereotype.Service;

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

}
