//package io.mosip.authentication.otp.service.filter;
//
//import static io.mosip.authentication.core.constant.IdAuthCommonConstants.UTF_8;
//import static org.junit.Assert.assertTrue;
//
//import java.io.IOException;
//import java.lang.reflect.UndeclaredThrowableException;
//import java.util.HashMap;
//import java.util.Map;
//
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
//import org.springframework.core.env.EnvPropertyResolver;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.test.util.ReflectionTestUtils;
//
//import com.fasterxml.jackson.core.JsonParseException;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.JsonMappingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import io.mosip.authentication.common.service.cache.PartnerServiceCache;
//import io.mosip.authentication.common.service.factory.RestRequestFactory;
//import io.mosip.authentication.common.service.filter.IdAuthFilter;
//import io.mosip.authentication.common.service.helper.RestHelper;
//import io.mosip.authentication.common.service.helper.RestHelperImpl;
//import io.mosip.authentication.common.service.impl.patrner.PartnerServiceImpl;
//import io.mosip.authentication.common.service.integration.PartnerServiceManager;
//import io.mosip.authentication.common.service.transaction.manager.IdAuthSecurityManager;
//import io.mosip.authentication.core.partner.dto.PolicyDTO;
//import io.mosip.authentication.core.spi.partner.service.PartnerService;
//import io.mosip.kernel.crypto.jce.core.CryptoCore;
//import io.mosip.kernel.cryptomanager.service.impl.CryptomanagerServiceImpl;
//import io.mosip.kernel.keygenerator.bouncycastle.KeyGenerator;
//import io.mosip.kernel.signature.service.impl.SignatureServiceImpl;
//
///**
// * The Class OTPFilterTest.
// */
//@RunWith(SpringRunner.class)
//@ContextConfiguration(classes = { IdAuthFilter.class, CryptoCore.class, PartnerServiceImpl.class,
//		PartnerServiceManager.class, RestRequestFactory.class, RestHelper.class, RestHelperImpl.class,
//		PartnerServiceCache.class, ConcurrentMapCacheManager.class, IdAuthSecurityManager.class,
//		CryptomanagerServiceImpl.class, SignatureServiceImpl.class, KeyGenerator.class })
//@WebMvcTest
//public class OTPFilterTest {
//
//	/** The filter. */
//	OTPFilter filter = new OTPFilter();
//
//	/** The environment. */
//	@Autowired
//	private EnvPropertyResolver environment;
//
//	/** The mapper. */
//	@Autowired
//	private ObjectMapper mapper;
//
//	@Autowired
//	private CryptoCore cryptoCore;
//
//	@Autowired
//	PartnerService partnerService;
//
//	/**
//	 * Before.
//	 */
//	@Before
//	public void before() {
//		ReflectionTestUtils.setField(filter, "mapper", mapper);
//		ReflectionTestUtils.setField(filter, "env", environment);
//		ReflectionTestUtils.setField(filter, "cryptoCore", cryptoCore);
//		ReflectionTestUtils.setField(filter, "partnerService", partnerService);
//
//	}
//
//	/**
//	 * Test OTP request not allowed as per policy.
//	 */
//	@Test
//	public void testOTPRequestNotAllowed() {
//		String errorMessage = "IDA-MPA-005 --> OTP Request Usage not allowed as per policy";
//		Map<String, Object> reqMap = new HashMap<>();
//		try {
//			ReflectionTestUtils.invokeMethod(filter, "checkAllowedAuthTypeBasedOnPolicy", getPolicyFor9903348702934(),
//					reqMap);
//		} catch (UndeclaredThrowableException ex) {
//			assertTrue(ex.getCause().getMessage().equalsIgnoreCase(errorMessage));
//		}
//	}
//
//	private PolicyDTO getPolicyFor9903348702934() {
//		String policy = "{ \"policies\": { \"authPolicies\": [ { \"authType\": \"otp\", \"mandatory\": false }, { \"authType\": \"pin\", \"mandatory\": false }, { \"authType\": \"bio\", \"authSubType\": \"FINGER\", \"mandatory\": true }, { \"authType\": \"bio\", \"authSubType\": \"IRIS\", \"mandatory\": false }, { \"authType\": \"bio\", \"authSubType\": \"FACE\", \"mandatory\": false } ], \"allowedKycAttributes\": [ { \"attributeName\": \"UIN\", \"required\": false, \"masked\": true }, { \"attributeName\": \"fullName\", \"required\": true }, { \"attributeName\": \"dateOfBirth\", \"required\": true }, { \"attributeName\": \"gender\", \"required\": true }, { \"attributeName\": \"phone\", \"required\": true }, { \"attributeName\": \"email\", \"required\": true }, { \"attributeName\": \"addressLine1\", \"required\": true }, { \"attributeName\": \"addressLine2\", \"required\": true }, { \"attributeName\": \"addressLine3\", \"required\": true }, { \"attributeName\": \"region\", \"required\": true }, { \"attributeName\": \"province\", \"required\": true }, { \"attributeName\": \"city\", \"required\": true }, { \"attributeName\": \"postalCode\", \"required\": false }, { \"attributeName\": \"photo\", \"required\": true } ] } }";
//		String policyId = "9903348702934";
//		PolicyDTO polictDto = null;
//		try {
//			polictDto = mapper.readValue(policy.getBytes(UTF_8), PolicyDTO.class);
//			polictDto.setPolicyId(policyId);
//		} catch (JsonParseException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (JsonMappingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (JsonProcessingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		return polictDto;
//	}
//}
