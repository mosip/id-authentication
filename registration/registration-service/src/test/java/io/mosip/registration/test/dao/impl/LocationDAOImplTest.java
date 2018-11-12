package io.mosip.registration.test.dao.impl;

import static org.junit.Assert.assertEquals;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.mosip.registration.dao.impl.LocationDAOImpl;
import io.mosip.registration.entity.GenericId;
import io.mosip.registration.entity.Location;
import io.mosip.registration.repositories.LocationRepository;

public class LocationDAOImplTest {
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	private LocationDAOImpl registrationLocationDAOImpl;
	@Mock
	private LocationRepository registrationLocationRepository;

	@Test
	public void test() {

		List<Location> list = new ArrayList<>();
		Location location = new Location();
		location.setName("name");
		GenericId genericId = new GenericId();
		genericId.setCode("code");
		genericId.setActive(true);
		location.setLocationId(genericId);

		location.setLocationId(genericId);
		location.setCreatedBy("createdBy");
		location.setCreatedDate(new Timestamp(new Date().getTime()));
		location.setDeleted(true);
		location.setDeletedTimesZone(new Timestamp(new Date().getTime()));
		location.setUpdatedBy("updatedBy");
		location.setUpdatedTimesZone(new Timestamp(new Date().getTime()));
		location.setLanguageCode("languageCode");
		location.setHeirarchyLevel(0);
		location.setHeirarchyLevelName("heirarchyLevelName");
		location.setParentLocationCode("parentLocationCode");
		list.add(location);
		Mockito.when(registrationLocationRepository.findAll()).thenReturn(list);
		assertEquals(list, registrationLocationDAOImpl.getLocations());

	}

}
