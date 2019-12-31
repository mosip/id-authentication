package io.mosip.preregistration.booking.serviceimpl.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import io.mosip.kernel.auth.adapter.model.AuthUserDetails;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.preregistration.booking.serviceimpl.codes.RequestCodes;
import io.mosip.preregistration.booking.serviceimpl.dto.AvailabilityDto;
import io.mosip.preregistration.booking.serviceimpl.dto.BookingRequestDTO;
import io.mosip.preregistration.booking.serviceimpl.dto.BookingStatus;
import io.mosip.preregistration.booking.serviceimpl.dto.BookingStatusDTO;
import io.mosip.preregistration.booking.serviceimpl.dto.DateTimeDto;
import io.mosip.preregistration.booking.serviceimpl.dto.MultiBookingRequest;
import io.mosip.preregistration.booking.serviceimpl.dto.MultiBookingRequestDTO;
import io.mosip.preregistration.booking.serviceimpl.dto.RegistrationCenterDto;
import io.mosip.preregistration.booking.serviceimpl.entity.AvailibityEntity;
import io.mosip.preregistration.booking.serviceimpl.errorcodes.ErrorCodes;
import io.mosip.preregistration.booking.serviceimpl.errorcodes.ErrorMessages;
import io.mosip.preregistration.booking.serviceimpl.exception.AvailablityNotFoundException;
import io.mosip.preregistration.booking.serviceimpl.exception.RecordNotFoundException;
import io.mosip.preregistration.booking.serviceimpl.exception.util.BookingExceptionCatcher;
import io.mosip.preregistration.booking.serviceimpl.repository.impl.BookingDAO;
import io.mosip.preregistration.booking.serviceimpl.service.util.BookingLock;
import io.mosip.preregistration.booking.serviceimpl.service.util.BookingServiceUtil;
import io.mosip.preregistration.core.code.AuditLogVariables;
import io.mosip.preregistration.core.code.EventId;
import io.mosip.preregistration.core.code.EventName;
import io.mosip.preregistration.core.code.EventType;
import io.mosip.preregistration.core.code.StatusCodes;
import io.mosip.preregistration.core.common.dto.AuditRequestDto;
import io.mosip.preregistration.core.common.dto.BookingRegistrationDTO;
import io.mosip.preregistration.core.common.dto.CancelBookingResponseDTO;
import io.mosip.preregistration.core.common.dto.DeleteBookingDTO;
import io.mosip.preregistration.core.common.dto.MainRequestDTO;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.common.dto.PreRegIdsByRegCenterIdResponseDTO;
import io.mosip.preregistration.core.common.entity.RegistrationBookingEntity;
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
 * @since 1.0.0
 *
 */
@Component
public class BookingService implements BookingServiceIntf {

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

	@Autowired
	private BookingLock bookingLock;

	@Value("${version}")
	String versionUrl;

	@Value("${mosip.preregistration.booking.availability.sync.id}")
	String idUrlSync;

	@Value("${mosip.preregistration.booking.id.book}")
	String idUrlBookAppointment;

	@Value("${mosip.preregistration.booking.fetch.booking.id}")
	String idUrlFetch;

	@Value("${mosip.preregistration.booking.cancel.id}")
	String idUrlCancel;

	@Value("${mosip.preregistration.booking.delete.id}")
	String idUrlDelete;

	@Value("${mosip.preregistration.booking.fetch.availability.id}")
	String idUrlAvailability;

	@Value("${mosip.preregistration.booking.fetchPreidByDate.id}")
	String idUrlBookingByDate;

	@Value("${mosip.preregistration.booking.availability.increase.id}")
	String idUrlIncreaseAvailability;

	@Value("${mosip.preregistration.booking.availability.check.id}")
	String idUrlCheckSlotAvailability;

	@Value("${mosip.preregistration.booking.delete.old.id}")
	String idUrlDeleteOld;

	@Value("${mosip.primary-language}")
	String primaryLang;

	@PostConstruct
	public void setupBookingService() {

	}

	private Logger log = LoggerConfiguration.logConfig(BookingService.class);

	@Autowired
	private AuditLogUtil auditLogUtil;

	public AuthUserDetails authUserDetails() {
		return (AuthUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
	}

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.preregistration.booking.serviceimpl.service.BookingServiceIntf#
	 * getAvailability(java.lang.String)
	 */
	@Override
	public MainResponseDTO<AvailabilityDto> getAvailability(String regID) {
		log.info("sessionId", "idType", "id", "In getAvailability method of Booking Service");
		MainResponseDTO<AvailabilityDto> response = new MainResponseDTO<>();
		response.setId(idUrlAvailability);
		response.setVersion(versionUrl);
		boolean isSaveSuccess = false;

		LocalDate endDate = LocalDate.now().plusDays(displayDays + availabilityOffset).minusDays(1);
		LocalDate fromDate = LocalDate.now().plusDays(availabilityOffset);
		AvailabilityDto availability = new AvailabilityDto();
		try {
			if (serviceUtil.isValidRegCenter(regID)) {
				int noOfHoliday = 0;
				List<AvailibityEntity> availableEntity = bookingDAO.findAvailability(regID, fromDate, endDate);
				List<DateTimeDto> dateTimeList = new ArrayList<>();
				if (availableEntity == null) {
					throw new RecordNotFoundException(ErrorCodes.PRG_BOOK_RCI_015.getCode(),
							ErrorMessages.NO_TIME_SLOTS_ASSIGNED_TO_THAT_REG_CENTER.getMessage());
				}

				noOfHoliday = getSlot(dateTimeList, noOfHoliday, availableEntity);
				while (noOfHoliday > 0) {
					fromDate = endDate.plusDays(1);
					endDate = endDate.plusDays(noOfHoliday);
					availableEntity = bookingDAO.findAvailability(regID, fromDate, endDate);
					if (availableEntity == null) {
						log.info("sessionId", "idType", "id",
								"There no slots available in case of holidays present in the given date range and no of Holidays is  "
										+ noOfHoliday);
						noOfHoliday = 0;
					} else {
						noOfHoliday = 0;
						noOfHoliday = getSlot(dateTimeList, noOfHoliday, availableEntity);
					}
				}
				availability.setCenterDetails(dateTimeList);
				availability.setRegCenterId(regID);
				isSaveSuccess = true;
			}
		} catch (Exception ex) {
			log.debug("sessionId", "idType", "id", ExceptionUtils.getStackTrace(ex));
			log.error("sessionId", "idType", "id", "In getAvailability method of Booking Service- " + ex.getMessage());
			new BookingExceptionCatcher().handle(ex, response);
		} finally {
			if (isSaveSuccess) {
				setAuditValues(EventId.PRE_401.toString(), EventName.RETRIEVE.toString(), EventType.BUSINESS.toString(),
						"Availability retrieved successfully for booking", AuditLogVariables.MULTIPLE_ID.toString(),
						authUserDetails().getUserId(), authUserDetails().getUsername(), regID);
			} else {
				setAuditValues(EventId.PRE_405.toString(), EventName.EXCEPTION.toString(), EventType.SYSTEM.toString(),
						"Availability failed to get", AuditLogVariables.NO_ID.toString(), authUserDetails().getUserId(),
						authUserDetails().getUsername(), regID);
			}
		}
		response.setResponsetime(serviceUtil.getCurrentResponseTime());
		response.setResponse(availability);
		return response;
	}

	public int getSlot(List<DateTimeDto> dateTimeList, int noOfHoliday, List<AvailibityEntity> availableEntity) {
		Map<LocalDate, List<AvailibityEntity>> result = null;
		result = availableEntity.stream().collect(
				Collectors.groupingBy(AvailibityEntity::getRegDate, () -> new TreeMap<>(), Collectors.toList()));
		for (Entry<LocalDate, List<AvailibityEntity>> entity : result.entrySet()) {
			DateTimeDto dateTime = new DateTimeDto();
			noOfHoliday = noOfHoliday
					+ serviceUtil.slotSetter(entity.getKey(), dateTimeList, dateTime, entity.getValue());
		}
		return noOfHoliday;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.preregistration.booking.serviceimpl.service.BookingServiceIntf#
	 * bookAppointment(io.mosip.preregistration.core.common.dto.MainRequestDTO,
	 * java.lang.String)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public MainResponseDTO<BookingStatusDTO> bookAppointment(MainRequestDTO<BookingRequestDTO> bookingRequestDTOs,
			String preRegistrationId) {
		bookingLock.setDate(bookingRequestDTOs.getRequest().getRegDate());
		bookingLock.setRegistrationCenter(bookingRequestDTOs.getRequest().getRegDate());
		bookingLock.setTimeslot(bookingRequestDTOs.getRequest().getSlotFromTime());
		log.info("sessionId", "idType", "id", "In bookAppointment method of Booking Service");
		MainResponseDTO<BookingStatusDTO> responseDTO = new MainResponseDTO<>();
		responseDTO.setId(idUrlBookAppointment);
		responseDTO.setVersion(versionUrl);
		boolean isSaveSuccess = false;
		synchronized (bookingLock) {

			log.info("Sync block :", preRegistrationId, " Start", "");

			BookingStatusDTO response = new BookingStatusDTO();
			try {

				BookingRequestDTO bookingRequestDTO = bookingRequestDTOs.getRequest();
				Map<String, String> dateMap = new HashMap<>();
				dateMap.put(RequestCodes.REG_DATE.getCode(), bookingRequestDTO.getRegDate());
				dateMap.put(RequestCodes.FROM_SLOT_TIME.getCode(), bookingRequestDTO.getSlotFromTime());
				dateMap.put(RequestCodes.PRE_REGISTRAION_ID.getCode(), preRegistrationId);
				if (serviceUtil.validateAppointmentDate(dateMap)) {
					/* Getting Status From Demographic */
					String preRegStatusCode = serviceUtil.getDemographicStatus(preRegistrationId);

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
							serviceUtil.timeSpanCheckForRebook(bookedDateTime, bookingRequestDTOs.getRequesttime());

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
				isSaveSuccess = true;
			} catch (Exception ex) {
				log.debug("sessionId", "idType", "id", ExceptionUtils.getStackTrace(ex));
				log.error("sessionId", "idType", "id",
						"In bookAppointment method of Booking Service- " + ex.getMessage());
				new BookingExceptionCatcher().handle(ex, responseDTO);
			} finally {
				if (isSaveSuccess) {
					setAuditValues(EventId.PRE_407.toString(), EventName.PERSIST.toString(),
							EventType.BUSINESS.toString(), "Appointment booked successfully",
							AuditLogVariables.MULTIPLE_ID.toString(), authUserDetails().getUserId(),
							authUserDetails().getUsername(), bookingRequestDTOs.getRequest().getRegistrationCenterId());
				} else {
					setAuditValues(EventId.PRE_405.toString(), EventName.EXCEPTION.toString(),
							EventType.SYSTEM.toString(), "Appointment failed to book",
							AuditLogVariables.NO_ID.toString(), authUserDetails().getUserId(),
							authUserDetails().getUsername(), bookingRequestDTOs.getRequest().getRegistrationCenterId());
				}
			}
			responseDTO.setResponsetime(serviceUtil.getCurrentResponseTime());
			responseDTO.setResponse(response);
			log.info("Sync block :", preRegistrationId, " End", "");
			return responseDTO;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.preregistration.booking.serviceimpl.service.BookingServiceIntf#
	 * bookMultiAppointment(io.mosip.preregistration.core.common.dto.MainRequestDTO)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public MainResponseDTO<BookingStatus> bookMultiAppointment(MainRequestDTO<MultiBookingRequest> bookingRequestDTOs) {

		log.info("sessionId", "idType", "id", "In bookMultiAppointment method of Booking Service");

		bookingLock.setDate(bookingRequestDTOs.getRequest().getBookingRequest().get(0).getRegDate());
		bookingLock.setRegistrationCenter(bookingRequestDTOs.getRequest().getBookingRequest().get(0).getRegDate());
		bookingLock.setTimeslot(bookingRequestDTOs.getRequest().getBookingRequest().get(0).getSlotFromTime());

		synchronized (bookingLock) {
			log.info("Sync block :", bookingRequestDTOs.getRequest().getBookingRequest().get(0).getPreRegistrationId(),
					" Start", "");
			MainResponseDTO<BookingStatus> responseDTO = new MainResponseDTO<>();
			BookingStatus response = new BookingStatus();
			responseDTO.setId(idUrlBookAppointment);
			responseDTO.setVersion(versionUrl);
			boolean isSaveSuccess = false;
			List<BookingStatusDTO> respList = new ArrayList<>();
			try {
				for (MultiBookingRequestDTO bookingRequestDTO : bookingRequestDTOs.getRequest().getBookingRequest()) {
					Map<String, String> dateMap = new HashMap<>();
					dateMap.put(RequestCodes.REG_DATE.getCode(), bookingRequestDTO.getRegDate());
					dateMap.put(RequestCodes.FROM_SLOT_TIME.getCode(), bookingRequestDTO.getSlotFromTime());
					dateMap.put(RequestCodes.PRE_REGISTRAION_ID.getCode(), bookingRequestDTO.getPreRegistrationId());
					if (serviceUtil.validateAppointmentDate(dateMap)) {
						/* Getting Status From Demographic */
						String preRegStatusCode = serviceUtil
								.getDemographicStatus(bookingRequestDTO.getPreRegistrationId());

						// Taking one booking request from multiple
						BookingRequestDTO bookingRequest = new BookingRequestDTO();
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
								BookingRequestDTO oldBooking = new BookingRequestDTO();
								oldBooking.setRegDate(bookingEntity.getRegDate().toString());
								oldBooking.setRegistrationCenterId(bookingEntity.getRegistrationCenterId());
								oldBooking.setSlotFromTime(bookingEntity.getSlotFromTime().toString());
								oldBooking.setSlotToTime(bookingEntity.getSlotToTime().toString());

								String str = bookingEntity.getRegDate() + " " + bookingEntity.getSlotFromTime();
								DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
								LocalDateTime bookedDateTime = LocalDateTime.parse(str, formatter);

								log.info("sessionId", "idType", "id",
										"In bookMultiAppointment method of Booking Service for booking Date Time- "
												+ bookedDateTime);
								/* Time span check for re-book */
								serviceUtil.timeSpanCheckForRebook(bookedDateTime, bookingRequestDTOs.getRequesttime());

								/* Deleting old booking */
								deleteOldBooking(bookingRequestDTO.getPreRegistrationId());

								/* Increase availability */
								increaseAvailability(oldBooking);

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
				log.debug("sessionId", "idType", "id", ExceptionUtils.getStackTrace(ex));
				log.error("sessionId", "idType", "id",
						"In bookMultiAppointment method of Booking Service- " + ex.getMessage());
				new BookingExceptionCatcher().handle(ex, responseDTO);
			} finally {
				if (isSaveSuccess) {
					setAuditValues(EventId.PRE_407.toString(), EventName.PERSIST.toString(),
							EventType.BUSINESS.toString(), "Appointment booked successfully",
							AuditLogVariables.MULTIPLE_ID.toString(), authUserDetails().getUserId(),
							authUserDetails().getUsername(),
							bookingRequestDTOs.getRequest().getBookingRequest().get(0).getRegistrationCenterId());
				} else {
					setAuditValues(EventId.PRE_405.toString(), EventName.EXCEPTION.toString(),
							EventType.SYSTEM.toString(), "Appointment failed to book",
							AuditLogVariables.NO_ID.toString(), authUserDetails().getUserId(),
							authUserDetails().getUsername(),
							bookingRequestDTOs.getRequest().getBookingRequest().get(0).getRegistrationCenterId());
				}
			}
			responseDTO.setResponsetime(serviceUtil.getCurrentResponseTime());
			response.setBookingStatusResponse(respList);
			responseDTO.setResponse(response);
			log.info("Sync block :", bookingRequestDTOs.getRequest().getBookingRequest().get(0).getPreRegistrationId(),
					" End", "");
			return responseDTO;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.preregistration.booking.serviceimpl.service.BookingServiceIntf#
	 * getAppointmentDetails(java.lang.String)
	 */
	@Override
	public MainResponseDTO<BookingRegistrationDTO> getAppointmentDetails(String preRegID) {
		log.info("sessionId", "idType", "id", "In getAppointmentDetails method of Booking Service");
		BookingRegistrationDTO bookingRegistrationDTO = new BookingRegistrationDTO();
		MainResponseDTO<BookingRegistrationDTO> responseDto = new MainResponseDTO<>();
		responseDto.setId(idUrlFetch);
		responseDto.setVersion(versionUrl);
		RegistrationBookingEntity entity = null;
		try {
			/* Checking Status From Demographic */
			serviceUtil.getDemographicStatus(preRegID);
			entity = bookingDAO.findByPreRegistrationId(preRegID);

			bookingRegistrationDTO.setRegDate(entity.getRegDate().toString());
			bookingRegistrationDTO.setRegistrationCenterId(entity.getRegistrationCenterId());
			bookingRegistrationDTO.setSlotFromTime(entity.getSlotFromTime().toString());
			bookingRegistrationDTO.setSlotToTime(entity.getSlotToTime().toString());
			responseDto.setResponse(bookingRegistrationDTO);

			responseDto.setErrors(null);
			responseDto.setResponsetime(serviceUtil.getCurrentResponseTime());

		} catch (Exception ex) {
			log.debug("sessionId", "idType", "id", ExceptionUtils.getStackTrace(ex));
			log.error("sessionId", "idType", "id",
					"In getAppointmentDetails method of Booking Service- " + ex.getMessage());
			new BookingExceptionCatcher().handle(ex, responseDto);
		}

		return responseDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.preregistration.booking.serviceimpl.service.BookingServiceIntf#
	 * cancelAppointment(java.lang.String)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public MainResponseDTO<CancelBookingResponseDTO> cancelAppointment(String preRegistrationId) {
		log.info("sessionId", "idType", "id", "In cancelAppointment method of Booking Service");
		MainResponseDTO<CancelBookingResponseDTO> responseDto = new MainResponseDTO<>();
		boolean isBatchUser = false;
		responseDto.setId(idUrlCancel);
		responseDto.setVersion(versionUrl);
		responseDto.setResponse(cancelBooking(preRegistrationId, isBatchUser));

		responseDto.setResponsetime(serviceUtil.getCurrentResponseTime());

		return responseDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.preregistration.booking.serviceimpl.service.BookingServiceIntf#
	 * cancelAppointmentBatch(java.lang.String)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public MainResponseDTO<CancelBookingResponseDTO> cancelAppointmentBatch(String preRegistrationId) {
		log.info("sessionId", "idType", "id", "In cancelAppointment method of Booking Service");
		MainResponseDTO<CancelBookingResponseDTO> responseDto = new MainResponseDTO<>();
		boolean isBatchUser = true;
		responseDto.setId(idUrlCancel);
		responseDto.setVersion(versionUrl);
		responseDto.setResponse(cancelBooking(preRegistrationId, isBatchUser));

		responseDto.setResponsetime(serviceUtil.getCurrentResponseTime());

		return responseDto;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * io.mosip.preregistration.booking.serviceimpl.service.BookingServiceIntf#book(
	 * java.lang.String,
	 * io.mosip.preregistration.booking.serviceimpl.dto.BookingRequestDTO)
	 */
	@Override
	public BookingStatusDTO book(String preRegistrationId, BookingRequestDTO bookingRequestDTO) {
		log.info("sessionId", "idType", "id", "In book method of Booking Service");
		BookingStatusDTO bookingStatusDTO = new BookingStatusDTO();
		MainResponseDTO<?> response = new MainResponseDTO<>();
		response.setId(idUrlBookAppointment);
		response.setVersion(versionUrl);
		try {
			AvailibityEntity availableEntity;

			availableEntity = bookingDAO.findByFromTimeAndToTimeAndRegDateAndRegcntrId(
					LocalTime.parse(bookingRequestDTO.getSlotFromTime()),
					LocalTime.parse(bookingRequestDTO.getSlotToTime()), LocalDate.parse(bookingRequestDTO.getRegDate()),
					bookingRequestDTO.getRegistrationCenterId());
			log.info("In Availablity of book method",
					"available slots before update :" + availableEntity.getAvailableKiosks(),
					" for Reg center" + availableEntity.getRegcntrId(),
					" and Date and Time " + availableEntity.getRegDate() + " " + availableEntity.getFromTime());
			if (serviceUtil.isKiosksAvailable(availableEntity)) {
				/* Updating booking */
				bookingDAO.saveRegistrationEntityForBooking(
						serviceUtil.bookingEntitySetter(preRegistrationId, bookingRequestDTO));
				/* Reduce Availability */
				availableEntity.setAvailableKiosks(availableEntity.getAvailableKiosks() - 1);
				AvailibityEntity availableUpdate = bookingDAO.updateAvailibityEntity(availableEntity);

				log.info("In Availablity of book method",
						"available slots after upadate :" + availableUpdate.getAvailableKiosks(),
						" for Reg center" + availableUpdate.getRegcntrId(),
						" and Date and Time " + availableUpdate.getRegDate() + " " + availableUpdate.getFromTime());

				/* Updating demographic */
				serviceUtil.updateDemographicStatus(preRegistrationId, StatusCodes.BOOKED.getCode());
				bookingStatusDTO.setBookingMessage("Appointment booked successfully");

			}

		} catch (Exception ex) {
			log.debug("sessionId", "idType", "id", ExceptionUtils.getStackTrace(ex));
			log.error("sessionId", "idType", "id", "In book method of Booking Service- " + ex.getMessage());
			new BookingExceptionCatcher().handle(ex, response);
		}

		return bookingStatusDTO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.preregistration.booking.serviceimpl.service.BookingServiceIntf#
	 * cancelBooking(java.lang.String, boolean)
	 */
	@Override
	public CancelBookingResponseDTO cancelBooking(String preRegistrationId, boolean isBatchUser) {
		log.info("sessionId", "idType", "id", "In cancelBooking method of Booking Service");
		CancelBookingResponseDTO cancelBookingResponseDTO = new CancelBookingResponseDTO();
		MainResponseDTO<?> response = new MainResponseDTO<>();
		response.setId(idUrlCancel);
		response.setVersion(versionUrl);
		boolean isSaveSuccess = false;
		AvailibityEntity availableEntity;
		RegistrationBookingEntity bookingEntity;
		try {
			if (serviceUtil.mandatoryParameterCheckforCancel(preRegistrationId)) {
				if (serviceUtil.getDemographicStatusForCancel(preRegistrationId)) {
					/* Getting Booking details */
					bookingEntity = bookingDAO.findByPreRegistrationId(preRegistrationId);

					availableEntity = bookingDAO.findByFromTimeAndToTimeAndRegDateAndRegcntrId(
							bookingEntity.getSlotFromTime(), bookingEntity.getSlotToTime(), bookingEntity.getRegDate(),
							bookingEntity.getRegistrationCenterId());
					/* Getting Status From Demographic */
					serviceUtil.getDemographicStatus(preRegistrationId);

					/* For batch condition will skip */
					if (!isBatchUser) {
						String str = bookingEntity.getRegDate() + " " + bookingEntity.getSlotFromTime();
						DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
						LocalDateTime bookedDateTime = LocalDateTime.parse(str, formatter);

						serviceUtil.timeSpanCheckForCancle(bookedDateTime);
					}
					/* Deleting the canceled booking */
					// bookingDAO.deleteRegistrationEntity(bookingEntity);
					bookingDAO.deleteByPreRegistrationId(preRegistrationId);

					/* Update the status to Canceled in demographic Table */
					serviceUtil.updateDemographicStatus(preRegistrationId, StatusCodes.PENDING_APPOINTMENT.getCode());

					/* No. of Availability. update */
					availableEntity.setAvailableKiosks(availableEntity.getAvailableKiosks() + 1);

					bookingDAO.updateAvailibityEntity(availableEntity);

					cancelBookingResponseDTO.setTransactionId(UUIDGeneratorUtil.generateId());
					cancelBookingResponseDTO
							.setMessage("Appointment for the selected application has been successfully cancelled");

				}
			}
			isSaveSuccess = true;
		} catch (Exception ex) {
			log.debug("sessionId", "idType", "id", ExceptionUtils.getStackTrace(ex));
			log.error("sessionId", "idType", "id", "In cancelBooking method of Booking Service- " + ex.getMessage());
			new BookingExceptionCatcher().handle(ex, response);
		} finally {

			if (isSaveSuccess) {
				setAuditValues(EventId.PRE_402.toString(), EventName.UPDATE.toString(), EventType.BUSINESS.toString(),
						"Booking cancel successfully", AuditLogVariables.MULTIPLE_ID.toString(),
						authUserDetails().getUserId(), authUserDetails().getUsername(), null);
			} else {
				setAuditValues(EventId.PRE_405.toString(), EventName.EXCEPTION.toString(), EventType.SYSTEM.toString(),
						"Booking failed to cancel", AuditLogVariables.NO_ID.toString(), authUserDetails().getUserId(),
						authUserDetails().getUsername(), null);
			}
		}
		return cancelBookingResponseDTO;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.preregistration.booking.serviceimpl.service.BookingServiceIntf#
	 * deleteBooking(java.lang.String)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
	public MainResponseDTO<DeleteBookingDTO> deleteBooking(String preregId) {
		log.info("sessionId", "idType", "id", "In deleteIndividual method of pre-registration service ");
		MainResponseDTO<DeleteBookingDTO> response = new MainResponseDTO<>();
		response.setId(idUrlDelete);
		response.setVersion(versionUrl);
		DeleteBookingDTO deleteDto = new DeleteBookingDTO();
		Map<String, String> requestParamMap = new HashMap<>();
		AvailibityEntity availableEntity;
		boolean isSaveSuccess = false;
		try {
			requestParamMap.put(RequestCodes.PRE_REGISTRAION_ID.getCode(), preregId);
			if (ValidationUtil.requstParamValidator(requestParamMap)) {
				RegistrationBookingEntity registrationEntityList = bookingDAO.findByPreRegistrationId(preregId);

				bookingDAO.deleteByPreRegistrationId(preregId);
				availableEntity = bookingDAO.findByFromTimeAndToTimeAndRegDateAndRegcntrId(
						LocalTime.parse(registrationEntityList.getSlotFromTime().toString()),
						LocalTime.parse(registrationEntityList.getSlotToTime().toString()),
						LocalDate.parse(registrationEntityList.getRegDate().toString()),
						registrationEntityList.getRegistrationCenterId());
				/* No. of Availability. update */
				availableEntity.setAvailableKiosks(availableEntity.getAvailableKiosks() + 1);

				/* Updating slot in DB */
				bookingDAO.updateAvailibityEntity(availableEntity);

				deleteDto.setPreRegistrationId(registrationEntityList.getDemographicEntity().getPreRegistrationId());
				deleteDto.setDeletedBy(registrationEntityList.getCrBy());
				deleteDto.setDeletedDateTime(new Date(System.currentTimeMillis()));

			}
			isSaveSuccess = true;
		} catch (Exception ex) {
			log.debug("sessionId", "idType", "id", ExceptionUtils.getStackTrace(ex));
			log.error("sessionId", "idType", "id", "In deleteBooking method of Booking Service- " + ex.getMessage());
			new BookingExceptionCatcher().handle(ex, response);
		} finally {

			if (isSaveSuccess) {
				setAuditValues(EventId.PRE_403.toString(), EventName.DELETE.toString(), EventType.BUSINESS.toString(),
						"Booking deleted successfully", AuditLogVariables.MULTIPLE_ID.toString(),
						authUserDetails().getUserId(), authUserDetails().getUsername(), null);
			} else {
				setAuditValues(EventId.PRE_405.toString(), EventName.EXCEPTION.toString(), EventType.SYSTEM.toString(),
						"Booking failed to delete", AuditLogVariables.NO_ID.toString(), authUserDetails().getUserId(),
						authUserDetails().getUsername(), null);
			}
		}

		response.setResponsetime(serviceUtil.getCurrentResponseTime());
		response.setResponse(deleteDto);
		return response;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.preregistration.booking.serviceimpl.service.BookingServiceIntf#
	 * checkSlotAvailability(io.mosip.preregistration.booking.serviceimpl.dto.
	 * BookingRequestDTO)
	 */
	@Override
	public void checkSlotAvailability(BookingRequestDTO bookingRequestDTO) {
		MainResponseDTO<?> response = new MainResponseDTO<>();
		response.setId(idUrlCheckSlotAvailability);
		response.setVersion(versionUrl);
		try {
			List<RegistrationCenterDto> regCenter = serviceUtil.getRegCenterMasterData();
			Boolean isValidRegCenter = regCenter.stream()
					.anyMatch(iterate -> iterate.getId().contains(bookingRequestDTO.getRegistrationCenterId()));

			if (!isValidRegCenter) {
				throw new RecordNotFoundException(ErrorCodes.PRG_BOOK_RCI_035.getCode(),
						ErrorMessages.REG_CENTER_ID_NOT_FOUND.getMessage());
			}

			bookingDAO.findRegistrationCenterId(bookingRequestDTO.getRegistrationCenterId());
			AvailibityEntity entity = bookingDAO.findByFromTimeAndToTimeAndRegDateAndRegcntrId(
					LocalTime.parse(bookingRequestDTO.getSlotFromTime()),
					LocalTime.parse(bookingRequestDTO.getSlotToTime()), LocalDate.parse(bookingRequestDTO.getRegDate()),
					bookingRequestDTO.getRegistrationCenterId());
			log.info("In Availablity", "available slots :" + entity.getAvailableKiosks(),
					" for Reg center" + entity.getRegcntrId(),
					" and Date and Time " + entity.getRegDate() + " " + entity.getFromTime());

			log.info("sessionId", "idType", "id", "In checkSlotAvailability method of Booking Service");
			if (entity.getAvailableKiosks() < 1) {
				throw new AvailablityNotFoundException(ErrorCodes.PRG_BOOK_RCI_002.getCode(),
						ErrorMessages.AVAILABILITY_NOT_FOUND_FOR_THE_SELECTED_TIME.getMessage());
			}
		} catch (Exception ex) {
			log.debug("sessionId", "idType", "id", ExceptionUtils.getStackTrace(ex));
			log.error("sessionId", "idType", "id",
					"In checkSlotAvailability method of Booking Service for Exception- " + ex.getMessage());
			new BookingExceptionCatcher().handle(ex, response);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.preregistration.booking.serviceimpl.service.BookingServiceIntf#
	 * deleteOldBooking(java.lang.String)
	 */
	@Override
	public boolean deleteOldBooking(String preId) {
		MainResponseDTO<?> response = new MainResponseDTO<>();
		response.setId(idUrlDeleteOld);
		response.setVersion(versionUrl);
		try {
			int count = bookingDAO.deleteByPreRegistrationId(preId);
			if (count > 0) {
				log.info("sessionId", "idType", "id", "In deleteOldBooking method of Booking Service");
				return true;
			}
		} catch (Exception ex) {
			log.debug("sessionId", "idType", "id", ExceptionUtils.getStackTrace(ex));
			log.error("sessionId", "idType", "id",
					"In deleteOldBooking method of Booking Service for Exception- " + ex.getMessage());
			new BookingExceptionCatcher().handle(ex, response);
		}
		return false;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.preregistration.booking.serviceimpl.service.BookingServiceIntf#
	 * increaseAvailability(io.mosip.preregistration.booking.serviceimpl.dto.
	 * BookingRequestDTO)
	 */
	@Override
	public boolean increaseAvailability(BookingRequestDTO oldBooking) {
		MainResponseDTO<?> response = new MainResponseDTO<>();
		response.setId(idUrlIncreaseAvailability);
		response.setVersion(versionUrl);
		try {
			AvailibityEntity availableEntity;
			availableEntity = bookingDAO.findByFromTimeAndToTimeAndRegDateAndRegcntrId(
					LocalTime.parse(oldBooking.getSlotFromTime()), LocalTime.parse(oldBooking.getSlotToTime()),
					LocalDate.parse(oldBooking.getRegDate()), oldBooking.getRegistrationCenterId());
			availableEntity.setAvailableKiosks(availableEntity.getAvailableKiosks() + 1);
			bookingDAO.updateAvailibityEntity(availableEntity);
			log.info("sessionId", "idType", "id", "In increaseAvailability method of Booking Service");

		} catch (Exception ex) {
			log.debug("sessionId", "idType", "id", ExceptionUtils.getStackTrace(ex));
			log.error("sessionId", "idType", "id",
					"In increaseAvailability method of Booking Service for Exception- " + ex.getMessage());
			new BookingExceptionCatcher().handle(ex, response);
		}
		return true;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.preregistration.booking.serviceimpl.service.BookingServiceIntf#
	 * setAuditValues(java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void setAuditValues(String eventId, String eventName, String eventType, String description, String idType,
			String userId, String userName, String ref_id) {
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
		auditRequestDto.setId(ref_id);
		auditLogUtil.saveAuditDetails(auditRequestDto);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see io.mosip.preregistration.booking.serviceimpl.service.BookingServiceIntf#
	 * getBookedPreRegistrationByDate(java.lang.String, java.lang.String,
	 * java.lang.String)
	 */

	@Override
	public MainResponseDTO<PreRegIdsByRegCenterIdResponseDTO> getBookedPreRegistrationByDate(String fromDateStr,
			String toDateStr, String regCenterId) {
		log.info("sessionId", "idType", "id", "In getBookedPreRegistrationByDate method of booking service ");
		MainResponseDTO<PreRegIdsByRegCenterIdResponseDTO> response = new MainResponseDTO<>();
		response.setId(idUrlBookingByDate);
		response.setVersion(versionUrl);
		try {

			if (toDateStr == null || toDateStr.isEmpty()) {
				toDateStr = fromDateStr;
			}
			String format = "yyyy-MM-dd";
			if (serviceUtil.validateFromDateAndToDate(fromDateStr, toDateStr, format)) {
				DateTimeFormatter parseFormatter = DateTimeFormatter.ofPattern(format);
				LocalDate fromDate = LocalDate.parse(fromDateStr, parseFormatter);
				LocalDate toDate = LocalDate.parse(toDateStr, parseFormatter);

				List<String> details = bookingDAO.findByBookingDateBetweenAndRegCenterId(fromDate, toDate, regCenterId);
				PreRegIdsByRegCenterIdResponseDTO responseDTO = new PreRegIdsByRegCenterIdResponseDTO();
				responseDTO.setPreRegistrationIds(details);
				responseDTO.setRegistrationCenterId(regCenterId);

				response.setResponse(responseDTO);
			}
		} catch (Exception ex) {
			log.debug("sessionId", "idType", "id", ExceptionUtils.getStackTrace(ex));
			log.error("sessionId", "idType", "id",
					"In getPreRegistrationByDate method of pre-registration service - " + ex.getMessage());
			new BookingExceptionCatcher().handle(ex, response);
		}
		response.setResponsetime(serviceUtil.getCurrentResponseTime());
		response.setId(idUrlBookingByDate);
		response.setVersion(versionUrl);
		response.setErrors(null);
		return response;
	}

}
