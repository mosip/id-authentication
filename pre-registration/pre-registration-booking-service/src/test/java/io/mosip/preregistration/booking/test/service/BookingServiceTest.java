package io.mosip.preregistration.booking.test.service;

import static org.junit.Assert.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.preregistration.booking.dto.AvailabilityDto;
import io.mosip.preregistration.booking.dto.DateTimeDto;
import io.mosip.preregistration.booking.dto.ResponseDto;
import io.mosip.preregistration.booking.dto.SlotDto;
import io.mosip.preregistration.booking.entity.AvailibityEntity;
import io.mosip.preregistration.booking.repository.BookingRepository;
import io.mosip.preregistration.booking.service.BookingService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BookingServiceTest {

	@MockBean
	private BookingRepository repository;
	
	@MockBean
	RestTemplateBuilder restTemplateBuilder;
	
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private BookingService service;
	
	private AvailabilityDto availability= new AvailabilityDto();
	private List<DateTimeDto> dateList= new ArrayList<>();
	private DateTimeDto dateDto= new DateTimeDto();
	private List<SlotDto> slotsList= new ArrayList<>();
	private AvailibityEntity entity= new AvailibityEntity();
	private SlotDto slots= new SlotDto();
	
	@Before
	public void setup() {
		DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String date1 = "2016-11-09 09:00:00";
		String date2 ="2016-11-09 09:20:00";
		LocalDateTime localDateTime1 = LocalDateTime.parse(date1, format);
		LocalDateTime localDateTime2 = LocalDateTime.parse(date2, format);
		LocalTime localTime1 = localDateTime1.toLocalTime();
		LocalTime localTime2=localDateTime2.toLocalTime();
		slots.setAvailability(4);
		slots.setFromTime(localTime1);
		slots.setToTime(localTime2);
		slotsList.add(slots);
		dateDto.setDate("2018-12-04");
		dateDto.setHoliday(true);
		dateDto.setTimeSlots(slotsList);
		dateList.add(dateDto);
		availability.setCenterDetails(dateList);
		availability.setRegCenterId("1");
		
		entity.setAvailabilityNo(4);
		entity.setRegcntrId("1");
		entity.setRegDate("2018-12-04");
		entity.setToTime(localTime2);
		entity.setIsActive(true);
		entity.setFromTime(localTime1);
		
		
	
		
	}
	
	@Test
	public void getAvailabilityTest() {
		logger.info("Availability dto "+availability);
		List<String> date= new ArrayList<>();
		List<AvailibityEntity> entityList= new ArrayList<>();
		date.add("2018-12-04");
		entityList.add(entity);
		logger.info("Availability entity "+entity);
		Mockito.when(repository.findDate(Mockito.anyString())).thenReturn(date);
		Mockito.when(repository.findByRegcntrIdAndRegDate(Mockito.anyString(),Mockito.anyString())).thenReturn(entityList);
		ResponseDto<AvailabilityDto> responseDto= service.getAvailability("1");
		logger.info("Response "+responseDto);
		assertEquals(responseDto.getResponse().getRegCenterId(),"1");
	
	}
}
