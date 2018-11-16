package io.mosip.kernel.masterdata.test.integration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import io.mosip.kernel.masterdata.entity.Holiday;
import io.mosip.kernel.masterdata.entity.HolidayId;
import io.mosip.kernel.masterdata.repository.HolidayRepository;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
@SuppressWarnings("unchecked")
public class HolidayIntegrationTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private HolidayRepository holidayRepository;

	private List<Holiday> holidays;

	@Before
	public void prepareData() throws ParseException {
		LocalDateTime specificDate = LocalDateTime.of(2018, Month.JANUARY, 1, 10, 10, 30);
		LocalDate date = LocalDate.of(2018, Month.NOVEMBER, 7);
		holidays = new ArrayList<>();
		Holiday holiday = new Holiday();
		holiday.setHolidayId(new HolidayId(1, "KAR", date, "ENG"));
		holiday.setHolidayName("Diwali");
		holiday.setCreatedBy("John");
		holiday.setCreatedtimes(specificDate);
		holiday.setHolidayDesc("Diwali");
		holiday.setIsActive(true);

		Holiday holiday2 = new Holiday();
		holiday2.setHolidayId(new HolidayId(1, "KAH", date, "ENG"));
		holiday2.setHolidayName("Durga Puja");
		holiday2.setCreatedBy("John");
		holiday2.setCreatedtimes(specificDate);
		holiday2.setHolidayDesc("Diwali");
		holiday2.setIsActive(true);

		holidays.add(holiday);
		holidays.add(holiday2);

	}

	@Test
	public void testGetHolidayAllHolidaysSuccess() throws Exception {
		when(holidayRepository.findAll(Holiday.class)).thenReturn(holidays);
		mockMvc.perform(get("/holidays")).andExpect(status().isOk());
	}

	@Test
	public void testGetAllHolidaNoHolidayFound() throws Exception {
		mockMvc.perform(get("/holidays")).andExpect(status().isNotFound());
	}

	@Test
	public void testGetAllHolidaysHolidayFetchException() throws Exception {
		when(holidayRepository.findAll(Mockito.any(Class.class))).thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/holidays")).andExpect(status().isInternalServerError());
	}

	@Test
	public void testGetHolidayByIdSuccess() throws Exception {
		when(holidayRepository.findAllByHolidayIdId(any(Integer.class))).thenReturn(holidays);
		mockMvc.perform(get("/holidays/{holidayId}", 1)).andExpect(status().isOk());
	}

	@Test
	public void testGetHolidayByIdHolidayFetchException() throws Exception {
		when(holidayRepository.findAllByHolidayIdId(any(Integer.class))).thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/holidays/{holidayId}", 1)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testGetHolidayByIdNoHolidayFound() throws Exception {
		mockMvc.perform(get("/holidays/{holidayId}", 1)).andExpect(status().isNotFound());
	}

	@Test
	public void testGetHolidayByIdAndLangCodeSuccess() throws Exception {
		when(holidayRepository.findHolidayByHolidayIdIdAndHolidayIdLangCode(any(Integer.class), anyString()))
				.thenReturn(holidays);
		mockMvc.perform(get("/holidays/{holidayId}/{languagecode}", 1, "ENG")).andExpect(status().isOk());
	}

	@Test
	public void testGetHolidayByIdAndLangCodeHolidayFetchException() throws Exception {
		when(holidayRepository.findHolidayByHolidayIdIdAndHolidayIdLangCode(any(Integer.class), anyString()))
				.thenThrow(DataRetrievalFailureException.class);
		mockMvc.perform(get("/holidays/{holidayId}/{languagecode}", 1, "ENG"))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testGetHolidayByIdAndLangCodeHolidayNoDataFound() throws Exception {
		mockMvc.perform(get("/holidays/{holidayId}/{languagecode}", 1, "ENG")).andExpect(status().isNotFound());
	}
}
