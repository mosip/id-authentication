package io.mosip.preregistration.booking.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import io.mosip.kernel.auth.adapter.model.AuthUserDetails;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.preregistration.booking.codes.RequestCodes;
import io.mosip.preregistration.booking.dto.AvailabilityDto;
import io.mosip.preregistration.booking.dto.BookingRequestDTO;
import io.mosip.preregistration.booking.dto.BookingStatusDTO;
import io.mosip.preregistration.booking.dto.CancelBookingResponseDTO;
import io.mosip.preregistration.booking.dto.DateTimeDto;
import io.mosip.preregistration.booking.dto.MultiBookingRequestDTO;
import io.mosip.preregistration.booking.dto.RegistrationCenterDto;
import io.mosip.preregistration.booking.entity.AvailibityEntity;
import io.mosip.preregistration.booking.entity.RegistrationBookingEntity;
import io.mosip.preregistration.booking.errorcodes.ErrorCodes;
import io.mosip.preregistration.booking.errorcodes.ErrorMessages;
import io.mosip.preregistration.booking.exception.AvailablityNotFoundException;
import io.mosip.preregistration.booking.exception.util.BookingExceptionCatcher;
import io.mosip.preregistration.booking.repository.impl.BookingDAO;
import io.mosip.preregistration.booking.service.util.BookingLock;
import io.mosip.preregistration.booking.service.util.BookingServiceUtil;
import io.mosip.preregistration.core.code.AuditLogVariables;
import io.mosip.preregistration.core.code.EventId;
import io.mosip.preregistration.core.code.EventName;
import io.mosip.preregistration.core.code.EventType;
import io.mosip.preregistration.core.code.StatusCodes;
import io.mosip.preregistration.core.common.dto.AuditRequestDto;
import io.mosip.preregistration.core.common.dto.BookingRegistrationDTO;
import io.mosip.preregistration.core.common.dto.DeleteBookingDTO;
import io.mosip.preregistration.core.common.dto.MainListRequestDTO;
import io.mosip.preregistration.core.common.dto.MainRequestDTO;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.common.dto.PreRegIdsByRegCenterIdResponseDTO;
import io.mosip.preregistration.core.config.LoggerConfiguration;
import io.mosip.preregistration.core.util.AuditLogUtil;
import io.mosip.preregistration.core.util.UUIDGeneratorUtil;
import io.mosip.preregistration.core.util.ValidationUtil;

/**
 * This class provides the service implementation for Booking application.
 * 
 * @author Kishan Rathore
 * @author Jagadishwari
 * @author Ravi C. Balaji
 * @author Sanober Noor
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

	@Value("${version}")
	String versionUrl;

	@Value("${id}")
	String idUrl;

	Map<String, String> requiredRequestMap = new HashMap<>();

	@PostConstruct
	public void setupBookingService() {
		requiredRequestMap.put("id", idUrl);
		requiredRequestMap.put("version", versionUrl);

	}

	private Logger log = LoggerConfiguration.logConfig(BookingService.class);

	@Autowired
	private AuditLogUtil auditLogUtil;

	public AuthUserDetails authUserDetails() {
		return (AuthUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

	/**
	 * It will sync the registration center details
	 * 
	 * @return ResponseDto<String>
	 */
	public MainResponseDTO<String> addAvailability() {
		log.info("sessionId", "idType", "id", "In addAvailability method of Booking Service");
		MainResponseDTO<String> response = new MainResponseDTO<>();
		boolean isSaveSuccess = false;
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
			isSaveSuccess = true;
		} catch (Exception ex) {
			log.error("sessionId", "idType", "id", "In addAvailability method of Booking Service- " + ex.getMessage());
			new BookingExceptionCatcher().handle(ex);
		} finally {
			if (isSaveSuccess) {
				setAuditValues(EventId.PRE_407.toString(), EventName.PERSIST.toString(), EventType.SYSTEM.toString(),
						"Availability for booking successfully saved in the database",
						AuditLogVariables.MULTIPLE_ID.toString(), authUserDetails().getUserId(),
						authUserDetails().getUsername());
			} else {
				setAuditValues(EventId.PRE_405.toString(), EventName.EXCEPTION.toString(), EventType.SYSTEM.toString(),
						"addAvailability failed", AuditLogVariables.NO_ID.toString(), authUserDetails().getUserId(),
						authUserDetails().getUsername());
			}
		}
		response.setResponsetime(serviceUtil.getCurrentResponseTime());
		response.setId(idUrl);
		response.setVersion(versionUrl);
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
		boolean isSaveSuccess = false;

		LocalDate endDate = LocalDate.now().plusDays(displayDays + availabilityOffset);
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
			isSaveSuccess = true;
		} catch (Exception ex) {
			log.error("sessionId", "idType", "id", "In getAvailability method of Booking Service- " + ex.getMessage());
			new BookingExceptionCatcher().handle(ex);
		} finally {
			if (isSaveSuccess) {
				setAuditValues(EventId.PRE_401.toString(), EventName.RETRIEVE.toString(), EventType.SYSTEM.toString(),
						"  Availability retrieved successfully for booking  ", AuditLogVariables.MULTIPLE_ID.toString(),
						authUserDetails().getUserId(), authUserDetails().getUsername());
			} else {
				setAuditValues(EventId.PRE_405.toString(), EventName.EXCEPTION.toString(), EventType.SYSTEM.toString(),
						"Availability failed to get", AuditLogVariables.NO_ID.toString(), authUserDetails().getUserId(),
						authUserDetails().getUsername());
			}
		}
		response.setResponsetime(serviceUtil.getCurrentResponseTime());
		response.setId(idUrl);
		response.setVersion(versionUrl);
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
	public MainResponseDTO<BookingStatusDTO> bookAppointment(MainListRequestDTO<BookingRequestDTO> bookingRequestDTOs,
			String preRegistrationId) {
		log.info("sessionId", "idType", "id", "In bookAppointment method of Booking Service");
		MainResponseDTO<BookingStatusDTO> responseDTO = new MainResponseDTO<>();
		boolean isSaveSuccess = false;
		BookingStatusDTO response = new BookingStatusDTO();
		try {
			if (ValidationUtil.requestValidator(prepareRequestParamMap(bookingRequestDTOs),requiredRequestMap)) {
				for (BookingRequestDTO bookingRequestDTO : bookingRequestDTOs.getRequest()) {

					/* Getting Status From Demographic */
					String preRegStatusCode = serviceUtil.callGetStatusRestService(preRegistrationId);

					if (serviceUtil.mandatoryParameterCheck(preRegistrationId, bookingRequestDTO)) {

						/* Checking the availability of slots */
						checkSlotAvailability(bookingRequestDTO);

						if (preRegStatusCode.equals(StatusCodes.PENDING_APPOINTMENT.getCode())) {

							/* Creating new booking */
							response = book(preRegistrationId, bookingRequestDTO);

						} else if (preRegStatusCode.equals(StatusCodes.BOOKED.getCode())) {

							/* Concatenating Booking date and slot from time */
							RegistrationBookingEntity bookingEntity = bookingDAO
									.findByPreRegistrationId(preRegistrationId);

							BookingRequestDTO oldBooking = new BookingRequestDTO();
							oldBooking.setRegDate(bookingEntity.getRegDate().toString());
							oldBooking.setRegistrationCenterId(bookingEntity.getRegistrationCenterId());
							oldBooking.setSlotFromTime(bookingEntity.getSlotFromTime().toString());
							oldBooking.setSlotToTime(bookingEntity.getSlotToTime().toString());

							String str = bookingEntity.getRegDate() + " " + bookingEntity.getSlotFromTime();
							DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
							LocalDateTime bookedDateTime = LocalDateTime.parse(str, formatter);

							log.info("sessionId", "idType", "id",
									"In bookAppointment method of Booking Service for booking Date Time- "
											+ bookedDateTime);
							/* Time span check for re-book */
							serviceUtil.timeSpanCheckForRebook(bookedDateTime);

							/* Deleting old booking */
							deleteOldBooking(preRegistrationId);

							/* Increase availability */
							increaseAvailability(oldBooking);

							/* Creating new booking */
							response = book(preRegistrationId, bookingRequestDTO);

						} else if (preRegStatusCode.equals(StatusCodes.EXPIRED.getCode())) {

							/* Deleting old booking */
							deleteOldBooking(preRegistrationId);

							/* Creating new booking */
							response = book(preRegistrationId, bookingRequestDTO);
						}

					}

				}
			}
			isSaveSuccess = true;
		} catch (Exception ex) {
			log.error("sessionId", "idType", "id", "In bookAppointment method of Booking Service- " + ex.getMessage());
			new BookingExceptionCatcher().handle(ex);
		} finally {
			if (isSaveSuccess) {
				setAuditValues(EventId.PRE_407.toString(), EventName.PERSIST.toString(), EventType.SYSTEM.toString(),
						"  Appointment booked successfully    ", AuditLogVariables.MULTIPLE_ID.toString(),
						authUserDetails().getUserId(), authUserDetails().getUsername());
			} else {
				setAuditValues(EventId.PRE_405.toString(), EventName.EXCEPTION.toString(), EventType.SYSTEM.toString(),
						"Appointment failed to book", AuditLogVariables.NO_ID.toString(), authUserDetails().getUserId(),
						authUserDetails().getUsername());
			}
		}
		responseDTO.setResponsetime(serviceUtil.getCurrentResponseTime());
		responseDTO.setId(idUrl);
		responseDTO.setVersion(versionUrl);
		responseDTO.setResponse(response);
		return responseDTO;
	}
	
	/**
	 * This method will book the multiple appointments.
	 * 
	 * @param multiBookingRequestDTO
	 * @return response with status code
	 * @throws java.text.ParseException
	 */
	/**
	 * @param multiBookingRequestDTOs
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public MainResponseDTO<List<BookingStatusDTO>> bookMultiAppointment(
			MainListRequestDTO<MultiBookingRequestDTO> bookingRequestDTOs) {
		log.info("sessionId", "idType", "id", "In bookMultiAppointment method of Booking Service");
		MainResponseDTO<List<BookingStatusDTO>> responseDTO = new MainResponseDTO<>();
		boolean isSaveSuccess = false;
		List<BookingStatusDTO> respList = new ArrayList<>();
		try {
			if (ValidationUtil.requestValidator(prepareRequestParamMap(bookingRequestDTOs),requiredRequestMap)) {
				for (MultiBookingRequestDTO bookingRequestDTO : bookingRequestDTOs.getRequest()) {
					/* Getting Status From Demographic */
					String preRegStatusCode = serviceUtil
							.callGetStatusRestService(bookingRequestDTO.getPreRegistrationId());

					//Taking one booking request from multiple
					BookingRequestDTO bookingRequest=new BookingRequestDTO();
					bookingRequest.setRegDate(bookingRequestDTO.getRegDate());
					bookingRequest.setRegistrationCenterId(bookingRequestDTO.getRegistrationCenterId());
					bookingRequest.setSlotFromTime(bookingRequestDTO.getSlotFromTime());
					bookingRequest.setSlotToTime(bookingRequestDTO.getSlotToTime());

					if (serviceUtil.mandatoryParameterCheck(bookingRequestDTO.getPreRegistrationId(),
							bookingRequest)) {

						/* Checking the availability of slots */
						checkSlotAvailability(bookingRequest);

						if (preRegStatusCode.equals(StatusCodes.PENDING_APPOINTMENT.getCode())) {
							
							

							/* Creating new booking */
							respList.add(book(bookingRequestDTO.getPreRegistrationId(), bookingRequest));

						} else if (preRegStatusCode.equals(StatusCodes.BOOKED.getCode())) {

							/* Concatenating Booking date and slot from time */
							RegistrationBookingEntity bookingEntity = bookingDAO
									.findByPreRegistrationId(bookingRequestDTO.getPreRegistrationId());

							String str = bookingEntity.getRegDate() + " " + bookingEntity.getSlotFromTime();
							DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
							LocalDateTime bookedDateTime = LocalDateTime.parse(str, formatter);

							log.info("sessionId", "idType", "id",
									"In bookMultiAppointment method of Booking Service for booking Date Time- "
											+ bookedDateTime);
							/* Time span check for re-book */
							serviceUtil.timeSpanCheckForRebook(bookedDateTime);

							/* Deleting old booking */
							deleteOldBooking(bookingRequestDTO.getPreRegistrationId());

							/* Increase availability */
							increaseAvailability(bookingRequest);

							/* Creating new booking */
							respList.add(book(bookingRequestDTO.getPreRegistrationId(), bookingRequest));

						} else if (preRegStatusCode.equals(StatusCodes.EXPIRED.getCode())) {

							/* Deleting old booking */
							deleteOldBooking(bookingRequestDTO.getPreRegistrationId());
							
							/* Creating new booking */
							respList.add(book(bookingRequestDTO.getPreRegistrationId(), bookingRequest));
						}

					}

				}
			}
			isSaveSuccess = true;
		} catch (Exception ex) {
			log.error("sessionId", "idType", "id", "In bookMultiAppointment method of Booking Service- " + ex.getMessage());
			new BookingExceptionCatcher().handle(ex);
		} finally {
			if (isSaveSuccess) {
				setAuditValues(EventId.PRE_407.toString(), EventName.PERSIST.toString(), EventType.SYSTEM.toString(),
						"  Appointment booked successfully    ", AuditLogVariables.MULTIPLE_ID.toString(),
						authUserDetails().getUserId(), authUserDetails().getUsername());
			} else {
				setAuditValues(EventId.PRE_405.toString(), EventName.EXCEPTION.toString(), EventType.SYSTEM.toString(),
						"Appointment failed to book", AuditLogVariables.NO_ID.toString(), authUserDetails().getUserId(),
						authUserDetails().getUsername());
			}
		}
		responseDTO.setResponsetime(serviceUtil.getCurrentResponseTime());
		responseDTO.setId(idUrl);
		responseDTO.setVersion(versionUrl);
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
			/* Checking Status From Demographic */
			serviceUtil.callGetStatusRestService(preRegID);
			entity = bookingDAO.findByPreRegistrationId(preRegID);

			bookingRegistrationDTO.setRegDate(entity.getRegDate().toString());
			bookingRegistrationDTO.setRegistrationCenterId(entity.getRegistrationCenterId());
			bookingRegistrationDTO.setSlotFromTime(entity.getSlotFromTime().toString());
			bookingRegistrationDTO.setSlotToTime(entity.getSlotToTime().toString());
			responseDto.setResponse(bookingRegistrationDTO);

			responseDto.setErrors(null);
			responseDto.setId(idUrl);
			responseDto.setVersion(versionUrl);
			responseDto.setResponsetime(serviceUtil.getCurrentResponseTime());

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
	public MainResponseDTO<CancelBookingResponseDTO> cancelAppointment(String preRegistrationId) {
		log.info("sessionId", "idType", "id", "In cancelAppointment method of Booking Service");
		MainResponseDTO<CancelBookingResponseDTO> responseDto = new MainResponseDTO<>();

		responseDto.setResponse(cancelBooking(preRegistrationId));

		responseDto.setResponsetime(serviceUtil.getCurrentResponseTime());
		return responseDto;
	}

	/**
	 * 
	 * This booking API will be called by bookAppointment.
	 * 
	 * @param preRegistrationId
	 * @param bookingRegistrationDTO
	 * @return BookingStatusDTO
	 */
	public BookingStatusDTO book(String preRegistrationId, BookingRequestDTO bookingRequestDTO) {
		log.info("sessionId", "idType", "id", "In book method of Booking Service");
		BookingStatusDTO bookingStatusDTO = new BookingStatusDTO();
		try {
			BookingLock bookingLock = new BookingLock(bookingRequestDTO.getRegistrationCenterId(),
					bookingRequestDTO.getRegDate(), bookingRequestDTO.getSlotFromTime());
			AvailibityEntity availableEntity;

			synchronized (bookingLock) {
				availableEntity = bookingDAO.findByFromTimeAndToTimeAndRegDateAndRegcntrId(
						LocalTime.parse(bookingRequestDTO.getSlotFromTime()),
						LocalTime.parse(bookingRequestDTO.getSlotToTime()),
						LocalDate.parse(bookingRequestDTO.getRegDate()), bookingRequestDTO.getRegistrationCenterId());
				if (serviceUtil.isKiosksAvailable(availableEntity)) {
					/* Updating booking */
					bookingDAO.saveRegistrationEntityForBooking(
							serviceUtil.bookingEntitySetter(preRegistrationId, bookingRequestDTO));
					/* Reduce Availability */
					availableEntity.setAvailableKiosks(availableEntity.getAvailableKiosks() - 1);
					bookingDAO.updateAvailibityEntity(availableEntity);
					/* Updating demographic */
					serviceUtil.callUpdateStatusRestService(preRegistrationId, StatusCodes.BOOKED.getCode());
					bookingStatusDTO.setPreRegistrationId(preRegistrationId);
					bookingStatusDTO.setBookingStatus(StatusCodes.BOOKED.getCode());
					bookingStatusDTO.setBookingMessage("Appointment booked successfully");
					
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
	public CancelBookingResponseDTO cancelBooking(String preRegistrationId) {
		log.info("sessionId", "idType", "id", "In cancelBooking method of Booking Service");
		CancelBookingResponseDTO cancelBookingResponseDTO = new CancelBookingResponseDTO();
		boolean isSaveSuccess = false;
		AvailibityEntity availableEntity;
		try {
			if (serviceUtil.mandatoryParameterCheckforCancel( preRegistrationId)) {
				if (serviceUtil.callGetStatusForCancelRestService(preRegistrationId)) {
					/* Getting Booking details */
					RegistrationBookingEntity bookingEntity = bookingDAO.findByPreRegistrationId(preRegistrationId);
					
					availableEntity = bookingDAO.findByFromTimeAndToTimeAndRegDateAndRegcntrId(
							bookingEntity.getSlotFromTime(),
							bookingEntity.getSlotToTime(),
							bookingEntity.getRegDate(), bookingEntity.getRegistrationCenterId());
					/* Getting Status From Demographic */
					serviceUtil.callGetStatusRestService(preRegistrationId);

					String str = bookingEntity.getRegDate() + " " + bookingEntity.getSlotFromTime();
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
					LocalDateTime bookedDateTime = LocalDateTime.parse(str, formatter);

					serviceUtil.timeSpanCheckForCancle(bookedDateTime);

					/* Deleting the canceled booking */
					bookingDAO.deleteRegistrationEntity(bookingEntity);

					/* Update the status to Canceled in demographic Table */
					serviceUtil.callUpdateStatusRestService(preRegistrationId,
							StatusCodes.PENDING_APPOINTMENT.getCode());

					/* No. of Availability. update */
					availableEntity.setAvailableKiosks(availableEntity.getAvailableKiosks() + 1);

					bookingDAO.updateAvailibityEntity(availableEntity);

					cancelBookingResponseDTO.setTransactionId(UUIDGeneratorUtil.generateId());
					cancelBookingResponseDTO.setMessage("Appointment cancelled successfully");

				}
			}
			isSaveSuccess = true;
		} catch (Exception ex) {
			log.error("sessionId", "idType", "id", "In cancelBooking method of Booking Service- " + ex.getMessage());
			new BookingExceptionCatcher().handle(ex);
		} finally {
			if (isSaveSuccess) {
				setAuditValues(EventId.PRE_402.toString(), EventName.UPDATE.toString(), EventType.SYSTEM.toString(),
						"  Booking cancel successfully ", AuditLogVariables.MULTIPLE_ID.toString(),
						authUserDetails().getUserId(), authUserDetails().getUsername());
			} else {
				setAuditValues(EventId.PRE_405.toString(), EventName.EXCEPTION.toString(), EventType.SYSTEM.toString(),
						" Booking failed to cancel ", AuditLogVariables.NO_ID.toString(), authUserDetails().getUserId(),
						authUserDetails().getUsername());
			}
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
	public MainResponseDTO<DeleteBookingDTO> deleteBooking(String preregId) {
		log.info("sessionId", "idType", "id", "In deleteIndividual method of pre-registration service ");
		MainResponseDTO<DeleteBookingDTO> response = new MainResponseDTO<>();
		DeleteBookingDTO deleteDto = new DeleteBookingDTO();
		Map<String, String> requestParamMap = new HashMap<>();
		AvailibityEntity availableEntity;
		try {
			requestParamMap.put(RequestCodes.PRE_REGISTRAION_ID.getCode(), preregId);
			if (ValidationUtil.requstParamValidator(requestParamMap)) {
				RegistrationBookingEntity registrationEntityList = bookingDAO.findByPreRegistrationId(preregId);
				
					bookingDAO.deleteByPreRegistrationId(preregId);
					availableEntity = bookingDAO.findByFromTimeAndToTimeAndRegDateAndRegcntrId(
							LocalTime.parse(registrationEntityList.getSlotFromTime().toString()),
							LocalTime.parse(registrationEntityList.getSlotToTime().toString()),
							LocalDate.parse(registrationEntityList.getRegDate().toString()), registrationEntityList.getRegistrationCenterId());
					/* No. of Availability. update */
					availableEntity.setAvailableKiosks(availableEntity.getAvailableKiosks() + 1);

					/* Updating slot in DB*/
					bookingDAO.updateAvailibityEntity(availableEntity);
					
					deleteDto.setPreRegistrationId(registrationEntityList.getBookingPK().getPreregistrationId());
					deleteDto.setDeletedBy(registrationEntityList.getCrBy());
					deleteDto.setDeletedDateTime(new Date(System.currentTimeMillis()));

				
			}
		} catch (Exception ex) {
			log.error("sessionId", "idType", "id", "In deleteBooking method of Booking Service- " + ex.getMessage());
			new BookingExceptionCatcher().handle(ex);
		}

		response.setResponsetime(serviceUtil.getCurrentResponseTime());
		response.setId(idUrl);
		response.setVersion(versionUrl);
		response.setResponse(deleteDto);
		return response;
	}

	public void checkSlotAvailability(BookingRequestDTO bookingRequestDTO) {
		try {
			AvailibityEntity entity = bookingDAO.findByFromTimeAndToTimeAndRegDateAndRegcntrId(
					LocalTime.parse(bookingRequestDTO.getSlotFromTime()),
					LocalTime.parse(bookingRequestDTO.getSlotToTime()), LocalDate.parse(bookingRequestDTO.getRegDate()),
					bookingRequestDTO.getRegistrationCenterId());

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

	public boolean increaseAvailability(BookingRequestDTO oldBooking) {
		try {
			AvailibityEntity availableEntity;
			availableEntity = bookingDAO.findByFromTimeAndToTimeAndRegDateAndRegcntrId(
					LocalTime.parse(oldBooking.getSlotFromTime()), LocalTime.parse(oldBooking.getSlotToTime()),
					LocalDate.parse(oldBooking.getRegDate()), oldBooking.getRegistrationCenterId());
			availableEntity.setAvailableKiosks(availableEntity.getAvailableKiosks() + 1);
			bookingDAO.updateAvailibityEntity(availableEntity);
			log.info("sessionId", "idType", "id", "In increaseAvailability method of Booking Service");

		} catch (Exception ex) {
			log.error("sessionId", "idType", "id",
					"In increaseAvailability method of Booking Service for Exception- " + ex.getMessage());
			new BookingExceptionCatcher().handle(ex);
		}
		return true;

	}

	/**
	 * This method is used to audit all the booking events
	 * 
	 * @param eventId
	 * @param eventName
	 * @param eventType
	 * @param description
	 * @param idType
	 */
	public void setAuditValues(String eventId, String eventName, String eventType, String description, String idType,
			String userId, String userName) {
		AuditRequestDto auditRequestDto = new AuditRequestDto();
		auditRequestDto.setEventId(eventId);
		auditRequestDto.setEventName(eventName);
		auditRequestDto.setEventType(eventType);
		auditRequestDto.setDescription(description);
		auditRequestDto.setId(idType);
		auditRequestDto.setSessionUserId(userId);
		auditRequestDto.setSessionUserName(userName);
		auditRequestDto.setModuleId(AuditLogVariables.BOOK.toString());
		auditRequestDto.setModuleName(AuditLogVariables.BOOKING_SERVICE.toString());
		auditLogUtil.saveAuditDetails(auditRequestDto);
	}

	/**
	 * This Method is used to retrieve booked PreIds by date and regCenterId**
	 * 
	 * @param fromDate
	 *            pass fromDate*
	 * @param toDate
	 *            pass toDate*@return response List of Booked preRegIds
	 ***/

	public MainResponseDTO<PreRegIdsByRegCenterIdResponseDTO> getBookedPreRegistrationByDate(String fromDateStr,
			String toDateStr, String regCenterId) {
		log.info("sessionId", "idType", "id", "In getBookedPreRegistrationByDate method of booking service ");
		MainResponseDTO<PreRegIdsByRegCenterIdResponseDTO> response = new MainResponseDTO<>();
		try {

			if (toDateStr == null || toDateStr.isEmpty()) {
				toDateStr = fromDateStr;
			}

			DateTimeFormatter parseFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

			LocalDate fromDate = LocalDate.parse(fromDateStr, parseFormatter);
			LocalDate toDate = LocalDate.parse(toDateStr, parseFormatter);

			LocalDateTime fromLocaldate = fromDate.atStartOfDay();
			LocalDateTime toLocaldate = toDate.atTime(23, 59, 59);

			List<String> details = bookingDAO.findByBookingDateBetweenAndRegCenterId(fromLocaldate, toLocaldate,
					regCenterId);
			PreRegIdsByRegCenterIdResponseDTO responseDTO = new PreRegIdsByRegCenterIdResponseDTO();
			responseDTO.setPreRegistrationIds(details);
			responseDTO.setRegistrationCenterId(regCenterId);

			response.setResponse(responseDTO);
		} catch (

		Exception ex) {
			log.error("sessionId", "idType", "id",
					"In getPreRegistrationByDate method of pre-registration service - " + ex.getMessage());
			new BookingExceptionCatcher().handle(ex);
		}
		response.setResponsetime(serviceUtil.getCurrentResponseTime());
		response.setId(idUrl);
		response.setVersion(versionUrl);
		response.setErrors(null);
		return response;
	}

	/**
	 * This method is used to add the initial request values into a map for input
	 * validations.
	 * 
	 * @param MainRequestDTO
	 *            pass requestDTO
	 * @return a map for request input validation
	 */
	public Map<String, String> prepareRequestParamMap(MainListRequestDTO<?> requestDTO) {
		Map<String, String> inputValidation = new HashMap<>();
		inputValidation.put(RequestCodes.id.getCode(), requestDTO.getId());
		inputValidation.put(RequestCodes.version.getCode(), requestDTO.getVersion());
		LocalDate date = requestDTO.getRequesttime().toInstant().atZone(ZoneId.of("UTC")).toLocalDate();
		inputValidation.put(RequestCodes.requesttime.getCode(), date.toString());
		inputValidation.put(RequestCodes.request.getCode(), requestDTO.getRequest().toString());
		return inputValidation;
	}
	
	public Map<String, String> prepareRequestParamMap(MainRequestDTO<?> requestDTO) {
		Map<String, String> inputValidation = new HashMap<>();
		inputValidation.put(RequestCodes.id.getCode(), requestDTO.getId());
		inputValidation.put(RequestCodes.version.getCode(), requestDTO.getVersion());
		LocalDate date = requestDTO.getRequesttime().toInstant().atZone(ZoneId.of("UTC")).toLocalDate();
		inputValidation.put(RequestCodes.requesttime.getCode(), date.toString());
		inputValidation.put(RequestCodes.request.getCode(), requestDTO.getRequest().toString());
		return inputValidation;
	}
}
