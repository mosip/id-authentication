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
		String faceData = "PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiIHN0YW5kYWxvbmU9InllcyI_Pgo8QklSIHhtbG5zPSJodHRwOi8vZG9jcy5vYXNpcy1vcGVuLm9yZy9iaWFzL25zL2JpYXNwYXRyb25mb3JtYXQtMS4wLyI-CiAgICA8VmVyc2lvbj4KICAgICAgICA8TWFqb3I-MTwvTWFqb3I-CiAgICAgICAgPE1pbm9yPjE8L01pbm9yPgogICAgPC9WZXJzaW9uPgogICAgPENCRUZGVmVyc2lvbj4KICAgICAgICA8TWFqb3I-MTwvTWFqb3I-CiAgICAgICAgPE1pbm9yPjE8L01pbm9yPgogICAgPC9DQkVGRlZlcnNpb24-CiAgICA8QklSSW5mbz4KICAgICAgICA8SW50ZWdyaXR5PmZhbHNlPC9JbnRlZ3JpdHk-CiAgICA8L0JJUkluZm8-Cgk8QklSPgogICAgICAgIDxCSVJJbmZvPgogICAgICAgICAgICA8SW50ZWdyaXR5PmZhbHNlPC9JbnRlZ3JpdHk-CiAgICAgICAgPC9CSVJJbmZvPgogICAgICAgIDxCREJJbmZvPgogICAgICAgICAgICA8Rm9ybWF0T3duZXI-MjU3PC9Gb3JtYXRPd25lcj4KICAgICAgICAgICAgPEZvcm1hdFR5cGU-ODwvRm9ybWF0VHlwZT4KICAgICAgICAgICAgPENyZWF0aW9uRGF0ZT4yMDE5LTAxLTI5VDE5OjExOjMzLjQzNCswNTozMDwvQ3JlYXRpb25EYXRlPgogICAgICAgICAgICA8VHlwZT5GYWNlPC9UeXBlPgogICAgICAgICAgICA8TGV2ZWw-UmF3PC9MZXZlbD4KICAgICAgICAgICAgPFB1cnBvc2U-RW5yb2xsPC9QdXJwb3NlPgogICAgICAgICAgICA8UXVhbGl0eT45NTwvUXVhbGl0eT4KICAgICAgICA8L0JEQkluZm8-CiAgICAgICAgPEJEQj4vOWovNEFBUVNrWkpSZ0FCQVFBQUFRQUJBQUQvMndDRUFBa0dCeE1URWhVUUVoSVZGUlVTRlJBU0VCVVFFaEFRRlJnV0ZSWVdGeGNWR0JVWUhTb2dHQm9sSFJVVklURWhKU2tyTGk0dUZ4OHpPRE10TnlndExpc0JDZ29LRGcwT0doQVFHU3NkSFIwckt5c3JNUzB0S3pjckxUY3ZMUzByTFMwdExTMHhLeTB0S3kwdEt5MHRLeTAxTFNzckxTMHRLeXNyTFNzdExTMHRMZi9BQUJFSUFPRUE0UU1CSWdBQ0VRRURFUUgveEFBY0FBRUFBUVVCQVFBQUFBQUFBQUFBQUFBQUJBTUZCZ2NJQWdIL3hBQkVFQUFCQXdJRUFnY0ZCUVVFQ3dBQUFBQUJBQUlEQkJFRkVpRXhCa0VIRTFGaGNZR1JJakp5b2JFVVFsSmkwUlVqTTdMQmM0S1Nrd2cwTlVORVZHT0RvdEx4LzhRQUdRRUJBQU1CQVFBQUFBQUFBQUFBQUFBQUFBSURCQUVGLzhRQUpoRUJBUUFDQVFRQ0FRUURBQUFBQUFBQUFBRUNFUU1TSVRGUkV6SkJJbEtSb1JSaGNmL2FBQXdEQVFBQ0VRTVJBRDhBM2lpSWdJaUlDSWlBaUlnSWlJQ0lpQWlJZ0lpSUNJaUFpSWdJaUlDSWlBaUlnSWlJQ0lpQWlJZ0lpSUNJaUFpSWdJaUlDSWlBaUlnSWlJQ0lpQWlJZ0lpSUNJaUFpSWdJaUlDSWlBaUlnSWlJQ0lpQWlJZ0lpSUNJaUFpeDNqTGpHbXc2TnI1eTRsNXRISEdBWHU3YlhJQUE3U3NSZzZhYVZ6d0RUVE5ZZDNreG0zZmxCMUNEYUNLTGhtSXhWRWJab1hoN0hDNExUZjhBK0ZTa0JFUkFSRVFFUkVCRVJBUkVRRVJFQkVSQVJFUUVSRUJFUkFSRkRwOFVoZkkrRmtyREpHYlNNRGhtYWQ5UWdoY1Y4U1EwRVBYelhJdUd0YTJ4YzRua0ZxTEcrbDZzbEpGTXhrRE9SSTYyVDFQc2owS2lkTHZFSDJxdE1ERGVLbXV3VzJNbjN6NWJlUldIeHhyaVVpNHpjVjRpL3dCNnVuOG5rZlJRdjJ0VkIzV2ZhcDh3MlBXdi9WZW1zQzh1alIzUzV1NDBxbnM2cXBFTlhHYmV6VnhaeVBoZTBnZzk2dE5mVnNrZURIVE1nRmlDMkYwam1uc05uazJQZ3Zoalh3dFEwdW5DM0ZNK0h5aVNKeE1aSTY2SW4yWGp3NU83MXVEQmVsckQ1eUd2YzZGeDB0SzNTL3hEUmFHZTFRS2lKSExIWVVFN1h0RDJPRG1uVUZwQkI4d3FpNVQ0VTQwck1QZGVHUzdQdnd5WGRHZkFmZFBlRjBqd2p4TkZYMHpLbVBUTmRybU9Jek5jTkMwcnFLOW9pSUNJaUFpSWdJaUlDSWlBaUlnSWlJQ0lpQWlJZ0xsamk2cWYrMGF0N1h1YTdyNVJtWTR0TmdjdTQ3aFpkRUhqWEQ4em1Hc2hEb3lXdkRuZ1dJMzFPaDhsejV4L0xUdXhHZDlJUVluT2FRVzZ0TGkxdWN0N3MxMHJzV2lNOCszY2xTMktERzVWMnlLS1NhMHJ5NHFpMlJmUzlCNkpYZ2xmQzlVM1BYUjljbzByVlZMbDRjZ2h5UnJ3eDdtKzY1dytGeEgwVXpMZFVwWTBjWGZBT09LNmpJTU5RNHRHOGNwTWtaOGp0NVdXOU9qdnBEaXhFR053RVZRMFhkSGU0Y1B4TUozQzVzeUtYaE5hNm1tanFZalo4TDJ2YnFSZTI0UGNSY2VhT2FkZ0lzYzRFNHFaaU5NS2hyY2pnU3lSbDcyY08vc1dScnJnaUlnSWlJQ0lpQWlJZ0lpSUNJaUF2RXpNelMzYTRJOVF2YUlPVHVMY0lrbzZ1V21sR29jWHNOckJ6SEVscngzSFVlUlZwRGxsUFN0Vk9reFNwemtuSTVzYkw4bXRBSUE3cmtueldOUVU5emEyOXJlZXk0a05lcWdldGk0WHduVGRVMWtrWWM2M3RPMUJ1ZXdyRk1jNFNxSUhFc1laSXJuSzVtcmdPeHczVmM1Y2JkTEx4NVNiV2xyMTdEMThndytkM3V3eW53amVmNktmVDhQMVR6WVU4bjk1aGI2M1V1cUk2cUNYTHlBU2JBRW5zQUpQb3M5d1hnRFo5UzcvQUxiRDlYZm9zdW84SmhpSDd1TnJlOERYMVZXWFBqUEhkWmp3NVh6MmFjR0ZWQjJwNXY4QUpsL1JWNHNBcW5Hd3A1QjhUQ3dmK1MzSVdxazVxaC9rWDBuOEU5dFNWZkQxUkNNNzJlenpMU0hXOGJLRStNRUxidFN3RUVIWTNCV3NNV28rcG1kSHl2ZHZnZGxaeDhuVjVWNThmVDRXRjBXVTkzTmZaQU9SVXFvYW9MNGxjcWIwNkFJUUtTWjRQdnpiZkMwQmJUWEtQQnRQVlBxNG9LU2Q4TWtyclptdWNHZ0FYTG5BYml3NXJxcW1ZNE1hSHV6T0RXaHpyV3VRTlRia3VvcWlJaUFpSWdJaUlDSWlBaUlnSWlJQ2o0Z3g1amVJM1pYNVRrUDV1Vis1U0VRY25jU1ZFMGxYTTZxRnA4MXBSbHk2dDBHbmdBcFBEZEhubmpiK1lFK1dxem5wNHJZMnp4d0NHUHJIeHRrZkwvdkxCemcwZUdqdm1zVzZQbTNxV2VEejhsRE82bFdZZDdHMEtiRDlGUFpRS1RUalJTbWhlYmE5Q1JFWlJCVkhVZ3NwUVM2anRMU0NhTlUvczZ1RGlxVGwzYm1sdWtwMUhsaFYxY0ZIbEM3SzVZc2RRRmcvR3NQdVA3eTAvVlo5WEJZZHhSRm1pZCtXenZSWDhWN3hUeVRzd0dyRjFGazAyVXVaUm5OVzFqYkU2Q01LTXRZNnBQdTB6Q1BGOGdzUGxmNUxmNjU5NkVNV01OZWFjbjJLbGpoYi9xTUYyL0lPWFFTNmlJaUlDSWlBaUlnSWlJQ0lpQWlJZ0lpSU5BOVAwTnNRaGsvSFROYVA3a2toUDg0Vm82T3Y5WWI4TC82TE5mOEFTQnd0NzIwOVMxamkyTHJHU09BMGFINWRUNXRDMVBoRk5QSVQxSmNDTEQyU1FkVGEyaWhuTnlwNFhWZER3eUFEVWdlSkFYc1Y4VzNXcy94dFdyS1hnQ3BlMEdvckMzOG9MNUNQVWdMeFAwZFJqYXROL3dBMFg2T1dMNDhQM2YwMmZKbiszKzIzbzVXdUYya0VkeEJYMjYwaExnMWZSWGxwNWk5ckJtSmpjZGh2ZGpsTHdqaXZHSjJPZERhUnJOSE82dG5qdnpLWGcvTXMwVG4vQUJaVzR5VmFxL0g2YUxTU2VOcDdDNFg5RnFXanhYRks5em9Xek9BYi9FMmpEZVd0aGRYU2w0QmdiclVUdmU3bUk3TkhxYmtydnd6SDdYK0Q1Ymw5Wi9MTW44YzBIL01OOUhmb3FFbkd0Q2YrSWI2Ty9SV2lIaG5EUm9ZaWU5MGovd0NoQ2tIZ3ZEM2oyWWlQaGxsLzlrMXgvd0N6ZkpmU25WOFdVWjJuYjgxYUtuR0taNGNPdVpZZ2c2OXFpOFNjQnNqWTZTbmU0NVJjc2ZZNkRzS3NtQzhOTW5pNjE3bkRNWEJvYmxHeHRyY2RxdHh4dzF1VlZsbG52VmkxeXVGeUFRYkVqUXFOS05DcTJPNEU2bmVBRG1EaGRwdFk2Y2lxQjI4bHBuZWRtZXBmRFZjNktvZ2xiNzBjMExoYnVlTGp6Rng1cnJoY3Q4SjhOU3ZraW1rR1dKc2tiblh2Y3RhNEVnRHlLNlp3ekVZNTI1NHpjQTJOeFlnOWlUS1hzWEd6dWxvaUxxSWlJZ0lpSUNJaUFpSWdJaUlDSWlERytrZW42ekRLdGczTUx5M3hHb1dvT0JxUVJWRXJPd01MYjY2T3M3K3EzdmpGTjFrRXNWcjU0NUcyOFdsYXJkVHRFdE5PMEFHV2theVMxeDdjRHNwTmo0alh1VmZMUDAxWnhYOVVYNTBCZjFoSnMySmhlUU5DNDJKQXZ5R2kxQlB4VS9PME5mbWNTNXptNWZaQUdvRitZc3QxVXJYT0Z3UjdUY3JnUmNFZDZ4dHZSdlRCK2NBa1h2bExpQnZ0NExMam5oSnI4dE9lR2R1NTRYQ3FvbVJ3eDFJSjZ0OFFrZTE1dllGbVlpL2dvdlI3Umh0SXh6UllTbDh0clcwY1RZZWlvOGZWRDNOaW9RNGRaVk9iR0dzRmd5SUVaamJ3RzZ5Nm1wMnhzYXhvczJOb2Ezd0FzdWN1VTEyL0tYSEx2djhBaGhXRXdNcDY2cXA3V00rV29qMTBJKzhCNEUzODFjSW9SSk95SjJnSjE1WHR5VVhqMmxjMFJZaEVDWktWMTNnZmVpT2poNUs0MGNUS3FObFJFN1J3RG1rYmc5bmltL0dWTmVjWTFwMGdZZzZPcm1qYkdUYXpZZzMyV3MyM0hNYnJPdWpSanBxRnhtR3JKWE5pZnp5NVdtMSt3RWtLOFYzRGNVNUQ1d0pIQnVXNWEwRzNlUUxsVnZzakkyQmpQWmFObWdrTjlObFA1c2QrRmZ3NWUxdXhsdVdFdVA0SG41Rllidzh5MUxGM2g3djhUM0grcXZYSFdLNUlPcUdyNXYzY2JlZXVoS2lRVS9WeHNqL0ExcmZRS09QMS93Q3BaZlpaT0tZYnNhNzhKdDZyR3NDcE90cVk0K1JlTCtBMVAwV1c4U0Q5d2U0dFZyNEpwUGFkTjJYYTArTzZ2eHkxaFZPVTNsR3c3dEZvb3gzZHdXWDhCd0ZzVWhQM3BQb0FGaCtFTUFCY2R5dGg4TlE1YWR2NXJ2MTd5cStDZnFUNXIrbGRFUkZyWlJFUkFSRVFFUkVCRVJBUkVRRVJFQmFrNDF3cW9vcHpVeHRNMUxJOHU2dHRzOFQ1UGZ5Z2ZkSkFQaVZ0dFduaW1ITlRQL0xaM29WSEw2MUxIN1JxK2s2UUlHaXpvcHdlenFYRlN6eDdJOFdwcUdva2NkQVhzTWJSNGs4bGNhV2JSWEtsa1dDM0gwM3laZTFrNFl3S2ZySFYxYVE2b2tHVmpSN3NUUHdqc1dVUEZtcU5QV2h1cDVLbk5palNORkMyNVhhVWtrMDliNkhVRzRJS3hBOFBWZEhJNStIeXRNVHlYT2dsOTBFL2g3RmtqYW02cTljdXkyT1dTc1pmanVLalEwRWJ1OXM0QSthaXpZaGlzbWdwNG9yODNQejI5RmxqbnFQSzVTbVU5Ukc0MzNXSjBIRGpteWZhYW1RelRjcis2M3dDbVZLdWRRNVd1YzZxVzdmS09wUEN5NHhHSDVZanFIYmhUNkdGc1FFYlcyRnJEczcvQURVU0QycG5PNU4wSGlyeE5UV0RiYWsySG1WSytrWjdYZkJvSFRQYkN6bjd4L0MzbVZzMkdNTmFHalpvQUhnRmJPSGNHYlRSZ0RWN3JHUjNhZXp3Q3V5MDhlSFRHZmt6NnFJaUt4V0lpSUNJaUFpSWdJaUlDSWlBaUlnS25QRUhOY3c3T0JCOHdxaUlOVnVhWTVIUnUzWTR0UGtkQ3A3YWdOYm1KMENtOGQwV1dSdFFObit3L3dDSWJIMCtpd25HTVRzM0lENHJCeVlheTAzWVo3eDJsVjNFamRzcFBmZFJtWTJ6bXgzazdUNks0d2NQUExXbGo0aGNBa3Vibk92bXZVdkRkU0Jkc3NYK1VQMVVlM3RMdXR6OGZjUGRqMDh5dmNQRXJocStQVHV2ZFYyWVZWODUyandqQStwWHY5bVNFMmtrYTV2TUZqUWZVSnFIZGNLV3ViSU16VDRqbWtyMWl4cU9vbWMxdXdOdkVLNk54VmpodlpOTzdWNTNxejRqVUJqU2VmSlZLM0UyamJWWTdWVHVrZGJ2VTVGZVZaNTBXNFFKSG1kNHVHZTBMN1pqdDZiclllSllGQk9RNlJudE5jSEJ6U1dPdU8wamZ6VnE2T3FMcTZRRzFzNUo4aG9Qb1ZsQzE0VFdMSm5lNGlJcG9pSWlBaUlnSWlJQ0lpQWlJZ0lpSUNJckp4SnhYU1VMTTlUTTFwKzZ3ZTFJN3Vhd2FsQmUxNGZLMGJ1QThTQXVlK01PbU9wcUNXVWdOUEZ0bU5uU3U3eWRtK0F1dGQxdUt6eW04czhyeitlUjUrVjBIV1BFRWxQTkErSjg4YmN3T1VsN2RIY2p2MnJSRDVDWEZwc1NDUVNEY0czTUhtRnJldzdBc2g0ZnhPMW8zSDRTZm9xdVhIYzJzNDh0WFRkR0ZOTG9tRUg3bytTaTQ1V3pSa05ZOGo1cURnR0w1WXcyK29WTEVhN09TZnFzVjh0dSt5ZGhPSXlQZGtrTjdqUTk2dXoyV1dJUXk1U0R6R3U2dkxzV0dYVThrTnJEamY4QUZkNnEzUGtOdExLUlhUWm5FcU0xbHoyM1U1RmRxanFWVnVJMk9sZHN3WDhUeUN1ZFBoVHJYZG9Pem1yZHhuSGFrZUJzTXZwZFR4MXZTR1c5YlpSdzcwMFJzYTJLZW1MV3RBYUhRdURyQWFhdE8vcXRwNERqOVBXUmlXbmtEd2R4czRkem1uVUZjYzNWeHdYSEo2V1FTd1NPWTRkaDM3aU9ZV3hsZGtJdFc5SDNTM0hWT1pUVlRlcm1kWnJYaXdqZTdrRCtFbjBXMGtCRVJBUkVRRVJFQkVSQVJFUUVSVXF1b2JHeDBqalpyR3VjNG5zQXVVR0VkSy9ISXcrRHFvaURVemdpSWI1RzdHUjNoZlFjeXVjS3FvZks0eVN2Yzk3dDNQSmNUNXFkeE5qVDYycWxxbmtuTzQ1QWZ1c0I5bG83TlBxcmNnb1BDcDNVaVFLT1JxZ0JTS09FdmV4amZlYzVyVytKS1J3QzJxei9BS08rRTNHUnRVOXBEV2c5V0hia243M2dvNTVUR2JxV0dOeXVsM3FNQ2V3QXhFblFabTg3MjNDaXVNcmZlWTRkdnNuOUZua3RMWVhYcW5QSllPcHU2V0Foeno3c2JqMit5N2IraWt4WVRVeWJSdUEvTllENXJZRFY5WE9wM29ZZFM4Sk8za2VCM04xUHFycEJockkvZGFQSGMrcXZFaFVPUk9xMDZaRUdvYXNZNHJoelUwby9LVDZMSzVtTFczR3ZFZHk2bWg1WGJLLzZ0YitxdDQ1YmV5dmtzazdzREM5Z0txMk5lbk5XMWlVUVNDQ0NRUVFRUm9RUnNWMEIwUWRKSDJvQ2hxM0FUdEZvWGsyNjBEbDhZK2EwQ1FsUE81ajJ5TUphNWhEbWthRUViRkIycWl3Zm9yNDFHSVUrV1FqN1JEWVNnYVpoeWVCM3JPRUJFUkFSRVFFUkVCRjhjNEFFblFEVWtyUlBHUFNGVXpUdkZOTytLQnB5czZ1elM2MzNpN2ZYc3VnM3NTdFQ5T1BGeldVd29ZSldsODdyVDVIQWxzVGRTRGJiTWJEd3V0YTFXT1ZVZ0lmVXpPQjNEcHBDUFM2eGl1WWM1SjU3SUtiVjlLOEFyMGcrT0Nva2FoVnlGVEJBYzBuWU9hVDRBNm9OcDhEY0dNTFdUVHR6T2Rad2E3Wm9PMm5NclprZE9HaXdHeXRXQ1Rpd3RzNE5JOENGZkF2TjVNcmxlNzBlUEdZenNwR0c2aFRRV0t1clF2c3NJSVZlMW1scGFWOUpYMmRtVXFpWHFUajVJVkdlVlVrZW8wajEySTFqUEhXUC9ab3NqRCs5bEJEZnl0NXVXcGdPWlYzNHN4TDdSVlNTWDlrSHEyZkMzVDVtNVZwdXQvSGgwNHNQSm4xWlBTOGxmQzVWNHFVa1puZXkzdlZpdEdEQzQyQzlkVTBkNVZSN2g3ck51WlZXR0JCSzRYeCtXZ3FtVk1keGxQdHQ1UFlkMmxkVzREaThkVkJIVXhHN1pHZ2p1UE1IdkM1Uyt6QTZFTExlQitNcHNORG1NQWtpY1E0eHZKRmp6TFR5dWc2UVJRY0R4RVZGUEZVQnVVVE1iSUFkeG1GN0tjZ0lpSUNJaUNoWC93QUtUNEgvQU1wWExnL2hOOGtSQlFLZ1lseTgwUkJibDZDSWcrdVVlYlpmVVFyZmZDZjhLSCt5aS9sQ3k5cStJdk16OHZTdzhLclZVQ0lxMWkzWWtyWTVFVTRoVkY2aDFudVArQi8wS0lwUkd0RWZxVjhLSXZTZWM5UTdqeENtNDE3b1JFRUtrVnhnUkVFcHErdjJSRUhTL0EzK3o2VCt3aS9sQ3ZpSWdJaUlDSWlELzltUlhhbzZsVmJpTmpwWGJNRi9FOGdyblQ0VTYxM2FEczVxM2NaeDJwSGdiREw2WFU4ZGIwaGx2VzJVY085TkViR3RpbnBpMXJRR2gwTGc2d0dtclR2NnJhZUE0L1Qxa1lscDVBOEhjYk9IYzVwMUJYSE4xY2NGeHllbGtFc0VqbU9IWWQrNGptRnNaWFpDTFZ2UjkwdHgxVG1VMVUzcTVuV2ExNHNJM3U1QS9oSjlGdEpBUkVRRVJFQkVSQVJFUUVSRUJFVktycUd4c2RJNDJheHJuT0o3QUxsQmhIU3Z4eU1QZzZxSWcxTTRJaUcrUnV4a2Q0WDBITXJuQ3FxSHl1TWtyM1BlN2R6eVhFK2FuY1RZMCt0cXBhcDVKenVPUUg3ckFmWmFPelQ2cTNJS0R3cWQxSWtDamthb0FVaWpoTDNzWTMzbk9hMXZpU2tjQXRxcy93Q2p2aE54a2JWUGFRMW9QVmgyNUorOTRLT2VVeG02bGhqY3JwZDZqQW5zQU1SSjBHWnZPOXR3b3JqSzMzbU9IYjdKL1JaNUxTMkYxNnB6eVdEcWJ1bGdJYzgrN0c0OXZzdTIvb3BNV0UxTW0wYmdQeldBK2EyQTFmVnpxZDZHSFV2Q1R0NUhnZHpkVDZxNlFZYXlQM1dqeDNQcXJ4SVZEa1RxdE9tUkJxR3JHT0s0YzFOS1B5aytpeXVaaTF0eHJ4SGN1cG9lVjJ5dityVy9xcmVPVzNzcjVMSk83QXd2WUNxdGpYcHpWdFlsRUVnZ2drRUVFRWFFRWJGZEFkRUhTUjlxQW9hdHdFND08L0JEQj4KICAgIDwvQklSPgo8L0JJUj4K";
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