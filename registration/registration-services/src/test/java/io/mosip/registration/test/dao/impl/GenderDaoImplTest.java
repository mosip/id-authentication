package io.mosip.registration.test.dao.impl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import io.mosip.registration.dao.impl.GenderDAOImpl;
import io.mosip.registration.entity.Gender;
import io.mosip.registration.entity.id.GenericId;
import io.mosip.registration.repositories.GenderRepository;

public class GenderDaoImplTest {
	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();
	@InjectMocks
	private GenderDAOImpl registrationGenderDAOImpl;
	@Mock
	private GenderRepository registrationGenderRepository;

	@Test
	public void test() {

		List<Gender> list = new ArrayList<>();
		GenericId genderId = new GenericId();
		genderId.setCode("code");
		genderId.setActive(true);
		Gender gender = new Gender();
		//gender.setGenericId(genderId);
		//gender.setCreatedTimesZone(new Timestamp(new Date().getTime()));
		gender.setCrBy("createdBy");
		//gender.setDeleted(true);
		//gender.setDeletedTimesZone(new Timestamp(new Date().getTime()));
		gender.setLangCode("languageCode");
		gender.setGenderName("name");
		gender.setUpdBy("updatedBy");
		//gender.setUpdatedTimesZone(new Timestamp(new Date().getTime()));
		list.add(gender);
		Mockito.when(registrationGenderRepository.findAll()).thenReturn(list);
		assertEquals(list, registrationGenderDAOImpl.getGenders());

	}

}
