package io.mosip.kernel.masterdata.test.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.masterdata.entity.Application;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.ApplicationRepository;
import io.mosip.kernel.masterdata.service.ApplicationService;
import io.mosip.kernel.masterdata.utils.ObjectMapperUtil;

/**
 * @author Neha
 * @since 1.0.0
 *
 */

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class ApplicationServiceExceptionTest {

	@MockBean
	private ApplicationRepository applicationRepository;

	@Autowired
	private ApplicationService applicationService;

	private Application application1 = new Application();
	private Application application2 = new Application();

	private List<Application> applicationList = new ArrayList<>();

	@MockBean
	private ObjectMapperUtil objectMapperUtil;

	@MockBean
	private ModelMapper modelMapper;

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

	@Test(expected = MasterDataServiceException.class)
	public void getAllApplicationFetchException() {
		Mockito.when(applicationRepository.findAllByIsActiveTrueAndIsDeletedFalse(Mockito.eq(Application.class)))
				.thenThrow(DataRetrievalFailureException.class);
		applicationService.getAllApplication();
	}

	@Test(expected = DataNotFoundException.class)
	public void getAllApplicationNotFoundException() {
		applicationList = new ArrayList<>();
		Mockito.when(applicationRepository.findAllByIsActiveTrueAndIsDeletedFalse(Application.class))
				.thenReturn(applicationList);
		applicationService.getAllApplication();
	}

	@Test(expected = MasterDataServiceException.class)
	public void getAllApplicationByLanguageCodeFetchException() {
		Mockito.when(applicationRepository.findAllByLanguageCodeAndIsActiveTrueAndIsDeletedFalse(Mockito.anyString()))
				.thenThrow(DataRetrievalFailureException.class);
		applicationService.getAllApplicationByLanguageCode(Mockito.anyString());
	}

	@Test(expected = DataNotFoundException.class)
	public void getAllApplicationByLanguageCodeNotFoundException() {
		Mockito.when(applicationRepository.findAllByLanguageCodeAndIsActiveTrueAndIsDeletedFalse(Mockito.anyString()))
				.thenReturn(new ArrayList<Application>());
		applicationService.getAllApplicationByLanguageCode(Mockito.anyString());
	}

	@Test(expected = MasterDataServiceException.class)
	public void getApplicationByCodeAndLangCodeFetchException() {
		Mockito.when(applicationRepository
				.findByCodeAndLanguageCodeAndIsActiveTrueAndIsDeletedFalse(Mockito.anyString(), Mockito.anyString()))
				.thenThrow(DataRetrievalFailureException.class);
		applicationService.getApplicationByCodeAndLanguageCode(Mockito.anyString(), Mockito.anyString());
	}

	@Test(expected = DataNotFoundException.class)
	public void getApplicationByCodeAndLangCodeNotFoundException() {
		Mockito.when(applicationRepository
				.findByCodeAndLanguageCodeAndIsActiveTrueAndIsDeletedFalse(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(null);
		applicationService.getApplicationByCodeAndLanguageCode(Mockito.anyString(), Mockito.anyString());
	}
}
