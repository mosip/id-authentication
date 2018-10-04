/**
 * 
 */
package io.mosip.registration.processor.status.service.impl;

import java.util.List;

import io.mosip.registration.processor.status.dto.SyncRegistrationDto;
import io.mosip.registration.processor.status.entity.SyncRegistrationEntity;
import io.mosip.registration.processor.status.service.SyncRegistrationService;
import org.springframework.stereotype.Component;

/**
 * The Class SyncRegistrationServiceImpl.
 *
 * @author M1047487
 */
@Component
public class SyncRegistrationServiceImpl implements SyncRegistrationService<SyncRegistrationDto> {

	/**
	 * Instantiates a new sync registration service impl.
	 */
	public SyncRegistrationServiceImpl() {
		super();
	}

	@Override
	public List<SyncRegistrationDto> sync(List<SyncRegistrationDto> syncResgistrationdto) {
		return null;
	}

	@Override
	public boolean isPresent(SyncRegistrationDto syncResgistrationdto) {
		return false;
	}

	/**
	 * Convert entity to dto.
	 *
	 * @param entities
	 *            the entities
	 * @return the list
	 */
	@SuppressWarnings("unused")
	private List<SyncRegistrationDto> convertEntityToDto(List<SyncRegistrationEntity> entities) {
		return null;
	}

	/**
	 * Convert dto to entity.
	 *
	 * @param entities
	 *            the entities
	 * @return the list
	 */
	@SuppressWarnings("unused")
	private List<SyncRegistrationEntity> convertDtoToEntity(List<SyncRegistrationDto> entities) {
		return null;
	}

}
