package io.mosip.kernel.masterdata.test.service;

import static org.junit.Assert.assertEquals;

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
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.masterdata.dto.ApplicationDto;
import io.mosip.kernel.masterdata.entity.Application;
import io.mosip.kernel.masterdata.repository.ApplicationRepository;
import io.mosip.kernel.masterdata.service.ApplicationService;

/**
 * @author Neha
 * @since 1.0.0
 *
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class ApplicationServiceTest {

	@MockBean
	private ApplicationRepository applicationRepository;

	@Autowired
	private ApplicationService applicationService;

	private Application application1 = new Application();
	private Application application2 = new Application();

	List<Application> applicationList = new ArrayList<>();

	@Before
	public void setUp() {
		application1.setCode("101");
		application1.setName("pre-registeration");
		application1.setDescription("Pre-registration Application Form");
		application1.setLanguageCode("ENG");
		application1.setIsActive(true);
		application1.setCreatedBy("Neha");
		application1.setUpdatedBy(null);
		application1.setIsDeleted(false);

		application2.setCode("102");
		application2.setName("registeration");
		application2.setDescription("Registeration Application Form");
		application2.setLanguageCode("ENG");
		application2.setIsActive(true);
		application2.setCreatedBy("Neha");
		application2.setUpdatedBy(null);
		application2.setIsDeleted(false);

		applicationList.add(application1);
		applicationList.add(application2);
	}

	@Test
	public void getAllBiometricTypesSuccess() {
		Mockito.when(applicationRepository.findAllByIsActiveTrueAndIsDeletedFalse(Mockito.eq(Application.class))).thenReturn(applicationList);
		List<ApplicationDto> applicationDtoList = applicationService.getAllApplication();
		assertEquals(applicationList.get(0).getCode(), applicationDtoList.get(0).getCode());
		assertEquals(applicationList.get(0).getName(), applicationDtoList.get(0).getName());
	}

	@Test
	public void getAllBiometricTypesByLanguageCodeSuccess() {
		Mockito.when(applicationRepository.findAllByLanguageCodeAndIsActiveTrueAndIsDeletedFalse(Mockito.anyString())).thenReturn(applicationList);
		List<ApplicationDto> applicationDtoList = applicationService
				.getAllApplicationByLanguageCode(Mockito.anyString());
		assertEquals(applicationList.get(0).getCode(), applicationDtoList.get(0).getCode());
		assertEquals(applicationList.get(0).getName(), applicationDtoList.get(0).getName());
	}

	@Test
	public void getBiometricTypeByCodeAndLangCodeSuccess() {
		Mockito.when(applicationRepository.findByCodeAndLanguageCodeAndIsActiveTrueAndIsDeletedFalse(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(application1);
		ApplicationDto actual = applicationService.getApplicationByCodeAndLanguageCode(Mockito.anyString(),
				Mockito.anyString());
		assertEquals(application1.getCode(), actual.getCode());
		assertEquals(application1.getName(), actual.getName());
	}

}