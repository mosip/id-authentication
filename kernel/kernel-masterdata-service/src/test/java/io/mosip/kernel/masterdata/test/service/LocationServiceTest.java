package io.mosip.kernel.masterdata.test.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
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

import io.mosip.kernel.masterdata.dto.LocationDto;
import io.mosip.kernel.masterdata.dto.LocationResponseDto;
import io.mosip.kernel.masterdata.entity.Location;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.repository.LocationRepository;
import io.mosip.kernel.masterdata.service.LocationService;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc

public class LocationServiceTest {

	@MockBean
	LocationRepository locationHierarchyRepository;

	@Autowired
	LocationService locationHierarchyService;

	List<Location> locationHierarchies = null;

	@Before
	public void Setup() {

		locationHierarchies = new ArrayList<>();
		Location locationHierarchy = new Location();
		locationHierarchy.setCode("IND");
		locationHierarchy.setName("INDIA");
		locationHierarchy.setHierarchyLevel(0);
		locationHierarchy.setHierarchyName(null);
		locationHierarchy.setParentLocCode(null);
		locationHierarchy.setLanguageCode("HIN");
		locationHierarchy.setCreatedBy("dfs");
		locationHierarchy.setUpdatedBy("sdfsd");
		locationHierarchy.setIsActive(true);
		locationHierarchies.add(locationHierarchy);
		Location locationHierarchy1 = new Location();
		locationHierarchy1.setCode("KAR");
		locationHierarchy1.setName("KARNATAKA");
		locationHierarchy1.setHierarchyLevel(1);
		locationHierarchy1.setHierarchyName(null);
		locationHierarchy1.setParentLocCode("TEST");
		locationHierarchy1.setLanguageCode("KAN");
		locationHierarchy1.setCreatedBy("dfs");
		locationHierarchy1.setUpdatedBy("sdfsd");
		locationHierarchy1.setIsActive(true);
		locationHierarchies.add(locationHierarchy1);

	}

	@Test
	public void getAllLocationHierarchyDetailsTest() {
		Mockito.when(locationHierarchyRepository.findAll()).thenReturn(locationHierarchies);

		LocationResponseDto locationHierarchyResponseDto = locationHierarchyService
				.getLocationDetails();

		LocationDto locationHierarchyDto = locationHierarchyResponseDto.getLocations().get(0);
		Assert.assertEquals(locationHierarchyDto.getLocationCode(), locationHierarchies.get(0).getCode());

	}

	@Test(expected = DataNotFoundException.class)
	public void noRecordsFoudExceptionTest() {
		Mockito.when(locationHierarchyRepository.findAll()).thenReturn(null);
		locationHierarchyService.getLocationDetails();

	}
	
	@Test(expected = DataNotFoundException.class)
	public void noRecordsFoudExceptionEmptyListTest() {
		Mockito.when(locationHierarchyRepository.findAll()).thenReturn(new ArrayList<Location>());
		locationHierarchyService.getLocationDetails();

	}

	@Test(expected = MasterDataServiceException.class)
	public void dataAccessExceptionTestInGetAll() {
		Mockito.when(locationHierarchyRepository.findAll()).thenThrow(DataRetrievalFailureException.class);
		locationHierarchyService.getLocationDetails();

	}

	@Test()
	public void getLocationHierachyBasedOnLangAndLoc() {
		// locationHierarchyImpl = PowerMockito.mock(LocationHierarchyImpl.class);
		Mockito.when(locationHierarchyRepository.findLocationHierarchyByCodeAndLanguageCode("IND", "HIN"))
				.thenReturn(locationHierarchies);

		LocationResponseDto locationHierarchyResponseDto = locationHierarchyService
				.getLocationHierarchyByLangCode("IND", "HIN");
		Assert.assertEquals(locationHierarchyResponseDto.getLocations().get(0).getLocationCode(), "IND");

	}

	@Test(expected = DataNotFoundException.class)
	public void getLocationHierarchyExceptionTest() {
		Mockito.when(locationHierarchyRepository.findLocationHierarchyByCodeAndLanguageCode("IND", "HIN"))
				.thenReturn(null);
		locationHierarchyService.getLocationHierarchyByLangCode("IND", "HIN");

	}
	
	@Test(expected = DataNotFoundException.class)
	public void getLocationHierarchyExceptionTestWithEmptyList() {
		Mockito.when(locationHierarchyRepository.findLocationHierarchyByCodeAndLanguageCode("IND", "HIN"))
				.thenReturn(new ArrayList<Location>());
		locationHierarchyService.getLocationHierarchyByLangCode("IND", "HIN");

	}

	@Test(expected = MasterDataServiceException.class)
	public void locationHierarchyDataAccessExceptionTest() {
		Mockito.when(locationHierarchyRepository.findLocationHierarchyByCodeAndLanguageCode("IND", "HIN"))
				.thenThrow(DataRetrievalFailureException.class);
		locationHierarchyService.getLocationHierarchyByLangCode("IND", "HIN");
	}

}
