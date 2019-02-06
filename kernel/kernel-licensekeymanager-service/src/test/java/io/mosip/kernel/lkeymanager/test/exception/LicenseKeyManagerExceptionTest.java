package io.mosip.kernel.lkeymanager.test.exception;

import static org.hamcrest.CoreMatchers.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.time.Month;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.lkeymanager.LicenseKeyManagerBootApplication;
import io.mosip.kernel.lkeymanager.dto.LicenseKeyMappingDto;
import io.mosip.kernel.lkeymanager.entity.LicenseKeyList;
import io.mosip.kernel.lkeymanager.entity.LicenseKeyTspMap;
import io.mosip.kernel.lkeymanager.repository.LicenseKeyListRepository;
import io.mosip.kernel.lkeymanager.repository.LicenseKeyPermissionRepository;
import io.mosip.kernel.lkeymanager.repository.LicenseKeyTspMapRepository;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest(classes = LicenseKeyManagerBootApplication.class)
public class LicenseKeyManagerExceptionTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private LicenseKeyListRepository licenseKeyListRepository;

	@MockBean
	private LicenseKeyPermissionRepository licenseKeyPermissionRepository;

	@MockBean
	private LicenseKeyTspMapRepository licenseKeyTspMapRepository;

	private LicenseKeyList licensekeyList;

	private LicenseKeyTspMap licenseKeyTspMap;

	@Before
	public void setUp() {
		licenseKeyListEntitySetUp();
		licenseKeyTspMapSetUp();
	}

	private void licenseKeyTspMapSetUp() {
		licenseKeyTspMap = new LicenseKeyTspMap();
		licenseKeyTspMap.setActive(true);
		licenseKeyTspMap.setCreatedBy("testadmin@mosip.io");
		licenseKeyTspMap.setCreatedDateTimes(LocalDateTime.now());
		licenseKeyTspMap.setDeleted(false);
		licenseKeyTspMap.setLKey("tEsTlIcEnSe");
		licenseKeyTspMap.setTspId("TSP_ID_TEST");

	}

	private void licenseKeyListEntitySetUp() {
		licensekeyList = new LicenseKeyList();
		licensekeyList.setActive(true);
		licensekeyList.setCreatedAt(LocalDateTime.now());
		licensekeyList.setCreatedBy("testadmin@mosip.io");
		licensekeyList.setDeleted(false);
		licensekeyList.setExpiryDateTimes(LocalDateTime.of(2019, Month.FEBRUARY, 2, 6, 23));
		licensekeyList.setLicenseKey("tEsTlIcEnSe");

	}

	/**
	 * TEST SCENARIO : When TSPID and LICENSEKEY entered for mapping permissions are
	 * not correct.
	 * 
	 */
	@Test()
	public void testLKMMappingServiceExceptionWhenInvalidValues() throws Exception {
		List<String> permissions = new ArrayList<>();
		permissions.add("Biometric Authentication - IIR Data Match");
		permissions.add("Biometric Authentication - FID Data Match");
		LicenseKeyMappingDto licenseKeyMappingDto = new LicenseKeyMappingDto();
		licenseKeyMappingDto.setLKey("tEsTlIcEnSe");
		licenseKeyMappingDto.setTspId("TSP_ID_TEST");
		licenseKeyMappingDto.setPermissions(permissions);
		String json = objectMapper.writeValueAsString(licenseKeyMappingDto);
		when(licenseKeyTspMapRepository.findByLKeyAndTspId(Mockito.anyString(), Mockito.anyString())).thenReturn(null);
		mockMvc.perform(post("/v1.0/license/map").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk()).andExpect(jsonPath("$.errors[0].errorCode", isA(String.class)));
	}

	/**
	 * TEST SCENARIO : When License Key is expired.
	 * 
	 */
	@Test
	public void testLKMFetchServiceExceptionWhenExpiredLicense() throws Exception {
		when(licenseKeyTspMapRepository.findByLKeyAndTspId(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(licenseKeyTspMap);
		when(licenseKeyListRepository.findByLicenseKey(Mockito.anyString())).thenReturn(licensekeyList);
		mockMvc.perform(get("/v1.0/license/fetch?licenseKey=tEsTlIcEnSe&tspId=TSP_ID_TEST")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.errors[0].errorCode", isA(String.class)));
	}

	/**
	 * TEST SCENARIO : When the license key and TSPID entered for the permissions to
	 * be fetched are not valid, i.e. the TSPID doesnt has the corresponding license
	 * key.
	 * 
	 */
	@Test
	public void testLKMFetchServiceExceptionWhenInvalidValues() throws Exception {
		when(licenseKeyTspMapRepository.findByLKeyAndTspId(Mockito.anyString(), Mockito.anyString())).thenReturn(null);
		when(licenseKeyListRepository.findByLicenseKey(Mockito.anyString())).thenReturn(licensekeyList);
		mockMvc.perform(get("/v1.0/license/fetch?licenseKey=tEsTlIcEnSe&tspId=TSP_ID_TEST")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.errors[0].errorCode", isA(String.class)));
	}

	/**
	 * TEST SCENARIO : When inputs has empty values.
	 * 
	 */
	@Test
	public void testLKMFetchServiceExceptionWhenEmptyValues() throws Exception {
		mockMvc.perform(
				get("/v1.0/license/fetch?licenseKey=&tspId=TSP_ID_TEST").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.errors[0].errorCode", isA(String.class)));
	}

	/**
	 * TEST SCENARIO : When inputs has null values.
	 * 
	 */
	@Test
	public void testLKMFetchServiceExceptionWhenNullValues() throws Exception {
		mockMvc.perform(
				get("/v1.0/license/fetch?licenseKey=hjdesufhdufyisehui").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

}
