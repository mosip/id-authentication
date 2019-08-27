/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.booking.repository.impl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.preregistration.booking.entity.AvailibityEntity;
import io.mosip.preregistration.booking.errorcodes.ErrorCodes;
import io.mosip.preregistration.booking.errorcodes.ErrorMessages;
import io.mosip.preregistration.booking.exception.AppointmentBookingFailedException;
import io.mosip.preregistration.booking.exception.AvailabilityUpdationFailedException;
import io.mosip.preregistration.booking.exception.AvailablityNotFoundException;
import io.mosip.preregistration.booking.exception.BookingDataNotFoundException;
import io.mosip.preregistration.booking.exception.CancelAppointmentFailedException;
import io.mosip.preregistration.booking.exception.RecordFailedToDeleteException;
import io.mosip.preregistration.booking.exception.RecordNotFoundException;
import io.mosip.preregistration.booking.repository.BookingAvailabilityRepository;
import io.mosip.preregistration.booking.repository.DemographicRepository;
import io.mosip.preregistration.booking.repository.RegistrationBookingRepository;
import io.mosip.preregistration.core.common.entity.DemographicEntity;
import io.mosip.preregistration.core.common.entity.RegistrationBookingEntity;
import io.mosip.preregistration.core.exception.InvalidRequestParameterException;
import io.mosip.preregistration.core.exception.TableNotAccessibleException;

/**
 * This repository class is used to implement the JPA methods for Booking
 * application.
 * 
 * @author Kishan Rathore
 * @since 1.0.0
 *
 */
@Component
public class BookingDAO {

	/** Autowired reference for {@link #bookingRepository}. */
	@Autowired
	@Qualifier("bookingAvailabilityRepository")
	private BookingAvailabilityRepository bookingAvailabilityRepository;

	/** Autowired reference for {@link #registrationBookingRepository}. */
	@Autowired
	@Qualifier("registrationBookingRepository")
	private RegistrationBookingRepository registrationBookingRepository;

	@Autowired
	@Qualifier("demographicRepository")
	private DemographicRepository demographicRepository;

	/**
	 * @param Registration
	 *            center id
	 * @param Registration
	 *            date
	 * @return List AvailibityEntity based registration id and registration date.
	 */
	public List<AvailibityEntity> availability(String regcntrId, LocalDate regDate) {
		List<AvailibityEntity> availabilityList = null;
		try {
			availabilityList = bookingAvailabilityRepository.findByRegcntrIdAndRegDateOrderByFromTimeAsc(regcntrId,
					regDate);
		} catch (DataAccessLayerException e) {
			throw new AvailablityNotFoundException(ErrorCodes.PRG_BOOK_RCI_016.getCode(),
					ErrorMessages.AVAILABILITY_TABLE_NOT_ACCESSABLE.getMessage());
		}
		return availabilityList;

	}

	/**
	 * @param entity
	 * @return boolean
	 */
	public boolean saveBookAppointment(RegistrationBookingEntity entity) {
		return registrationBookingRepository.save(entity) != null;
	}



	/**
	 * @param slotFromTime
	 * @param slotToTime
	 * @param regDate
	 * @param regcntrd
	 * @return Availibity Entity based on FromTime, ToTime, RegDate and RegcntrId.
	 */
	public AvailibityEntity findByFromTimeAndToTimeAndRegDateAndRegcntrId(LocalTime slotFromTime, LocalTime slotToTime,
			LocalDate regDate, String regcntrd) {
		AvailibityEntity entity = null;
		try {
			entity = bookingAvailabilityRepository.findByFromTimeAndToTimeAndRegDateAndRegcntrId(slotFromTime,
					slotToTime, regDate, regcntrd);
			if (entity == null) {

				throw new AvailablityNotFoundException(ErrorCodes.PRG_BOOK_RCI_002.getCode(),
						ErrorMessages.AVAILABILITY_NOT_FOUND_FOR_THE_SELECTED_TIME.getMessage());
			}

		} catch (DataAccessLayerException e) {
			throw new TableNotAccessibleException(ErrorCodes.PRG_BOOK_RCI_016.getCode(),
					ErrorMessages.AVAILABILITY_TABLE_NOT_ACCESSABLE.getMessage());
		}
		return entity;
	}

	/**
	 * This method find entity for status other then CANCEL.
	 * 
	 * @param preregistrationId
	 * @param statusCode
	 * @return RegistrationBookingEntity based on Pre registration id and status
	 *         code.
	 */
	public RegistrationBookingEntity findByPreRegistrationId(String preregistrationId) {
		RegistrationBookingEntity entity = null;
		try {
			entity = registrationBookingRepository.getDemographicEntityPreRegistrationId(preregistrationId);
			if (entity == null) {
				throw new BookingDataNotFoundException(ErrorCodes.PRG_BOOK_RCI_013.getCode(),
						ErrorMessages.BOOKING_DATA_NOT_FOUND.getMessage());
			}
		} catch (DataAccessLayerException e) {
			throw new TableNotAccessibleException(ErrorCodes.PRG_BOOK_RCI_016.getCode(),
					ErrorMessages.BOOKING_TABLE_NOT_ACCESSIBLE.getMessage());
		}
		return entity;
	}

	/**
	 * @param bookingEnity
	 * @return RegistrationBookingEntity
	 */
	public RegistrationBookingEntity saveRegistrationEntityForCancel(RegistrationBookingEntity bookingEnity) {
		RegistrationBookingEntity entity = null;
		try {
			entity = registrationBookingRepository.save(bookingEnity);
			if (entity == null) {
				throw new CancelAppointmentFailedException(ErrorCodes.PRG_BOOK_RCI_019.getCode(),
						ErrorMessages.APPOINTMENT_CANCEL_FAILED.getMessage());
			}
		} catch (DataAccessLayerException e) {
			throw new TableNotAccessibleException(ErrorCodes.PRG_BOOK_RCI_016.getCode(),
					ErrorMessages.BOOKING_TABLE_NOT_ACCESSIBLE.getMessage());
		}
		return entity;
	}

	/**
	 * @param availibityEntity
	 * @return AvailibityEntity
	 */
	public AvailibityEntity updateAvailibityEntity(AvailibityEntity availibityEntity) {
		AvailibityEntity entity = null;
		try {
			entity = bookingAvailabilityRepository.update(availibityEntity);
			if (entity == null) {
				throw new AvailabilityUpdationFailedException(ErrorCodes.PRG_BOOK_RCI_024.getCode(),
						ErrorMessages.AVAILABILITY_UPDATE_FAILED.getMessage());
			}
		} catch (DataAccessLayerException e) {
			throw new TableNotAccessibleException(ErrorCodes.PRG_BOOK_RCI_016.getCode(),
					ErrorMessages.AVAILABILITY_TABLE_NOT_ACCESSABLE.getMessage());
		}
		return entity;
	}

	/**
	 * @param bookingEntity
	 * @return RegistrationBookingEntity
	 */
	public RegistrationBookingEntity saveRegistrationEntityForBooking(RegistrationBookingEntity bookingEntity) {
		RegistrationBookingEntity entity = null;
		try {
			entity = registrationBookingRepository.save(bookingEntity);
			if (entity == null) {
				throw new AppointmentBookingFailedException(ErrorCodes.PRG_BOOK_RCI_005.getCode(),
						ErrorMessages.APPOINTMENT_BOOKING_FAILED.getMessage());
			}
		} catch (DataAccessLayerException e) {
			throw new TableNotAccessibleException(ErrorCodes.PRG_BOOK_RCI_016.getCode(),
					ErrorMessages.BOOKING_TABLE_NOT_ACCESSIBLE.getMessage());
		}
		return entity;
	}

	/**
	 * @param regcntrId
	 * @param regDate
	 * @return List of AvailibityEntity
	 */
	public List<AvailibityEntity> findByRegcntrIdAndRegDateOrderByFromTimeAsc(String regcntrId, LocalDate regDate) {

		List<AvailibityEntity> entityList = null;
		try {
			entityList = bookingAvailabilityRepository.findByRegcntrIdAndRegDateOrderByFromTimeAsc(regcntrId, regDate);
		} catch (DataAccessLayerException e) {
			throw new TableNotAccessibleException(ErrorCodes.PRG_BOOK_RCI_016.getCode(),
					ErrorMessages.AVAILABILITY_TABLE_NOT_ACCESSABLE.getMessage());
		}
		return entityList;
	}

	/**
	 * @param entity
	 * @return boolean
	 */
	public boolean saveAvailability(AvailibityEntity entity) {
		return bookingAvailabilityRepository.save(entity) != null;
	}

	public List<RegistrationBookingEntity> findByPreregistrationId(String preId) {
		List<RegistrationBookingEntity> entityList = null;
		try {
			entityList = registrationBookingRepository.findByDemographicEntityPreRegistrationId(preId);
			if (entityList.isEmpty()) {
				throw new BookingDataNotFoundException(ErrorCodes.PRG_BOOK_RCI_013.getCode(),
						ErrorMessages.BOOKING_DATA_NOT_FOUND.getMessage());
			}
		} catch (DataAccessLayerException e) {
			throw new TableNotAccessibleException(ErrorCodes.PRG_BOOK_RCI_016.getCode(),
					ErrorMessages.BOOKING_TABLE_NOT_ACCESSIBLE.getMessage());
		}
		return entityList;

	}

	public int deleteByPreRegistrationId(String preId) {
		int count = registrationBookingRepository.deleteByDemographicEntityPreRegistrationId(preId);
		if (count == 0) {
			throw new RecordFailedToDeleteException(ErrorCodes.PRG_BOOK_RCI_028.getCode(),
					ErrorMessages.FAILED_TO_DELETE_THE_PRE_REGISTRATION_RECORD.getMessage());
		}
		return count;
	}

//	public void deleteRegistrationEntity(RegistrationBookingEntity bookingEnity) {
//		try {
//			registrationBookingRepository.delete(bookingEnity);
//		} catch (DataAccessLayerException e) {
//			throw new TableNotAccessibleException(ErrorCodes.PRG_BOOK_RCI_016.getCode(),
//					ErrorMessages.BOOKING_TABLE_NOT_ACCESSIBLE.getMessage());
//		}
//	}
	
	/**
	 * @param fromLocaldate
	 * @param toLocaldate
	 * @return
	 */
	public List<String> findByBookingDateBetweenAndRegCenterId(LocalDate fromLocaldate, LocalDate toLocaldate,
			String regCenterId) {
		List<String> listOfPreIds = new ArrayList<>();
		try {
			if (regCenterId != null && !regCenterId.isEmpty()) {
				List<RegistrationBookingEntity> entities = registrationBookingRepository
						.findByRegDateBetweenAndRegistrationCenterId(fromLocaldate, toLocaldate, regCenterId);
				if (entities != null && !entities.isEmpty()) {
					for (RegistrationBookingEntity entity : entities) {
						listOfPreIds.add(entity.getDemographicEntity().getPreRegistrationId());
					}
				} else {
					throw new BookingDataNotFoundException(ErrorCodes.PRG_BOOK_RCI_032.getCode(),
							ErrorMessages.RECORD_NOT_FOUND_FOR_DATE_RANGE_AND_REG_CENTER_ID.getMessage());
				}
			} else {
				throw new InvalidRequestParameterException(ErrorCodes.PRG_BOOK_RCI_007.getCode(),
						ErrorMessages.REGISTRATION_CENTER_ID_NOT_ENTERED.getMessage(), null);
			}
		} catch (DataAccessLayerException e) {
			throw new BookingDataNotFoundException(ErrorCodes.PRG_BOOK_RCI_032.getCode(),
					ErrorMessages.RECORD_NOT_FOUND_FOR_DATE_RANGE_AND_REG_CENTER_ID.getMessage());
		}
		return listOfPreIds;
	}

	/**
	 * 
	 * This method will update the booking status in applicant table.
	 * 
	 * @param preRegId
	 * @param status
	 * @return
	 */
	public DemographicEntity updateDemographicStatus(String preRegId, String status) {
		DemographicEntity demographicEntity = null;

		try {
			demographicEntity = demographicRepository.findBypreRegistrationId(preRegId);

			if (demographicEntity == null) {
				throw new RecordNotFoundException(ErrorCodes.PRG_PAM_APP_005.getCode(),
						ErrorMessages.UNABLE_TO_FETCH_THE_PRE_REGISTRATION.getMessage());
			}
		} catch (DataAccessLayerException e) {
			throw new BookingDataNotFoundException(ErrorCodes.PRG_BOOK_RCI_032.getCode(),
					ErrorMessages.RECORD_NOT_FOUND_FOR_DATE_RANGE_AND_REG_CENTER_ID.getMessage());
		}

		demographicEntity.setStatusCode(status);
		demographicRepository.save(demographicEntity);
		return demographicEntity;

	}

	/**
	 * 
	 * This method will update the booking status in applicant table.
	 * 
	 * @param preRegId
	 * @return
	 */
	public String getDemographicStatus(String preRegId) {
		DemographicEntity demographicEntity = null;

		try {
			demographicEntity = demographicRepository.findBypreRegistrationId(preRegId);

			if (demographicEntity == null) {
				throw new RecordNotFoundException(ErrorCodes.PRG_PAM_APP_005.getCode(),
						ErrorMessages.UNABLE_TO_FETCH_THE_PRE_REGISTRATION.getMessage());
			}
		} catch (DataAccessLayerException e) {
			throw new BookingDataNotFoundException(ErrorCodes.PRG_BOOK_RCI_032.getCode(),
					ErrorMessages.RECORD_NOT_FOUND_FOR_DATE_RANGE_AND_REG_CENTER_ID.getMessage());
		}
		return demographicEntity.getStatusCode();

	}

	public boolean findRegistrationCenterId(String regCenterId) {
		List<AvailibityEntity> entityList = null;
		try {
			entityList = bookingAvailabilityRepository.findByRegcntrId(regCenterId);
			if (entityList == null || entityList.isEmpty()) {
				throw new RecordNotFoundException(ErrorCodes.PRG_BOOK_RCI_015.getCode(),
						ErrorMessages.NO_TIME_SLOTS_ASSIGNED_TO_THAT_REG_CENTER.getMessage());
			}
			return true;

		} catch (DataAccessLayerException e) {
			throw new TableNotAccessibleException(ErrorCodes.PRG_BOOK_RCI_016.getCode(),
					ErrorMessages.AVAILABILITY_TABLE_NOT_ACCESSABLE.getMessage());
		}

	}

	/**
	 * 
	 * @param regDate
	 * @return list of regCenter
	 */
	public List<String> findRegCenter(LocalDate regDate) {
		List<String> regCenterList = new ArrayList<>();
		try {
			regCenterList = bookingAvailabilityRepository.findAvaialableRegCenter(regDate);
		} catch (DataAccessLayerException e) {
			throw new TableNotAccessibleException(ErrorCodes.PRG_BOOK_RCI_016.getCode(),
					ErrorMessages.AVAILABILITY_TABLE_NOT_ACCESSABLE.getMessage());
		}
		return regCenterList;
	}

	/**
	 * 
	 * @param regDate
	 * @param regID
	 * @return list of date
	 */
	public List<LocalDate> findDistinctDate(LocalDate regDate, String regID) {
		List<LocalDate> localDatList = null;
		try {
			localDatList = bookingAvailabilityRepository.findAvaialableDate(regDate, regID);
		} catch (DataAccessLayerException e) {
			throw new TableNotAccessibleException(ErrorCodes.PRG_BOOK_RCI_016.getCode(),
					ErrorMessages.AVAILABILITY_TABLE_NOT_ACCESSABLE.getMessage());
		}
		return localDatList;
	}

	/**
	 * 
	 * @param regDate
	 * @param regID
	 * @return list of AvailibityEntity
	 */
	public List<AvailibityEntity> findSlots(LocalDate regDate, String regID) {
		List<AvailibityEntity> localDatList = null;
		try {
			localDatList = bookingAvailabilityRepository.findAvaialableSlots(regDate, regID);
		} catch (DataAccessLayerException e) {
			throw new TableNotAccessibleException(ErrorCodes.PRG_BOOK_RCI_016.getCode(),
					ErrorMessages.AVAILABILITY_TABLE_NOT_ACCESSABLE.getMessage());
		}
		return localDatList;
	}

	/**
	 * 
	 * @param regId
	 * @param regDate
	 * @return number of deleted items
	 */
	public int deleteSlots(String regId, LocalDate regDate) {
		int deletedSlots = 0;
		try {
			deletedSlots = bookingAvailabilityRepository.deleteByRegcntrIdAndRegDate(regId, regDate);
		} catch (DataAccessLayerException e) {
			throw new TableNotAccessibleException(ErrorCodes.PRG_BOOK_RCI_016.getCode(),
					ErrorMessages.AVAILABILITY_TABLE_NOT_ACCESSABLE.getMessage());
		}
		return deletedSlots;
	}

	/**
	 * 
	 * @param regId
	 * @param regDate
	 * @return list of RegistrationBookingEntity
	 */
	public List<RegistrationBookingEntity> findAllPreIds(String regId, LocalDate regDate) {
		List<RegistrationBookingEntity> registrationBookingEntityList = null;
		try {
			registrationBookingEntityList = registrationBookingRepository.findByRegistrationCenterIdAndRegDate(regId,
					regDate);
		} catch (DataAccessLayerException e) {
			throw new TableNotAccessibleException(ErrorCodes.PRG_BOOK_RCI_016.getCode(),
					ErrorMessages.AVAILABILITY_TABLE_NOT_ACCESSABLE.getMessage());
		}
		return registrationBookingEntityList;
	}

	/**
	 * 
	 * @param regId
	 * @param date
	 * @return list of RegistrationBookingEntity
	 */
	public List<RegistrationBookingEntity> findAllPreIdsByregID(String regId, LocalDate date) {
		List<RegistrationBookingEntity> registrationBookingEntityList = null;
		try {
			registrationBookingEntityList = registrationBookingRepository.findByRegId(regId, date);
		} catch (DataAccessLayerException e) {
			throw new TableNotAccessibleException(ErrorCodes.PRG_BOOK_RCI_016.getCode(),
					ErrorMessages.AVAILABILITY_TABLE_NOT_ACCESSABLE.getMessage());
		}
		return registrationBookingEntityList;
	}

	/**
	 * 
	 * Aparam regId
	 * 
	 * @param regDate
	 * @return number of deleted items
	 */
	public int deleteAllSlotsByRegId(String regId, LocalDate regDate) {
		int deletedSlots = 0;
		try {
			deletedSlots = bookingAvailabilityRepository.deleteByRegcntrIdAndRegDateGreaterThanEqual(regId, regDate);
		} catch (DataAccessLayerException e) {
			throw new TableNotAccessibleException(ErrorCodes.PRG_BOOK_RCI_016.getCode(),
					ErrorMessages.AVAILABILITY_TABLE_NOT_ACCESSABLE.getMessage());
		}
		return deletedSlots;
	}
	
	/**
	 * @param regcntrId
	 * @param regDate
	 * @return List of AvailibityEntity
	 */
	public List<AvailibityEntity> findAvailability(String regcntrId,LocalDate starteDate,LocalDate endDate ) {

		List<AvailibityEntity> entityList = null;
		try {
			entityList = bookingAvailabilityRepository.findByRegcntrIdAndRegDateGreaterThanEqualAndRegDateLessThanEqualOrderByRegDateAsc(regcntrId,starteDate,endDate);
		} catch (DataAccessLayerException e) {
			throw new TableNotAccessibleException(ErrorCodes.PRG_BOOK_RCI_016.getCode(),
					ErrorMessages.AVAILABILITY_TABLE_NOT_ACCESSABLE.getMessage());
		}
		return entityList;
	}

}
