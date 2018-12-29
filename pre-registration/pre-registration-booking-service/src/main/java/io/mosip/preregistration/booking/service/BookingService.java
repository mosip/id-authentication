package io.mosip.preregistration.booking.service;

import java.time.LocalDate;
import java.util.ArrayList;
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
import org.springframework.transaction.annotation.Transactional;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.preregistration.booking.code.StatusCodes;
import io.mosip.preregistration.booking.dto.AvailabilityDto;
import io.mosip.preregistration.booking.dto.BookingRegistrationDTO;
import io.mosip.preregistration.booking.dto.BookingRequestDTO;
import io.mosip.preregistration.booking.dto.BookingStatusDTO;
import io.mosip.preregistration.booking.dto.CancelBookingDTO;
import io.mosip.preregistration.booking.dto.CancelBookingResponseDTO;
import io.mosip.preregistration.booking.dto.DateTimeDto;
import io.mosip.preregistration.booking.dto.MainListRequestDTO;
import io.mosip.preregistration.booking.dto.MainListResponseDTO;
import io.mosip.preregistration.booking.dto.MainRequestDTO;
import io.mosip.preregistration.booking.dto.MainResponseDTO;
import io.mosip.preregistration.booking.dto.PreRegIdsByRegCenterIdDTO;
import io.mosip.preregistration.booking.dto.PreRegIdsByRegCenterIdResponseDTO;
import io.mosip.preregistration.booking.dto.RegistrationCenterDto;
import io.mosip.preregistration.booking.entity.AvailibityEntity;
import io.mosip.preregistration.booking.entity.RegistrationBookingEntity;
import io.mosip.preregistration.booking.entity.RegistrationBookingPK;
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
import io.mosip.preregistration.booking.exception.RecordNotFoundException;
import io.mosip.preregistration.booking.exception.util.BookingExceptionCatcher;
import io.mosip.preregistration.booking.repository.BookingAvailabilityRepository;
import io.mosip.preregistration.booking.repository.RegistrationBookingRepository;
import io.mosip.preregistration.booking.service.util.BookingServiceUtil;
import io.mosip.preregistration.core.exception.TablenotAccessibleException;
import io.mosip.preregistration.core.util.ValidationUtil;

/**
 * @author M1046129
 *
 */
@Component
public class BookingService {

	@Autowired
	BookingServiceUtil serviceUtil;

	@Value("${noOfDays}")
	int noOfDays;

	@Autowired
	BookingAvailabilityRepository bookingAvailabilityRepository;

	@Autowired
	@Qualifier("registrationBookingRepository")
	RegistrationBookingRepository registrationBookingRepository;

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

	/**
	 * It will sync the registration center details
	 * 
	 * @return ResponseDto<String>
	 */
	public MainResponseDTO<String> addAvailability() {
		MainResponseDTO<String> response = new MainResponseDTO<>();
		try {
			LocalDate endDate = LocalDate.now().plusDays(noOfDays);
			List<RegistrationCenterDto> regCenter = serviceUtil.callRegCenterDateRestService();
			for (RegistrationCenterDto regDto : regCenter) {
				List<String> holidaylist = serviceUtil.callGetHolidayListRestService(regDto);
				for (LocalDate sDate = LocalDate.now(); (sDate.isBefore(endDate)
						|| sDate.isEqual(endDate)); sDate = sDate.plusDays(1)) {
					serviceUtil.timeSlotCalculator(regDto, holidaylist, sDate, bookingAvailabilityRepository);
				}
			}
		} catch (Exception e) {
			new BookingExceptionCatcher().handle(e);
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
		MainResponseDTO<AvailabilityDto> response = new MainResponseDTO<>();
		LocalDate endDate = LocalDate.now().plusDays(Math.addExact(noOfDays, 2));
		LocalDate fromDate = LocalDate.now().plusDays(2);
		AvailabilityDto availability = new AvailabilityDto();
		try {
			List<java.sql.Date> dateList = bookingAvailabilityRepository.findDate(regID, fromDate, endDate);
			if (!dateList.isEmpty()) {
				List<DateTimeDto> dateTimeList = new ArrayList<>();
				for (int i = 0; i < dateList.size(); i++) {
					DateTimeDto dateTime = new DateTimeDto();
					List<AvailibityEntity> entity = bookingAvailabilityRepository
							.findByRegcntrIdAndRegDateOrderByFromTimeAsc(regID, dateList.get(i).toLocalDate());
					if (!entity.isEmpty()) {
						serviceUtil.slotSetter(dateList, dateTimeList, i, dateTime, entity);
					}
				}
				availability.setCenterDetails(dateTimeList);
				availability.setRegCenterId(regID);
			} else {
				throw new RecordNotFoundException(ErrorCodes.PRG_BOOK_RCI_015.toString(),
						ErrorMessages.NO_TIME_SLOTS_ASSIGNED_TO_THAT_REG_CENTER.toString());
			}
		} catch (DataAccessLayerException e) {
			throw new AvailablityNotFoundException(ErrorCodes.PRG_BOOK_RCI_016.toString(),
					ErrorMessages.AVAILABILITY_TABLE_NOT_ACCESSABLE.toString());
		} catch (Exception ex) {

			new BookingExceptionCatcher().handle(ex);
		}
		response.setResTime(serviceUtil.getCurrentResponseTime());
		response.setStatus(true);
		response.setResponse(availability);
		return response;
	}

	/**
	 * @param bookingDTO
	 * @return response with status code
	 * @throws java.text.ParseException
	 */
	@Transactional(rollbackFor = { DataAccessException.class, AppointmentBookingFailedException.class,
			BookingTimeSlotAlreadyBooked.class, AvailablityNotFoundException.class,
			AppointmentCannotBeBookedException.class })
	public MainResponseDTO<List<BookingStatusDTO>> bookAppointment(MainListRequestDTO<BookingRequestDTO> bookingDTO) {
		MainResponseDTO<List<BookingStatusDTO>> responseDTO = new MainResponseDTO<>();
		RegistrationBookingPK bookingPK = new RegistrationBookingPK();
		List<BookingStatusDTO> respList = new ArrayList<>();
		try {
			if (ValidationUtil.requestValidator(serviceUtil.prepareRequestMap(bookingDTO), requiredRequestMap)) {
				for (BookingRequestDTO bookingRequestDTO : bookingDTO.getRequest()) {
					if (serviceUtil.mandatoryParameterCheck(bookingRequestDTO)) {
						if (serviceUtil.callGetDocumentsByPreIdRestService(bookingRequestDTO)) {
							if (bookingRequestDTO.getOldBookingDetails() == null) {
								/* booking of new Appointment */
								synchronized (bookingRequestDTO) {
									BookingStatusDTO statusDTO = serviceUtil.bookingAPI(bookingDTO.getReqTime(),
											bookingRequestDTO, bookingPK, registrationBookingRepository,
											bookingAvailabilityRepository);
									respList.add(statusDTO);
								}
							} else {
								/* Re-Booking */
								BookingRegistrationDTO oldBookingRegistrationDTO = bookingRequestDTO
										.getOldBookingDetails();
								BookingRegistrationDTO newBookingRegistrationDTO = bookingRequestDTO
										.getNewBookingDetails();
								if (serviceUtil.checkForDuplicate(oldBookingRegistrationDTO,
										newBookingRegistrationDTO)) {
									CancelBookingDTO cancelBookingDTO = serviceUtil.cancelBookingDtoSetter(
											bookingRequestDTO.getPreRegistrationId(), oldBookingRegistrationDTO);
									synchronized (cancelBookingDTO) {
										CancelBookingResponseDTO cancelBookingResponseDTO = serviceUtil
												.cancelBookingAPI(cancelBookingDTO, registrationBookingRepository,
														bookingAvailabilityRepository);
										if (cancelBookingResponseDTO != null && cancelBookingResponseDTO.getMessage()
												.equals("APPOINTMENT_SUCCESSFULLY_CANCELED")) {
											BookingStatusDTO statusDTO = serviceUtil.bookingAPI(bookingDTO.getReqTime(),
													bookingRequestDTO, bookingPK, registrationBookingRepository,
													bookingAvailabilityRepository);
											respList.add(statusDTO);
										} else {
											throw new CancelAppointmentFailedException(
													ErrorCodes.PRG_BOOK_RCI_019.toString(),
													ErrorMessages.APPOINTMENT_CANCEL_FAILED.toString());
										}
									}
								}
							}
						} else {
							BookingStatusDTO noDocumentDTO = new BookingStatusDTO();
							noDocumentDTO.setPreRegistrationId(bookingRequestDTO.getPreRegistrationId());
							noDocumentDTO.setBookingStatus("Failed");
							noDocumentDTO.setBookingMessage("BOOKING_FAILED_DUE_TO_NO_DOCUMENT");
							respList.add(noDocumentDTO);
						}
					}

				}
				responseDTO.setStatus(true);
				responseDTO.setResTime(serviceUtil.getCurrentResponseTime());
				responseDTO.setErr(null);
				responseDTO.setResponse(respList);
			}
		} catch (DataAccessLayerException e) {
			throw new TablenotAccessibleException(ErrorCodes.PRG_BOOK_RCI_010.toString(),
					ErrorMessages.BOOKING_TABLE_NOT_ACCESSIBLE.toString(), e.getCause());
		} 
		return responseDTO;
	}

	public MainResponseDTO<BookingRegistrationDTO> getAppointmentDetails(String preRegID) {
		BookingRegistrationDTO bookingRegistrationDTO = new BookingRegistrationDTO();
		MainResponseDTO<BookingRegistrationDTO> responseDto = new MainResponseDTO<>();
		RegistrationBookingEntity entity = new RegistrationBookingEntity();
		try {
			entity = registrationBookingRepository.findPreIdAndStatusCode(preRegID, StatusCodes.BOOKED.getCode());
			if (entity != null) {
				bookingRegistrationDTO.setRegDate(entity.getRegDate().toString());
				bookingRegistrationDTO.setRegistrationCenterId(entity.getRegistrationCenterId());
				bookingRegistrationDTO.setSlotFromTime(entity.getSlotFromTime().toString());
				bookingRegistrationDTO.setSlotToTime(entity.getSlotToTime().toString());
				responseDto.setResponse(bookingRegistrationDTO);
				responseDto.setStatus(true);
				responseDto.setErr(null);
				responseDto.setResTime(serviceUtil.getCurrentResponseTime());
			} else {
				throw new BookingDataNotFoundException(ErrorCodes.PRG_BOOK_RCI_013.toString(),
						ErrorMessages.BOOKING_DATA_NOT_FOUND.toString());
			}
		} catch (DataAccessLayerException e) {
			throw new TablenotAccessibleException(ErrorCodes.PRG_BOOK_RCI_010.toString(),
					ErrorMessages.BOOKING_TABLE_NOT_ACCESSIBLE.toString(), e.getCause());
		}

		return responseDto;
	}

	@Transactional(rollbackFor = { DataAccessException.class, CancelAppointmentFailedException.class,
			AppointmentAlreadyCanceledException.class, AvailablityNotFoundException.class,
			AppointmentCannotBeCanceledException.class })
	public MainResponseDTO<CancelBookingResponseDTO> cancelAppointment(MainRequestDTO<CancelBookingDTO> requestdto) {
		MainResponseDTO<CancelBookingResponseDTO> dto = new MainResponseDTO<>();
		try {
			if (ValidationUtil.requestValidator(serviceUtil.prepareRequestMap(requestdto), requiredRequestMap)) {
				CancelBookingDTO cancelBookingDTO = requestdto.getRequest();
				CancelBookingResponseDTO cancelBookingResponseDTO = serviceUtil.cancelBookingAPI(cancelBookingDTO,
						registrationBookingRepository, bookingAvailabilityRepository);
				if (cancelBookingResponseDTO != null) {
					dto.setResponse(cancelBookingResponseDTO);
					dto.setErr(null);
					dto.setStatus(true);
					dto.setResTime(serviceUtil.getCurrentResponseTime());
				}else {
					throw new CancelAppointmentFailedException(ErrorCodes.PRG_BOOK_RCI_019.toString(),
							ErrorMessages.APPOINTMENT_CANCEL_FAILED.toString());
				}
			}
		} catch (DataAccessLayerException e) {
			throw new TablenotAccessibleException(ErrorCodes.PRG_BOOK_RCI_010.toString(),
					ErrorMessages.BOOKING_TABLE_NOT_ACCESSIBLE.toString(), e.getCause());
		} 
		return dto;

	}

	public MainListResponseDTO<PreRegIdsByRegCenterIdResponseDTO> getPreIdsByRegCenterId(
			MainRequestDTO<PreRegIdsByRegCenterIdDTO> requestDTO) {
		MainListResponseDTO<PreRegIdsByRegCenterIdResponseDTO> responseDto = new MainListResponseDTO<>();
		PreRegIdsByRegCenterIdResponseDTO preRegIdsByRegCenterIdResponseDTO = new PreRegIdsByRegCenterIdResponseDTO();
		List<PreRegIdsByRegCenterIdResponseDTO> preRegIdsByRegCenterIdResponseDTOList = new ArrayList<>();
		try {
			String regCenterId = requestDTO.getRequest().getRegistrationCenterId();
			List<RegistrationBookingEntity> bookingEntities = registrationBookingRepository
					.findByRegistrationCenterIdAndStatusCode(regCenterId.trim(), StatusCodes.BOOKED.getCode());
			List<String> preRegIdList = requestDTO.getRequest().getPreRegistrationIds();
			List<String> entityPreRegIdList = new LinkedList<>();
			
			if(bookingEntities!=null && !bookingEntities.isEmpty()) {
				for (RegistrationBookingEntity bookingEntity : bookingEntities) {
					entityPreRegIdList.add(bookingEntity.getBookingPK().getPreregistrationId());
				}
				preRegIdList.retainAll(entityPreRegIdList);
				preRegIdsByRegCenterIdResponseDTO.setRegistrationCenterId(regCenterId);
				preRegIdsByRegCenterIdResponseDTO.setPreRegistrationIds(preRegIdList);
				preRegIdsByRegCenterIdResponseDTOList.add(preRegIdsByRegCenterIdResponseDTO);
			}else {
				throw new BookingDataNotFoundException(ErrorCodes.PRG_BOOK_RCI_013.toString(),
						ErrorMessages.BOOKING_DATA_NOT_FOUND.toString());
			}
			responseDto.setResTime(serviceUtil.getCurrentResponseTime());
			responseDto.setStatus(true);
			responseDto.setResponse(preRegIdsByRegCenterIdResponseDTOList);
		} catch (DataAccessLayerException e) {
			throw new TablenotAccessibleException(ErrorCodes.PRG_BOOK_RCI_010.toString(),
					ErrorMessages.BOOKING_TABLE_NOT_ACCESSIBLE.toString(), e.getCause());
		}
		return responseDto;
	}

}
