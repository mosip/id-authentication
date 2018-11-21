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
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.masterdata.dto.BiometricAttributeDto;
import io.mosip.kernel.masterdata.entity.BiometricAttribute;
import io.mosip.kernel.masterdata.exception.DataNotFoundException;
import io.mosip.kernel.masterdata.exception.MasterDataServiceException;
import io.mosip.kernel.masterdata.repository.BiometricAttributeRepository;
import io.mosip.kernel.masterdata.service.BiometricAttributeService;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class BiometricAttributeServiceTest {
	@MockBean
	BiometricAttributeRepository biometricAttributeRepository;

	@Autowired
	BiometricAttributeService biometricAttributeService;

	List<BiometricAttribute> biometricattributes = null;

	@Before
	public void Setup() {

		biometricattributes = new ArrayList<>();
		BiometricAttribute biometricAttribute = new BiometricAttribute();
		biometricAttribute.setCode("iric_black");
		biometricAttribute.setName("black");
		biometricAttribute.setIsActive(true);
		biometricattributes.add(biometricAttribute);
		BiometricAttribute biometricAttribute1 = new BiometricAttribute();
		biometricAttribute1.setCode("iric_brown");
		biometricAttribute1.setName("brown");
		biometricAttribute1.setIsActive(true);
		biometricattributes.add(biometricAttribute1);

	}

	@Test
	public void getBiometricAttributeTest() {
		String biometricTypeCode = "iric";
		String langCode = "eng";
		Mockito.when(biometricAttributeRepository.findByBiometricTypeCodeAndLangCode(biometricTypeCode, langCode))
				.thenReturn(biometricattributes);

		List<BiometricAttributeDto> attributes = biometricAttributeService.getBiometricAttribute(biometricTypeCode,
				langCode);
		Assert.assertEquals(attributes.get(0).getCode(), biometricattributes.get(0).getCode());
		Assert.assertEquals(attributes.get(0).getName(), biometricattributes.get(0).getName());

	}

	@Test(expected = DataNotFoundException.class)
	public void noRecordsFoudExceptionTest() {
		List<BiometricAttribute> empityList = new ArrayList<BiometricAttribute>();
		String biometricTypeCode = "face";
		String langCode = "eng";
		Mockito.when(biometricAttributeRepository.findByBiometricTypeCodeAndLangCode(biometricTypeCode, langCode))
				.thenReturn(empityList);
		biometricAttributeService.getBiometricAttribute(biometricTypeCode, langCode);

	}

	@Test(expected = DataNotFoundException.class)
	public void noRecordsFoudExceptionForNullTest() {
		String biometricTypeCode = "face";
		String langCode = "eng";
		Mockito.when(biometricAttributeRepository.findByBiometricTypeCodeAndLangCode(biometricTypeCode, langCode))
				.thenReturn(null);
		biometricAttributeService.getBiometricAttribute(biometricTypeCode, langCode);

	}

	@Test(expected = MasterDataServiceException.class)
	public void dataAccessExceptionInGetAllTest() {
		String biometricTypeCode = "face";
		String langCode = "eng";
		Mockito.when(biometricAttributeRepository.findByBiometricTypeCodeAndLangCode(biometricTypeCode, langCode))
				.thenThrow(DataAccessResourceFailureException.class);
		biometricAttributeService.getBiometricAttribute(biometricTypeCode, langCode);

	}

}
