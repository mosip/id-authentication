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

import io.mosip.kernel.masterdata.entity.BiometricType;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.BiometricTypeRepository;
import io.mosip.kernel.masterdata.service.BiometricTypeService;
import io.mosip.kernel.masterdata.utils.ObjectMapperUtil;

/**
 * @author Neha
 * @since 1.0.0
 *
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class BiometricTypeServiceExceptionTest {

	@MockBean
	private BiometricTypeRepository biometricTypeRepository;

	@Autowired
	private BiometricTypeService biometricTypeService;

	private BiometricType biometricType1 = new BiometricType();
	private BiometricType biometricType2 = new BiometricType();

	List<BiometricType> biometricTypeList = new ArrayList<>();

	@MockBean
	private ObjectMapperUtil objectMapperUtil;

	@MockBean
	private ModelMapper modelMapper;

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

	@Test(expected = MasterDataServiceException.class)
	public void getAllBiometricTypesFetchException() {
		Mockito.when(biometricTypeRepository.findAllByIsActiveTrueAndIsDeletedFalse(Mockito.eq(BiometricType.class)))
				.thenThrow(DataRetrievalFailureException.class);
		biometricTypeService.getAllBiometricTypes();
	}

	@Test(expected = DataNotFoundException.class)
	public void getAllBiometricTypesNotFoundException() {
		biometricTypeList = new ArrayList<>();
		Mockito.when(biometricTypeRepository.findAllByIsActiveTrueAndIsDeletedFalse(BiometricType.class))
				.thenReturn(biometricTypeList);
		biometricTypeService.getAllBiometricTypes();
	}

	@Test(expected = MasterDataServiceException.class)
	public void getAllBiometricTypesByLanguageCodeFetchException() {
		Mockito.when(biometricTypeRepository.findAllByLangCodeAndIsActiveTrueAndIsDeletedFalse(Mockito.anyString()))
				.thenThrow(DataRetrievalFailureException.class);
		biometricTypeService.getAllBiometricTypesByLanguageCode(Mockito.anyString());
	}

	@Test(expected = DataNotFoundException.class)
	public void getAllBiometricTypesByLanguageCodeNotFoundException() {
		Mockito.when(biometricTypeRepository.findAllByLangCodeAndIsActiveTrueAndIsDeletedFalse(Mockito.anyString()))
				.thenReturn(new ArrayList<BiometricType>());
		biometricTypeService.getAllBiometricTypesByLanguageCode(Mockito.anyString());
	}

	@Test(expected = MasterDataServiceException.class)
	public void getBiometricTypeByCodeAndLangCodeFetchException() {
		Mockito.when(biometricTypeRepository.findByCodeAndLangCodeAndIsActiveTrueAndIsDeletedFalse(Mockito.anyString(),
				Mockito.anyString())).thenThrow(DataRetrievalFailureException.class);
		biometricTypeService.getBiometricTypeByCodeAndLangCode(Mockito.anyString(), Mockito.anyString());
	}

	@Test(expected = DataNotFoundException.class)
	public void getBiometricTypeByCodeAndLangCodeNotFoundException() {
		Mockito.when(biometricTypeRepository.findByCodeAndLangCodeAndIsActiveTrueAndIsDeletedFalse(Mockito.anyString(),
				Mockito.anyString())).thenReturn(null);
		biometricTypeService.getBiometricTypeByCodeAndLangCode(Mockito.anyString(), Mockito.anyString());
	}
}
