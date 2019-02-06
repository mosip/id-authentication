package io.mosip.kernel.lkeymanager.test.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.isA;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.kernel.lkeymanager.LicenseKeyManagerBootApplication;
import io.mosip.kernel.lkeymanager.dto.LicenseKeyFetchResponseDto;
import io.mosip.kernel.lkeymanager.dto.LicenseKeyGenerationDto;
import io.mosip.kernel.lkeymanager.dto.LicenseKeyGenerationResponseDto;
import io.mosip.kernel.lkeymanager.dto.LicenseKeyMappingDto;
import io.mosip.kernel.lkeymanager.dto.LicenseKeyMappingResponseDto;
import io.mosip.kernel.lkeymanager.entity.LicenseKeyList;
import io.mosip.kernel.lkeymanager.entity.LicenseKeyPermission;
import io.mosip.kernel.lkeymanager.entity.LicenseKeyTspMap;
import io.mosip.kernel.lkeymanager.repository.LicenseKeyListRepository;
import io.mosip.kernel.lkeymanager.repository.LicenseKeyPermissionRepository;
import io.mosip.kernel.lkeymanager.repository.LicenseKeyTspMapRepository;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest(classes = LicenseKeyManagerBootApplication.class)
public class LicenseKeyManagerServiceTest {

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

	private LicenseKeyPermission licenseKeyPermission;

	@Before
	public void setUp() {
		licenseKeyListEntitySetUp();
		licenseKeyTspMapSetUp();
		licenseKeyPermissionSetUp();
	}

	private void licenseKeyListEntitySetUp() {
		licensekeyList = new LicenseKeyList();
		licensekeyList.setActive(true);
		licensekeyList.setCreatedAt(LocalDateTime.now());
		licensekeyList.setCreatedBy("testadmin@mosip.io");
		licensekeyList.setDeleted(false);
		licensekeyList.setExpiryDateTimes(LocalDateTime.of(2600, Month.FEBRUARY, 6, 6, 23));
		licensekeyList.setLicenseKey("tEsTlIcEnSe");

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

	private void licenseKeyPermissionSetUp() {
		licenseKeyPermission = new LicenseKeyPermission();
		licenseKeyPermission.setActive(true);
		licenseKeyPermission.setCreatedBy("testadmin@mosip.io");
		licenseKeyPermission.setCreatedDateTimes(LocalDateTime.now());
		licenseKeyPermission.setDeleted(false);
		licenseKeyPermission.setLKey("tEsTlIcEnSe");
		licenseKeyPermission
				.setPermission("Biometric Authentication - IIR Data Match,Biometric Authentication - FID Data Match");
		licenseKeyPermission.setUpdatedBy("testadmin@mosip.io");
		licenseKeyPermission.setUpdatedDateTimes(LocalDateTime.now());
	}

	/**
	 * TEST SCENARIO : Testing License Key Generation service implementation.
	 * 
	 * @throws Exception.class
	 */
	@Test
	public void testLKMGenerationService() throws Exception {
		LicenseKeyGenerationDto licenseKeyGenerationDto = new LicenseKeyGenerationDto();
		licenseKeyGenerationDto.setLicenseExpiryTime(LocalDateTime.of(2019, Month.FEBRUARY, 6, 6, 23, 0));
		licenseKeyGenerationDto.setTspId("TSP_ID_TEST");
		String json = objectMapper.writeValueAsString(licenseKeyGenerationDto);
		when(licenseKeyListRepository.save(Mockito.any())).thenReturn(licensekeyList);
		when(licenseKeyTspMapRepository.save(Mockito.any())).thenReturn(licenseKeyTspMap);
		MvcResult result = mockMvc
				.perform(post("/v1.0/license/generate").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk()).andReturn();
		ObjectMapper mapper = new ObjectMapper();
		LicenseKeyGenerationResponseDto returnResponse = mapper.readValue(result.getResponse().getContentAsString(),
				LicenseKeyGenerationResponseDto.class);
		assertThat(returnResponse.getLicenseKey(), isA(String.class));
	}

	/**
	 * TEST SCENARIO : Testing License Key Mapping service implementation when new
	 * permissions are added to the already existing permissions.
	 * 
	 * @throws Exception.class
	 */
	@Test
	public void testLKMMappingServiceUpdatePermission() throws Exception {
		List<String> permissions = new ArrayList<>();
		permissions.add("Biometric Authentication - IIR Data Match");
		permissions.add("Biometric Authentication - FID Data Match");
		LicenseKeyMappingDto licenseKeyMappingDto = new LicenseKeyMappingDto();
		licenseKeyMappingDto.setLKey("tEsTlIcEnSe");
		licenseKeyMappingDto.setTspId("TSP_ID_TEST");
		licenseKeyMappingDto.setPermissions(permissions);
		String json = objectMapper.writeValueAsString(licenseKeyMappingDto);
		when(licenseKeyTspMapRepository.findByLKeyAndTspId(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(licenseKeyTspMap);
		when(licenseKeyPermissionRepository.findByLKey(Mockito.any())).thenReturn(licenseKeyPermission);
		MvcResult result = mockMvc
				.perform(post("/v1.0/license/map").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk()).andReturn();
		ObjectMapper mapper = new ObjectMapper();
		LicenseKeyMappingResponseDto returnResponse = mapper.readValue(result.getResponse().getContentAsString(),
				LicenseKeyMappingResponseDto.class);
		assertThat(returnResponse.getStatus(), is("Mapped License with the permissions"));

	}

	/**
	 * TEST SCENARIO : Testing License Key Mapping service implementation when
	 * permissions are added.
	 * 
	 * @throws Exception.class
	 */
	@Test
	public void testLKMMappingServiceCreatePermission() throws Exception {
		List<String> permissions = new ArrayList<>();
		permissions.add("Biometric Authentication - IIR Data Match");
		permissions.add("Biometric Authentication - FID Data Match");
		LicenseKeyMappingDto licenseKeyMappingDto = new LicenseKeyMappingDto();
		licenseKeyMappingDto.setLKey("tEsTlIcEnSe");
		licenseKeyMappingDto.setTspId("TSP_ID_TEST");
		licenseKeyMappingDto.setPermissions(permissions);
		String json = objectMapper.writeValueAsString(licenseKeyMappingDto);
		when(licenseKeyTspMapRepository.findByLKeyAndTspId(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(licenseKeyTspMap);
		when(licenseKeyPermissionRepository.findByLKey(Mockito.any())).thenReturn(null);
		MvcResult result = mockMvc
				.perform(post("/v1.0/license/map").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk()).andReturn();
		ObjectMapper mapper = new ObjectMapper();
		LicenseKeyMappingResponseDto returnResponse = mapper.readValue(result.getResponse().getContentAsString(),
				LicenseKeyMappingResponseDto.class);
		assertThat(returnResponse.getStatus(), is("Mapped License with the permissions"));
	}

	/**
	 * TEST SCENARIO : Testing fetching permissions service implementation.
	 * 
	 * @throws Exception.class
	 */
	@Test
	public void testLKMFetchService() throws Exception {
		when(licenseKeyTspMapRepository.findByLKeyAndTspId(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(licenseKeyTspMap);
		when(licenseKeyListRepository.findByLicenseKey(Mockito.anyString())).thenReturn(licensekeyList);
		when(licenseKeyPermissionRepository.findByLKey(Mockito.any())).thenReturn(licenseKeyPermission);
		MvcResult result = mockMvc.perform(get("/v1.0/license/fetch?licenseKey=tEsTlIcEnSe&tspId=TSP_ID_TEST")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andReturn();
		ObjectMapper mapper = new ObjectMapper();
		LicenseKeyFetchResponseDto returnResponse = mapper.readValue(result.getResponse().getContentAsString(),
				LicenseKeyFetchResponseDto.class);
		assertThat(returnResponse.getMappedPermissions().get(0), isA(String.class));
	}
}
