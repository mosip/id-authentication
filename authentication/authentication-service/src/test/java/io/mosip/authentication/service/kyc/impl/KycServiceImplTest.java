package io.mosip.authentication.service.kyc.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.codec.DecoderException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.config.IDAMappingConfig;
import io.mosip.authentication.common.service.entity.KycTokenData;
import io.mosip.authentication.common.service.factory.IDAMappingFactory;
import io.mosip.authentication.common.service.helper.IdInfoHelper;
import io.mosip.authentication.common.service.impl.IdInfoFetcherImpl;
import io.mosip.authentication.common.service.impl.match.BioMatchType;
import io.mosip.authentication.common.service.repository.KycTokenDataRepository;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.common.service.util.EnvUtil;
import io.mosip.authentication.core.constant.IdAuthCommonConstants;
import io.mosip.authentication.core.constant.IdAuthenticationErrorConstants;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdAuthenticationDaoException;
import io.mosip.authentication.core.indauth.dto.EKycResponseDTO;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.KycExchangeRequestDTO;
import io.mosip.authentication.core.spi.indauth.match.MappingConfig;
import io.mosip.authentication.core.util.CryptoUtil;
import io.mosip.kernel.cbeffutil.impl.CbeffImpl;

/**
 * Test class for KycServiceImpl.
 *
 * @author Sanjay Murali
 */
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class, IDAMappingFactory.class,
		IDAMappingConfig.class })

@RunWith(SpringRunner.class)
@Import(EnvUtil.class)
@WebMvcTest
public class KycServiceImplTest {
	
	@Value("${ida.id.attribute.separator.fullAddress}")
	private String fullAddrSep;

	@Autowired
	EnvUtil env;

	@Autowired
	EnvUtil environment;
	
	@Mock
	private MappingConfig mappingConfig;
	
	@Mock
	private IdInfoHelper idInfoHelper;
	
	@Autowired
	private IDAMappingConfig idMappingConfig;
	
	@InjectMocks
	private IdInfoHelper idInfoHelper2;

	@InjectMocks
	private KycServiceImpl kycServiceImpl;
	
	@InjectMocks
	private KycServiceImpl kycServiceImpl2;
	
	@InjectMocks
	private IdInfoFetcherImpl idinfoFetcher;
	
	@Autowired
	private ObjectMapper mapper;

	@Mock
	private KycTokenDataRepository kycTokenDataRepo;

	@Mock
	private IdAuthSecurityManager securityManager;

	@Value("${sample.demo.entity}")
	String value;

	Map<String, List<IdentityInfoDTO>> idInfo;
	
	@Value("${mosip.date-of-birth.pattern}")
	private String dobPattern;
	

	@Before
	public void before() throws IdAuthenticationDaoException {
		ReflectionTestUtils.setField(kycServiceImpl, "env", env);
		ReflectionTestUtils.setField(kycServiceImpl, "idInfoHelper", idInfoHelper);
		ReflectionTestUtils.setField(kycServiceImpl, "mapper", mapper);
		ReflectionTestUtils.setField(idInfoHelper, "env", env);
		ReflectionTestUtils.setField(kycServiceImpl2, "env", env);
		ReflectionTestUtils.setField(kycServiceImpl2, "idInfoHelper", idInfoHelper2);
		ReflectionTestUtils.setField(kycServiceImpl2, "mapper", mapper);
		ReflectionTestUtils.setField(kycServiceImpl2, "mappingConfig", idMappingConfig);
		ReflectionTestUtils.setField(idInfoHelper2, "env", env);
		ReflectionTestUtils.setField(idInfoHelper2, "idInfoFetcher", idinfoFetcher);
		ReflectionTestUtils.setField(idInfoHelper2, "idMappingConfig", idMappingConfig);
		ReflectionTestUtils.setField(idinfoFetcher, "cbeffUtil", new CbeffImpl());
		ReflectionTestUtils.setField(idinfoFetcher, "environment", env);
		ReflectionTestUtils.setField(kycServiceImpl2, "cbeffUtil", new CbeffImpl());

		idInfo = getIdInfo("12232323121");

	}

	@Test
	public void validUIN() throws IOException {
		try {
			deleteBootStrapFile();
			prepareMap(idInfo);
			List<String> allowedKycList = limitedList();
			Mockito.when(idInfoHelper.getIdEntityInfoMap(BioMatchType.FACE, idInfo, null)).thenReturn(entityInfo());
			Set<String> langCodes = new HashSet<>();
			langCodes.add("ara");
			EKycResponseDTO k = kycServiceImpl.retrieveKycInfo(allowedKycList, langCodes, idInfo);
			assertNotNull(k);
		} catch (IdAuthenticationBusinessException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void validdata() throws IOException {
		try {
			deleteBootStrapFile();
			prepareMap(idInfo);
			List<String> allowedKycList = limitedList();
			Map<String, List<IdentityInfoDTO>> idInfo1 = idInfo;
			idInfo1.remove("face");
			Mockito.when(idInfoHelper.getIdEntityInfoMap(BioMatchType.FACE, idInfo, null)).thenReturn(entityInfo());
			Set<String> langCodes = new HashSet<>();
			langCodes.add("ara");
			EKycResponseDTO k = kycServiceImpl.retrieveKycInfo(allowedKycList, langCodes, idInfo1);
			assertNotNull(k);
		} catch (IdAuthenticationBusinessException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void validdata_noPhoto() throws IOException {
		try {
			deleteBootStrapFile();
			prepareMap(idInfo);
			List<String> allowedKycList = limitedList_nophoto();
			Map<String, List<IdentityInfoDTO>> idInfo1 = idInfo;
			idInfo1.remove("face");
			//Mockito.when(idInfoHelper.getIdEntityInfoMap(BioMatchType.FACE, idInfo, null)).thenReturn(entityInfo());
			Set<String> langCodes = new HashSet<>();
			langCodes.add("ara");
			EKycResponseDTO k = kycServiceImpl.retrieveKycInfo(allowedKycList, langCodes, idInfo1);
			assertTrue(mapper.readValue(k.getIdentity().getBytes(), Map.class).isEmpty());
		} catch (IdAuthenticationBusinessException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void validdata2() throws IOException {
		try {
			deleteBootStrapFile();
			prepareMap(idInfo);
			Map<String, List<IdentityInfoDTO>> idInfo1 = idInfo;
			idInfo1.remove("face");
			Mockito.when(idInfoHelper.getIdEntityInfoMap(BioMatchType.FACE, idInfo, null)).thenReturn(entityInfo());
			Set<String> langCodes = new HashSet<>();
			langCodes.add("ara");
			EKycResponseDTO k = kycServiceImpl.retrieveKycInfo(Collections.emptyList(), langCodes, idInfo1);
			assertNotNull(k);
		} catch (IdAuthenticationBusinessException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void validdata3() throws IOException {
		try {
			deleteBootStrapFile();
			prepareMap(idInfo);
			Map<String, List<IdentityInfoDTO>> idInfo1 = idInfo;
			idInfo1.remove("face");
			Mockito.when(idInfoHelper.getIdEntityInfoMap(BioMatchType.FACE, idInfo, null)).thenReturn(entityInfo());
			Set<String> langCodes = new HashSet<>();
			langCodes.add("ara");
			EKycResponseDTO k = kycServiceImpl.retrieveKycInfo(null, langCodes, null);
			assertNotNull(k);
		} catch (IdAuthenticationBusinessException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void validUIN1() {
		try {
			deleteBootStrapFile();
			Mockito.when(idInfoHelper.getIdEntityInfoMap(BioMatchType.FACE, idInfo, null)).thenReturn(entityInfo());
			Set<String> langCodes = new HashSet<>();
			langCodes.add("ara");
			EKycResponseDTO k = kycServiceImpl.retrieveKycInfo(limitedList(), langCodes, idInfo);
			assertNotNull(k);
		} catch (IdAuthenticationBusinessException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void validUINWithoutFace() {
		try {
			deleteBootStrapFile();
			Mockito.when(idInfoHelper.getIdEntityInfoMap(BioMatchType.FACE, idInfo, null)).thenReturn(null);
			Set<String> langCodes = new HashSet<>();
			langCodes.add("ara");
			EKycResponseDTO k = kycServiceImpl.retrieveKycInfo(limitedList(), langCodes, idInfo);
			assertNotNull(k);
		} catch (IdAuthenticationBusinessException e) {
			e.printStackTrace();
		}
	}
	
	@Test(expected = IdAuthenticationBusinessException.class)
	public void testValidUINWithFace()
			throws JsonParseException, JsonMappingException, IOException, IdAuthenticationBusinessException {
		Set<String> langCodes = new HashSet<>();
		langCodes.add("ara");
		EKycResponseDTO k = kycServiceImpl2.retrieveKycInfo(limitedList(), langCodes, idInfo);
		assertNotNull(mapper.readValue(k.getIdentity().getBytes(), Map.class).get("Face"));
	}
	
	@Test
	public void validUINWithoutFace2() {
		try {
			deleteBootStrapFile();
			Mockito.when(idInfoHelper.getIdEntityInfoMap(BioMatchType.FACE, idInfo, null)).thenReturn(null);
			Set<String> langCodes = new HashSet<>();
			langCodes.add("fra");
			EKycResponseDTO k = kycServiceImpl.retrieveKycInfo(limitedList(), langCodes, idInfo);
			assertNotNull(k);
		} catch (IdAuthenticationBusinessException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void validUINWithoutAttributes() {
		try {
			deleteBootStrapFile();
			Mockito.when(idInfoHelper.getIdEntityInfoMap(BioMatchType.FACE, idInfo, null)).thenReturn(null);
			Set<String> langCodes = new HashSet<>();
			langCodes.add("ara");
			EKycResponseDTO k = kycServiceImpl.retrieveKycInfo(Collections.emptyList(), langCodes, idInfo);
			assertNotNull(k);
		} catch (IdAuthenticationBusinessException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void validUINWithoutAttributes2() {
		try {
			deleteBootStrapFile();
			Mockito.when(idInfoHelper.getIdEntityInfoMap(BioMatchType.FACE, idInfo, null)).thenReturn(null);
			EKycResponseDTO k = kycServiceImpl.retrieveKycInfo(null, null, idInfo);
			assertNotNull(k);
		} catch (IdAuthenticationBusinessException e) {
			e.printStackTrace();
		}
	}

	private void prepareMap(Map<String, List<IdentityInfoDTO>> idInfo) {
		String value = "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiIHN0YW5kYWxvbmU9InllcyI_Pgo8QklSIHhtbG5zPSJodHRwOi8vZG9jcy5vYXNpcy1vcGVuLm9yZy9iaWFzL25zL2JpYXNwYXRyb25mb3JtYXQtMS4wLyI-CiAgICA8VmVyc2lvbj4KICAgICAgICA8TWFqb3I-MTwvTWFqb3I-CiAgICAgICAgPE1pbm9yPjE8L01pbm9yPgogICAgPC9WZXJzaW9uPgogICAgPENCRUZGVmVyc2lvbj4KICAgICAgICA8TWFqb3I-MTwvTWFqb3I-CiAgICAgICAgPE1pbm9yPjE8L01pbm9yPgogICAgPC9DQkVGRlZlcnNpb24-CiAgICA8QklSSW5mbz4KICAgICAgICA8SW50ZWdyaXR5PmZhbHNlPC9JbnRlZ3JpdHk-CiAgICA8L0JJUkluZm8-Cgk8QklSPgogICAgICAgIDxCSVJJbmZvPgogICAgICAgICAgICA8SW50ZWdyaXR5PmZhbHNlPC9JbnRlZ3JpdHk-CiAgICAgICAgPC9CSVJJbmZvPgogICAgICAgIDxCREJJbmZvPgogICAgICAgICAgICA8Rm9ybWF0T3duZXI-MjU3PC9Gb3JtYXRPd25lcj4KICAgICAgICAgICAgPEZvcm1hdFR5cGU-ODwvRm9ybWF0VHlwZT4KICAgICAgICAgICAgPENyZWF0aW9uRGF0ZT4yMDE5LTAxLTI5VDE5OjExOjMzLjQzNCswNTozMDwvQ3JlYXRpb25EYXRlPgogICAgICAgICAgICA8VHlwZT5GYWNlPC9UeXBlPgogICAgICAgICAgICA8TGV2ZWw-UmF3PC9MZXZlbD4KICAgICAgICAgICAgPFB1cnBvc2U-RW5yb2xsPC9QdXJwb3NlPgogICAgICAgICAgICA8UXVhbGl0eT45NTwvUXVhbGl0eT4KICAgICAgICA8L0JEQkluZm8-CiAgICAgICAgPEJEQj4vOWovNEFBUVNrWkpSZ0FCQVFBQUFRQUJBQUQvMndDRUFBa0dCeE1URWhVUUVoSVZGUlVTRlJBU0VCVVFFaEFRRlJnV0ZSWVdGeGNWR0JVWUhTb2dHQm9sSFJVVklURWhKU2tyTGk0dUZ4OHpPRE10TnlndExpc0JDZ29LRGcwT0doQVFHU3NkSFIwckt5c3JNUzB0S3pjckxUY3ZMUzByTFMwdExTMHhLeTB0S3kwdEt5MHRLeTAxTFNzckxTMHRLeXNyTFNzdExTMHRMZi9BQUJFSUFPRUE0UU1CSWdBQ0VRRURFUUgveEFBY0FBRUFBUVVCQVFBQUFBQUFBQUFBQUFBQUJBTUZCZ2NJQWdIL3hBQkVFQUFCQXdJRUFnY0ZCUVVFQ3dBQUFBQUJBQUlEQkJFRkVpRXhCa0VIRTFGaGNZR1JJakp5b2JFVVFsSmkwUlVqTTdMQmM0S1Nrd2cwTlVORVZHT0RvdEx4LzhRQUdRRUJBQU1CQVFBQUFBQUFBQUFBQUFBQUFBSURCQUVGLzhRQUpoRUJBUUFDQVFRQ0FRUURBQUFBQUFBQUFBRUNFUU1TSVRGUkV6SkJJbEtSb1JSaGNmL2FBQXdEQVFBQ0VRTVJBRDhBM2lpSWdJaUlDSWlBaUlnSWlJQ0lpQWlJZ0lpSUNJaUFpSWdJaUlDSWlBaUlnSWlJQ0lpQWlJZ0lpSUNJaUFpSWdJaUlDSWlBaUlnSWlJQ0lpQWlJZ0lpSUNJaUFpSWdJaUlDSWlBaUlnSWlJQ0lpQWlJZ0lpSUNJaUFpeDNqTGpHbXc2TnI1eTRsNXRISEdBWHU3YlhJQUE3U3NSZzZhYVZ6d0RUVE5ZZDNreG0zZmxCMUNEYUNLTGhtSXhWRWJab1hoN0hDNExUZjhBK0ZTa0JFUkFSRVFFUkVCRVJBUkVRRVJFQkVSQVJFUUVSRUJFUkFSRkRwOFVoZkkrRmtyREpHYlNNRGhtYWQ5UWdoY1Y4U1EwRVBYelhJdUd0YTJ4YzRua0ZxTEcrbDZzbEpGTXhrRE9SSTYyVDFQc2owS2lkTHZFSDJxdE1ERGVLbXV3VzJNbjN6NWJlUldIeHhyaVVpNHpjVjRpL3dCNnVuOG5rZlJRdjJ0VkIzV2ZhcDh3MlBXdi9WZW1zQzh1alIzUzV1NDBxbnM2cXBFTlhHYmV6VnhaeVBoZTBnZzk2dE5mVnNrZURIVE1nRmlDMkYwam1uc05uazJQZ3Zoalh3dFEwdW5DM0ZNK0h5aVNKeE1aSTY2SW4yWGp3NU83MXVEQmVsckQ1eUd2YzZGeDB0SzNTL3hEUmFHZTFRS2lKSExIWVVFN1h0RDJPRG1uVUZwQkI4d3FpNVQ0VTQwck1QZGVHUzdQdnd5WGRHZkFmZFBlRjBqd2p4TkZYMHpLbVBUTmRybU9Jek5jTkMwcnFLOW9pSUNJaUFpSWdJaUlDSWlBaUlnSWlJQ0lpQWlJZ0xsamk2cWYrMGF0N1h1YTdyNVJtWTR0TmdjdTQ3aFpkRUhqWEQ4em1Hc2hEb3lXdkRuZ1dJMzFPaDhsejV4L0xUdXhHZDlJUVluT2FRVzZ0TGkxdWN0N3MxMHJzV2lNOCszY2xTMktERzVWMnlLS1NhMHJ5NHFpMlJmUzlCNkpYZ2xmQzlVM1BYUjljbzByVlZMbDRjZ2h5UnJ3eDdtKzY1dytGeEgwVXpMZFVwWTBjWGZBT09LNmpJTU5RNHRHOGNwTWtaOGp0NVdXOU9qdnBEaXhFR053RVZRMFhkSGU0Y1B4TUozQzVzeUtYaE5hNm1tanFZalo4TDJ2YnFSZTI0UGNSY2VhT2FkZ0lzYzRFNHFaaU5NS2hyY2pnU3lSbDcyY08vc1dScnJnaUlnSWlJQ0lpQWlJZ0lpSUNJaUF2RXpNelMzYTRJOVF2YUlPVHVMY0lrbzZ1V21sR29jWHNOckJ6SEVscngzSFVlUlZwRGxsUFN0Vk9reFNwemtuSTVzYkw4bXRBSUE3cmtueldOUVU5emEyOXJlZXk0a05lcWdldGk0WHduVGRVMWtrWWM2M3RPMUJ1ZXdyRk1jNFNxSUhFc1laSXJuSzVtcmdPeHczVmM1Y2JkTEx4NVNiV2xyMTdEMThndytkM3V3eW53amVmNktmVDhQMVR6WVU4bjk1aGI2M1V1cUk2cUNYTHlBU2JBRW5zQUpQb3M5d1hnRFo5UzcvQUxiRDlYZm9zdW84SmhpSDd1TnJlOERYMVZXWFBqUEhkWmp3NVh6MmFjR0ZWQjJwNXY4QUpsL1JWNHNBcW5Hd3A1QjhUQ3dmK1MzSVdxazVxaC9rWDBuOEU5dFNWZkQxUkNNNzJlenpMU0hXOGJLRStNRUxidFN3RUVIWTNCV3NNV28rcG1kSHl2ZHZnZGxaeDhuVjVWNThmVDRXRjBXVTkzTmZaQU9SVXFvYW9MNGxjcWIwNkFJUUtTWjRQdnpiZkMwQmJUWEtQQnRQVlBxNG9LU2Q4TWtyclptdWNHZ0FYTG5BYml3NXJxcW1ZNE1hSHV6T0RXaHpyV3VRTlRia3VvcWlJaUFpSWdJaUlDSWlBaUlnSWlJQ2o0Z3g1amVJM1pYNVRrUDV1Vis1U0VRY25jU1ZFMGxYTTZxRnA4MXBSbHk2dDBHbmdBcFBEZEhubmpiK1lFK1dxem5wNHJZMnp4d0NHUHJIeHRrZkwvdkxCemcwZUdqdm1zVzZQbTNxV2VEejhsRE82bFdZZDdHMEtiRDlGUFpRS1RUalJTbWhlYmE5Q1JFWlJCVkhVZ3NwUVM2anRMU0NhTlUvczZ1RGlxVGwzYm1sdWtwMUhsaFYxY0ZIbEM3SzVZc2RRRmcvR3NQdVA3eTAvVlo5WEJZZHhSRm1pZCtXenZSWDhWN3hUeVRzd0dyRjFGazAyVXVaUm5OVzFqYkU2Q01LTXRZNnBQdTB6Q1BGOGdzUGxmNUxmNjU5NkVNV01OZWFjbjJLbGpoYi9xTUYyL0lPWFFTNmlJaUlDSWlBaUlnSWlJQ0lpQWlJZ0lpSU5BOVAwTnNRaGsvSFROYVA3a2toUDg0Vm82T3Y5WWI4TC82TE5mOEFTQnd0NzIwOVMxamkyTHJHU09BMGFINWRUNXRDMVBoRk5QSVQxSmNDTEQyU1FkVGEyaWhuTnlwNFhWZER3eUFEVWdlSkFYc1Y4VzNXcy94dFdyS1hnQ3BlMEdvckMzOG9MNUNQVWdMeFAwZFJqYXROL3dBMFg2T1dMNDhQM2YwMmZKbiszKzIzbzVXdUYya0VkeEJYMjYwaExnMWZSWGxwNWk5ckJtSmpjZGh2ZGpsTHdqaXZHSjJPZERhUnJOSE82dG5qdnpLWGcvTXMwVG4vQUJaVzR5VmFxL0g2YUxTU2VOcDdDNFg5RnFXanhYRks5em9Xek9BYi9FMmpEZVd0aGRYU2w0QmdiclVUdmU3bUk3TkhxYmtydnd6SDdYK0Q1Ymw5Wi9MTW44YzBIL01OOUhmb3FFbkd0Q2YrSWI2Ty9SV2lIaG5EUm9ZaWU5MGovd0NoQ2tIZ3ZEM2oyWWlQaGxsLzlrMXgvd0N6ZkpmU25WOFdVWjJuYjgxYUtuR0taNGNPdVpZZ2c2OXFpOFNjQnNqWTZTbmU0NVJjc2ZZNkRzS3NtQzhOTW5pNjE3bkRNWEJvYmxHeHRyY2RxdHh4dzF1VlZsbG52VmkxeXVGeUFRYkVqUXFOS05DcTJPNEU2bmVBRG1EaGRwdFk2Y2lxQjI4bHBuZWRtZXBmRFZjNktvZ2xiNzBjMExoYnVlTGp6Rng1cnJoY3Q4SjhOU3ZraW1rR1dKc2tiblh2Y3RhNEVnRHlLNlp3ekVZNTI1NHpjQTJOeFlnOWlUS1hzWEd6dWxvaUxxSWlJZ0lpSUNJaUFpSWdJaUlDSWlERytrZW42ekRLdGczTUx5M3hHb1dvT0JxUVJWRXJPd01MYjY2T3M3K3EzdmpGTjFrRXNWcjU0NUcyOFdsYXJkVHRFdE5PMEFHV2theVMxeDdjRHNwTmo0alh1VmZMUDAxWnhYOVVYNTBCZjFoSnMySmhlUU5DNDJKQXZ5R2kxQlB4VS9PME5mbWNTNXptNWZaQUdvRitZc3QxVXJYT0Z3UjdUY3JnUmNFZDZ4dHZSdlRCK2NBa1h2bExpQnZ0NExMam5oSnI4dE9lR2R1NTRYQ3FvbVJ3eDFJSjZ0OFFrZTE1dllGbVlpL2dvdlI3Umh0SXh6UllTbDh0clcwY1RZZWlvOGZWRDNOaW9RNGRaVk9iR0dzRmd5SUVaamJ3RzZ5Nm1wMnhzYXhvczJOb2Ezd0FzdWN1VTEyL0tYSEx2djhBaGhXRXdNcDY2cXA3V00rV29qMTBJKzhCNEUzODFjSW9SSk95SjJnSjE1WHR5VVhqMmxjMFJZaEVDWktWMTNnZmVpT2poNUs0MGNUS3FObFJFN1J3RG1rYmc5bmltL0dWTmVjWTFwMGdZZzZPcm1qYkdUYXpZZzMyV3MyM0hNYnJPdWpSanBxRnhtR3JKWE5pZnp5NVdtMSt3RWtLOFYzRGNVNUQ1d0pIQnVXNWEwRzNlUUxsVnZzakkyQmpQWmFObWdrTjlObFA1c2QrRmZ3NWUxdXhsdVdFdVA0SG41Rllidzh5MUxGM2g3djhUM0grcXZYSFdLNUlPcUdyNXYzY2JlZXVoS2lRVS9WeHNqL0ExcmZRS09QMS93Q3BaZlpaT0tZYnNhNzhKdDZyR3NDcE90cVk0K1JlTCtBMVAwV1c4U0Q5d2U0dFZyNEpwUGFkTjJYYTArTzZ2eHkxaFZPVTNsR3c3dEZvb3gzZHdXWDhCd0ZzVWhQM3BQb0FGaCtFTUFCY2R5dGg4TlE1YWR2NXJ2MTd5cStDZnFUNXIrbGRFUkZyWlJFUkFSRVFFUkVCRVJBUkVRRVJFQmFrNDF3cW9vcHpVeHRNMUxJOHU2dHRzOFQ1UGZ5Z2ZkSkFQaVZ0dFduaW1ITlRQL0xaM29WSEw2MUxIN1JxK2s2UUlHaXpvcHdlenFYRlN6eDdJOFdwcUdva2NkQVhzTWJSNGs4bGNhV2JSWEtsa1dDM0gwM3laZTFrNFl3S2ZySFYxYVE2b2tHVmpSN3NUUHdqc1dVUEZtcU5QV2h1cDVLbk5palNORkMyNVhhVWtrMDliNkhVRzRJS3hBOFBWZEhJNStIeXRNVHlYT2dsOTBFL2g3RmtqYW02cTljdXkyT1dTc1pmanVLalEwRWJ1OXM0QSthaXpZaGlzbWdwNG9yODNQejI5RmxqbnFQSzVTbVU5Ukc0MzNXSjBIRGpteWZhYW1RelRjcis2M3dDbVZLdWRRNVd1YzZxVzdmS09wUEN5NHhHSDVZanFIYmhUNkdGc1FFYlcyRnJEczcvQURVU0QycG5PNU4wSGlyeE5UV0RiYWsySG1WSytrWjdYZkJvSFRQYkN6bjd4L0MzbVZzMkdNTmFHalpvQUhnRmJPSGNHYlRSZ0RWN3JHUjNhZXp3Q3V5MDhlSFRHZmt6NnFJaUt4V0lpSUNJaUFpSWdJaUlDSWlBaUlnS25QRUhOY3c3T0JCOHdxaUlOVnVhWTVIUnUzWTR0UGtkQ3A3YWdOYm1KMENtOGQwV1dSdFFObit3L3dDSWJIMCtpd25HTVRzM0lENHJCeVlheTAzWVo3eDJsVjNFamRzcFBmZFJtWTJ6bXgzazdUNks0d2NQUExXbGo0aGNBa3Vibk92bXZVdkRkU0Jkc3NYK1VQMVVlM3RMdXR6OGZjUGRqMDh5dmNQRXJocStQVHV2ZFYyWVZWODUyandqQStwWHY5bVNFMmtrYTV2TUZqUWZVSnFIZGNLV3ViSU16VDRqbWtyMWl4cU9vbWMxdXdOdkVLNk54VmpodlpOTzdWNTNxejRqVUJqU2VmSlZLM0UyamJWWTdWVHVrZGJ2VTVGZVZaNTBXNFFKSG1kNHVHZTBMN1pqdDZiclllSllGQk9RNlJudE5jSEJ6U1dPdU8wamZ6VnE2T3FMcTZRRzFzNUo4aG9Qb1ZsQzE0VFdMSm5lNGlJcG9pSWlBaUlnSWlJQ0lpQWlJZ0lpSUNJckp4SnhYU1VMTTlUTTFwKzZ3ZTFJN3Vhd2FsQmUxNGZLMGJ1QThTQXVlK01PbU9wcUNXVWdOUEZ0bU5uU3U3eWRtK0F1dGQxdUt6eW04czhyeitlUjUrVjBIV1BFRWxQTkErSjg4YmN3T1VsN2RIY2p2MnJSRDVDWEZwc1NDUVNEY0czTUhtRnJldzdBc2g0ZnhPMW8zSDRTZm9xdVhIYzJzNDh0WFRkR0ZOTG9tRUg3bytTaTQ1V3pSa05ZOGo1cURnR0w1WXcyK29WTEVhN09TZnFzVjh0dSt5ZGhPSXlQZGtrTjdqUTk2dXoyV1dJUXk1U0R6R3U2dkxzV0dYVThrTnJEamY4QUZkNnEzUGtOdExLUlhUWm5FcU0xbHoyM1U1RmRxanFWVnVJMk9sZHN3WDhUeUN1ZFBoVHJYZG9Pem1yZHhuSGFrZUJzTXZwZFR4MXZTR1c5YlpSdzcwMFJzYTJLZW1MV3RBYUhRdURyQWFhdE8vcXRwNERqOVBXUmlXbmtEd2R4czRkem1uVUZjYzNWeHdYSEo2V1FTd1NPWTRkaDM3aU9ZV3hsZGtJdFc5SDNTM0hWT1pUVlRlcm1kWnJYaXdqZTdrRCtFbjBXMGtCRVJBUkVRRVJFQkVSQVJFUUVSVXF1b2JHeDBqalpyR3VjNG5zQXVVR0VkSy9ISXcrRHFvaURVemdpSWI1RzdHUjNoZlFjeXVjS3FvZks0eVN2Yzk3dDNQSmNUNXFkeE5qVDYycWxxbmtuTzQ1QWZ1c0I5bG83TlBxcmNnb1BDcDNVaVFLT1JxZ0JTS09FdmV4amZlYzVyVytKS1J3QzJxei9BS08rRTNHUnRVOXBEV2c5V0hia243M2dvNTVUR2JxV0dOeXVsM3FNQ2V3QXhFblFabTg3MjNDaXVNcmZlWTRkdnNuOUZua3RMWVhYcW5QSllPcHU2V0Foeno3c2JqMit5N2IraWt4WVRVeWJSdUEvTllENXJZRFY5WE9wM29ZZFM4Sk8za2VCM04xUHFycEJockkvZGFQSGMrcXZFaFVPUk9xMDZaRUdvYXNZNHJoelUwby9LVDZMSzVtTFczR3ZFZHk2bWg1WGJLLzZ0YitxdDQ1YmV5dmtzazdzREM5Z0txMk5lbk5XMWlVUVNDQ0NRUVFRUm9RUnNWMEIwUWRKSDJvQ2hxM0FUdEZvWGsyNjBEbDhZK2EwQ1FsUE81ajJ5TUphNWhEbWthRUViRkIycWl3Zm9yNDFHSVUrV1FqN1JEWVNnYVpoeWVCM3JPRUJFUkFSRVFFUkVCRjhjNEFFblFEVWtyUlBHUFNGVXpUdkZOTytLQnB5czZ1elM2MzNpN2ZYc3VnM3NTdFQ5T1BGeldVd29ZSldsODdyVDVIQWxzVGRTRGJiTWJEd3V0YTFXT1ZVZ0lmVXpPQjNEcHBDUFM2eGl1WWM1SjU3SUtiVjlLOEFyMGcrT0Nva2FoVnlGVEJBYzBuWU9hVDRBNm9OcDhEY0dNTFdUVHR6T2Rad2E3Wm9PMm5NclprZE9HaXdHeXRXQ1Rpd3RzNE5JOENGZkF2TjVNcmxlNzBlUEdZenNwR0c2aFRRV0t1clF2c3NJSVZlMW1scGFWOUpYMmRtVXFpWHFUajVJVkdlVlVrZW8wajEySTFqUEhXUC9ab3NqRCs5bEJEZnl0NXVXcGdPWlYzNHN4TDdSVlNTWDlrSHEyZkMzVDVtNVZwdXQvSGgwNHNQSm4xWlBTOGxmQzVWNHFVa1puZXkzdlZpdEdEQzQyQzlkVTBkNVZSN2g3ck51WlZXR0JCSzRYeCtXZ3FtVk1keGxQdHQ1UFlkMmxkVzREaThkVkJIVXhHN1pHZ2p1UE1IdkM1Uyt6QTZFTExlQitNcHNORG1NQWtpY1E0eHZKRmp6TFR5dWc2UVJRY0R4RVZGUEZVQnVVVE1iSUFkeG1GN0tjZ0lpSUNJaUNoWC93QUtUNEgvQU1wWExnL2hOOGtSQlFLZ1lseTgwUkJibDZDSWcrdVVlYlpmVVFyZmZDZjhLSCt5aS9sQ3k5cStJdk16OHZTdzhLclZVQ0lxMWkzWWtyWTVFVTRoVkY2aDFudVArQi8wS0lwUkd0RWZxVjhLSXZTZWM5UTdqeENtNDE3b1JFRUtrVnhnUkVFcHErdjJSRUhTL0EzK3o2VCt3aS9sQ3ZpSWdJaUlDSWlELzltUlhhbzZsVmJpTmpwWGJNRi9FOGdyblQ0VTYxM2FEczVxM2NaeDJwSGdiREw2WFU4ZGIwaGx2VzJVY085TkViR3RpbnBpMXJRR2gwTGc2d0dtclR2NnJhZUE0L1Qxa1lscDVBOEhjYk9IYzVwMUJYSE4xY2NGeHllbGtFc0VqbU9IWWQrNGptRnNaWFpDTFZ2UjkwdHgxVG1VMVUzcTVuV2ExNHNJM3U1QS9oSjlGdEpBUkVRRVJFQkVSQVJFUUVSRUJFVktycUd4c2RJNDJheHJuT0o3QUxsQmhIU3Z4eU1QZzZxSWcxTTRJaUcrUnV4a2Q0WDBITXJuQ3FxSHl1TWtyM1BlN2R6eVhFK2FuY1RZMCt0cXBhcDVKenVPUUg3ckFmWmFPelQ2cTNJS0R3cWQxSWtDamthb0FVaWpoTDNzWTMzbk9hMXZpU2tjQXRxcy93Q2p2aE54a2JWUGFRMW9QVmgyNUorOTRLT2VVeG02bGhqY3JwZDZqQW5zQU1SSjBHWnZPOXR3b3JqSzMzbU9IYjdKL1JaNUxTMkYxNnB6eVdEcWJ1bGdJYzgrN0c0OXZzdTIvb3BNV0UxTW0wYmdQeldBK2EyQTFmVnpxZDZHSFV2Q1R0NUhnZHpkVDZxNlFZYXlQM1dqeDNQcXJ4SVZEa1RxdE9tUkJxR3JHT0s0YzFOS1B5aytpeXVaaTF0eHJ4SGN1cG9lVjJ5dityVy9xcmVPVzNzcjVMSk83QXd2WUNxdGpYcHpWdFlsRUVnZ2drRUVFRWFFRWJGZEFkRUhTUjlxQW9hdHdFND08L0JEQj4KICAgIDwvQklSPgo8L0JJUj4K";
		List<IdentityInfoDTO> identityList = new ArrayList<>();
		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		identityInfoDTO.setLanguage(null);
		identityInfoDTO.setValue(value);
		identityList.add(identityInfoDTO);
		idInfo.put("documents.individualBiometrics", identityList);
	}

	@Test
	public void validUIN2() {
		try {
			prepareMap(idInfo);
			Set<String> langCodes = new HashSet<>();
			langCodes.add("ara");
			EKycResponseDTO k = kycServiceImpl.retrieveKycInfo(limitedList(), langCodes, idInfo);
			assertNotNull(k);
		} catch (IdAuthenticationBusinessException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void validUIN3() {
		try {
			prepareMap(idInfo);
			Set<String> langCodes = new HashSet<>();
			langCodes.add("ara");
			EKycResponseDTO k = kycServiceImpl.retrieveKycInfo(fullKycList(), langCodes, idInfo);
			assertNotNull(k);
		} catch (IdAuthenticationBusinessException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void validUIN4() {
		try {
			prepareMap(idInfo);
			Set<String> langCodes = new HashSet<>();
			langCodes.add("ara");
			EKycResponseDTO k = kycServiceImpl.retrieveKycInfo(fullKycList(), langCodes, idInfo);
			assertNotNull(k);
		} catch (IdAuthenticationBusinessException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void validUIN5() throws IdAuthenticationDaoException {
		try {
			prepareMap(idInfo);
			Set<String> langCodes = new HashSet<>();
			langCodes.add("ara");
			EKycResponseDTO k = kycServiceImpl.retrieveKycInfo(fullKycList(), langCodes, idInfo);
			assertNotNull(k);
		} catch (IdAuthenticationBusinessException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void validUIN6() throws IdAuthenticationDaoException, IOException, IdAuthenticationBusinessException {
		Set<String> langCodes = new HashSet<>();
		langCodes.add("ara");
			
		try {
			kycServiceImpl.retrieveKycInfo(fullKycList(), langCodes, idInfo);
		}
		catch (IdAuthenticationBusinessException ex) {
			assertEquals(IdAuthenticationErrorConstants.BIOMETRIC_MISSING.getErrorCode(), ex.getErrorCode());
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map<String, List<IdentityInfoDTO>> getIdInfo(String uinRefId) throws IdAuthenticationDaoException {
		ObjectMapper mapper = new ObjectMapper();
		try {
			Map<String, Object> outputMap = mapper.readValue(value, new TypeReference<Map>() {
			});

			return outputMap.entrySet().parallelStream()
					.filter(entry -> entry.getKey().equals("response") && entry.getValue() instanceof Map)
					.flatMap(entry -> ((Map<String, Object>) entry.getValue()).entrySet().stream())
					.filter(entry -> entry.getKey().equals("identity") && entry.getValue() instanceof Map)
					.flatMap(entry -> ((Map<String, Object>) entry.getValue()).entrySet().stream())
					.collect(Collectors.toMap(Entry<String, Object>::getKey, entry -> {
						Object val = entry.getValue();
						if (val instanceof List) {
							List<Map> arrayList = (List) val;
							return arrayList.stream().filter(elem -> elem instanceof Map)
									.map(elem -> (Map<String, Object>) elem).map(map1 -> {
										IdentityInfoDTO idInfo = new IdentityInfoDTO();
										idInfo.setLanguage(
												map1.get("language") != null ? String.valueOf(map1.get("language"))
														: null);
										idInfo.setValue(String.valueOf(map1.get("value")));
										return idInfo;
									}).collect(Collectors.toList());

						}
						return Collections.emptyList();
					}));
		} catch (IOException e) {
			throw new IdAuthenticationDaoException();
		}

	}

	private void deleteBootStrapFile() {
		String property = System.getProperty("java.io.tmpdir");
		property = property.concat("/bootstrap.min.css");
		File file = new File(property);
		if (file.exists()) {
			file.delete();
		}
	}

	private List<String> limitedList() {
		String s = "fullName,firstName,middleName,lastName,gender,addressLine1,addressLine2,addressLine3,city,province,region,postalCode,photo";
		List<String> allowedKycList = Arrays.asList(s.split(","));
		return allowedKycList;
	}
	private List<String> limitedList_nophoto() {
		String s = "fullName,firstName,middleName,lastName,gender,addressLine1,addressLine2,addressLine3,city,province,region,postalCode";
		List<String> allowedKycList = Arrays.asList(s.split(","));
		return allowedKycList;
	}

	private List<String> fullKycList() {
		String s = "fullName,firstName,middleName,lastName,dateOfBirth,gender,phone,email,addressLine1,addressLine2,addressLine3,city,province,region,postalCode,photo";
		return Arrays.asList(s.split(","));
	}
	
	private Map<String, String> entityInfo(){
		Map<String, String> map = new HashMap<>();
		map.put("FACE", "agsafkjsaufdhkjesadfjdsklkasnfdkjbsafdjbnadsfkjfds");
		return map;
	}
	
	@Test
	public void testGetKycInfo_FullAddress() {
		Map<String, List<IdentityInfoDTO>> idInfo = Map.of(
				"addressLine1", List.of(new IdentityInfoDTO("eng", "Address Line1")),
				"addressLine2", List.of(new IdentityInfoDTO("eng", "Address Line2")),
				"addressLine3", List.of(new IdentityInfoDTO("eng", "Address Line3")),
				"city", List.of(new IdentityInfoDTO("eng", "City")),
				"region", List.of(new IdentityInfoDTO("eng", "Region")),
				"province", List.of(new IdentityInfoDTO("eng", "Province")),
				"postalCode", List.of(new IdentityInfoDTO(null, "12345"))
				);
		
		
		List<String> allowedkycAttributes = List.of("addressLine1", "addressLine2", "addressLine3", "city", "region", "province", "postalCode", "fullAddress");
		Map<String, List<IdentityInfoDTO>> filteredIdentityInfo = idInfo;
		Set<String> langCodes = Set.of("eng");
		Map<String, Object> kycInfo = ReflectionTestUtils.invokeMethod(kycServiceImpl2, "getKycInfo", allowedkycAttributes, filteredIdentityInfo, langCodes);
		
		Map<String, String> expectedMap = Map.of("addressLine1_eng", "Address Line1",
		         "addressLine2_eng", "Address Line2" ,
		         "addressLine3_eng", "Address Line3" ,
		         "location1_eng", 		"City",
		         "location2_eng", 		"Region",
		         "location3_eng", 	"Province" ,
		         "postalCode", 	"12345",
		         "fullAddress_eng", "Address Line1" + fullAddrSep
				 + "Address Line2" + fullAddrSep
				 + "Address Line3" + fullAddrSep
				 + "City" + fullAddrSep
				 + "Region" + fullAddrSep
				 + "Province" + fullAddrSep
				 + "12345");
		assertTrue(kycInfo.entrySet().containsAll(expectedMap.entrySet()));
	}
	
	@Test
	public void testGetKycInfo_Name() throws IdAuthenticationBusinessException {
		Map<String, List<IdentityInfoDTO>> idInfo = Map.of(
				"fullName", List.of(new IdentityInfoDTO("eng", "My Name"))
				);
		
		List<String> allowedkycAttributes = List.of("fullName");
		Map<String, List<IdentityInfoDTO>> filteredIdentityInfo = idInfo;
		Set<String> langCodes = Set.of("eng");
		Map<String, Object> kycInfo = ReflectionTestUtils.invokeMethod(kycServiceImpl2, "getKycInfo", allowedkycAttributes, filteredIdentityInfo, langCodes);
		
		Map<String, String> expected = Map.of("name_eng", "My Name");
		assertTrue(kycInfo.entrySet().containsAll(expected.entrySet()));

	}
	
	@Test
	public void testGetKycInfo_Name_twoLangs() throws IdAuthenticationBusinessException {
		Map<String, List<IdentityInfoDTO>> idInfo = Map.of(
				"fullName", List.of(new IdentityInfoDTO("eng", "My Name"), new IdentityInfoDTO("ara", "My ara Name"))
				);
		
		List<String> allowedkycAttributes = List.of("fullName");
		Map<String, List<IdentityInfoDTO>> filteredIdentityInfo = idInfo;
		Set<String> langCodes = Set.of("eng", "ara");
		Map<String, Object> kycInfo = ReflectionTestUtils.invokeMethod(kycServiceImpl2, "getKycInfo", allowedkycAttributes, filteredIdentityInfo, langCodes);
		
		Map<String, String> expected = Map.of("name_eng", "My Name");
		assertTrue(kycInfo.entrySet().containsAll(expected.entrySet()));

	}
	
	@Test
	public void testGetKycInfo_NameMap2() throws IdAuthenticationBusinessException {
		IDAMappingConfig config = Mockito.mock(IDAMappingConfig.class);
		ReflectionTestUtils.setField(idInfoHelper2, "idMappingConfig", config);
		ReflectionTestUtils.setField(kycServiceImpl2, "mappingConfig", config);
		Mockito.when(config.getDynamicAttributes()).thenReturn(Map.of("name", List.of("firstName", "lastName")));
		Map<String, List<IdentityInfoDTO>> idInfo = Map.of(
				"firstName", List.of(new IdentityInfoDTO("eng", "First Name")),
				"lastName", List.of(new IdentityInfoDTO("eng", "Last Name"))
				);
		
		List<String> allowedkycAttributes = List.of("firstName", "lastName", "name");
		Map<String, List<IdentityInfoDTO>> filteredIdentityInfo = idInfo;
		Set<String> langCodes = Set.of("eng");
		Map<String, Object> kycInfo = ReflectionTestUtils.invokeMethod(kycServiceImpl2, "getKycInfo", allowedkycAttributes, filteredIdentityInfo, langCodes);
		
		Map<String, String> expected = Map.of("firstName_eng", "First Name", 
				"lastName_eng", "Last Name", 
				"name_eng", "First Name Last Name");
		assertTrue(kycInfo.entrySet().containsAll(expected.entrySet()));

	}
	
	@Test
	public void testGetKycInfo_Name2() throws IdAuthenticationBusinessException {
		Map<String, List<IdentityInfoDTO>> idInfo = Map.of(
				"firstName", List.of(new IdentityInfoDTO("eng", "First Name")),
				"lastName", List.of(new IdentityInfoDTO("eng", "Last Name"))
				);
		
		List<String> allowedkycAttributes = List.of("firstName", "lastName", "name2");
		Map<String, List<IdentityInfoDTO>> filteredIdentityInfo = idInfo;
		Set<String> langCodes = Set.of("eng");
		Map<String, Object> kycInfo = ReflectionTestUtils.invokeMethod(kycServiceImpl2, "getKycInfo", allowedkycAttributes, filteredIdentityInfo, langCodes);
		
		Map<String, String> expected = Map.of("firstName_eng", "First Name", "lastName_eng", "Last Name", "name2_eng", "First Name Last Name");
		assertTrue(kycInfo.entrySet().containsAll(expected.entrySet()));
		
	}
	
	@Test
	public void testGetKycInfo_Phone() throws IdAuthenticationBusinessException {
		Map<String, List<IdentityInfoDTO>> idInfo = Map.of(
				"phone", List.of(new IdentityInfoDTO(null, "9988776655"))
				);
		Map<String, String> expected1 = Map.of("phone", "9988776655");
		Map<String, String> expected2 = Map.of("phoneNumber", "9988776655");
		
		List<String> allowedkycAttributes = List.of("phone");
		Map<String, List<IdentityInfoDTO>> filteredIdentityInfo = idInfo;
		Set<String> langCodes = Set.of("eng");
		Map<String, Object> kycInfo = ReflectionTestUtils.invokeMethod(kycServiceImpl2, "getKycInfo", allowedkycAttributes, filteredIdentityInfo, langCodes);
		
		assertTrue(kycInfo.entrySet().containsAll(expected1.entrySet()) || kycInfo.entrySet().containsAll(expected2.entrySet()));
	}
	
	@Test
	public void testGetKycInfo_Age() throws IdAuthenticationBusinessException {
		String dateBefore10Years = LocalDate.now().minusYears(10L).format(DateTimeFormatter.ofPattern(dobPattern));
		Map<String, List<IdentityInfoDTO>> idInfo = Map.of(
				"dateOfBirth", List.of(new IdentityInfoDTO(null, dateBefore10Years))
				);
		Map<String, String> expected = Map.of("age", "10");
		
		List<String> allowedkycAttributes = List.of("age");
		Map<String, List<IdentityInfoDTO>> filteredIdentityInfo = idInfo;
		Set<String> langCodes = Set.of("eng");
		Map<String, Object> kycInfo = ReflectionTestUtils.invokeMethod(kycServiceImpl2, "getKycInfo", allowedkycAttributes, filteredIdentityInfo, langCodes);
		
		assertTrue(kycInfo.entrySet().containsAll(expected.entrySet()));
	}
	
	@Test
	public void testGetKycInfo_MappedDynamicAttribWithLang() throws IdAuthenticationBusinessException {
		Map<String, List<IdentityInfoDTO>> idInfo = Map.of(
				"residenceStatus", List.of(new IdentityInfoDTO("eng", "Citizen"))
				);
		
		List<String> allowedkycAttributes = List.of("residenceStatus");
		Map<String, List<IdentityInfoDTO>> filteredIdentityInfo = idInfo;
		Set<String> langCodes = Set.of("eng");
		Map<String, Object> kycInfo = ReflectionTestUtils.invokeMethod(kycServiceImpl2, "getKycInfo", allowedkycAttributes, filteredIdentityInfo, langCodes);
		
		Map<String, String> expected = Map.of("residenceStatus_eng", "Citizen");
		assertTrue(kycInfo.entrySet().containsAll(expected.entrySet()));
		
	}
	
	@Test
	public void testGetKycInfo_NonMappedDynamicAttribWithLang() throws IdAuthenticationBusinessException {
		Map<String, List<IdentityInfoDTO>> idInfo = Map.of(
				"newAttribute", List.of(new IdentityInfoDTO("eng", "New Attribute"))
				);
		
		List<String> allowedkycAttributes = List.of("newAttribute");
		Map<String, List<IdentityInfoDTO>> filteredIdentityInfo = idInfo;
		Set<String> langCodes = Set.of("eng");
		Map<String, Object> kycInfo = ReflectionTestUtils.invokeMethod(kycServiceImpl2, "getKycInfo", allowedkycAttributes, filteredIdentityInfo, langCodes);
		
		Map<String, String> expected = Map.of("newAttribute_eng", "New Attribute");
		assertTrue(kycInfo.entrySet().containsAll(expected.entrySet()));
	}
	
	@Test
	public void testGetKycInfo_MappedDynamicAttribWithoutLang() throws IdAuthenticationBusinessException {
		Map<String, List<IdentityInfoDTO>> idInfo = Map.of(
				"introducerRID", List.of(new IdentityInfoDTO(null, "11223344"))
				);
		
		List<String> allowedkycAttributes = List.of("introducerRID");
		Map<String, List<IdentityInfoDTO>> filteredIdentityInfo = idInfo;
		Set<String> langCodes = Set.of("eng");
		Map<String, Object> kycInfo = ReflectionTestUtils.invokeMethod(kycServiceImpl2, "getKycInfo", allowedkycAttributes, filteredIdentityInfo, langCodes);
		
		Map<String, String> expected = Map.of("introducerRID", "11223344");
		assertTrue(kycInfo.entrySet().containsAll(expected.entrySet()));
	}
	
	@Test
	public void testGetKycInfo_NonMappedDynamicAttribWithoutLang() throws IdAuthenticationBusinessException {
		Map<String, List<IdentityInfoDTO>> idInfo = Map.of(
				"newAttribute1", List.of(new IdentityInfoDTO(null, "New Attribute1"))
				);
		
		List<String> allowedkycAttributes = List.of("newAttribute1");
		Map<String, List<IdentityInfoDTO>> filteredIdentityInfo = idInfo;
		Set<String> langCodes = Set.of("eng");
		Map<String, Object> kycInfo = ReflectionTestUtils.invokeMethod(kycServiceImpl2, "getKycInfo", allowedkycAttributes, filteredIdentityInfo, langCodes);
		
		
		Map<String, String> expected = Map.of("newAttribute1", "New Attribute1");
		assertTrue(kycInfo.entrySet().containsAll(expected.entrySet()));
	}
	
	@Test
	public void testGetKycInfo_photo() throws IdAuthenticationBusinessException {
		Map<String, List<IdentityInfoDTO>> idInfo = Map.of(
				IdAuthCommonConstants.PHOTO, List.of(new IdentityInfoDTO(null, "face image"))
				);
		
		List<String> allowedkycAttributes = List.of("photo");
		Map<String, List<IdentityInfoDTO>> filteredIdentityInfo = idInfo;
		Set<String> langCodes = Set.of("eng");
		Map<String, Object> kycInfo = ReflectionTestUtils.invokeMethod(kycServiceImpl2, "getKycInfo", allowedkycAttributes, filteredIdentityInfo, langCodes);
		
		Map<String, String> expected = Map.of("photo", "face image");
		assertTrue(kycInfo.entrySet().containsAll(expected.entrySet()));
	}
	
	@Test
	public void testGetKycInfo_photo_twoLanguages() throws IdAuthenticationBusinessException {
		Map<String, List<IdentityInfoDTO>> idInfo = Map.of(
				IdAuthCommonConstants.PHOTO, List.of(new IdentityInfoDTO(null, "face image"))
				);
		
		List<String> allowedkycAttributes = List.of("photo");
		Map<String, List<IdentityInfoDTO>> filteredIdentityInfo = idInfo;
		Set<String> langCodes = Set.of("eng", "ara");
		Map<String, Object> kycInfo = ReflectionTestUtils.invokeMethod(kycServiceImpl2, "getKycInfo", allowedkycAttributes, filteredIdentityInfo, langCodes);
		
		Map<String, String> expected = Map.of("photo", "face image");
		assertTrue(kycInfo.entrySet().containsAll(expected.entrySet()));
	}
	
	@Test
	public void testGetKycInfo_Face() throws IdAuthenticationBusinessException {
		Map<String, List<IdentityInfoDTO>> idInfo = Map.of(
				"Face", List.of(new IdentityInfoDTO(null, "face image"))
				);
		
		List<String> allowedkycAttributes = List.of("Face");
		Map<String, List<IdentityInfoDTO>> filteredIdentityInfo = idInfo;
		Set<String> langCodes = Set.of("eng");
		Map<String, Object> kycInfo = ReflectionTestUtils.invokeMethod(kycServiceImpl2, "getKycInfo", allowedkycAttributes, filteredIdentityInfo, langCodes);
		
		Map<String, String> expected = Map.of("Face", "face image");
		assertTrue(kycInfo.entrySet().containsAll(expected.entrySet()));
	}
	
	@Test
	public void testGetKycInfo_photo_withPhotoNotInAllowedKycAttrib() throws IdAuthenticationBusinessException {
		Map<String, List<IdentityInfoDTO>> idInfo = Map.of();
		
		List<String> allowedkycAttributes = List.of();
		Map<String, List<IdentityInfoDTO>> filteredIdentityInfo = idInfo;
		Set<String> langCodes = Set.of("eng");
		Map<String, Object> kycInfo = ReflectionTestUtils.invokeMethod(kycServiceImpl2, "getKycInfo", allowedkycAttributes, filteredIdentityInfo, langCodes);
		
		Map<String, String> expected = Map.of();
		assertTrue(kycInfo.entrySet().containsAll(expected.entrySet()));
	}

	@Test
	public void generateAndSaveKycTokenTest() throws DecoderException {
		String idHash = "73616d706c65496448617368";
		String authToken = "testAuthToken";
	    String oidcClientId = "sampleOidcClientId";
		String requestTime = "2023-10-19T12:35:57.835Z"; 
		String tokenGenerationTime = "2023-10-19T12:35:57.835Z";
		String reqTransactionId = "abc1234";
		String resKycToken = "sampleKycToken";
		KycTokenData kycTokenData = new KycTokenData();
		
		Mockito.when(securityManager.generateKeyedHash(Mockito.any())).thenReturn(resKycToken);
		Mockito.when(kycTokenDataRepo.saveAndFlush(kycTokenData)).thenReturn(null);

		String kycToken = ReflectionTestUtils.invokeMethod(kycServiceImpl2, "generateAndSaveKycToken", idHash, authToken, oidcClientId, requestTime, 
			tokenGenerationTime, reqTransactionId);
		assertEquals(kycToken, resKycToken);
	}

	@Test
	public void isKycTokenExpireTest() {
		LocalDateTime currentTime = LocalDateTime.now();
		LocalDateTime tokenIssuedTime = currentTime.minusSeconds(20);
		String dummyToken  = "dummyToken";
		boolean valid = ReflectionTestUtils.invokeMethod(kycServiceImpl2, "isKycTokenExpire", tokenIssuedTime, dummyToken);
		assertFalse(valid);
	}

	@Test
	public void isKycTokenExpireTokenExpiredTest() {
		LocalDateTime currentTime = LocalDateTime.now();
		LocalDateTime tokenIssuedTime = currentTime.plusSeconds(310);
		String dummyToken  = "dummyToken";
		boolean valid = ReflectionTestUtils.invokeMethod(kycServiceImpl2, "isKycTokenExpire", tokenIssuedTime, dummyToken);
		assertTrue(valid);
	}

	@Test
	public void buildKycExchangeResponseTest() throws IdAuthenticationBusinessException {
		
		String dummySubject = "dummyPSUToken";
		List<String> consentedAttributes = Arrays.asList("name", "gender", "dob", "address", "individual_id", "sub");
		List<String> consentedLocales = Arrays.asList("ara");		
		String idVid = "12232323121";
		KycExchangeRequestDTO kycExchangeRequestDTO = new KycExchangeRequestDTO();
		ReflectionTestUtils.setField(kycServiceImpl2, "consentedIndividualAttributeName", "individual_id");
		ReflectionTestUtils.setField(kycServiceImpl2, "consentedAddressAttributeName", "address");
		ReflectionTestUtils.setField(kycServiceImpl2, "addressSubsetAttributes", new String[]{});
		ReflectionTestUtils.setField(kycServiceImpl2, "consentedFaceAttributeName", "picture");
		ReflectionTestUtils.setField(kycServiceImpl2, "idInfoHelper", idInfoHelper2);
		
		String resKycToken = "responseJWTToken";
		Mockito.when(securityManager.signWithPayload(Mockito.anyString())).thenReturn(resKycToken); 
		Map<String, String> faceMap = prepareFaceData(idInfo);
		Mockito.when(idInfoHelper.getIdEntityInfoMap(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(faceMap);

		String response = kycServiceImpl2.buildKycExchangeResponse(dummySubject, idInfo, consentedAttributes, consentedLocales, idVid, kycExchangeRequestDTO);
		assertEquals(response, resKycToken);
	}

	private Map<String, String>  prepareFaceData(Map<String, List<IdentityInfoDTO>> idInfo) {
		String faceData = "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiIHN0YW5kYWxvbmU9InllcyI_Pgo8QklSIHhtbG5zPSJodHRwOi8vc3RhbmRhcmRzLmlzby5vcmcvaXNvLWllYy8xOTc4NS8tMy9lZC0yLyI-CiAgICA8VmVyc2lvbj4KICAgICAgICA8TWFqb3I-MTwvTWFqb3I-CiAgICAgICAgPE1pbm9yPjE8L01pbm9yPgogICAgPC9WZXJzaW9uPgogICAgPENCRUZGVmVyc2lvbj4KICAgICAgICA8TWFqb3I-MTwvTWFqb3I-CiAgICAgICAgPE1pbm9yPjE8L01pbm9yPgogICAgPC9DQkVGRlZlcnNpb24-CiAgICA8QklSSW5mbz4KICAgICAgICA8SW50ZWdyaXR5PmZhbHNlPC9JbnRlZ3JpdHk-CiAgICA8L0JJUkluZm8-Cgk8QklSPgogICAgICAgIDxCSVJJbmZvPgogICAgICAgICAgICA8SW50ZWdyaXR5PmZhbHNlPC9JbnRlZ3JpdHk-CiAgICAgICAgPC9CSVJJbmZvPgogICAgICAgIDxCREJJbmZvPgogICAgICAgICAgICA8Rm9ybWF0T3duZXI-MjU3PC9Gb3JtYXRPd25lcj4KICAgICAgICAgICAgPEZvcm1hdFR5cGU-ODwvRm9ybWF0VHlwZT4KICAgICAgICAgICAgPENyZWF0aW9uRGF0ZT4yMDE5LTAxLTI5VDE5OjExOjMzLjQzNCswNTozMDwvQ3JlYXRpb25EYXRlPgogICAgICAgICAgICA8VHlwZT5GYWNlPC9UeXBlPgogICAgICAgICAgICA8TGV2ZWw-UmF3PC9MZXZlbD4KICAgICAgICAgICAgPFB1cnBvc2U-RW5yb2xsPC9QdXJwb3NlPgogICAgICAgICAgICA8UXVhbGl0eT45NTwvUXVhbGl0eT4KICAgICAgICA8L0JEQkluZm8-CiAgICAgICAgPEJEQj5Sa0ZEQURBek1BQUFBUVlQQUFFQUFBQUFBUVgrQitZTUhBMHBEUUlpQlFBQUFBQUJVQUFCQUFFQUFQOEFBQUFBQUFBQUFBQUFBQUFBQUFFQ0FQQUJRQUFBQVFBQkFBRUZ4Z0FBQUF4cVVDQWdEUXFIQ2dBQUFCUm1kSGx3YW5BeUlBQUFBQUJxY0RJZ0FBQUFSMnB3TW1nQUFBQVdhV2hrY2dBQUFVQUFBQUR3QUFNSEJ3QUFBQUFBRDJOdmJISUJBQUFBQUFBUUFBQUFHbkpsY3lBQUFBQVNjbVZ6WXdQb0FQNEQ2QUQrQkFRQUFBQUFhbkF5WS85UC8xRUFMd0FBQUFBQThBQUFBVUFBQUFBQUFBQUFBQUFBQVBBQUFBRkFBQUFBQUFBQUFBQUFBd2NCQVFjQkFRY0JBZjljQUExQVFFaElVRWhJVUVoSVVQOVNBQXdBQUFBQkFRTUVCQUFCLzJRQURnQUJURlJmU2xBeVh6SXlOditRQUFvQUFBQUJCUFVBQWYrVDM0ZCs0SnQwUE05T2NqRjFUdSt6SC8wMytNRFpMRzdqbWlEMUQ2ZS9OWEZpbWdYZ1l1bCtlWTEva1k0R3Zsc29HQWJGeDRwRlNseVNScE1FNG90SVJZNnFEYkxZUjI2SGhGY0tIT1lSeWFxNlVLbkEwWmI2cTVIK1pRY3c5ZzMrTFVPSXUvZmxhczJIaElzWDFqQllDeENpMVpOY1IwR3F4YXdhSWNlVkpvVTlwN2pCWkNIbHBJdXhyazJyYmY4aitadEVlZ3VJcFRscVNuWWJoT0tUYW9ZMFVKOXA3U2VOaWFzTmRJZEpXUGQrSERtUnpveWo0dVdUek9aM04xbVdFQll3S2w4S3VkYlRCS3ByT3RXdHBhNVR0dS9vb0N6ekpzVVdreVZxZTNsa0RobUZHMkdFSGZtYXp0cldHeUwyaEcybDlxN2JnUGJzU1BaRXNtcDJUc1gzZEh6aUhGbmdoSFcvYUZqOXNjZDF1WE16S0t5bVJBUFdFTS8rKzRoaVRDbkpsdEhTOXFZT2RyL1JiVEZyZ2ZnL1lSRFRPRHVmblUxbkZ0QlZqNEoxZ2hnV0JEeVZRRzIwaThuUkF0aGd0N2xMeG14b3NjM0VzNTlNWTZmV0x1UXF4TFJGUm5mcytUWHgvRzNOemR5WmZvYlBQTDVjSFdYZmVRTHNEVUJIS21Xb3NQZGJDK1VDRFFSdEFhRkxHWGh0aDJDY05IL2w4R0RKNkF4aFFJRzhGUTJSMWVJVS9qVnEvenNScjAzOHY2QTc1aUdVWElSV2Jrdmd5ZVplbDBvTDZQQWZRTGNpNTl3OU9NWVc4SVgwMEZxMXBzV0hLaGVsenVEeStXRWlXSEF1TFBOVzJWeEdhLzl5VGEzSy9ybDRPSWpUZDYwanBZay9nQkRYVnFQMnFtNTEvMlhySk5JamNVampZZHFyZS94REk5aHUwRFVraFp4aXc5cDlsNzZBQWcrdlFleThpL0EyeW5CcEg0VzdCci93WVljNDh3YjhHbUtJQkp6Q0ZzRVI5RW5NcjN0cHNFZ3IzK0s2Y0hYRTg0VDEzdm54UUl4MkEwa0NvZDIxT0U2SWdZYzFKQjNoYmswZXZ2YTFTVDNUKzRrck45YUtMUVNaUmJDVUFlaGx6WUZ3aEp0ZU1mNytXamtrODh3WWZncmpBSGtSNjV3bDQ1c2RxcXhnaEJnMUd4WGtaYTl2T200aFNYUFlscUNaQ0xJS3BrbEROc3Ira29seG9CQzdPZHRKT1NpOWIvSElBdkNPWW51enU4MjNkSjBOT1NDamMxbkdLb0ZFaFhSam8wK3lmQiswblZ0cFNnR1FRS295OUFiNFNDanFuNklhNGczTDBTSmhQUGE3aXUyK0VNeXowTjJRUXVZU1Q5NWNrOVFhSGdRMGJHMXBkTC9ORHI2T2lobmZHRlJnUmZPV3U2Z2pLbEVUVWx6ejFKczRpcnVxV1R1K1UxQ2V1aVBDT3VjMzJwSlZOYjdUdkV1TEhWTndMeTB0NjAxK3ZzaWVNNnd3UHozYWZMMEM1T0lORm9BTmxpbDB5OHBKZ1F4dUc2RytaQ0t1ZGZiQVlWREhadTl5YlRma1JyNktvWVUySzlkeEozYzJUbHoraGVqSlBJY2o3V0pvckd3cDhXaEVjUXhJTjhxMXlDdEU4VFEyNHZZUWdBWmVQODF3OEQvYUlhTnJPTUZhU1ViQkZDR05hOCtPalVobjhUZGRwb2lpRkh6T2l4UE1ERkhWa1NYd3AwSk5tM0pjNnMvOEZxMlZldFRiNjdyemxjTld0dWRTc2VXZzk2ZkgxZGJBRW1HYlF1NWtqRTVaRERrY1hWdVp6OU9IYWUydVNQU1NTRjBhb0xqaFJ2NUtueTgwZitodGFNV3BGd05wY1FoQ2VkQnNOR0V4Nnd1UHlzYitpbkNUYnRHZzBpZVlwYm1ibG9ISzQ0d0kvRXg1cGFKK3hIa1lkZGM3QnZrRlNHNzJ1cWY2WVBBNEZPK1NuZ0Q0ZHFFejhXZVFQVVV1eXFaeEhFbTBPN3ZZclkxL0s3MUd3SEovcm5DNVU3bitxSzcvU3R2ZXY2RmpsQmFIR2ViRFZWSURSUy9JNmJWM002MmVrVVhMU21nNDlvOTNWdlU5OERJM3JLTHA2Tng4V0xZUkVjQVBMd3VDNStqdE1vQjJzTHZtUHA1WTVyZzBOOFZsTzhyQkpYVjl4OHRZeTVZaVc1aWgyV0FWTGhvSEpKYk1LNFA2YTgyZ3VSakJzV21hVTdUcHlXc3dUVjdycXdYWWlDd0J5aFhJbSs2empTK0o3OWlIT25aODZIbTBQMkVRSTZVZXpKU2RaZEthaGJXWTAyd0tGTXd1Qmt4VnhLMThTWkxxZzNOMmhSMFhDWXZqNmNOL2hCVUdTa2JSa2VaRXhaeE9peEdvWGlveGhlMEhKZUc3MnZnamFvVzBCMUZFSnpCd0ZUVWJ1M1Zpc0lWMHVJNEwxL0JrOVpJdDZUVGJHUDZ2eGJrZ25UNGppeU9lZ01pSm1NTGV2QVNUb0d1VmRzcTc3MHVWWUxzM0xRK21wdFkrL2tzM2VXS25WYmpaNWYwc01kQ2RrbmRYQU9nQkgyRy9TWlU0azJxRitlaGxtdzI3S3Z1WnVPM0dpeEZyS1BaUzhWbUtjSXZOSXZRRkxaeHVOVTNESklDcXBtYU80MENseWFmRlU3Nmhoa3k5ejQ3Wi9EdC9uMlBseUJ5OStnSTFZOGE1elYwR2ZvMXZiZXl1eUpXQ1lGSHRFMmNDUXgrQmNRNzRJNUpJclJldTYzWWFnZDMwU1B6cVNpYStCYWdhMGZTeXNSS3Ruc2lrYU5zeXdwaUwrckhKRUphUXBpSFhpdkN0Um9nWE83T3pib3ZDcS9BOFdDMUdMNnNnd1l2MkJzTmhUTlprUWVERkF0SWxpejFSMXJENGZYVm5Mdk1CaG9aWDRBVmh4YUZKZmNLUGU4Zk5lY2Z0b0lBRXk3YWFyVXNRS2tSck5qcVdmVENkcm1qazh4VmREUFhHTDhEOEpMUWlhek9IZ0pCTFhudCt3bVdQbHFzV0JWZk12MG9ONXpKOXovVVN6TnVKdmxxczNVV200ci9maDFhZ0ZPaTM3c0tJZkFlaS92MUpGdW5jL3F3T05RQlRuOFJma1h2Q3VoVGR1QTJmTm1uejFwKzNpV2pTSmRrMEZUV215cEE4bDlnTENrY1VGNkY5MWVxcnpUQ1p2b1dON2N2eTBPNnN4RVhHSnl2OGRsVUtHeWJwckRqRTE4YUVDTVFNVVdiVDJWRFF2a1pzb3pHYktBSGRHSFJmZDdMVndYanZPalhMdmRnTWV4L25UT0JUVGoyN0M5V0RMemI0NHBMNUIxMkpwMHVZRGtxZVlwR2d0U0wyS2k3SlpEVE5Mc3pQZlB6S0FpNHlkcUFDdXNNcHloQkxCaHdoQ2FiZ3N1dVU3b2NnYWJObUxWSjkwR2piWjFhSGpuZXo5QkNINHFSVFRScnIrVkc5ZzZqREwxak80MWtXT01ZV0pRR0VjZTg3UWYxaE9wU3VYdWVhNllIT3FMRHQvM3NSTWVwY0hQOUlENUlSQkJCZ24rUWhVUFFLVzR1V1B2b0d5WXNZb3FGUFJ0SzQyYlQ0dTRMUWNaVjF3Vk5VYTFYSERHZ0huaUt2VS9uY290Nld0UEtQUGF6b3UwOTdIbHFkWDA4ckhNaEpQbytDVW42VXdnajhvOXdTa2FuaTBDcFlZYXJCajBrYWYzeEJJRG5penlETWdXcCttZWsrQ2MzTWc5NGZFSC9XMjhPVktxWDdCdU1kaFFQaEJwRDVCTVdMMTJpZ0FFMi90bE41aDAzSTFXSDd0cUc1cjdDaGRIOTk3cWZxRjhxc1Y1MzdMRFRsM0lpU25lNlphMHA1S21wRjlOdWp6MFR0ZGQrTno5K3lyS2NzRXQ1ZkJzNE4zVmhtR0I3eCtiUjlBcVljQWJyYVFrLzRqTWt5bDNVMFFMcmdXTnR6Z0U0SmtleUhkaTZqMm1hOW1jZ0hpUjg1Rm9iWUw4SzgrOThHa0preTJ1Z3EyRGMvc1pMWlpLWEY2V0Q1aGxZTFJvRWg5RkhiYkVTOUtocE50elZnKzZ0Tk03VEpTZ1QzVXEvL1VsOUpvSjVqVFdGZEpLbDNCdmZlQWxHaG0rMFNGQnJGNFRxa2N6UVpubGhSSUxXaWtIK2Rpaml2NUgyWDE2UXoyL3NmOTBGNlU0aVdYcnFaTGUrcGxDV1lNN0J6OVFEMjNnbmZQNldndXFOOWYwOVU5ZGR4WDFPMUZ4UDlSVGJrTG5mS2E3MW56eUVveCtacE9CUzRydENZWW9kL3o4T3RLZmgyR1IrSFg0RFFudkJ6ZURncnpTMHNORFFienlkSmNLeDdMak95bm4zeXNqZW05RWhseFFka09SbzJvZDZPYUljZ2JmS0MxYURQWndFNTJvdkIvYjdsb240bUIvdHRCUnpRR1Rvaldxa280TUxzZzA0SlZCL1FsUVlvZ1ZMN3FpV2hPMjFvaUova0xQVmF4dmhNWWNhQ2FwdkRxQkNuVk1EVElDekc1K2QyU3lSVVBvd1BBN3lsc2tGYU85YTVkck8xUnRadFg0WG9MQ0Z3ZCtSeVB3dS8xcFZ6bXR5NWdEVklLMWJWZ0FEUFppYWx5enJLWHZKbmh2V2tFTTlnVkdIOGRpU1lydWpHNDkzcE8wQmtielYycTZ2RGtSSW5VVVZxY01MZW03K01nNTdmQlpIMHNVOWkraCs4MUVqMlRqREJ1aHJja3V2ZEkxellHc25uMGxvdGJXTnlSTm1zT0ovZ0h0MTBIbHo2TjdMeDdpUTZGUzlzd2RoS284MUVqWnE3bmQrK0ordGJtRGR6VGVHM0d3M3lvc25GcEorV0ZRd2tPaEFIczVXSmJIWmU3ZUhzZkR3bFdnWWpZVGFTOXg0Z3QrNURmcEpnckhjODNtdmpNdFEvdjR4SDg1Z2U0U2xvQ1RrSUl2WFVHb0pNTzd3ZmhRTlZMWVNMZE11NDRFYlNQV2liV2RHRTUvNjJEOEZDUVN1anpBcVNqeGlTK09JSFZ4V0k4SVl4VlBlQm5Fa01qWEpXZmY3RUg3UjNCLzExMmQ3RWwyMnNDbTNUWVdKQWFWbzF1cGc3eU9pQnRybk1HdS9JNlZnSWlVMkdvNy9lYXhKNGd5ZjRpQlU5L3JXakRURkxFVUdqbHloeVQ4T1UxMjd4WnFUUzYyMVRqT1NTYVd4UVhmT2dORWVpdzJtTG1zRmRNMGhrOUs0aTNuUHZxOVdJZ29BRjBRVGhWbm93U21RZGFBZFdDWDQ4djBjaHJmUzRMbS9CWm5kc2EveWl6QVNxUnhlc3VqcXJQU0MzblNtWFZNQzVBSkFidTJLY0tQNTFmVDdCc25yZzNOL3BRczRqcVE2emRUZ2syTXBVOXY4MEF3R01kUkZPcEFuN3pScmFTL0pnaVluNUJRSUdZMWhnMytYY0JvaWVaUVIyMXRsWmg0eWhKcHJiRHFEQkFkVTM2UUpvQnlURzFrUGJSQ0pWR1JlbklhWm8xY0NUT29BVGN5RldOUEw4dGZMdVgyYWp3OVJ3bS9jMFZPdm96cm1pN1BHTmVQSEhZV3ZReE9lb2xGS25sNTZzZEwvYk9oZzY0VzhNVTBHcExIWmJpbU9PNklqVThXcEEydExBcVNNOWQ1YU5vZENrS2VtUTFaWk9WTVQ2U3FuQjVKMzhTNFFJVjZXMTNqLzUxMGMrSkZmY1ZDMlBYU0pmL3A0czRDMW81am5tRXpsMlNwRVJXVjd2OTB0RktScTRuVG9WNUc4QVg2aFNDbzk1Q28va1dZZlhuamFrSDNiNGl0ZWwvcVlRYVVhdzlYN3ViRWRwTlhzNUJIa2IwUThtVlRYQmdKdnFOV1A3SEtNU0JqeVlGRFovNHFwTkpTcWtub1Z3TlpzUmwvQzdyM3RvcGdOY0JiUWdQSCtMRkMyeFdEdFhmQ1R4N05ySkdlME5iYiszdm9ndFJ6eXZ2dyswampFNUpRUHlzR3Ewb1JDNnVvdy9FUHZWSytKR0JEbXd6QkNSeUwvVlprMTN3VmNpN1A3QTRRNWZaSy91bEZtQjhZN09MSWd1MXFoNFdFRWh4aHpqVFBaV2tsSkl0aVo4QXlSNkdjbXpOT21jUjYwQ3JiNnkyQlBXTGxQcDF3YWtLejVOTXg5MEZIcndIdkZPOG1rNTVtN3cxWTFEakNpMTZqSkcvcWw2Z2VLM3p2QnRrVjJKOVVBNGJSMFRuUUV5OGYxN0o5N05rcWtzQ2hZSFhlSldQVm9iNVh0d2o2VUROL3FwR3Vxc3VjTDEyYlZwZU5wRHhFampKUGhTcytWREl2OEh3eWhQaUNHNEF6b2R6MUpHdDByMC96WVlIVjdoaFhOZ0l5LzVPbUMvZFRGS2ExYWlnVmpwUU15dlVZdjJyWXpGL1FwQWFmOTRXcHpTMzY1dTFkWHpKK2l2MnBBeVE4TG91WThSbFNQdlBCUlpCSml3aUwxVm1qcjZjMnVzNmtWeDJsSFNzTld6QzJMeFgxTXRoZFJnMXl0MFhMZXZUbE5aRW9HTkNCNUFxaEpOQTBLTzRxY1FBWFNkTnpndkJIaHBidmVITkhDcFNXUnVNYjc4ci9hcGgvUGt5ZlRxL3laZUo3MmtEZWxSdEIraUQrNW9Zc0ZxSzF4NjdHTkZLWWNBZmM5QzhBeGhJK1A0SGhPNFlTVzJvY0hDM1BOeHVqK09UOGQ2V3FTdDltUVVGb1FQR3oxcDh5MlhRWVlSVC9HVE5yYzRncHZEU2pMc1h3NU94WVcrZFl4MXNkTlhBS2RIaUk2SlJkWmtZVDRZT3RVRVhhd0ZzcU0wTVZmNHlpcFlDcHhNdlY1SVFXOUxublgxVFVCMWExVGFxM2IwcTY0dGNJR0c4TUcrNkRMblpZNzlwMEJVanVUTDV6S1hjWC9iU1ZBb04wWmtSUTBscWhyTEtmaTVDcmlGRHNMUlpuTWNrWjlKTnVMYnRiOTBhNWJwWVlta2dBUmp0ek5HSnIyRzR5ei9QaG13aFZJWUI5dGcrcGRxdmRVczEvUGlxTGdaVkN3eDVxZjEwdC9RT2plMDVLQXNJYWxBekRWd1ZlYldaZHJsb1Y2TjJ1dTEySU1Ud3NDYjZ1TXpZNDFuK1hBMmhuclB4Sm9kNG5OZXJiSHVlT0EzdHdwK2lsWHlLYVlIaUFzTjRITVlZdkVWeXRNZHNTQU5ranlBZVhYWjRkakt5K3grNXFBNlFYUWtYQ2dDMndFMnRLZGhaZWM2Y21IRy8wUVhrT0t3WXhNcWE2YlcvQWxkS1o4Z3JlYXgrdUxjWGExME42Y09kdld5WEI2K3I5ei9TTlNSTWlIQkJhRnU1UFQ5aFdGVUp6Wlh3MEpTeURBMHBjNDMrUklyY09KalVTbGhvMGd2Y3ZNNzAzZmJPSlB4bjlNRGVyUkZvaTk3bFFVYWJEQUtvOHJFWDF1TUN6TWVZMmlaN3dEem5nSHh0ZUZNck1mYW1jKzUwYnFkSUw5V3pTLzM4Z2Fob2NYOWdQYnZCWmVSZ0hsRnNxSzhOejFYM2lnWFdYRklxM3UyTDJsZ2xmTU9wTzhYSE1ucU4waTNkUmJvQnh3MThoUUlWSHh2N3RWYnRlcStpWDhjQUpNSDNOZTdoaC81QjF0YVE4K0tsK0hpQUNiTGJxTklldzg3VFhPdUtZZzFxMExVQXZmamtWS2dQZGJlc1JPM3daMngvQUNpVEkxVTVFMGRTYUlWSUNaL2tlZWM5QWIydXFMWGM2ajRYUmNoejJkVU5wVHUwOWxtVzZMalk2cm53Y2U0a0J3d3lUZVRlK3YyNU5Ud0NMRE5CMTJhekZCOHZVRzh6OWZobTdCbHRRSEU4N0I0azJWbE1xaFlPcWYxS3pTb1l1cGpsa05HckhzQnNUdW4xTGZkSVpMbTRONlQ2ZC9zUkZMSnhFUWFZZEs0Nng2ZVFieFdLYXhSV28ydXpWSXZKTjcwT2VJaWlReDUxOFo3QVYvWTY5cjFIdThsQ2Z6S2ZsS2FqeU41a1d3TTZFeVM2UmF5TW9CbU9CM2s1cEl0cjllN1U2SW1mN2EvbHowbXlaaVI2R050cWhHTTFjL3pETmhqQVJNS2xhMFRha2RtcVVFcWFUV3BuU0xyaWVMTkVmV0JDRFlubE41ZzZEUVRvdjlBNDRicHhvbnBNVHlXbjV2aTUrYUVSSzZIN0lCcStUdUdoWXVTOGVCRHVrbnhMNnRJcXNiOWY5RThOaWpNTGdvTG0wNkpOaFJRUi9CRGdjT2t5Y1hKNUE0aFdmcmNsaU5vNXZNbW9jNkZiblVYUm9FY3l5RXBscWpoNWo1L3dmUDZTSVBuOVJVRDUvVW80UXZYamsvS0wrUnhFWmxGTE5iS2JJRWxES2E2ZU1TbEI5V2EyZlVtVk5BWTd4cWJ2SnVOR2RIVThwOUc1dGROcHpucW9Qc21ld2RaVlE2Q3MzTFdpVEN4dis2L04rUjdmdnJYSDN0cUpKbkFVNlRIWUxOM3dQcURkZmZLS05WZkNOKzYyQzlZN1ozeG4vWnVqQ1NMd21vZUZwb3IvenMwak5IR0E3RWU0ZHh0cFlNSGJuNG1ERDJLM1BnWWtvYnB2LzNWTzNTTll4Vmp2K2d0L1BJalc3Vjk3V3BFeGY2cFJxWDIwMlJuUTUraUxUeHo4MU44ODFDMmFHbzdqaENvdTRCaHliNG9RbDVtd09QaFp5Yjl6Y3BjTnUxbS9OTlhTejQzdGxlUy9kV3BtUThzU3daRm9QN1dWZWFmTmtqY0pKbzJUQldHZ3p6Uk15OFRHMGMrdkVKbU5KN2pLK2FtSmVkWGUrTytNL3dBMVNva1h3U0tMY1MzOFZ4R1hTb2k1MmdkZGhvb1Bid0xCNkJQQndmVlhMVDRKK2FWYXVabUpOSkNKa0ZidDIwMUJaRkZLUVJEV1B4cXZhSnBMKy90UEY0WTQyS0RLcmVlTHljcjhTQWlpb0FXUFp5OFZGQ2gydHNQV2pXRzlvVklHMmcrblcvVUZHY3JocGt3RFd3b1AvekRqRGE4Sm8wNnJJNVVNTEMwdkpzTjkrTTlZdkxOb2lObG5SK1Q1cCtoYi9DeTk1R05tY2RwamhNRVNGYTlGZXJRTzBXMHlsMGUwaXRmb0c4RlJOeGRacDNGV3JZakRZbnVnbjhZc1UyUVEraFdaNUw0V0RxbVRzdzNRWjNwZTc0VEhEQVRXQzJyUmluUlJnMjdXSHIxK3pTaytOejdiS0Y2bTF1c3lIN25pdnZmakFtcEtDK2gydmhzZjZCTmNRNDhvQVRoTVQwOWk0ZE9lMGNtcEJIWWpCWG5zOFB6VmkvOHdGaGIyQ3NxU2ZuYXdmQUdNRnJzOFRrS3N1cngza283akZ4YnBuOGRJMmQ5dkYzNnZHc1RmRmZEM2FJVHI3VU14SE4wb1ViTm5ldFhaWVBjU0RtSGdCZGNoMzMyU3RZem92bmRXU0VXcTRzYVBTNHNZYlJPU04yeTNwY3lzRVIwbWpQMHdCS0RLcE1KUldQZUpxWTladFdOQmpwS3NRemNoaElzL2w5ZjNaSTVCZ1BwY1pKNXk1cCtnZjZOS3pRa3dPSG9MeVA4dUNOVlZOWjlrOVI2MzhCUnN5cThlbk00UW1HUm4wNmV0K0tYb0RxekhpZkpqR3hhKzFmSW9ZOUpYUU5JQnJyUXU5OTNZV1dnYU8wbDI0UVoyNW1XUm5UUUladHZ4Tit3Ni9EWXdyMStkN0FPUkdRZTlxWGFVMmJzdTFSYVNndWJPZjJnS0NUN0JzM25xK2RiSnV1NXVNWlRMVVQ4QWlVRHN6REFBUHFYclo3RkU2VG40YnJlamZNaWZVczNjaFU3RUQ2ZlY5S3JlNHZmTG1DVXFRaERNOWdxcHpvcXBadVZVeGlucXhnd0c2Nm9NVE11Rk9rQXlua0lHaEEvN3pLYVQ5dlRhQlM4cUxUR29mR1RXbGo3cTRLOVZ6YXRhdmNTTGxWeWF3U2xIL0toOXRHU01xQlluZCtNNTJvbkdwTW4rcFRpODFWUEo4eGZEc3VpcDhVMkZEWlFhUEdERk1BanZtbTNEUGNkV3BEMzJZaWM2ajlzM3ErZmZsT1JCTUt5c3NaNHF5Zm1WZG1jWUsrUHFYQjBBTWRaVCt1NTZLa1RuVDYycVJjQS9LUmFJMVI5MTJzY2JkN1M3anBJSXRJeVhZNThnMEFteDFaY3J3d2U5ZnorSUpVcGoxeGZXRjFjaHYzUG9ibUN6b05ubWRRWUxVcTNkVVN5NnRBM3J2dzZ5T1U1SjN6dllDWDBaVjhtV0tFOWJlcGNDRENFdzlDeEQwK3NObkJJQy9aQW9BWW9YQ3ljZSttR0NoZkdWMnlBT0NjTkZESzc5Tzk0Smpqa3N3TDRVbDlJYUNUQTFSdHFEVVMzU1JxV0NlaG5YMDY2VW9kVFlaajRWU2ZMM2FQdzVsZklwZ1Z2a2lQUThrRHdRM1ZOR0FZWE42Z0crMnRWOXZUUjRjQzM5bFZqeFhKODMrZU0vUkZCOHlhUWduTUR4OWFjbUMySEJrZ21haDRDSlNDamxyNFA5bm9hSkY3dUw5ZFF2QWpQc1pZSzhycHZhakFDVkhjUWZvS3BRNmFETjA3bjVCbEFSM0YwMGlPU0ZuZ2l5R0ZycVpBUE5OenZjTitCZW01NzRKdGxTRFczVjhCbVcrZ1BlR0gwNFpMSCtxemVrc3kvVE9SM0ZLUXhFT1JOZWZZWjhsMk5PL1hmNWM3Wk93azhNWmRoZjJwZ3dYZ3U3OUoraFpqWlFOSXM2a1ZQQ0h6OWJvVi9ZQ1RjL0tBZ1FDOUZtOVRrVWVCclFqUVd4eEZsUW04T2w4M2lYcTlQOEZUNUFIMTJ4SXREdlpVNkNBRHNDa0o3OFF4QXZQZ1FJNUczN280VGJWajlIWTErU0tucVZzV0hFM0JFUXVMSnd1a01ONmIxYXhXbG9EcUJ0QUZPL0xiK01td1pKMm9OZG8xZ3VRb296cTVFdUJid0Vka24zMzdqd1l4Q3A5YXJqRnZLMXpIelNQbXBsRUZKUHJJOG9icVRYckZ5a3dsMlJjYVJVNkVWbUJGVHYrdHBhUlpCQkpZV2FsRUFVV1N4ZzZ3bnlMVHRFS0kxZ0k3ellITmkrV3NKUU5yd0pTU3pnNDBmRUl6Y0pkNFEvd2pjc2hTeTNKSnRLdzhJcUJXY0dGQWV0aVBQUCtIYXArRG54VjY3ZzRZL0k1OGQ3TzQ3Zlh5Rk01dm1mQy80bEVZbjg2N3FHMzM5L244Rlg4M2plSEZkYnQzVVBSMXkyc1FXRVdvQ3F4dlBRNW9kZWZyTmk1RzVBdkxJL2Z6am9PdGhvbzNtbitEOCtjQis4RXV3UG1aTUQwZjZSQTBSOFBOd0dSV1U2ZWF6dVVUTUtCNE5veUh1YWt4Y1kvZmplRXNsNjlVKzlhRm5lOHlQblp5OUE1azdzVnJjMkE0clUvRE9hemV1YURPci9Ud3lYSEpRTzJWUjFVaTJ2cm9VeFhWTER3L2ZsUjdhVlhOaE9aUDduSHJTVmN5UmdaMks1WkcwbjJ0WHhyMSsva2liNTNWZFBrQVc5UTI3ZldQaWJtUTZwVjJIZ25wNk8zNStFcTlaZk9LZ3VVeDJmMElVSmFzdm9yWU5QcEtLa3FNakxVKy9yaW91bHNRaEVjcjNFeWQ5N05DSS9TMUhJQUhjNThIeitpR0gxZExrRDUvU3NNNmNLRjhBOTVEWnk1UGVaeWsvTlhzYTRpRXB5aWtzRS82NUxFNGk5MzRwU1hsMTIwUnpScWRuSXNCdy9hYVAwRzJVWVZtQUdJaFdaZFMxZjFnRHlnY3RSc0p4bEhjWFYzN1JqMjd5MEVnbkkxdmc2TExZdkJDMitKL0NCRHBrM1QyZWk0QXdpcitKelN0OVJoUkNUcTc1ek5BMEZnVTl0MzdEck14NldXUmZxN1R1ZjRPeEE5bi9QUVlzVjREL0FoZjVsMENTKzJlemRyTnVGbERRSzZqSFJUMGlkL092Sk9kVFR3MENadWdSdmpLakFwbURYVVdJRjhobXZPVzArV3dNQzB6c3Juem5nY1Q3eE1xdzlZMFFZVmt6UlVtMSsyRlZDaFpvYjRkbEtPUlQ1THoxVTl5VHArZGhlNkxyL09TNjFRNTVVM1g5RE52RUllaC9yNmtjMFcrRkR0MnNvZkRFU0pUY2VLODlXYXBCNXFjNVIvcGZEVUI4RWF0OE9ZUU9NWkJBWldER294VUwweHJ3OFc1N3dxOEZXc3pUSmp1TzRKZUphemVhTktrWVhlYTdsQjFadUZQK25Bem1PU0pPL3BlbHc5M2VRUGpPZktUYzk3TVN4dHZNN1Q0WHFhTG5CT0lWa2dFbWFVdk5lMkc1NWFWSmViUnFmV1l4UmpiSUY0MTVLWjhuK0pmeEVsT3ZTdWRyeWlWcU15VzNNR3cyVTBBY3FWOFQrcUEvc3VWVmxyb2Zsb092YWluQmV6eWpCdEhWazVyTGlIVlVna3pjRWNiQzl1RE5sbzgyZmhGcTdPNnJUREpMVWEzeE9mYXNDS21hS0l1R3k4TXdJd2Q5Mm5MVmhvaXBPcVNnK0JVYlRWVzM0V1V1blFhcFkzS2tSSDZkNUdEdzFPbEg0ektsNU9DK1h5Y0EzdmVPeTFRcytlNkpuTEtWV1FBUWFDQVZXUVovRko0bWwvOWhDa1BGV3ZmYjdmVGZBYUF2NEwxZ2RocFVmb09mZDJHVHZLb1hsVVBmcWVsY1Yrdm4yZVpBd0xWdEtIVHNsN1B3eDdqd0Q0dE93dWVDUktVcFd0S3F6aFRFY2VUSTJuQmhMb0Q4bUR5MVBaNVJYakgvT0Z0ZDNEK1BDb3h2NC80U2wwL01PRHZRTWZtK3p5RDlQL2ZTZytzVHFWRGdibkdaUTFJTHA4dmhPbWtidFNRa3BOajlZUDNVa0JVa2htRlFNSkIzTlZTU3hQM2lUUWJYSmRYYzJMZGNZcDU3QmNkSTB2V2pRaW10clUxUFBTb0hRL0NRN0ptU09lQmlDVVFhM2kxQm1DZW5QVDRxQS9mSDlYRXYrR3hobS9PbjNPUjlaM2pwelU1T2VBV1o1aVdNbDU3QjJHOHEyMWpickZzUzNKRjdwVC9LTlhLTkUzSFlwNTRUZXhMeFV0R0NzUktxNjhKcW1ZSVVLK2VJdUU4eWtMNEoya0xVNnVTN0JUV2xUS01IU3lqZ0tIR01MWTgycnhSTEM5aGtnMjBzMHNWWWJkM2hJWHI3bzhQVDAvRmIxOU9KblJMUGdmOTBQRUJpZEl1UEthMmh4RU8wczV5ZFB6ZDhhOVB4aWk0MFNJWG1zeXJiTEwyS0Jjc05qUi9XZWFCR1BiSkdMSGszNmoybTFUUTVUMWNkSXNNM2oyR250TGtTYTAyVFVBSnhEOW1FTGVDSUlDR2NuMEhDNUt0L1pjREtDcUlBVG9oYUFnZlJXNkNxbmRCdUFlWmZ5U3JGRjcwMjBrNWNjL3d5Yitjd2hTekhMaFEySWtwWFRkNmNpK2hocUFVcUdMU1ZrNkZXU3RpcVdXTFMxK2l3SzcxY0pqM2xXdG5QcTZmL0luZ2xHM3VhNm11eUo0RWw4T2JnbjNzSWVkRkRINko2WTNFOXl4dFBRTG5QeXp4ejZLTG1hRmZ5TnY5WmJrWnh0Mm51blZvV01kTy9SNmNwM2RSeXdjUWJFZ3R0MU02NzNrOWp3WVg2MGVSVFQxekNhMXplMVBLd1FGQVJzN1NiTXh5bTd6bDF4MU85K2I3T3RkaE0vMlQ3aWMvekdRVnpNdzQ1RzhTaThtSUdpQUtOQ2I5L3R1TzNZeG4rQkRvN053aUpHWkN1SFZoRmc1dlYrOW9IQ1BiRHByMzNmMkhINHZVZ21JUWxaU2wyRnB1aS9KWFRPbmhPL05BVEhvbVdTa0JYNEJMcVg5OGc2b1FtcFpHbEpXazh5aldZbVJSdGZqSXZ4bjVJTDRtc0NWZHpQd29OUjNkTlQ0dnVLaGxSejl1ZmFVc1lyWkVVM3FvUVowSFFhMjB2SHQwT2hwSnZpdHJCcjFuL1MvOVU5Mld4c1lkVVlCL2lUeXJIb1kwNTY4R1plbWpwVjdHWnM3dUZ6SUJaNC9DeHZ2RHlUMEhWV3N2aGZ5SmhLaGdOT2VDdDZVMG56bVVjclB6VFVhSkNhdm9WelJkbnFxY2tQOU52UjNLUG5lWmdQMmNYcXRGZmVXbDgzdXhNVjh3aEV0MHl5S0t0OGhmMmdrU3NZY2NoL2ZraFBYL05qalQvUjF6bEhSVHdmUlVrRS9Vby9BRUtPRUNKMXIzMmVLZ0RESnpJODBzZ2lsMmNvcTV5a0JCdWliUE5ZMi96cDNsTTlYM3dqNzU4ZDJyZ3BDRWFodlE1eU5sTmxmSlBieTFDeHlaQjRLbzZXZzk3UDRYUTMxTHRSQ0plbHhDUWZ0dDd3NlcwQWJQcFBoWk9FaDRkSUZ4ZVpydC9FWU5zdjFMQ1hLZHFaNTIveC9TclIrT0dMQzlnRDdlb1prYXFkNFpHdFQrYlpXZ3l4OWNFaGVvbEF6b3pYS1JNaWFnc2lMaG9OTjRzY1RtcERaTUhZbm1sYWJLWjZFUTJVTUl3T3ErditKeWI0OEY0clpmZWVKZWUvWURsN0xmT241MHhteG1LM0lBVW14WkhIa3NHR3ozc1JjV0NwNjV1c2IvR3ZScEpRNFpnTEducFJlOXVsNlh2UWN2cjR0VHpTN2xOVG1JRzkxdnhIT05LeERZYTRpMWx6M2h4NmlvdzlUeHUyS1VFcFNwN05OTDk4dmk1TjNlKzJuL2w5dnRXZnc2RXN2dDkxRCtIUnRqK0h0QmZ3NkV3NVdXbG9LRmd1SzdPVEdpOXBDbTE2dkc2UTJ0NUFDZXFZdEhzbjgwSjBZY2hvUWdlSytXbEtINUNqMkVJZzhOdXZqZmR4T1FxNVlGcHhQZk5VaGMxM05NT2NuWmtoSkx5alhaL2RuWnFzOVJJbXhxRnF4dVVBT2JyUFJKV0FaYUVBVHI2ZjhXQmVJemh4MlRvNkxxUW5wWWRhUzZMVS84VVFQRysyUGxVd2pZRjgwVTNvcEJiU25rNWJwaVVLUldZWjVheUJhYkRPc0w1dDFOc2tMb0w4NkM3NHN5d1FDeHVwNEY5RFpITE5ld0ZzdmFrV3h1S0hiQmJFVGQxY2dnTEV3R3I1dmFhdlVaV1hOcEVjT1JjN3B1UG9CYUx5Rm9hSitXbUJERHpraDVrd200WjVmT3FpUWJ0TDI3bHNBTTYzQ216amY4UlJOM2hEZ0drTDRkMU9EZnBmbFhvQWh1VitZWVE3VlMvQkwzRHFabFRHVGdHRFRTcDF1aStSNkZPaXdZczFvUW96RDcwZk1VcG5jcUZrS0g1ay9MaEtqNFA0NjN2NmhxN0lncUlYR2F2bG94S3BpdTBwVFFTL1E1bURJZW1ZTWNZUGRnSGlIbjZSYkFTVFp3elRnM0NnL0tkcnZqQ1JCYnRjdThuM2NVREQvTHAycFlvYzYyazhQcDFFdk5hNDFybHhYWmRXRGR3Y3lqblhodENqSE1UcFhNV2dvTHhaNUhsYU43Ym5IRGp3Ty9hc0V4NU8vclorUE9Lc2F4bFRxWFl1ZWNQdWZZb1Y4M25sb3g4UEpQSlBkVVlvYkdGa0s2cHZnbm1NMFd0Y1FESStNWkRxRlZQQVJyUmluUnR4eXZ0N0ZZVDN0YXNhTGVqS0FLTkNoS2pRTXI4VlRqNDFCUk5ubjRLR2hJUGFseG93NU9VMDJ2alN3bEFISHpWbGxGWm8yTlpRdVNicktyc29yTXpKZnVaVVk0UXNCcm5TN0RUMnp0bHBzMUtxMTJnTGZuaWZITGl2akRGcEIycmRRODl4MWRxWXdLWERYUzkxSFRYNzNpTlhac0IvenZUMnh3dXUyRGM4MUFvVGdzSCtEK3h4bHY0cFJiVFh0WEpjQ1AvVHhpdjd3WXR2cXlDNS96ZFQ5azZ3bFJoSk51ci94U3dzUDhKa1NvbDl5ZE5pSlJwOE9iMVNFNUxRV0FwVTFXek5QNmRTTldBK1MrT01sUW4yTzk2b3VPODN6anhSK1JoMEpGTkFNUXg4NnIrNkljTmRyOWJSeHg0R0sxRkdZMTJRdEFTOWlWQkg2dkd5RWlaK3NqSG5qUzV2dmZWd29zWG45b0svZlR4djcvVC96WGR3SnBsYTB0a2lCZVduaVl1MENhSC90eHVIZWxTL0FVMTZVcUVmQ2pJS29tUmNXOFIzcVBlZzRnZmpyNGFTVGRIeEQ4cXZyZnNqbzZoTllqZHRaWHM5UkRwb0NBc1dXNGNDN1NBUWJWcHMrdXB3dnV1R0V2dDFocW1oOHRaS3hoUGdHcmZLQ0IvaEZvbnQybGJQWkUxMEdrOE9PQUtIMjUwTWdrTW43T1pLTU43bEt3bTJudENuMUo2VGRqQzlXZjc1QitxMWxwaERjMW50NjMwU1NlUWZTM3IvVVZ3VXV2eUtSQ0lVZzM4MmJhK2V2TVI5VFQxdjE2NG5UQlE1aFBDaysvWFhlVEc1UDVlVDNkMElMTnBMNzBMNktWeVQxajhhRnpQWVdSTVRyajdZZHBoaGhteU0yTS9ZRUxybnNRNGVSVTA3RkZvT3B3OFFRdE9kWnh6dytBTHYyN3FoK3pNZkxBeFpyZENab3pqa0VOaS9zQ0xqTDlESUJ0cVhwVk1Dbno4M3NBa29GOXNtVUlBVWpzUkZKUklpN09wZjNDQVdKR1Jqamh0aVhrdDd3M2xUZ015RVpPMVFOME5USXdndzU0MEI2T0R0NXluZDdrVHR4dDRRQ1hES0JWNENlb0JZYS9GSjlUejNPQWNxSFJHOTNSZ29GSkk5cmVZWHJjdUZ6MVQweFZLRmRhdWJ5ei9iRVJ6QW5WOXBOU2FNZm5TSnVRSTBXMlVOUWFuOEVqbEtmTVFRTVFOOCt0ajlRa1lRcUdLUlQ2NlZ3L1Y0WTcwTk8zMngrTUdRbkZnTnZJdGhZc3RVSHJYTWhqejZlNGZ1bjRKMWJ0L09pbUZpZ05XbzRuQzU1M0NtQmtRZ1VHMFVuZUFkSkRrWi9WNEcyc2YrL1o1dFpodEl0ZjB2ckhQcHhGcjY4M1B1RFgzVE1ycW8wUG1tQXFZYmg3OXU0SmtmMDNGRzBLSG9PdFI5TU9iS2h6Ujd3eEUxRmh6eEs4OEh5VTNuS1Y3clJnUTdoVnJja3lKNFZSdzM1M1NtYkJvWGZhWnFKVHFrQkROcEZUNjZrL3JtQk43YVlPZmIzZkpaaEFVMkJRbmNTbkFRZUFHajVoT1JOWWFMYXUvT0xhVWlOemNBcVBWbXhRbVkzQ1pudUNwM2p6L0VRTDVVQ0NGNll2NlVKTTNKdExIdGx1MGl6bVRzcDkvVnF2N0VXenp4RHppNHdpa0EyRDNpM29oSUVMZDdOSEdiRk9lQXV6Y05kMWRJUHlkem5HdUxMSUt4ZllzR29zWS95T1hONXpqT3FjSzFBaUcxSXBFV1A4Vi9KRnZQSExLWlJibGI5Z3FORzZrRXphZWEwaUFETmJab3JtQjMzcFBwdTI0c0Y5aFlxR3oyMUZLYjR1RWVTWjN6emJwek9QVmJTSHlpUFlnZzRjWjRZbXRWeUxtQjZBYmhGS0lMV2lRYUVibklvZ0dnVExDcUZCdG1IdUJOakw3aGVMZFpTeEEvelI3YThRdDZYUVdLaXlxUko5VUYrWWpHSE8rcXVuMzdIbER5VUZaUnJack9pTnZHQmdnam5OelJQRjkyUWx5SXlsU0JlVjNtU2I0dU5qREtJZFFVRzMwWmFHTDVvelI5bXlMTkxkRGRiQjFCSURaekxLOENTaU1iUFhqNDJzMDJoQUJmNmxhOE4zWkJxTG5UbnJCZGJXaFI5RTVGZnIrVzhWSk1oR0g3Q21oR0tLbUsyYzAyS1RtM3lVamQxRW50SzV2M290OW5NQ05hc000YUhxeGRWMnJkelMvUGZoUC9ZUjk1VC9Qb296bGEvclE2UWFwMjhYL0RpbkhWL0pmU2xDaTQ5eTV5WFB3d09iYmRqY3ZObEVQMGM5VjZmaE43M1FWYXFOZGZwaHd2aDNJb0hzVlZWWkpjRjRRclpscmRjWEVCd3Qxc1c3bDJaMmQrUmV0UzZEenJzenpHcmtncDErV2YvY3FuUzhsdWhMR3g4MVlXdFVtWnZFU3ZUUUkvSWpVUVo1TGh6MFBleEU1aXFxRHRNdjBrRkFPd1lwTEpHUFhRVWN4WTRsdC9iWUVpbWpsTzRYN0xsYUhtbnFIczQyY2VDT1U1RE5TRTljME82S2dRRERBUWRVek9Oei9lT3FZM05sODNVZXF2bTNDL2tmNk81UjZJQlZmcTk0TWF5MFJuTHplSEFnMnF4ZGxsTnZoK3VIcmwvWStPV1hKZi9aTEdBR2k2dlp0b1RoaEd1SEtSTjA4bkNSWUxPK25STExzdmt6ZmpQTFo5TnVwMVI0MlFYVTZsZU5CdGJzR3BYdWJYL3lHN1VTb25NQ2NoSEprRUVKRmdGM3BCbHIrQ3RzOVlXQlRrZmZQaUVieWxzYUZFcTVXZUl1c1RaK3NFZzVSKzdPaHEwUHE3aWZyMlY1LzU2dkZ3aFZVR2cxTGJERDZ4KzlXMmNIWkllek02d0djVzdBMHFIbXAyaHllUEYxaHV1M1gwQXM5bWY4U3dlYW5IUkUxUnhWTjcvUzVRaFVUOWtPQ3pUd1R0aXpUTUVYUWl2TWd1RHRxKzBoZ1p5a2NiZm9Jd0VkdEpaaFkzN2FXSHpXVUtqYmlXZXYwOHA2WmlwcWYwcHloUjByUzNkeUlmNHVPb0F5TFdDaytMdVlvMDYxTVNUMTg3NDJwdFBtVkxSSkJ3aW1KMXJTbzdDM0l6bS94WHAxQ0I0aWhVdjhEbnRORjNxTVA2aStOZkV0K0cxQ0FIOFNvaE5OV0RKSVZmQVhLK2N3QTNHR29NSG1BTDNOaG5NaVdiVmdNM3g4LzBmMkk5YUtpaVZMVmZwb0tiK3hnMzJ0bm1IWUxWdldjRXJGSDNrWHNnSnRLVU1QZUpRV0Y3RHJVR3U2TUhCTWN4akdkeXROdWl6QzJqc1l5WktGWk10NTF3YkhCK1ZuMnMrTURxOFZyWjVndGF6RmxyN1gxYXhoa2I5SUJsM1RUY1dTTUpzVXV6c2RVNWNZcWF0RXZJREpvSFkzZzlOM0dlZkJ1ZGtCams0SG5TYldmY3hjT1VBcklKS2hCOUxSK3hBeERkQUdtZ2p4bTFmMkVJRm9acjVETHAzWS95MDdZZE5PWCtnWXJ2U3NCTU04alU5Z3VicDZwenhXRm10d1F1M2ZSRFNkTTBaU01tQy9nUDJ4bVBhclZMUFpRR3hKTm0yWkFUVmd1aDY3VW5DeEZSY1JlVjdtTmNrOHFxaW5QREVFcmFFcnR2V3pta0ZLcjUwdTJ3bmJHZXdsbHdoVlY5NGZvcVg4ektzNW9HZTdXRGhERmF3Ym5xdERkc0NiVGk3bk1mVVFDTXpzN0VhaGdadk0wUHZ2WWFMTHhTdjU2V2JJZTZyR3VoWmZPdWR4V3ZVby9RVFgya2grV1BWNDJsR0NJSWNSaStlS0JYMkI1OXdORkRxTlVrWFZpUnhKaFZjVzJCZWpwbldPWUU5dUZaR0JuOXpLQzdldW9pVDJpeGh2Q2I5QmJna2hmVldVWXR6TngvSEpLaVVuWlhRbHpOUmNFWFBlSUFnck9CQWljcHA3KzJkRlFHWEs5YkNVS3pzZWRhSkJpTGllT1lOUTJ3QUlmT01UcWFmZ2lnNlN6WFVhdTNKRE5hcTFXQ2NiMk5HUXpDaFZ5RVlJUW1aeXBOd0c4RllZVEUwbHFmNDE0ODUrTkNQOTBXc2hTN3Rra3kxVkV5TWNaQ3lUcm5oQXh1WkdHVWNmR3oyM24rN1ZuQXMzT3c0VXA1KzQ4ZHJTSC9XNmdsQzMwYkV5Z3RJdUt3WkY5KzRSb2VGdHpWU3U3aUYxTzhKbElYSUZQT2NubEpFNW56eU4yWCtxSHZXdks1dzhZY3BEVUVFTjJzVFk5U0F3cXovTnB3VFp3QUVBMzE3TnRJM2ZWdk4xdTQrWjczQXNFQ005REdWQ05DdVFnRFY3Q3NNQ214NWJjZWRyYWJldm84Z1RDZHY1dEk1bmZvWEUwWGJIdzMyT2dLaS80MElrOTlGZFRKcks1T0EvUjdWRWFwYnNuTEdoTTg1M2RySFBMdkZRQnVYWU45TmNKNDRPZUpvdWZqbGgzaUJ2ZXJHc1RIQ292SFp1UStJOUlzdTNZTGRYZzB0Y2lUSWc0aGdWN0F2ZDlwMkFQUklpYVJXaU85QlJ2NVI3eS8wTS9EK3k4N08rY2ZCV3UzdzlabGMyNWE2ZmN5clRJdUdlY1NvMDdtbk91bGs1WGE4bnI5QlZ3ZVlkeU5pcDMweXN6VmdFeURXTkh0ajlvL1NaeU4vMTFOTXpiWlZWejVBem5LTHJyYXJoMWVSNGxvWHIxWUp3cGRrb3pNanRXUFB2RWd4b011UW5wVUIxZHlsK0NnMHN5bFd4NFdaWDduODd3RDBQd3A4VnJTSWV4cENYOThpek5adFNpSlNZdXRheGQ4NEVQelhzUlhtU2U1YW83ejFPRWliTEVpM09aYUZrTDBhYzdoWGtMNjBiQ2UzZlRvWTdkQVZSczhhR2tWZUxUMnBsVnJ1SHJwcStpamU1Ylc0RWVpdjVxQTc3eGhVSnFxMFp1d0tFdGtNekZ5MEtnS1cwTU5ZdG1EVjBvdWhMNUdWK1RLaEl5S0RlaWV4TFBPNW5oaHJhMXB3NU54T2s5eWVFT1Zwckw1NW1YWGVoUis3c0EwczNHVnE3RXUvMUx2YVlMem0vZE9lTFlIZGFDa0VSd3pxbVFPWm5zWEhpMWhDeWt0eWI5RkpLQ0tLRU9vZjhDRWlQdXRiMW1lbUU5T2E0Y2dSR3huQXVmUTRsQkpmNDdPcjZnRXIvWi9pQmtEQ3ZQUUhUS2pNbWtkRnhSY0g0a0ZOQVRYV0xVemplYzNtVW5IQkJNdEIzWlVnRHdWb0N2YkZBTDJnQ21Ib1BTZlJTZHRUMkJ4d0ZOSW50SUxRVXhHN09ORmtQM3NkcVl1dURjOU53WVgzWmdNcDB6dkxqR2trK0tRbTdoZWw3K294enB3V1QxMnFHcEMxT1l2TDdwM2tMeU51NWV3UjNDaUdnWDE4QmxBNmd4RE9EckhIeS92ay9mMXVrb2dwbHlTbDJyZFlacEFsL0R1L1dRUTlzOUNPVkRkUUtQT1FDQ1JHZ1VaemJEVFBhTWJCaHhMcEw2UDZWenA3dFk4NStzdHQyemJhUXVXRE0xNStYU2h3ZThYKzZ5TXFvUlFTYWZyRTFQa29nbU5tZWtKMlFEaTBlcmU4bFlBaDVFZmRjbnFOVkF6a1Uxenp0R3JRa0tjMXRRZUdrQXhjaGwydGk5VFB1TFMxc3Vlc0paYnlDRXVka29SV1NxUWloNk1XbWNGRGttU1lJQVNESFNiSFB1dDEvZnFiUi9hRVBRRDRYSW5CNVk3L0JtM2xxZ1BqNWJKNCs5NC9ZK21nOE1FcWRieHdYVFJjRFJHU0tTUk1jUU5NWDlPQXdlSlF5WU5RZnJ1Um1ocmsweFdCckRGanhYbDFZcXlLcXNyNnVQZVZtaVlVTzZyMlNJa2xXejFOemZab1dxdDk2N2pDang0K3JIaVIzQURBeXlRK1M0R3piWUZsRURKamtMTFB4b2o3Ukk2S2VMeHo5cEsrZTNhZWtaRXB5bEJGK2RMSW9zdkxIcjg5eWwrSXpUQXloYjhxRCtXQmpCUzU5SVRLS20wbWtyaCsvTlNENVhwdzFNbjQ3OVBaSnZJVTRHekVuWFRLQWQ1T0hKdFBKTE1rVFZPOEtZci91Q2RDaWZCTEhUTFIzT2NBVU1TYkljd085T2oyQXpyU3g0ODNKU3k1QkJ2L0diRWFmamJEbGx5R2hIQVphQ21GM2c5d3FlMEplOHVEcm5FbW9DSmN2VWUvaVgzekx5czRvN0Z6ZGh2aENxYklFbFdEVnhuUU80WXdEdm9hKy8ySEhRU2Fla2RSZUVnUDI2TWRCNHZSKy84Zk1Ya093emQxNWZwbGw1T2JVVVlTbDZaNlRPOGg4Y2p2c3dHNTdXWVJlQ3ZJWTEzU1V5b3VsL1dOVjV3SzNVNlo1S3pmM2ZsNEtNdFphOVM3c0ZMcmQwRGtzQjNUalg5b3Qwd0ZyMUVDbzB6VktiQ1llS2lRTUloRDlXbVp0RjJyc0sxa3ZzaVhiUnk5dWduSVJCSW51YkdlRWsrekZ6QUFYUWV6SGZWejVqRVFrem14L0o5QmJqS2xvMWZWMmZQc2JSeWg3RGk3MU9nVlp4Y2gwa3l0SGtQR2p4ckFyZTVLMjlBL1B3dkI4ODhDTzVkVDBpUmxUdXg2cFYzY2JLeitBTFYxSEZKMW1Nd3RmZEROd3QvWUJWNStkMnowcFV2RjlkenhzeUU0UXZKMlhRVVN4Q2F3dW00UWNrWUxBQlV1VGxGZGJjbU5COFZMMVFXQkJoSit0Y0o5L1AzUG5UdmJKbHU3Y0oyWkdCYitwUHI2N0pxQ3RaaWsxaVhvQ0lsOVBVUmpjRVpnMjVzcmJVaFoxZzBrd2hIUTBYb1pVZ2dZbXBlZFhxcDV3N24yL0Q4OVBhUHhmaCtkWk1oYU1BbHQxTFE0bWZjN0g2RnJ3c0x1dlNqajFHNU9vM2RidjAyNjFKOWpOWC9qVEx5NyswOGV6WGFUMUVrcWVwV3c5ZW9XbS9XSENlOWxOZk9xd0pKMDNnTk9LVytKcVRvSnhyR09jdVJrNEtzT0x3VUZwZERTVlQxdjV1NERGN3VjUHg0WnFrZWdDdnhRMWJpNE1FeFdYTGdMTWxOd0dlQTZCTHNtejhVeE5jUGRGa1ZROUhaUVFvQXN6TkV5RmRrZC9qRnFCbXNjTVNOZkdDeGkrZVZoZ1FVRUVpV0YxL1UvRVdRdmpPcjB5UitaZjl1SlNXdDNEZ2daUTY5TFNVdFNHdDV0disvS3IxVW8rWEFabTdnOHNiNGxUQlNSV1ZxZXN2SkJoMmY4YVRPa1gvTlNyT1FNdjg5M1o1MHhGaXl1M2xQellqdDZpQnNSaDZUSXViQTdGYjgvQjFwQ1lEQzJtRUlhZVJRdHN2bk81K1FQRlZFQ1ltdDF4Y21FcVdleUg1VE9FY0VoYXNycFZQclJkSWhsdW1kZzVJTzNjWXFYY3YwSkZPUmc1ZFBnak5kZmQrd1BEMm4ySzRhVjFRT0NwQ0VvUnlrZTFGbDRpL2graHhsNzIzcGh3VmQ1OTdsRXlvYWluTkZzbDhGZ3BpcTZUYjdzUzBPT1pnN3NRUVZESFB0aTlGZHp6OEduNU9qdnhGVjMvU3ZhTlRBemhSbjdYWUJ4TU9DbWVzaDZOUE1rOFlPTXFIYlRDbUZxV1dLSTk5RXNTcEY4a3UwTDRMNU5HM0wyRXBoVUVaOEltYnVtVWdadmJRRzltN1haVmQvejExcVAzVmxTOGQyMXBuUDMwazJzUmt3L25LTU5xWjBteTlURWVxWEU1VkNXcEp5d0loMkdtcDVlMjhGOG5qMFVTWkpBa2RKRURaYlhzV3YrQVgzM2NYWEZDRVV1WVpScjFNeE1TSmkzNnMyUnl6MXN1blVvTzdwSW5peFByWjBwejY1c1ZXOEdITThuNW9LR2QwSHVUdTJ2OFFaNXdxdW9uYm1kSDJmMC80NzJ6N2lRdTFybm5sRm12c0w2YnZwdzZOSkt0YmM0TzRseEFiemZQSkdtK0NFTU9TcUFtMm1wWmJtUDRhL2tPZ05VMTUxM3RDcytIbzg1RFJzZnFrZzFKUXBSbmZ6WEVzUjRDRlU2dCtab1orYTJxMHpObW1ocTZqUFo0akxVd2dGRzNQdnloY1RIdWlNR2pxRnRPcjNHNm1UM1dmT05Oby9yWGJpQWRHOTN1MlVsajU0dmRWSm9DZmwvc0VoUmFiUUdMUVA3SzZQczhaZmc5aUpVRWNYWDAzVWd3KzJZQTJxZ1FzTlkyVVBzRmRNK2N2REJxdzBWNHpia3QwSVQ4bjAwN2p0bW9NSjg2OG4xNWcwTFRtNXhBK0M1ZHp3ZjVNa01VUUc0aEZWeGpTeG5GK3c1MCtJVTB2Smk2eW5RVTVsZnJiTGFiRzRxRmhldjZtTGtWTi9uL21IQ3ZoUTZCSWZhek1FQzIybFBTWFhFN3dBanR2bmtwVHlJQ3ljUit2WVJPN0VCazNvTWNqcWhma0ZSSjlnWEY0a2RaYmlBc0lRS3BHVlMwWkZrM2NKMllyZHJ1RDgxVW8wY0tOZ2U2K0R3OTJmbFlZbFcxdG55UzVXOGp3MmY0T0FXcmIvdklDWW0xMnNiSFpPR0NXSWcyMFF6N3VkYllYTXgzc2xpQWp5ejJNR0hubjU0cWt5VHRtYmc2TlN0TXpOQ3d0RS9hOHNhU3phbUZSQzdZRjBwbGJuSEpHb3JtejRncmxlUzhycU5oUnRHOW1rSXZzeU4rOGZSTnI1VVRMVzl4S2Q1NDA3am4wWUdlZnRZZ3k2RUdRZ3JST3FnR1EycFhhSk5rUFhhT2o5RkMzNnBLd1I4dHlCQUY2VzAvL2FuQXVPVGM2dmJsVXRyaTJUeFV6WGNJOUQ2ZkR3d3hvRHRuZ1dNZUpJQzQ4cTBhUUFKekUwUHlvKzNOOHFkVzNyaWRyQ1JtOSt5T3B3eUtvQUxZbFhEVnk0ZE12Ukx4Q3hSa29wNUdkcDRPZldhb2ovcEZEdW9OQ09zNzFqWFpuMXBBS1lyWng0bDNOWnhnMzhHa2JHWlBydEV5eEVKN3JKWDRrTTkvT21NUms0RmNnSzZ5TnU5YmJIeVo0bDBxNzJFUG1jVXl3NWZtNDhXT2p1TFgrZnUxSXgwZGFhL3lUZmJ0aTBSeTZnUkJNRFc4TTBKUXByS0gwU3o4QWF5OVR5VmV1WVJZOG9lUmw2R3lVYzVvWVNGWEpuL3BraEtodmg2NXFKQnBhUndPUVI4cXlyZWRBeTFtWGQ2V3dtTXJlaXJxeERnU0d0SzJtbi82aUQxSXZtdTV5ZGV0bnUrMm9VL3ppRXNabElMRE85U04rMm9DWmRCSUpSVThGTVFEalk4ZDd1OTdnbVI5UEZxaDJJZGdQcUhCUlVkTTRra3ZjWDVhSVNqdEYwSFZDcVNVOUFreElhR1hvRVVRS2JLRlFMV1dwN2htOWhKeHJsQy80aWp4YkNhYzBSQ2luZC9yL1NPaFhyKytvUWs3bjRjbEVBU0JaREtkVnRYQmdpcVJQVkxVQmFPcHZOTllQQUQ5dm1MODRGQm5RR2M0bHVEVlNjQVRJTzhYMGhHcDRqNUhiRWw4SVJQMmVKRXVOYXF5bUg4L1hyY2huNEJkSGNOaVBPMVZIanFIdkFlUnA1djgxbTN0aCtSWmpQQ01oVkhVdE1HejQrRjYzZ3JNS0ZpK2pLcXJIQ0I5SHhhVGdKek1XVGxuYWx3TW1vSmlqMVR1QjZZQ2cwRDFPUXIrMlRHNGhnaHB6ME54TVBZL2hCeGJwTFVzSm0vcDlRUlJSMkl0U001NEJWcGVITytSYS9DSkhXUkRPc2JmM0Zyb3I1TjNvNk9GVHZ6RUtuUHhMMGJHczFKdGdLTkVRMWwrcHlnTlJWV2FuRGRuajlNckNVWStlL3lyK1Z4QXlBbVdraTdtdkU4WDI1QnRTcmRqMFc3OVpjVWFtdEZ1SHU5WVZ6alJTdEt2cGduOEovZGMva0QwMVQrYmVvcWIvZGJQSWNXbFQ3ME5pV28vWXM1d0ZxcHMvMXdxeVBKVldLYWdreWhhWFRHU1pNRGM3ZkliTkZ1OC9QeTBuMTZNaDZQb0NheE9DNlRBZS9HZGY3QmZqRzZnRGJpNVhQbC83d3ZEVkQ4RGdFVFFmMnA2ellUT2FRL2tibkI3WFlhbldHYVZsSURVMnVyaks0Uzgxc2JJdmZqK0F5eGVLdFRxeVg4eDM2WDg1QUxzYU5Ba1VVcjVsQVUyTEtRcnk5SUVuWlBwTFBDRDhuOGliWmlNSk1wbGJ6NlJiRE1vZmU2UlhzMmlXczFiNWc5a1RBSnlXRzFxbjVLZWlKOVM4WGFCMHp5Nm5pNmwvaWh2bVJqS09pNkN2WXBGMDgyT3ppZDlPS285TElqcXlKV3JwWnk5UGowUDBJdjZXZXpJVmJGaFY1TDZHV0NLNUdxSzVhdTEwekptUWxtNUJsTHdZSUlwRWhTTFdXK1pCKytnZXRzam9hTjRCaE1HMkhHWVFaSS9jMGhUTmpDNm0vWGI0NElkeWFTSTY0OW9ndjEzMHZOdFJpd1o4OXUyalpGVXJKUTJxdlE0UzVaTGUzRzZyWnZiWjFpM3ROelhtNVdybFFyU2hOTGpnak1aaFZFdlhYTXRncHF3Qkt2VFF1OXFkajUwdUswTllTK1Q2UnBCalFVcWdNdUdaaHlpaGFLdEdJWnJmQkZjUVZueWpReFFNeUNwNmg0Zm1NcFE2TXp5R3EweVM2TFptUnlXOUdGejdXajYwNSs5cEtyY0orTVM4anpmYlFSNW5haGhiRE5wd0VnMTBVSm5VbXJNb0dPRDR1enQySC9IZTRWd0tBcHJUQ3J3bE9CbXZoblpvMHFvdnVuTnpFQUYyb3Z6a2owR1RsUUhiakNsZWovem9zbCs0N0JpbHZRY2VJQmVINyt0R256REV6SFp3WVJDbzBib3BWaUdsMVN6K2NiZXRyTWVkckEwbmVnUlNNcWlCTmdsMS9jUzNBRzJFVCt1WnlWYVp1TDZhc2VuUnZ4SHlVT3FKMWVWei9Gb0VGcGg4TFo2MlR4UUtpd0taRW5ybElDT1ByVm5KZStKcTZJWFI3M0FxRFlLVkNlUlU5OEkzYmFuZUdBUEZ6TFNCbytEYmFKbFowOStKdUhpR1JxVmhPdSthTC85OWNhMGtzZjE3TXFPMWIwN0xBaE51SmdXT2xoWGJaV3lGOUYrOWwvVnQ0TDVQcVgvbjYvWVh6L2RIL1Z2M2doMzcwdjFhVGt5bEZITmo3UWExbUwxVDdzUUhpbEJOZk5XQW5ueE1FRWhOclNqQkg3R0pROVNSVVpkVGR4R2hST2lRcXl1KzFUeHl1UE5UT2NCLzVqZzFKMjJ0M0JDZ2xwT214K0JIWUpKbmZ6QWI5eXV4MkxGWk5SNW9MRGtDU1hoK1hVcHlGSExOa2R2MStPU2tUcEFsWFJNRWpoSDlKODduWXk1TE9od2IyOFFoYUhieUI3dGRGelJhRUpxTVNkckc3UkFZYTRwQXY4K2EvYjJpa2o1cTMzN3drZmJCdm14TXdDSE1qZ2RkRldhc3VEZm0relhqTkxRRHM2SUZSK1d3R3A0WUtQamhnNDR0ekgyS1VYU002L2ZCK2JZQktFZVkvM0dxZ05ETzkxVVdDOWxOeTlwWjI2YS9vbXZKQk96dGdDS0VYQXVkVHNMenNwM21iZ1k3clVTRWdRVGhGbjZMZWxWVGFpRFNCL040eVdMZWJkdFdMTnpvUzNYYmRwekI2Q3RyK3B3REtPZFVZKzhWeFNDOWVSWFB6NHdDclZ3R2FmR3JrMFFSNk9TY2RqdWg4ajZDRk9UaUtOT25MenFPeE5iK1lueVR1SExCcmRiTEZVOWtZU2ZicGFxUnBJUS9aRnA1S2ZaVTNMTzBPakNFMjY2WWZGSi9SWEMyWmdWQW1iL1I1K0gxZStzcUZUTVovMFNUMUpGc25oNUNmRnpFcXBZczd1Sjc3QXE5ZlNhZmt5RXFXWkJYQk1rdk56M2tTdUloSGdWZElzeGJidndVdC9vSml1a28xNW9QSHp2UjRjU1VUMXEvSmVzR3BVaCtPcEZaTzdNSVd5eDZiVGNKQUJLYW1QWmMxaXVibnlGaGtkOGE0VTNub1lCL1lrK2NhM1RMMXBldVVMcjVKTGdoaHFBN3dscVhDL1J6Y1JRRXhRMXhyQWJhZ0FFS0d1MGtTenB1Y3lrMm4rSElkRlV2QjBpVXM4TUt1OThnZWJnQW12Zk9vWlk1T1BCbkIzc3NkWjJQeDkvNHVrOUl5QkQ3T1pjSkRtUUdvL09ON3N4QkFRYVlGRjdhaTZEN0lkcDhMWXY1MDVjRngzcmx3N3JITklTdGQwTzIrRkdzQzVpMDJCMG9wd0RaeklidWp5MVRkR20wcWlXMkpuOG5HeWpPa1h6NW8xVnhlK3pRRFVWaVVYRmJPVnIyeHBhL1JRNjBEVW14bGtXczRaU3hZWUhyVTVlREVvNENUdnRwanU0NmIvWERSMXdLMlFiNVVUN1J4UkJnZ21nZXMxRkE2T1JpWnEwYWJlalZGY0VoSzcrZzU4Y0Q0d0VRN0JPa2Qrem1tamZLTWZZc2hhOC9sZ1pQU01GRThmU3RpREtaaHhlcVN6N2tEeWJPTzZTa1k0Uk5kSE55MzJ2T2oxYzVoUFMza0tFVmtvZS8yR0xiYnB2NUNWd0Y1enVBdTdDSkp4bzN0R2tBRUdVR29Nc0NxWFBRTGVUWTZST0tESlhEZVpMc2RlZWphR0lzcVdTYUhQSlZibm5CT3NyQjRncjJiYmNmUSszc3ZDc1BmN0dSbHhrNGo1UjJoRkNZNU9PdGtLdDM4YktobUpkaURoaHlGWFk5bEpFRW1iL2Z4S25vQVB5b0xSMFNVNEYxWlpyQ1pmTHZxQTM4aHlRWUVGVXVzUjBRYnRYdDd4anFlRTM0Tm9yOFdRYnBYdjJIZjVHRzh0UEhvZEFGcWxKcW9oZmlIRjBOZysxVVhIYk10b1RlSXNjRnNHV2NJcW1zRkk0WDI1ZFliTEk4cUxRUHhjNE5veDRob1g2aGExNCtKOU5MSXAyMHZQRE1TNFEvZG82MGVqRForV1BpbkhvUXp6K1NETmMxR1dWRzM1OFV2V0VpaXZxanNDUldtMWhXNzlVWWwxajg2OElrZTBselZick05OUE5c1FQVDVEcW91TjRsMnE0WDQydVNaamIyMzRWTW8xUnRxNE42K0FTL01VN3FMUE5pSzBLQXB1ZTZQVUhvUjd6cHB2Q2dLcjJOOGJvajRGY0dobWdVL0dSVXVnbnBNM2szL2s3V1BPclhzSUJYcWhLelRQS3B4WVBEdXIrV3BLS3NueVREQlFMY3FvMmR3RExPUFB2NkpqYlZNRHIzZWxOSS9zR1BOTjc2RjFqK3N5YmtZQ3Z1NVBpdmNOVGlxVXZ3TFB6WDJWWFMxY1laR2RRK0pwYjQ5T2FvenFFMkk2ajAvMG1BTTJaaUNQeVUveFNLUEtTZG1HbGl6bjJzZy83ZlZUaEN4bitudWQ0Lzc0ZmR6cmlBRDVQZjJPNzJEaldmdkd3TXRCM09HTVRieitNUFBPN1F0U2hRQnhDWXhyK3BiOXhUTFlGQlhCNEgxa2w2SlhUY1BuM21MeVNJWjAwanVMOFdSSE9yUlJxSHBwL2lVKzlmWFEweCtQSWlsbG5xMmZvVEg3eG1CZlRpOHBBdDZvTW5BNnZkSHMrZGd6cklPSVRtUXl1Y1g4eVdLWlV5NmtOcFV2cWhHZkRPbFFGRkZGWlhLcDI5RmNrTzdNS1RxNGFvWWN4SzdRZWlZQXNoTXg5Z3BJWmZUR2w3TjgrOXhlTnAzY1JqbXVXcUlleHFSbjVNU2tNV29lS3RwcmhUUnZLYnZTMWV1VHowejVXSzhJcFM3OVpvTHpJTXBORmduUDBXdGVYaVQvSk9BSUR6TUhFRFhQUE5wQWVMTy9meWMyL2hCTTA4dXdZNk9QUGZWeURMVDhVQ09oQXpGbk5GalBESXowVG5KRkFUOVkzdDZJelNUclBRWlNEeGw5Sm91SzJORTdnbFpjUE4zRExEdm51TVJubWYzNGE0L1dweHlycURCZXpzaFJDcW03RXdBdnU4MHdqdFRZS1N0Y1BUSUVDSmQyanR5SHlWb1c3TUdiVERSWVFGZDNXRWxzeDdPOFpCU3dxWU04K2s3TUVzVnBxZXlHLzlkQ1BWZlE3anRXY0dDVStWYWJFOW11ZTlUbFpXRnpYWjRmdHZ5NHpwTDh1SEh4elMvMFZIK0JVMjFFQkwwdGpUby85Yzc3S1lTZUVMYllnaDNEOG43WlBRVmNuT0tJSFJnSFZZRldOR0Y3TXVESUdvWDN2ZDF4ZkZFQlRkei9RZUZRb05Zb3J2anh3VXRRMGVseWRGbmI3UUI0cS83SGw1UisyTi9FUTRrbWovTXhFUE1qTTZtY0VrZFozUFFZRmJzcG9hUlc3TWNmVHBWMFJwcDd4eVh5bmUrN2daOEwvRkhWRCtYd09GWU5NcjFKekM5U3NYRkw0a2I0amJUTjN1eWJ5ZHZUUERuWkZ6VXE1d2xGWVJpL0hxdkw2NWtpMVVQRlkrejhJU05TSXpVMzJLQXRsSjk5YkRMWVVXcWk2eXo3YlIzeERkWU1adHlxYkpBN3B0MkZhY3p3L2VrRXR2UTVyWHRzbklEeWtLbGZrT0lkMzVZa253S2pqcFlrODdsSHpWRjhjYk1LNGdhM1p2NGwxMWxTUTdPN1lVdUhoakh5MFVzN3hpVFF2ZHZGazNtS0IzWEhibXJHSlRESU9teDhJVXA1TVhpZEY3Wmd5eEpRYWsvY0lpTm1Oa3RpQkEveGhqdlpCU3p6N2xrUmVrMVNDS1ZlbzJxRUdaK0lJK2hWS3ZKS3pXdFJZZXUxdm9ERTFDSUxxT294VEUraW5VSzRxVFVDMmZGaG5wVThMdVpPRUhqZktPZlI0bmgwUzBxWEhHbU5YdElwWnVURDU0WWMvczlnUlN3bjR5aWw3OGFoVFhUakJWbGpVT1dTcXhEQnVxWTdNVVJDaU9rMVg2ODRlcHU1K0tMb3N6N1R3YWNldzh1K1hxbnRFWmY3bWQ1SDdmYXZaQVJDdlRpb1llQS9TOXZzbXN5MmZHa050a1JibU9maFUzNllsZ2lvWU0xL1VXMzZLMFBibXlNMTVZcWJ0Qko3WkpuYjdxL2s2Y1loQ1BjOENrVW5LTE9qUnpSTlBWakFBYUJsRC9Fcmp5QUlQd2ZyR3ptQ0p1eXFkUmpOc0ZPc0hrTk9JUDIrdnRKWUxVbHNHOC8wYUpFaUpNNTV2T0FDNkV6WlJlWCt5TFZBdGJxTUZaN1pVNVRDUWpnZHF1VVJYSmF6QU5tcW13aDZ2QkJJZm1LSjlOZWw1c3Vnelp5bCswVk9BRCsyTnM0dDVKL0RVczhnMXBwUWs3RGdQazlCYnJ4M2cwQ3lvbU9zY0g0YVgxbTZiMWc5OGliMklhZWVLcEtpb1JKZzZINTExUWphZkFpTnNYSFJVT1NCTjJHL2xLcU5VZDhNTUhqMGpEdTVJU1I1TWhmYVBHUFJZVVRnK2ZBU29tVXBzc3FPUkpVaVFIdTgzNVdZMXR0Slp5d2grRFNrM3ZnenBPWEJKdTQ2bFNXSVFvaVNBMnFRQy91MGpsckxrZVdSbGtQcVVya2Y3cXRjR2VPdk01cDVUUDRoemQwdnk3TmM2VlBSeDk3dlVEc1J5eC91Ly9hMFQyQmdEUUwvVUt3ZHZyelcyY2RFMUd2dUFuekt6YkltL0lZa0UrZjNmTjBqbzNtYnU2d2p1UVUrNWtFNmoyZUxYU3d6STVEenIwMGNCVU40WkIxUWU4dEtsdUlpdDB5V0cvc3lQOVlMUUh3d01MblFHaFFCTzRMMFdEMXBrTVB1bTRuVmJXeVpnQXF0SUNDc3JVcU9ocEE4WjhYekEvcVNIVlh2VUl5VzZRcldIUUlhcVB4VExIWS9aOFhuOVVIb0NSaUltQ1FlWVFUT1JpY09iYzNWYlVkbEViRDY3Y2xDWVFyUGVyVWd6UlVYN2ZtTkZjTmNsNGhzL2l6VVJQN2hjTGVoTFhXdDdxWU9NSlVrRThVWWVWWnJUU0hRSC9jZExnOElUeWJ1MlVKTmdkUXExOXdtZUpKakVXRUg1ejRoa0xuNUhoekliSDl6NGY5ejZlazBsQW9OVmdrL3N0Y2hyRTh5V3JkeldGU3M5aFJkS3FyMGZxbzdYWVRFSU82aW9kdmE4T05OK2xadE9XVGs1ZGhmeFUzYlVVanR3U0JsY2xaT3JESGJWZHBEcWRhb29tc3Q3THFRQzNZaUdwclY4S3lOYTN4cDVJdEhtL2pIRm8vaHpHRkowcCtjVmo0YkpmUXRDRGJKNHZEM3B3c0hRVnA2QWY0c1FuUlU5NHEyNk1HSG9uU1A2SDFyOVNEbkFuNHZrS0tCS0ZXT1BsZHlqVVB2UWg2am53VzVBSkh3YmdMWXB5RGdtVnZsVGN6QjFUa0hycmNpeEZMQ0NuT2x1ejlyYkJHWitGRFhXVVQzTnA1TERUb1JpZDRiN21hSjhxOHlTVHU3YjhkOUl4Q3FjWXNxelpWS3JRSExZTFRHSU9reUNJa3B1OUpuR1NiZjQ5Z0IzdHBzd1VBRGM3MlF5NWhkd3o4bFlxbXNWUll0SnFXbmkwMk4zR1ptU3NYRm1uV0dLcFZMWm1rRHJkRExvTEYxU2xnS01TRjZ1dU9rNC9TMXlEQ1pEU3N0djRaWm51Y05JTkpMQXViVEZQWjArczN0bU1IYUJ0enY3UVNpUEMvclNyUkdDeHhTbTBkMDVHME1qZ21nL1o1b2ptQlNIVFhUVDAvYTBpblhtZDdwdXBld2tjV1kyYktzVkNUMzlzVVYvSDZQTmltVlFyR3UzMUo1NW1aUG1GS2tDeE5yMGx3bWJ5a0xCNVJIY004dlNhelRyc0ZVWHFsNFkxM3NTclo5aXYwaS9BZHI5T2p2c3FSNW1TdFEvTERJaEx6VHN1K0tkcEh5Rzl0czdFaXhRbG9EVkhMMWtSWVpTWHkvODlPbXIrOTJxbmFVRjFkM0MzN3ZPM282ZDBRMWxzNHJUb0QveU1zRG93SkRXK2NNR1FqaHBHTUhNSno1VDV2V1NnNFZDS1lyMVBGS0Y2em1VQnlVSGV5cmhFVlN2QUlrNTVRY2pTWnU1a2VJNUdGRFN2SXdZTHMvM3BoM2c4dm1VSWx5V09GQ29Kdm00cGpRNzlMa1Jod1QzTFJyalhrZENDb1o5bWJXam9qbGdXMXdiOHkzM1JzLzBuMzdYWnFIZ0tqOHB2L3NEUGt3UVNpU3ZzQlM3c2IySC9VeDRRMVVWaWRrS0ZsRzVqcklQenA1ZklwQWlRVkNEc2V6NTZ4ankzR3ZjamJ6RTFmVWhDYjBQbFp6Z3JvNit1b0ViRk5MaUZnQlpFMWJHbERva05rTnl0Sm1VN3pJVW40NkMwOVhiem42WURNNkFab0FoN1h3VFVYcnh1R3FJWVZZMnM1T1ExaUhVU2xPdVFMNldtdGRmTHQrVS9HNWVSem5Ka1BnaERKREovNEE4WjZIa3AwL2p2V2NMTXNZOGxjZDNDb2FjWEZQVlc3Tmc3Q3RVeGFEZUdJMFZmRS9RRm82S0VYbkN2MjhCenpiY2Fuekp2cVFsVnZYcERkclQvVjNCQmpvbkZKT0xLZUlvK01zYlhxbXdsdFkzK0pLRGhrMnA2ZVBrNGNEdjRIOVdsVjZNL0p0bTVTcGZETGlDZW9yZk4yOTFUUG9qOWl2YjFIZnFBRXlIQVV0RHJ4UFZYa0Y3ZzBXSGs4WWFBMmZhTjJMZnJLL3d1ZEJiSW51SWFNcG4rbnVReVJlcHBzRjZFeXRiZ2dsZ2VuUmdNeDlKRWgvSXpaMDNnalQyQkhFanl4K0RGZWRHaDAvWWprNU41RlJnWXU0YU1RcktxZDdGSzZvYU5XK3RCU0JBd1FHVEppdXZDWDVzc1JrdWc1Ymc3TXpwZy9xV2E1VTd3MkUzczVjWC9DQW9yOWdBa3ZtMDZsSkpFakFxSXpZYVZUcTRMdGthcmxTbzIrWHYvWC92bnlmZlVNT0ZzNUVMSW9yQUtOZTQxbGYyOXZISkNzb0dmNTVCakFMR0UzMmFTbmVqTVB1Mk9ka2tCYWVBN21sNmljNmdWUWhHSW1vMiswWUhncUswMkF4Y0pjVUtUZHpQNVdTSmhkU0lnZ2RxL3hlclowQm5mRERBeU5XNHJrblU5WmdKZ0VabVMwYUtXR3cwNTdBOGdVT3dab1J6R1BWZVJsZ29KOHdBWWkzMVhXM2FwOXNISEJsNThOcWpJUFlUYlJkK1daTHlSdjFWc1NZTDkxNkhsZlhjempDV2t1THNvV1JKRTlkajdhR3grdjFZN3Z0ZUhUMFFOUUtQS1VQQW9pRWdIckxBYWc1Q3Yxb1FtYVRHU0RpSDBNeXJOZnl5UEFzbVBrdVlLT0NYVXBwby9SK2E5RGxITUhuZzBsbmJPZkp1UHZiQU9JQ1Nlbk56czdJZS9FVnB6RFB6T1oyZnJTL3dpdlZBVzFJblpTcWIzZytURlpuRk5LVEFhd1pvdEMya2FIREVJdXZJVWFoYWRWUmszK2xHZjFkSFF0cUxSSTRoVVJxV0xjVlg3bmV0TldOZ2ROdlFpSFY0dTdnTXZHM2xDMmdIZk04UDA5TzQ0TDdrQks2UWE3WFFoSi9UTldIMWpjTWVGS1ZuSndWV0h2Qzc2amlPZ0xxRkpHM0trdXhVbXlvSGdCK1BBbUZUcGVoWHZSNVFIb2dhSkZBQnk3MkpwSWViR1hCMUVQMFpPeXRidVVGWWppdkh3SE9tSmdYU1JLb3d2K2NEWHppTnk0Rkd4QXl1VUQrMURqSTFreUFEdyt4RHJUayswaGIvZGZKc2ovVmxnWmVieE9pQVo5Ky9NUGZoYW9ydUxZbElaODRSRjQ4SmZxOUJZaXVJQlI1UUxRUHBnWjQ0TEtvcldDcTgxTDM1N29YbzlyQzB4aS82NE90Um5HbWw5c2xFalorUWJHREgySXR5eWYvYzkxYy9GWEVEekxrcld6bzRwd1dVM3FuR1R0TURadm1QZkNGcldhVGoyQ1V5WkUwR3pyOE9ZSkNIMUN1Zm9hUXZDNlJaM0dGSmNMM05ackovQmVCNzdtOTdjc2JnNllidkdEYmc3MG1yM21uS2MwdTlxYktCT3NKMW94dWdXNXF0T1hIbDU2WkxMUU5qSThQcUkrVC93NTdBNzQ2aDByNndUZWJ1UkNyNGR6TDZ4dVByTStwaHlOU1kzSndqbjBxVEk3cUNXL3Y1dWlnUlZVakFKSVhac0dzOXFDMDB4L3FtRUhHeEI5cy9jN3lRYmRWNmdXbkwvY1BuRFBjNVU5LzBmdHJCQTk5ZkxtS05vSnFvTkRFdTVXcGdjM25wbDdsNFJqS2lkSW11VUFxSktsUGRhNmU3RGR3dklmYVRwS0xXSEZGQWE3dmgzTFFvQnFqaEFYeXEydVBYUHZ4a2ZHMHpvTStqTy95T1NUMkRLa3RoSlFCcEdzUmI3ajhWdWJGWGpwYjhUc2xiVGpEdFliL1lpR2V1SUNJYjcydlltZDNDMlJkd1p2QkQ4UDhtYnREbWxEQkorVnpEbnlhakRlbTlpcTlDbEVMZjcyazJVcklhNlJjdXZORndva3RJWktpUVEybnRzcllPN2kvRXBvbTErWFo4K2VSTFQ3T3JtQVFadk9NdzBqYXc0bXpYSWtmVGZvYVZFb2hDWnVWS3ZEaStiM2U2WlhZMkVQYmlJQVNpUlVsNzdFekptRXJpUUxwcnEwMHNLQVJVZkpTL3FkOEpsMzg5di9xc0N6VjU5cmNqWk94UEZrY1JZUFRQalMzWDM1WVdieXBFUklCN3JUdVl4U1dPaGY2OFhnVHowaVdKN015bk51QmFZNFl4VE5kS0gxWEw4MUZnRWxyNVVJSzBzbDRGRk92THdzNjRMN1pTY0VOaWVSNjl5aExXQjM0UERnaDNpRGZwQWkzMWdxS3N5azhySGkxTlJMSDJUYnZZREYvVUJUUVhwenNibVJjWDR1OGlRZXFNTWQvYkowczJDUTFWMUU0Njc3U3hsOVBld0sxOWx0TUFDZWtwaS9jQjZ0WXlqa1ZMZENTUnA4dm5kVWFkb1dyU1RTQzRQVGNORFRwc09ZWHpEMzdlVGo3eFN5U1hIMG4vdklTY3M3TkhhN2RGb0h3b2htNWF1Mmhhd3crZEVxVFQwRCtvT3BZREcyd1pLQURES1ZoUUJpeGlaa3BZK0lNTzhIME1teHFCZE9WbG1ucERDS3NrU1drOGUvTzZjSzVXc0p6cDV2N3dkMlZrYkZUUFdaVFI3YkUwRVRoN0h6bDRuZEl2TG1kbnJUbWpFd2xGQ242WE40WGRuMGZsN0FDZVZFQ0ZFOGg0dnlEYklnWHF5SkM2U3JwQ3FjY2R5ZE9UMnMyYUZaNWtYYkFKbis0U0J1d3dobmIzRGZGdEZDZ3lqVnhHeDl1MVBxa2NvaHNTMm1MbGQxclV0Wmp5UktBYU1mL29vUlV5MjR2OXFTTndWTUhYdUhGTXZXTXgxUlZXMG9tRUdsY3k5b2N2alZJQWxVdjhkQUZ3WmtmTGxnbGZpZ09DUnVOaUx3QUo1VTZFeGFxZHZndHo4T2o4azN1Y0Jkb3RnV2VPZndNQlMrc21jQzJoSkI2SE1mK0IrVDN2L1NkRE1QMWV5OWZQM0R3RDhQM1UvaDlBZ2pIdUxMYzVMazNyTkhZUDhTaHJzUDBQbGI1VFlva05sdjhqelFJWEFIRGJFQkkza0JQYndlRHhNaVZXZDdJL3NBYmdybDhoOVhMYUtsdVJjSFdVNVk0SnVLQ0dOY3ZOTE00OVhPMHNicnFadVo2MHVqRkVJcEdWajhDMTU2eUZQWjdpeVdTWGpST050blovbnpIUy9VUzRvSW42T1RvVUJzSWwxNTVidWt1cXhoam1Kc1JGS05zN3l4aFFGL1h5cm53MXZCNEtNanltdjAvWktvMDF5RE41L014NkkrWG9uRXVQY29LYVV5RlpJNHFrTXdOMnhOaEw5LzB3ajdSWmoxeVovbDJ3MXhXSlRnK2VKVHdhL3k3STE2R1hyc20rQ0p0SWh4RVZUaUo5a2NQZGI1a3YrZloycDlYUTVSWEwxNzlDbEpEc0ZVVTRzeXZ1WHFmNEpXVjljL0pEcVZBZlcrYTBSTDVIbXU3eUJtRzJzcTQwMzVCNzJaMmFPNG0yRHZPQnBnUnhFN3czYkJCd3RWMkFVZnBKLzUranRieFBDbWxkOXp5aWpxSVpzVzZRWXFYcGwyak1QT2NQSnVqZUVnK1BKZllvOUZpWENYSm9lN2M4L2NsYXFTZUNnclJjQ0w5RWVMMXNYaVRibUVGQzRIT3hMK2NoU0RsNG82VjFOdEI0VkJxMHJiQjVFWXRtSzJJNHE4bzBWVXJha0FFNFBQaE1obForNU5OT0tkSmpscmZPYk9mbENET01jUTNyNWRNS3pOTjhTZWdrazBKTnoyamlWN3dEcHNHUjd1VFRsZzJXQnc0d3BrdHlCdWc2SDVlQUY5d2R5UGEvZVd1dGZETURWMkthdmpjaHB4OUN5Tk1qVUd6WlhvRExycDlwMTJST3IxR3BZN2VscGxmNUsxemZqOHVTSjNvYW4yRjVBN2x0aW1jUEVLaHNIYVl3a3QwK1VUNi9iVGEzT0c4T0NSRUtKSWhpcks3cHF1cURLZXVkTFpWbksvS1hCSDVxTkFIMTI3MnZLSUFOOGpSNktBRkNBR0hpaytZU1Jpek90cTNwMjNlWlloeHByV2U0UzJjVTFIdjlvN01tR0crQW45dzR2bmN1cFN2bFVPc0t0bVcwT2pXNDBKUjhaelVEeHdZamM1MVROUi9tSXRFVUNVL251VmxFYXRYR2x3Y1QvWDRTMHdYSnptd2dLbmc1SUdNYVFBRzRNbVo3Vkx5NkNlakQ4NU1vZkYvdVN5L1d4RHc5ejNlWTlpRWdoMU9hZjJ6elVSRVRLTUpadWZUVkdhOW5lQjIvaXpkN3FrSUE3QklaUHVHUDFZU0xYalRHMVl4NVpnWmVoMnc4RXk1TVVxamRNRDlDZXN5Q01meC81YUJKa3NDalI3RytyWHFOQ0RicjYzaVl2WFJhZWhWWHNzTm96UTZrKzdkN2c0b2J5cEowei9VSFBiRmF3QUpDcGltaVJyT0EzVW10UEZKQkQ1bmxnTUM3YlB0WENLd1NicW13bnFocGJZdkEzdEtTdW82L1V3TnFPZSs4LzcyVmhrRmFNTkFiOW5GdHIrSmN0SDJ3aWRaWGUxZmlRc2FxZTM5aS9TRStNYTRreWQxVUs5UVpOVGRPdzlvSE00MlZYSXFCVEMrK3docXgyTFg3Qjd2dXRzQXA4ZlNQY2NjR2IrNUM1RWE0cmdEUEMrc1ZpZStnd3pMNnNUTGs1K1FoTzdYeFdlcW5MT0RpSGdNUzJKR09mSEcyMUcyY29TVmd1dFpZYXEzTEhPb1ZlTXRYMDc0TUVGaEtkdlh0TGVvUXl1R1F5bFhoK0x5cGVBNHhLZmFFY1RnZFZxbWpHZS9GSDNqL0loWlpCallzQm40WFBPWmMwTEFtMUJ4RUtGMUFGQVdBa2t1Yy8wUmpTK1BJSkVLUGx0M1JOV2dWWndmRjJkc25QR3g1OTFLSm8vajhqUlJETjdmZmNEdmVmQUJuOWRJcDFHT0JMYThma0JxOGx1Qm51eXdZYUc0a3FJTDJZdTNPdHF3aGZUL2l1eHdCTzB5cldrYTJpYS9hVXZ1VUJNN1QwRHdnek5vK1I2MUhncU9BNTNzY1d4blNEQWZFMlkrRlFiaHdHc2VCbUs2RDFBdytIWlJkaXlCK1dUR01jQkdJQXQ2SGEwUURFZzdYRlJoZnJUVUFuMWg3OXFsVzhoUEovMEZwREFOSGJZV0p0dEdOdGxqTVhzMzNkZEg4aVRpWW9hNTJyNTlaSm1vNkpINkdDb0hPUW5kcHhGZnIxSE85SFMwRGlGZWhYcnN6eGd1bDdFckRxMTM1c3JhRnpvemJuQWZ5cWt4eUt1Ujc4SERNV21rM3hyVXQzcGJvWFlCdGpEZDFuanJ6WnZiTXo0UFUyYVBFWjZGSVB6NUdVcmpwK0JkMnVPbDJUYTcyUzF5TWZHVlAvVmg1SWxHQkw4NlpETExGeHhWZjltb01CL1RPUmIzRUdnTWE4Y3k1azhyL0hlWkV5Mi85cmg1NHU2RkJ6MlU3dTk0Z3RrUFBYZ0I2cDl1a2JRcGlieUtjNEprYzd0cEVyVGR6Ukp1MmpXYTFIMzZtM0R1OFhxUWk1Z2dVS3gzNCtUOVVFcU5tY003KzVXdmlxQ3hDcElzQThEQ0R5MHBHbHRmRkNLdUJNTmxzSnVJMEtpN1ZmWnNnVmtkaW1yMnUwYW1OUjNKSGFwSWZoY01WVFdVZXJrWFRBZkhQK0RTU0cyL0JHcVIrSVp4WTRPUlJ5T2RsQVorUUc2UktrWk1VQ0NKcGdFMkgyQ3RPUDZKR1RJWDRzc3QweTR2MC9IU1VpTHg2MjJGemlIQUhjcFVKUXRVekpmejBFVzIxdVdpSGx5S05pOHNmaXlmRlNvRGUreGU4QnE5aGtaQUZFQnEvUUJCVGpTTGVZZHc0eXhsZllpcVpvVStpWTdZUFJhMHVnN0ZHUXkwOUpQVWdYaUJXRmZTdFhUNERiWCtLK0NrOERvVkNkNVdRU0dJVDR1UjdsaWZ2UVFuRmN6Q2FPaHczMHhwZlBmSTVTUlB1VFArWDlSTG1KRTBmOXdTQVVZd0xNSXZYcVppcXJQRkxBKzVNTzJ1QmN0MXhwNE5FRURZVGRManpjZlBUc3hxbzBscmlGQjJQTVZhUGM2UURRK1kwS3Q5cFdDVktwano1RkZQOUxDVlM1bE1lUnN4WUMwTmJRRmFOOG5jZTBLeUFrdlYrNnBRbzVubDlScG94emRjV1JSbTdaZzZBQ2JTYmtMdURML0Z2TG9EMGFlSi9XcUhLWEdNNDB3dGY4MVIvQUtGVXRsbTJDZnpZVWRlSk9oakNDUjhkRTlJTFp1NDZLRUNGUFhzZ0VFamNVMlBHYko0N3BoR21jWTl4dnAxdmd6TmE2ZXNPT3B6OTZocXNEbmthRldpamJrMElpRllmRFc2TUxDaGxIUTEvZFdoaFZESWtaV0szYzZ3NE9nUXhob3BmOE90cm9MaDRTajVwWDFyL2VBRFFWR0d3ZmVjbXM5dmFrN1RoSWdkYU9pTnZNUmVEOEJBeGI5NVA2d0xQWC9xaGk0NEFxVjkwUWZrTWJtVDUzWnRNRngvTFdhNTZIVkE2ajVPbEo1RjF3Slc0YWgrWXZ1cUZjbDhKaHZzOFRkZmpFcTJsQ3Nnb1ppUmZHTGhQMlByQ1hBSUZ2azRDVkR1U21iYTdxdDFOaFZIRlQ2OFFwU1BYRjl3dzVCaHdENkd2SVh6VitXaXM4cm1LTi9WWlJFS3dLVmdtajF0SnpLNDJaK2xvelhtWWVTUHFXZm5VRXlRZksvdkxkWnY5SytGbFp2djh3dlVjSEtWT3RHc3k0aHI4MHZlbUtoK01BbzBQL1hYUWFaNHp0RnRGRUZiNENHUElQbDZJS1JkTHZlb0xRb2NrTHp0ZVdQelBkMHJVYnhYbEpWU1VHUlZkUXBIWmIzQ2YyejhScE1Oc0l1LzlRRk52ZnZ3U1RxalozYUhpeU1xQ2hQcWZrbHhhckMwOWQwQ0FMQUdVcCtZSlR3T0l3dDdpV0l1Y2dqTUpVQVgzQlhURkdQU0FnUVZpc3VmT3hFLzVwTzVOaUlvT1Z0eEVJcjV2cXpROUp3RGd6TStsL0JWejFKZVQ4aHBUNXJSUmpVNUQ0b2d3Mkl3YlJQdmxPaVd6N3QzK2xCRFF4a09qSGJSamVTNWp6djhtUkJKdWY5eWpwT0dneVJMTGtuZjQra3JQbkRQekFDTysySklZZFB2QlVWZ2NpR2o2NHFBMCsvT0RDYWZkSVM5cTNCUDZxVzRlN0ZHVDBrRWdJdk0xa1ljRXEzRE5EWXQxaWFFZ2R5TVBvWnlNbG96OHBmZno1V0FrN2hzeXBNYkZGaHdCMkFJb1JyL2lLSUkwaFZydnlkYWF2N0kzZkVvMk5leXpDSkcxSGZiNU9YOVRFMm9RcjdXNjVUdU5wbjZnZFV2TVNQb2grVDZxZis2OGdkTFV3dWJHbVlYNkVhZ3FPekZpWHhERDJDSDJidFVpVDhEZHZ3cHE3MUh5ZitjN3ZHL1FHc2MreGVDRGNqMDA3VlY4U3dreldGSnQ3T3JtRVYyZkxFYXZqaW15dnhLY1QvemFzNUNldHlVd2Y3Ri9mQ1BBNzEvdVJIRGJ4dEhpY0l4ekFEL0ZoZFBhTXdxdlh1b2V0QzlLa09abm5QM09ZVGpWU05qOHRraHV6anFyRU8ydE5QZzZUd2RHRGlNT2cwblpTemNZTHphblk5dVQvZ3JXbE82dTF4eXJCcVhQU2ZEeGpEdkNHdmpmYXBuZWFtSk5SY3VWajdINXoyaElDN0RtenVMOThvakhBM3lOOWNOYUV3eXFGT3VIYTlsWEt4STNndW5UTHYzNTRCcXI2SG5WWFYrKzJSVm9TNFFnTVk4bXp4QXZDRWovYUVjbTlGQUs3TWYzU2JoNytTaUdBYVQ3T1RXYlBtbjQrSzg5bXZLSTd0RitUam1RL08zSzNOZnYyNnJiQTk4aUpyOFg5VENubzNFRG9vdWpZRUwza3NyRWJuNTdUWm03eU93Zm1KWFJ1eGpIbjJmNmxNbS94Sjh1ZmVONVYwTEN2TWR4YjV2VFFWSVJnSUhjWG1tL2UrWjIxRyt5VnAybWpiZWNWbWpyeVJtc0JxYzNOWThkbzVnMFVNQ0hEU2NXcmQ0UWtVYkFvQ0Y0ZS9ZTTIydTVMSi8yYWZ5SXpzMjl4RE1TQTRnZElRUERQN2NpeWFlOW5FajhSclZUS0FFQ3BLRHVvQW1QcWczaVl4V2ovT0VVUXpGZjVwN2hMaEtnS3FaU25mVFo3eXUzZ0NiazFFQXJQUEtkak9PelpwNGNsU2FPbklQam4xLzhzS1dpQUtaTkVZMmlDYXZYZkd0RE1BcXdjTzFCTnp0U2FPcEljWHpNZ05oWUV3UXlCUmN0YkRPRnEvdko5QlJpOWt3YVFPcGphYzlBRnFKZzk4MUgyMERmV2RiMmgrZ0tpWXVRSjZqSldFMk9VVTZnZ3NBUTU2THFXN3BTRUV2WVBIYWUzVytpcjhqdU9QZGRIZTFnMnRseFpQejJESFJJU0tWTGcrZU9OZ3h4M3JJTDRnSW1YU2p5R0V5K09KUTNieFBTc0lVOVRWTy8waUlrOHpraWdaSU1tQ3I2cFNOSzBibGMrMnVjVTB5K1RWZEo0TFcyeUwrSXQxQXB4ZU5xTlhrRXJ2ODRiRTBQRHUwcXl3TE1YN0VXR3pKODNaWFhHM2FFUTJPZ1ltNWQrdG0zSUR2SnJrWFpQSXZ3UzlxNWg4SVV2R2RaRGJWck1hUTlESCt4L0dxQll3clgvYWtXaTk1dGJtUDF4eGhNM2xGMk5ML2htWHp5Q2pwakNXMG5wdUNLVENBNlNDMHkxSEFNa0p1VmRrV29xV1hoL2Zkb1NlN3ZiVm5RaVF2Z1dYd0ZpMS9NY1dGRVNxclRTblFUWkZ2UGV6OUtxRXgwdVcxdnpLYk9wd1ZtcEUxb3htOERwZUNBQnh6Z085N3JhVDdRZkd2RGpmRzJaZUkzL2JLdHorSDdldDlDYWdmSXdLaHN1LzFjWElqS25jTks2MG9KZEo0TmEveG1yUVdFRDBLZ1NWd2lOM09Dbm43RVdSRFhzL0hVWUo4RDAwYXkxQTdWOFVGRWZpQWNTeWNrdFRwZnpkTUFDYlhOWHhaYllrQ2w5ZmdtaG5kVWdhZnV4c1RaeUY1dXFpQlczalZ5cW9VV252aUdGUjhvbkRrZlFHbmtiallWM0V0NG9rdEZPbitiZXFLbTVxSkxsY3BtWStQaEl5OXFQQ1lLdHZLcUV0aVRud0ZWZjRyaDZBK0I0QWJXWGhNRDM4TDJHaEZOTEsxYlZ0QmVPTTFIa01wTXROZ1dXeUY4VEVsZGtRcWV2ZG5VR0VleS9CcUlRbDVjTVppMkxRN1hmV1lNejNDa3VKZXQ5MDliQm1adDEwK0Y2ejhrTlhuY3F2QXA0L0kvaTJJMlgxQUgvWDBuckJ4dFpCemhvRm9UZ2R3SldTUVovMTdnLzEwSHlPSU5ob0FITlM1SDhYdDlCWGZsd1g0akd4WG1IbTAxRHpuY3FxRnkycjVnMjR0MmoyWURxM0d6N2g5cUFXSjFpZTU0S3E4Wnhxd3J1OEhRemdlUllOdU12NnlMcU50L3RhT3hPdDRvSzBMa3o2SkYwcG5heEdia1BhblY5cEF3cW1hOW9BMVRwbFBrcm1tOVZMK2MvWERxdnFOYVFWbFJXbVZxcGJ6a3hoRHhpQm5XYkpoRDVOM0pOeEZXRkdCUHBYZkZKVVZqOUg3VVF5YXNLZEtzNEx1OFZlN05rdjZsblQwRVkrcGpiZWNlWjk5VmtTWSs4bGR0S3RURWJsWWs1N0pSYThUMHNnZitGdWJhMmUyaFhxR0JyTWhpZTcvYWc5c1pjMGFIb3lEcVA2cFdKMkZsbjZFZ0JCeVc3RUEvbnovMzdPTWYvSlp0a0E0THVVYkNQZGpXUWd2Y2dpdHIyQWNtTXM3L2FQNS9ta3pMdlpCV05NMHBUdFBYeHdXTnJQTG9aNVdLbUFLWDhVd2tFUHVYZG5TVk1sL09jaTNYcFZ4QzdnUG1YTjRaRzRHQU5MeWNaN2lHL1ZCR3dtdDhXbTkxWnFUWlAvc3d0d3YzbStBWWpPNVBUZjJJbFZscEdjYlR4L1Z5endxekFLSUlGZ3liY3h3ZkdwOTFvOTZhTGJqM0s5cDdrUXgwSUhLaE8rdW90R0s4cEpyQzc2UFY4dlZod3k0SWhaUmF2YzFWUmRjNlBReENhN05ZUTAzLysva0FCZ2JkNlVxRDJ2c05TeVAxb1FlenFxMzRUbEtRdzZaZlRYdjNFais0QjZmUVZQUkQzcnZhSlV5WFRrQTBOdG5lTEhtMG85Q2w5RUdCUmZVTkxIMVovVzFPWU4weHJ5SDUzWDV6YTNEemdjRHRvVzRHaFFySFV1NEg2Vi94WjNvZ0FyeUp5ZmJjbThZMjMwVzZreDZXQXoycDFJMy9GRlZNeFpZVmJjR1J6SmQrY1d3OU1TclVSajF2TGdsemNycGlRRldmUlFmNmpwSUZxWXptY3ppK25SZXRCN1FVcUlOdWF1MGo4OCtRd21zdDZqU1JFMmZySE5WRkVsbm5EbWovZkQ2QWZmZzBhcytjUHhpc3lORitxc0luTEpMYVZXa0lQY1IxTUYrYnhncHRPQXZidHJKTG4rVW54Yk02SE0vUXp6OFVLSTdQVis3ZjI5UFpNVy9ORzJNQ08wNk4vRFgwQm1KdmpVTkxUek5pVHliSnR6QkptdU5kQ3BHZ2MwUlRJZkZEZXNnOTdZVlIxZDdYV2NMSzloYzdGZHFSOXR1RWZCaW9IV2NSV011Rlc5aXdlMy9iYmxxZXp2cGRsdytBank0WnUrU2dlazBCaVNSczJ2eUdBaGFoQmZHZWM3Qit2Wk1Sc1VUWkZ2R3p5S0xvTmhXRE5MZnpzZVlxdUdYQm5yS3g4YkpaczIxRmp6VUw4amJtdzJzVkkrN2FvSGFWcCttNFo4VmhuY2MveHZxNzBiNnV0SDl2ckZmdDdhdjgzc2d2cTMyNWZ0OU5iOXZiYmZWNnovMXZkQjMyOW5YOE9pemh2cTdvUDI5Y2w5WG9HWDFkZ2UvVjFCZnEzVFBEWGNPbWk5ckU5MVU1QmhvckVDeHJEVlhMZ0ZVMlg4RTJCa1BhUjNsa2xWTCtlTTZ4Z2tLRmV0aVdqaFhMMXFtZHR1dlR3UjZWV0pjWnB0SStqM2pMVkcrR002T0pXOEl2d1lGSHhWbG5Sb0I1QWxBelY5dkJLY3VWN0x0TmVCSWoyUis0OW1SZE1jZE9HeGd3RjZVdW92eExSMEFrRXJJWG9lV3FURWQ3OVEwSGFqZ2cyZlFWZTFzdW0wbkhTemZCTWtYVEFHdCtkdng5T0lEaHVkODNqSEdsbEZJa0FLeDZQaExKMmJrdEdiQXZmbVVrS3N6cVRObTIzZEk4MVZXTnBGQUZidnN0YW42Zm5PQkwycWpxaU14MEowTjVCdW1vOWlLNkgxMk9mYTRwQS9yYW02UUd4Z2VFTUpIa3duMXVaRngyWlFudHkvemlWaWlMZ1R3bDArbHVDekJXbUJyZjJ4WUF3K20rUzdIL2llMitCUlZIbkN6eHVIb1dWQVA5MTBkTkZ2N0Zlem9MTC93SlhDTERnVnBQNkJyMEJXTkRiSDNlOFh3N1VVOUc2WlpqR283cGg2Q1BoMWNyZkFDZ1lwZlpTaHlTZEk3UlM2UnJqRUo3UFRsRU5DY1RhNmltNUhsWWI3WHEvN0JQQnFTakFhSGgzK3VwdGFxb1Qxbmo4eDZMM3Vudjh4TlFEZlMwUzExYm04WmIxdTUzTFZKNlljZlNsU1lkWi9yT21mdWF1NHhxdTRDOGVmSGsrL3ZNNmE2QmFiUHdHcnYxTDllYXZxYVlkY253UXBzMU9jUVJaVGsya0xDTEJPRnlrVFppUldKcWtuakhxRitEcThudWh2Umh3TmtTTUhjSkgvZTFKdWhtRGZwTTBGcVRwR3N3WTBFZkYxVEdEcmxGdmhIbCtQK3h1cDB2aklMZzJ6NEN4MURpeklOVnE1OU5rNjZOWjNEdVd2ZmhYazNZdkNXM1NBSnhRU1VITjNVcG84bjZpUWdXTlhaQ0RRcndhcTBVcVhXRU9TUkQ1ajRqMS9NUmdmbUxSb2hYcTZVcmpicjNYclovazJ2cmZOVGNLMVNlRkdaSmlYVmVZYXBVTXZKK1FwcjRhZHFYNzJtWXJxOXdDWE9JUE1USW9UaitaYXFMcFdkMTNIWXkrZHJQcVRPcXpGLzVFNFYrL2ZXQ0s4M0N5NDNncWVzdHlKKzRJam9odUFkdG1FTmxzeXczaElIUENXR2pFZUQ2eURubEZEelFUMzhSZWpUQ1gzd1FPbG1VSVJDVWxOMmtiT0hQV1dYQ044VXlyWjNxSFFxb0ZIb01mSWN5UWo5LzhUa3hYYS9RVldUVzA0aG11TFdRK3c4eDdoZ0FwR1ZCWHF5VExuUDhLWDRkWDFZVUl3Tm12K09lOEFOSTBhaFpaMHdodVU3ZXMvdCtMRHYydUV5M3cxUG1ScWZBWlMvcnl1NXlrMHdaOGpNUVp4VzVXMmZ3MUkyam5BUjRRRGVDRlltczlaeWV1QWpIVXd0TjBRR0tXLzAxTGxkYzBWM3ZseDhLRThEaWx3ZUMrdG5PL28yR3pjRzFkVXBjWG1nbXFVV20zSHIvZW5WbUVJYm40dTh4U0lnWWtTcS9ldjNnQ0poQVV6TnhmTkNXa1dtQVNDSURYZXVYRjBYTytibEdMN1BzQWhoOHNRaFN1QTZteURsNmc3ZzRhcUR5Zllna003ckpHakZPS1daQmw4WEUwOGZpYXJkVFF5T2ZnSmpjWkcxaGhmN3VYUEFXM09WblJhdnNDcmljTDFFMmp4NE00R0c4aHpWRkdSZ1R0YTY4NkVxaTFDMzFBUzQrMHFmbStZSmdLOWFlSXlKSTRkdXpmNVBRZkVuZ0pWbHdBNEJWUm55bVp2cDhlWlhUU1E5eHorTlQ4ZHlYVWxsbE11WG1remdRU1RIV3l6UE1hUzZiNDVrSEk2Y1dqN2c2dzZQTGhST0U3alNIU3daNzdrZ2h5VFFLSTdscUd2UDFqbnZSVjhLcExLRWFId25hUDZabWVhSUFmZUsxUUFpMlNYTUpFdEdOSE5mN3QyUkhjZVNiWVFJNk9CQ252L1psNElsK2hjQTJoT0FuaUgwTVZnWURIamNDMVczZVBmOXZzSUQvMDNRNmFsM3JicUhTME1Benc5dTdNMTlLQ2xBT2pybXZWUFNVaGF1SlVFVTBUZXRKVExhZWg2dGlTc3FXL0lxYkpZeGNuSmRZNFdmWHJMYWF4eko4MHhCbE95Rk12SUxRRDkwUm10SFhBaGlScVl0cXRJV2d1RzhhdGx5NGd0VWFTM3phYVlaelZjOUNvVEhsL1Buc0YxbTFBZ1BwSU1kL1R4cmpvZGlQV0d2VVBZMjBZS25BTWpBbVVtc2R2ZXdCMU5lTkZPNWxCV0RPZkN5WFNBTkJDejk1MnF5N2ttSmxkSlVRa3BDL3YzR01VM2hxODhxRE1tR1lXclI5VFJOd0NBUXdKSEJKeldxOEx5Q1RZYzNEWFJOdWdCbWZFMW1vQTBLdjE3SzhGK2dpdXB0UjJRbUhqZ2h0RWY0ZDdSV1J4QmR6WHBwU1hjc2t1c21ldjl3NTl5dDJUeGpEV1dUOWRuT1ZaSUFjUVJHcFo1Mm5ySjZNeiswSXFuK1Z1dE9SZUVOSFFRSXZlclBDRXgzWWtqblI1S0RVMHREMVBMY1IrZ3BDYWxPNUdPRnFyanFHTUZiRm01RDBaSFJVTXpPMGM0bXVLbGxLVThIaGJJdU8rZDU4RUFpWE9RR0FkKzVGL0pONjBlVFd4b2g1eXFSdXhpNTVZR1RnNnJZUEM1SmI2YjhLWUZScHpiYXFWOS81TGFuWFBQU0V0eldBdnZweG5vWHRLNk1nang0QlorWHV1TWNmZTJKdStpcFNoNUN1eWtFWG1kNFpydlNDNGxnSGdIQmRZMGUrcWRWVEJ2SjNNQjhVYlZhTEtMR2prSGpVRmdtaHdVVm1pWWVtTkZLd2lxd1J1Qm1RSXAyaDNDNGRvbjlCdWZLbXpJclNpaDFTZ0xZVExGTFJndmdITXZEcnNnb0VNUlEwMlo4ek83bXhuOUdIem5vRnViVFpFR3p1SVRGaS9NajVmeDFxMnlVNlZWblQwMmlGRGY0VnVFS21HNTNEcnAxRTJ1SjVSWC9mUG83YVJHYis3Rm1taE9Fc05nNURNR1cyWWRTWCtFZUtQSkxNQkR0QmhGbEU2ZGQ0V0xqUVhzUEl1NkNOcXpidS9PYXZjemgzQTFHaHpvSy9LbzdmZXNpcERQY3p6TEZzUnU1MG0yalVJTWc2NDBiVE5vcFlSSm5VVzFwNlhvUVpHdHJFSUI5cmJPQXlScTV0dVVaWEQvYkZMZHpQMjJwQ0RwYzFBcDdIK0lpaU01RStoeUNtZzQrMFJlcGtRaFd1bHdYUWEyVWtZY0RLQjQzZU16anB2V0NrREcxUlMxTmR2cFBkQXJkRktaRG13VWpuWDBMVzBWOEdEQlFXUkVNczIxUDh3bjQ2WkxxYklaVDVQWlg0bHJqNmV5R2NEWWx3K0RrVks0RGZNTk1HRkhLbEI0SkhWZ1BFRzVXL3k1QTVLeHRpZXR5djF0QWllOXFac1UxVWdDazNENXUzZG5oZ3dCcXZYRkI0NEJ3OGJ0ditIcEtYVTIwNzJwMUZtejdnUCtJOFpsN0lwUWRmVDNoNlE5L2hNMlF3SGVuV3lXREZORGpjRHIrVG9sZjg1Uzl3UnNpc1plVEtaZmpBZTUxc3pIRWw4aThBelpibWZId2d0clFVclZjb29mYjg2U2ZGbVhEUjFsTHk2T1dSQXJPOWxxK0U5SlFXVERzamdBWXZ4dnplV0xpUERnRlZuZTZYNklKdlQrZDAwamJSTmhqWkxlS3JiUUxWaGtTOUlSWFJvM0xGMThNbVdiR2VvSjhiS2tHOUlTZDgrTndpV1FyVkdCclY5TTRvVVVXZk5RVGhxNGZtcW1OZm1nM0tobHZIcEk3cklDdjM5OVViSkhDTCthTFFMUmppQzhGb2VrL3A0bmdMN2VGQURrTVdIM21YMW5oa3F5Zll2SHpUWnFmbnhJbG1zWEtnakpuMEc1TlJjS0JkbXlvbWk5UGluajNNV1hyMUZDWG5HOFljWFlkNWlJaitqUEo2VVZzc1F5K2hvWndrVXRZMFgrc056aC9VOVhsM290MnlQWWFSOG1LVGwyM0ppU3dQdGFaeVJzaGZ3eXNaV0xpVzVmMHZKWVo3NVFsaXBrRTB1U1NiOUJiemNEN09QekIvM0wwWmluQmhYZ1pyRFBaT3ZheDIxTkxucG8rRnd6elpWSlVIeHc0STdZL0lGbzVoNDBVRFZqenY0UkgrdjZmb0wzUzBOcWFXOUhzb3NxNjJZMk1JaldMNnROek91bkFiaTZEdENkeFdnWnpwZ2tkRXpRQUJBTHhQeE91QWswVS9MSkRhOUo5Q1VtTDFNSGlpUDhlY3JmdElLNExSZ05UdG9aVUFqQitQTHkrYTJid3lkMFpEKzViSGI1bURzZ2o1TFRNQkhUR2o5M3M4K2RRTVkvZ01OUXNHVW9mWEJ6Vzd3dzdOUnR0YzVCZlErZTMzalJrSG9wLzNCeTlKSUpLNlRJdTJVS2xBTnByK2k2UGpvWkFXakZ5N1JMVjhkMUJ0dldSYzZtejlGbjV2RC8wMHhia2JvS2hpR0dkZzJtSTV4SW1oRGR0bnBmSHRmV1p0MCs3eEN1dXlETzVwbHRTNjBDb3ZzK28vQk4yVVppb0NudE1pZlZiTDg5bGVwVWlYMXVIL05FTWVsM3ZXVmJXNmNpKzBFajg5eU44a28vMkhZNmc4RVE3N3VYOVJ4cHVmZk5aYzQwMEtPZFYwbGlIL1J1VG1DTUxpU2dPcS95elVMVTNMb1FWdVJmSWdYYy9sdHpSR3A5YWdyajJNeEFUSUdld1l5eUl3blVRZVprTFZWdmxoWXlOSkFoTXVHcC9YTE9rcnBhVkhtaVRQRGFXUndWTnpxUDhNenNqNGQ5djhvK1BNU1EvakovSlp0RE1QakRmZkJUTEpaRWFxOFR2SUg5cXoyZG5MVFNPNGZjVVN1SEkvVzZzVlRKWVBIYld2NTJWczNnM0pidU94K3I5QkYwczRqbk5PMzA4Ky8xYS9jS3oxcHBWZEx6MldsOTY5eTJETktNRlR5dWxlejFkZjVyRWRZTjkrNDZDQWhnLzJIa3E3WmM5L0NDcWhjT1JuN2lRcDJPc01GVmgrQVloU1VPNExteHJHN0tiNWJGWTRkckFUUHJTQUNyZmFEUCtndHQ1ZkVGV1dnYUYvZDczR3pjd1Q3MWR2ZHd0bzlpa1ZTQnI0cEJaVkcvd2FDSmQ0MmtwTnlVaDhYdFZ3K2FmRjRMM29rT09qWUIvVC9mL29YSEZsSk00bHdqVFZOSE1HZlR2Wi8rWUJHeTNXeVZRZFhmT2ZNSzgzL1l4aDg3cVYzYzBjV1c1endndmlWc2FYdGdoTWp0NjBqczVzejNzNVo5cW92OHpJL3dMcXQwTUJ6SFlHbnJlRFVFV1h0RkhjQlNPWVNSSStRN3VINHlEeG96Q21uZGd4RkIrZUZBeXhwbEdXVTNVTEUwSnRidGRsWUd0cW1pUkx1UFREK1R3UW8xd0RNMUtkSEJpVy8ycldOVHp0VTRNQmZyOEc5dkhROTBkeGh4Um1xK0oxZ1RscFZmaFRKV0x3R2FORUQrOVptU2NRYWVkbFVXWEhoMWxiK2lMZmxSUXgwUW1tZ2pmdjVmd2NWSVVQOVBpQ3djSHozWHdWcWRGVU9yNStMVWhBRVhoMCs1ZUxCVzViVStlWnVnUGxoNisrWTVZaWdIc3dBaXBpSUQ4bkV6Q1pMczhwTmk2ZEloN0NCZkN3RFNGMGUySFRzSGE3WjdmcDN5K3FWTElLaC9mMDdMUzRPVVc0US9Zb2ZUMElZaHRBb0FlNG9MdGdZaVdTVEMvMXVSQ2V5eHowQnZSSkRtMC9sSUZ2UnFVbEdndkMwQ3dyUFhpeUM3QW4xSDA2ejlPdDFoblIvaENHWHhFOEJxQm9IZ21iMnNVbW5nVk9qQWd0WnJLOVZ3K1AyM3FUMGd1RXJUcERHa2hFQ1F5YTBhSWpHczh2Zi9OT1cxdDVLUmFnOHZuU1RxSXFlQXh1TWRRQ28vcEFoU2czTnp5SU14UEh3SHRRSVh0T1h4akRJL3JPYTNuUllRYnk3TmxIVnJrYmtiWWM3RlVBZ2hWZnU4MnJ5bzR1dTk3aVFCNVUxWWh3TEVJU00va29ub1c0TUd0cmNaU3pkYVNVNnZLMzBTZjJLVzNWWkt2RzBrK3dBZWVDSTU4UlRvVXM0czVYRTI2V0Z5bWM3eHlOQ0hoOElYeUhLTmUrUTVaM2p2K1AxZWZtSDN6M0xmaHByaCtPellzNkYzNk0yMXIxSVBzUDNNb0RQZFpRZXlXNlJkSzZxL0pIejJEVFE3QU9PUGlsTkY0VU5pRGhxL01JWjBUNUVmQU5QVER6VXJnaDBmTGJ3MXJWS0JQdHc1TndzSWh5eHAvYzdETDlEM0dZZ0FtWDZHZm1xMVhrSXJsam9HR1dQWE1ud1RsNkhnY3pOenJYbUdpcUFUa1k0UERvZU1xTXlCSjBPelZTcXNFbi9lWkJKV0RzR2hTVzlwOUlNUFFXcTV2Z3hFOTJMYzRDT1B6eWhzMldDdU5XS1pNMjZreFRTWG9pTmlrM2M1SDVTZzJHR3ZadGJlaTh1QTJyK3I5S2VmQ1VsVy9EVFR3LzIvYitQbXFQdExYdUhyWW1ncWhCUzZia2lhMnJxTXFRU0V6dXpCMDN2QjBLNFc2OUFha0VIWXVQRXY4c0hrSmVtODBQa3cyVDhzQmRkM0lpTnZaa25ML3diU1FHSnpPdUFzc2RiOE1kSkRQUlZ4cjI1WFRMdU5DUG1yMW8yeHRTOTZwVkFJYS9tSmRYcHRUbG1GbWJEYVRZZ1RUSFNuMklHQ09GQXdHdWE4a21QaHpKaDhGbTBkK1c1c3NYV3Q4bzRtenFUYnpubm5JVjhJWHdGMjVZWTJxU1V1elYwOUZuOVMrWXRRVDNtTzlsek1WVVV4OE9RYS9ubm9aUzZLRkY4K2p5UE9GYUVZSXd5UC9uSU5OYW9odk04VzN5TUhtOXNzY3AzNlMxTzVqUkNTcm44aU1MR2RHZk56bWNzd0d5Qng2RUUxdGJuc2pZQ0NqMUhoOG1HdWZwQ3pSTDZON2pPRG5UMVBobE1IV1FFTEF1a2c2c0RjTjA5N2VOVnhKY3JFc29qVStTTjFZMGNXY1FzWEVHa0lkTXZUelRmMWRkYmowZ3Rta0RrcFZQVzlFM0w0Q1BlK3BCNjhIZlpLTTRSOHc2UjFjSHVaaks1TFJmQW1NUmZIeERvcWxxRzNsOXlMZ05JTFZYcExOS3JWS2lES2M5S2UxN0Nmci9PanIrZjdHelZnSXlaU0Z3eWptZ0EwNnZpcStGQmF2OUFyTTJybXVjY2o0ZkJ2R2swYnhyeXVuTlp3cndqbUc1RkFiS3VNeXBDdlMvY3lWMnIvTDF5SldZMWN1RU8yWERqbWkxVlRuUzNOSDAzQ1lWMmg4NGNmd2UxelJ6RytNQnIzSy9yNHIyOGlibUlGTnhhV3F5WldCbFQzVTRBRW91SWRuT0xXVGNsSmNrQnA5VmFlK1l4YnNjR2xPbGdrUXpMb0N3MFpzYTNnRzJrTXFsTjVDU09mVmhjUU4xMzBmdUF4UjJoRnpOL3Fxckx4TDF0eVRMajd1MEZhTnp3cGI3UDhXcTNUZkZNZWRSdkNyVS9uYk02NElUQTF1Sk9BMHVFQXFEckJjNlZiTTl1UGRsa3JLekcyQ3E0REV0QTVITUdoWXI3TDQ5V1dhb29UdWEyRC9Wby9MUCt5enFGbFRtNll1ODRISnJDR0lBM1VVRTM3OG1xTWJvdTVSTGhMR0IyQjFTcmVSNEk3enhSMXZtZUxIZGJoOWJITGFhbDI5NjhUeWxobDAxRHJzWit2T2JDUFIybytDWXVOOThFcmJhK3QrZzdKQXZYWFVpdG1BbG9QRUZJVlUxZlVUNUtiejIzQkR5TFFJc2dCVzM4YVg3Sk13dWlva2h2eVpwQXRKVDNXckdWUUdQeWNXcFZybmxTNWljc1ZJNWU4RXBueTRGbG9iQVUvY1BtNGhhMkMxWjcybXd4b241NndUNDZyR2ZORDIxVm03dm5BRW5ZUmN4YU01ZTV1aGZOS3V5bWs4YUYxT2pXMmg3aklDVzJQNUowVjBYYXVaZ2lnOHdXajlUdC9RV21GdkRqc1N0b2Q4Wm82MVFQc3JRc0ZrV2JkaDlVYk5zeXBCVlRpcGNaajJzS052Z0sxS1p6VG92TGlhQ1BPQi9EdXNhMEdVK0YwNHpGSENSNWV5NXEwYnYxSHY4d25qcTNCUlp4Y1FScm12RU05cjI5dEN3cUlGN1VGdEFJUFQyajJNWGY0U0xwSXNNYzRvbytmYjNuL0IrZ3BScVNVNkZDRmdIZlkySjArM1ZQeFgxWW1wdmZISy9NWmZPN0RjSjR1cmx6RGx0Wk1FZFFNSkZGcHgzOUdvRGkrdWx0NHhWWmMraytSVVFrQnAzalFvN3hTSVJYUFlRR0ZiQzZVVlZ6Y29jM0VwZ2Q4WGlKbmI0dzZqZWZlWUxFclYvZjBZQmVWVERtSjRhSjVuWXVVTXRYUmhqMDdxeCtkb1pYSTc1Z0ovWlFsdlFrZUthdGRQb3F5TkMzVzJvZEdHOHFLeG5SMW8rRGp5TFNPcllVL3dMNW9JUEkvMjZENFpXYzdhS3lYUlhUUm1HeFU4bnl2c0FEdmVpVVhMc3NCalNLRkx0UUJTTnFvVmJoeHpuUnAwWEZSUUZTR042UERJcDg2dGUwU3FqYWJvNmJSYVFCbm1BS3BmZXlWMzl0eFdDYmpvUG1UQ2VIcllJY2dINGtSazlSeUo1WWdvV1lLeGNCWE9IOFRXSzkyRWJIWGptVHJBdmhJKy8xMUJFS0VtTUp5VjFvQ0JQR0FtcFM1TzdvMW1maHdXTVFsVjhRVDJDck40M1cwWVNlaU5MYkp2RVQrVVFWWHNtUkdzZUJ6ajJDNDhRcmRUKzBlRlNRV2xEbE5UdW12VkVoWm1HMmR3TEYyY0ZKVmt0QzJOVFVpa09LSFVpSG93QXBLU1o2OUJaVGZja2plMmZtaE5tcXNGTnlDNnBCWm90eWdKcWJ1MlZuTDU1NjJ4UERmNWxtTWVPRm5GN1ZOUlliNEtXRS9DbXh1SWEyUGJxNUFCMkFNZG4zWmxQVzB1d1NsS0lCM011WThNK1l6T1FFUUkyMnM2dXNkSDAwbm5KSlYydExROXdtQ2swUzFVZWNYNXdJVCt1dkpXUC9FalBHQ1V2a1gzMFJPUDloMVNqTEV0YkhMcWtTQkZlOHllVFJmU0xHZ1AxeGlVdGhkN3FMQjI4MVNBZnQ0cTlITUZQZXU1U1hFbTJGK21YZ0pja1FMVFhiZGljaXZ1T2t5QVVQWkYyWHkySW8yc21UQTZwM2RnREtXb01zdlVzL2NYcWtvQnh1SEloR2VqS2dRWmZzT0tLR2F0ajFYaGdOMFVyZE9vdzdCOW11bExTczlKYlZNOU1sUStpRDV1ZXBNMkRkSDZ0U29wb2ZhUDkrekxBMDhERm9ucE5DMWs3c25oQUsvR2VEZFI5dGpJWmNNdEhMdTlONTROSHRxRUhEcXZiamxyMnRudmFNV3h1V3prd1JKelZOS0EvcW1XcFhIcXdWbnlrc28wQXRXQkRjMmJpL3ZzbEc2dDMvSE9MN1RXY2w1N096OFZQa3doUUUzRzlFUk55V0g5eU1wSUJHaFkyenZLbHMzYWRHckR6dDVPaVVpcVZNcjF3UU55UDBZL3FZN0ZsVk1IdktEM2JOdHpUcllpOHdNUG9ZVG1LeEdJTWRmVDc3N2x0RTQ2clFpbWltbkluRUtMOTkzUmhKMFYxMWNZdUU5dDNaWW9rTUJkWVNEM3M5cStNbktlRkdTdlkzYnM4bW4yeHlnc3R1S1lnNWU3YW56TlVDSmpDdEY3cGdqc3VxeTUxRjVkV0NlS1JZWmtUUkh4M20vcU5DdG1tYk5oWnluMjRyV2luTllRVlptL0R6bDBKeXZaM1VIbDJQalF5Y0I1MzhjbGU5UHB2ekNqNG10Wmo1RmdnMHdoU0l5TkdNN0ZIcXVzSW1rZTBYdm9xdktRVTB6aTFjY1RoYXhGdTMwSFZ0d0NBYVl3c2JoYVNQVDlPMlpsVG9ZdTA5bHRVd0hNTTJvYUxLOG9hTE16VzM5a09JT0NWNU1Nb3p4a3BCdmVZUnIrQUg1ZlBLTXRuSXBKNWd3K0lhakczL0tHQzFhZ3duM3NUZXJMejk0MVllZElvQTZOV0RoVnZ6bEZrQmxkeVJYRjVXNG1LQkx1ZDVLOFN2QlpGYUxkcnd6Zjloa1hTRWtLYThXWWw0YWFSM2RFWTRBTjRtS2RJTzB4eDRPTzkwN0UxaVhnMjRTOE82MFBJK1pPT0E2MTQ2RmxvWERFc3dLckw3TzFWRUIzNk0xQ0pVbXZlRVBTVlFieUVqcGZKeDVYWEtPR0daWnNGWGRKUTJ2bFZsZHNLSldBZmh6SHhCcXlDVkVmMkEwWTZhcndjV1Z2RWRGQm5YU003d0RPT1hOS1FVdVUrQjdUc2RleVRIdmw2czJSUjVEb1BTUG9kTFQ4WWhRb0FMSnZXZ1lmRThnd29iYXYyZHFjemxMa20zVTdFNDJMcmFZRzFxVGQ5TlFPOCtjSmtJOG81a0p0clJPbFlzcXBCeTlrS21wRit5clJTejRSRjgwcm5sdTFESndybnk3L0lHY0RYK0t4ckhHRjJLYW1xMW5LdHowVnBlWjRVbVk5N0VrTlhramhiNDlQS0FyeEIzR2RpMjNkOUsvczl1Ym5wdS9wc3RONUd6Rm5ESlNFdHpoaHZXRVRUckR2OXlnN3p0cXd5R3Zvdkc3dFl5K1ZyVEQyTVR4L0FxUTRhMFJmV3VCR3MxUjNTb1ptWXFTRm5YOXJ5ZURUU3YvajVUTng0S1V5Z2NrK3kwVFVTZFRWTDh0L3NuSjdDb0E5TkZCSmtrVXVGR3ZraWNQMENDdmUvWGk0MzZYSU1nMVdSbCsvWWRuclVuUzIvQks0Zm9abmJkZXRrR2ZvWDBzekRWaEh5SDF1MXRoZHFuRUQ0UDhSRlZORWN5dU83ZnB1Q00rWW43cFBFZFZiOUlPamVDNE1jd0Ric1FSRE9jZnhnY25HcHpWbXFkSHNvOFA1K21Jbm5EM29oUSt3RU5pQmVLeCtVZUN3OUZXdzJMVUxPclNRRDdGRnFvd25VTWpvSWZTak9PZFJBK0c2b3Y1ajNDVTVxSEZlUkN3WlpzbVZVZzFiUjFSMlJ6MUxpL0hTZWJOOEUwUkNaM2gvVmR4dEVEMG9FSzBmemtUTjh0My81TDN6UGFNSnF2SUJaRGUrcGRNdVY3S0JuM3l5RUpGR2drWU05enk3RElSeC82MElkeVBrMlhTWEdhTTJRakt0ZWtUeVBpaU1TNElVeS82TkJvTHdpUzNBVjlxOXk5ejUvMVkxa0pZRC9wRjB5REtOd0lBRHMrNVBYdi9LREFyaTBQTmFlSEY0Zm52ZVNFSS9RWWdzdDhPNVFWM1ZMZllQQWlKdXRBZXNHMTE5SFkyZWdEWk5uUVNDaXJ6a0pJeFM3eUhPQTV3WUdBdnhqTkR2bkhLSkh6T1A4YUVSN3M4U0xNMEpDQnFyaFpvWWNoM3JwOEQvOXlnNjBXQmNtdzNrc2QvVEh5dURxeVJaSlBKczhtdXY1aGlyck0zWUI2N21USTNWY1pLQ0IrSkJwWGJKOTBzaXBQejR4L3laQlFUVlg5ckNNYUlnZWM3MzVuc09CZ0hXRzJZYmczakUzYm9aR0dRbTlqdHJ2SW85WnUwMTBRckQyUHZKTzZoNjhiOHdxOU15MEthSmE0RFJ0UzV2U1BCTkIwUWRWTDdiWkNoV2xQYllLc2NHeGIzVGFBY0RraE81bEcxV3pPNC9iMG5iWUtQT1dsV3ZGUE5YZkRIVUVEajB5TTJYSWl4WFNuWGZwYW9yRmJDUXl2NDFWSlJuQmlIa21MZkxtTEhjNzlOcFRZYkVjSThHdnZRWkZSaXY3L05rU2toRk01L2pHUDM2K1VGaUNTdDZlVlZqSVZHd2pQZE12UUZ3VTlnOEhyb0U2TGU5eFZwMHhoSnJacHphL1AvMmxUanRVNzA1YjRtRy9jZkgzRVg1SGpsWjJKU0gvZVltU2pNeFdQUUdMNHBUbCtzeWFuWG9yMEJSVFVGWVdSZ2dQZmhIakJaaEhjaEFQZDB1bFlJc3VrSnRxTi9SUXNlRlA0UzdwUyswM1AzTWN5N05uMjNwNmVYRkRyM25udHo2U2c1Q0sxUUdXeEo2V1E4UGJrMHVwSzBGUXQ2c01LRFBGZ3hVT3FsUGdsYjNkUjI4c25kOWo1SHcrSndsQ2ZBZlRZcFJSbVg5cWVIRDdwRG5QNHByZk5lS0JtV29QRDM0eC9oWk82RGtSWjBKT3prUWthSkxhVTNqdTNET2lTbzVqU2lONkpGc0ZTb3oxc2pISjduRlFXbHZ4MnpqeUFLRVkyOHpSaDkxN2w5aVJBK2dETkZSQ1Qvem9IUzJHV1JZTlNiWHZUM3dHaGVxTkllRmVJZ0xlaENORy9pQ2R3UTYrOVh1NVUzYnFKVUxBSDZJWHFVUmFRalp0c2FwMktCZmNFdGMzOU5pdzVLcmpHaUZZZTBPRmIvcHp5cWRPQ3VDTk0xQkt1dDllK3gyRXR0eXYvU2ZxSjk5dTRGcy9XYWhoaFl2Z0dVNDEwVjBscHdKQmhhNUlBRU9pSDJpLzFWMDkrMkFFc1RNeWJTWmRLRDdPRnFqcjJkK1hzbHppNGlNNFN6Rm4xSkE5T3NmMERocWpnMnA1SXpvR3U2MEMxUE9PaUMrZjVQWVQ4NHQrVjVWb1dEVjc4dlNwS0J1NVJlWlVTR0JrS1hkalluaVVHOEh2WGJPNDk0R2NrbTlVVkhpNGN4MzRyN2U4NFFIVlh4OVVteGkwSmdHcko4Q3F1UGo2MFhyemhhQzRxUDhaOVlhRmszQm5TY3UyR1hOcGRXZm00WjdBQUhaRGkxOWNLNE9LK29NT2hwaHo0ZlZBeGJSVkY2RHFDSnE1Y2VFN3NramJBTkQvVXJYNldmMXkveDdlVkRyRVpxMnRobjhCOFZHUEtGSFpIMmpyTDdub2tYYWFkc3lrSTFDcG9SYS96bXlua2NDK3h1aGJ4MGY2RnYzSGtTNUdqK3d1ZlFkNVVKREJWQ2dSN3lkellZWVZ2NzFKYll5V3VFZjUrNUJvQjdUY25vZC9mekRuK0trQXBqR25HY2dZYk40TVlERUVEZGV3bUEyLzBwaFROenI2VFhIdWk3QmgxS1B4RnBRbHhvLzUydkJMb0ROOExWbFhDa08zdDRZMHFnOU9EeC9pNmFuUXFXbzhGY1g4UHlqRTl1emphZXlSN01CNG5NOFJrMDB5RTFLdHRKL0dEM2VjL3dvSnh5MkZGYkp3NE1BeHFHRUpJT0c2UzhnNmZ5UlVNeWtxNmxoVHpGWGNBUlA4aGZaeXh1WVlaVkdjcEIxZUcrV2hjYWJZWCtBRE5ON2p5SXJIV1dKOGVzQWpsVDlBY2RScUZRQm5oK2EraGovUENzMTAxdHEyVHpuNThwOUV4dDB6ektub1dESzBYaWUzL2VaWWJTblVzSlhsMHZGSVArQncyd0VLZHFURzZ3SU1FNmQ1OUlkVm1CcWkrVlY5TTh4ME10SWY1ZjFEc0hmWERWK0ZsUThyZWlOR1Q0dU9RS3Eyc0tQR1EyVWNoSnpLMDFaQnR0OU9XQmNyZnBKWk9SeTFXUWRRRmxOZXljK2wwNE5pUFR6b2FVbmJqejk0WlVoSXEvWlFjTXljcjRNSUQ1OHlFZ0RjaDJ1QjVHK2lXaHNiSm96QnladTJOTWtGWjRmVi9uZkxra1ZsL1ljS1JQZ2tsYkd3enpDU2hVUXRHOUFMRWE3eExXL0dDbVdGUitCdXBPMkNEMnF6UlNTSm9vRzJCOVZuR1NCendrMEQxN0hUbldhNzltcEZYRm9La2dXak5odGtCa255dHk1OVdmQWxMNkpVN0sxQWRXNHA3bjI2a2lSMkRXRStkMU8rQWg0VzlrOEI2ak9FTll0d3laMW1oQ2hzT09qSkp4RTBCWmM0Z2NyaUltVmZzZGtxbFZvbGMxS1hFV2RaNlg4QTgwbURySGRWeFZMUzRJY2lBTEUvenoweGNSL2pDRTBLVitQSUhaMGJ2RkppRG1PSWdVU2lrdUF6ZHZ6SkRqYms4aTY0bG95MHlpSDVBR2FibG1FTk02UkhDRFRSamRZQktINk5qYThURG1jcnNIUG15bWFMdndVTXREYUxITG8vMzA3WU40TURtaUpYalhNaUhvWGgweTQyTEszRlErZHdWSUtoT3FBTC9hQ21Nc1hTVzk1TFh3SEVyWG1rcE5NM3ZvcksvOUJ2UGZtL1pqek9WTlFFTUprT0V1Uk5pbUk5RjRIRWVLcVRjL09DN29lTktpd2UwOWRMMnNyZU1LcUhjMEZjbW1BL24yVC9OZ01Denp4YWlvYWx0aFlnWGdtN0NBZWJON2JZbzdrV016OFhYOE5GdHNMREtwY3dIcHpJN084TWcxb3BCY2JJUG8zYVpGQUJHeE1RSE4va3BVcU52NVh2MXBXRUtWbS85OGxsaWZUU3RWeDFhTEpRbjdZZjNFYkM5Y25mMkR4NmJERG9sbTdtdFdyaWVNMjVQYlVIRzVOYnQ3dFJGSW51SkFYVlFWR2ZaZkdRZldzYmtydDZydlFoSjk2bE5lTjM0NkUwdG5EblhnVjdZZ3lGQ3NoeVZ3TkM0dUZxVis0VFVZVmo3LzFrVWppTUJ3Qk5QdTZJVkxsL3NvNlgzTG5FeDBiZ1lrTUluK2RNWDJwOWE3NUxHU3dGNnAyQUJjM25FKzhmWG9mOGFjMnZRZjlkVThYQTBzaE9idWhoZXZjSCtzK09mOXllRUJMTGhHbjFVbVl5cVF5QWxrMlBBeEk3bVhHU0gxUjNQWEtFam10Y1UySkRTNXpBcUZwUWU5TXphbU50UnNlUU5MSW5WSEp5RUFFOGpNM2NNdFdwczVhMnVvSGtWeGtwWlFlZ3A2b2hWY2d0TllsV08vQmlLMzFJNTd3bWtjd1NMcW5pNEFibVlNMURGajlrbDAxdTVoQUVqYXVWUTZoUjY5T0gyS0ZLcVFSRUVzZWlkc3ZiYTY5KzdLcnhBejBaT0c0OWVuc0VYWUNscEY5T0hwWjZUc1BtYXo3TzlVT2tDWFF1dExFdTVDVHNtWmJDRVpkcnZ6U1NINzNvTkI3VmtMcXBnMTBMblhsc3YrNFppWVJmVzRXNjFCbUM0TG81NDd3VkJhdWx6MWhQeWYyMHNRbk5CL3k3amdSVitCMytPbHdlNEJFdFZGazRrSGpUVEptc1JVMXd0MlRwQXgwL0VOTHd2ZUoxeG5mZEsrd3lxdnNwSVJ6eFdUT3dYVjRUZkJrYlNidytPTzNDTzBUV2UrWTNKL3ZyU08xek5BbGUwT3ZEZTVWSlMyK1BXbERMWHoyMTRFaEovd1VNL3dEalhZeUJBMzdaREtKRlplSmFxZk1ySXJQYmlCckNkcUI5bWdJVkdRU29zN2xYTlhkaCszSE1EZDFsaHVuMVhoa1lmZEFXUW5wTHZyN29rOGdZaHNid0hPMDNiMW5HMWV1c3dZakFqQmthN2ZvYkNJUTMvVUl0NGZDbjZLd0x6WG4ydUt0UkRoMmo5Rkh6TnlxWTI4RWtHQm1MVHNCTVVUWERLK0hYTWhtVEIwaHcxSUZXOEppSFdBa1A2UDNaNWdrTFJIb0RQanU1KzAyLytQK3AyUWdrQ1FhY3NaN0o5aWM2U2orZElQTWxYY1A3WDdZOTNBNTVkbzZ3SjkxTkpaZ2dWeGpURWpXR3VKUnQ4Mmg2cDJ5UWNHMUhaSWt3emlUUlNZWDBaRG1TcXBtWnlKQnltYW50NEU0d3JyQklDQXFUaWFMUkdoTVZ1ZnZrQWhIUUpmWnVLVExrY21VeTZSVWFFTmVURkNlbEtHR0U1YkZaR09iMEkvcUxEaEdBTWlYdlBnNWc0elE2Tk14c2hhMWxYZ0xxaVRpUWQrSG1TejBDZm9Ca1gwMmxiaEs2cTk3N1ZmeUJMYTdWQmxUclJNcWVSaHJ5RWRjZ0JGcTRRQWc3Z1lKUTZnREkwZzBEbnB4dnZlUnVxMHdwcGpaQ01sNXdvT2ZOeEF1d0hQaTJ5TDZMQ3lPVmZGc0w4Z1cyUmRHbHR3WkdxOEc3NG03VlRMczZJckhYWnY5VjhvNlErOFR2SEF0dWdZTnZ4bk9DTW10SUljTE5mZjVnREhzOXh0bDB3ZFE1b3UwOUcxYnAvczFwWWpPWEhmeFplUHppMjQxMG9ycEZ4WkxibC9sbkRmaGptdEJYOVFPNnVjVlJBaHlhVmxqUVVPYy9nNVIzdEhlU1hjK21QOGw3WmEyck1FbjNSMVUvVDB0ZEN0UGE5cVlCQlA2U0x2OEhtYmJpTEdWTVlBYmtMZnE5ZUVSTTVnWHFkVytDNjZIcXZ1a3lSRnVuQ0FINk1acWNuZitZcDFrTjdMWEhsekFqaVpOOXFjdytQMnZCZUt6YXNoTUcrckxrNWhsOGFISlJmaFg2dno4VFRjbCtVcStZdFpDaWZPbUYwNGE5eFYzZUZmOW9wL2wybW10R1lOQ1hHVkhneTJZTVlsY0ovUE9sZFlscDl2TTlQK2g4VUdYQnpacGhWYXE1OHVEWFlzT1lhZEVraXBxdmVscTlMWUpyd3RyTkRjRzIxYnR1ay96SzUzRVdiL1dya29GQWFyamxsTGUwT3ZIVjN2NlFrUFBjbExKT3V3WUdNZUNnNXJBczNQY281UEMzNmh1blRuOTBlRjloYnZ5SlRNdnZNWGFBaC9RNXdaaEhPNXJRMXVHVXpZdkpNM2x5aE5kbEJrL2djTTYrcE9kbFZqTWZYMk41amhTUGdjakg5SWU1SWYwaVVEM2h1M3R3b3hnY3ppVkdsa3RqU0diRVhEY05jYitsdEhtUmtXK2NZRUN5Tnl4SE9EUHR2U1dqQWRuZWhLZTBUYWVKRmxRRURBSUNONllwb0FzY3FGczd0bjljN3pWU0wxbkF5YkJqckpDR1gyMUxTSFQzbGNXdzdmQUU1bXZoNHQyU0x3bVpWRG5oVitHL0pUNGV2aS9EYy9YZ29udHlvOElMWmhWb1U4WHk1WGVCTlVkUjE1bmVDM3VHdnI0K2xJbkhYMUpKc2srWHQybHRTbmdaaEt1NENqOFpGaU9XSVFBLzg1UThVUEZ5UGNGVFFHMHhRV3RlWVc2N3NSV0NtVFZudXVMblVmTmtDbzFHODlQa0Q2YnFUNjdEMDlFNFVVWDBWZWVQQlV1VURwbzFRS2dXUXN1YnQ2Wks1aTV4SXVjcFZWTkt4cjlqajFiRHFDRWVFS1AyMnJpSFYwT1FwTXVYSWRQOHAzKzJJZVA5dTZzTTZQTGhZSC9lZUtLc2dNdDc1ajNod1JZWVpwNHZ4dGQwNUQ2VzFWcDhwbUhYVU9uakN0dm9uNE1WRHVMVGZhUUlWVWJHWlhzMmg1bjZjTXJmcjFNclNLYVZBZlNlWDVYTG9NYnRmNWUrRmsrd1daRHo3MDl6RC9URXppRDZLUjU0YzZXanBMRTZkbnBZV0tjTU05WDZrVUgxclFUWlhHOVJIbEZLZ3Z3cUtIUDZmYW1sOG9VY3kxdWNCY0YvOWZrd3pHZExUU1lmSVA5dFBCZ3IvU1FveEl5Sjk0T2JYTzFoZlRFTzIxU3VHZ3ZqSnVWeElUYzVSN0h6YlN2NEpJSHlEN3l6QU44U1FNaW9XaC9MSFZJd1Z0cnVsTmIrN0tZZ2ExeDFIV0RSQ0EvOHpLMmZZVzhYUWZMQU9va1EwQUtsaVg2Vy9GZDV0SWNFeGxQSmZPcDJzOUVlN3ExV1djZ0ZYTWJsN3ZCNjhKYkJQTTI4cGJvaWhQeEtISU4yNDZEVk1tdUFkSkx4aUNyN1ltQVVrS3Y2ZG5kQ0VOWkhrN1ErSjlkbzlnazhVczllVmhSUEpPTXMrazVlSnlTUktnc2NhQkV6dExCNzRza2ZFcngwbXhXVkpWRkNoUXppZ0oyT0s1SE9hekhOQy8wN05KWGw1YmM0ZjBqY3RwRWpETlJVVnV3dWJrRUEzOWQ5dGhmS0NoVWcxeDJjTnRHaVlBZ0VGTXFieGJZdGdmcVRJaTlpZEFSaUdmaTE5WXFrMk5hcnhlTm13ZUpRR2hRZ1NSMGd0akxHOUdCQVdzek9jWlFmbXBZZFB2WVhsV3JtOWg0ek5UNGd2V3RFTGdkalE5VCtnaklPMUZEbGFTSmpXREY1b3VhbnZuR3lPSWFDS0JFRk5sSzNkcDRETlNtSm9DK3ptRk8yN0QzU1lXVUJNZ0VTQmZERktsU0pxWkZVT1h1RkpRaWg3ZkY5eGNJWVJkTnBjZUkwbFJ6bEhEYXN2VDM3b2RiQU1zZWkyS2VVb1VzTGZoWWNxR2c0eUlzaUcwWFVOeXU4WU0rWTZCVm03WnVrRHZoQ29KUkl3d2V2RGFIaXNpOENxYWFueFUyMUFpNk9PUllOdkJOSmMzYTlSS0lYMDh0ZEhBL0hHV3RIN2hSQk1rQzZGNnRwVmVXNWU0dS9HQ3B3WmJrVWZFM1phdTcxbjFENy9FTjJ1YnZDdkxyT2QwOEtEVk5iY0hZVk5MODFhZGg2U256bjBmK25Ja3hjVkU1L05rbmFmUlBsb3RMMFBrcy9nOWdPL0VxUE8wOWFWU3dFK2IySjd0azJBSGU0Z0ZlblR2OGlYYmhESmlnbm9RUjg5bXUvOS9VK0NzUWZmcjBrKzBYb1lwaytCbFlFbFNmd3FqL0NMdENHdnBtK2N5dC9iaXAvVnNUaCtaRDhWRmlPQlROUy9BMFRoOW82emgrVndacUFkaTNhVHR2bjZUQzJIS1kzRHlJWDY5cXhKejVvaTF5T2R3UFJIME1NR2I5UzBkaGRSZjcwRkwzeXd5a01ob2NVNFYvb0N5UmZrLzBKZE5sbU13Qlh3NU9mdFozSzhmWm4xS0dtcnZTaHJIcVNydDJ4L1FHZ0FsOGdpWkNpMG9CckdOVEtUSExGSlJIb1pqWGNrSnV5WU84b0FoUmx4bFo5Tzk5ZzV2b0ZIUkF1cE90bGNYSlhiN3Qwclg4dVNQUjRzRG5sV3ZwcDBoN3hGK0QyUTlteFJtR0k1QlluRGFnRGdIOTFEbXJ2Z2lzTzk5T1BQQUYrR2ZLKzJ0MTRaR0NHQ2JUZkN6Q21RdlFNT2twS2hJYmVVYlgzZU45UkdiK3Azb2JQZU5yZEMvVklFU3dpTVFwdjk5WUl6cXRIak02TzNQL3NQWVBETVVsdXhzT1dlOXF5V29RK2R5UE8xRVNQWEluUE4vcVA2bjBuU0g4QTZpZVRiWnBiRVVsb1JydmQ4bDR5Y1N4YWEvZzdQT1ZMK01BRlNkcXBuT3ZwdzJsemV2Y1NFYWxSUFltdXpKSGJKQlJwTStWZ3FmZG91dks4cWp1RVRCNTJaS2NOaU9zdU9jbnpQb1N4bzJRQkQ1ZGtyZm9NOVgwRWxpc1pxdTBINldubFp0amhLVVZSUWF3S1REK1pWeS9EaUk1d2Y4TVJwUkxpeXBhdW9VYjUrVUloOUxzMkFuOGFodjN1VnV6MlRIYmpjaU0yQmFaaGc0ckRVSC9uODBrSGFUalN5dEV5eFB1RzJCVzhwRnVtblQrbjBaakJrRDI0ekYwNVJndlg0QldFakU5bkN3NldJRnVwcVVjUm9selhLdEJxTTRwQ1Z0Q2g1ZHNyWUthWUNodUdYZjRuU1JQOGQyLzFiMEZuOEJCMTJmV1lJdEJtL1kzdndRWld2TktoRG9nRHh5cG1MaG1WeUhoMHVmZm92ZEdhK0h1WDNqNXcveTVOcTExYW5lTHBVL0xSWXFTZlV6NjZXMzhjTkhLVVBVc1lub0lGd0o3Q0JPWHl3QjZqMmFVQlRvalFlQit1b0pQOTVTZWpBYVdNdXVIVmhYTTIzcTZiVjl2OGVLMmsvTGFOUHpKaDNBRGtycEtRK1RIQ0RaNGpLeXJvR3BDMEFVdE9LV0dYY2VRWk8vY3RtNE50citweG9jYnhsVUxDRThoUnZDbWtsUStqVklIcnpjTWJFR0pENE5RbExpamREY1QweXozTnNjUTJ5MVkrWnEySG4yN0VyWE40NzVYY1JIL2tJdTJKSXBKTTlNSUNxc3NwamUxNldGTjZHNHVrMDhaaFlEK0R5WlV4Zm5LWVA2ZFRvdThZbDExd2ZqbUpuODFCVnlEOHlzVlk0ZDR4VVoyekxqM1NyaW9RK05oekNEck5JMTlDcmVURGJmYTl0YUc0VmVFbXBadGVjcHZxZzdtTERleVozR3VTbHc0eXZDTEwxMnV2c2NtQVd4dEtpRjEvaGl4dVp1Vnlidll4WVlFM2MvUG5WVHMwZmtqeHR6Y0VtNEdLOTFRWkd3cXNZaE9RTW41TFAySW5PZmxLU3ZFT0dXZVZnR1ZWL0N6SE1kQmorWTI1NEJtUmFEbklkYU5qQk9kOXBoOVArcVdIQ016OC9mb25NMHNyaVhrUU0vY2hqNW5zTzA0UnFFU0g4OTIxSWZZc2F0MUZaSHN3cG1WOHdpamd3VGFiOFNhTzhlVFBrTWlLMklZZm15NGJBZlk3c2J5UExPSjFhN09XZmNDK2s4VHRrM3p6UkVXd2tKbEpoL2tMSC94QW1QdEZOVnpHc1c3aTFBZWtVVCtsK0xaYzNhYmEwVEJmbG9MQ0lWaW1RNm4zNFVjZHRqQVhYYUlqTysybVBMVndhK3NkTDlCdUxOTXFJTWRSY0FyUGxVQWk5cEsrWGZyK2JzNVoxcFpKRXZHTjV3Y0lZNEJaTWM1OE8vcFcwT1ZHOVFYTm9qTEdBMVJlTGZTdFlJczk4aUwrQVpRMGdEOWIvSmw4ZjEyM20vTXpvbW4rcUo1MTE1M3d2dWp0UDcwRHhSZmNxTmxwYVpnUEJVaG40MXNEaXJRTUhGLzlWc2V1M0FPTVhUcUJqWnVRNjc2RWZmcnZwbDRyMHpWL2tXZEpnajhtUUhaVDVDempoWmNuL0RHSkNreGI0a0FRcVRkUXhpU1pzRkNLY2lOZHZsTG1VV245VkNMbHFtQzBmY29mVXJISVkxOG1wSHdodzZDVEdrY2N1T1RKMFQwTEVTWjJxRUkxMm5zUVVQR0RibjZpSkkzamgreGJ2dmhGVWQzVmNpdG9jZk1OSU9tWXRwL0JjVlRwczNuR2hUdWU2dFdFUXpXTnNDQVZGNGIrWTdnZHNkd0w3TTczNE9HVk02dTdSZUU3V3N2bjNzREpCMVFPVy8yd2VjY09pdlgwMkIvZkpoZE04RktUTVdCNk5CU2xCWjdUUHpERHFWRFJxcU9lcVJ2eFVkNTZET0FySDNpbFoxbi8yQ2M1eHlCaFhibkxjWERLZ2krNFdkeGJmdmVkRGI1UGlpSTBDZlVOVS9HNnBZNlAvQnNjeFU1cXpUbHRkbEw1aVFpSUNCaU1PdHZVVVpLK0poOTBwbGtpQzRMSzJXVW4wNE00SmVvd0dSQmFsOGZ0T1U0TmlZTm5KSUVZV24yTkJYS0Z4WDB6ZWlMR3JxbHNEQVZSUDBUWUdnRDYvVkdRZFNKUUlUb1MrdW1ES092QjZkYWgxVkhkd25RNVdra2pRN3hEeGFCQldBMHBFNm0zcEM3Wk45bGhOdzVZaEQ3eW5UVlBDMU1RZUExV2h3V3dhSUVzR2Fuek0reW4vWVg4NTBNWC9WMXUwRUN5dDBEUDEzK1JvYjl2WUZ0ME8ralBTZGlFTC9HeXRkOU53d29OU3NMSUwzL0JwZ2hxcHNvSWhZdkQranJCcHZ6R2NmKzRlU2UwdnVlOXd0MGxCS292cEoxRThkTHNnM1VLVjNFa0xDQlQ1RmVZb0FjYkZpRWIxWkl3ck5MYnJOY09TYmhkNTZZOTZCbnBZaXhaaW9wWksvUDhDSnJTajUvV21xN0xLbVRDZzg0VmU1RHh0V2JUMFdQWVdTZlVaWUNDL05wbWQzMjZiVlFqNmxtU0VzYWQ3aUN4Nm5NL0Y5akNlVHhPM01raFl0S1laVHlLT1RmVDZ1aTV4QkQvT29SRzJMK2diVFYrOW1RK0QxK2IzUnozRk1QNEtySGpoa1hDU1ZkcXVWSlBjbUhUSWkwOUdEWi9ZMnpvU0ljSTFHZUkwYk5GTXFMTXhmTjF0REhmZk90Y3Qzdk45cE9leC96RjVNMy85OVpEZjB4SzVMZUg3TUFJVVk1RitoOFNYbU4zYm9EWC9YdGxiNTFZTEl2OGx4dFlXcGk1YWJNb2RPT05nZXJ6Nzdab3NkZDlHNmt5MGJLamxnZlF1U0Vpcm9DNzRVcDVxeGVRY3BmNHlEbUVGNDBOdWsyMFJNZVhzMDhrWFBCdm1id3E0SU1IZm9nMzBaMktIS05JbFJHclc1VHZXQndyeHM3YWJwQXZKZm1GOU95N3I3QWM5SHpoWWRwQTZDNFpTRlpjRjVnaFE2RHQ5a0hhWkZhbzVGYW5NNUNZelNRYzdwM3dQOGFJb1M4RnlMNHV6MVNYcTNMMDNkTjVGbGpLdXZ5MHVSRzI0VVhDcmJFaGcwTXNqZVNyWUNVSXhraTNvS1duWTVNVzFtZkIxNUlkRTE5N0xMQW0rcm91R09nWFh1SXZMVkdUa2JtV1V3Uml6WGozbmdmVXlMa2pCcmVaeUVGa1V2RFdpTHlsZUczYWpYN2RQZWtobGQ4WTlsTnhabkwzdkRsdm5QNEFoYWF3SFhTb0tENjFjQXR6bS9CN2ttUW9TeFVUYW9WT3pJQ3J3Y3VMQzBTR3JJTHhaeGlRUlBPbnJOTUJzdjRZenYzU2hIRHFiV1VVNjVheHhqeTRJaGJrWW9ZUDlTY1FSQy9GYnJjT0hIamlwbUh4c1NOOEVjR2x3T3g0S3JvVkFFRitJSUU5WnZEY3lRU3lJM1YzVGhtL1BUUHFBWkxjZG8rdXFnN2x5aWZEY2pqVlNGWnJTMFUxanQ2SXdBVEswd3hoaWU3ZUE4U3N5RHVkMzc5VURZNGtKYzFPZXIyVXZKbjZOZVBlMU5jWXRZWFc2L2dGYU9wYTVJQmFYS0hmWWgxWTdKSThYUGw2V2xZYW56U1hyTzd0UzVFbUFjTVQ1VEJMZGZoMFY5c3hXTjZLdkMzbVJ1d1EwaUsrQ2REeWo0M1lRc2xNNWVGaGwxV3UxY3BsbXl4UVQ4eXNiazVZdDRFd0o1NndxTkM0eldJaEgyOXhiWGhYL0E4bEVXZTVoejFsZlVTTGVEMGtTam5xYkx1K0lEdVI5NmdKZVFoT0crS1A4M3ZyOTFQSTN5SFljdU02Y2k1aGlNckM0disxdXpSSmJ3Y3VUenkyaUNJZGxibHhYT05FbmNHa3J6YjkzYUoybFFkWkU3dS9VVXY4NnEyL2h6Rks3WG53WHk1UlA5K084MWdZMllJamJpTHpHdGhFOWhOaFFpaEJvQ3poUU1zYzMyRE16V3BzYzVvbWFaeDBDWVJIZFZVeldZNUkxdTNJUFI1N3VZSWdEaHA2L3Q0eVhickw0MVhEMVJCckNMRzJDNlZjNXp3ZkJxMXRKci9NazBzN3p0SzRraSs4OU5wd3V4N2QzbTBwUytwRGNseXVFd3MwWWIrSUFGOGdlYzZNaXU3RkJaU2VIc3ZsSGhWUGdmZFBYYkVXWDR2M3lZeXRPSUJaRFRNQitSUitPSHhFUG5KZzJOQldFOTJPV1RDRHloTW5MNi9xdFhtaVlDR1NJL2YzaGRMKzcvYXJsQnRvTW5TNlViTlV0NGVzeXRMNEVlYmN2ejZyb3lneDNiektwOHVrNFRCL2JjNzgzY2ttUnhOZUdnM2pDS1RaMkdvd1hTRjRRb1hPejBjaXYyWHNxazd3M3BKeFVQWWROTzBPQWRrMUFzamVuc2FrNHg1V3hWdFRBYmFkQlhLRVk1RjRsU1dYNGtrUmROVi8zajVSOGg5S1FJSkthM1NIZVA4a25pVXcwVlJJNG5Ydm1zckJGNHViY3djZElPYUlxZTNUSTZJbmo2amtUc2xySEtDSU9jU3dNaU5tbFdtVzBRd3ZpMEV3WWUvZHNUdVhNKy85cFBsTHlxeGl1NnFWamo3TVFST0tibmN6UFRGWVpheGltaXpUdlE2UFI3cFo0VlRyTmV4UHJFSHE0VFd2Rzg2QWpNb0RNb1FZVVZLMDQxSTBpOVpITTNKR2ZqcDg5anhSM3FtWmxJM29qQWZDU3NHdVpyN1p2V3dZM0NNMXF6Zmg5YU5LRVhJUjFkRkg5aTRqZjM3bXNnTVJiZGxiOTlhK1cxS2k2d0RsbE90SEVrd3V1NWNpQTlRSEZDaXJDM01nbnJOMkVaK2o2UCs0b2lzWk1IK0NNK0xGNWcrRDYycVVzZnBGU1VTKzdGbG5xWDFuQXM3dElGQmRtVmJMSU5sM29Lb2RtNERPY1A0RHpNREt6dXVOalQ3a1A4SjJDVDhJNDJNU1Q3aUNuN055ci9tdW8rd0JPaTBtVWZpRUNndkRtUUM1bjhwOTJON21ST2NoMlliQjJBMHJCdXY1ZkthMnJ6bGxrSEJUN2F1cUVsUEYyTy9acDBZTUV6c3NSd2dONmF1NFU1NEhMZ0J0WFQ5ampISWFsYmh2QW1GVzQvUGl4enpFckM0d3lSaFNVT05iT05RM1FjeGdrck1kWmQxbmdpUThMS0N2UlJ3TXpTTndjdi9iNmJzbnNOTC8rQThaZ0hNUG1iZnYzU1JWcS9EZnd2L1RQdUVCai9TYUVTVnhmTVVTSS9RYVlmL1RpQWowYnRZS1RkWWRuTURYTDlSM0ZNUUo5RXZwVktjdENQSVlWOWJibW1kaXJDTms4SktlYVdPVHhYNzdYeVFzV3JOeUhsWG9zUW1Kb1FQT1F2bzBKNWdlck0rN0JUU2duMVNDQzlHSmc3cDR5aElLYTF1eEV2cThNZkFwVG1IWjU5YXl3a0NpS3lEWENCR3gyRmRnUnBmdGZCUjRtTTMvckl2empxdDlpMFpiSjN6Mzl3dHRGd2NXaTZ1aDdhWWpmelpPQVVtb2dzTUxiTitQemNYQ1RNT1BTQXI1NmQ1aGxPVnA4SnNyK1J3N1graS9JYTVlemxmSE5GY3crTnZ0RU5RYXlWT1IrY214aFFjUGxBSVBuVUZPWmQzdFJZY1pKQnFkNUlhdzVwUkJSN21GR3QyM1VjaHl5Szl0U3N6WkRMZlllMUIxMTIrcEE3VVpDM2JHMDNtTGtXNUNCREdySVp2ZngrU2VoK1Q4K3F2UkJKNGp1S2pzU0w5T2RNUzBRdjZKVG9NcEhNcFQ1dEJqWDJwTHNGYlVOVi9yMWl0UkpsTHNhVkd1SmxzNXA5OGxOYU4zTGhEcnlVb00zM0NnRFVZOVVadUhtUEhzMzEydGRJVVUrd0dvaHJmV3RId0JtS2hoMGhydWFzRThhazZHOGU0UTB6VjNMTmMwYUo4MXZNYUZFNHBMMngyeVNZTTRxQVFnRXVKcjhONDY2aUVXQnA2R2IxUnJGUDhhUUlmcFFFSFlHOFZOaUVqbjkvTDE4OExaUDgzRGMwZDhXK01CbDJhSHZDK2UzTDJvNnh3SFNZbTN0MGdCczFqaUliQTdyTFA1bXh6UlpLcWZrdzJ5WDB2VFZwV09WeWZOU3NCWnlOWkUrck5TK016Y1VaVUE5RDkzbDkvZVNEdVczRHNCem9pWi9KK3g2Yk0xZkdhTnBWK0hWV3c0eVpCcHRIbmdrSzl0VWRkcU5NdTYxYllQd3BzN3FrRTFGdktHRkdxL1BWVU9qMWxrN3czLzIvQTVQeWhDTjBJSlBjeWdBblZDTGZWTHdDVlE5NkUrOEhyNU5nOTdWSmhHYU5IbFQ0bmc2WENmYWg0UU1CbTVIMFpqTUJuZDBHSy9DOFg5cjZVaGh6c1FxVUtqZFVKQ29LdS9HMjltZjRCb0lKMFhaQlNobnA3a0dxRUlkb2hPQ2pTRExiQ3N1RUlZWGZsbitPenZZYVU3OG5XY1dJK2x0cWFtbmlicVB0VHRyc1o4U1BQdk04WFdoSHJnUEFMcm05YkJUVzdkL29LMGxSOCt0Z29TL0Rwc1EzR3dzdWNZd2dEN3JOR2NUZVBqUXMxK2pzNFB3WncxQk0yQlJpUm53ZVk4R2M3b1dtTW1lMmhDakR5S1VPM3pUQ2Y4K3dWY1I5aCticmczbnUyM2lMZlg3blRmY0tka1A5b1NJVmlOMS9ib2trcmlQaFpLVktnbEF6ckhDNXFwdStOZ293SnRZM0w0TWs5MDBMbVN2Qi9FNjVwTnBmdmt1aU42V015OTJMTzB1anF1VmpWSUxGcWY4aVFOWVFGeXQvVEJPNHlWZ3RmbVZBOU5LNzNiSUR3ZDlPRmF3R3BpdG5sUjFoODY1aVlvNzRZNGxGSFNUcDNNMU1kRVBXZCt2ZFBMSGl4dE1RNGw2UFFmWlF6KzBXV1NzL09RYVY1NFB5OHQzbHY0VzM0N3IxaHJJbENBUFhuL0VyQXN1S0VDVEdCb0trZXY2Z05NdXZId1oyNWgzQnF3UXNIcFJ0TldoNjM1T2VvbjViVDhsblRwcUtqQThPeGNqMm9UNjRKTHZNb01iVkF6OXdnUVd5MlNlM2JxU3F4SlZhaVlNbG5lT2J4RnhIUWhUcG0ycGNMYzBydHdwSlRxdWJ3L1FSdTltVzBkTDYyMmo5OXp1cXBUOGluQkQ2YU8xZUFLSG9NeGc4YUpMdDhTbnovaDVMaWMyQ3ltaE52Uy9jUElHYVloaitsRHBiVEh5dkcxYUxWeWd0TzBkZklTK0lYeW1yR25oSzFlOW54K2R6UTRuOE9USnl5bnhiYk4vQ2ZIWkZJcWY5aW1CejhYbjBPemI5MUlPUzVTeitMY1RsMUtIOWcxcmNsVVpUdnY3bVBOa25tc2hsK2NBOXVocG9CdnRXTDVCZm1QalRRei9Wc2ZGTEl5ZnRBL2FpUHpqNDdobVNSY1lZcm9QV0J5T01GaCtnK21zOFBDdEtleHpQTE1vNEE1eUVJaVY3RHBKNDNoVjNGMDFVWmx0VEY5MlRKaGpZT0xwemVMYlk0ZFRtTmp2aEM4dDlZNWZhWFlGVXliUVZ2bFY4aDZlaUdnaVBuZ2s0UllVdkJEa3l1S3c2alNmZ0E1T29NaUtRSGkyNUJTa3lKekJSRENORXpBODlMZFVDZkNNbk94MHpkWjBCei9YUElWVUdqK3pGMGdZSlZJSHR2b0t6enQ4RlNQUzIyS2hXa3h1ZnZub0MxczJ3aCt0V2pNYVNnaVJrTnFob3lpajQvcHgrU0pBdyt4ejVZUEIzY0NvV1lPQjg1Q29Ta3BPZ1Jma0RoMERMOUZ4N1NPQWp2STlBUm9MeVhLVmVGSVRBdnd1TjE3dHFXNzY2WWticW1FNHdmZDlvQk5SRmhEWFBLcnhuWW9GVG1oVkdUb0tOUDk0Z2kwajU5YTZNcURQUmJqWlZtNGg3QzduL3BJNU95dTJTd21mcXczMEtMbW9KNlNRMHlQN1h5YU9BdVhIOXNySzE4Tmw2KzZGb09kWW1OZXZGTWxRSS9SN21ta2tLQUwzM2VIUWtzdDVmWjc4OFVON0syNFBOVnkvMGJjVDZRTDhjRllzTHZibk5JZkdnOG5VZlZ5YXFRUW1yaDR0NFRicWd0cjlJNlV3cGQ2dHBOVGNIaUR2OEZmRDlqczc5Uy81UGN6ZkQ5VGYrZjAwM3lkZHVEZko5U1MrVDJlL24ra2xmSjZ2UDgvcUYvUDZIZUJIdjI5ZS9Xdjc5dm52MXgvNSs5djUreVF0MStzdjdYV0FVRGN0M0RrekFpZ0M5M2tCU1JpVTdIY3lLMGpkajlvK2hkVDVGdFBZL3lUUWxWd3RmT3JoMU8wTnBlVUI0dkc1V3JnVkk3b2trbkUrZjI3UDNUbGVwMWIwWTBtS3hGRkI3ckk4eFl4VXRhbUJJMi91eWN4SmRpY1J0aG9FY0o2K0ZrUDFpbWdRMnFMbmVDelA1TDZFV2VGd0Y0WHoreHN1QU9yZkY1WXFGUXFQRVpRaG5KbmdsakFIcCsvNlJwSUtoNjI5bHRoZ0ZKZzFnWFZISmZKNTFIbEFzTEZxaWNGdlQwei9mRFR4YmM4OVdiL1ZESVBRRW9mckkxREgrWGFHa2c5KyswZmtUOEtZUVJZU3Q0Tmg5cTEramtqWThpYW82ZXR0aitmNmthSTB3T0VCaVN5cWlXWFVERnZ6ZHBvMVAwRXhlZ2ZIUmlaTDJ6RlM4b2NvbitvNC93ajBSd0ZlcUxKa29wbUd3d0o4bmtIVm5Wd002RUQvYnRIZnJ0UmYwblhwdjlvVGVqMU5aQkY2NGpiemlxdFY5TTVudkl2a3NBbjlVdWcyQ2hTemtNblhNc2d1UGREdGw1QmsvdXhtbG1WY25ZTGtGWDhhNTNERTJkbXZJNitOc1FJaDhNQVN0aVNOZDRiNFRka2duTHZRWmpJK2dyM0NqdlBsVUNWQk5YWkQ5ZlF0VWZmeWRYdnpObEFpdXVESzVnNkJsS0dFWEtRODF5bTh4dWJFSUZtQ2NkYlQ1SlpsVjFrcklhYUVjRm9MMi9qVXJFa1RLSVdnTG51RGNSN0ljc1lqYlhpTmJ0S2J1VHAvT3NTQkd2TEwyU1RWTWt1eWk0NTlLdDA5RDl3RkJEaTBBTEZkRVhtTzFVSFRkYkVIZ1d6cjRMR1VKV0JGWTdXRGh4Mm1URGFMUmhGaEZKM01SRnYraU9YRVh4N3RtVTZRaUY1ZzgxU29nZEJYTk54WXZVK203UXgxQ3l5VkVGSUJMTGNodmR5VVNZYnJ2TE0xMnd3LzBzd3BlRi9tcmNxd0RYd3oxTUtwa1NTZTB0SVVIT3dJYXN0dW9tM3FJcnZ2ZHp4U0dSZFdOTjVMYjV2UkxTMEhVYzl4Y2tOYzhIa0g2cTBqU3BzMjl4N2JYbm9NWUl3TXhhUFRLMFBrUlJIcFRUYzBTdFBsVU44Yjl1UCtCTGlqZ0RqYVdsUEpxYkd4RWsyQ05oUkhKNzduRHhaNXFaU0JiaGRPamtacE10WUZHMlhjK2QwNDRmWUl3dlVHL0ZwL0tVbEJ5MUhGa1p5Vm5LMnhxNHVTOUkwbFZTY0dYNzhQMWxMWGtRendBaWx2OWtMZUhSY2xQQjQwZ2FlNTVWZ05vSUdPakxKOUl3SDdiN3pkNzJqVytLaHdpRGpDUVRTd2xHb0hMbXZ0czRFaFo2MGg4RFFxOWV2c3h0RnJVazF6S3RTMkgwOThla1cyakxMSXp0aDNnWVNCSmtvYXdiWEp3aUZ6Zy92SVpVcm5jem45ekZjd3ZRMEtiVVVvajJNaTdiYUpLSTlSYmYrRklETWU5a3pBT3pSNUs1ZDBCcUVRcnhqU0pCcDNHWGZPWEFVYXVoMUU2QXNWTzh3WXZITUVKTnlDSGlkRytRUy9hS0NLaEY5bDVvYldiOGJBeUE0WCtTTEpJZnBvRkNUZWwwWXNBNDdRb1F2S3E1SlM0M3ZPVlZ3SGlIUDkrTEppUjVvRkMxTUNxc01qVVlKdmNZdU4vT3REbWhWenNNRTdiZjUwRjVPY0d4ZkFLeE9EQlJwN3FCUjRXbkp1NmxXNjVrSThvOU5taXNTd0V6YWFiUFFtc1ZVc0xoWVFpNWJHcXZpcDI4ZmNCVThkbVRtQUFDWmgrZy9iMjJRQitnUWI0cTMyWDRidjllRWorejA2ZklvbElZSTNpNng0VDlqZkx4YnVDL2l4TXNROXgrdFgwMzkxcDFVcVgvbWVWWnVTVmRzUjhHUjFyK3E4eHRDWHFiQW83d0c2TUppYjE3VlRwTjJoMHk4QVBTaDZ0SEloTloxb0hwdHpVYTF3SW91OEc0Z1A0RE5KU1UrTi95M2hHNWxqTVhHbTk1cSsxdXE1UEZ0UUZQREFKRzZJVW9tUzNWWFdRL3kycnc0N3BTTWY1cThwYmx3KzUzWTlQNTIxNjlZTDRqaGI0V1JjekNqN09BODBlaVJBUGUxSFJ1NGFJbEZ2Mmh3SVhjSHIreEVuVnVXdFN6VzNGYjNUV0lnL3NhSGhIS3FrZmhKTmxaK1ptOFZhOTFKRXZYUEFQaTJab1Y3T3NlWUxkU09YT0ZSUi9wajZvUVJxd2VoQXZ6OURHaDVMQm5zcFZBL1pDZmFyZEJLbTkrSGJqRGpid2pIVWFWaUVueldkcC85aDgvT1RiWm91UkxVWmtSTFU1dzBmcFJzNGpzNFkzT2UxM0d4TzNnYUhTWGdaUGZZREd1VC9YZmdUSnJaeW1mMVllSmc3VXg3RUd1RkZreERYYmFtb1BURHdUZnNTT2lmeVNpQ3FTZmZqc2tHQUIwQzJ4ank4eTNOWWpnRCtXT0lyU0YwZWpHQld0WSsvZjhxanB1d1p0ZW9lbWhvdTlrZlNEL1RnVjMzZzZ1Y2pkRk9veDBjTHNZamwxSUIrSWZpbGFYMGhhWDUyRm9HeEp2aUdrRHBLR1lMUkJVZWFTajlsQ0hqV05mV0VHb0xIVzAvZlc1b000dml4eGIwMHM0VTE2enJqSWQ4aDN3VjV2d0dDMTBUNVE1TjU1NVdsVXR2VTFjSzNtdDV6K0wrdjl0TjJKNmF6QUc5QUFCczVKb3lRY1licDNNSlQ4Q3FUcVdyczk5aUtIcGZkMEduYVJtakxBQVlheXl1Qnl6SlozQ3JPWUhtRzdLSFl4b0NLQkR2dXRpbzNITFc3U01nMmdZaHk3WkxES0l6TjVVcGpNY2xkTlBMQUltTThLZHBZUUdWSnZETndRNGN3Rk5CeEg5bktHdml6eFo0TDcvTjd3TW4xd05ZT1djWkN3RmZQTjZIRllvUGFMTXdObzhvNVFYSXRWdk1rSEZMMTZoeE1PcmRoS0l0N1RYRmFYaDRXY2o4T3lMaG5ZU3J0L1FCeUV2UERXNUdzOEM4cDFJQ1JrQUs3VVNLcFBYMnhEc1k3M1pVVmN2UFUwWnQzNUFTZkt2OU1qYmJWbDlGdmlhUzArZ1hGNTBuaTFVaVl6cHo2NVl4VlJERlJ3SUxnZFZkYzEzZUlYUHRqN1MzTjhRMnpCZjE2aENXeXh0WGFWS0V4OXlCVlBHb1VVWUpHOUVZSnFqZlZROWpZbXFLLzJDVmlIZEl4Mjl4dlhBMHl4dGlYODY2ZlBBTzZXcXVWZ3h5RXBCME1xcDFRR2VZMituVmk3MWsxY2VLOFlqTXJlQkhFV3V4bTNsQzlFNXFKMFFlRS9jVXRhYzJMNkVGUnpuUGd1a3l0KytQUXNUSXVVbnVKWUhlT21WemJxU2NSL0xjMko2SmJKTTlyZnIxSTFVRVcyVW83MjM0bnhTZzVUVUxTamEvaHdkTWVuMXIzTlVhNUNyK2VrcUlUQktGaGQ4NGxWUE45Z1ZtbkRLaGJFRUNjemF0SUlranZkZkwrN08zOFAzN2hmeE1DTnhnL0gvSWozR1R1K3MvcmdhdnlLUkVNVzVUZ2hpbzVucTY5ZFhmTUxKK3N3VFJ2UjJHaTNadmpoeFdFakhOY0dwVnlvMHZYd0svYk9xNHFYczlvK0dJa09JS09hSmY0MVl0czE0OUg2ZlNCbFhmZkgxYzhvZkUyQmVvdlZkclZ4WXZYRzBTNVNsZnN3YytBTTFjbXlwZ0FUTzVoYklqZERJenM4UStHSXNvQkRaSGdacHNkMldsRklUSlo0QjVweXRCSFhBYnhuK2hUNUxPYUp5NDNJbVNkS1pXb2VOTm1CRkZnakdiVi9GdlBKbld2VlQ3a3lLcWJmcnd5c2lVVW0xS0creXFiV3hJdCtnMVhlRGlkWjk3Qnc4YzZoY0pBbnR0MWJyZ1ZMUm5xMnpIaVlzNkJSeVVpcmwxblloT2UrR21XblJYR2xERkJRc1pYc0pZck9MNnNDVnQzUW5iUmx4bEpBRUVpUmxUTVh1VlJINnFyemZ0OUd3OWVhS3dpV0ZBd3Q0dlNROGNkUW1Iems4RWN3TzB5ejZ5UHJDK0pXN3ZITkpMamVOaW95ZTY3MDBCYzE4Q0dQeDBjb0FkV1hTK3hKNGdOVmpoZWpzbUFjRkdxZTJ2cEZlTnBmVlJUQ1RuRVE5SzIrMmdONmxtSFBDZllqTkhzNGxyZDBwOVpMcDBxRU02OFZpTWNXb1QvU3oyZFRUMjVPYkhlYjVaajVvTGVkVWpHTlpCNUM5aTNhTFVZTmVzRTBXRXp4WFFkVGlPeURFbHY1Nk51emV1WHFCYVdFeGF6UkFMdHNNTVlNMEU2TFlsMTIwN3RuRzNVTkw2SHpEM3VEdkRNZ3JxMTNWQUhtTll5U1Q2UnRKNnVEWDJvZjJpb0dCTFBwaytyeWttYmN6QlR6aEh6c3pJaTFYNkNnb1k1ZThRbTFXWllSaThHTXEyM0FOdFNleFhpNm14RklDZ25PMy8wcGRxZDJRV2t4RTR3aHlUeEVLbkNodjl0b0ZBVk9scmNKNjUwR1pma0ptZlRrSUp6Z0MxRXF2bnhTYjJzUVFxb0dDK0hrK0ZPb2RIZ1NqQkNCV0tIR3lHNDc2SXZRNTNsQVUrSUtERGhFM2l3RTZDRlArckNqVW1PdXBIeXluNFkyZkRCbTJleHVtQlZCaFJpTTMvUUNXak5FOGIxaDBQWHJEZ3MyQjRYN0J6ODg1blUxaVJYV1VESmdEbXYwekEzdGE0bElHMkJUNHBKejNaOCtJTmF3YjZqc2FEY1RWSzdxWUZRS3g3bmZndjBBTkFGNHdQbGJ5bWRlV1hDbEZDZEZzOWo5RDhlZHUycmFFQXNkbnJMWWlGTlpwallSRFVhYVBYZ3V0bEFxa1JGTmtZWG83d2wrNXRUUUNWTjdBME9PMWcwQWt6Nlo1dDNQVHBzRlRxUFFCSTRTRDZvdDlHQnRkUXZncmZTVTZnb0hBWjBvdEdTZm5PbGFpVFUwOXc2OVh5QVMyNW5sbkwzekhJYm94cmVhVXBCL0dBMngybFRCVUU4c29LTWVZTHNPOXBUQlc4Ty9QQW1BWFdlWXdoTklsWm4rUW1oWmNsOTNXS0x2VjBLZG9TNlByQXhPSU9lY1MxVUJYMytuc1hGMFR2bjkxaUs1Y0pNbnZ6NlBhSVVBS24rYmsyekwvdU1tYm50TThaRjRhcHl5Z1hwT2NWYVBJbVY2ZCtmc0Npb2c3dnlKaVNCcDg3bjcxUXE1bjBYbHlrZ0pQakhWV2E0VHhwZ0VqL21LN0xRSWZtTkZ0NWtiTDk2L1htU2EyMzhxTUpGK0dwcEh3ZWJiQXd5TWRiR1VKbWUrSkFpVDRmdnFsNS9DZUo2U1VyMnBuWVhPRDJsSGIyQnFFb21YQkFFR2drbit4TlRLNXlxNCs0U1lMOHJ3S3YrQ0tRYzJUR21zdWh1c0VpaWk4SkZYN2pBSVcvcE5ra3gzamZVTXVKQzU0OC9ubUozdTZsZG54QzdMTC9ObGVSYUgwRVdzU3B2bVhnTXFrVGtsSG1MWGpmK0NlSjE5TmQ4alJ6d1drK3FkNHAzWWpNQWs1emRvRzZFMTBQc05jL1Aza2FFeVFXQkszWHpBb0V3UDBOYmtML1kyR1BzM0pKN1hXMWJnbXZYZ25xMUd3KzBCMEw5VlpyVlBYVmQ5NVBJU29neVJZd29mUXJjS3h3cmc3S2lGYW41M0Q4UDA2dXpoNWtNUUlJZDVnWEZ0T1hYdHFidGN6ellMS0lyUmxNanVvbFBIZzkvWnNQMHZDQXFXTTBqNG0vU1BaV0VvYzczOGFBNlJWQ2U1US9vUHFrSmdHSURPSFZZYU9hWVdyMEhZSVhGT3ZiNzQvejN2blZuUzViSGNEMEVsemdCWC9IZnE4NE5mZlpHbXdQcjBpOW9Xd09xNDArYXdkb2YzRXJBaEMxWlBSN21ZWHRTNEtiK0VmMmo5L0RtYmRpYnU3cFYzU2hJL2g3R0FpUTF6dDN5dnA1VUtubTNZSWpUZFBySDA1aERMUUo5d0REdVppRllkUDlxQThrYU9UVHorMVhETWR0eUQyZE1lQk5BWGZNL0licldOcFBMSmlxQ2VWWmtzM2JkMHg5N0Zad3k3dUhUU2NuNVJUSnhrS2hKbG5FRkwvaFBTR0VrVGVNMVVIZ1lOelRqY3F1VkZrS3JmTEtScDBKREU3ODgyYy9TMFhEQ0pqN0lkd0pCRlpNbnNCbnVvRDh3aG4vejNuamlyWlBsbmFwaU5hUllqTmtucUhTS3RSUDlVOWdIMi84YzVabVIyN3l5L1ltdXNxbjBqTG1YMTlHbE5CUkx5SzhvQVpEZXo0YjRIa20rRkhNOW9EeVExakxMSjhYMHdsaGRFWkJ2UENPWm9kcStvY1hMRHpaUEdHcVM5d1Zyc3BKODYwT3BzOGlCWjl2ck5yZHlINGRKWVFHTXlaZWhZK3cxSEFTNnNsVmVHdkFhQ0FsK2dsalo1dkpPNVhKbDJ2aENIdFIzaDB3YVorMkpiQk1SSWpEM3VGenlYc0ZoN0tvTzRmYjdjMStrSm1WZERXLzU0T3JYb0JSS2RUVWNqYkptSWJxckRwb0pvOWhhcUJ0bllWbHFka0JMYXdhaFNxMEhpTWRIbVpHN0QyNTlvRU1HcVoyYUJUaEJFUUxCQnQwUThZNXNoVmk2bTI3Q2JJOFd4dXloVTFVWFZBQUlZMTBxTEhSRGF2VDViaXQ3T1czbVUwUkF2RDdRby9hMzVlSldDTldVT0dGcVdlNjhGd3lpMEZVLzAxMEc5MXR4dkFLWEwrZjE3NkRTb1duSEJ0UFZjWGdDdmZzS2NmRDRVWkNLZmIvQk9Jc2I5SEVOaWUzYTBsMmViLzFsTS9iWnhteUl6dXhyS1JJcCtWZlkxenp2NThxQTRuYXVrVEoxK2huZEtmMEZDMzQzVWtxZkdKQW5RMUNENk5sUmlSMEhhczYwTDc4TFZjWTA3MkVzN0tPc09Zb205US9TTm5QRHlOYVc5eWdQSW84SEFjay9IYnIybXV1cnBiZUxPUnNDcEpiL1g4aDZCbHVnTzg3VWNwL3lBZndhYnhRWkVLWGxEbitieVZxVzVkNzFqaFpaZ0lkQkZXa3dUZVo3dk9nSW5VVGd5RktTSmdCK3Y0VEJaY2ZQZlU2MTV1ZnpLR0JjMDhQRmtabGU4eFAwTlNTL1hQSGtIN3JuYmNKTTV6dVE0b0o4R2wwaGtkNzg2T2dkU0hIZmdLZ0MxOVhhbFdEazlocFJHNzNMbEh3TVFyc3AzV2VKNG41dkxMQmd3UGo0OExXT3R1T0RVK2NHeGVBYXBtUEZYM0RBRlFUMmpubWlmN3hVUkZmblEreWpGaHN0Zi9VU3kyMko5c3BISkZJWUhQeU5uTWlEcld4L3FhbzAxQ2lnRS9NTkV1VWNpaDFIdE1PVCtCMVlTeFIwVjZwQ3QwdWxtVWp2bTJ6UGJZRzVpTWtzeXc1VWR0MmYzdGtYVVUraHMzR2xvRjQyR0kyb0l2dmhGYzVnR1ZDbDJCbUltQm9mRGc5QmQrekFRNm9ieWZ5TlZXWUJ0UDZSY2owY0M4M0NlZWhKMjJEWXZiMlkxYkNkd3F4dGN1UEl1TEdDV3V6M09uMWZ2cEFvYWdFdTZuWmw5MHdoNkt2dmxIYVB3SDJsT0pNUWhhd3BjenFxSVJ0aG5FZHQveThsWEU1cFU4VmZBN2hiamN3bnUzTzVZWUhKajVhZktPcEZydndjQjVTR0s5NmVzZC9aTDRuZU55VEh4ZFBhQVh0QU5MOS9JSkphVWY5bDFvSitWK2pqaFhETmxNT2wrTmhUNkJFS0ZETFpzNTBUWHBCQjdEMGo5d2xYZEQ3ZXJlVnQ5U2JORitjMXZRRjFwQm1oOTc5emF5djZmWWo1SDNTWW5PRXBjakNnMEtyUTE5UkVQSnY1ejZpd2o2aVFadlVCTUlpSERCcldpYWlkZnkyeEdMa1ZqaWU3NHJmQ1Rxa1RQZ3ZGNXhYdi9rbk9KQjU5UjY0QnBPVEx6SXFBZTNXN0pjVUY5ZDVnVVI5U2RzbEFKM3JkWFFXSEEySEtTcCtMY2svbDN2NUlCeGx1aHp0bklYYy83RWhqdXpaeER4dkxKQVVVOVlLTVRBS3BwTWlXVmRYRFp4Qm5MSitveHQremwvR0dGQ0tTUG42bkg1TC9Xc0NneEZob082OGJCSUV5czlSOGdqTng0dk5pN0tpVWh3OXB6YWpEL1RsanBlR2tINjVrNnlvWGxYWERvcXA3VGlBSUp6UysxVHNEeWRKSDZOVm5Cb2FKa3llelpvWEJIT09oUklGQnFubXRSekdYZWY3Zi9jRUQxREt4MFJ4VGxxOGJQOHBILzFuZXJVdWVneElWMkcrd2NHbzl4Y1NEejBhN1V4SmpsaGlvTU1VVUdyV29kTkV0Sk04bG16RHJiM0tRdjlnUVF6TFc3SnE0WVNCM0VxOHpsTTVjMVZPcmhFbjhieWUzc0NPOHhEUmpTSGFlT3lhbzE4aFRQOGFDdGRlUFZjbGk3WnBDNDVMS2t6bmxlL0FNY25qRnVtSkV0RUx1M2ZOb0U2ckVweWl6dk1QbDltT1EzbTQ0MlRtd2VrVWxqZUtTNVBjVGs2KzFqc295QlhzTVVKdGFGY05rbVpoTzAzcFNRYkg1MmEzZ0FLemRQS0MzaTdLaWJEbExmSE5ZZGlzNUdFcXFhc0RpZXhDQ1BUS2RYeUNoZnhabW5CNHZGb2pQd2xiZjc2K2JyZXhaQ3E5RU13STFwemJiUXJYUW1kN3BvSUFwVno2dnFYREFRL3I4ZUNxbDVQQU5MWXRqVDBwbEovN2N6WXhuM09ydUtrNThCRmxjeHFVbXRJUzNEUGx3cVljaVQ1U1Nubk1OSjFzL01JR3hkREpRcGY5VldkRjRhc0Fld0M4d1pDQ2MwTE1FUWVUNmtBcmxJdnVmekdLTnZwaWxTRFhEWXpockNyQk9uVElSaWNnc09HbVdmd0l1QnMzTVhhUUhnWmRHbFliQU1iTSs1UittL2Ywd3FDUEFvd1JJSDVNS2RQVVdRUHloMDVLSlVhVzN5ZjM3NVpCdVMvUHRZR0toUndvZm9WaUxZM2lReVp6YkVpYWsrZ2dCS3VBd2hxMVY4SlNzb0lmVi9SNTI3VXRrcEhTRkFKenlPdXprampTRHFBTzl0S1RNMW1YQkhHdHFnT2RKdnhJZ01CWENqV1FxQ3lWTjI2b05zaHdEMzdyOUlYQi9uLzZyUGo1TTBTR2NnZnorUG5jUjlsM1I1N0RQZ2puZ2VwQStORCtHRzArRzExRlpUczF3T21hUnBwNks2dm9lNnF3THJleFkyUFBEOFoxWUQ3Znd5eCtzcXFsWlJidDdrbmYrZzJ4YUM3UW5UQjcxUjlQaFBZdTUvQjhRakI2QUdVWDI3VENBSGRqdjFpY0FyQ0dQNEVRdklCajRCajduaWpjMlpqcXFtNm1wVTZSWlNYQkR0WExVdjFkOW0rNUl1VkxQeUR3WEtMNVJmMmNIZXNiZktaOU9FdExtcWtjaDhSK0VmdytBNU1RdVZUTDJETTNmaFNmYkZBOTV2M1l5UnFwM2ZQZytuVE5hcFhSd2FXSzl0enN2eW1FMmpBUWp4MHA1S2JZSTloR1QzVFBwTW91N2lqVkV6TlFxa0JUKzhXSnBmY2R5Y1ZWOEI2RUQxRmdDOVlKTFI2Z09hbnhLVGNVeTJUY1FCZTIwampmL2hNYzd2QVhNZkd1RGljWUdzMGRpUE9JQVpCb1o0MTFmZE1IbFJub2FRN21vN1JpbHozNUNzdGg4TnJFLzFuMlgwbXpVNGZPcEdybWFpMmZCRENLcXE0N2tTYmNKU3N2cEtYUnRXU29BcHQvMXdnT3JUb2JsMzdaM3R0U3RWMW11QTE0cDBWM2tMUnFkVW83bkp4amJjekVKMUs3RjA2VG5IYUc5MUwrWkdrc2dqb0lyRm5uQVFUMGQ5V3dHU0VJZnZrMDhQaEs3TFNsei9tVWJaZEtvSVFnZ1F3RlZKejVEQU5rY2lsaGF2QWptcUlJYmZMNkVoTk85ZEdWaFVmVWt4V1NQMlRGZnh4bHJDendzSUNZOGQveXJUQ0RBT1VsMno0U1JqaVkxcGhlNVM2L3ZhZVYvRjd3dDA5S3J3SWdpMWdEZGdER3V6WkNFR1hZeFR6dFp2c2gwT2xWL090YmRhb0ZJaVVDZS9Na0xLeWQxZkkvTzVUUGhFNXVxS3IyWitUYzFEUUozeG1KQTlOMXV3aDlQZzF5RlJwMDBOaXdRL0plRTFUSXVMRVVHSnVtRXlvRTdyNDBKM3d2Zkl1cGFrV3pmYWRRNXF0anZYbzRmcjJuMk9OR0pHanRlZUhOSExHa2xDOGVxQmZPNjFFUURQZzMzSzJGSFYvazU0aUg2N2NDYXJ0SC9OY3dMekgyeU92YVlWQmtEZXR4UCtFZmhFK0UySGdISlVEaHF4MjkwM1NEeklnZGxMeXF0WGlseXI5VWVUYUtkVjh3aG9nNmV4MWN2L3lvTGZxY2xndDlob1BWRzJodkdHZ2FvYlZ0Y0llNXRhZGF2di9kWTdCNVF5WSs0Tm92eXk5YTloOE9tOUxnKzdXNjBNUUU5dTFBSmo3RFROcUFMd1ZSWkZDY2VsaXNVdFF3Mm1iT3NoZWZmazZGN0llWmZna1d6T1JXTFJOWTFkZ0hkWjRLU0dUSGxVeERqWTRSMGk2UnVrREZvYnh3SnJZRDFLNGxwbEZxVzJkZnRxU0lPU1R3ZFIvSEp3QnpPUlBrcTM2U1dTdzAybDA3K0FHUHA0cTBaOGlvRlVMakVJTEZoVjkrRWU5dUVuYklGUXZhSFNnTU91eDRGS3ltV3VPWll6TjlCMlA3TW5pdWd6QzVEUGROa0JwdnBFakdITHFzck00TjBOMHpkUTE3TUtXRFI3dmloeFlzc1kxSWd3S05kNEdmVTd3d0tXaDJOaHdlektoS2xQNXZMK0pGZzBaRWtITTBtdzhCT0RDSUwvYVFmSThFNitmcjVPWSsxTkZvUDVxQ2drbjZrYVF0QmNSbFNpRTNnTXNjaTZjMXZCUGZBOWpFK0lRSXpoNjNZRnVKMTRDZFZDUzR0WVBKM3pHZDhRTnB0YUFwMm1wd0svS3pFMUFnVGVxNUFTS1RTbGk4bS9DdVRWeHZQcUxKYk1BcE1nOHZOZm9DbExpdnpHQld4VUJCZjl3dW9FSU5XTTE3U3BXYW84SlI4Tzk2cjYvbjFLc3ZkOU91MzFTamE0UWxUb1FPMmVIZzBZaEc1TWllWFMzVXhaMDRlOXhXVFlTLzhGUHc2Q0ZYNDdmeXdVUll3cmxMSXY5bVhTeC9PSVhJTVBXeG15NHBUdCt4TkRJNmJ6Y3VneFRkVzJWTTlzY3Yrem95Y01rc3Q0N2JNeVo0V0I0ODFhUkNNY0lIanZ0T0pxa1ovQzg3TDVrS0dVbXJLanJmbW5XR1N4TVhXVnVWd1kzeG1mci9KaHZmWTY0MzJtdzZXaTRrSWJrNHZZVkZGUVJOZXJQazZ4L3pTRklkUHJvU2J2SGxLa0dhUi94Y0JTSkFXdjZSZEU2bm5ieFhHTm9xZ24zNnV2V3l5Q0NZM29xSnNHQ2N3WjkzZlNyZWl3S2dzM3I0TzFDZlEwbkdEWEJjV3JqRXZmY0NrM3A0Um1wc00wbmd5eW9peXZwTHFuWUx6UHIzcVAzT003NEV1UzRiOWRCRXQ3cVpHeHZvbk1NMXFEWnJvT1QydmZCb3JSOHowWEZvYVl4NnNDOGI3eXMyekNCYmVEMmlxRzNCTnVBZVBEY3plVnE1WEYzSk9yamZPUTBNRDZaUng5RTFZWEVCM25DRjUwTkZrc0k2NHNMYVFiVk9nQmo2NjRQYlZHUDVaSEZHb2ZCQVNmVTB5WEV2N3piR1dnRXRQM1p0cVZBV2FrTlh1dTQwcVFWa2x2UTdicEIwVCtEaUZEa0hiNDZlNStHeDRVM0FHdjhnQUM5NzFWcFdUUXByQTNPOFlEbElUSC9ydWZQVDJYZzIzakNkMW5ySHc3c29wRlNRbEh0NFNrMitoait2RW8xcDhjTGtMVWxaVEZYd0xjdTJYWGd3dkhlM2hzOEk4djlMWVAvcVdMZXp2cnZQWng2ZjN3Q202MGJ5UjBPTDlhVnBoNHM4UlhRR0M2TjY1UDBSMzZQS1N1R2I1L3BIM1lId0JRZFMrWkJjWXF1WTRWWVpTbG1TZ25RQzZMS3ZJV3JGVTVqeTNNZC9JV1ZWRHlUV1NGOFgvVHMvWVFsU3hzbnF5cktyZTV3SUFyTlpxM3lYQm5XeHkrOHRCMmU3NnFJbWxyTmp0MXlpdExkREJEempCMTFvaERJSlBvZWR4SWthNkt3a2FjVkVudWVQSUpjZjRKaGFiMzEySEk0ZDVzTDJMVDloQzRRYmNxQUNHNytHbFRmWTVEVDNoYzNUb1VNK1BjWXZacjRJVW1TVFhwYXNVT3pyaTRSZWd2RWRsTzFUMStvcHdNeGdoUmZNN3R1ai9WQndCODhTeHZYK1BVQko4TG9rR2QxamhRODc4ZmEvWlk2SElQeUJ6SEc1QmlsWWpvZlBBb05sc0UySFRYaVhuRlNRdXRtQkNUaUFzb3VPMm9TTG5zZzNTOGRwZjN1VTZpWlIzdkN6N3I5b3VpYml5RC9JVC9GRjhVRVdpVEVPRWkvengxWUdVdW9WbUprTXYvYitnV0tCRkZJY2xtSFBkL0loelA5a3c5NGpuQ09nODZHcTVsZ1dMU0FGc1NJNlBvamtLSDdOdHY4eUJ1RDhtR2svTmpmWWRiRHdObVpTTVk1QmFmdWNqZ3lBSnc1Mm9RTVZ1eVFQcEpVUmlzT2tEaU9RVkZmcnY5RkhndGlsc1drb21heXRyaTJseXA1dG5QYWUrQ1dyWkh0K2xOWXdDQkxNN0NWWHBRaHhsNFlSby9yaTNZOFdsMlUyM2xyaHpVUFczK0NQS2tkeFpUeDFHZXZoUnJBWGoyY0xtdUZDcnRJU3liNzBDK08zdUNFM1c2ODJPMUxXUmJTSEMyR1pqWUpGOGhoK05kNDJXZ3VYV0gzSkgxLzZiN0RWOXRaUFZaNitKM3JlUmVDand4WER0VldVK0V4bzdRL1JpMTQyQWgzVHRaU0hsdE92VC9OSEtGcERzbkJjcjRCZ25iakdHYnJ0UCtoSTRYL0ZMQnFFVG5NaVIraUZtTnhTRnFrWnd4SzJFYzVjeWZYQmpncEdrN0dNNVNacUJ2NFJKY0dIckR3dGVLSFdjamc5UiswR0F1WisrdXA3ZFhDQXhqeXhpU1dhTWtaSzZYOFpvd3NiTE1neWYrQXBIcTRGKzBTekMxck5iRnhHWFEwRGpEV2dBbWVkNHNqMk5wMzR0RmlPY05rOUdhK2NiMjNHZUhadVdRMDFtVlVtcGlSVk5hZk94QTZGeU0vOHNlMHBkMG8wWmV6dHFoUW91WUhTMUo4NURHeWtjd0I1MHRyQ2RUTlNCQUN6WHNKN21pdDZSaTRNS0dLZzVqdnYxZVVMWEdvQ2JkMHk1ejNxQ3RxKzhydW9zc2twS2ZTUkpKNzBhcWJTNmRtWlNvaEdEQ2JFa3l6TzdpVzA3YXN4WmVrNi9XM1FtYytzWWNERWc4ZU5JeEFKeDhQZ2V0Sk5mVUM5TWJnOTc0S1JyRDlJK1RXYXNFakhNdk43K1hzQkNDTmI0QThlajUvMTVQL2NWeFRMdXc4UytuUStiT0loZ2NTZjRoTTlZcWdZN0dXMDFWMXZJam84eW9FSGRYR0JwSHpaN1Z5RWh6NnlUREtzNnZOK0VTQWF3a21TSENvemFYZjh6MzhpMS8xV1UvQnJDcTRQSm1TMGVmbzZCd0hnSkUrNVJXallWNDRRYjJTY296L3d5QSsvbElwZm1xaUJiN241c25EWllKc3RqcXY4MnB1amRFRCtXVmlVcldTdHVjSTJRWlh5ZU1YcGlaQmdnNVdaVzd4NmE4TklnbFBaZ1laUHNFUFhJZjl6dU5meUFqM2NzaHdnS0MrSW93VENiOXh2V3BQODBvNzU5bVVIeGZOUHFicEJwMTRDSFNsNm9iK3dXcS9MYk9qYVI3cWN3ajFNU08zdkYxME9IMDBaNVBpQzZhVUtidEQxOVg4Qk41WVNHVldScE9KeEVxbFptSzUzY28rWDhpeEtPUE0zRTAveHE0SkR5NStIbTNpdWlUZUY1bWM4a3RRUFV4VndqQUhRVGNvanRyMnl2U3RpLzlxWEpYNGhqbWxPR0ZNQkhhOVZIK25HQ1YvcEt3WUtrbXRmWmViaE1KdVd3ZmQ4RVZ0b0d3L3pTS2FxQmJxNlVaQ3V1OXhGNzU2NmdxOVhRSUd0WnM0RWNKT1c1TGdod1FUUktKbklCdkUrSEZOWW5MaStPUU8wRUllUTdhclh3TE85SUNpUCtwSDIzNURtS3dDc1ljRGRWVkVIZTkzSm1aS2pFaVcvdU40NTlRY1JyTEdVME9BNFg4ZlUrQkRHUDN6R3RYK1k0VS9vOXFKa09OZndyNmxqTGwzbGs4QmV4OEJCdW1uUGNmM1ROMDVNUWRGRVlCK1NlZFVzK0tGdlBiWXNld0Q2ZFpRd1VqdU0vS0c5RlMyeS9LbzhKanpVK3puTkV6Vlc1V0ZOSVpGUWt4MmFFTXZvTTZwcTBON083RUErejQvRDZUZ3ExMitUL0IzYWpNazQ3OVFldkp3bWhRNUhxV0dLZ0NtVEFRYTRJUmk0KzdJZkZoUFF3d1lFWXFmbVB4RTZMNytBYVJHYnhpT2Eycm1RZkZ1YUJZZVdFNGtNd0JHaWhyS3RhUHgyNUE5MHF0ajgyT3J6OXZGZ0tncVdSRjRORGl5YmFtM2tJa0s2R1BlTytscGJPb2Qydnl3WnRPYlplMmRFNjZ3RTN2Vlk4ZkNxeUVJbFhxSTVGQWdSUUNITXQzR2VBNXhXRVJ3M25panEwZ3A3b3JyR1MyaUYxcHdVWTdzNElCd2R2SUtrNW1rUFo2dVQ2enVhS2laZGdNS2NSR2JVMXIvQXh4b2FDTTVlNEcvNHg4cXBPelhDZzRqc25CY2hJcmd3SkVpZ21IUE84RS9sbFVURXNxVXVpeVFwNTcydkRaTS9GN2creCtTYXhSQUFpcjgvU092OFRBbnIxVjJuZzQzMlp5bmJ0bkMwUU9kdWhCcW9hSkZkOHdDTHQ3VVZpbTEwUFRNWFNpWERPUXcyMlZSaHFRVXJYMEdNblNFS0pZODE1QXpVNGY4WTRUNE90Tk0yeTVqanlsMDhERmV3K2FmVlRqb3ZQNUJvSVNVMkUyR0FjNjZIeGFId3FIT3lXb0VBWDBwa3kzeG4yNk91SzN2anBiSUREWnMwTzh6Y0RnNUNJOXNDeG5mVXRtUUVyV3NOUHI4OCtzRmFUb3psSEVBOVFQME92U0Ztam5ZNEUxTUFpeHAvazcrWTVRWEU0cEFGTVVTbXo5c014blNZUlZvMEl1NGo1MFZZU1htWjBiNmwxeG9NREhvRTdCdzZsMllZdlBaMVVzVkswS0c4bDVGbkF5cUxvNUVjZUo1UGY4OWJwNS9zMmxNZklXRzZnTzJ2UjVmRU1oSzNpNkZEYVF6UUVacThyVytyZkthQUJ2empFMWxjNnhDdzVwUmdkM2lXVy8yZmFJWlFuNzZXQ3BCc0I2dG5uRkF0bzhEajdKbm5DS213RENOYllyVGxBckFyaUJaTFUrTWZVajZQTkdyVURtN1U1SVdQOEJmY2FnSnBZRmhVMjl0RitWVmZ1OVo4ZCtUZ1A5ZThaUC93VkpBbGQyZUk4SXoxR0d6eEhFVGtNL2E1cG1jLzg3dkk4WkZ0RmI0bGtZNGNzcDQzZjRGQ2dNZ2oyTEVSb2xiWWt0UkpkNHZqMG9aM1I0QzVXUmI2YmVmSTBFeG95cFl5YjFwcWZ0TS9ZMGtvR2ZpaTJBQkhFUldtR2dvSERscEhkck5CdHBJSGFlbGtQbHVDRmUvTkk4ZTlxa3NSaWR2NUUzQms4YytERlJJZ3VCdFQ1bHJ0cm1EZndhekorRzZRQ3pLWXdDbXc5a2pPZ0ZHZU8zRTA5TC85eTJWU3JnZUU4T1oyZWJtcDdIMHphanRNa0FDbFg0dnkzTWc4YlFRV3k5NEhxOUF5eHcwRTE3WmtSVG0zMm5aUVlVK0hTbHJnUWoxQUhleWtOVEZaTytDN1Z1a044Y3lsT0czODRrMU1zV2l0RWpwVzAwOEI2cm1yMDBuTE9XZzFtTXcwOC9LbENIZ2dYbXlXd1ZseGo3cmdRMzM4MUtTcWlLMTcrb0gxV1AxdVRCdFNpb3RMMG80SXQzazFqcmlWdlV2ZlEwYnM0b1lIYVp2SXdwWFRLRjlWcWNJSzlqbVh0THlPdHQrN1k3SjA3emhieGh3Uk5xQ0R6TkxoQ2JRRVlVQUpHOUlzOEluV2trMkphTlpCczRFVU9GQVFXbUFPLzZsSVdXajZxQWlWU1RtbFNSS1h2bDVlS2JlcnlZeGRlbmJMUlREVi92MitPM3JoS1dUblNDY2tLV0tDT1dMV3BySTBlVGpic3VMSnRBVDZrTFo0ZE9RdGtua3Bvb1MvK3ZVZm5FOTgxOVM1YTg2RExENVluMnp2TUNnRGNSQkpZVUExWWowQjk4ZUsyVzdOekNtc0UzTjRzcWRySTFPdVZXS2NucDFKZFkrbytBaVYyZkowa0RLWmFsMkNXZHNQWm5rb2M2S1hLUG5NZTVLamQxV1h2NVlDcDF4L29oNkxIMTR6S3BpRWxXcm1VSk1LaEJ6NmpjNUVNSERkbG1xMGg0L3NCcjNleUc3VDZMeDErWU9aVU1XMlViQlFGRjNiMWR1Y2thTXF4K2NNeUh1VHNSNXVtdndDRmFSSzZFSjlUaitBZXA2ckZReGx1MnlQcWIzR0FFb2ZuOUtkUHpTaTBPK2N5NG1aS29yM2F6RCttZDlwWGhqTmV3eGdpQVRTc1ZEdkcwcmhuSkdOVGMxNUVPUmV0b1VwRWRVYnZseXYycVJvcGt2SkVEYk5Pbm4xTXdNUnBOVlpQMHo0QURGaHZHMXNjbmkvZWVJb1lOdkkrRnZmNnBVUXBJMURnRmpLZmNyU3JHTjdqOEFDcEdLVE9manRPaWFKajJOY3hVLzRDU2plemRJS2U4QW5OR0Y3N0tWRkZpSkFaNW5NdlJhUll1UGczZEtXWEtYZHE1dkk3SGo4SFRaUnVaNFZwVU90VDA5K0FZNkRiampva1MxakFHSnBHMHhlVWkyZy93OHFySXdJbEYyaGo1aWV3QkE1WWpvNzdPaHY5NVFIajdJdkl5YXpiQVgyZG5oR3VScG16bE5TOEhDeE4zMFFNRHpFM1RyMEhCMVVlRUxFb0JmNHVFR3dGMmVtVTdpaHVtMEVOUzBFTURkN1NLc0tBQUszajFBemJ3V3dCbEJxWE5pYnlBVkN3cS9pbXZkb3c0VlFkblN6dmpYWWlaM0JMRzlzaE9EQ1YzQ2RGMmFNZndaT3F6VVdGMk9Oa29tQU1ackZEZE93bjVjUC9PTXBWM0haU3d1Y3h6V2N0VXM2Rjc1UXBMWlRLb21tY25lWm85NGJwYW1qazBhRG1QZS9UN3pNMnV4T0FBa2IyclZLeng1NXlHWGxZOTQ1MkxNNU9rcXErYTZZRWlDVFZFU2djdTkwd2RkZjFRMFkyaDFTUnRWSXpZdVB0OTZnN25YU0g2THc1SXl4OWMyVHMvZVpzcFBIT2U4WTJReDJ3dE1DYzVkM2xjaytmZWkzNmVBTFF5RHFhQXUyMkYxUXhKTDNDb2krdUxDeHY3dzdteUJkZXJPOGxXcEZVMjY0ZnFSZDIzS2JYazUwZVFsOXl5b25wTGxMM1R2eEU4MWhmQWI5aGNqZ3V6TVlPaEx5ajJtdXFrbkpEa0Q5NUUydjFmVFpuVnA4ZlhLclJDbCs0YVF4Mzh1VlhIa0pTY2RTcHZ4blFoak9McnZReTFacGd4eTM4OVlpcC96Qi9QVHNqNHYxaTlLNXJUbENpNUQ2akNqandpZ2xKYVdpSEhxT0MrQ01ZcWt1YUozZVR0UFFzL2VVTmFsdzNNNlo1ZTZ5RXJSU0lBaFViQXh6NVk5QjBnR3VYbFRHKzU1Q1hBN3BDVTlBMzJxL2JaUTY5NVJVKzROUGoxYlBQL2dNL3lvRUk3b0NjOXByT2NBZm90cUxKYW1BWHEzSXZ3UW9zNEltR1g3OVBsZDhKOWh4b3B3bldhU2t3VnNXY05DUGpHbXB6VS9hdGEvVXVPdWt5cmhBakhsVERhdnJuVng5dXdqdGdGYmUrSCtFWEFpQ3NtMkREWnBybEdMV3p4cDYzVnBLcVlzQVBFSEJTR1hqZmJiVTlmUjBrY3k1QjRLaXFENm0xYjNyOHhMOTZIOG8ySFUvcVR3ZmFPM01Id1oxODhOdi9CeG9Yc3VKamRVUEI1T2pBZzJ5bEMyMlZub0szWG0rbmhqYTJWbVNuU21Tdk94ZTVJdmxYYmdBd2k5WmhkRGo0SU1pbnlsR0wyVmZqSFpmQi96a2FmTTk1VFdCd25sODArYkxRRE1qYXBoamhUeFdidWFGeTZkSHlSL1BNb1h5V214UDZ5dStkbmw4d1ZCemoyMXNySC83dkxBYVlqekJ0Nk9vTnpyWHBkMU1md1dBWFVhNDFFZGZOZC9zN3NyZ1J2L1VLZG9VODFYY3FqQlJrMEVydlRmMDRSS01uOTI1Nkw4U1p3MmZ6aG9PTXJoU2lUL1J2bUdTc0Zma0trWEVGUXJaMUVndXlRNFpPc1BzenFDUFMxaDFoRjVVYTJoam16UWpEaDcxRUZsNHdjRytnOHIrU052aHFXeGpDL0hxL1o2UXhzdkMrOFJGU3lMUkhkOWg0Rng4Q0cxN0NKQkIrbVV3SkhLZWNjUW9mQmJqdi9RNTZxaTFtSkpUNjlBcGM3a0dJTWxpV1VtY2J3U1lvOTFWZDhEaU5hNndROWN3RjVKN0FEdVNxYkt6OTNkMThadWgvbTlwSGZ4aUFMRGo0MW9vRjVCazB0QWp1U0E4NWV0alNXQ2VsRDRUTVEvRWpENGJtZVg4c2VnUHZrMFJzbjdYWUdsb1pkajV1NHlOVWFoSi8ra2grWUZHbkxBUG8vU3QyM3lmdWhqYjZuVWdwSjRzZUVDUi9mWlN4ck1iUE8xSGNPT0s5UGlCbXFFVkRpeFJMY3pMOTNkTWttZ21XOUlwUHdONkhKdmF1UXRKZWNiakRjSk04TnJzbi9MT3hreFhHL0h0WHhMUnF3amNxTEI0eUFxbGp1OFp3K0ltaGpCK1ZxaitabmsvVEJnSmtMSDFpUFNOVnVYQUlJOWlTUXQ0T3AvSlZCSjRVUWpwdThWS2t2VjA0UFcvQW9icnNuYmNENEp5bjg1dG9JQzgxNG91eHJ1ZTk4cXNLc3dmdXhxKzVwUVF6Z1dtaG9COTQwSGR4clh5aXZ1SnhrekxnT3RIaG01SGZ5RkdxZzcyMFN5TllIb0JsOW9OanI1RHA3T2tDSGhFUmNUYStBdmdyTUFmUlpUOUswRFpaejRpS01YY1IzdGZJTnl6TWp6emdkcFcvcGt0TzNWd2U1VndFS1FIUklRN3J2TGRFV21VVjNyLzZOeVFkOHBabVZtQThxVm4xTW1PTkxoelhzcFBPQm12b2h1NEhnNVNmU2w1NXVSazNieVFpcDEzOHp3eTNxNFM1RkNhclhFV09zQXNyNXVuaTQreU1aZkt5OHVsdmFSc1FEeXdhcTQxYzF0VTEyVDZuV21HMVB3VXhxYitvTk1BYTAxT2plenY2eGU2WHNodGJvS0h0cUxFcS9jT2hWNkxyazlQeW9TSE1kdnRndXJqQm5qTEpNK21DQW50eXA4RHk2VGd2Ris1YXd0bmFjNTYwZVJpQWcyVlFPUklkZ3BKdlM1RmhSWHQ3YTBqeEg4SlIzRFltaGFISkRhYm5IeWI3STF5OFl5QjNKZVoyYW9iUlBLVHQzWU9reWc3dWpvVFk0clFRQkpuK1pCK2w4bmxqWDJmVktLc2YrVDZUbkRxdUpla3dRTDhQWkpHOFVVRFROTjZlZFI4dnBJMy9HTGNLK2EwNnZVU1Q4emcycnRNdm1TM29paHAveWZETGZKMytEUU1LZnNoYyt6T3pJVnhnN0lqbUNjUlI3NjJnNm5Da1JSVm5qa3VHV0tzWi9DZWdwWDZINFNtNTljcVdGb0hOeWsrZ0ZBRWUwMXhJNU9vSS9SZ0FkMmlFdGx5QjVhOUM1MHNiYXJ4ZWhyUExyTy9ZTG4ySmx4RkxvTVRXRjU1eEM0K21ldTUzd2JnNmluZFF5VjJvZThUVDlvOStyVGZZVVovYnNnc0lyTnNLTGhKMWVYWkwxbUF5UndQb3NuYUluQTI4cnRYTyswLzc3YUFGUzZJclBreDdKbE9kcFdXNUFsSDRuQkxzT2hkaklHb3VDOG4zS29SYlVFL2d1ME9lM1Y2clYyejljdElMNXdGOEpHMDA1THNxOC9peVJiQmZXRG9SN0FTOEtkWUxwTkkzbXpGZERsWHk0dllMMEpyeEtFSlFHQUorRDgvM1BOaW0zRE9EN1dwTjV0UUxRYktMVThEY3l3RmhtNjlkR2JnWEltcEFxQ2tBVzRRL0xsdVJ5WVdRTVZJQ0h1eTY5TTBVQitYc2hJUmtCWFkwZHowRS95SGwzYmw3SUc3VURQcjlvVmxOejFYanIwWGw1bmp1S1ZDZ0xtUDF1ZStuZGdtNnZleXFwU0xlMWQwdE9oTGZBaXh6TjFVbWlSeHlmTHlXU1NqQ1RhSmNSZnl0Qi9sNkNYdWg2L1VnUGVVVmM3NE9NQW9UQk5ZZTlBUktWVWw4YVZEREYvSERxdHY3dVdXcndrSVhNMlZmMzZkTkxZSmdrbUZ0VXpXUDhWUU1yaCsxalR4QlJPSXFwSnNVNTdBM285NzRSaG1RcFh2cXhVTmtETG9sQUtNSEluZGp5RHJpL21DWHl2ZDk3eGVCZ3JRdzBOdTJoVm5oNE5PUFBrT0NFMFgyTlNpV1FRSU5WL1RsTlpERTRHOFVvaDZBSW1HdVY5bDNud2F2TUxBV2RESk4yQTRiSGdjMzRVN1RMc1RnWm56UjZvQ1YwVytzMXF6Y0hQZlI4VlBJRHpXdnRrZkEvTjhhSndiTXJLcW41eEw2enMxOXNOMXZGdjlMWU8xb3NPL0F2blFPQVhjQW9EN085WTh0aHpqS3VNbXZnclBwYjFhN0tCZDMxckJVYmtYaTU5R0JpMkpIbnI4RXZRZUxPYUtPLzExbW9SM3JNeDUwZTR6dXpjd2RWazg5QkQycW93THpSYjNIT3RFdnF1b01NRU1WdDUwUDROUDYxRDZXZlU5UGlRNTVXYnRGY0F6Rjl4UUVTSTRlbTdvdlJMSjQydkVMWjNSSHdHQ28xUmJPWmN6Yjc0MGdCMis1VERPd3JsTFZxUlZjM2paMk9sc3FTb3BjT0VuWkNZRmtiaXFTSnZoNFVYM0lZMklqQjJXL0Q4U0RLSUg5QmdWd1RMdTNGaFNTQksyZHJXWDRPWkw0US9HNS8yU2dMdlJ2VDBmc0pOVHRaYWtQNVlPT1ZreG5ubXdGN1JQVHpndytiWjJlbmltV0RETDI4UjVVcjhrbWJtbmxBTytrR0tSeDVjeURvQ0hqSUdUcC93dUd1SXVFdmpNaUtEUlhZQ0tQV1ZnQWRhVHFkY0JsRkp1Mm5RVWxXYWRQOTNDUW8wUVdweFJ3VFNhY2UxUUViUTZ2UGxzdGpPaURQN1BGaXo0K3V4cXhwbnI0VDR5Wk9PYTVRT0FaM0xyb1R0ZEdzcjY4WFBVQ0lqL3FzK0ZKUGhSNlBTbzhNWHZ5ZHQyV0RBYWhMTmhhaVpOU0VtOGxudExpdDdFbURSYisrWnJtRk91MFBSaHV2N2NxZ29YSEpEcUdRc3JsTEY5UG5pK3NHMHZDMFFTUHYrSHhaemVjcGo0cGNlVHJscjM3d2RkY1ArMFNKcUkwOFZKaVptYmd4VEpxTGRFRHRXM3F2R3dXVk9TL3hpbVNFZHMwaUYxeG5uUHY4YS9YSGExc0tLNWNRNmdQTjlCRXNlMWM0b2hwa095UHBpdkF2UlZpQnpjN2hwcmJKQkFTV2F0M200a3V1OWRwVEdlS0RINU1QQXJRWkQ1UXQvYnVIbXpUZkt3aXI2R1VzVGRkbkZ3SGtQOEVPM2ZZa1hpU09SdFJDSjBwSzU3Q0d2OEJpUVI4OTR4eStlZ1VNdXZlRitwUjdHQW1CcTZBZ3JkYVdDOXBmRk45Vnl4RCtPazhMaWdzZnYxSmVnVWw4MjU2LzJYcVJ5cVlrNXB4elpZRlQ0U1o4WUV5ZmE0N2RQblF2ZmY2QVFENlN5d1gxWEs2eEFVWDFEdm1NYVFXdTBTWmFqRnBMbjhJbitmL2FNQTRoeVZycFVvVFlmOFhncjZBZkpvZ3R4SHhKN3RUR2QwcnRSTFhyWnV6WkNEdWZoaVQrUXNDbVZaNTVseUtjK1NQZnB4czlBY0pKQWROU0xGOEE0S0FBVXFsVmhsaDVkcTFQWnpJYm5CMUFSZ3paay9VTkdsY0xqQWFMalpUQUJkanpyTWwyMzlEY0dJbUZhSzRRUlhUaWVtVGEyNitvMkNSTEFTZ0gxZW5FWWhNZ1pYVy94U29CbEtQTDhvMWUvelhYRWRqV0tjT1Y3VVp5TTB3NDM0TmZCMjFwSklQSi85TSsvYjliWHBuQ29mZVpMTkN0RXRkQTNjUllXSUtYeTVlRTJJU3ZnZFV4TTJZZmtXcUZsMytmeUQ5MmtHYVQrSmZrOEg1VktmNEdqSm5vYmpBM0NmRi91Um5YTVprSnRTbEFrbHl6RmVQbFA5YmRlYSt3M3FYRHZKUHFmVkNwR1AvTkxjNFV3aUp6R2JqMzQ2TkVCSjRmc1RWZjd3YUF2akE4cXRZMXJDRjlSOW5zSm9jM1FpN0Y5TWwreVMwUmhjUmF3RUFLUnVDRlVjc1lreEllY21MSGFCRG9SUWMwam93eVFKZm9nY1pTeTBjcU1nWjZnZkNqeVFkR1NUZkt5WWl0QjFGYkxEUlVKSzNXYTdkYnlkUWxLM2xGRVlHdTkzYmlUOFhjM1lNU3dWektnLzg2dzUrUk1LSStrdXRUckhTV3E1WVlXK3djRGMyOG9Pcm90TStKZ3RGN3BzMEVXcXJTWkZjdkxaY0I0azFKb0hGWC9MRFFaN0lZTzE1d3c5ZkxiVTVKYmVrend1anY1azFyS2tDSytxNDh1V014Z0MrNTBwNDlrYTFoMVVlY3RIMktTYUNuWFZ3MGo0WXlNeDNGZ3ZXU09lTzZ0MlE3M0I3Q21uUzlYaXVhSGZPUjFMRGM1NjVnbkk0S1AyR3ZiNmxtTkw1Y1o2Wjd6QmUyUENnZWZaSDVZUE1MWi9HNlhTSnY4VjdBajhEYlhUY3VvbS9OOG9OMEk3WlEwVCtpTHFKNmxHY202RjZzZXRlNFZSUDRnR01LTyt4OUZaMm1sanQrZVMxNXhaQmdFOHpMa2RmUk1MRmowRnRsc253YjVQWDFIZnAvM245c3Q4bnF3MzVPNlg1T3RIRCtyMFZwOG5zdG55ZTdPZko2cE5mSjM0L1AyczRCZS9hdjc5YnZ2Mm1lL1d4L2g5di93Zll3QjVzNVAwZk1IN1AvcllkMEdZeWFMZVk4M1RialZrWUVDcDEzVTErVWwyMXZUZ0I1ZzNoaHJMR25ucCtZWSs4UGxuSm90WmZMWFM2RFVpRTFDanN1WVpJY0toVVU5Yjh2aWVrT1p2YXN6K0kyVWVLT3h4Z1o3dHMwaENvSHpINThPd0lBOS93NE9wZUtaSk4xdmthWWNlYXh4MjVEeFEwa0lvME9XYTd2R2xia0xrS2s3elA5Q291SWpxZDJ1bXVBd0Y5MlM4TUplUmxHTjVoSFR2NGNMS3g2dCs0L05sQTBYYldkaHl6K2dmZ1hETHp2WjBhK2g2SElHRHZkUisyaXlJaWJycmMyL2FRUjRTWEQwYXVZY3ZjT3BOcW1HZU5sNURpUWtxUjdORjlIT0ltMzNWbHBnMFE0Y05EOXVidHg1Qm80bWgvb3hLSldJT1ZSejRyRWdKTFN2SVg2MjZBZkZMdFdoa25OZ3hGZUM4aGtQTVV3K0ZYL2w5ZmhKeWtpWTNveUpPSjRKaVY5M2oxZkVGYU5UN2Y4WExpVjkxWXBzMnJLT29GNlRHNUhKdFpJRFR2SnNvb2ZNWWc4Nmdhc3ZFYW1hRXgrN0RrSWY5b2hPOUt5NndEODYyVjJDdDM1RldlendSZ3c3RXVpSHc3MzJCK1poWEVrWHNGQ2lQc2w4VDFyTzk1MDdpaDltSlVsekZhSGxDV2FEeGdZNzY3MjNvRWJheW5pcGZlS2pGMmRUYWNxWmZqZlpWV3pINDNLK3dWbmhXZ1hDYWJVWGNNb09nMWFDM0daVUtRK2xvbnRnVi9UMzYvTlluTnRsTGFZWnJlSTE0bGxIY2F2bnkramlJdUVDZ1FIYSthaWpnemxkZ3JCWUxiOEsrNXBnVzlaY1drOUhRUWpMdVlmdFRMQnc5dmRoT0NNQU9wOWVRL2JGNnB5SDZlVVJENWxHK3NzSzBGSlUvSDZvN2JmRGlJaUVCcEw5RGdIYytjZ0xNNlMxRDZGbDJjZ1dKRnpIWTF4MGlYTWs1TXRod05qcWtFQVUrV005b3M4RDlRWTYrMG5XWFpSL1BPNmxTYUtWY0Z5K05wZjNyd0NqQzk1UGpjZ3lqQWxRaUVlSllpSUlWQXNlaGhuTUxId0d0QkhlQkNDSzk4SWdTa1NFcXFnTHpGbm51ZW90OFNaa2toNngrL09sS1paQnNTTlNBcW82Q1VPdFEyZUE2QjFmd2lXUjFScnNaYnhhQzk3R3Q3UGNwN3RPa0JlVkJuc004NFpwcVNHSmRvSlNmdStkM3lxY2UxVWxuUUsrZ3dzYVdWQzdRMDFwS0RDY2FGRC9jeGVXUDlMMS82bUVXZXZ3OTRtck5VaGJqSzR1SG9aakdmSktrL0IwVXgxNE04MU9KZWwzU3IzcTJJVDdubWtZREZaSzJxWWxXN0tZNG0vQ3dGdVdhUUVFdFRHdVQwZGg1U0JySWlEbllzaExnUWRMWHZEd0ZZc085RmNXSkxVaFFYbjNzK1Y5WlZ5U0V5bFdONis3bWlGbkp0L2pFalRyWnBUZ0liMEorNHIxSm9uUytpN09Nd2hFclg2WnBNVUtOR2s3ZjlyY3B4TEVxb2NoaisrR3k1dGJrZnRMRVQ0d1dhRytqNEg0c3ArVi9nVUpadzQ2ckEwTllUQlJWT3JlQ2RRZU1nUy84UHJuVXhVMDFRM2JhUDMxMDZWQ2J1YW9ESFBVYkluUzgzOE5kdzZKR3hockYzeTltR1UwK3RrVkk2d2xVUG9LK25XQ2o5NXpqMDIzMjBXUmFFMTRVYy96RjJNZkVTeFhQejNsc0kzbWU4TFd5dFVOZ1ZjdTROQVEyMnJxQkNPeUt5U244SHJ1cTRmdnZxRlNsNWxWRGlyd2dNWmFiSXRwNVZVK0IrTG5iZndtN2Q1SVFOZU5uUEdBNkZ3eHNrb1NocGxoTEZIa1JBYkJzdzcyU1U3UmJGNFMvcmRXaTgrRTBxRER2MVhnYU11cEVSU0ZHLzlVMWZocExjdU1MR0g0K3A2RGFqb2hSM20xbkNzam1VMlN1MkVGWk1mR2tkWjlaVHVWZ2p6V1VhRUZqR1k3MnpHM280UVB4QmgzZkcrMWI0UDdHNkVlYzlpZTg2M0lybENLT2NVSUcwN1ZTbm1oTlVyYWFabXpDK1o3eXhpL2RlWUxmeUd5djhCRzVWRGQ4MU4vc1FHZnA0MDU3VHd5RDRMaXRHakt0d296dndsTGkrRGRocEZPRVNraHVvZ2EwZ3JaOFhkS1BHYVpkNzQ1MWF5bTZDQVFBRERLcXRjbkR2emJmL2VrZFBIMEcxZHdNMExmdlRrSU52UnhmaWs2ck1aTGhTdnF6dVRZZzN2MjltMXExbGJ0ZlVKekJ5Y05ZcEY4YndTbUIxU3lBVFlXOERnUk1VUVNWL2ppNHRycVlzeTdlM1RLUGxIelYxMmxSVmtnc2FOcEhiK1d1RG96eU9COXBKY3VwS1BNRlBXdzZ1Q2MyTkNHSVdjR2hNUFJlQXBxeHhhUjdlcmJnZnVtR3poWHNvTzFGNmlMMFVucDNPSGRVT0haaEhNK3JPWkNxRXN1R2xBNE1pMUxWWUwvNXQ3N25yWWRnRlV5YlJrek50T28xVE43MyttbmdOZ0xLZEZhd2RqeWE2TUV0UFk2eXlOTFhITDJjOW90S0tTRkVCQkRmSHc1MGhRWnVEaWVyN2RGUUNaSVJHbEtvczExdUNCNzYzNElTQVZSenA0Y0xIdWFxZGdlWVd1OUM3S0puamFUek1TRGUxdVF5c3dPT1U3c1BiUi9zQy9CcVQyalpveHc3VW4xSWM0aEd6WmFrLzBxTE1sY2NUeDBwOWoxMHNxSWw4WTRYeVdMa3E5U3Y4QnpIdWRQUnhUOXZhQjJLMzhTTUFBM29rZ1BNL1I2bG96dGtBZHp3UDFUR2hXNFRDR3VHSjdHcXk5eU9hcnd5Z24wYkhqajZ4bmdLUzg1VWVVViswYno5Qk9nVWJHb1RCUTB5d0lHK3Q5aEhpaHNOY3VWZjZaekdqbnhBSWUyenNLVW95OXZxU01xTVI3eXFDdnVIV2tQUmdrSWpuYWxqVnZjSmNiRHdpOXpyTzcwZURxWk5uNG0zc1pwQWxEcVdYVjFQREJHbm9kOHZCd2xwbUwrcVZZdklGdjdHN25keGx6V2E1ekk5SmJ3QWpEUWlvcUhvWXE4WHNXUlcvZjdDNXhsVUxzNmZyWGhIZ3Q1cHkwV2hFNnRwYmNNTUhKZ1cwajZEQ1dCUjhGMWI3WlZnMTI5WjJ3eWtaMUZLREdYWGVoZnF3cnpzQjlBTEpsRyttS3U1UjFTSmQxSzZUUUMwK0psWGdkS2FwKzFqV3RLNEFVZGxUdlhwcUR4ZVZOaThvVERwNlhxMis0ZnBVeHRwckFBOXJFUTV5YjNiblpFcjBta1FGUm5QMHRhaXJzL0RQVk1nTEN2OGl5VmZ3RXZjRnpzMFR1cWhMZU43Z3V5bDJxbDlkWTRIU1YzTlgvb2ozajNIVW5NanhWdGxwcnNabEFLM3lVcE1TUkdyaHFjYzVmUWNYZnpzeWwyMjlYbVVtQjIwemRmS05hWkdUMHc3YVZSWTdZemI5U1Z2SmNHWklXaEhldkUzcG1HRjROL2V0Y3ZVclZFTHI1MGlncTRMaGZXUDVoSW1JOS9XVlNpSDBjUVFYTHJUN0NZSFpFWGxCOWFGdFVXSE9jRTUzM2o0QXo4U1Rpc0dBVlE2TldnRUVhNU14VFc2ekF2bU8vWXcwTmxBaUZkYnhkNHRTc3lYOVc2NWJ3KzdMZ3VHcnFmcjFhellSZFB5U3JXdXNPazdibVpjQ29POVlJcTcxKytCaGJOUUNnSWljQ2VIclB3U01sakhrZWhqSWlBQ08wQzR4Z3BpOW5JSllWYlVsZ0RSSnB1OXl4cVB4dis3Y1N4Z2xsQ1BhZ3pUZkk1SXBaQ1hsdlR1NDlxUHlpcWdEclFyaVpoQm94NW5zV1dNNlZYUmp0aDBZMHdCWDRWMVdtMW8yd0lYUHFaUitkMzEyN2JRQnpWQ0ZIa0pzM3A5Y3pNY05ZcWt0UW1zYmdUM2E2MmdjZ25ZYll2N1ZlYUt5ekVmSTZ4cUlURitIN0MwL01oVlJtS05yMTIvdUtXVk1zQUhSanVtUVVpbUFWUG9EenFRL0I4c0dRVWhxTXBnMEl4OUtGcVFRS3FGcVhmb01TWkxGODErbUtiUzlMSFlLVUVRaldSMU5IWEZwM2ovTjk3cEp3ZWhYYlVoWE1kR3lXZFgvZWtGZ1hNQ2toU3NseU1YUjFYL3VYTHU0QlpzVis1QmZzNGNWMGs1cC9ZOXN1TGd5clBkY3drWFlhdmx1aGp1S1dNMWhYRVdyRGpOc0pUZEc4Qzd1MndkYjdrQmNBTmxXclJVaklqSlhjTXhGWnAweXY1R25RSnBqS2hzZEF1cThmSHMvRXFaYUZ5cWlpaVhSZmdUeXJueVFzVVQ2VkR4ZUZjckV6Y2dTZDZRaDRYeWFrK0xIV0VSYkxjWGRMU1BzRDdrcHIvcVZEYjdPY0I0Zk15TGVaR0dCeWdUTUxWelA3bHpJQktlVlZoa3BWTFFSVnIra2JUNWw1Z0dqTDNPNkI3MnZ6NkR2b2VhUHRoeXZ1Unl5RFNpNTlFZTB3L2pNUWEyTWhQcmNySGpES0xiYkpXeWF0Z1g5Mk0xTVdxSzU1aHhPOFRwcHQ2NzdpVkliNXdIbTE4azk3UkVWZWZXUzVydzV1UEhkeHVyS3ZoQUxuRlppRFZpSk13ZEpnZzZYZ25xT3VuVCtJeUFQYTBUWjZ4V0RiUGVyaHhnanVJQ2cxN05aKzNYczdmZjN3WkxNanhlcVVsT25zSU91K0N5OEwzMHBRdldBY1k4QVBxUFBIY3RESDk0NTN2eXhUUHFjZVBkRkh0L1VRSzd2QlJFalhVcC9nMlc5RE1SVGFQcURPZXFKMWU3eXlxaWpDT1NpZ2tEbHNTRGQ4U3RlN2N3TndHVS9jMHU2VklOSWRMM1VQcldqTTZESVlYYWJGdlU2cjVsTlhiZnBaTkZ2QnR5YU1oRnpadnZpYkFIaksyUGlOZVNPY0pNSmhjR2dCM2hmS3Y5UHIzU3NIdUhKcTBXM1Y0UmZwN0xGRFF1c29HbGt1aFRraWFxNXBzZVd1WUhoNmhnd3E4UkxuOGhkUmRMR2VmRmV3WDNWcmJjNGMzdVNrNVNFd2N2VkZVN3pQZHdQSUFJc0I5U0JJVWNzVGM2TGtQVHJpWVE1TDNuWTUxdmxjbjl5MEx2eHZjYzhpdmZiQzB1L1RjSDZlZmlRV282V3dWQUpPN3pCb2wrUUhvS0xUdUt6YmtoWHJQU2h1UUk3TW5GV3l5UWxtKzQzVjNoV0NBOGJETTFqazJhSlBsUHdhcFFtbFJaVFptNGVYKzg2cXk4TWVoTnhsdTU3dFlyck8zeXhPRGVzdzZwYzNxRk5aWkdPTVhLL2NOSHRuMDNDNHQ1Yjk4ZG5VeXhKL1V6WW85VFl1dCtIcnhEWUR2ZWhNN0lOc0pFWS94ZFk3eUdwRVRYdHNCUGpJRGM0ZGtZL3cxbmNqV3YreTNSVEJuVm9vRmNqaWNKRDJWYmJwOURZak5mRXNZcmRVZ0s2dDhYNi9lYVhFVnUwNVVsQTIyUHhvUDkwdkdPN0g3SUhyRHhyelBWaXhrRkVSK2IxQ0xNME9MMEROWGN1K0NPV3AwUEFTL0NwS3FWeEQ0bllNVzJPcnlhWGFQTXUxaDNGQ0szMU0rd1gwQUV4bXZra3J0SmVWNjZzL3lpYTF2QTE0MkhRU2NUalplL0J0NFJ3clR6S29BTzRpYVpYaGM4bGlRa0JzV2R1NXMwM2ZVSXFvRnRBN1QwQVBkajF3NXdZYmtVamZvRFhMR1J0L0NlUWVuT0hnVTJvdGxqZFprZGw2VkFXb3VqOUtBTERiVHdQL00zam12dWorNEt1OGRObVhrY2lCSmIreVIxa2dnbnArWWRUQzIwZTA0RFVXNHVTTTZSUURqOFpRcUowUmV3cHFuRlJONVA4dTdReVdpWVZVOGErNm52RnNKRWtrWTlranUrSTFqVXlPZmdPbWE3WVgyRWVSRkZ1Qml6Q1FLaUFtT09BNmlDeC9jYlBEcmdKeFI5SUVwdHQwcCtVQlB6dDRmbjBSSnhnSXJiZEN4aUNEZWN1TXFOeFNFdjJzenBSR0VsWE1ldnBQckEzZnBqbVFaOG54ZUZpNTcyZ1VZQ1FmTDVHOEpxcGo5VFBST3ZzUFdVKzc3R2UyZ0lMM0U0LzhETjdJYVcwU1I1d1l4RWdXSFdZZzYveGppaURtbTZxZ0tGenIrdHJzbXg1bnB5eTVYeWFoVUJOTFY0Q2JibDZ6aFI5dzBRMExqZi9mV29STDFWSTV6WElmR2ZqWmNuSlY0U05xVzRRelJQUlA5TnJEejZ1Tzl0OVdWd1p5R2tiS2dLbjdtcDZnVFBvSzJvSlBCMXVpMTk0eTBpTTlGaDBhWk12L1NoUDFTSVpGclJQVVBsVERvOGVpdFBXeStrQ0JaMFNWRjR0SGJpczQvd3laaCtpVGJJOFpJTlYvSWNVVEI0ZUYzUlN0L2Q2KzBpbzhKSmptUTBOdlhlejVZT2JVRzBZZGFWVmVLSWhCbE5yRG1wMUpQSVhOaytRcXB6c005SkNDeURQL0JPMGVRUnZzUGlFZXM4ZWRmTXdkajNJN1poeTJPQ0t6QWluZFBwQ3JwOXpqWitOQ2pVd1dSYTFYV0dlOEd0TTBpd0VoSWxsMzA0cHVoSFBGWkNVd3NTOXd0YVRJWjhxME52VG1qVlN1anhGRkI0WTEzRFo4T0dITENGNzJhRlFudVRuYmIxZTRZV2NBNFRhaHlnQmYrc3NWYkZUMHRrRXJoeWlHZUk1UXBraW0xb1BoRXZZdlQ0UTRScVBMWmdycG9hYXJwM1g2ZTNvYkFobGhsMGVyZldGN1p0SUcxamFWUEg5cVdHZ0hzeG1WUUFnZGk4ZDhlcEFvYzNwZGlTMmo4NUxrUDVHRUZTelRpMEUzb0R3dmZ3b3ZWZFhnSzZrZnlKZkhkK3doL3lkL3VySTBmMGJvbHBTRjB5eWhlOUsvWWNZclYrdUlQdHgwZlNoL2QvS3FpUlJZT0hIN0dqU1kzYlhacTg5UVMxNFBRNnRsbWpyM1lUbHhSR0s3SEZsVlVjZitLWHB4dFZKNTZlNHpHaTlhZ2NJZUJ3Ymc4c3U3ampXWFYxMnV6TENBVkV6SlJaOWxZOUJJWjlLNkRKMGg2RzRmVU1vUWlkdnBGWjBtZk91QWhQc08wRW9FeG1BVG9UKzFkQUhhK1c0Tk5CQkV3amtJZzk0NXRzWXNmcDlmRFhOYWsvM2FYSkNMdnRYRnIyd2pQdzl1L2Q0VGZGVjFVSVN2SkpxZm5JV1VOM3NCMldFVGhnYW1TSWUzRktjM3JYNmpicEZuRlRMcjErRjBwY0J3aTkvdHFCbUlENEo0RkpMWHYxdDBoajFTeXMrMXZQMlQ1Z21SUjR1VkI2akszTENJSGFMSVNKVEtZWU5SamRibGNTdVFtd3FXTm1kS21ZZGpBL3l6ODVRUjR5L1FUaXZ1Z2pGRDJjb2VDRjdtekpmcEk1ZTNIUk0wMEVuMnJYZjVpSlRHczgyd1JXUC9IWEVNYkdNemVzVVhHT0toMXZsNlBQZHNGRE5FbzlLWGxKcytFL0ZwVTNGWnkwNHVuV3kvMzNVbFR2c2JzdHRIV3B1VDhuRC95RnBsOVRORDU5NWZxSkdNV0poYTRKeXBGNThtY3JjSHVHRFZ2bnlUbE82MXNKT1dkUWhNYXVDTlVuRktyd0FWVXBWbUFtaDFtNWEzT2RhcUtLemhCMjFKV2tCMjg2aEtuQjB0Q3FwMmpZRVhpZm9uS2J6OHgxdks2SHhyVVhlRTdXMGFHOGEvYlBNcXFteFo2bXZ4d3B2d3ozaUp2SnVCN2hac0dWZDdDTmwrcENNcUlQRC9mZUdqZjFIdjkzZzhZalJDTVI2d05KVC9mNzA2SVh1WVpkZUdueDk5dUFrTHk5OE5JY1IrQmZXK1lBaG4va1l3RERtK0JzaTZtaEl2T1dycVplN2s0aHRIcGxSNUlPaUVuM2VOWWVLVzVncldyajBnMDFtQ3BCTjBXdEtpcnd2N1d1a1pRaTMvekNYVXI5LzM4TU90aVJQdTZ4a3NoZllkWmlTcE9vMGNvQWpwRER3YU51SXVtUEZFODd1ZDNYbE5IRjRJeWttRmNRTjFpWmkva1VyUGFmcWlRNFFFSFA4bVJyMDFxQzllVGNHY2xHSmdXRFZPclIxVTM4MlJtc2RsR2VXSGllNm1saC9RekhrcHNQdEFuRWJZa2NtdCtVTUpoVmp0UUI2WWRTMTNKTEU1SHl3R1gvMUpYWm15RGUzdnV4eXQyREFIdFJnZGxIY0ErbFppNHNVMEF3a0VBZHJDb0RNRUVRQXgyVVM0cWhTZlBkaGg3Qk1pMEtIZmszZlJyQ2J4VXIzL3JlVlFjNVhLS0dGUitaTSs0N0V1UVBoQ3lDZGU4WjhrK1Jvd0p0cFZMVEJvK1Y1VUZJRzJoVk02RkhtZ2FJTWZzMCtOeCs2YUM0dHFCc05GcVF5bG9kSkZudkYvWEp2VGlnNnpmSEcrOGw3QTl3YUw4dk5oaGFGZmF4VnZhbTJxQUhRSGZkdlZseU5zVzV0MGhkcXNJcStNTmdjRndwcjdKckgwYjNMcGRod1VmRlBiejBUTDZuVmFrYUpQcnU1c3doSWxUV1UvVHFjOE5zVldUUnJBeDlIb2NqWHhNa3dxZ3RING5pMzFlL2c4c0FtRi8yM3dMa1c1ak9IS0hXWG9OUThSU25Qa2ltSTFHZ1M5RVY3b3N0TFhwTTRtcGJzMEwvSVk0ZjY0eEcwUXBkSFFYVWdGekd2RjFlYWQ4WUF5VHo3Qy9uMitBUU93SUNwSTJTd1ExdVZUR3NqVEJ5VlJGQ3A5cVR1elEvZmI2eW1SNFRFZVVpcHBWVEgycy9HOCtBSHR4V3ducTUvQ1RUYnIvdy9MbktXNTZ4cGgxVEV0NFNFQytqS01MMVZKa0ZxNm9OcHJ2dVBwcEljM2ZwWDZ4RXFHajJGQ3B6TGtERlphc0pNTFF0dGo5REJMOHk2VlJNSGJWN3d2RGZwTEhUbWJGeFZLQVpqMi9lcFlFRmJidkFqRlZzeStCQzZneFhTSW0xNDhxRjZoSm5RUjJVanhTZWZXc3BqMlRFeDBqR0crYWZYSXVkMzVzYks1VmQvd3FaL3pEd0pObm5keVBiaGwrZm05RysramJVangyNzBhWXZLMjQ3VFlrblMyaXRheExLMEJRb0psSzhmWm5QWlROR1A0aGpXWi9ubzRJZnZYTERwdUc0SjA4WkFzelVjbklNT05UUVdOR0ZTeVo2UnBURFJySEtyQVdqWE9wdGlSUzViSHpBNk43LzNGNjJqdUZqOHJYZ0dNYVNKWHNhRjY5cU1wbWtIbEhzdldYOC84cmh1ZndvSVcvbkRlYUJQanpOUnVEU3VpL0dVTlQ5YXpGb3p5K2tLREhkMFBxK3BYU01McTZMUXlNdXNvRk5KTWxxWVV6c0hXUFluUFRYWklKai9VVTRhcXhaMXNhM0VnUG4rajFhdHZVSDNTbmlqVGc5dExaalJNeDMzQUd2eVZ0N2hmOHVrbFNuTVpmdDJuNUZYajdoRUdMU2tKRXZXYllkVFFvL0xrVlNrOHVNYzczZzB6ZnRFQXBpekxERDhrU3RYMnVOYnVaM2t1NnVWdjdNSWFzempnWE1FVzAyc2RxdTJLWVNLSFZrQVFHQWp5blF2MFFEZ2Z3emN6S0FHL0hacFZrekJkTjFjUGFxOU1PRmNUSytENmI5U1RzOHRGQjBjSXVzTG1kQVV3VXlzekU2My9jQks3YWR1RDhWQkJXTXlvUUgrczZLOW5Ud0tGNXNtZ0ExaVJ3T1BOTUJqRS9KVVJOcVFzbXhJVWQyZEVlcm92NWNaUTVhYTFSRTdhclZVQmZ4M0wzMWsyUWlmSSttRndtU3JzNjhnZmlVOXRWd0dtNkhWa3NuM3p5UWFLYmpDMlRhbWVUOUREaitXeGpuNHJkeWY4bmhvVXMwNGhCZUlORFNXelNLYmVEZTB6cFE4OFpiOWZmd0d5QUc0bGhibjFXRVFaY3RIUThYUW42bEZsL21JT3I1UWtVWHBnd09YNERtUFN6Mm1YOGJsQ2JHSzZhMWRoc1NFeExsL1NVVlIxVHR5Rmg5ODUwcXRncmhlTEtjYnF6WlZkd3RJOEk4TW1Ib0kvenh1cTlkRFd3cHlaZ083VnY5VXNLeDFBdGg2eWtaQnZIZ0lRQUxjakIveUp2bnQwbndXa2tIYmovS2Z0YTdLV1FNclRGTnMyWDZlYTF1TjF5b1RYeXk5aUxOZjd4ZU40OHRXenZoS25RODFIY1RZVmNmclVaWWsyNGxoZVEzUXBEa2RVZFNwYnFMUDV2WjJVY0todUdZVjRhL1p3N1pQMSswNzNjMHp0aTRmSkFSYXVCdjZkUzdBdENZOVZMdzVOdDhaNDVuWWNHWDBudUJKTE1UeVRKeEJaNHZxN3AzcllVOXFFVnFGVVlKTDkyZVM1aDZxb0N5ckM0d0FxeW5TTE5kTE4zRkxlUFVPdzJ1TVlCVE1heGVvSE53bWhFSmxTTjhSK0hPUk40c0lsWXB3UHNodjY2S1R0aDIxOEE1WFNPdlVqaFdHMDNrSVJESmRqTHNQOU8xdGM2akQyU1hLWmcvcS9KSEM1MkQwL2dFQk5iY2EyN0hUdzdDRWF4RlR6anhURDRTQzdxbW14Rm12bVlPWnpxTlltRFBoMmQzc0Zkbnp4UnUwdUJHSCtrRjNoWXhOS1M4dk5rU3RwUmdTaVhYblA0TnFNS1E5cDBIdTNneU9sSVBFMmlSYmpPVWdqMGhVcnhSQmdOSmEyOVBIQi9tSkZvNVNQUjl6S0tZaStBUWo2MXQ2WDBGZG1maVFUcXphcm1Vc1ZOUmlHY3djSmRPcnBTK1dtU1ZpN3F5dWMyU2hYVjd1dXZNTEJpZlhWSlJ1aFNacFREdzR6WWxuYmhxRUIweWtVOXdsNm5oMkxMMnhzaFlaTThlakNIb2VjZE5xYjVaR0o1bjJzM2dTK1phcTVSNGJ3MWtFVHF6cm53R1JJU251eElBNXU2bjF3WVdyb2I4TU0wd0lWMmNZZS9xWHJkUDdmbkpnZUtqWkxESzVxUjV5S1NlOUVJZ0o1Z29YZm9DODhpVmpXQzdpb0lqZFllZlZyOUdld052TUhnV1pSL1d6bjh2bllkZWc0LzI4S0NZM0tWZUtPQWlkbkJsalhNNi9qclVTMVpRRnpMQks4V3dzanFyRWFsdDVVYXlPZzlIengzUUI4TWV3bVFlVC9rR1FkcjlBMDlNeGJrRU9uWU4zdFBsdng0RG9kbHRpcGJsbW41REpuS1IzLzFiMHFwVXd2SXBrRHpBMWZkMHlxcXRhWHA4OFh4MHN4TW1URG1mbUxSNkFyU1lKcFFydklNRkg0ak9ObW9LQVRtNzQvc0NXc3M5M3ZFZEJMOGpRMW92Ymtxcyt6Sk5DR0kwWno5NTlyeHhPTWgrMzNROVJiWGNlRUtadWJ4VlhvUkRTaThWWU4vUjdvZjBMZmR1d1ZIaENNKzgwRUVMbG1JblU0OXcyOG9XM3VOZzZxc3VMS2lTRi95eTJVSGd3S0dHbmlKQ295S1Q1elFsMEovOGpkNzByazhwWERldTQzZnpQSDBnU2dvalNBb0greWtYSXE0L3N5WmhUKytvcUFkRzJyZXBLUHJSb2RrYWVTdzUrcWlpV2ZBTm9UN1kyQjNCZVNlYUZmMVVQNFR0Sk14WWlVN2hXc2I4cVJEbzBFc3pvSUlmSVBOeHJRd3Q4NWtqYWU0dmllZW5GaWE1K2RkQlZBOTVaM0VUMDJTTUlGRDZVTkxFQjA3MWlNcktUZDk4TGtJYXNjVm9rbk84MFFmTTRiaStPTFlKSEpzUzd0RDkrNWgxNmhxZHlHcFRvVk9sUFNqc0RXM3ZGN2dTTUlyM0hmSkhqL1NPbHV2c0lrVHhCK2dXQ1U5YzM1YVViYkZSbTk3NTB6TFUzR2pvdncvWFVMWkIwUE4rczFDRlVvZWE4K2ZRcS9QbXdOZjFuUEtYeERQblc0NW53SS90V0RNY0dkbUZyZ251S08xbzdTdVdQR1hxSGsxdU9jRGFva2ZwcE9RYXRDanB6UFJIRk16ek41cGdNWmg5TFNlMEJZTkRYaCs4aUdBNnhsVGRWc29jaTVrM0NzZzFYMG1jb1FRWlREMlFNSWcyL0t6TmtmaExGWUN0RVp4T3RWak9NYmpuT1ZjM1dFdmwvMitOZXF4UTdtdE1tOGJMclFyYU1mR21YVHlYU2dyZ2pyV1RERnVZaGl1TmhEeDRDVXJ6T2JOekZ1bFdzTXZqNS9EWTBod0dWSUtmVk1uUCtxTGFOS2xidGdHUklXWDY3UFYrTnk2Szl0ZWtSc1lRUXpuK295Q3BjU2tOTjhOOS9VakVnaFJDU3RqYmhNQkFiL1JXRjlwRXRCTmh5ck12c0Npd1pQT0NHNzZveUVCUjkwRlNUMmE4MjBGU3dhZ05TQ0J6WWNNeHEvVkJBWFdmVmRLQ3hUMVJ3bzlnRCtueXU2bVYxMytnUTZPNy9kTEJmcHloamM2U3NxOGZIYUhXSkhiS3lDa1FSeTdiTFdXTE1EK3MxSFZqSnRiaTFXcGVLNy9nNEtNY2tuc3V3QUZwMmJlclhackdFa0RXaHVBSTlVQ2tZcnpuWHF1ODVkWEVGc05XUlRRcHRWRXlpdkVuSS9oN2JRRGEwMjNjQ0kyVjgwQ1lhUkk3QUtxMStoTTJCTTM3L1orN3dPSVVZMUw4cWlmTDRRamhSQ20wWTl3b1E2Rk8xc3l0SG1TZDAxMy85aFU2bGpzdUZPbk1RNUFVOW45SG1JMFZNUWNWSldyNEN5cXF6QmVyVSs5Z3FXUFJaMjFFcXlJTkg4SUUvZVBNcklMbzYyVjZkRXBUdFNBcVEvMWQ0YmdLRVRmVHpEbGYwN2RZeE8wV05NSVdOYnIxbHZYeDlKOVdoV0szS0pxZG8xcmlidnFyY2pucjBiQW80eitNZzdLVGE1WnRZaC9NaXlia0F2Y0V6Q0hldFZaU29qbEJIenZVSU1lSXJxeU9qNTAwWFRmbXRKRmI5R2wyeDdvUFM3djBxZWRFYnU1TFdkWEFIZjZpMjdMdmN4a09TTFR6VWI5UjduckZKQ1VwRG9YRnlVUHhnQlJvcmF5a2lvM2p2VmNnRXAyYTJtZjQxclhaUUROaVdQVldRVFZuOHRONjhDTExtNTIvaW9TN3ZPbFlEZVlyNlRsaW9XRlRnUmpHaDZSOE12c1hFR0VrRmVKZ3A3Mlp6aGhOK2xOczdpS1RxaytIN0ZEZFNQdkxaVi9BSTVzb0ppdXBpdXNsbWNoRmdvNUdyN0lacXJqalAzRmRxZzRUZ3JNTnFBM2x3VnFnMS9pbUVyRjFmRERCb3piejV3elJ5TXNzSWpDMyt1Y0pqMDN2MUdBMG1Xbkx2NDNLeWZaNkxLQXJucnZacTJGUFFidklKNlcyR2dEQm5haFlyYWdZWnQ2aVZuenB5VlV2WmlvV1Z6R1V2NzI4ZjRjbVpTRExRYTlYK1ZQTXNNT21xSmxMT3E1NFoyMXcwN2RkUzhzTzMyUE9IMG1aL3ZmcHMxWUNTK2oySldjMWhWR1MwQTRXZnNOb0tmU0RVdGoxeDFnTlpZQ0xtTlhPSFBzaDkxVHdxNTE2aUdrb09yR3BEdWRGVVVTeVlUVVdyWXpxWDQ4QURSUllVYlkvR0xaRmNKVnNxWk1PRHNsOTN0MGF0M25wKzAyRUU2VmczdE1IQ2FLM3VXdy8rU2J5YkFDMllzNnc4ZnorQTVkVU9pVEs4NHp3eEZIQzFpN0NBcVl3WlJrcjBGbk8yQk90QWR0MlMvSjNZSEVkNldGaWJHdVRzRENzWldhd3AxaFc2dFN2b1krTEN0NXQ5bVd1MmhUeE1RQ3JKS3BSTk51czRLeTRBTmY4cFU4ZlpLVTJUbHI2cVpQNk40RmpyMEhDOXVFWFpoMUNEdUNNUmdoeE03QXlTYVl4ZE1mUnRhWmR4UmJ5a0FrQlZkWFVxSjdHMFpGMFUwWHBINmUzUUg2eW1JVHVZQWVBS0IrZTVISHM2MWVpYjhzc1NFQ3BkNWNyMTZ6M3ZHa0ZRVGMxR2RtblJnODk1MDlaL0NJNENVU0xKRUtCemN4YkZKMUZVT0VGazdPZmtzWTVNdG9xVEdOaFRkTC9sZy94dzNmWFRlL1h0WTg5YkhxZG04MXNIUFNNQzBibFZmcDV3dzZ3UURzQkYweWpYMldPMnZiZHkxbTdCUHRNRDE4c2lsd1VYU09vWHVvRDRPTjdyUnlIQXhKejYvWDVLb2NrRHl5dmRoMVFKQWtQczRhaXN4UkNwcDk0d3VTaVg1SFA5eHZVYnJUS1BqZGkvRzFCTzA0Y3dtdGFWeWhsZlA5TFhpZER1dGJENkhUMmVOWFpBc1k0L3lCQ3A4VE9LU0Y1cllDdjUrZVJwM2o1SEx4SXdyUVQrWmxLZkcvQnN1ZHpKT0NQVE8yNFlEVk1FdmN3Rmx5anlsajFUNjh4amI4U25nNW00Q2tyZzJ2Z0oydE5jNk5WTXJNcDkvdjg0eFh1ZnBOblkyL0FSL0tzVTRNZmJWSTM0RlRjVFd2c0pMREQ3aDI5aU1pZDU5Y1ZXWG1NeFg2V1FETUdnNHgxV21vSjd3a0FoQUxPc202Z2xPZzA5akNaeGdrelFGSGRGb3NsbVZEUlpCcGg4TkZrelBOM2NHVVVDVityNGVzdk9RRUZCd2lSa3ZPd21Db3hoU09XcTYxckJ5L3MxclAzVnpxdnZCbHh6azZQSDcvd1g1SVJOeXlrODdQdi9VV1ZKUkhON3Uzd0pKeDYxRXFMUlVuN3dRT1lENGlZcEVpY1ZnRkwvSGZkM0RaR2FyWG5Wa3FDWVRDYWphaGFEQldiZFVCYjVtZE0yT25IbmVFSTd2OERTcHV5RjdYcGJCWjN2Yy9ScnBva1VCeWhIU0d0ZmUzWDY3eEg0dmtzTDdBU3p4NThoc3A5N1FMWjB5SDhhQ0ZCQTE1S21mNktoMklXWDJ0TlQwVEVUcTFQdlptYXVtNkFhcE9OZzdNMnVnc1ZIcG84Q0JacEp3RS9qZTJ2STZUWTJYNHFGd0JPMDdFRmZwMk1XYlI5TG5MSEFTY2N6aU01OHdpeFFsRlNqbWE4Rk5kbDdUK0ZsRWFIUXMvTk9xdTNDS1BNaDBYY3R6UVUvL1E0aW43ZWpTWENYajBoS0lYRWlCb2N1aXZDQ1dZSFFCV2o1cnlzOVpaYW5HVzRGd3VwSUZyMi85c3BXSkQzOE1zeEdwV1IvY0U5NSszczVBYmhadmdYR3RpOGo5TGJqbWFLdG9KcnNFdUkwUjZZcVV5R2V5VTBaZVk0Uk9ZNm9qMWVjSlpYaVhEWEpHN3BFSkF3ZlVFVThGMkhVcVpHb1dYUnJkb0V4SXJ6UjRvOWVHNVdTcFpoZ0FqSWEyMFVmWktTWWc0VkZlL29UNlBUeC93VU5UaS84VmJCK0VTWlBJNzkzSHRxZUFOTGpraFg2b3JGWDNadmlGS3hORW8yWTc1UmZFR21qOWZyZ2MxSXBYbXJ6ZFpleWp6Vm94ZmVCOWFna2t6cmVGS3grWWNOZW8wa3E2Z2lNQkkzUVdnY2NEdHhmZHhjOGVvTllNM3N4Y2I1UjROeDN0L3VzVVhWbjlKaDdrTFF3RHEzN3lZblE5cTcxbk1ubXRqQWxOZDNtenNOV2U0T1JzWXpKZ3EzbUMyZFlrcTRwNFljaHUwNXJZY21KcTFlS245QkZpZnEzTzl0TlZ5TXB6SFRFTGFoNFJSN1J5eitwR3ZUcmlVems3Q1dVczU0YXdxRm1hbGdGdG5wL3dYNmxkSXdGYUhZd1I5VnZ6TW01bTQ0TThGL0hoTkdkQ1h6L2VVTk5BUVQzSjh3Qi9QWC9TbDNuZ2Z2OTREcnZCZTZQZzVydlEvY0FtVThpWjVmaDBBZUtvczJJQ2hBeGIxTzJlVEJJWEtGQ0NtT3Y5QzQ3UVl6MHV3Q05mSmRBSFlQa2tMT1lyeE5GNnM3TW1YQmY5RzFXSENaWjd2M3ZqVVp3WlJSYmVQclNaNk1DUDJoMU9Kb1NJcnc1c2c1UE1rbWZIUUY0M0EzTGNPMGpzT1l3UHBNcVlIK0d6Nk0rb1lWTGViTXhIbEdHcGxERVFkclpvTUZFMGRGWnlzcTJCVDd2ZDlObEt0a3lsZzZ1RVVYYk1zdWN6M2RxUjF0ZXJLaTdWTkNidDdFUVJ4V1habXFzSVRERFhHckVDcjdOM2pHZDdEUUl6UWF4eCtoN3h6WkFnTHB5QThFdXp4SnYyRDNYcE5hRWZIdDJwSGxKWllubXNxckNlVkpoN1BIRjM3Y2dieFNFYnRJamhLb3JFQTZWNEhPNW1LbWc4MUxIbXJhcUloa1VVMkh4U0JZSGhVU0s3aitOWnM5c1BwdW9pSjJIK2NPS2NhckE1eTRqMytkK2NmU29YYy9LRjB4RmpGZ0NUNlUvU3R0czJtRTJKM1FOTk0zOWwxbkJoSU0rckxyY3RHTDgwOVdFcWdwSG5YUzJLYnRPT1FXbFJuTFFPTHNJNnJLSFlXVVN3czJRQVRCNklXUEVBV1M0UEk1NnNVUWh2aGFnZEtjdGdWN3YzUlo5bmlpRTVmbThlL0lOUHpKNU9ZSm90bG52MWpjUjhmVlhVNTlpbUdMSVpMUHJkS2tkSTA5V1dQdnlYYzFjemtMV0o2MzZyazc5TkNjZDdxUDE3OHo3aDlEdnVZNHlPaDVSMjE0WWZhUDhtSUIwcDU5UTJFWXhocGZkMnZTNjQ1azVtZjVSMXBaZDNoVlNYN1VDcUtpdGJsYU5GT0d3VXN0YzZwdlhDTC9saEtHdmVPOXU2QytzQ2ZlRmVhYWhPS3B0bHlGTVJMdklwT1JNUEE3a2h6ZklvbnFsS2xzZjh1YTN5RWFwSEZNanhuS1NDbXVObHhkU2luYm5zSjliWnZxYWg4NDNWRzJRcmd5cGRkd1lMaTQ1OVNDbEdYUHVDRmMwUUlKU0lMdytVM0JlK0J5Lzh0a05GNEJSeXdLVStqTkVXLzV4WjF2OXdwNHRtWnY0THk4UGJvc0pCNmF1VkpNdmJVODBvcmNpTC9JMVd5dnpoTnVPUWRSbjZvSjJINURLTFU3S1BXTm9LRG5qRHp6a2dabzFqazFxbitRb1RKdU1ZN0tFS1B3MnlwK29UcnBPVGkzL0lJWGpyQUwxSHlhdGdwN1VMQ1l5SENROUhQUXk4cGUwY1lXZW1KTmpxTm14UTY4ZlBvemMyUHBaQnFuK3B2Ny9iMGRTRGt5Y0pNWERjZGhnUGlPbFVmMGE0R0w3aWJFS3Zyckh5S0o2VDIwaFJ2Nzk0dktpOFQxMm9Qa21HWEozNVYwNXd1NW1hL2J0ZmxUWjRKZlRYUEJhVlREMEErYkRkclZKTDFKSVVNOENCSisxeVdrb2p0K3dXL3hqc2d5dWJQOGI1RDBDVmZBQXFPbU1rMllQdndwVng1S3dxdFhoam5GZmR3M0o2dmNLSCtZZGZDb2tidzIzQVM3TTVGSlE2a04xZmpQbjdvMlExekJMMXlwZkFXaE1DVDhUcVFQNCt0eVp6Ry9HUWpDa2lnc2pwSnlDbk81R2xKT3FrVElrU09mS0w5bVpaT0l0cWFuckpkaG9POVVCa1hCSS9ZU2ppWHF6bzlzbTRUQjhvT29NMlY1NUZOZUhYcVZPbENFQUNhem1sK1JtRkxCcWNnSW4yZGVVN2EvNTBHVVFLYXlNQW1wVTB5blVseWxBM2JieTZNaDBqeGF6RVkySjJQYVcwUVFlMEYzZlZDVktsTWIzbmwvWEJOTDRwenZHd21BUC8yMGg0Zmp0aVpVZzhBNThuRkhGZzJiQ0xMSE1kanRYekxOMHhoaWl5d2x1L0JIanBIaGRJMDI0TWR1RTR0dkpHWno2MEZXTFdBaFdkbUdWR1QvZWx5emJTaVEyY0ZseEIwdXpJSy9YVjJDZkZxelo1K1N2VVUwUXViQ3BucHMxbXJNUFdWNkdaNXpTcW1mUXZrZUErdVpudFVDMlg2TWJnbkhYNmIxUDR6VkFyWXd1dXlUOGhKOTNXUjR2S2gyRjhyR1hCRXVUSGZwdHUrT2hUZkNsVWg2L3pLYmlkVGdWVEpWajNMeXpPdFpHVUR2WWd2OXE5eXd3dDZrc0orUlg3Q01ET01Cc0U3a2JLN0lnb0lKRWwwdHR0dUR6YWljU2VvYVJ3TFFYZUxGdU1POHNYQUpRdHovYUFoLzVlUy9Qd3ZISzk1ZTJ2YzRRV1YvS2w4bEV5aERhN3U0Y3FJVEhvSGs0ZGxjOEEwbFh1djlQQlM0YmNVTlNZRTRKUXMxRmVTTklScW4wV21Sb1MySDQ3T1pKQVZiL2FDUVZYNGd0aWlrbU9DZkRvK0JZMEJQaHlaKzI5R3lUNzdtaGJYamhBTXBDU0gvT1VsWTJCMFR3TkE3b3JGZXJkbWF4N2lxc3EvckE1RU96dmhRdzNxck5QV2VwaXBQaDczUHRhMklkcGkzL2VRSEtxZE5LY1RoUUx3SDRmRXFYMHB1aTgrNmRyNWtLTjVRU2h6NFRxTUp4V3BkZklkalNjNlpFVkVxOTFreDVwYkpYek95V3pKSktQWDhaTDBjazdWeFcvT3ExVWw0NnZVRFd6aE1xZklkZGlBV3lNcENMcmRFaTJGTVNaYnA0TGxndFk5WGsxRFpUZGNKQUEvUC9QMDNEd3J3eUlnaUI1aGkyU1JKN1ptOU9EMnFETGRRczFBVVI2NFBqMnB2R1lxazM3Uy9SNDV4QmlKN3NXalJFZ29weTdiSUpkZXZya2Y4d0dQNWphN2RPU0xmaERIbWw1ajd0dHNGZnlDRFh6NTdtenlxM0tyVzVUSkRpM0Y4eCttaWUvQzV3VmdRNUl0NXZIWjhxYWoydTNWK2l3Yko3OGhYTFNmeDFucktZTTNQajRRT0JJU2FRWmJ5bVY1QVRnVDBIQjhGRk9mdFJZeGVtbmpsVllheU1lQlc1T3pCUGdjYitPbXJnY2R1RjJRQzNNb0xqYmhNS0xKT2pCSE1xVWRRRk81MjU2VGx3aWVoYXR3a1pkcXhjWkM5TmpGQjBFaHRRdjBIdSt4RUdzVkk1VnJtZTltTG96WURNNGNxRVVGdnhmbXIwNStKV1BRRUpFanUvZk11U0E3SG9KS2pjNFFlWDhMSnBVYUlTSkVzMmdEUFZCTzl1SFduZlN2bDNwMVdmTy9ZK3FRQzBjQ1lxV0hMUVAxYTdYN3NoR0ZPVVI1bkhMQ2d0S3Q2K09CK0JFNW50dnA4c2o5QlhPQlVUY3ViMi8zaEVwUk5Cd3NmVkJnNHhIN0d3bzBFTExIWUxtN2hqV0JrcmFLV2Rtc056cDNPL2JJcXJBNWM3L2FaQ0hFOGMrSEFxcmdSRnFmUXZYM2NtMnhxN0NjQm1NYmVqZU5lbWNjcFJxc3pjM3lKL3I3M0ZRZHMyRklvVCt6ZTRLV0R3NzlnUWZJMXFFZFA4VGU4VzFjVzErU0dkcThMUVJPbzRTS3dGVUhGWk5vTVYvSi9vNS9DQ1A4UFYwTy9UL1JJeXNTRUU3c3g2c2E0VUNDOHZ6ZjdvVXoxNUk3MWVlVzV6dmNrOXVWUWRqSWNGN1ZQUUYwU1UwclR3U0g4OHJaVTRFSUgwa0NVNWV2Yy9yaGJHWjcvR1V5Q3JBbCtYcy85U3RBUFlJdVR6L0l2SEdBbWpkRlhpaWdhV0JnUFQxdFVURjIzOWgydWZrMFVPSzNzZXRSSUdidHhCeEJlTXdqMTJFSXVSYjBkaXk4SXZZd3FVOHc3d2ZPVy9aNnpyNjJGZ29BNFRwbU5nQUdWOGIraG9qWUFMSUZTY2VBSTk0SXE4TUxzejhkMVBpUktzUDZTUUIzT2dUTGwzZlE3VmhJNE83WDZKQnpKeDd3WGRicHlmU1l5S0tRei9LVlIwRUtUdkYxSndpOU9NTHY4NlZUenA3SUxVOTgybFhTZFRJNXhEOXFZQXpMaGVHVmN0SHZwcWNBSjN4WEowRG9xZGJZL3lyWi9oOFdjOGxjVm10NW81VGx1bW9ta05MRVJ0TFpWeEEwUjhDb0FlNTU3N050RGsxdHJsS21JbklXdE5QYVBtOUtXaW8vOGp6OHNIUURnUTBPUHEyWE9vbms2SmZwdDM4elovMmtqMjZTcjducjI3SUZSR3hyeUp5czZsdkZkU3VOWElWN3g4ZDFacmkzbXVhOTVjaXAveFRRdmlFWVg0dTl6Q2VmTnAvYnl2aHZKVGZ6MGVQWmw1Nm9sVHg2OWszSk9OT2Y5Y25ZY2NPbG1CNWphWVQxa3ZOVXdUNUJaUmhoK0hPeU83QzRUa3BJbFJ0bjZDeXNuVXozS1pwSzlpMWx4bVM1S3ErRExkL1dyNExFN3BZc3psMnAydGhxV3NKVGxRa0xNb05mWExIMXNZVGkrRXBvaTNITVhkMURPZ2tVdFc1TFg0WGViYjFmaGU5Y1hEdHllQjg3ZFllVUJJSGZ1L3RXVmJJb1Z3L3BmRnNDZzNCZHNUb0UrVVN6S0ZEYXFZaTlweWdVZEsreFI1WkF4ZWNjeHBPUEplcC9qcVJIV2Y0UzdqeFBCTmJ2NzBIVUtKZk04L0x4RlQrOGNKSkcvN0tORFdQdm5mNlFTYnl1SmpiSHBEVnR6d25tVzVBVzJaKzlPMVIzU2xiY2p5d3czb3JPM2d5bStFRTB4R21id3JzK04yZnVtNUI4K2ZlNlJKN0treTE4UWQwMlc1SngvTEN6SE8zNzJxOVFuTzI4emM3SUMrbGNraGpBdVo0MkN1N1llMHhWSHA0WFd2TFNUcWVnTEdxRkFpVzQyaG5LNTRSSWhXd1k2cWJkQXlWRGxaRk9HQ1d6VEF5OUxucnFCYkFlYVI5cU9pMlNubGJQck5tdXpWcjQ1cXJSOS9XbmU2eW02aHJQRFh0NFZtYVRhVmVTcUI5MkJrV09YRWtTbUZDY2s5VkFZYzVrK3lZWDVhSlA5dzVFUFdiVVpVbExLc0c5eHhrdVYvemQwa3JvVWdIQVJPOG1tNWZjRk5SUXd1Tkd5Q2lmZGNhTXE2ZHVzMEFzczZBYzNnNVZIOVZVc0ZRT3IyS3I0NUdZVklIamhYN1p6MUdkTGxtbXZRRlo4M2x6RHpJTm5lYW5vS1ZhRkZYSVdXMFBzNnk0RzVHL3A3dDBHVjdSZ3IxaktHTENZeFVIdHVEdFNLbHJiM3RqeFJKNHVFNlFCL0ZXQldyMlV6bytwK0ladzV4bnZTT3N0NTNVYUNVNjJiNEJXUkQ4aGNQUUFacy9LK0trYS9NMmNpeEFyZncrNWl2QzcxaFJTOXBhbHRQbkJIQWpXWTB4V2VZRkpkakhDVFg4TjZvckR4NFI2TWN5OTRVckMydjZkTmxuY3ZiLzlBU0x1cFJVb2RpeTkwYVFHR0FwcGVEOTQ5d3BFWUwyQ1ArbkhwRlZya1lmTkVBeTV4WFUwVk5SalB0UkVtdGU5U0VWWWZvakE4ZFgyb3FYN1p1bXpDeTAyQWVuLy8yUT09PC9CREI-CiAgICA8L0JJUj4KPC9CSVI-Cg";
		List<IdentityInfoDTO> identityList = new ArrayList<>();
		IdentityInfoDTO identityInfoDTO = new IdentityInfoDTO();
		identityInfoDTO.setLanguage(null);
		identityInfoDTO.setValue(new String(CryptoUtil.decodeBase64Url(faceData)));
		identityList.add(identityInfoDTO);
		idInfo.put("Face", identityList);
		return Map.of("Face", new String(CryptoUtil.decodeBase64Url(faceData)));
	}

	@Test
	public void buildKycExchangeResponseWithFaceDataTest() throws IdAuthenticationBusinessException {
		
		String dummySubject = "dummyPSUToken";
		List<String> consentedAttributes = Arrays.asList("name", "gender", "dob", "address", "individual_id", "picture", "sub");
		List<String> consentedLocales = Arrays.asList("ara");		
		String idVid = "12232323121";
		KycExchangeRequestDTO kycExchangeRequestDTO = new KycExchangeRequestDTO();
		ReflectionTestUtils.setField(kycServiceImpl2, "consentedIndividualAttributeName", "individual_id");
		ReflectionTestUtils.setField(kycServiceImpl2, "consentedAddressAttributeName", "address");
		ReflectionTestUtils.setField(kycServiceImpl2, "addressSubsetAttributes", new String[]{});
		ReflectionTestUtils.setField(kycServiceImpl2, "consentedFaceAttributeName", "picture");
		ReflectionTestUtils.setField(kycServiceImpl2, "idInfoHelper", idInfoHelper);
		
		String resKycToken = "responseJWTToken";
		Mockito.when(securityManager.signWithPayload(Mockito.anyString())).thenReturn(resKycToken); 
		Map<String, String> faceMap = prepareFaceData(idInfo);
		Mockito.when(idInfoHelper.getIdEntityInfoMap(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(faceMap);

		String response = kycServiceImpl2.buildKycExchangeResponse(dummySubject, idInfo, consentedAttributes, consentedLocales, idVid, kycExchangeRequestDTO);
		assertEquals(response, resKycToken);
	}

	@Test
	public void buildKycExchangeResponseTypeJWETest() throws IdAuthenticationBusinessException {
		
		String dummySubject = "dummyPSUToken";
		List<String> consentedAttributes = Arrays.asList("name", "gender", "dob", "address", "individual_id", "sub");
		List<String> consentedLocales = Arrays.asList("ara");		
		String idVid = "12232323121";
		KycExchangeRequestDTO kycExchangeRequestDTO = new KycExchangeRequestDTO();
		kycExchangeRequestDTO.setRespType("JWE");
		Map<String, Object> metadata = Map.of("PARTNER_CERTIFICATE", "DUMMY-X509-CERTIFICATE");
		kycExchangeRequestDTO.setMetadata(metadata);
		ReflectionTestUtils.setField(kycServiceImpl2, "consentedIndividualAttributeName", "individual_id");
		ReflectionTestUtils.setField(kycServiceImpl2, "consentedAddressAttributeName", "address");
		ReflectionTestUtils.setField(kycServiceImpl2, "addressSubsetAttributes", new String[]{});
		ReflectionTestUtils.setField(kycServiceImpl2, "consentedFaceAttributeName", "picture");
		ReflectionTestUtils.setField(kycServiceImpl2, "idInfoHelper", idInfoHelper2);
		ReflectionTestUtils.setField(kycServiceImpl2, "jweResponseType", "JWE");
		
		String resKycToken = "responseJWEToken";
		String dummyTokenData = "dummyJWTTokenData";
		Mockito.when(securityManager.signWithPayload(Mockito.anyString())).thenReturn(dummyTokenData); 
		Map<String, String> faceMap = prepareFaceData(idInfo);
		Mockito.when(idInfoHelper.getIdEntityInfoMap(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(faceMap);
		Mockito.when(securityManager.jwtEncrypt(Mockito.anyString(), Mockito.anyString())).thenReturn(resKycToken);
		
		String response = kycServiceImpl2.buildKycExchangeResponse(dummySubject, idInfo, consentedAttributes, consentedLocales, idVid, kycExchangeRequestDTO);
		assertEquals(response, resKycToken);
	}
	
	@Test
	public void buildKycExchangeNoLangTest() throws IdAuthenticationBusinessException {
		
		String dummySubject = "dummyPSUToken";
		List<String> consentedAttributes = Arrays.asList("name", "gender", "dob", "address", "individual_id", "sub");
		List<String> consentedLocales = List.of();		
		String idVid = "12232323121";
		KycExchangeRequestDTO kycExchangeRequestDTO = new KycExchangeRequestDTO();
		
		String resKycToken = "responseJWTToken";
		Mockito.when(securityManager.signWithPayload(Mockito.anyString())).thenReturn(resKycToken); 
		String response = kycServiceImpl2.buildKycExchangeResponse(dummySubject, idInfo, consentedAttributes, consentedLocales, idVid, kycExchangeRequestDTO);
		assertEquals(response, resKycToken);
	}

	@Test
	public void buildKycExchangeNoFaceDataTest() throws IdAuthenticationBusinessException {
		
		String dummySubject = "dummyPSUToken";
		List<String> consentedAttributes = Arrays.asList("name", "gender", "dob", "address", "individual_id", "picture", "sub");
		List<String> consentedLocales = List.of("ara");		
		String idVid = "12232323121";
		KycExchangeRequestDTO kycExchangeRequestDTO = new KycExchangeRequestDTO();
		ReflectionTestUtils.setField(kycServiceImpl2, "consentedIndividualAttributeName", "individual_id");
		ReflectionTestUtils.setField(kycServiceImpl2, "consentedAddressAttributeName", "address");
		ReflectionTestUtils.setField(kycServiceImpl2, "addressSubsetAttributes", new String[]{});
		ReflectionTestUtils.setField(kycServiceImpl2, "consentedFaceAttributeName", "picture");
		ReflectionTestUtils.setField(kycServiceImpl2, "idInfoHelper", idInfoHelper);

		String resKycToken = "responseJWTToken";
		Mockito.when(securityManager.signWithPayload(Mockito.anyString())).thenReturn(resKycToken); 
		
		String response = kycServiceImpl2.buildKycExchangeResponse(dummySubject, idInfo, consentedAttributes, consentedLocales, idVid, kycExchangeRequestDTO);
		assertEquals(response, resKycToken);
	}

	@Test
	public void buildKycExchangeNoFullnameDataTest() throws IdAuthenticationBusinessException {
		
		idInfo.remove("fullName");
		String dummySubject = "dummyPSUToken";
		List<String> consentedAttributes = Arrays.asList("name", "gender", "dob", "address", "individual_id", "sub");
		List<String> consentedLocales = List.of("ara");		
		String idVid = "12232323121";
		KycExchangeRequestDTO kycExchangeRequestDTO = new KycExchangeRequestDTO();
		ReflectionTestUtils.setField(kycServiceImpl2, "consentedIndividualAttributeName", "individual_id");
		ReflectionTestUtils.setField(kycServiceImpl2, "consentedAddressAttributeName", "address");
		ReflectionTestUtils.setField(kycServiceImpl2, "addressSubsetAttributes", new String[]{});
		ReflectionTestUtils.setField(kycServiceImpl2, "consentedFaceAttributeName", "picture");
		ReflectionTestUtils.setField(kycServiceImpl2, "idInfoHelper", idInfoHelper2);

		String resKycToken = "responseJWTToken";
		Mockito.when(securityManager.signWithPayload(Mockito.anyString())).thenReturn(resKycToken); 
		Map<String, String> faceMap = prepareFaceData(idInfo);
		Mockito.when(idInfoHelper.getIdEntityInfoMap(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(faceMap);
		
		String response = kycServiceImpl2.buildKycExchangeResponse(dummySubject, idInfo, consentedAttributes, consentedLocales, idVid, kycExchangeRequestDTO);
		assertEquals(response, resKycToken);
	}

	@Test
	public void buildKycExchangeResponseMultiLangTest() throws IdAuthenticationBusinessException {
		
		String dummySubject = "dummyPSUToken";
		List<String> consentedAttributes = Arrays.asList("name", "gender", "dob", "address", "phone", "individual_id", "sub");
		List<String> consentedLocales = Arrays.asList("ara", "fre");		
		String idVid = "12232323121";
		KycExchangeRequestDTO kycExchangeRequestDTO = new KycExchangeRequestDTO();
		ReflectionTestUtils.setField(kycServiceImpl2, "consentedIndividualAttributeName", "individual_id");
		ReflectionTestUtils.setField(kycServiceImpl2, "consentedAddressAttributeName", "address");
		ReflectionTestUtils.setField(kycServiceImpl2, "addressSubsetAttributes", new String[]{});
		ReflectionTestUtils.setField(kycServiceImpl2, "consentedFaceAttributeName", "picture");
		ReflectionTestUtils.setField(kycServiceImpl2, "idInfoHelper", idInfoHelper2);
		
		String resKycToken = "responseJWTToken";
		Mockito.when(securityManager.signWithPayload(Mockito.anyString())).thenReturn(resKycToken); 
		Map<String, String> faceMap = prepareFaceData(idInfo);
		Mockito.when(idInfoHelper.getIdEntityInfoMap(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(faceMap);

		String response = kycServiceImpl2.buildKycExchangeResponse(dummySubject, idInfo, consentedAttributes, consentedLocales, idVid, kycExchangeRequestDTO);
		assertEquals(response, resKycToken);
	}

	@Test
	public void buildKycExchangeResponseMultiLangAddressAttributesTest() throws IdAuthenticationBusinessException {
		
		String dummySubject = "dummyPSUToken";
		List<String> consentedAttributes = Arrays.asList("name", "gender", "dob", "address", "phone", "individual_id", "sub");
		List<String> consentedLocales = Arrays.asList("ara", "fre");		
		String idVid = "12232323121";
		KycExchangeRequestDTO kycExchangeRequestDTO = new KycExchangeRequestDTO();
		ReflectionTestUtils.setField(kycServiceImpl2, "consentedIndividualAttributeName", "individual_id");
		ReflectionTestUtils.setField(kycServiceImpl2, "consentedAddressAttributeName", "address");
		ReflectionTestUtils.setField(kycServiceImpl2, "addressSubsetAttributes", new String[] {"street_address","locality"});
		ReflectionTestUtils.setField(kycServiceImpl2, "consentedFaceAttributeName", "picture");
		ReflectionTestUtils.setField(kycServiceImpl2, "idInfoHelper", idInfoHelper2);
		
		String resKycToken = "responseJWTToken";
		Mockito.when(securityManager.signWithPayload(Mockito.anyString())).thenReturn(resKycToken); 
		Map<String, String> faceMap = prepareFaceData(idInfo);
		Mockito.when(idInfoHelper.getIdEntityInfoMap(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(faceMap);

		String response = kycServiceImpl2.buildKycExchangeResponse(dummySubject, idInfo, consentedAttributes, consentedLocales, idVid, kycExchangeRequestDTO);
		assertEquals(response, resKycToken);
	}

	@Test
	public void buildKycExchangeResponseAddressAttributesTest() throws IdAuthenticationBusinessException {
		
		String dummySubject = "dummyPSUToken";
		List<String> consentedAttributes = Arrays.asList("name", "gender", "dob", "address", "phone", "individual_id", "sub");
		List<String> consentedLocales = Arrays.asList("ara");		
		String idVid = "12232323121";
		KycExchangeRequestDTO kycExchangeRequestDTO = new KycExchangeRequestDTO();
		ReflectionTestUtils.setField(kycServiceImpl2, "consentedIndividualAttributeName", "individual_id");
		ReflectionTestUtils.setField(kycServiceImpl2, "consentedAddressAttributeName", "address");
		ReflectionTestUtils.setField(kycServiceImpl2, "addressSubsetAttributes", new String[] {"street_address","locality"});
		ReflectionTestUtils.setField(kycServiceImpl2, "idInfoHelper", idInfoHelper2);
		
		String resKycToken = "responseJWTToken";
		Mockito.when(securityManager.signWithPayload(Mockito.anyString())).thenReturn(resKycToken); 

		String response = kycServiceImpl2.buildKycExchangeResponse(dummySubject, idInfo, consentedAttributes, consentedLocales, idVid, kycExchangeRequestDTO);
		assertEquals(response, resKycToken);
	}

	@Test
	public void buildKycExchangeTwoNameAttributesTest() throws IdAuthenticationBusinessException {
		
		String dummySubject = "dummyPSUToken";
		List<String> consentedAttributes = Arrays.asList("name");
		List<String> consentedLocales = List.of("ara");		
		String idVid = "12232323121";
		KycExchangeRequestDTO kycExchangeRequestDTO = new KycExchangeRequestDTO();
		ReflectionTestUtils.setField(kycServiceImpl2, "consentedIndividualAttributeName", "individual_id");
		ReflectionTestUtils.setField(kycServiceImpl2, "consentedAddressAttributeName", "address");
		ReflectionTestUtils.setField(kycServiceImpl2, "consentedNameAttributeName", "name");
		ReflectionTestUtils.setField(kycServiceImpl2, "addressSubsetAttributes", new String[]{});
		ReflectionTestUtils.setField(kycServiceImpl2, "idInfoHelper", idInfoHelper);

		String resKycToken = "responseJWTToken";
		Mockito.when(securityManager.signWithPayload(Mockito.anyString())).thenReturn(resKycToken); 
		List<String> attributes = List.of("middleName", "lastName");
		Mockito.when(idInfoHelper.getIdentityAttributesForIdName(Mockito.anyString())).thenReturn(attributes);
		
		String response = kycServiceImpl2.buildKycExchangeResponse(dummySubject, idInfo, consentedAttributes, consentedLocales, idVid, kycExchangeRequestDTO);
		assertEquals(response, resKycToken);
	}

	@Test
	public void buildKycExchangeTwoNameAttributesMultiLangTest() throws IdAuthenticationBusinessException {
		
		String dummySubject = "dummyPSUToken";
		List<String> consentedAttributes = Arrays.asList("name");
		List<String> consentedLocales = List.of("ara", "fre");		
		String idVid = "12232323121";
		KycExchangeRequestDTO kycExchangeRequestDTO = new KycExchangeRequestDTO();
		ReflectionTestUtils.setField(kycServiceImpl2, "consentedIndividualAttributeName", "individual_id");
		ReflectionTestUtils.setField(kycServiceImpl2, "consentedAddressAttributeName", "address");
		ReflectionTestUtils.setField(kycServiceImpl2, "consentedNameAttributeName", "name");
		ReflectionTestUtils.setField(kycServiceImpl2, "addressSubsetAttributes", new String[]{});
		ReflectionTestUtils.setField(kycServiceImpl2, "idInfoHelper", idInfoHelper);

		String resKycToken = "responseJWTToken";
		Mockito.when(securityManager.signWithPayload(Mockito.anyString())).thenReturn(resKycToken); 
		List<String> attributes = List.of("middleName", "lastName");
		Mockito.when(idInfoHelper.getIdentityAttributesForIdName(Mockito.anyString())).thenReturn(attributes);
		
		String response = kycServiceImpl2.buildKycExchangeResponse(dummySubject, idInfo, consentedAttributes, consentedLocales, idVid, kycExchangeRequestDTO);
		assertEquals(response, resKycToken);
	}
}