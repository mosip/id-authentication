/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.booking.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.preregistration.booking.codes.RequestCodes;
import io.mosip.preregistration.booking.dto.AvailabilityDto;
import io.mosip.preregistration.booking.dto.BookingRequestDTO;
import io.mosip.preregistration.booking.dto.BookingStatusDTO;
import io.mosip.preregistration.booking.dto.CancelBookingDTO;
import io.mosip.preregistration.booking.dto.CancelBookingResponseDTO;
import io.mosip.preregistration.booking.dto.DateTimeDto;
import io.mosip.preregistration.booking.dto.RegistrationCenterDto;
import io.mosip.preregistration.booking.entity.AvailibityEntity;
import io.mosip.preregistration.booking.entity.RegistrationBookingEntity;
import io.mosip.preregistration.booking.errorcodes.ErrorCodes;
import io.mosip.preregistration.booking.errorcodes.ErrorMessages;
import io.mosip.preregistration.booking.exception.AvailablityNotFoundException;
import io.mosip.preregistration.booking.exception.BookingDataNotFoundException;
import io.mosip.preregistration.booking.exception.TimeSpanException;
import io.mosip.preregistration.booking.exception.util.BookingExceptionCatcher;
import io.mosip.preregistration.booking.repository.impl.BookingDAO;
import io.mosip.preregistration.booking.service.util.BookingLock;
import io.mosip.preregistration.booking.service.util.BookingServiceUtil;
import io.mosip.preregistration.core.code.StatusCodes;
import io.mosip.preregistration.core.common.dto.BookingRegistrationDTO;
import io.mosip.preregistration.core.common.dto.DeleteBookingDTO;
import io.mosip.preregistration.core.common.dto.MainListRequestDTO;
import io.mosip.preregistration.core.common.dto.MainListResponseDTO;
import io.mosip.preregistration.core.common.dto.MainRequestDTO;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.common.dto.PreRegIdsByRegCenterIdDTO;
import io.mosip.preregistration.core.common.dto.PreRegIdsByRegCenterIdResponseDTO;
import io.mosip.preregistration.core.config.LoggerConfiguration;
import io.mosip.preregistration.core.util.UUIDGeneratorUtil;
import io.mosip.preregistration.core.util.ValidationUtil;

/**
 * This class provides the service implementation for Booking application.
 * 
 * @author Kishan Rathore
 * @author Jagadishwari
 * @author Ravi C. Balaji
 * @since 1.0.0
 *
 */
@Component
public class BookingService {

	/**
	 * Autowired reference for {@link #serviceUtil}
	 */
	@Autowired
	BookingServiceUtil serviceUtil;

	/**
	 * Reference for ${preregistration.availability.sync} from property file
	 */
	@Value("${preregistration.availability.sync}")
	int syncDays;

	/**
	 * Reference for ${preregistration.availability.noOfDays} from property file
	 */
	@Value("${preregistration.availability.noOfDays}")
	int displayDays;
	
	/**
	 * Reference for ${preregistration.booking.offset} from property file
	 */
	@Value("${preregistration.booking.offset}")
	int availabilityOffset;

	@Autowired
	private BookingDAO bookingDAO;

	@Value("${ver}")
	String versionUrl;

	@Value("${id}")
	String idUrl;

	Map<String, String> requiredRequestMap = new HashMap<>();

	@PostConstruct
	public void setupBookingService() {
		requiredRequestMap.put("id", idUrl);
		requiredRequestMap.put("ver", versionUrl);

	}

	private Logger log = LoggerConfiguration.logConfig(BookingService.class);

	/**
	 * It will sync the registration center details
	 * 
	 * @return ResponseDto<String>
	 */
	public MainResponseDTO<String> addAvailability() {
		log.info("sessionId", "idType", "id", "In addAvailability method of Booking Service");
		MainResponseDTO<String> response = new MainResponseDTO<>();
		try {
			LocalDate endDate = LocalDate.now().plusDays(syncDays);
			List<RegistrationCenterDto> regCenter = serviceUtil.callRegCenterDateRestService();
			for (RegistrationCenterDto regDto : regCenter) {
				List<String> holidaylist = serviceUtil.callGetHolidayListRestService(regDto);
				for (LocalDate sDate = LocalDate.now(); (sDate.isBefore(endDate)
						|| sDate.isEqual(endDate)); sDate = sDate.plusDays(1)) {
					serviceUtil.timeSlotCalculator(regDto, holidaylist, sDate, bookingDAO);
				}
			}
		} catch (Exception ex) {
			log.error("sessionId", "idType", "id", "In addAvailability method of Booking Service- " + ex.getMessage());
			new BookingExceptionCatcher().handle(ex);
		}
		response.setResTime(serviceUtil.getCurrentResponseTime());
		response.setStatus(true);
		response.setResponse("MASTER_DATA_SYNCED_SUCCESSFULLY");
		return response;

	}

	/**
	 * Gives the availability details
	 * 
	 * @param regID
	 * @return ResponseDto<AvailabilityDto>
	 */
	public MainResponseDTO<AvailabilityDto> getAvailability(String regID) {
		log.info("sessionId", "idType", "id", "In getAvailability method of Booking Service");
		MainResponseDTO<AvailabilityDto> response = new MainResponseDTO<>();
		LocalDate endDate = LocalDate.now().plusDays(displayDays+availabilityOffset);
		LocalDate fromDate = LocalDate.now().plusDays(availabilityOffset);
		AvailabilityDto availability = new AvailabilityDto();
		try {
			List<LocalDate> dateList = bookingDAO.findDate(regID, fromDate, endDate);
			List<DateTimeDto> dateTimeList = new ArrayList<>();
			for (int i = 0; i < dateList.size(); i++) {
				DateTimeDto dateTime = new DateTimeDto();
				List<AvailibityEntity> entity = bookingDAO.findByRegcntrIdAndRegDateOrderByFromTimeAsc(regID,
						dateList.get(i));
				if (!entity.isEmpty()) {
					serviceUtil.slotSetter(dateList, dateTimeList, i, dateTime, entity);
				}
			}
			availability.setCenterDetails(dateTimeList);
			availability.setRegCenterId(regID);
		} catch (Exception ex) {
			log.error("sessionId", "idType", "id", "In getAvailability method of Booking Service- " + ex.getMessage());
			new BookingExceptionCatcher().handle(ex);
		}
		response.setResTime(serviceUtil.getCurrentResponseTime());
		response.setStatus(true);
		response.setResponse(availability);
		return response;
	}

	/**
	 * This method will book the appointment.
	 * 
	 * @param bookingRequestDTO
	 * @return response with status code
	 * @throws java.text.ParseException
	 */
	/**
	 * @param bookingRequestDTOs
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public MainResponseDTO<List<BookingStatusDTO>> bookAppointment(
			MainListRequestDTO<BookingRequestDTO> bookingRequestDTOs) {
		log.info("sessionId", "idType", "id", "In bookAppointment method of Booking Service");
		MainResponseDTO<List<BookingStatusDTO>> responseDTO = new MainResponseDTO<>();
		List<BookingStatusDTO> respList = new ArrayList<>();
		try {
			if (ValidationUtil.requestValidator(serviceUtil.prepareRequestMap(bookingRequestDTOs),
					requiredRequestMap)) {
				for (BookingRequestDTO bookingRequestDTO : bookingRequestDTOs.getRequest()) {
					/* Getting Status From Demographic */
					String preRegStatusCode = serviceUtil
							.callGetStatusRestService(bookingRequestDTO.getPreRegistrationId());

					BookingRegistrationDTO oldBookingRegistrationDTO = bookingRequestDTO.getOldBookingDetails();
					BookingRegistrationDTO newBookingRegistrationDTO = bookingRequestDTO.getNewBookingDetails();

					if (serviceUtil.mandatoryParameterCheck(bookingRequestDTO.getPreRegistrationId(),
							oldBookingRegistrationDTO, newBookingRegistrationDTO)) {

						/* Checking the availability of slots */
						checkSlotAvailability(newBookingRegistrationDTO);

						if (preRegStatusCode.equals(StatusCodes.PENDING_APPOINTMENT.getCode())) {

							/* Creating new booking */
							respList.add(book(bookingRequestDTO.getPreRegistrationId(), newBookingRegistrationDTO));

						} else if (preRegStatusCode.equals(StatusCodes.BOOKED.getCode())) {

							/* Concatenating Booking date and slot from time */
							RegistrationBookingEntity bookingEntity = bookingDAO
									.findByPreRegistrationId(bookingRequestDTO.getPreRegistrationId());

							String str = bookingEntity.getRegDate() + " " + bookingEntity.getSlotFromTime();
							DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
							LocalDateTime bookedDateTime = LocalDateTime.parse(str, formatter);

							/* Time span check for re-book */
							serviceUtil.timeSpanCheckForRebook(bookedDateTime);

							/* Deleting old booking */
							deleteOldBooking(bookingRequestDTO.getPreRegistrationId());

							/* Increase availability */
							increaseAvailability(oldBookingRegistrationDTO);

							/* Creating new booking */
							respList.add(book(bookingRequestDTO.getPreRegistrationId(), newBookingRegistrationDTO));

						} else if (preRegStatusCode.equals(StatusCodes.EXPIRED.getCode())) {

							/* Deleting old booking */
							deleteOldBooking(bookingRequestDTO.getPreRegistrationId());

							/* Creating new booking */
							respList.add(book(bookingRequestDTO.getPreRegistrationId(), newBookingRegistrationDTO));
						}

					}

				}
			}
		} catch (Exception ex) {
			log.error("sessionId", "idType", "id", "In bookAppointment method of Booking Service- " + ex.getMessage());
			new BookingExceptionCatcher().handle(ex);
		}
		responseDTO.setStatus(true);
		responseDTO.setResTime(serviceUtil.getCurrentResponseTime());
		responseDTO.setResponse(respList);
		return responseDTO;
	}

	/**
	 * This method is for getting appointment details.
	 * 
	 * @param preRegID
	 * @return MainResponseDTO
	 */
	public MainResponseDTO<BookingRegistrationDTO> getAppointmentDetails(String preRegID) {
		log.info("sessionId", "idType", "id", "In getAppointmentDetails method of Booking Service");
		BookingRegistrationDTO bookingRegistrationDTO = new BookingRegistrationDTO();
		MainResponseDTO<BookingRegistrationDTO> responseDto = new MainResponseDTO<>();
		RegistrationBookingEntity entity = new RegistrationBookingEntity();
		try {
			/* Getting Status From Demographic */
			String preRegStatusCode = serviceUtil.callGetStatusRestService(preRegID);
			entity = bookingDAO.findByPreRegistrationId(preRegID);
			if (!preRegStatusCode.equals(StatusCodes.BOOKED.getCode())) {
				throw new BookingDataNotFoundException(ErrorCodes.PRG_BOOK_RCI_030.getCode(),
						ErrorMessages.CANNOT_GET_DETAILS_FOR + "_" + preRegStatusCode.toUpperCase() + "_STATUS");
			}
			bookingRegistrationDTO.setRegDate(entity.getRegDate().toString());
			bookingRegistrationDTO.setRegistrationCenterId(entity.getRegistrationCenterId());
			bookingRegistrationDTO.setSlotFromTime(entity.getSlotFromTime().toString());
			bookingRegistrationDTO.setSlotToTime(entity.getSlotToTime().toString());
			responseDto.setResponse(bookingRegistrationDTO);
			responseDto.setStatus(true);
			responseDto.setErr(null);
			responseDto.setResTime(serviceUtil.getCurrentResponseTime());

		} catch (Exception ex) {
			log.error("sessionId", "idType", "id",
					"In getAppointmentDetails method of Booking Service- " + ex.getMessage());
			new BookingExceptionCatcher().handle(ex);
		}

		return responseDto;
	}

	/**
	 * This method will cancel the appointment.
	 * 
	 * @param MainRequestDTO
	 * @return MainResponseDTO
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public MainResponseDTO<CancelBookingResponseDTO> cancelAppointment(MainRequestDTO<CancelBookingDTO> requestdto) {
		log.info("sessionId", "idType", "id", "In cancelAppointment method of Booking Service");
		MainResponseDTO<CancelBookingResponseDTO> responseDto = new MainResponseDTO<>();
		try {
			if (ValidationUtil.requestValidator(serviceUtil.prepareRequestMap(requestdto), requiredRequestMap)) {
				responseDto.setResponse(cancelBooking(requestdto.getRequest()));
			}

		} catch (Exception ex) {
			log.error("sessionId", "idType", "id",
					"In cancelAppointment method of Booking Service- " + ex.getMessage());
			new BookingExceptionCatcher().handle(ex);
		}
		responseDto.setStatus(true);
		responseDto.setResTime(serviceUtil.getCurrentResponseTime());
		return responseDto;
	}

	/**
	 * This method will get Pre registration Id based on registration center Id.
	 * 
	 * @param requestDTO
	 * @return
	 */
	public MainListResponseDTO<PreRegIdsByRegCenterIdResponseDTO> getPreIdsByRegCenterId(
			MainRequestDTO<PreRegIdsByRegCenterIdDTO> requestDTO) {
		log.info("sessionId", "idType", "id", "In getPreIdsByRegCenterId method of Booking Service");
		MainListResponseDTO<PreRegIdsByRegCenterIdResponseDTO> responseDto = new MainListResponseDTO<>();
		PreRegIdsByRegCenterIdResponseDTO preRegIdsByRegCenterIdResponseDTO = new PreRegIdsByRegCenterIdResponseDTO();
		List<PreRegIdsByRegCenterIdResponseDTO> preRegIdsByRegCenterIdResponseDTOList = new ArrayList<>();
		try {
			if (ValidationUtil.requestValidator(serviceUtil.prepareRequestMap(requestDTO), requiredRequestMap)) {
				String regCenterId = requestDTO.getRequest().getRegistrationCenterId();
				List<RegistrationBookingEntity> bookingEntities = bookingDAO
						.findByRegistrationCenterId(regCenterId.trim());
				Iterator<RegistrationBookingEntity> iterate = bookingEntities.iterator();
				while (iterate.hasNext()) {
					String preRegStatusCode = serviceUtil
							.callGetStatusRestService(iterate.next().getBookingPK().getPreregistrationId());
					if (!preRegStatusCode.equals(StatusCodes.BOOKED.getCode())) {
						bookingEntities.remove(bookingEntities.indexOf(iterate.next()));
					}
				}
				List<String> preRegIdList = requestDTO.getRequest().getPreRegistrationIds();
				List<String> entityPreRegIdList = new LinkedList<>();
				for (RegistrationBookingEntity bookingEntity : bookingEntities) {
					entityPreRegIdList.add(bookingEntity.getBookingPK().getPreregistrationId());
				}
				preRegIdList.retainAll(entityPreRegIdList);
				if (!preRegIdList.isEmpty()) {
					preRegIdsByRegCenterIdResponseDTO.setRegistrationCenterId(regCenterId);
					preRegIdsByRegCenterIdResponseDTO.setPreRegistrationIds(preRegIdList);
					preRegIdsByRegCenterIdResponseDTOList.add(preRegIdsByRegCenterIdResponseDTO);

					responseDto.setResTime(serviceUtil.getCurrentResponseTime());
					responseDto.setStatus(true);
					responseDto.setResponse(preRegIdsByRegCenterIdResponseDTOList);
				} else {
					throw new BookingDataNotFoundException(ErrorCodes.PRG_BOOK_RCI_013.toString(),
							ErrorMessages.BOOKING_DATA_NOT_FOUND.toString());
				}
			}
		} catch (Exception ex) {
			log.error("sessionId", "idType", "id",
					"In getPreIdsByRegCenterId method of Booking Service for Exception- " + ex.getMessage());
			new BookingExceptionCatcher().handle(ex);
		}
		return responseDto;
	}

	/**
	 * This booking API will be called by bookAppointment.
	 * 
	 * @param preRegistrationId
	 * @param bookingRegistrationDTO
	 * @return BookingStatusDTO
	 */
	public BookingStatusDTO book(String preRegistrationId, BookingRegistrationDTO bookingRegistrationDTO) {
		log.info("sessionId", "idType", "id", "In book method of Booking Service");
		BookingStatusDTO bookingStatusDTO = new BookingStatusDTO();
		try {
			BookingLock bookingLock = new BookingLock(bookingRegistrationDTO.getRegistrationCenterId(),
					bookingRegistrationDTO.getRegDate(), bookingRegistrationDTO.getSlotFromTime());
			AvailibityEntity availableEntity;

			synchronized (bookingLock) {
				availableEntity = bookingDAO.findByFromTimeAndToTimeAndRegDateAndRegcntrId(
						LocalTime.parse(bookingRegistrationDTO.getSlotFromTime()),
						LocalTime.parse(bookingRegistrationDTO.getSlotToTime()),
						LocalDate.parse(bookingRegistrationDTO.getRegDate()),
						bookingRegistrationDTO.getRegistrationCenterId());
				if (serviceUtil.isKiosksAvailable(availableEntity)) {
					/* Updating booking */
					bookingDAO.saveRegistrationEntityForBooking(
							serviceUtil.bookingEntitySetter(preRegistrationId, bookingRegistrationDTO));
					/* Updating demographic */
					serviceUtil.callUpdateStatusRestService(preRegistrationId, StatusCodes.BOOKED.getCode());
					bookingStatusDTO.setPreRegistrationId(preRegistrationId);
					bookingStatusDTO.setBookingStatus(StatusCodes.BOOKED.getCode());
					bookingStatusDTO.setBookingMessage("APPOINTMENT_SUCCESSFULLY_BOOKED");
					/* Reduce Availability */
					availableEntity.setAvailableKiosks(availableEntity.getAvailableKiosks() - 1);
					bookingDAO.updateAvailibityEntity(availableEntity);
				}

			}

		} catch (Exception ex) {
			log.error("sessionId", "idType", "id", "In book method of Booking Service- " + ex.getMessage());
			new BookingExceptionCatcher().handle(ex);
		}

		return bookingStatusDTO;
	}

	/**
	 * This cancel API will be called by cancelAppointment.
	 * 
	 * @param cancelBookingDTO
	 * @return response with status code
	 */
	public CancelBookingResponseDTO cancelBooking(CancelBookingDTO cancelBookingDTO) {
		log.info("sessionId", "idType", "id", "In cancelBooking method of Booking Service");
		CancelBookingResponseDTO cancelBookingResponseDTO = new CancelBookingResponseDTO();
		AvailibityEntity availableEntity;
		try {
			if (serviceUtil.mandatoryParameterCheckforCancel(cancelBookingDTO)) {
				if (serviceUtil.callGetStatusForCancelRestService(cancelBookingDTO.getPreRegistrationId())) {
					availableEntity = bookingDAO.findByFromTimeAndToTimeAndRegDateAndRegcntrId(
							LocalTime.parse(cancelBookingDTO.getSlotFromTime()),
							LocalTime.parse(cancelBookingDTO.getSlotToTime()),
							LocalDate.parse(cancelBookingDTO.getRegDate()), cancelBookingDTO.getRegistrationCenterId());
					/* Getting Status From Demographic */
					serviceUtil.callGetStatusRestService(cancelBookingDTO.getPreRegistrationId());

					RegistrationBookingEntity bookingEntity = bookingDAO
							.findByPreRegistrationId(cancelBookingDTO.getPreRegistrationId());

					String str = bookingEntity.getRegDate() + " " + bookingEntity.getSlotFromTime();
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
					LocalDateTime bookedDateTime = LocalDateTime.parse(str, formatter);

					if (!serviceUtil.timeSpanCheckForCancle(bookedDateTime)) {
						throw new TimeSpanException(ErrorCodes.PRG_BOOK_RCI_026.getCode(),
								ErrorMessages.BOOKING_STATUS_CANNOT_BE_ALTERED.getMessage());
					}

					/* Deleting the canceled booking */
					bookingDAO.deleteRegistrationEntity(bookingEntity);

					/* Update the status to Canceled in demographic Table */
					serviceUtil.callUpdateStatusRestService(cancelBookingDTO.getPreRegistrationId(),
							StatusCodes.PENDING_APPOINTMENT.getCode());

					/* No. of Availability. update */
					availableEntity.setAvailableKiosks(availableEntity.getAvailableKiosks() + 1);

					bookingDAO.updateAvailibityEntity(availableEntity);

					cancelBookingResponseDTO.setTransactionId(UUIDGeneratorUtil.generateId());
					cancelBookingResponseDTO.setMessage("APPOINTMENT_SUCCESSFULLY_CANCELED");

				}
			}
		} catch (Exception ex) {
			log.error("sessionId", "idType", "id", "In cancelBooking method of Booking Service- " + ex.getMessage());
			new BookingExceptionCatcher().handle(ex);
		}
		return cancelBookingResponseDTO;
	}

	/**
	 * This Method is used to delete the Individual Application and documents
	 * associated with it
	 * 
	 * @param preregId
	 *            pass the preregId of individual
	 * @return response
	 * 
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public MainListResponseDTO<DeleteBookingDTO> deleteBooking(String preregId) {
		log.info("sessionId", "idType", "id", "In deleteIndividual method of pre-registration service ");
		MainListResponseDTO<DeleteBookingDTO> response = new MainListResponseDTO<>();
		List<DeleteBookingDTO> deleteList = new ArrayList<>();
		DeleteBookingDTO deleteDto = new DeleteBookingDTO();
		Map<String, String> requestParamMap = new HashMap<>();
		try {
			requestParamMap.put(RequestCodes.PRE_REGISTRAION_ID.getCode(), preregId);
			if (ValidationUtil.requstParamValidator(requestParamMap)) {
				List<RegistrationBookingEntity> registrationEntityList = bookingDAO.findByPreregistrationId(preregId);
				registrationEntityList.forEach(iterate -> {
					bookingDAO.deleteByPreRegistrationId(preregId);
					deleteDto.setPreRegistrationId(iterate.getBookingPK().getPreregistrationId());
					deleteDto.setDeletedBy(iterate.getCrBy());
					deleteDto.setDeletedDateTime(new Date(System.currentTimeMillis()));
					deleteList.add(deleteDto);

				});
			}
		} catch (Exception ex) {
			log.error("sessionId", "idType", "id", "In deleteBooking method of Booking Service- " + ex.getMessage());
			new BookingExceptionCatcher().handle(ex);
		}
		response.setResTime(serviceUtil.getCurrentResponseTime());
		response.setStatus(Boolean.TRUE);
		response.setResponse(deleteList);
		return response;
	}

	public void checkSlotAvailability(BookingRegistrationDTO newBookingRegistrationDTO) {

		try {
			AvailibityEntity entity = bookingDAO.findByFromTimeAndToTimeAndRegDateAndRegcntrId(
					LocalTime.parse(newBookingRegistrationDTO.getSlotFromTime()),
					LocalTime.parse(newBookingRegistrationDTO.getSlotToTime()),
					LocalDate.parse(newBookingRegistrationDTO.getRegDate()),
					newBookingRegistrationDTO.getRegistrationCenterId());
			log.info("sessionId", "idType", "id", "In checkSlotAvailability method of Booking Service");
			if (entity.getAvailableKiosks() < 1) {
				throw new AvailablityNotFoundException(ErrorCodes.PRG_BOOK_RCI_002.getCode(),
						ErrorMessages.AVAILABILITY_NOT_FOUND_FOR_THE_SELECTED_TIME.getMessage());
			}

		} catch (Exception ex) {
			log.error("sessionId", "idType", "id",
					"In checkSlotAvailability method of Booking Service for Exception- " + ex.getMessage());
			new BookingExceptionCatcher().handle(ex);
		}

	}

	public boolean deleteOldBooking(String preId) {
		try {
			int count = bookingDAO.deleteByPreRegistrationId(preId);
			if (count > 0) {
				log.info("sessionId", "idType", "id", "In deleteOldBooking method of Booking Service");
				return true;
			}
		} catch (Exception ex) {
			log.error("sessionId", "idType", "id",
					"In deleteOldBooking method of Booking Service for Exception- " + ex.getMessage());
			new BookingExceptionCatcher().handle(ex);
		}
		return false;

	}

	public boolean increaseAvailability(BookingRegistrationDTO bookingDto) {
		try {
			AvailibityEntity availableEntity;
			availableEntity = bookingDAO.findByFromTimeAndToTimeAndRegDateAndRegcntrId(
					LocalTime.parse(bookingDto.getSlotFromTime()), LocalTime.parse(bookingDto.getSlotToTime()),
					LocalDate.parse(bookingDto.getRegDate()), bookingDto.getRegistrationCenterId());
			availableEntity.setAvailableKiosks(availableEntity.getAvailableKiosks() + 1);
			log.info("sessionId", "idType", "id", "In increaseAvailability method of Booking Service");

		} catch (Exception ex) {
			log.error("sessionId", "idType", "id",
					"In increaseAvailability method of Booking Service for Exception- " + ex.getMessage());
			new BookingExceptionCatcher().handle(ex);
		}
		return true;

	}

}
