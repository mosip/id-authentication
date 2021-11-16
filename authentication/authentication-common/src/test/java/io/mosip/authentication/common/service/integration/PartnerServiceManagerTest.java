package io.mosip.authentication.common.service.integration;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.authentication.common.service.entity.ApiKeyData;
import io.mosip.authentication.common.service.entity.MispLicenseData;
import io.mosip.authentication.common.service.entity.PartnerData;
import io.mosip.authentication.common.service.entity.PartnerMapping;
import io.mosip.authentication.common.service.entity.PolicyData;
import io.mosip.authentication.common.service.repository.ApiKeyDataRepository;
import io.mosip.authentication.common.service.repository.MispLicenseDataRepository;
import io.mosip.authentication.common.service.repository.PartnerDataRepository;
import io.mosip.authentication.common.service.repository.PartnerMappingRepository;
import io.mosip.authentication.common.service.repository.PolicyDataRepository;
import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.partner.dto.PartnerPolicyResponseDTO;
import io.mosip.authentication.core.partner.dto.PolicyDTO;
import io.mosip.kernel.core.websub.model.Event;
import io.mosip.kernel.core.websub.model.EventModel;
import io.mosip.kernel.core.websub.model.Type;

/**
 * Test class for PartnerServiceManager
 * 
 * @author Nagarjuna
 *
 */
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class PartnerServiceManagerTest {

	@Autowired
	ConfigurableEnvironment env;

	@Mock
	private PartnerMappingRepository partnerMappingRepo;

	@Mock
	private PartnerDataRepository partnerDataRepo;

	@Mock
	private PolicyDataRepository policyDataRepo;

	@Mock
	private ApiKeyDataRepository apiKeyRepo;

	@Mock
	private MispLicenseDataRepository mispLicDataRepo;

	@Autowired
	private ObjectMapper mapper;

	@Mock
	private IdAuthSecurityManager securityManager;

	@InjectMocks
	private PartnerServiceManager partnerServiceManager;

	HashMap<String, Object> data = null;
	HashMap<String, Object> partnerData  = null;
	HashMap<String, Object> policyData = null;
	HashMap<String, Object> apiKeyData = null;
	EventModel eventModel = null;
	Event event = null;
	Type type = null;
	
	
	@Before
	public void before() {
		ReflectionTestUtils.setField(partnerServiceManager, "mapper", mapper);
		data = new HashMap<String, Object>();
		partnerData = new HashMap<String, Object>();
		policyData = new HashMap<String, Object>();
		apiKeyData = new HashMap<String, Object>();
		
		eventModel = new EventModel();
		event = new Event();
		Type type = new Type();
		type.setNamespace("io.mosip.pmp.partner.manager.service.impl.PartnerManagementServiceImpl");
		type.setName("PartnerManagementServiceImpl");
		
	}

	@Test
	public void validateAndGetPolicyTest() throws IdAuthenticationBusinessException, Exception {
		String partnerMappingObj = "{\"partnerId\":\"1635497344579\",\"policyId\":\"21\",\"apiKeyId\":\"130956\",\"partnerData\":{\"partnerId\":\"1635497344579\",\"partnerName\":\"1635497344579\",\"certificateData\":\"data\",\"partnerStatus\":\"ACTIVE\"},\"policyData\":{\"policyId\":\"644269\",\"policyName\":\"policy 1635497343191\",\"policyDescription\":\"Auth Policy\",\"policyStatus\":\"ACTIVE\",\"policyCommenceOn\":\"2021-10-10T06:12:52.994Z\",\"policyExpiresOn\":\"2021-12-11T06:12:52.994Z\",\"policy\":{\"authPolicies\":[{\"authType\":\"otp\",\"authSubType\":\"\",\"mandatory\":true},{\"authType\":\"demo\",\"authSubType\":\"\",\"mandatory\":false},{\"authType\":\"bio\",\"authSubType\":\"FINGER\",\"mandatory\":true},{\"authType\":\"bio\",\"authSubType\":\"IRIS\",\"mandatory\":false},{\"authType\":\"bio\",\"authSubType\":\"FACE\",\"mandatory\":false},{\"authType\":\"kyc\",\"authSubType\":\"\",\"mandatory\":false}],\"allowedKycAttributes\":[{\"attributeName\":\"fullName\",\"required\":true},{\"attributeName\":\"dateOfBirth\",\"required\":true},{\"attributeName\":\"gender\",\"required\":true},{\"attributeName\":\"phone\",\"required\":true},{\"attributeName\":\"email\",\"required\":true},{\"attributeName\":\"addressLine1\",\"required\":true},{\"attributeName\":\"addressLine2\",\"required\":true},{\"attributeName\":\"addressLine3\",\"required\":true},{\"attributeName\":\"location1\",\"required\":true},{\"attributeName\":\"location2\",\"required\":true},{\"attributeName\":\"location3\",\"required\":true},{\"attributeName\":\"postalCode\",\"required\":false},{\"attributeName\":\"photo\",\"required\":true}]}},\"apiKeyData\":{\"apiKeyId\":\"591856\",\"apiKeyStatus\":\"ACTIVE\",\"apiKeyCommenceOn\":\"2021-10-10T06:12:52.994Z\",\"apiKeyExpiresOn\":\"2021-12-11T06:12:52.994Z\"}}";
		String mispLicenseDataObj = "{\"mispId\":\"1635497344579\",\"licenseKey\":\"rtaCBxYlsrWeuYpLgfUFT5ic6LetsOZzbvxBEe8yR5FCKkEsvQ\",\"mispCommenceOn\":\"2021-10-10T06:12:52.994Z\",\"mispExpiresOn\":\"2021-12-11T06:12:52.994Z\",\"mispStatus\":\"ACTIVE\"}";

		PartnerMapping partnerMapping = mapper.readValue(partnerMappingObj, PartnerMapping.class);
		Optional<PartnerMapping> partnerMappingDataOptional = Optional.of(partnerMapping);
		Mockito.<Optional<PartnerMapping>>when(partnerMappingRepo.findByPartnerIdAndApiKeyId("1635497344579", "591856"))
				.thenReturn(partnerMappingDataOptional);
		MispLicenseData mispLicenseData = mapper.readValue(mispLicenseDataObj, MispLicenseData.class);
		Optional<MispLicenseData> mispLicOptional = Optional.of(mispLicenseData);
		Mockito.when(mispLicDataRepo.findByLicenseKey("rtaCBxYlsrWeuYpLgfUFT5ic6LetsOZzbvxBEe8yR5FCKkEsvQ")).thenReturn(mispLicOptional);
		PartnerPolicyResponseDTO partnerPolicyResponseDTO = new PartnerPolicyResponseDTO();
		PartnerData partnerData = partnerMapping.getPartnerData();
		PolicyData policyData = partnerMapping.getPolicyData();
		ApiKeyData apiKeyData = partnerMapping.getApiKeyData();
		partnerPolicyResponseDTO.setPolicyId(policyData.getPolicyId());
		partnerPolicyResponseDTO.setPolicyName(policyData.getPolicyName());
		partnerPolicyResponseDTO.setPolicy(mapper.convertValue(policyData.getPolicy(), PolicyDTO.class));
		partnerPolicyResponseDTO.setPolicyDescription(policyData.getPolicyDescription());
		partnerPolicyResponseDTO.setPolicyStatus(policyData.getPolicyStatus().contentEquals("ACTIVE"));
		partnerPolicyResponseDTO.setPartnerId(partnerData.getPartnerId());
		partnerPolicyResponseDTO.setPartnerName(partnerData.getPartnerName());
		partnerPolicyResponseDTO.setPolicyExpiresOn(policyData.getPolicyExpiresOn());
		partnerPolicyResponseDTO.setApiKeyExpiresOn(apiKeyData.getApiKeyExpiresOn());
		partnerPolicyResponseDTO.setMispExpiresOn(mispLicenseData.getMispExpiresOn());
		partnerPolicyResponseDTO.setCertificateData(partnerData.getCertificateData());
		assertEquals(partnerPolicyResponseDTO, partnerServiceManager.validateAndGetPolicy("1635497344579", "591856","rtaCBxYlsrWeuYpLgfUFT5ic6LetsOZzbvxBEe8yR5FCKkEsvQ", true));
	}

	// io.mosip.authentication.core.exception.IdAuthenticationBusinessException:IDA-MPA-012 --> Partner is deactivated
	@Test(expected = IdAuthenticationBusinessException.class)
	public void validateAndGetPolicyException1Test() throws IdAuthenticationBusinessException, Exception {
		String partnerMappingObj = "{\"partnerId\":\"1635497344579\",\"policyId\":\"21\",\"apiKeyId\":\"130956\",\"partnerData\":{\"partnerId\":\"1635497344579\",\"partnerName\":\"1635497344579\",\"isDeleted\":\"true\",\"partnerStatus\":\"ACTIVE\"},\"policyData\":{\"policyId\":\"644269\",\"policyName\":\"policy 1635497343191\",\"policyDescription\":\"Auth Policy\",\"policyStatus\":\"INACTIVE\",\"isDeleted\":true,\"policyCommenceOn\":\"2021-10-10T06:12:52.994Z\",\"policyExpiresOn\":\"2021-12-11T06:12:52.994Z\",\"policy\":{\"authPolicies\":[{\"authType\":\"otp\",\"authSubType\":\"\",\"mandatory\":true},{\"authType\":\"demo\",\"authSubType\":\"\",\"mandatory\":false},{\"authType\":\"bio\",\"authSubType\":\"FINGER\",\"mandatory\":true},{\"authType\":\"bio\",\"authSubType\":\"IRIS\",\"mandatory\":false},{\"authType\":\"bio\",\"authSubType\":\"FACE\",\"mandatory\":false},{\"authType\":\"kyc\",\"authSubType\":\"\",\"mandatory\":false}],\"allowedKycAttributes\":[{\"attributeName\":\"fullName\",\"required\":true},{\"attributeName\":\"dateOfBirth\",\"required\":true},{\"attributeName\":\"gender\",\"required\":true},{\"attributeName\":\"phone\",\"required\":true},{\"attributeName\":\"email\",\"required\":true},{\"attributeName\":\"addressLine1\",\"required\":true},{\"attributeName\":\"addressLine2\",\"required\":true},{\"attributeName\":\"addressLine3\",\"required\":true},{\"attributeName\":\"location1\",\"required\":true},{\"attributeName\":\"location2\",\"required\":true},{\"attributeName\":\"location3\",\"required\":true},{\"attributeName\":\"postalCode\",\"required\":false},{\"attributeName\":\"photo\",\"required\":true}]}},\"apiKeyData\":{\"apiKeyId\":\"591856\",\"apiKeyStatus\":\"INACTIVE\",\"isDeleted\":true,\"apiKeyCommenceOn\":\"2021-11-10T06:12:52.994Z\",\"apiKeyExpiresOn\":\"2021-12-11T06:12:52.994Z\"}}";
		String mispLicenseDataObj = "{\"mispId\":\"1635497344579\",\"licenseKey\":\"rtaCBxYlsrWeuYpLgfUFT5ic6LetsOZzbvxBEe8yR5FCKkEsvQ\",\"mispCommenceOn\":\"2021-10-10T06:12:52.994Z\",\"mispExpiresOn\":\"2021-12-11T06:12:52.994Z\",\"mispStatus\":\"ACTIVE\"}";
		PartnerMapping partnerMapping = mapper.readValue(partnerMappingObj, PartnerMapping.class);
		Optional<PartnerMapping> partnerMappingDataOptional = Optional.of(partnerMapping);
		Mockito.<Optional<PartnerMapping>>when(partnerMappingRepo.findByPartnerIdAndApiKeyId("1635497344579", "591856"))
				.thenReturn(partnerMappingDataOptional);
		MispLicenseData mispLicenseData = mapper.readValue(mispLicenseDataObj, MispLicenseData.class);
		Optional<MispLicenseData> mispLicOptional = Optional.of(mispLicenseData);
		Mockito.when(mispLicDataRepo.findByLicenseKey("rtaCBxYlsrWeuYpLgfUFT5ic6LetsOZzbvxBEe8yR5FCKkEsvQ"))
				.thenReturn(mispLicOptional);
		partnerServiceManager.validateAndGetPolicy("1635497344579", "591856",
				"rtaCBxYlsrWeuYpLgfUFT5ic6LetsOZzbvxBEe8yR5FCKkEsvQ", false);
	}

	// IdAuthenticationBusinessException: IDA-MPA-019 --> Partner policy is not active
	@Test(expected = IdAuthenticationBusinessException.class)
	public void validateAndGetPolicyException2Test() throws IdAuthenticationBusinessException, Exception {
		String partnerMappingObj = "{\"partnerId\":\"1635497344579\",\"policyId\":\"21\",\"apiKeyId\":\"130956\",\"partnerData\":{\"partnerId\":\"1635497344579\",\"partnerName\":\"1635497344579\",\"partnerStatus\":\"ACTIVE\",\"isDeleted\":true},\"policyData\":{\"policyId\":\"644269\",\"policyName\":\"policy 1635497343191\",\"policyDescription\":\"Auth Policy\",\"policyStatus\":\"INACTIVE\",\"isDeleted\":true,\"policyCommenceOn\":\"2021-11-10T06:12:52.994Z\",\"policyExpiresOn\":\"2021-12-11T06:12:52.994Z\",\"policy\":{\"authPolicies\":[{\"authType\":\"otp\",\"authSubType\":\"\",\"mandatory\":true},{\"authType\":\"demo\",\"authSubType\":\"\",\"mandatory\":false},{\"authType\":\"bio\",\"authSubType\":\"FINGER\",\"mandatory\":true},{\"authType\":\"bio\",\"authSubType\":\"IRIS\",\"mandatory\":false},{\"authType\":\"bio\",\"authSubType\":\"FACE\",\"mandatory\":false},{\"authType\":\"kyc\",\"authSubType\":\"\",\"mandatory\":false}],\"allowedKycAttributes\":[{\"attributeName\":\"fullName\",\"required\":true},{\"attributeName\":\"dateOfBirth\",\"required\":true},{\"attributeName\":\"gender\",\"required\":true},{\"attributeName\":\"phone\",\"required\":true},{\"attributeName\":\"email\",\"required\":true},{\"attributeName\":\"addressLine1\",\"required\":true},{\"attributeName\":\"addressLine2\",\"required\":true},{\"attributeName\":\"addressLine3\",\"required\":true},{\"attributeName\":\"location1\",\"required\":true},{\"attributeName\":\"location2\",\"required\":true},{\"attributeName\":\"location3\",\"required\":true},{\"attributeName\":\"postalCode\",\"required\":false},{\"attributeName\":\"photo\",\"required\":true}]}},\"apiKeyData\":{\"apiKeyId\":\"591856\",\"apiKeyStatus\":\"ACTIVE\",\"isDeleted\":false,\"apiKeyCommenceOn\":\"2021-11-10T06:12:52.994Z\",\"apiKeyExpiresOn\":\"2021-12-11T06:12:52.994Z\"}}";
		String mispLicenseDataObj = "{\"mispId\":\"1635497344579\",\"licenseKey\":\"rtaCBxYlsrWeuYpLgfUFT5ic6LetsOZzbvxBEe8yR5FCKkEsvQ\",\"mispCommenceOn\":\"2021-10-10T06:12:52.994Z\",\"mispExpiresOn\":\"2021-12-11T06:12:52.994Z\",\"mispStatus\":\"ACTIVE\"}";
		PartnerMapping partnerMapping = mapper.readValue(partnerMappingObj, PartnerMapping.class);
		Optional<PartnerMapping> partnerMappingDataOptional = Optional.of(partnerMapping);
		Mockito.<Optional<PartnerMapping>>when(partnerMappingRepo.findByPartnerIdAndApiKeyId("1635497344579", "591856"))
				.thenReturn(partnerMappingDataOptional);
		MispLicenseData mispLicenseData = mapper.readValue(mispLicenseDataObj, MispLicenseData.class);
		Optional<MispLicenseData> mispLicOptional = Optional.of(mispLicenseData);
		Mockito.when(mispLicDataRepo.findByLicenseKey("rtaCBxYlsrWeuYpLgfUFT5ic6LetsOZzbvxBEe8yR5FCKkEsvQ")).thenReturn(mispLicOptional);
		partnerServiceManager.validateAndGetPolicy("1635497344579", "591856","rtaCBxYlsrWeuYpLgfUFT5ic6LetsOZzbvxBEe8yR5FCKkEsvQ", false);
	}

	// IdAuthenticationBusinessException: IDA-MPA-019 --> Partner policy is not active
	@Test(expected = IdAuthenticationBusinessException.class)
	public void validateAndGetPolicyException3Test() throws IdAuthenticationBusinessException, Exception {
		String partnerMappingObj = "{\"partnerId\":\"1635497344579\",\"policyId\":\"21\",\"apiKeyId\":\"130956\",\"partnerData\":{\"partnerId\":\"1635497344579\",\"partnerName\":\"1635497344579\",\"partnerStatus\":\"ACTIVE\",\"isDeleted\":true},\"policyData\":{\"policyId\":\"644269\",\"policyName\":\"policy 1635497343191\",\"policyDescription\":\"Auth Policy\",\"policyStatus\":\"ACTIVE\",\"isDeleted\":true,\"policyCommenceOn\":\"2021-11-10T06:12:52.994Z\",\"policyExpiresOn\":\"2021-12-11T06:12:52.994Z\",\"policy\":{\"authPolicies\":[{\"authType\":\"otp\",\"authSubType\":\"\",\"mandatory\":true},{\"authType\":\"demo\",\"authSubType\":\"\",\"mandatory\":false},{\"authType\":\"bio\",\"authSubType\":\"FINGER\",\"mandatory\":true},{\"authType\":\"bio\",\"authSubType\":\"IRIS\",\"mandatory\":false},{\"authType\":\"bio\",\"authSubType\":\"FACE\",\"mandatory\":false},{\"authType\":\"kyc\",\"authSubType\":\"\",\"mandatory\":false}],\"allowedKycAttributes\":[{\"attributeName\":\"fullName\",\"required\":true},{\"attributeName\":\"dateOfBirth\",\"required\":true},{\"attributeName\":\"gender\",\"required\":true},{\"attributeName\":\"phone\",\"required\":true},{\"attributeName\":\"email\",\"required\":true},{\"attributeName\":\"addressLine1\",\"required\":true},{\"attributeName\":\"addressLine2\",\"required\":true},{\"attributeName\":\"addressLine3\",\"required\":true},{\"attributeName\":\"location1\",\"required\":true},{\"attributeName\":\"location2\",\"required\":true},{\"attributeName\":\"location3\",\"required\":true},{\"attributeName\":\"postalCode\",\"required\":false},{\"attributeName\":\"photo\",\"required\":true}]}},\"apiKeyData\":{\"apiKeyId\":\"591856\",\"apiKeyStatus\":\"ACTIVE\",\"isDeleted\":false,\"apiKeyCommenceOn\":\"2021-11-10T06:12:52.994Z\",\"apiKeyExpiresOn\":\"2021-12-11T06:12:52.994Z\"}}";
		String mispLicenseDataObj = "{\"mispId\":\"1635497344579\",\"licenseKey\":\"rtaCBxYlsrWeuYpLgfUFT5ic6LetsOZzbvxBEe8yR5FCKkEsvQ\",\"mispCommenceOn\":\"2021-11-10T06:12:52.994Z\",\"mispExpiresOn\":\"2021-12-11T06:12:52.994Z\",\"mispStatus\":\"INACTIVE\"}";
		PartnerMapping partnerMapping = mapper.readValue(partnerMappingObj, PartnerMapping.class);
		Optional<PartnerMapping> partnerMappingDataOptional = Optional.of(partnerMapping);
		Mockito.<Optional<PartnerMapping>>when(partnerMappingRepo.findByPartnerIdAndApiKeyId("1635497344579", "591856"))
				.thenReturn(partnerMappingDataOptional);
		MispLicenseData mispLicenseData = mapper.readValue(mispLicenseDataObj, MispLicenseData.class);
		Optional<MispLicenseData> mispLicOptional = Optional.of(mispLicenseData);
		Mockito.when(mispLicDataRepo.findByLicenseKey("rtaCBxYlsrWeuYpLgfUFT5ic6LetsOZzbvxBEe8yR5FCKkEsvQ")).thenReturn(mispLicOptional);
		partnerServiceManager.validateAndGetPolicy("1635497344579", "591856","rtaCBxYlsrWeuYpLgfUFT5ic6LetsOZzbvxBEe8yR5FCKkEsvQ", false);
	}

	// IdAuthenticationBusinessException: IDA-MPA-019 --> Partner policy is not active
	@Test(expected = IdAuthenticationBusinessException.class)
	public void validateAndGetPolicyException4Test() throws IdAuthenticationBusinessException, Exception {
		String partnerMappingObj = "{\"partnerId\":\"1635497344579\",\"policyId\":\"21\",\"apiKeyId\":\"130956\",\"partnerData\":{\"partnerId\":\"1635497344579\",\"partnerName\":\"1635497344579\",\"partnerStatus\":\"ACTIVE\",\"isDeleted\":true},\"policyData\":{\"policyId\":\"644269\",\"policyName\":\"policy 1635497343191\",\"policyDescription\":\"Auth Policy\",\"policyStatus\":\"ACTIVE\",\"isDeleted\":true,\"policyCommenceOn\":\"2021-11-10T06:12:52.994Z\",\"policyExpiresOn\":\"2021-12-11T06:12:52.994Z\",\"policy\":{\"authPolicies\":[{\"authType\":\"otp\",\"authSubType\":\"\",\"mandatory\":true},{\"authType\":\"demo\",\"authSubType\":\"\",\"mandatory\":false},{\"authType\":\"bio\",\"authSubType\":\"FINGER\",\"mandatory\":true},{\"authType\":\"bio\",\"authSubType\":\"IRIS\",\"mandatory\":false},{\"authType\":\"bio\",\"authSubType\":\"FACE\",\"mandatory\":false},{\"authType\":\"kyc\",\"authSubType\":\"\",\"mandatory\":false}],\"allowedKycAttributes\":[{\"attributeName\":\"fullName\",\"required\":true},{\"attributeName\":\"dateOfBirth\",\"required\":true},{\"attributeName\":\"gender\",\"required\":true},{\"attributeName\":\"phone\",\"required\":true},{\"attributeName\":\"email\",\"required\":true},{\"attributeName\":\"addressLine1\",\"required\":true},{\"attributeName\":\"addressLine2\",\"required\":true},{\"attributeName\":\"addressLine3\",\"required\":true},{\"attributeName\":\"location1\",\"required\":true},{\"attributeName\":\"location2\",\"required\":true},{\"attributeName\":\"location3\",\"required\":true},{\"attributeName\":\"postalCode\",\"required\":false},{\"attributeName\":\"photo\",\"required\":true}]}},\"apiKeyData\":{\"apiKeyId\":\"591856\",\"apiKeyStatus\":\"ACTIVE\",\"isDeleted\":false,\"apiKeyCommenceOn\":\"2021-11-10T06:12:52.994Z\",\"apiKeyExpiresOn\":\"2021-12-11T06:12:52.994Z\"}}";
		String mispLicenseDataObj = "{\"mispId\":\"1635497344579\",\"licenseKey\":\"rtaCBxYlsrWeuYpLgfUFT5ic6LetsOZzbvxBEe8yR5FCKkEsvQ\",\"mispCommenceOn\":\"2021-11-10T06:12:52.994Z\",\"mispExpiresOn\":\"2021-12-11T06:12:52.994Z\",\"mispStatus\":\"INACTIVE\"}";
		PartnerMapping partnerMapping = mapper.readValue(partnerMappingObj, PartnerMapping.class);
		Optional<PartnerMapping> partnerMappingDataOptional = Optional.of(partnerMapping);
		Mockito.<Optional<PartnerMapping>>when(partnerMappingRepo.findByPartnerIdAndApiKeyId("1635497344579", "591856")).thenReturn(partnerMappingDataOptional);
		MispLicenseData mispLicenseData = mapper.readValue(mispLicenseDataObj, MispLicenseData.class);
		Optional<MispLicenseData> mispLicOptional = Optional.of(mispLicenseData);
		Mockito.when(mispLicDataRepo.findByLicenseKey("rtaCBxYlsrWeuYpLgfUFT5ic6LetsOZzbvxBEe8yR5FCKkEsvQ")).thenReturn(mispLicOptional);
		partnerServiceManager.validateAndGetPolicy("1635497344579", "591856","rtaCBxYlsrWeuYpLgfUFT5ic6LetsOZzbvxBEe8yR5FCKkEsvQ", false);
	}

	// io.mosip.authentication.core.exception.IdAuthenticationBusinessException: IDA-MPA-012 --> Partner is deactivated
	@Test(expected = IdAuthenticationBusinessException.class)
	public void validateAndGetPolicyException5Test() throws IdAuthenticationBusinessException, Exception {
		String partnerMappingObj = "{\"partnerId\":\"1635497344579\",\"policyId\":\"21\",\"apiKeyId\":\"130956\",\"partnerData\":{\"partnerId\":\"1635497344579\",\"partnerName\":\"1635497344579\",\"partnerStatus\":\"ACTIVE\",\"isDeleted\":true},\"policyData\":{\"policyId\":\"644269\",\"policyName\":\"policy 1635497343191\",\"policyDescription\":\"Auth Policy\",\"policyStatus\":\"ACTIVE\",\"isDeleted\":true,\"policyCommenceOn\":\"2021-10-10T06:12:52.994Z\",\"policyExpiresOn\":\"2021-12-11T06:12:52.994Z\",\"policy\":{\"authPolicies\":[{\"authType\":\"otp\",\"authSubType\":\"\",\"mandatory\":true},{\"authType\":\"demo\",\"authSubType\":\"\",\"mandatory\":false},{\"authType\":\"bio\",\"authSubType\":\"FINGER\",\"mandatory\":true},{\"authType\":\"bio\",\"authSubType\":\"IRIS\",\"mandatory\":false},{\"authType\":\"bio\",\"authSubType\":\"FACE\",\"mandatory\":false},{\"authType\":\"kyc\",\"authSubType\":\"\",\"mandatory\":false}],\"allowedKycAttributes\":[{\"attributeName\":\"fullName\",\"required\":true},{\"attributeName\":\"dateOfBirth\",\"required\":true},{\"attributeName\":\"gender\",\"required\":true},{\"attributeName\":\"phone\",\"required\":true},{\"attributeName\":\"email\",\"required\":true},{\"attributeName\":\"addressLine1\",\"required\":true},{\"attributeName\":\"addressLine2\",\"required\":true},{\"attributeName\":\"addressLine3\",\"required\":true},{\"attributeName\":\"location1\",\"required\":true},{\"attributeName\":\"location2\",\"required\":true},{\"attributeName\":\"location3\",\"required\":true},{\"attributeName\":\"postalCode\",\"required\":false},{\"attributeName\":\"photo\",\"required\":true}]}},\"apiKeyData\":{\"apiKeyId\":\"591856\",\"apiKeyStatus\":\"INACTIVE\",\"isDeleted\":false,\"apiKeyCommenceOn\":\"2021-11-10T06:12:52.994Z\",\"apiKeyExpiresOn\":\"2021-12-11T06:12:52.994Z\"}}";
		String mispLicenseDataObj = "{\"mispId\":\"1635497344579\",\"licenseKey\":\"rtaCBxYlsrWeuYpLgfUFT5ic6LetsOZzbvxBEe8yR5FCKkEsvQ\",\"mispCommenceOn\":\"2021-11-10T06:12:52.994Z\",\"mispExpiresOn\":\"2021-12-11T06:12:52.994Z\",\"mispStatus\":\"INACTIVE\"}";
		PartnerMapping partnerMapping = mapper.readValue(partnerMappingObj, PartnerMapping.class);
		Optional<PartnerMapping> partnerMappingDataOptional = Optional.of(partnerMapping);
		Mockito.<Optional<PartnerMapping>>when(partnerMappingRepo.findByPartnerIdAndApiKeyId("1635497344579", "591856")).thenReturn(partnerMappingDataOptional);
		MispLicenseData mispLicenseData = mapper.readValue(mispLicenseDataObj, MispLicenseData.class);
		Optional<MispLicenseData> mispLicOptional = Optional.of(mispLicenseData);
		Mockito.when(mispLicDataRepo.findByLicenseKey("rtaCBxYlsrWeuYpLgfUFT5ic6LetsOZzbvxBEe8yR5FCKkEsvQ")).thenReturn(mispLicOptional);
		partnerServiceManager.validateAndGetPolicy("1635497344579", "591856","rtaCBxYlsrWeuYpLgfUFT5ic6LetsOZzbvxBEe8yR5FCKkEsvQ", false);
	}

	// io.mosip.authentication.core.exception.IdAuthenticationBusinessException: IDA-MPA-009 --> Partner is not registered
	@Test(expected = IdAuthenticationBusinessException.class)
	public void validateAndGetPolicyException6Test() throws IdAuthenticationBusinessException, Exception {
		String partnerMappingObj = "{\"partnerId\":\"1635497344579\",\"policyId\":\"21\",\"apiKeyId\":\"130956\",\"partnerData\":{\"partnerId\":\"1635497344579\",\"partnerName\":\"1635497344579\",\"partnerStatus\":\"ACTIVE\",\"isDeleted\":true},\"policyData\":{\"policyId\":\"644269\",\"policyName\":\"policy 1635497343191\",\"policyDescription\":\"Auth Policy\",\"policyStatus\":\"ACTIVE\",\"isDeleted\":true,\"policyCommenceOn\":\"2021-10-10T06:12:52.994Z\",\"policyExpiresOn\":\"2021-12-11T06:12:52.994Z\",\"policy\":{\"authPolicies\":[{\"authType\":\"otp\",\"authSubType\":\"\",\"mandatory\":true},{\"authType\":\"demo\",\"authSubType\":\"\",\"mandatory\":false},{\"authType\":\"bio\",\"authSubType\":\"FINGER\",\"mandatory\":true},{\"authType\":\"bio\",\"authSubType\":\"IRIS\",\"mandatory\":false},{\"authType\":\"bio\",\"authSubType\":\"FACE\",\"mandatory\":false},{\"authType\":\"kyc\",\"authSubType\":\"\",\"mandatory\":false}],\"allowedKycAttributes\":[{\"attributeName\":\"fullName\",\"required\":true},{\"attributeName\":\"dateOfBirth\",\"required\":true},{\"attributeName\":\"gender\",\"required\":true},{\"attributeName\":\"phone\",\"required\":true},{\"attributeName\":\"email\",\"required\":true},{\"attributeName\":\"addressLine1\",\"required\":true},{\"attributeName\":\"addressLine2\",\"required\":true},{\"attributeName\":\"addressLine3\",\"required\":true},{\"attributeName\":\"location1\",\"required\":true},{\"attributeName\":\"location2\",\"required\":true},{\"attributeName\":\"location3\",\"required\":true},{\"attributeName\":\"postalCode\",\"required\":false},{\"attributeName\":\"photo\",\"required\":true}]}},\"apiKeyData\":{\"apiKeyId\":\"591856\",\"apiKeyStatus\":\"ACTIVE\",\"isDeleted\":false,\"apiKeyCommenceOn\":\"2021-11-10T06:12:52.994Z\",\"apiKeyExpiresOn\":\"2021-12-11T06:12:52.994Z\"}}";
		String mispLicenseDataObj = "{\"mispId\":\"1635497344579\",\"licenseKey\":\"rtaCBxYlsrWeuYpLgfUFT5ic6LetsOZzbvxBEe8yR5FCKkEsvQ\",\"mispCommenceOn\":\"2021-11-10T06:12:52.994Z\",\"mispExpiresOn\":\"2021-12-11T06:12:52.994Z\",\"mispStatus\":\"INACTIVE\"}";
		PartnerMapping partnerMapping = mapper.readValue(partnerMappingObj, PartnerMapping.class);
		Optional<PartnerMapping> partnerMappingDataOptional = Optional.of(partnerMapping);
		Mockito.<Optional<PartnerMapping>>when(partnerMappingRepo.findByPartnerIdAndApiKeyId("1635497344579", "591856")).thenReturn(partnerMappingDataOptional);
		MispLicenseData mispLicenseData = mapper.readValue(mispLicenseDataObj, MispLicenseData.class);
		Optional<MispLicenseData> mispLicOptional = Optional.of(mispLicenseData);
		Mockito.when(mispLicDataRepo.findByLicenseKey("rtaCBxYlsrWeuYpLgfUFT5ic6LetsOZzbvxBEe8yR5FCKkEsvQ")).thenReturn(mispLicOptional);
		partnerServiceManager.validateAndGetPolicy("1635497344579", "591856","rtaCBxYlsrWeuYpLgfUFT5ic6LetsOZzbvxBEe8yR5FCKkEsvQ", false);
	}

	// io.mosip.authentication.core.exception.IdAuthenticationBusinessException: IDA-MPA-017 --> License key of MISP is blocked
	@Test(expected = IdAuthenticationBusinessException.class)
	public void validateAndGetPolicyMispException1Test() throws IdAuthenticationBusinessException, Exception {
		String partnerMappingObj = "{\"partnerId\":\"1635497344579\",\"policyId\":\"21\",\"apiKeyId\":\"130956\",\"partnerData\":{\"partnerId\":\"1635497344579\",\"partnerName\":\"1635497344579\",\"partnerStatus\":\"ACTIVE\",\"isDeleted\":true},\"policyData\":{\"policyId\":\"644269\",\"policyName\":\"policy 1635497343191\",\"policyDescription\":\"Auth Policy\",\"policyStatus\":\"ACTIVE\",\"isDeleted\":true,\"policyCommenceOn\":\"2021-10-10T06:12:52.994Z\",\"policyExpiresOn\":\"2021-12-11T06:12:52.994Z\",\"policy\":{\"authPolicies\":[{\"authType\":\"otp\",\"authSubType\":\"\",\"mandatory\":true},{\"authType\":\"demo\",\"authSubType\":\"\",\"mandatory\":false},{\"authType\":\"bio\",\"authSubType\":\"FINGER\",\"mandatory\":true},{\"authType\":\"bio\",\"authSubType\":\"IRIS\",\"mandatory\":false},{\"authType\":\"bio\",\"authSubType\":\"FACE\",\"mandatory\":false},{\"authType\":\"kyc\",\"authSubType\":\"\",\"mandatory\":false}],\"allowedKycAttributes\":[{\"attributeName\":\"fullName\",\"required\":true},{\"attributeName\":\"dateOfBirth\",\"required\":true},{\"attributeName\":\"gender\",\"required\":true},{\"attributeName\":\"phone\",\"required\":true},{\"attributeName\":\"email\",\"required\":true},{\"attributeName\":\"addressLine1\",\"required\":true},{\"attributeName\":\"addressLine2\",\"required\":true},{\"attributeName\":\"addressLine3\",\"required\":true},{\"attributeName\":\"location1\",\"required\":true},{\"attributeName\":\"location2\",\"required\":true},{\"attributeName\":\"location3\",\"required\":true},{\"attributeName\":\"postalCode\",\"required\":false},{\"attributeName\":\"photo\",\"required\":true}]}},\"apiKeyData\":{\"apiKeyId\":\"591856\",\"apiKeyStatus\":\"ACTIVE\",\"isDeleted\":false,\"apiKeyCommenceOn\":\"2021-10-10T06:12:52.994Z\",\"apiKeyExpiresOn\":\"2021-12-11T06:12:52.994Z\"}}";
		String mispLicenseDataObj = "{\"mispId\":\"1635497344579\",\"licenseKey\":\"rtaCBxYlsrWeuYpLgfUFT5ic6LetsOZzbvxBEe8yR5FCKkEsvQ\",\"mispCommenceOn\":\"2021-10-10T06:12:52.994Z\",\"mispExpiresOn\":\"2021-10-30T06:12:52.994Z\",\"mispStatus\":\"INACTIVE\"}";
		PartnerMapping partnerMapping = mapper.readValue(partnerMappingObj, PartnerMapping.class);
		Optional<PartnerMapping> partnerMappingDataOptional = Optional.of(partnerMapping);
		Mockito.<Optional<PartnerMapping>>when(partnerMappingRepo.findByPartnerIdAndApiKeyId("1635497344579", "591856")).thenReturn(partnerMappingDataOptional);
		MispLicenseData mispLicenseData = mapper.readValue(mispLicenseDataObj, MispLicenseData.class);
		Optional<MispLicenseData> mispLicOptional = Optional.of(mispLicenseData);
		Mockito.when(mispLicDataRepo.findByLicenseKey("rtaCBxYlsrWeuYpLgfUFT5ic6LetsOZzbvxBEe8yR5FCKkEsvQ")).thenReturn(mispLicOptional);
		partnerServiceManager.validateAndGetPolicy("1635497344579", "591856","rtaCBxYlsrWeuYpLgfUFT5ic6LetsOZzbvxBEe8yR5FCKkEsvQ", false);
	}

	// io.mosip.authentication.core.exception.IdAuthenticationBusinessException:IDA-MPA-008 --> License key of MISP has expired
	@Test(expected = IdAuthenticationBusinessException.class)
	public void validateAndGetPolicyMispException2Test() throws IdAuthenticationBusinessException, Exception {
		String partnerMappingObj = "{\"partnerId\":\"1635497344579\",\"policyId\":\"21\",\"apiKeyId\":\"130956\",\"partnerData\":{\"partnerId\":\"1635497344579\",\"partnerName\":\"1635497344579\",\"partnerStatus\":\"ACTIVE\",\"isDeleted\":true},\"policyData\":{\"policyId\":\"644269\",\"policyName\":\"policy 1635497343191\",\"policyDescription\":\"Auth Policy\",\"policyStatus\":\"ACTIVE\",\"isDeleted\":true,\"policyCommenceOn\":\"2021-10-10T06:12:52.994Z\",\"policyExpiresOn\":\"2021-12-11T06:12:52.994Z\",\"policy\":{\"authPolicies\":[{\"authType\":\"otp\",\"authSubType\":\"\",\"mandatory\":true},{\"authType\":\"demo\",\"authSubType\":\"\",\"mandatory\":false},{\"authType\":\"bio\",\"authSubType\":\"FINGER\",\"mandatory\":true},{\"authType\":\"bio\",\"authSubType\":\"IRIS\",\"mandatory\":false},{\"authType\":\"bio\",\"authSubType\":\"FACE\",\"mandatory\":false},{\"authType\":\"kyc\",\"authSubType\":\"\",\"mandatory\":false}],\"allowedKycAttributes\":[{\"attributeName\":\"fullName\",\"required\":true},{\"attributeName\":\"dateOfBirth\",\"required\":true},{\"attributeName\":\"gender\",\"required\":true},{\"attributeName\":\"phone\",\"required\":true},{\"attributeName\":\"email\",\"required\":true},{\"attributeName\":\"addressLine1\",\"required\":true},{\"attributeName\":\"addressLine2\",\"required\":true},{\"attributeName\":\"addressLine3\",\"required\":true},{\"attributeName\":\"location1\",\"required\":true},{\"attributeName\":\"location2\",\"required\":true},{\"attributeName\":\"location3\",\"required\":true},{\"attributeName\":\"postalCode\",\"required\":false},{\"attributeName\":\"photo\",\"required\":true}]}},\"apiKeyData\":{\"apiKeyId\":\"591856\",\"apiKeyStatus\":\"ACTIVE\",\"isDeleted\":false,\"apiKeyCommenceOn\":\"2021-10-10T06:12:52.994Z\",\"apiKeyExpiresOn\":\"2021-12-11T06:12:52.994Z\"}}";
		String mispLicenseDataObj = "{\"mispId\":\"1635497344579\",\"licenseKey\":\"rtaCBxYlsrWeuYpLgfUFT5ic6LetsOZzbvxBEe8yR5FCKkEsvQ\",\"mispCommenceOn\":\"2021-10-10T06:12:52.994Z\",\"mispExpiresOn\":\"2021-10-30T06:12:52.994Z\",\"mispStatus\":\"ACTIVE\"}";
		PartnerMapping partnerMapping = mapper.readValue(partnerMappingObj, PartnerMapping.class);
		Optional<PartnerMapping> partnerMappingDataOptional = Optional.of(partnerMapping);
		Mockito.<Optional<PartnerMapping>>when(partnerMappingRepo.findByPartnerIdAndApiKeyId("1635497344579", "591856")).thenReturn(partnerMappingDataOptional);
		MispLicenseData mispLicenseData = mapper.readValue(mispLicenseDataObj, MispLicenseData.class);
		Optional<MispLicenseData> mispLicOptional = Optional.of(mispLicenseData);
		Mockito.when(mispLicDataRepo.findByLicenseKey("rtaCBxYlsrWeuYpLgfUFT5ic6LetsOZzbvxBEe8yR5FCKkEsvQ")).thenReturn(mispLicOptional);
		partnerServiceManager.validateAndGetPolicy("1635497344579", "591856","rtaCBxYlsrWeuYpLgfUFT5ic6LetsOZzbvxBEe8yR5FCKkEsvQ", false);
	}

	// io.mosip.authentication.core.exception.IdAuthenticationBusinessException: IDA-MPA-007 --> License key does not belong to a registered MISP
	@Test(expected = IdAuthenticationBusinessException.class)
	public void validateAndGetPolicyMispException3Test() throws IdAuthenticationBusinessException, Exception {
		Optional<PartnerMapping> partnerMappingDataOptional = Optional.empty();
		Mockito.<Optional<PartnerMapping>>when(partnerMappingRepo.findByPartnerIdAndApiKeyId("1635497344579", "591856"))
				.thenReturn(partnerMappingDataOptional);
		partnerServiceManager.validateAndGetPolicy("1635497344579", "591856",
				"rtaCBxYlsrWeuYpLgfUFT5ic6LetsOZzbvxBEe8yR5FCKkEsvQ", false);
	}

	@Test
	public void handleApiKeyApprovedTest() throws Exception {
		partnerData.put("partnerId", "879");
		partnerData.put("partnerName", "879");
		partnerData.put("partnerStatus", "ACTIVE");

		policyData.put("policyId", "644269");
		policyData.put("policyName", "policy 1635497343191");
		policyData.put("policyDescription", "Auth Policy");
		policyData.put("policyStatus", "ACTIVE");
		
		apiKeyData.put("apiKeyId", "87912");
		apiKeyData.put("apiKeyStatus", "ACTIVE");
		apiKeyData.put("apiKeyCommenceOn", "2021-10-10T06:12:52.994Z");
		apiKeyData.put("apiKeyExpiresOn", "2021-12-10T06:12:52.994Z");

		data.put("partnerData", partnerData);
		data.put("policyData", policyData);
		data.put("apiKeyData", apiKeyData);
		
		event.setData(data);
		event.setType(type);
		event.setDataShareUri("");
		event.setId("845eef0b-d30c-4bc8-90f0-e153c1cf0be0");
		event.setTimestamp("2021-10-26T04:30:32.250Z");
		event.setTransactionId("1234567895");
		eventModel.setEvent(event);
		eventModel.setPublishedOn("2021-10-26T04:30:32.250Z");
		eventModel.setPublisher("PartnerManagementServiceImpl");
		eventModel.setTopic("APIKEY_APPROVED");
		partnerServiceManager.handleApiKeyApproved(eventModel);
	}
	
	@Test
	public void handleApiKeyApprovedTest1() throws Exception {
		partnerData.put("partnerId", "879");
		partnerData.put("partnerName", "879");
		partnerData.put("partnerStatus", "ACTIVE");

		policyData.put("policyId", "644269");
		policyData.put("policyName", "policy 1635497343191");
		policyData.put("policyDescription", "Auth Policy");
		policyData.put("policyStatus", "ACTIVE");

		apiKeyData.put("apiKeyId", "87912");
		apiKeyData.put("apiKeyStatus", "ACTIVE");
		apiKeyData.put("apiKeyCommenceOn", "2021-10-10T06:12:52.994Z");
		apiKeyData.put("apiKeyExpiresOn", "2021-12-10T06:12:52.994Z");

		data.put("partnerData", partnerData);
		data.put("policyData", policyData);
		data.put("apiKeyData", apiKeyData);
		
		event.setData(data);
		event.setType(type);
		event.setDataShareUri("");
		event.setId("845eef0b-d30c-4bc8-90f0-e153c1cf0be0");
		event.setTimestamp("2021-10-26T04:30:32.250Z");
		event.setTransactionId("1234567895");
		eventModel.setEvent(event);
		eventModel.setPublishedOn("2021-10-26T04:30:32.250Z");
		eventModel.setPublisher("PartnerManagementServiceImpl");
		eventModel.setTopic("APIKEY_APPROVED");
		Mockito.when(securityManager.getUser()).thenReturn("IdaUser");
		partnerServiceManager.handleApiKeyApproved(eventModel);
	}

	@Test
	public void handleApiKeyUpdatedTest() throws Exception {
		apiKeyData.put("apiKeyId", "87912");
		apiKeyData.put("apiKeyStatus", "ACTIVE");
		apiKeyData.put("apiKeyCommenceOn", "2021-10-10T06:12:52.994Z");
		apiKeyData.put("apiKeyExpiresOn", "2021-12-10T06:12:52.994Z");
		data.put("apiKeyData", apiKeyData);
		
		event.setData(data);
		event.setType(type);
		event.setDataShareUri("");
		event.setId("835eef0b-d30c-4bc8-90f0-e153c1cf0be0");
		event.setTimestamp("2021-10-26T04:30:32.250Z");
		event.setTransactionId("1234567894");
		eventModel.setEvent(event);
		eventModel.setPublishedOn("2021-10-26T04:30:32.250Z");
		eventModel.setPublisher(null);
		eventModel.setTopic("APIKEY_UPDATED");
		Mockito.when(securityManager.getUser()).thenReturn("IdaUser");
		partnerServiceManager.handleApiKeyUpdated(eventModel);		
	}
	
	@Test
	public void handleApiKeyUpdatedTest2() throws Exception {
		String apiKeyDataObj = "{\"apiKeyId\":\"87912\",\"apiKeyStatus\":\"ACTIVE\",\"apiKeyCommenceOn\":\"2021-10-10T06:12:52.994Z\",\"apiKeyExpiresOn\":\"2021-12-11T06:12:52.994Z\"}";
		ApiKeyData apiKeyData = mapper.readValue(apiKeyDataObj, ApiKeyData.class);
		Optional<ApiKeyData> apiKeyDataOptional = Optional.of(apiKeyData);
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("apiKeyData", apiKeyData);
		
		event.setData(data);
		event.setType(type);
		event.setDataShareUri("");
		event.setId("835eef0b-d30c-4bc8-90f0-e153c1cf0be0");
		event.setTimestamp("2021-10-26T04:30:32.250Z");
		event.setTransactionId("1234567894");
		eventModel.setEvent(event);
		eventModel.setPublishedOn("2021-10-26T04:30:32.250Z");
		eventModel.setPublisher("PartnerManagementServiceImpl");
		eventModel.setTopic("APIKEY_UPDATED");
		Mockito.when(securityManager.getUser()).thenReturn("IdaUser");
		
		Mockito.when(apiKeyRepo.findById("87912")).thenReturn(apiKeyDataOptional);
		partnerServiceManager.handleApiKeyUpdated(eventModel);		
	}
	
	@Test
	public void updatePartnerDataTest() throws Exception {
		String partnerData = "{\"partnerId\":\"1635497344579\",\"certificateData\":\"data\",\"partnerName\":\"1635497344579\",\"partnerStatus\":\"INACTIVE\",\"isDeleted\":true}";
		PartnerData partnerEventData = mapper.readValue(partnerData, PartnerData.class);
		Optional<PartnerData> partnerDataOptional = Optional.of(partnerEventData);
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("partnerData", partnerEventData);
		
		event.setData(data);
		event.setType(type);
		event.setDataShareUri("");
		event.setId("825eef0b-d30c-4bc8-90f0-e153c1cf0be0");
		event.setTimestamp("2021-10-26T04:30:32.250Z");
		event.setTransactionId("1234557893");
		eventModel.setEvent(event);
		eventModel.setPublishedOn("2021-10-26T04:30:32.250Z");
		eventModel.setPublisher("PartnerManagementServiceImpl");
		eventModel.setTopic("PARTNER_UPDATED");
		Mockito.when(securityManager.getUser()).thenReturn("IdaUser");
		
		Mockito.when(partnerDataRepo.findById("1635497344579")).thenReturn(partnerDataOptional);
		partnerServiceManager.updatePartnerData(eventModel);		
	}
	
	@Test
	public void updatePartnerDataTest1() throws Exception {
		String partnerData = "{\"partnerId\":\"1635497344579\",\"certificateData\":\"data\",\"partnerName\":\"1635497344579\",\"partnerStatus\":\"INACTIVE\",\"isDeleted\":true}";
		PartnerData partnerEventData = mapper.readValue(partnerData, PartnerData.class);
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("partnerData", partnerEventData);
		
		event.setData(data);
		event.setType(type);
		event.setDataShareUri("");
		event.setId("825eef0b-d30c-4bc8-90f0-e153c1cf0be0");
		event.setTimestamp("2021-10-26T04:30:32.250Z");
		event.setTransactionId("1234557893");
		eventModel.setEvent(event);
		eventModel.setPublishedOn("2021-10-26T04:30:32.250Z");
		eventModel.setPublisher("PartnerManagementServiceImpl");
		eventModel.setTopic("PARTNER_UPDATED");
		partnerServiceManager.updatePartnerData(eventModel);		
	}
	
	@Test
	public void updatePolicyDataTest() throws Exception {
		String policyData = "{\"policyId\":\"644269\",\"policyName\":\"policy 1635497343191\",\"policyDescription\":\"Auth Policy\",\"policyStatus\":\"INACTIVE\",\"isDeleted\":true,\"policyCommenceOn\":\"2021-11-10T06:12:52.994Z\",\"policyExpiresOn\":\"2021-12-11T06:12:52.994Z\",\"policy\":{\"authPolicies\":[{\"authType\":\"otp\",\"authSubType\":\"\",\"mandatory\":true},{\"authType\":\"demo\",\"authSubType\":\"\",\"mandatory\":false},{\"authType\":\"bio\",\"authSubType\":\"FINGER\",\"mandatory\":true},{\"authType\":\"bio\",\"authSubType\":\"IRIS\",\"mandatory\":false},{\"authType\":\"bio\",\"authSubType\":\"FACE\",\"mandatory\":false},{\"authType\":\"kyc\",\"authSubType\":\"\",\"mandatory\":false}],\"allowedKycAttributes\":[{\"attributeName\":\"fullName\",\"required\":true},{\"attributeName\":\"dateOfBirth\",\"required\":true},{\"attributeName\":\"gender\",\"required\":true},{\"attributeName\":\"phone\",\"required\":true},{\"attributeName\":\"email\",\"required\":true},{\"attributeName\":\"addressLine1\",\"required\":true},{\"attributeName\":\"addressLine2\",\"required\":true},{\"attributeName\":\"addressLine3\",\"required\":true},{\"attributeName\":\"location1\",\"required\":true},{\"attributeName\":\"location2\",\"required\":true},{\"attributeName\":\"location3\",\"required\":true},{\"attributeName\":\"postalCode\",\"required\":false},{\"attributeName\":\"photo\",\"required\":true}]}}";
		PolicyData policyEventData = mapper.readValue(policyData, PolicyData.class);
		Optional<PolicyData> policyDataOptional = Optional.of(policyEventData);
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("policyData", policyEventData);
		
		event.setData(data);
		event.setType(type);
		event.setDataShareUri("");
		event.setId("815eef0b-d30c-4bc8-90f0-e153c1cf0be0");
		event.setTimestamp("2021-10-26T04:30:32.250Z");
		event.setTransactionId("1234557892");
		eventModel.setEvent(event);
		eventModel.setPublishedOn("2021-10-26T04:30:32.250Z");
		eventModel.setPublisher("PartnerManagementServiceImpl");
		eventModel.setTopic("POLICY_UPDATED");
		
		Mockito.when(policyDataRepo.findById("644269")).thenReturn(policyDataOptional);
		partnerServiceManager.updatePolicyData(eventModel);		
	}
	
	@Test
	public void updatePolicyDataTest1() throws Exception {
		String policyData = "{\"policyId\":\"644269\",\"policyName\":\"policy 1635497343191\",\"policyDescription\":\"Auth Policy\",\"policyStatus\":\"INACTIVE\",\"isDeleted\":true,\"policyCommenceOn\":\"2021-11-10T06:12:52.994Z\",\"policyExpiresOn\":\"2021-12-11T06:12:52.994Z\",\"policy\":{\"authPolicies\":[{\"authType\":\"otp\",\"authSubType\":\"\",\"mandatory\":true},{\"authType\":\"demo\",\"authSubType\":\"\",\"mandatory\":false},{\"authType\":\"bio\",\"authSubType\":\"FINGER\",\"mandatory\":true},{\"authType\":\"bio\",\"authSubType\":\"IRIS\",\"mandatory\":false},{\"authType\":\"bio\",\"authSubType\":\"FACE\",\"mandatory\":false},{\"authType\":\"kyc\",\"authSubType\":\"\",\"mandatory\":false}],\"allowedKycAttributes\":[{\"attributeName\":\"fullName\",\"required\":true},{\"attributeName\":\"dateOfBirth\",\"required\":true},{\"attributeName\":\"gender\",\"required\":true},{\"attributeName\":\"phone\",\"required\":true},{\"attributeName\":\"email\",\"required\":true},{\"attributeName\":\"addressLine1\",\"required\":true},{\"attributeName\":\"addressLine2\",\"required\":true},{\"attributeName\":\"addressLine3\",\"required\":true},{\"attributeName\":\"location1\",\"required\":true},{\"attributeName\":\"location2\",\"required\":true},{\"attributeName\":\"location3\",\"required\":true},{\"attributeName\":\"postalCode\",\"required\":false},{\"attributeName\":\"photo\",\"required\":true}]}}";
		PolicyData policyEventData = mapper.readValue(policyData, PolicyData.class);
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("policyData", policyEventData);
		
		event.setData(data);
		event.setType(type);
		event.setDataShareUri("");
		event.setId("815eef0b-d30c-4bc8-90f0-e153c1cf0be0");
		event.setTimestamp("2021-10-26T04:30:32.250Z");
		event.setTransactionId("1234557892");
		eventModel.setEvent(event);
		eventModel.setPublishedOn("2021-10-26T04:30:32.250Z");
		eventModel.setPublisher("PartnerManagementServiceImpl");
		eventModel.setTopic("POLICY_UPDATED");
		
		partnerServiceManager.updatePolicyData(eventModel);		
	}
	
	@Test
	public void updateMispLicenseDataTest() throws Exception {
		String mispLicenseDataObj = "{\"mispId\":\"1635497344579\",\"licenseKey\":\"rtaCBxYlsrWeuYpLgfUFT5ic6LetsOZzbvxBEe8yR5FCKkEsvQ\",\"mispCommenceOn\":\"2021-11-10T06:12:52.994Z\",\"mispExpiresOn\":\"2021-12-11T06:12:52.994Z\",\"mispStatus\":\"ACTIVE\"}";
		MispLicenseData mispLicenseData = mapper.readValue(mispLicenseDataObj, MispLicenseData.class);
		Optional<MispLicenseData> mispLicenseDataOptional = Optional.of(mispLicenseData);
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("mispLicenseData", mispLicenseData);
		
		event.setData(data);
		event.setType(type);
		event.setDataShareUri("");
		event.setId("805eef0b-d30c-4bc8-90f0-e153c1cf0be0");
		event.setTimestamp("2021-10-26T04:30:32.250Z");
		event.setTransactionId("1234557891");
		eventModel.setEvent(event);
		eventModel.setPublishedOn("2021-10-26T04:30:32.250Z");
		eventModel.setPublisher("PartnerManagementServiceImpl");
		eventModel.setTopic("MISP_LICENSE_UPDATED");
		
		Mockito.when(mispLicDataRepo.findById("1635497344579")).thenReturn(mispLicenseDataOptional);
		partnerServiceManager.updateMispLicenseData(eventModel);		
	}
	
	@Test
	public void updateMispLicenseDataTest1() throws Exception {
		String mispLicenseDataObj = "{\"mispId\":\"1635497344579\",\"licenseKey\":\"rtaCBxYlsrWeuYpLgfUFT5ic6LetsOZzbvxBEe8yR5FCKkEsvQ\",\"mispCommenceOn\":\"2021-11-10T06:12:52.994Z\",\"mispExpiresOn\":\"2021-12-11T06:12:52.994Z\",\"mispStatus\":\"ACTIVE\"}";
		MispLicenseData mispLicenseData = mapper.readValue(mispLicenseDataObj, MispLicenseData.class);
		HashMap<String, Object> data = new HashMap<String, Object>();
		data.put("mispLicenseData", mispLicenseData);
		
		event.setData(data);
		event.setType(type);
		event.setDataShareUri("");
		event.setId("805eef0b-d30c-4bc8-90f0-e153c1cf0be0");
		event.setTimestamp("2021-10-26T04:30:32.250Z");
		event.setTransactionId("1234557891");
		eventModel.setEvent(event);
		eventModel.setPublishedOn("2021-10-26T04:30:32.250Z");
		eventModel.setPublisher("");
		eventModel.setTopic("MISP_LICENSE_UPDATED");
		partnerServiceManager.updateMispLicenseData(eventModel);		
	}
}

