package io.mosip.kernel.masterdata.test.integration;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import io.mosip.kernel.core.dataaccess.exception.DataAccessLayerException;
import io.mosip.kernel.masterdata.dto.RegistrationCenterDto;
import io.mosip.kernel.masterdata.entity.Holiday;
import io.mosip.kernel.masterdata.entity.HolidayId;
import io.mosip.kernel.masterdata.entity.RegistrationCenter;
import io.mosip.kernel.masterdata.repository.HolidayRepository;
import io.mosip.kernel.masterdata.repository.RegistrationCenterRepository;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class RegistrationCenterIntegrationExceptionTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	RegistrationCenterRepository repository;

	@MockBean
	ModelMapper modelMapper;

	RegistrationCenter center;

	List<RegistrationCenter> centers = new ArrayList<>();

	@MockBean
	HolidayRepository holidayRepository;
	private List<Holiday> holidays;

	@Before
	public void setInitials() {
		center = new RegistrationCenter();
		center.setId("1");
		center.setName("bangalore");
		center.setLatitude("12.9180722");
		center.setLongitude("77.5028792");
		center.setLanguageCode("ENG");
	}

	@Before
	public void prepareData() throws ParseException {
		LocalDateTime specificDate = LocalDateTime.of(2018, Month.JANUARY, 1, 10, 10, 30);
		LocalDate date = LocalDate.of(2018, Month.NOVEMBER, 7);
		holidays = new ArrayList<>();
		Holiday holiday1 = new Holiday();
		Holiday holiday = new Holiday();
		holiday.setHolidayId(new HolidayId(1, "KAR", date, "ENG"));
		holiday.setHolidayName("Diwali");
		holiday.setCreatedBy("John");
		holiday.setCreatedtime(specificDate);
		holiday.setHolidayDesc("Diwali");
		holiday.setActive(true);

		Holiday holiday2 = new Holiday();
		holiday2.setHolidayId(new HolidayId(1, "KAR", date, "ENG"));
		holiday2.setHolidayName("Diwali");
		holiday2.setCreatedBy("John");
		holiday2.setCreatedtime(specificDate);
		holiday2.setHolidayDesc("Diwali");
		holiday2.setActive(true);

		holidays.add(holiday1);
		holidays.add(holiday2);

	}

	@Test
	public void getSpecificRegistrationCenterByIdAndLangCodeNotFoundExceptionTest() throws Exception {
		when(repository.findByIdAndLanguageCode("1", "ENG")).thenReturn(null);

		mockMvc.perform(get("/registrationcenters/1/ENG").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());

	}

	@Test
	public void getSpecificRegistrationCenterByIdAndLangCodeNotMappingExceptionTest() throws Exception {
		when(repository.findByIdAndLanguageCode("1", "ENG")).thenReturn(center);
		when(modelMapper.map(Mockito.any(), Mockito.eq(RegistrationCenterDto.class)))
				.thenThrow(IllegalArgumentException.class);
		mockMvc.perform(get("/registrationcenters/1/ENG").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotAcceptable());

	}

	@Test
	public void getSpecificRegistrationCenterByIdAndLangCodeFetchExceptionTest() throws Exception {

		when(repository.findByIdAndLanguageCode("1", "ENG")).thenThrow(DataAccessLayerException.class);

		mockMvc.perform(get("/registrationcenters/1/ENG").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotAcceptable());

	}

	@Test
	public void getCoordinateSpecificRegistrationCentersRegistrationCenterNotFoundExceptionTest() throws Exception {
		when(repository.findRegistrationCentersByLat(12.9180022, 77.5028892, 0.999785939, "ENG")).thenReturn(centers);
		mockMvc.perform(get("/getcoordinatespecificregistrationcenters/ENG/77.5028892/12.9180022/1609")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound()).andReturn();
	}

	@Test
	public void getCoordinateSpecificRegistrationCentersRegistrationCenterFetchExceptionTest() throws Exception {
		when(repository.findRegistrationCentersByLat(12.9180022, 77.5028892, 0.999785939, "ENG"))
				.thenThrow(DataAccessLayerException.class);
		mockMvc.perform(get("/getcoordinatespecificregistrationcenters/ENG/77.5028892/12.9180022/1609")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotAcceptable()).andReturn();
	}

	@Test
	public void getCoordinateSpecificRegistrationCentersRegistrationCenterMappingExceptionTest() throws Exception {
		centers.add(center);
		when(repository.findRegistrationCentersByLat(12.9180022, 77.5028892, 0.999785939, "ENG")).thenReturn(centers);
		when(modelMapper.map(Mockito.any(), Mockito.eq(new TypeToken<List<RegistrationCenterDto>>() {
		}.getType()))).thenThrow(IllegalArgumentException.class);
		mockMvc.perform(get("/getcoordinatespecificregistrationcenters/ENG/77.5028892/12.9180022/1609")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotAcceptable()).andReturn();
	}

	@Test
	public void getCoordinateSpecificRegistrationCentersNumberFormatExceptionTest() throws Exception {
		mockMvc.perform(get("/getcoordinatespecificregistrationcenters/ENG/77.5028892/12.9180022/ae")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotAcceptable()).andReturn();
	}

	@Test
	public void getSpecificRegistrationCenterByLocationCodeAndLangCodeNotFoundExceptionTest() throws Exception {
		when(repository.findByLocationCodeAndLanguageCode("ENG", "BLR")).thenReturn(null);

		mockMvc.perform(get("/getlocspecificregistrationcenters/ENG/BLR").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());

	}

	@Test
	public void getSpecificRegistrationCenterByLocationCodeAndLangCodeNotMappingExceptionTest() throws Exception {
		centers.add(center);
		when(repository.findByLocationCodeAndLanguageCode("BLR", "ENG")).thenReturn(centers);
		when(modelMapper.map(Mockito.any(), Mockito.eq(new TypeToken<List<RegistrationCenterDto>>() {
		}.getType()))).thenThrow(IllegalArgumentException.class);
		mockMvc.perform(get("/getlocspecificregistrationcenters/ENG/BLR").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotAcceptable());

	}

	@Test
	public void getSpecificRegistrationCenterByLocationCodeAndLangCodeFetchExceptionTest() throws Exception {

		when(repository.findByLocationCodeAndLanguageCode("BLR", "ENG")).thenThrow(DataAccessLayerException.class);

		mockMvc.perform(get("/getlocspecificregistrationcenters/ENG/BLR").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotAcceptable());

	}

}
