package io.mosip.authentication.kyc.service.impl;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

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
import org.springframework.core.env.Environment;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.config.IDAMappingConfig;
import io.mosip.authentication.common.service.helper.IdInfoHelper;
import io.mosip.authentication.common.service.impl.match.BioMatchType;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.exception.IdAuthenticationDaoException;
import io.mosip.authentication.core.indauth.dto.IdentityInfoDTO;
import io.mosip.authentication.core.indauth.dto.KycResponseDTO;
import io.mosip.authentication.core.spi.indauth.match.MappingConfig;
import io.mosip.kernel.templatemanager.velocity.builder.TemplateManagerBuilderImpl;

/**
 * Test class for KycServiceImpl.
 *
 * @author Sanjay Murali
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class, TemplateManagerBuilderImpl.class })
@WebMvcTest
@Import(IDAMappingConfig.class)
@TestPropertySource("classpath:sample-data-test.properties")
public class KycServiceImplTest {

	@Autowired
	Environment env;

	@Autowired
	Environment environment;

	@Mock
	private IdInfoHelper idInfoHelper;

	@Autowired
	private IDAMappingConfig idMappingConfig;

	@Autowired
	private MappingConfig mappingConfig;

	@InjectMocks
	private KycServiceImpl kycServiceImpl;

	@Value("${sample.demo.entity}")
	String value;

	Map<String, List<IdentityInfoDTO>> idInfo;

	@Before
	public void before() throws IdAuthenticationDaoException {
		ReflectionTestUtils.setField(kycServiceImpl, "env", env);
		ReflectionTestUtils.setField(kycServiceImpl, "idInfoHelper", idInfoHelper);
		ReflectionTestUtils.setField(idInfoHelper, "environment", environment);
		ReflectionTestUtils.setField(idInfoHelper, "idMappingConfig", idMappingConfig);
		ReflectionTestUtils.setField(kycServiceImpl, "mappingConfig", mappingConfig);
		idInfo = getIdInfo("12232323121");

	}

	@Test
	public void validUIN() throws IOException {
		try {
			deleteBootStrapFile();
			prepareMap(idInfo);
			List<String> allowedKycList = limitedList();
			Mockito.when(idInfoHelper.getIdEntityInfoMap(BioMatchType.FACE, idInfo, null)).thenReturn(entityInfo());
			KycResponseDTO k = kycServiceImpl.retrieveKycInfo("12232323121", allowedKycList, "ara", idInfo);
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
			KycResponseDTO k = kycServiceImpl.retrieveKycInfo("12232323121", allowedKycList, "abc", idInfo1);
			assertNotNull(k);
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
			KycResponseDTO k = kycServiceImpl.retrieveKycInfo("12232323121", Collections.emptyList(), "ara", idInfo1);
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
			KycResponseDTO k = kycServiceImpl.retrieveKycInfo("12232323121", null, "ara", null);
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
			KycResponseDTO k = kycServiceImpl.retrieveKycInfo("12232323121", limitedList(), "ara", idInfo);
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
			KycResponseDTO k = kycServiceImpl.retrieveKycInfo("12232323121", limitedList(), "ara", idInfo);
			assertNotNull(k);
		} catch (IdAuthenticationBusinessException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void validUINWithoutFace2() {
		try {
			deleteBootStrapFile();
			Mockito.when(idInfoHelper.getIdEntityInfoMap(BioMatchType.FACE, idInfo, null)).thenReturn(null);
			KycResponseDTO k = kycServiceImpl.retrieveKycInfo("12232323121", limitedList(), "fra", idInfo);
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
			KycResponseDTO k = kycServiceImpl.retrieveKycInfo("12232323121", Collections.emptyList(), "ara", idInfo);
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
			KycResponseDTO k = kycServiceImpl.retrieveKycInfo("12232323121", null, null, idInfo);
			assertNotNull(k);
		} catch (IdAuthenticationBusinessException e) {
			e.printStackTrace();
		}
	}

	private void prepareMap(Map<String, List<IdentityInfoDTO>> idInfo) {
//		String value = "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";
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
			KycResponseDTO k = kycServiceImpl.retrieveKycInfo("12232323121", limitedList(), "ara", idInfo);
			assertNotNull(k);
		} catch (IdAuthenticationBusinessException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void validUIN3() {
		try {
			prepareMap(idInfo);
			KycResponseDTO k = kycServiceImpl.retrieveKycInfo("12232323121", fullKycList(), "ara", idInfo);
			assertNotNull(k);
		} catch (IdAuthenticationBusinessException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void validUIN4() {
		try {
			prepareMap(idInfo);
			KycResponseDTO k = kycServiceImpl.retrieveKycInfo("12232323121", fullKycList(), "ara", idInfo);
			assertNotNull(k);
		} catch (IdAuthenticationBusinessException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void validUIN5() throws IdAuthenticationDaoException {
		try {
			prepareMap(idInfo);
			KycResponseDTO k = kycServiceImpl.retrieveKycInfo("12232323121", fullKycList(), "ara", idInfo);
			assertNotNull(k);
		} catch (IdAuthenticationBusinessException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void validUIN6() throws IdAuthenticationDaoException, IOException, IdAuthenticationBusinessException {
		MockEnvironment environment = new MockEnvironment();
		environment.setProperty("uin.masking.required", "true");
		environment.setProperty("uin.masking.charcount", "2");
		ReflectionTestUtils.setField(kycServiceImpl, "env", environment);

		kycServiceImpl.retrieveKycInfo("12232323121", fullKycList(), "ara", idInfo);
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
		String s = "fullName,firstName,middleName,lastName,gender,addressLine1,addressLine2,addressLine3,city,province,region,postalCode,face,documents.individualBiometrics";
		List<String> allowedKycList = Arrays.asList(s.split(","));
		return allowedKycList;
	}

	private List<String> fullKycList() {
		String s = "fullName,firstName,middleName,lastName,dateOfBirth,gender,phone,email,addressLine1,addressLine2,addressLine3,city,province,region,postalCode,face,documents.individualBiometrics";
		return Arrays.asList(s.split(","));
	}
	
	private Map<String, String> entityInfo(){
		Map<String, String> map = new HashMap<>();
		map.put("FACE", "agsafkjsaufdhkjesadfjdsklkasnfdkjbsafdjbnadsfkjfds");
		return map;
	}
}