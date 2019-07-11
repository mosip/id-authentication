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

import io.mosip.kernel.core.http.RequestWrapper;
import io.mosip.kernel.lkeymanager.LicenseKeyManagerBootApplication;
import io.mosip.kernel.lkeymanager.dto.LicenseKeyGenerationDto;
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
	 * 
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
		licenseKeyMappingDto.setLicenseKey("tEsTlIcEnSe");
		licenseKeyMappingDto.setTspId("TSP_ID_TEST");
		licenseKeyMappingDto.setPermissions(permissions);
		RequestWrapper<LicenseKeyMappingDto> reqWrapperDTO = new RequestWrapper<>();
		reqWrapperDTO.setId("ID");
		reqWrapperDTO.setMetadata(null);
		reqWrapperDTO.setRequest(licenseKeyMappingDto);
		reqWrapperDTO.setRequesttime(LocalDateTime.now());
		reqWrapperDTO.setVersion("v1.0");
		String json = objectMapper.writeValueAsString(reqWrapperDTO);
		when(licenseKeyTspMapRepository.findByLKeyAndTspId(Mockito.anyString(), Mockito.anyString())).thenReturn(null);
		mockMvc.perform(post("/license/permission").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk()).andExpect(jsonPath("$.errors[0].errorCode", isA(String.class)));
	}

	/**
	 * 
	 * TEST SCENARIO : When License Key is expired.
	 * 
	 */
	@Test
	public void testLKMFetchServiceExceptionWhenExpiredLicense() throws Exception {
		when(licenseKeyTspMapRepository.findByLKeyAndTspId(Mockito.anyString(), Mockito.anyString()))
				.thenReturn(licenseKeyTspMap);
		when(licenseKeyListRepository.findByLicenseKey(Mockito.anyString())).thenReturn(licensekeyList);
		mockMvc.perform(get("/license/permission?licenseKey=tEsTlIcEnSe&tspId=TSP_ID_TEST")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.errors[0].errorCode", isA(String.class)));
	}

	/**
	 * 
	 * TEST SCENARIO : When the license key and TSPID entered for the permissions to
	 * be fetched are not valid, i.e. the TSPID doesnt has the corresponding license
	 * key.
	 * 
	 */
	@Test
	public void testLKMFetchServiceExceptionWhenInvalidValues() throws Exception {
		when(licenseKeyTspMapRepository.findByLKeyAndTspId(Mockito.anyString(), Mockito.anyString())).thenReturn(null);
		when(licenseKeyListRepository.findByLicenseKey(Mockito.anyString())).thenReturn(licensekeyList);
		mockMvc.perform(get("/license/permission?licenseKey=tEsTlIcEnSe&tspId=TSP_ID_TEST")
				.contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.errors[0].errorCode", isA(String.class)));
	}

	/**
	 * 
	 * TEST SCENARIO : When inputs has empty values.
	 * 
	 */
	@Test
	public void testLKMFetchServiceExceptionWhenEmptyLKey() throws Exception {
		mockMvc.perform(
				get("/license/permission?licenseKey=&tspId=TSP_ID_TEST").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.errors[0].errorCode", isA(String.class)));
	}

	/**
	 * 
	 * TEST SCENARIO : When inputs has null values.
	 * 
	 */
	@Test
	public void testLKMFetchServiceExceptionWhenNullTSP() throws Exception {
		mockMvc.perform(
				get("/license/permission?licenseKey=hjdesufhdufyisehui").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError());
	}

	/**
	 * 
	 * TEST SCENARIO : When inputs has empty values.
	 * 
	 */
	@Test
	public void testLKMFetchServiceExceptionWhenEmptyTSP() throws Exception {
		mockMvc.perform(
				get("/license/permission?licenseKey=jsudhauidhiw&tspId=").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.errors[0].errorCode", isA(String.class)));
	}

	/**
	 * 
	 * TEST SCENARIO : When inputs has null values.
	 * 
	 */
	@Test
	public void testLKMFetchServiceExceptionWhenNullLicenseKey() throws Exception {
		mockMvc.perform(get("/license/permission?tspId=98376").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isInternalServerError());
	}

	/**
	 * 
	 * TEST SCENARIO : When Expiry Time entered is a date before current DateTime.
	 * 
	 */
	@Test
	public void testLKMGenerationServiceExceptionWhenExpiredDateEntered() throws Exception {
		LicenseKeyGenerationDto licenseKeyGenerationDto = new LicenseKeyGenerationDto();
		licenseKeyGenerationDto.setLicenseExpiryTime(LocalDateTime.of(2010, Month.FEBRUARY, 6, 6, 23, 0));
		licenseKeyGenerationDto.setTspId("TSP_ID_TEST");
		RequestWrapper<LicenseKeyGenerationDto> reqWrapperDTO = new RequestWrapper<>();
		reqWrapperDTO.setId("ID");
		reqWrapperDTO.setMetadata(null);
		reqWrapperDTO.setRequest(licenseKeyGenerationDto);
		reqWrapperDTO.setRequesttime(LocalDateTime.now());
		reqWrapperDTO.setVersion("v1.0");
		String json = objectMapper.writeValueAsString(reqWrapperDTO);
		mockMvc.perform(post("/license/generate").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk()).andExpect(jsonPath("$.errors[0].errorCode", isA(String.class)));
	}

	/**
	 * 
	 * TEST SCENARIO : When permissions are mapped and TSP entered is null.
	 * 
	 */
	@Test()
	public void testLKMMappingServiceExceptionWhenTSPIDNull() throws Exception {
		List<String> permissions = new ArrayList<>();
		permissions.add("Biometric Authentication - IIR Data Match");
		permissions.add("Invalid Permission Test");
		LicenseKeyMappingDto licenseKeyMappingDto = new LicenseKeyMappingDto();
		licenseKeyMappingDto.setLicenseKey("tEsTlIcEnSe");
		licenseKeyMappingDto.setTspId(null);
		licenseKeyMappingDto.setPermissions(permissions);
		RequestWrapper<LicenseKeyMappingDto> reqWrapperDTO = new RequestWrapper<>();
		reqWrapperDTO.setId("ID");
		reqWrapperDTO.setMetadata(null);
		reqWrapperDTO.setRequest(licenseKeyMappingDto);
		reqWrapperDTO.setRequesttime(LocalDateTime.now());
		reqWrapperDTO.setVersion("v1.0");
		String json = objectMapper.writeValueAsString(reqWrapperDTO);
		when(licenseKeyTspMapRepository.findByLKeyAndTspId(Mockito.anyString(), Mockito.anyString())).thenReturn(null);
		mockMvc.perform(post("/license/permission").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk()).andExpect(jsonPath("$.errors[0].errorCode", isA(String.class)));
	}

	/**
	 * 
	 * TEST SCENARIO : When permissions are mapped and TSP entered is empty.
	 * 
	 */
	@Test()
	public void testLKMMappingServiceExceptionWhenTSPIDEmpty() throws Exception {
		List<String> permissions = new ArrayList<>();
		permissions.add("Biometric Authentication - IIR Data Match");
		permissions.add("Invalid Permission Test");
		LicenseKeyMappingDto licenseKeyMappingDto = new LicenseKeyMappingDto();
		licenseKeyMappingDto.setLicenseKey("tEsTlIcEnSe");
		licenseKeyMappingDto.setTspId("  ");
		licenseKeyMappingDto.setPermissions(permissions);
		RequestWrapper<LicenseKeyMappingDto> reqWrapperDTO = new RequestWrapper<>();
		reqWrapperDTO.setId("ID");
		reqWrapperDTO.setMetadata(null);
		reqWrapperDTO.setRequest(licenseKeyMappingDto);
		reqWrapperDTO.setRequesttime(LocalDateTime.now());
		reqWrapperDTO.setVersion("v1.0");
		String json = objectMapper.writeValueAsString(reqWrapperDTO);
		when(licenseKeyTspMapRepository.findByLKeyAndTspId(Mockito.anyString(), Mockito.anyString())).thenReturn(null);
		mockMvc.perform(post("/license/permission").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk()).andExpect(jsonPath("$.errors[0].errorCode", isA(String.class)));
	}

	/**
	 * 
	 * TEST SCENARIO : When permissions are mapped and license key entered is null.
	 * 
	 */
	@Test()
	public void testLKMMappingServiceExceptionWhenLicenseKeyNull() throws Exception {
		List<String> permissions = new ArrayList<>();
		permissions.add("Biometric Authentication - IIR Data Match");
		permissions.add("Invalid Permission Test");
		LicenseKeyMappingDto licenseKeyMappingDto = new LicenseKeyMappingDto();
		licenseKeyMappingDto.setLicenseKey(null);
		licenseKeyMappingDto.setTspId("TSP_ID_TEST");
		licenseKeyMappingDto.setPermissions(permissions);
		RequestWrapper<LicenseKeyMappingDto> reqWrapperDTO = new RequestWrapper<>();
		reqWrapperDTO.setId("ID");
		reqWrapperDTO.setMetadata(null);
		reqWrapperDTO.setRequest(licenseKeyMappingDto);
		reqWrapperDTO.setRequesttime(LocalDateTime.now());
		reqWrapperDTO.setVersion("v1.0");
		String json = objectMapper.writeValueAsString(reqWrapperDTO);
		when(licenseKeyTspMapRepository.findByLKeyAndTspId(Mockito.anyString(), Mockito.anyString())).thenReturn(null);
		mockMvc.perform(post("/license/permission").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk()).andExpect(jsonPath("$.errors[0].errorCode", isA(String.class)));
	}

	/**
	 * 
	 * TEST SCENARIO : When permissions are mapped and license key entered is empty.
	 * 
	 */
	@Test()
	public void testLKMMappingServiceExceptionWhenLicenseKeyEmpty() throws Exception {
		List<String> permissions = new ArrayList<>();
		permissions.add("Biometric Authentication - IIR Data Match");
		permissions.add("Invalid Permission Test");
		LicenseKeyMappingDto licenseKeyMappingDto = new LicenseKeyMappingDto();
		licenseKeyMappingDto.setLicenseKey("  ");
		licenseKeyMappingDto.setTspId("TSP_ID_TEST");
		licenseKeyMappingDto.setPermissions(permissions);
		RequestWrapper<LicenseKeyMappingDto> reqWrapperDTO = new RequestWrapper<>();
		reqWrapperDTO.setId("ID");
		reqWrapperDTO.setMetadata(null);
		reqWrapperDTO.setRequest(licenseKeyMappingDto);
		reqWrapperDTO.setRequesttime(LocalDateTime.now());
		reqWrapperDTO.setVersion("v1.0");
		String json = objectMapper.writeValueAsString(reqWrapperDTO);
		when(licenseKeyTspMapRepository.findByLKeyAndTspId(Mockito.anyString(), Mockito.anyString())).thenReturn(null);
		mockMvc.perform(post("/license/permission").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk()).andExpect(jsonPath("$.errors[0].errorCode", isA(String.class)));
	}

	/**
	 * 
	 * TEST SCENARIO : When permission entered is invalid[i.e. permission not
	 * present in master list].
	 * 
	 */
	@Test()
	public void testLKMMappingServiceExceptionWhenInvalidPermissions() throws Exception {
		List<String> permissions = new ArrayList<>();
		permissions.add("Biometric Authentication - IIR Data Match");
		permissions.add("Invalid Permission Test");
		LicenseKeyMappingDto licenseKeyMappingDto = new LicenseKeyMappingDto();
		licenseKeyMappingDto.setLicenseKey("tEsTlIcEnSe");
		licenseKeyMappingDto.setTspId("TSP_ID_TEST");
		licenseKeyMappingDto.setPermissions(permissions);
		RequestWrapper<LicenseKeyMappingDto> reqWrapperDTO = new RequestWrapper<>();
		reqWrapperDTO.setId("ID");
		reqWrapperDTO.setMetadata(null);
		reqWrapperDTO.setRequest(licenseKeyMappingDto);
		reqWrapperDTO.setRequesttime(LocalDateTime.now());
		reqWrapperDTO.setVersion("v1.0");
		String json = objectMapper.writeValueAsString(reqWrapperDTO);
		when(licenseKeyTspMapRepository.findByLKeyAndTspId(Mockito.anyString(), Mockito.anyString())).thenReturn(null);
		mockMvc.perform(post("/license/permission").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk()).andExpect(jsonPath("$.errors[0].errorCode", isA(String.class)));
	}

	/**
	 * 
	 * TEST SCENARIO : When permission entered is empty.
	 * 
	 */
	@Test()
	public void testLKMMappingServiceExceptionWhenEmptyPermissions() throws Exception {
		List<String> permissions = new ArrayList<>();
		permissions.add("Biometric Authentication - IIR Data Match");
		permissions.add("  ");
		LicenseKeyMappingDto licenseKeyMappingDto = new LicenseKeyMappingDto();
		licenseKeyMappingDto.setLicenseKey("tEsTlIcEnSe");
		licenseKeyMappingDto.setTspId("TSP_ID_TEST");
		licenseKeyMappingDto.setPermissions(permissions);
		RequestWrapper<LicenseKeyMappingDto> reqWrapperDTO = new RequestWrapper<>();
		reqWrapperDTO.setId("ID");
		reqWrapperDTO.setMetadata(null);
		reqWrapperDTO.setRequest(licenseKeyMappingDto);
		reqWrapperDTO.setRequesttime(LocalDateTime.now());
		reqWrapperDTO.setVersion("v1.0");
		String json = objectMapper.writeValueAsString(reqWrapperDTO);
		when(licenseKeyTspMapRepository.findByLKeyAndTspId(Mockito.anyString(), Mockito.anyString())).thenReturn(null);
		mockMvc.perform(post("/license/permission").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isOk()).andExpect(jsonPath("$.errors[0].errorCode", isA(String.class)));
	}

}
