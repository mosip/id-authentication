package io.mosip.kernel.masterdata.test.controller;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.mockito.Mock;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import io.mosip.kernel.masterdata.dto.RegistrationCenterDto;
import io.mosip.kernel.masterdata.entity.Holiday;
import io.mosip.kernel.masterdata.entity.HolidayId;
import io.mosip.kernel.masterdata.entity.RegistrationCenter;
import io.mosip.kernel.masterdata.repository.HolidayRepository;
import io.mosip.kernel.masterdata.repository.RegistrationCenterRepository;
import io.mosip.kernel.masterdata.utils.ObjectMapperUtil;

//@RunWith(SpringRunner.class)
//@SpringBootTest
//@AutoConfigureMockMvc
//@SuppressWarnings("unchecked")
public class RegistrationCenterControllerExceptionTest {
	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private HolidayRepository holidayRepository;
	@MockBean
	private RegistrationCenterRepository registrationCenterRepository;

	@MockBean
	private ModelMapper mapper;

	@Mock
	ModelMapper modelMapper;

	@MockBean
	ObjectMapperUtil mapperUtil;

	private RegistrationCenter registrationCenter;
	private List<Holiday> holidays;
	private RegistrationCenterDto dto;

	@Before
	public void prepareData() throws ParseException {
		LocalDateTime specificDate = LocalDateTime.of(2018, Month.JANUARY, 1, 10, 10, 30);
		LocalDate date = LocalDate.of(2018, Month.NOVEMBER, 7);
		registrationCenter = new RegistrationCenter();
		registrationCenter.setAddressLine1("7th Street");
		registrationCenter.setAddressLine2("Lane 2");
		registrationCenter.setAddressLine3("Mylasandra-560001");
		registrationCenter.setIsActive(true);
		registrationCenter.setCenterTypeCode("PAR");
		registrationCenter.setContactPhone("987654321");
		registrationCenter.setCreatedBy("John");
		registrationCenter.setCreatedtimes(specificDate);
		registrationCenter.setHolidayLocationCode("KAR");
		registrationCenter.setLocationCode("KAR_59");
		registrationCenter.setId("REG_CR_001");
		registrationCenter.setLanguageCode("ENG");
		registrationCenter.setWorkingHours("9");
		registrationCenter.setLatitude("12.87376");
		registrationCenter.setLongitude("12.76372");
		registrationCenter.setName("RV Niketan REG CENTER");

		holidays = new ArrayList<>();

		Holiday holiday = new Holiday();
		holiday.setHolidayId(new HolidayId(1, "KAR", date, "ENG"));
		holiday.setHolidayName("Diwali");
		holiday.setCreatedBy("John");
		holiday.setCreatedtimes(specificDate);
		holiday.setHolidayDesc("Diwali");
		holiday.setIsActive(true);

		holidays.add(holiday);

		dto = new RegistrationCenterDto();
		dto.setId("KAR-001");

	}

}
