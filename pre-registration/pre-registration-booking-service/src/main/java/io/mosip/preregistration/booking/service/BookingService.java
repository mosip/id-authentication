/* 
 * Copyright
 * 
 */
package io.mosip.preregistration.booking.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.kernel.core.util.DateUtils;
import io.mosip.preregistration.booking.dto.AvailabilityDto;
import io.mosip.preregistration.booking.dto.BookingRequestDTO;
import io.mosip.preregistration.booking.dto.BookingStatusDTO;
import io.mosip.preregistration.booking.dto.CancelBookingDTO;
import io.mosip.preregistration.booking.dto.CancelBookingResponseDTO;
import io.mosip.preregistration.booking.dto.DateTimeDto;
import io.mosip.preregistration.booking.dto.PreRegIdsByRegCenterIdDTO;
import io.mosip.preregistration.booking.dto.PreRegIdsByRegCenterIdResponseDTO;
import io.mosip.preregistration.booking.dto.RegistrationCenterDto;
import io.mosip.preregistration.booking.entity.AvailibityEntity;
import io.mosip.preregistration.booking.entity.RegistrationBookingEntity;
import io.mosip.preregistration.booking.errorcodes.ErrorCodes;
import io.mosip.preregistration.booking.errorcodes.ErrorMessages;
import io.mosip.preregistration.booking.exception.AppointmentAlreadyCanceledException;
import io.mosip.preregistration.booking.exception.AppointmentBookingFailedException;
import io.mosip.preregistration.booking.exception.AppointmentCannotBeBookedException;
import io.mosip.preregistration.booking.exception.AppointmentCannotBeCanceledException;
import io.mosip.preregistration.booking.exception.AvailablityNotFoundException;
import io.mosip.preregistration.booking.exception.BookingDataNotFoundException;
import io.mosip.preregistration.booking.exception.BookingTimeSlotAlreadyBooked;
import io.mosip.preregistration.booking.exception.CancelAppointmentFailedException;
import io.mosip.preregistration.booking.exception.util.BookingExceptionCatcher;
import io.mosip.preregistration.booking.repository.BookingAvailabilityRepository;
import io.mosip.preregistration.booking.repository.RegistrationBookingRepository;
import io.mosip.preregistration.booking.repository.impl.BookingDAO;
import io.mosip.preregistration.booking.service.util.BookingLock;
import io.mosip.preregistration.booking.service.util.BookingServiceUtil;
import io.mosip.preregistration.core.code.StatusCodes;
import io.mosip.preregistration.core.common.dto.BookingRegistrationDTO;
import io.mosip.preregistration.core.common.dto.MainListRequestDTO;
import io.mosip.preregistration.core.common.dto.MainListResponseDTO;
import io.mosip.preregistration.core.common.dto.MainRequestDTO;
import io.mosip.preregistration.core.common.dto.MainResponseDTO;
import io.mosip.preregistration.core.config.LoggerConfiguration;
import io.mosip.preregistration.core.exception.TableNotAccessibleException;
import io.mosip.preregistration.core.stateUtil.StateManager;
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
	 * Reference for ${noOfDays} from property file
	 */
	@Value("${noOfDays}")
	int noOfDays;

	/**
	 * Autowired reference for {@link #bookingAvailabilityRepository}
	 */
	@Autowired
	BookingAvailabilityRepository bookingAvailabilityRepository;

	/**
	 * Autowired reference for {@link #registrationBookingRepository}
	 */
	@Autowired
	@Qualifier("registrationBookingRepository")
	RegistrationBookingRepository registrationBookingRepository;

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
		requiredRequestMap.put("ver", versionUrl);

	}

	private Logger log = LoggerConfiguration.logConfig(BookingService.class);

	/**
	 * It will sync the registration center details
	 * 
	 * @return ResponseDto<String>
	 */
	@Transactional
	public MainResponseDTO<String> addAvailability() {
		log.info("sessionId", "idType", "id", "In addAvailability method of Booking Service");
		MainResponseDTO<String> response = new MainResponseDTO<>();
		try {
			LocalDate endDate = LocalDate.now().plusDays(noOfDays);
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
		LocalDate endDate = LocalDate.now().plusDays(Math.addExact(noOfDays, 2));
		LocalDate fromDate = LocalDate.now().plusDays(2);
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
	@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = { DataAccessException.class,
			AppointmentBookingFailedException.class, BookingTimeSlotAlreadyBooked.class,
			AvailablityNotFoundException.class, AppointmentCannotBeBookedException.class })
	public MainResponseDTO<List<BookingStatusDTO>> bookAppointment(
			MainListRequestDTO<BookingRequestDTO> bookingRequestDTOs) {
		log.info("sessionId", "idType", "id", "In bookAppointment method of Booking Service");
		MainResponseDTO<List<BookingStatusDTO>> responseDTO = new MainResponseDTO<>();
		List<BookingStatusDTO> respList = new ArrayList<>();
		try {
			if (ValidationUtil.requestValidator(serviceUtil.prepareRequestMap(bookingRequestDTOs),
					requiredRequestMap)) {
				for (BookingRequestDTO bookingRequestDTO : bookingRequestDTOs.getRequest()) {
					String preRegStatusCode = serviceUtil
							.callGetStatusRestService(bookingRequestDTO.getPreRegistrationId());
					BookingRegistrationDTO oldBookingRegistrationDTO = bookingRequestDTO.getOldBookingDetails();
					BookingRegistrationDTO newBookingRegistrationDTO = bookingRequestDTO.getNewBookingDetails();
					String preRegistrationId = bookingRequestDTO.getPreRegistrationId();
					if (serviceUtil.mandatoryParameterCheck(preRegistrationId, oldBookingRegistrationDTO,
							newBookingRegistrationDTO)) {
						if (!serviceUtil.isNull(oldBookingRegistrationDTO)) {
							cancel(preRegistrationId, oldBookingRegistrationDTO, newBookingRegistrationDTO,
									preRegStatusCode);
						}
						else {
							checkAlreadyBooked(preRegistrationId,newBookingRegistrationDTO);
						}
						respList.add(book(preRegistrationId, newBookingRegistrationDTO, preRegStatusCode));
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

	public boolean checkAlreadyBooked(String preRegistrationId, BookingRegistrationDTO newBookingRegistrationDTO) {
		log.info("sessionId", "idType", "id", "In checkAlreadyBooked method of Booking Service");
		
		return true;
	}

	public boolean cancel(String preRegistrationId, BookingRegistrationDTO oldBookingRegistrationDTO,
			BookingRegistrationDTO newBookingRegistrationDTO, String status) {
		log.info("sessionId", "idType", "id", "In cancel method of Booking Service");
		if (serviceUtil.isNotDuplicate(oldBookingRegistrationDTO, newBookingRegistrationDTO)
				&& StateManager.checkIsValidStatus(status, "cancel")) {
			cancelBooking(serviceUtil.cancelBookingDtoSetter(preRegistrationId, oldBookingRegistrationDTO));
		}
		return true;
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
			entity = bookingDAO.findPreIdAndStatusCode(preRegID, StatusCodes.CANCELED.getCode());// ?
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
	@Transactional(rollbackFor = { DataAccessException.class, CancelAppointmentFailedException.class,
			AppointmentAlreadyCanceledException.class, AvailablityNotFoundException.class,
			AppointmentCannotBeCanceledException.class })
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
						.findByRegistrationCenterIdAndStatusCode(regCenterId.trim(), StatusCodes.BOOKED.getCode());
				if (!bookingEntities.isEmpty()) {
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
				} else {
					throw new BookingDataNotFoundException(ErrorCodes.PRG_BOOK_RCI_013.toString(),
							ErrorMessages.BOOKING_DATA_NOT_FOUND.toString());
				}
			}
		} catch (DataAccessLayerException ex) {
			log.error("sessionId", "idType", "id",
					"In getPreIdsByRegCenterId method of Booking Service for DataAccessLayerException- "
							+ ex.getMessage());
			throw new TableNotAccessibleException(ErrorCodes.PRG_BOOK_RCI_010.toString(),
					ErrorMessages.BOOKING_TABLE_NOT_ACCESSIBLE.toString(), ex.getCause());
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
	public BookingStatusDTO book(String preRegistrationId, BookingRegistrationDTO bookingRegistrationDTO,
			String status) {
		log.info("sessionId", "idType", "id", "In book method of Booking Service");
		BookingStatusDTO bookingStatusDTO = new BookingStatusDTO();
		try {
			if (StateManager.checkIsValidStatus(status, "book")) {
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
						availableEntity.setAvailableKiosks(availableEntity.getAvailableKiosks() - 1);
						bookingDAO.updateAvailibityEntity(availableEntity);
					}
				}
				bookingDAO.saveRegistrationEntityForBooking(
						serviceUtil.bookingEntitySetter(preRegistrationId, bookingRegistrationDTO));
				/* Pre registration status code update */
				serviceUtil.callUpdateStatusRestService(preRegistrationId, StatusCodes.BOOKED.getCode());
				bookingStatusDTO.setPreRegistrationId(preRegistrationId);
				bookingStatusDTO.setBookingStatus(StatusCodes.BOOKED.getCode());
				bookingStatusDTO.setBookingMessage("APPOINTMENT_SUCCESSFULLY_BOOKED");

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

					RegistrationBookingEntity bookingEntity = bookingDAO.findPreIdAndStatusCode(
							cancelBookingDTO.getPreRegistrationId(), StatusCodes.CANCELED.getCode());
					bookingEntity.setStatusCode(StatusCodes.CANCELED.getCode());
					bookingEntity.setUpdDate(DateUtils.parseDateToLocalDateTime(new Date()));

					bookingEntity = bookingDAO.saveRegistrationEntityForCancel(bookingEntity);

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

}
