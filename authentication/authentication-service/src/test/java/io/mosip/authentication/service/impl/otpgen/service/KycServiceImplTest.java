package io.mosip.authentication.service.impl.otpgen.service;

import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.core.dto.indauth.IdentityInfoDTO;
import io.mosip.authentication.core.dto.indauth.KycInfo;
import io.mosip.authentication.core.dto.indauth.KycType;
import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.authentication.core.spi.id.service.IdAuthService;
import io.mosip.authentication.core.spi.id.service.IdInfoService;
import io.mosip.authentication.service.config.IDAMappingConfig;
import io.mosip.authentication.service.impl.id.service.impl.IdAuthServiceImpl;
import io.mosip.authentication.service.impl.id.service.impl.IdInfoServiceImpl;
import io.mosip.authentication.service.impl.indauth.service.KycServiceImpl;
import io.mosip.authentication.service.impl.indauth.service.demo.DemoHelper;
import io.mosip.authentication.service.integration.IdTemplateManager;
import io.mosip.kernel.core.pdfgenerator.spi.PDFGenerator;
import io.mosip.kernel.pdfgenerator.itext.impl.PDFGeneratorImpl;
import io.mosip.kernel.templatemanager.velocity.builder.TemplateManagerBuilderImpl;

/**
 * Test class for KycServiceImpl.
 *
 * @author Sanjay Murali
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class, IdTemplateManager.class, TemplateManagerBuilderImpl.class })
@WebMvcTest
@Import(IDAMappingConfig.class)
@TestPropertySource("classpath:sample-output-test.properties")
public class KycServiceImplTest {
	
	@Autowired
	Environment env;
	
	@Autowired
	Environment environment;
	
	@InjectMocks
	private DemoHelper demoHelper;
	
	@Autowired
	private IDAMappingConfig idMappingConfig;
	
	IdInfoService idInfoService = new IdInfoServiceImpl();
	
	@InjectMocks
	private KycServiceImpl kycServiceImpl;
	
	IdAuthService idAuthService = new IdAuthServiceImpl();
	
	@Autowired
	IdTemplateManager idTemplateManager;
	
	PDFGenerator pdfGenerator = new PDFGeneratorImpl();
	
	@Value("${sample.demo.entity}")
	String value;
	
	@Before
	public void before() {
		ResourceBundleMessageSource source = new ResourceBundleMessageSource();
		source.setBasename("eKycPDFTemplate");
		ReflectionTestUtils.setField(kycServiceImpl, "messageSource", source);
		ReflectionTestUtils.setField(kycServiceImpl, "env", env);
		ReflectionTestUtils.setField(kycServiceImpl, "idInfoService", idInfoService);
		ReflectionTestUtils.setField(kycServiceImpl, "idAuthService", idAuthService);
		ReflectionTestUtils.setField(idInfoService, "value", value);
		ReflectionTestUtils.setField(kycServiceImpl, "idTemplateManager", idTemplateManager);
		ReflectionTestUtils.setField(kycServiceImpl, "pdfGenerator", pdfGenerator);
		ReflectionTestUtils.setField(kycServiceImpl, "demoHelper", demoHelper);
		ReflectionTestUtils.setField(demoHelper, "environment", environment);
		ReflectionTestUtils.setField(demoHelper, "idMappingConfig", idMappingConfig);
		
	}
	
	@SuppressWarnings("null")
	@Test
	public void validUIN() {
		try {
			Map<String, List<IdentityInfoDTO>> idInfo = null;
			List<IdentityInfoDTO> name = null;
			List<IdentityInfoDTO> dateOfBirth=null;
			List<IdentityInfoDTO> dateOfBirthType = null;
			List<IdentityInfoDTO> age = null;
			List<IdentityInfoDTO> gender = null;
			List<IdentityInfoDTO> phoneNumber = null;
			List<IdentityInfoDTO> emailId = null;
			List<IdentityInfoDTO> addressLine1 = null;
			List<IdentityInfoDTO> addressLine2 = null;
			List<IdentityInfoDTO> addressLine3 = null;
			List<IdentityInfoDTO> location1= null;
			List<IdentityInfoDTO> location2= null;
			List<IdentityInfoDTO> location3= null;
			List<IdentityInfoDTO> pinCode= null;
			List<IdentityInfoDTO> fullAddress= null;
			List<IdentityInfoDTO> leftEye= null;
			List<IdentityInfoDTO> rightEye= null;
			List<IdentityInfoDTO> leftIndex= null;
			List<IdentityInfoDTO> leftLittle= null;
			List<IdentityInfoDTO> leftMiddle= null;
			List<IdentityInfoDTO> leftRing= null;
			List<IdentityInfoDTO> leftThumb= null;
			List<IdentityInfoDTO> rightIndex= null;
			List<IdentityInfoDTO> rightLittle= null;
			List<IdentityInfoDTO> rightMiddle= null;
			List<IdentityInfoDTO> rightRing= null;
			List<IdentityInfoDTO> rightThumb= null;
			List<IdentityInfoDTO> face= null;
			IdentityInfoDTO dto = null;
			dto.setLanguage("FR");
			dto.setValue("John");
			name.add(dto);
			
			IdentityInfoDTO dto1 = null;
			dto1.setLanguage("FR");
			dto1.setValue("20-10-2018");
			dateOfBirth.add(dto1);
			idInfo.put("name", name);
			idInfo.put("dateOfBirth", dateOfBirth);
			idInfo.put("dateOfBirthType", dateOfBirthType);
			idInfo.put("age", age);
			idInfo.put("gender", gender);
			idInfo.put("phoneNumber", phoneNumber);
			idInfo.put("emailId", emailId);
			idInfo.put("addressLine1", addressLine1);
			idInfo.put("addressLine2", addressLine2);
			idInfo.put("addressLine3", addressLine3);
			idInfo.put("location1", location1);
			idInfo.put("location2", location2);
			idInfo.put("location3", location3);
			idInfo.put("pinCode", pinCode);
			idInfo.put("fullAddress", fullAddress);
			idInfo.put("leftEye", leftEye);
			idInfo.put("rightEye", rightEye);
			idInfo.put("leftIndex", leftIndex);
			idInfo.put("leftLittle", leftLittle);
			idInfo.put("leftMiddle", leftMiddle);
			idInfo.put("leftRing", leftRing);
			idInfo.put("leftThumb", leftThumb);
			idInfo.put("rightIndex", rightIndex);
			idInfo.put("rightLittle", rightLittle);
			idInfo.put("rightMiddle", rightMiddle);
			idInfo.put("rightRing", rightRing);
			idInfo.put("rightThumb", rightThumb);
			idInfo.put("face", face);
			KycInfo k = kycServiceImpl.retrieveKycInfo("12232323121", KycType.LIMITED, true, false, idInfo);
			assertNotNull(k);
		} catch (IdAuthenticationBusinessException e) {
			e.printStackTrace();
		}
	}
	@SuppressWarnings("null")
	@Test
	public void validUIN1() {
		try {
			Map<String, List<IdentityInfoDTO>> idInfo = null;
			List<IdentityInfoDTO> name = null;
			List<IdentityInfoDTO> dateOfBirth=null;
			List<IdentityInfoDTO> dateOfBirthType = null;
			List<IdentityInfoDTO> age = null;
			List<IdentityInfoDTO> gender = null;
			List<IdentityInfoDTO> phoneNumber = null;
			List<IdentityInfoDTO> emailId = null;
			List<IdentityInfoDTO> addressLine1 = null;
			List<IdentityInfoDTO> addressLine2 = null;
			List<IdentityInfoDTO> addressLine3 = null;
			List<IdentityInfoDTO> location1= null;
			List<IdentityInfoDTO> location2= null;
			List<IdentityInfoDTO> location3= null;
			List<IdentityInfoDTO> pinCode= null;
			List<IdentityInfoDTO> fullAddress= null;
			List<IdentityInfoDTO> leftEye= null;
			List<IdentityInfoDTO> rightEye= null;
			List<IdentityInfoDTO> leftIndex= null;
			List<IdentityInfoDTO> leftLittle= null;
			List<IdentityInfoDTO> leftMiddle= null;
			List<IdentityInfoDTO> leftRing= null;
			List<IdentityInfoDTO> leftThumb= null;
			List<IdentityInfoDTO> rightIndex= null;
			List<IdentityInfoDTO> rightLittle= null;
			List<IdentityInfoDTO> rightMiddle= null;
			List<IdentityInfoDTO> rightRing= null;
			List<IdentityInfoDTO> rightThumb= null;
			List<IdentityInfoDTO> face= null;
			idInfo.put("name", name);
			idInfo.put("dateOfBirth", dateOfBirth);
			idInfo.put("dateOfBirthType", dateOfBirthType);
			idInfo.put("age", age);
			idInfo.put("gender", gender);
			idInfo.put("phoneNumber", phoneNumber);
			idInfo.put("emailId", emailId);
			idInfo.put("addressLine1", addressLine1);
			idInfo.put("addressLine2", addressLine2);
			idInfo.put("addressLine3", addressLine3);
			idInfo.put("location1", location1);
			idInfo.put("location2", location2);
			idInfo.put("location3", location3);
			idInfo.put("pinCode", pinCode);
			idInfo.put("fullAddress", fullAddress);
			idInfo.put("leftEye", leftEye);
			idInfo.put("rightEye", rightEye);
			idInfo.put("leftIndex", leftIndex);
			idInfo.put("leftLittle", leftLittle);
			idInfo.put("leftMiddle", leftMiddle);
			idInfo.put("leftRing", leftRing);
			idInfo.put("leftThumb", leftThumb);
			idInfo.put("rightIndex", rightIndex);
			idInfo.put("rightLittle", rightLittle);
			idInfo.put("rightMiddle", rightMiddle);
			idInfo.put("rightRing", rightRing);
			idInfo.put("rightThumb", rightThumb);
			idInfo.put("face", face);
			KycInfo k = kycServiceImpl.retrieveKycInfo("12232323121", KycType.LIMITED, true, false, idInfo);		
			assertNotNull(k);
		} catch (IdAuthenticationBusinessException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void validUIN2() {
		try {
			Map<String, List<IdentityInfoDTO>> idInfo=null;
			KycInfo k = kycServiceImpl.retrieveKycInfo("12232323121", KycType.LIMITED, true, false, idInfo);	
			assertNotNull(k);
		} catch (IdAuthenticationBusinessException e) {
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void validUIN3() {
		try {			
			Map<String, List<IdentityInfoDTO>> idInfo=null;
			KycInfo k = kycServiceImpl.retrieveKycInfo("12232323121", KycType.LIMITED, true, false, idInfo);
			assertNotNull(k);
		} catch (IdAuthenticationBusinessException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void validUIN4() {
		try {			
			Map<String, List<IdentityInfoDTO>> idInfo=null;
			KycInfo k = kycServiceImpl.retrieveKycInfo("12232323121", KycType.LIMITED, true, false, idInfo);
			assertNotNull(k);
		} catch (IdAuthenticationBusinessException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void validUIN5() {
		try {			
			Map<String, List<IdentityInfoDTO>> idInfo=null;
			KycInfo k = kycServiceImpl.retrieveKycInfo("12232323121", KycType.LIMITED, true, false, idInfo);
			assertNotNull(k);
		} catch (IdAuthenticationBusinessException e) {
			e.printStackTrace();
		}
	}

}
