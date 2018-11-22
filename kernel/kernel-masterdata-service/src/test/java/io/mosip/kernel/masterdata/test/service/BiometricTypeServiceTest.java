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

import io.mosip.kernel.masterdata.dto.BiometricTypeDto;
import io.mosip.kernel.masterdata.entity.BiometricType;
import io.mosip.kernel.masterdata.repository.BiometricTypeRepository;
import io.mosip.kernel.masterdata.service.BiometricTypeService;

/**
 * @author Neha
 * @since 1.0.0
 *
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class BiometricTypeServiceTest {

	@MockBean
	private BiometricTypeRepository biometricTypeRepository;

	@Autowired
	private BiometricTypeService biometricTypeService;

	private BiometricType biometricType1 = new BiometricType();
	private BiometricType biometricType2 = new BiometricType();

	List<BiometricType> biometricTypeList = new ArrayList<>();

	@Before
	public void setUp() {
		biometricType1.setCode("1");
		biometricType1.setName("DNA MATCHING");
		biometricType1.setDescription(null);
		biometricType1.setLangCode("ENG");
		biometricType1.setIsActive(true);
		biometricType1.setCreatedBy("Neha");
		biometricType1.setUpdatedBy(null);
		biometricType1.setIsDeleted(false);

		biometricType2.setCode("3");
		biometricType2.setName("EYE SCAN");
		biometricType2.setDescription(null);
		biometricType2.setLangCode("ENG");
		biometricType2.setIsActive(true);
		biometricType2.setCreatedBy("Neha");
		biometricType2.setUpdatedBy(null);
		biometricType2.setIsDeleted(false);

		biometricTypeList.add(biometricType1);
		biometricTypeList.add(biometricType2);
	}

	@Test
	public void getAllBiometricTypesSuccess() {
		Mockito.when(biometricTypeRepository.findAllByIsActiveTrueAndIsDeletedFalse(Mockito.eq(BiometricType.class))).thenReturn(biometricTypeList);
		List<BiometricTypeDto> biometricTypeDtoList = biometricTypeService.getAllBiometricTypes();
		assertEquals(biometricTypeList.get(0).getCode(), biometricTypeDtoList.get(0).getCode());
		assertEquals(biometricTypeList.get(0).getName(), biometricTypeDtoList.get(0).getName());
	}

	@Test
	public void getAllBiometricTypesByLanguageCodeSuccess() {
		Mockito.when(biometricTypeRepository.findAllByLangCodeAndIsActiveTrueAndIsDeletedFalse(Mockito.anyString())).thenReturn(biometricTypeList);
		List<BiometricTypeDto> biometricTypeDtoList = biometricTypeService
				.getAllBiometricTypesByLanguageCode(Mockito.anyString());
		assertEquals(biometricTypeList.get(0).getCode(), biometricTypeDtoList.get(0).getCode());
		assertEquals(biometricTypeList.get(0).getName(), biometricTypeDtoList.get(0).getName());
	}

	@Test
	public void getBiometricTypeByCodeAndLangCodeSuccess() {
		Mockito.when(biometricTypeRepository.findByCodeAndLangCodeAndIsActiveTrueAndIsDeletedFalse(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(biometricType1);
		BiometricTypeDto actual = biometricTypeService.getBiometricTypeByCodeAndLangCode(Mockito.anyString(),
				Mockito.anyString());
		assertEquals(biometricType1.getCode(), actual.getCode());
		assertEquals(biometricType1.getName(), actual.getName());
	}

}