package io.mosip.preregistration.booking.service;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import io.mosip.preregistration.booking.dto.AvailabilityDto;
import io.mosip.preregistration.booking.dto.DateTimeDto;
import io.mosip.preregistration.booking.dto.HolidayDto;
import io.mosip.preregistration.booking.dto.RegistrationCenterDto;
import io.mosip.preregistration.booking.dto.RegistrationCenterHolidayDto;
import io.mosip.preregistration.booking.dto.RegistrationCenterResponseDto;
import io.mosip.preregistration.booking.dto.ResponseDto;
import io.mosip.preregistration.booking.dto.SlotDto;
import io.mosip.preregistration.booking.entity.AvailabilityPK;
import io.mosip.preregistration.booking.entity.AvailibityEntity;
import io.mosip.preregistration.booking.exception.TablenotAccessibleException;
import io.mosip.preregistration.booking.repository.BookingRepository;

@Component
public class BookingService {

	private RestTemplate restTemplate;

	@Autowired
	RestTemplateBuilder restTemplateBuilder;

	@Autowired
	BookingRepository bookingRepository;

	@Value("${regCenter.url}")
	String regCenterUrl;

	@Value("${holiday.url}")
	String holidayListUrl;

	public ResponseDto<String> addAvailability() {
		ResponseDto<String> response = new ResponseDto<>();

		try {
			restTemplate = restTemplateBuilder.build();
			UriComponentsBuilder regbuilder = UriComponentsBuilder.fromHttpUrl(regCenterUrl);
			String date = LocalDate.now().getYear() + "/12/02";
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
			LocalDate endDate = LocalDate.parse(date, formatter);
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON_UTF8);

			HttpEntity<RegistrationCenterResponseDto> entity = new HttpEntity<>(headers);

			String uriBuilder = regbuilder.build().encode().toUriString();
			ResponseEntity<RegistrationCenterResponseDto> responseEntity = restTemplate.exchange(uriBuilder,
					HttpMethod.GET, entity, RegistrationCenterResponseDto.class);
			List<RegistrationCenterDto> regCenter = responseEntity.getBody().getRegistrationCenters();

			for (RegistrationCenterDto regDto : regCenter) {
				String holidayUrl = holidayListUrl + regDto.getLanguageCode() + "/" + 1 + "/2018";

				UriComponentsBuilder builder2 = UriComponentsBuilder.fromHttpUrl(holidayUrl);

				HttpEntity<RegistrationCenterHolidayDto> entity2 = new HttpEntity<>(headers);

				String uriBuilder2 = builder2.build().encode().toUriString();
				ResponseEntity<RegistrationCenterHolidayDto> responseEntity2 = restTemplate.exchange(uriBuilder2,
						HttpMethod.GET, entity2, RegistrationCenterHolidayDto.class);

				List<String> holidaylist = new ArrayList<String>();
				for (HolidayDto holiday : responseEntity2.getBody().getHolidays()) {
					holidaylist.add(holiday.getHolidayDate());
				}
				holidaylist.add("2018-11-30");

				for (LocalDate sDate = LocalDate.now(); (sDate.isBefore(endDate)
						|| sDate.isEqual(endDate)); sDate = sDate.plusDays(1)) {
					if (holidaylist.contains(sDate.toString())) {
						DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
						String text = "2016-11-09 00:00:00";
						LocalDateTime localDateTime = LocalDateTime.parse(text, format);
						LocalTime localTime = localDateTime.toLocalTime();
						saveAvailability(regDto, sDate, localTime, localTime);

					} else {

						int loop1 = ((regDto.getLunchStartTime().getHour() * 60
								+ regDto.getLunchStartTime().getMinute())
								- (regDto.getCenterStartTime().getHour() * 60
										+ regDto.getCenterStartTime().getMinute()))
								/ regDto.getPerKioskProcessTime().getMinute();

						int loop2 = ((regDto.getCenterEndTime().getHour() * 60 + regDto.getCenterEndTime().getMinute())
								- (regDto.getLunchEndTime().getHour() * 60 + regDto.getLunchEndTime().getMinute()))
								/ regDto.getPerKioskProcessTime().getMinute();

						int extraTime1 = ((regDto.getLunchStartTime().getHour() * 60
								+ regDto.getLunchStartTime().getMinute())
								- (regDto.getCenterStartTime().getHour() * 60
										+ regDto.getCenterStartTime().getMinute()))
								% regDto.getPerKioskProcessTime().getMinute();

						int extraTime2 = ((regDto.getCenterEndTime().getHour() * 60
								+ regDto.getCenterEndTime().getMinute())
								- (regDto.getLunchEndTime().getHour() * 60 + regDto.getLunchEndTime().getMinute()))
								% regDto.getPerKioskProcessTime().getMinute();

						LocalTime currentTime1 = regDto.getCenterStartTime();
						for (int i = 0; i < loop1; i++) {
							if (i == (loop1 - 1)) {
								LocalTime toTime = currentTime1.plusMinutes(regDto.getPerKioskProcessTime().getMinute())
										.plusMinutes(extraTime1);
								saveAvailability(regDto, sDate, currentTime1, toTime);

							} else {
								LocalTime toTime = currentTime1
										.plusMinutes(regDto.getPerKioskProcessTime().getMinute());
								saveAvailability(regDto, sDate, currentTime1, toTime);
							}
							currentTime1 = currentTime1.plusMinutes(regDto.getPerKioskProcessTime().getMinute());
						}

						LocalTime currentTime2 = regDto.getLunchEndTime();
						for (int i = 0; i < loop2; i++) {
							if (i == (loop2 - 1)) {
								LocalTime toTime = currentTime2.plusMinutes(regDto.getPerKioskProcessTime().getMinute())
										.plusMinutes(extraTime2);
								saveAvailability(regDto, sDate, currentTime2, toTime);

							} else {
								LocalTime toTime = currentTime2
										.plusMinutes(regDto.getPerKioskProcessTime().getMinute());
								saveAvailability(regDto, sDate, currentTime2, toTime);
							}
							currentTime2 = currentTime2.plusMinutes(regDto.getPerKioskProcessTime().getMinute());
						}
					}
				}
			}
		} catch (HttpClientErrorException e) {

		} catch (DataAccessException e) {
			throw new TablenotAccessibleException("Table not accessable ");
		}
		
		response.setResTime(new Timestamp(System.currentTimeMillis()));
		response.setStatus(true);
		response.setResponse("Master Data is synched successfully");
		return response;

	}

	public ResponseDto<AvailabilityDto> getAvailability(String regID) {
		ResponseDto<AvailabilityDto> response= new ResponseDto<>();
		String date = LocalDate.now().getYear() + "/11/01";
		LocalDate convertedDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy/MM/dd"));
		convertedDate = convertedDate.withDayOfMonth(convertedDate.getMonth().length(convertedDate.isLeapYear()));
		System.out.println("Converted date " + convertedDate);

		List<String> dateList = bookingRepository.findDate(regID);
		AvailabilityDto availability = new AvailabilityDto();
		List<DateTimeDto> dateTimeList = new ArrayList<>();
		for (String day : dateList) {
			DateTimeDto dateTime = new DateTimeDto();
			List<AvailibityEntity> entity = bookingRepository.findByRegcntrIdAndRegDate(regID, day);
			List<SlotDto> slotList = new ArrayList<>();
			for (AvailibityEntity en : entity) {
				SlotDto slots = new SlotDto();
				slots.setAvailability(en.getAvailabilityNo());
				slots.setFromTime(en.getFromTime());
				slots.setToTime(en.getToTime());
				slotList.add(slots);
			}
			if (entity.get(0).getIsActive()) {
				dateTime.setHoliday(false);
			} else {
				dateTime.setHoliday(true);
			}
			dateTime.setTimeSlots(slotList);
			dateTime.setDate(day);
			dateTimeList.add(dateTime);
		}
		availability.setCenterDetails(dateTimeList);
		availability.setRegCenterId(regID);

		response.setResTime(new Timestamp(System.currentTimeMillis()));
		response.setStatus(true);
		response.setResponse(availability);
		
		return response;

	}

	private void saveAvailability(RegistrationCenterDto regDto, LocalDate date, LocalTime currentTime, LocalTime toTime)
			throws TablenotAccessibleException {

		AvailibityEntity avaEntity = new AvailibityEntity();
		avaEntity.setRegDate(date.toString());
		avaEntity.setRegcntrId(regDto.getId());
		avaEntity.setFromTime(currentTime);
		avaEntity.setToTime(toTime);
		
		avaEntity.setCrBy(regDto.getContactPerson());
		if (currentTime.equals(toTime)) {
			avaEntity.setIsActive(false);
			avaEntity.setAvailabilityNo(0);
		} else {
			avaEntity.setAvailabilityNo(regDto.getNumberOfKiosks());
			avaEntity.setIsActive(true);
		}
		bookingRepository.save(avaEntity);
	}

}
