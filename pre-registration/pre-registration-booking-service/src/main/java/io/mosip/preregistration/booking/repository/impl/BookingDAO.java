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
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Component;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.preregistration.booking.entity.AvailibityEntity;
import io.mosip.preregistration.booking.entity.RegistrationBookingEntity;
import io.mosip.preregistration.booking.errorcodes.ErrorCodes;
import io.mosip.preregistration.booking.errorcodes.ErrorMessages;
import io.mosip.preregistration.booking.exception.AppointmentBookingFailedException;
import io.mosip.preregistration.booking.exception.AvailabilityUpdationFailedException;
import io.mosip.preregistration.booking.exception.AvailablityNotFoundException;
import io.mosip.preregistration.booking.exception.BookingDataNotFoundException;
import io.mosip.preregistration.booking.exception.CancelAppointmentFailedException;
import io.mosip.preregistration.booking.exception.RecordNotFoundException;
import io.mosip.preregistration.booking.repository.BookingAvailabilityRepository;
import io.mosip.preregistration.booking.repository.RegistrationBookingRepository;
import io.mosip.preregistration.core.exception.TableNotAccessibleException;

/**
 * This repository class is used to implement the JPA methods for Booking application.
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

	/**
	 * @param Registration center id
	 * @param Registration date
	 * @return List AvailibityEntity based registration id and registration date.
	 */
	public List<AvailibityEntity> availability(String regcntrId, LocalDate regDate) {
		List<AvailibityEntity> availabilityList = new ArrayList<>();
		try {
			availabilityList = bookingAvailabilityRepository.findByRegcntrIdAndRegDateOrderByFromTimeAsc(regcntrId, regDate);
		} catch (DataAccessLayerException e) {
			throw new AvailablityNotFoundException(ErrorCodes.PRG_BOOK_RCI_016.toString(),
					ErrorMessages.BOOKING_TABLE_NOT_ACCESSIBLE.toString());
		}
		return availabilityList;

	}

	/**
	 * @param entity
	 * @return boolean
	 */
	public boolean saveBookAppointment(RegistrationBookingEntity entity) {
		if (registrationBookingRepository.save(entity) != null) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @param regcntrId
	 * @param fromDate
	 * @param toDate
	 * @return List of Local date
	 */
	public List<LocalDate> findDate(String regcntrId, LocalDate fromDate, LocalDate toDate) {
		List<LocalDate> localDatList = new ArrayList<>();
		try {
			localDatList = bookingAvailabilityRepository.findDate(regcntrId, fromDate, toDate);
			if (localDatList.isEmpty()) {
				throw new RecordNotFoundException(ErrorCodes.PRG_BOOK_RCI_015.toString(),
						ErrorMessages.NO_TIME_SLOTS_ASSIGNED_TO_THAT_REG_CENTER.toString());
			}
		} catch (DataAccessLayerException e) {
			throw new TableNotAccessibleException(ErrorCodes.PRG_BOOK_RCI_016.toString(),
					ErrorMessages.BOOKING_TABLE_NOT_ACCESSIBLE.toString());
		}
		return localDatList;
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
			entity = bookingAvailabilityRepository.findByFromTimeAndToTimeAndRegDateAndRegcntrId(slotFromTime, slotToTime, regDate,
					regcntrd);
			if (entity == null) {

				throw new AvailablityNotFoundException(ErrorCodes.PRG_BOOK_RCI_002.toString(),
						ErrorMessages.AVAILABILITY_NOT_FOUND_FOR_THE_SELECTED_TIME.toString());
			}

		} catch (DataAccessLayerException e) {
			throw new TableNotAccessibleException(ErrorCodes.PRG_BOOK_RCI_016.toString(),
					ErrorMessages.BOOKING_TABLE_NOT_ACCESSIBLE.toString());
		}
		return entity;
	}

	/**
	 * @param preregistrationId
	 * @param statusCode
	 * @return RegistrationBookingEntity based on Pre registration id and status code.
	 */
	public RegistrationBookingEntity findPreIdAndStatusCode(String preregistrationId, String statusCode) {
		RegistrationBookingEntity entity = null;
		try {
			entity = registrationBookingRepository.findPreIdAndStatusCode(preregistrationId, statusCode);
			if (entity == null) {
				throw new BookingDataNotFoundException(ErrorCodes.PRG_BOOK_RCI_013.toString(),
						ErrorMessages.BOOKING_DATA_NOT_FOUND.toString());
			}
		} catch (DataAccessLayerException e) {
			throw new TableNotAccessibleException(ErrorCodes.PRG_BOOK_RCI_016.toString(),
					ErrorMessages.AVAILABILITY_TABLE_NOT_ACCESSABLE.toString());
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
				throw new CancelAppointmentFailedException(ErrorCodes.PRG_BOOK_RCI_019.toString(),
						ErrorMessages.APPOINTMENT_CANCEL_FAILED.toString());
			}
		} catch (DataAccessLayerException e) {
			throw new TableNotAccessibleException(ErrorCodes.PRG_BOOK_RCI_016.toString(),
					ErrorMessages.AVAILABILITY_TABLE_NOT_ACCESSABLE.toString());
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
				throw new AvailabilityUpdationFailedException(ErrorCodes.PRG_BOOK_RCI_024.toString(),
						ErrorMessages.AVAILABILITY_UPDATE_FAILED.toString());
			}
		} catch (DataAccessLayerException e) {
			throw new TableNotAccessibleException(ErrorCodes.PRG_BOOK_RCI_016.toString(),
					ErrorMessages.BOOKING_TABLE_NOT_ACCESSIBLE.toString());
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
				throw new AppointmentBookingFailedException(ErrorCodes.PRG_BOOK_RCI_005.toString(),
						ErrorMessages.APPOINTMENT_BOOKING_FAILED.toString());
			}
		} catch (DataAccessLayerException e) {
			throw new TableNotAccessibleException(ErrorCodes.PRG_BOOK_RCI_016.toString(),
					ErrorMessages.AVAILABILITY_TABLE_NOT_ACCESSABLE.toString());
		}
		return entity;
	}
	
	/**
	 * @param registrationCenterId
	 * @param statusCode
	 * @return List of RegistrationBookingEntity
	 */
	public List<RegistrationBookingEntity> findByRegistrationCenterIdAndStatusCode(String registrationCenterId,
			String statusCode) {
		List<RegistrationBookingEntity> entityList = new ArrayList<>();
		try {
			entityList=registrationBookingRepository.findByRegistrationCenterIdAndStatusCode(registrationCenterId, statusCode);
			if (entityList == null && entityList.isEmpty()) {
				throw new BookingDataNotFoundException(ErrorCodes.PRG_BOOK_RCI_013.toString(),
						ErrorMessages.BOOKING_DATA_NOT_FOUND.toString());
			}
		} catch (DataAccessLayerException e) {
			throw new TableNotAccessibleException(ErrorCodes.PRG_BOOK_RCI_016.toString(),
					ErrorMessages.AVAILABILITY_TABLE_NOT_ACCESSABLE.toString());
		}
		return entityList;
	}
	
	/**
	 * @param regcntrId
	 * @param regDate
	 * @return List of AvailibityEntity
	 */
	public List<AvailibityEntity> findByRegcntrIdAndRegDateOrderByFromTimeAsc(String regcntrId, LocalDate regDate){
		
		List<AvailibityEntity> entityList=new ArrayList<>();
		try {
			entityList=bookingAvailabilityRepository.findByRegcntrIdAndRegDateOrderByFromTimeAsc(regcntrId, regDate);
		} catch (DataAccessLayerException e) {
			throw new TableNotAccessibleException(ErrorCodes.PRG_BOOK_RCI_016.toString(),
					ErrorMessages.BOOKING_TABLE_NOT_ACCESSIBLE.toString());
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

}
