/**
 * 
 */
package io.mosip.registration.processor.status.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.idvalidator.exception.InvalidIDException;
import io.mosip.kernel.core.idvalidator.spi.RidValidator;
import io.mosip.registration.processor.core.code.EventId;
import io.mosip.registration.processor.core.code.EventName;
import io.mosip.registration.processor.core.code.EventType;
import io.mosip.registration.processor.core.constant.ResponseStatusCode;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.rest.client.audit.builder.AuditLogRequestBuilder;
import io.mosip.registration.processor.status.dao.SyncRegistrationDao;
import io.mosip.registration.processor.status.dto.SyncRegistrationDto;
import io.mosip.registration.processor.status.dto.SyncResponseDto;
import io.mosip.registration.processor.status.dto.SyncTypeDto;
import io.mosip.registration.processor.status.entity.SyncRegistrationEntity;
import io.mosip.registration.processor.status.exception.TablenotAccessibleException;
import io.mosip.registration.processor.status.service.SyncRegistrationService;
import io.mosip.registration.processor.status.utilities.RegistrationUtility;

/**
 * The Class SyncRegistrationServiceImpl.
 *
 * @author M1048399
 * @author M1048219
 * @author M1047487
 */
@Component
public class SyncRegistrationServiceImpl implements SyncRegistrationService<SyncResponseDto, SyncRegistrationDto> {

	/** The Constant CREATED_BY. */
	private static final String CREATED_BY = "MOSIP";

	/** The event id. */
	private String eventId = "";

	/** The event name. */
	private String eventName = "";

	/** The event type. */
	private String eventType = "";

	/** The sync registration dao. */
	@Autowired
	private SyncRegistrationDao syncRegistrationDao;

	/** The core audit request builder. */
	@Autowired
	private AuditLogRequestBuilder auditLogRequestBuilder;

	/** The rid validator. */
	@Autowired
	private RidValidator<String> ridValidator;

	/**
	 * Instantiates a new sync registration service impl.
	 */
	public SyncRegistrationServiceImpl() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.registration.processor.status.service.SyncRegistrationService#sync(
	 * java.util.List)
	 */
	public List<SyncResponseDto> sync(List<SyncRegistrationDto> resgistrationDtos) {
		List<SyncResponseDto> synchResponseList = new ArrayList<>();

		boolean isTransactionSuccessful = false;
		try {
			for (SyncRegistrationDto registrationDto : resgistrationDtos) {
				synchResponseList = validateSync(registrationDto, synchResponseList);
			}
			isTransactionSuccessful = true;
		} catch (DataAccessLayerException e) {
			throw new TablenotAccessibleException(
					PlatformErrorMessages.RPR_RGS_REGISTRATION_TABLE_NOT_ACCESSIBLE.getMessage(), e);
		} finally {
			String description = "";
			if (isTransactionSuccessful) {
				eventName = eventId.equalsIgnoreCase(EventId.RPR_402.toString()) ? EventName.UPDATE.toString()
						: EventName.ADD.toString();
				eventType = EventType.BUSINESS.toString();
				description = "Registartion Id's are successfully synched in Sync Registration table";
			} else {
				eventId = EventId.RPR_405.toString();
				eventName = EventName.EXCEPTION.toString();
				eventType = EventType.SYSTEM.toString();
				description = "Registartion Id's sync is unsuccessful";
			}

			// auditLogRequestBuilder.createAuditRequestBuilder(description, eventId,
			// eventName, eventType,
			// AuditLogConstant.MULTIPLE_ID.toString());
		}
		return synchResponseList;

	}

	/**
	 * Validate sync.
	 *
	 * @param registrationDto the registration dto
	 * @param syncResponseList the sync response list
	 * @return the list
	 */
	private List<SyncResponseDto> validateSync(SyncRegistrationDto registrationDto,
			List<SyncResponseDto> syncResponseList) {
		if (validateLanguageCode(registrationDto, syncResponseList)
				&& validateStatusCode(registrationDto, syncResponseList)) {
			if (validateRegistrationID(registrationDto, syncResponseList)) {
				SyncResponseDto syncResponseDto = new SyncResponseDto();
				try {
					if (ridValidator.validateId(registrationDto.getRegistrationId())) {
						if (registrationDto.getParentRegistrationId() != null) {
							if (validateRegIdAndParentRegId(registrationDto, syncResponseList)) {
								if (ridValidator.validateId(registrationDto.getParentRegistrationId())) {
									syncResponseList = validateRegId(registrationDto, syncResponseList);
								}
							}
						} else {
							syncResponseList = validateRegId(registrationDto, syncResponseList);
						}
					}
				} catch (InvalidIDException e) {
					syncResponseDto.setRegistrationId(registrationDto.getRegistrationId());
					syncResponseDto.setParentRegistrationId(registrationDto.getParentRegistrationId());
					syncResponseDto.setStatus(ResponseStatusCode.FAILURE.toString());
					syncResponseDto.setMessage(e.getErrorText());
					syncResponseList.add(syncResponseDto);
				}
			}
		}
		return syncResponseList;
	}

	/**
	 * Validate status code.
	 *
	 * @param registrationDto the registration dto
	 * @param syncResponseList the sync response list
	 * @return true, if successful
	 */
	private boolean validateStatusCode(SyncRegistrationDto registrationDto, List<SyncResponseDto> syncResponseList) {

		String value = registrationDto.getSyncType();
		if (SyncTypeDto.NEW.getValue().equals(value)) {
			return true;
		} else if (SyncTypeDto.CORRECTION.getValue().equals(value)) {
			return true;
		} else if (SyncTypeDto.UPDATE.getValue().equals(value)) {
			return true;
		} else if (SyncTypeDto.LOST_UIN.getValue().equals(value)) {
			return true;
		} else if (SyncTypeDto.UPDATE_UIN.getValue().equals(value)) {
			return true;
		} else if (SyncTypeDto.ACTIVATE_UIN.getValue().equals(value)) {
			return true;
		} else if (SyncTypeDto.DEACTIVATE_UIN.getValue().equals(value)) {
			return true;
		} else {
			SyncResponseDto syncResponseDto = new SyncResponseDto();
			syncResponseDto.setRegistrationId(registrationDto.getRegistrationId());
			syncResponseDto.setParentRegistrationId(registrationDto.getParentRegistrationId());
			syncResponseDto.setStatus(ResponseStatusCode.FAILURE.toString());
			syncResponseDto.setMessage(
					"Invalid syncType. Available types are NEW, CORRECTION, UPDATE, LOST_UIN, UPDATE_UIN, ACTIVATE_UIN, DEACTIVATE_UIN");
			syncResponseList.add(syncResponseDto);
			return false;
		}
	}

	/**
	 * Validate language code.
	 *
	 * @param registrationDto the registration dto
	 * @param syncResponseList the sync response list
	 * @return true, if successful
	 */
	private boolean validateLanguageCode(SyncRegistrationDto registrationDto, List<SyncResponseDto> syncResponseList) {
		if (registrationDto.getLangCode().length() == 3) {
			return true;
		} else {
			SyncResponseDto syncResponseDto = new SyncResponseDto();
			syncResponseDto.setRegistrationId(registrationDto.getRegistrationId());
			syncResponseDto.setParentRegistrationId(registrationDto.getParentRegistrationId());
			syncResponseDto.setStatus(ResponseStatusCode.FAILURE.toString());
			syncResponseDto.setMessage("Language Code must be of three character");
			syncResponseList.add(syncResponseDto);
			return false;
		}
	}

	/**
	 * Validate reg id and parent reg id.
	 *
	 * @param registrationDto the registration dto
	 * @param syncResponseList the sync response list
	 * @return true, if successful
	 */
	private boolean validateRegIdAndParentRegId(SyncRegistrationDto registrationDto,
			List<SyncResponseDto> syncResponseList) {
		if (!registrationDto.getRegistrationId().equals(registrationDto.getParentRegistrationId())) {
			return true;
		} else {
			SyncResponseDto syncResponseDto = new SyncResponseDto();
			syncResponseDto.setRegistrationId(registrationDto.getRegistrationId());
			syncResponseDto.setParentRegistrationId(registrationDto.getParentRegistrationId());
			syncResponseDto.setStatus(ResponseStatusCode.FAILURE.toString());
			syncResponseDto.setMessage("RegistrationId and Parent RegistrationId cannot be same");
			syncResponseList.add(syncResponseDto);
			return false;
		}
	}

	/**
	 * Validate registration ID.
	 *
	 * @param registrationDto the registration dto
	 * @param syncResponseList the sync response list
	 * @return true, if successful
	 */
	private boolean validateRegistrationID(SyncRegistrationDto registrationDto,
			List<SyncResponseDto> syncResponseList) {
		if (registrationDto.getRegistrationId() != null) {
			return true;
		} else {
			SyncResponseDto syncResponseDto = new SyncResponseDto();
			syncResponseDto.setRegistrationId(registrationDto.getRegistrationId());
			syncResponseDto.setParentRegistrationId(registrationDto.getParentRegistrationId());
			syncResponseDto.setStatus(ResponseStatusCode.FAILURE.toString());
			syncResponseDto.setMessage("RegistrationId cannot be null");
			syncResponseList.add(syncResponseDto);
			return false;
		}
	}

	/**
	 * Validate reg id.
	 *
	 * @param registrationDto the registration dto
	 * @param syncResponseList the sync response list
	 * @return the list
	 */
	public List<SyncResponseDto> validateRegId(SyncRegistrationDto registrationDto,
			List<SyncResponseDto> syncResponseList) {
		SyncResponseDto syncResponseDto = new SyncResponseDto();
		SyncRegistrationEntity existingSyncRegistration = findByRegistrationId(
				registrationDto.getRegistrationId().trim());
		SyncRegistrationEntity syncRegistration;
		if (existingSyncRegistration != null) {
			// update sync registration record
			syncRegistration = convertDtoToEntity(registrationDto);
			syncRegistration.setId(existingSyncRegistration.getId());
			syncRegistration.setCreateDateTime(existingSyncRegistration.getCreateDateTime());
			syncRegistrationDao.update(syncRegistration);
			syncResponseDto.setRegistrationId(registrationDto.getRegistrationId());
			syncResponseDto.setParentRegistrationId(registrationDto.getParentRegistrationId());
			eventId = EventId.RPR_402.toString();
		} else {
			// first time sync registration
			syncRegistration = convertDtoToEntity(registrationDto);
			syncRegistration.setId(RegistrationUtility.generateId());
			syncRegistrationDao.save(syncRegistration);
			syncResponseDto.setRegistrationId(registrationDto.getRegistrationId());
			syncResponseDto.setParentRegistrationId(registrationDto.getParentRegistrationId());
			eventId = EventId.RPR_407.toString();
		}
		syncResponseDto.setStatus(ResponseStatusCode.SUCCESS.toString());
		syncResponseDto.setMessage("Registartion Id's are successfully synched in Sync table");
		syncResponseList.add(syncResponseDto);
		return syncResponseList;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.registration.processor.status.service.SyncRegistrationService#
	 * isPresent(java.lang.String)
	 */
	@Override
	public boolean isPresent(String registrationId) {
		return findByRegistrationId(registrationId) != null;
	}

	/**
	 * Find by registration id.
	 *
	 * @param registrationId
	 *            the registration id
	 * @return the sync registration entity
	 */
	private SyncRegistrationEntity findByRegistrationId(String registrationId) {
		return syncRegistrationDao.findById(registrationId);
	}

	/**
	 * Convert dto to entity.
	 *
	 * @param dto
	 *            the dto
	 * @return the sync registration entity
	 */
	private SyncRegistrationEntity convertDtoToEntity(SyncRegistrationDto dto) {
		SyncRegistrationEntity syncRegistrationEntity = new SyncRegistrationEntity();
		syncRegistrationEntity.setRegistrationId(dto.getRegistrationId().trim());
		syncRegistrationEntity.setIsActive(dto.getIsActive() != null ? dto.getIsActive() : Boolean.TRUE);
		syncRegistrationEntity.setIsDeleted(dto.getIsDeleted() != null ? dto.getIsDeleted() : Boolean.FALSE);
		syncRegistrationEntity.setLangCode(dto.getLangCode());
		syncRegistrationEntity.setParentRegistrationId(dto.getParentRegistrationId());
		syncRegistrationEntity.setStatusComment(dto.getStatusComment());
		syncRegistrationEntity.setStatusCode(dto.getSyncStatus().toString());
		syncRegistrationEntity.setRegistrationType(dto.getSyncType().toString());
		syncRegistrationEntity.setCreatedBy(CREATED_BY);
		syncRegistrationEntity.setUpdatedBy(CREATED_BY);
		if (syncRegistrationEntity.getIsDeleted()) {
			syncRegistrationEntity.setDeletedDateTime(LocalDateTime.now());
		} else {
			syncRegistrationEntity.setDeletedDateTime(null);
		}

		return syncRegistrationEntity;
	}
}
