package io.mosip.kernel.masterdata.test.service;

import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.internal.util.reflection.FieldSetter;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import org.springframework.beans.factory.annotation.Autowired;

import io.mosip.kernel.masterdata.entity.Location;
import io.mosip.kernel.masterdata.repository.LocationRepository;
import io.mosip.kernel.masterdata.service.impl.LocationServiceImpl;

@RunWith(PowerMockRunner.class)
@PrepareForTest(LocationServiceImpl.class)
public class LocationHierarchyTest {
    
	@Mock
	List<Location> mockList;
	@Autowired
	LocationRepository locationRepo;

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
	public void getParentListTest() throws Exception {
		/*
		 * LocationHierarchyImpl locationHierarchyImpl = new LocationHierarchyImpl();
		 * PowerMockito.spy(locationHierarchyImpl);
		 */
		/*
		 * PowerMockito.doReturn(locationHierarchies).when(locationHierarchyImpl,
		 * "getLocationHierarchyList", Mockito.anyString(), Mockito.anyString());
		 */

		@SuppressWarnings("unchecked")
		List<Location> mockPoint = mock(List.class);
		
		LocationServiceImpl hierarchyImplSpy = PowerMockito.spy(new LocationServiceImpl());
		FieldSetter.setField(hierarchyImplSpy, LocationServiceImpl.class.getDeclaredField("parentHierarchyList"), mockList);
		PowerMockito.doReturn(mockPoint).when(hierarchyImplSpy, "getLocationHierarchyList", ArgumentMatchers.any(),
				ArgumentMatchers.any());
		
		//PowerMockito.whenNew(Locat)
		
		

		// PowerMockito.doReturn(locationHierarchies).when(
		// locationRepo.findLocationHierarchyByCodeAndLanguageCode(Mockito.anyString(),
		// Mockito.anyString()));

		Whitebox.invokeMethod(hierarchyImplSpy, "getParentList", "KAR", "KAN");
	}
}

