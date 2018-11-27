package io.mosip.authentication.service.impl.indauth.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.authentication.core.exception.IdAuthenticationBusinessException;
import io.mosip.kernel.core.pdfgenerator.spi.PDFGenerator;
import io.mosip.kernel.core.templatemanager.spi.TemplateManager;
import io.mosip.kernel.pdfgenerator.itext.impl.PDFGeneratorImpl;
import io.mosip.kernel.templatemanager.velocity.builder.TemplateConfigureBuilder;

/**
 * This class tests the IdAuthServiceImpl.java
 * 
 * @author Arun Bose
 */

@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class, PDFGeneratorImpl.class })
public class TemplatesTest {
	
	private static final String EKYC_SECTION_VALUE = "<table style=\"height: 410px; color: Black; border-top-width: 1px; border-right-width: 1px; border-bottom-width: 1px; border-left-width: 1px; border-bottom-color: #000000; border-left-color: #000000; border-top-color: #000000; border-right-color: #000000; border-right-style: solid; border-left-style: solid; border-bottom-style: solid; border-top-style: solid\" >\r\n" + 
				"<tbody>\r\n" + 
				"<tr style=\"height: 5px;\">\r\n" + 
				"<td style=\"width: 660.255px; height: 5px; text-align: center;\">&nbsp;E-KYC</td>\r\n" + 
				"</tr>\r\n" + 
				"<tr style=\"height: 279px; border-color: black;\">\r\n" + 
				"<td style=\" padding-left:30px; padding-bottom:30px; padding-right:30px; width: 660.255px; height: 279px; border-color: black;\">\r\n" + 
				"<table style=\"height: 332px;\">\r\n" + 
				"						<tbody>\r\n" + 
				"							<tr>\r\n" + 
				"								<td style=\"padding-top:10px\">POI</td>\r\n" + 
				"							</tr>\r\n" + 
				"							<tr style=\"height: 151.516px;\">\r\n" + 
				"								<td style=\"width: 650px; height: 151.516px;\">\r\n" + 
				"								<table style=\"padding:10px; width: 600px; height: 77px; border-right-style: solid; border-top-width: 1px; border-left-style: solid; border-right-width: 1px; border-bottom-width: 1px; border-bottom-style: solid; border-left-width: 1px; border-top-style: solid\">\r\n" + 
				"										<tbody>\r\n" + 
				"											<tr>\r\n" + 
				"												<td style=\"width: 137px;\\ \">\r\n" + 
				"												<table style=\";width: 150px; height: 144px; border-right-style: solid; border-top-width: 1px; border-left-style: solid; border-right-width: 1px; border-bottom-width: 1px; border-bottom-style: solid; border-left-width: 1px; border-top-style: solid\">\r\n" + 
				"												<tbody>\r\n" + 
				"												<tr>\r\n" + 
				"												<td style=\"width: 90px; height: 60px; \"><img src=\"photoUrl\" \r\n" + 
				"                width=\"100%\" height=\"100%\" border=\"2\" alt=\"Photo\" style=\"display: block;vertical-align: bottom;\"/></td>\r\n" + 
				"												</tr>\r\n" + 
				"												</tbody>\r\n" + 
				"												</table>\r\n" + 
				"\r\n" + 
				"</td>\r\n" + 
				"												<td style=\"padding:20px;padding-left:20px;width: 621px; height: 123px;\">\r\n" + 
				"												<table style=\"width: 595px; height: 238px; \">\r\n" + 
				"												<tbody>\r\n" + 
				"												<tr>\r\n" + 
				"												<td style=\"width: 116px; \">UIN</td>\r\n" + 
				"												<td style=\"padding-left:5px; width: 326px; border-right-style: solid; border-top-width: 1px; border-left-style: solid; border-right-width: 1px; border-bottom-width: 1px; border-bottom-style: solid; border-left-width: 1px; border-top-style: solid\">1234567890</td>\r\n" + 
				"												</tr>\r\n" + 
				"												<tr>\r\n" + 
				"												<td>&nbsp;</td>\r\n" + 
				"												<td>&nbsp;</td>\r\n" + 
				"												</tr>\r\n" + 
				"												<tr>\r\n" + 
				"												<td>name</td>\r\n" + 
				"												<td style=\"padding-left:5px; width: 326px; border-right-style: solid; border-top-width: 1px; border-left-style: solid; border-right-width: 1px; border-bottom-width: 1px; border-bottom-style: solid; border-left-width: 1px; border-top-style: solid\">Sathish Kumar</td>\r\n" + 
				"												</tr>\r\n" + 
				"												<tr>\r\n" + 
				"												<td>Date of Birth</td>\r\n" + 
				"												<td style=\"padding-left:5px; width: 326px; border-right-style: solid; border-top-width: 1px; border-left-style: solid; border-right-width: 1px; border-bottom-width: 1px; border-bottom-style: solid; border-left-width: 1px; border-top-style: solid\">15/04/1991</td>\r\n" + 
				"												</tr>\r\n" + 
				"												<tr>\r\n" + 
				"												<td>Gender</td>\r\n" + 
				"												<td style=\"padding-left:5px; width: 326px; border-right-style: solid; border-top-width: 1px; border-left-style: solid; border-right-width: 1px; border-bottom-width: 1px; border-bottom-style: solid; border-left-width: 1px; border-top-style: solid\">M</td>\r\n" + 
				"												</tr>\r\n" + 
				"												<tr>\r\n" + 
				"												<td>Phone No.</td>\r\n" + 
				"												<td style=\"padding-left:5px; width: 326px; border-right-style: solid; border-top-width: 1px; border-left-style: solid; border-right-width: 1px; border-bottom-width: 1px; border-bottom-style: solid; border-left-width: 1px; border-top-style: solid\">988287272</td>\r\n" + 
				"												</tr>\r\n" + 
				"												<tr>\r\n" + 
				"												<td>Email Id</td>\r\n" + 
				"												<td style=\"padding-left:5px; width: 326px; border-right-style: solid; border-top-width: 1px; border-left-style: solid; border-right-width: 1px; border-bottom-width: 1px; border-bottom-style: solid; border-left-width: 1px; border-top-style: solid\">sathisk@abc.com</td>\r\n" + 
				"												</tr>\r\n" + 
				"												</tbody>\r\n" + 
				"												</table>\r\n" + 
				"												</td>\r\n" + 
				"											</tr>\r\n" + 
				"										</tbody>\r\n" + 
				"									</table>\r\n" + 
				"								</td>\r\n" + 
				"							</tr>\r\n" + 
				"							<tr>\r\n" + 
				"								<td style=\"padding-top:10px\">POA</td>\r\n" + 
				"							</tr>\r\n" + 
				"							<tr style=\"height: 84px;\">\r\n" + 
				"								<td style=\"width: 646.389px; height: 84px;\">\r\n" + 
				"								<table style=\"padding:10px; border-right-style: solid; border-top-width: 1px; border-left-style: solid; border-right-width: 1px; border-bottom-width: 1px; border-bottom-style: solid; border-left-width: 1px; border-top-style: solid; \">\r\n" + 
				"								<tbody>\r\n" + 
				"								<tr style=\"width: 653px; height: 63px; \">\r\n" + 
				"								<td style=\"width: 344px; vertical-align:top\">\r\n" + 
				"								<table style=\"width: 338px; height: 85px; \">\r\n" + 
				"								<tbody>\r\n" + 
				"								<tr>\r\n" + 
				"								<td style=\"width: 104px; \">Address Line1</td>\r\n" + 
				"								<td style=\"padding-left:5px; width: 193px; border-right-style: solid; border-top-width: 1px; border-left-style: solid; border-right-width: 1px; border-bottom-width: 1px; border-bottom-style: solid; border-left-width: 1px; border-top-style: solid\">14, Madhan Street</td>\r\n" + 
				"								</tr>\r\n" + 
				"								<tr>\r\n" + 
				"								<td>Address Line2</td>\r\n" + 
				"								<td style=\"padding-left:5px; width: 193px; border-right-style: solid; border-top-width: 1px; border-left-style: solid; border-right-width: 1px; border-bottom-width: 1px; border-bottom-style: solid; border-left-width: 1px; border-top-style: solid\">Ram Avenue</td>\r\n" + 
				"								</tr>\r\n" + 
				"								<tr>\r\n" + 
				"								<td>Address Line3</td>\r\n" + 
				"								<td style=\"padding-left:5px; width: 193px; border-right-style: solid; border-top-width: 1px; border-left-style: solid; border-right-width: 1px; border-bottom-width: 1px; border-bottom-style: solid; border-left-width: 1px; border-top-style: solid\">T Nagar</td>\r\n" + 
				"								</tr>\r\n" + 
				"								</tbody>\r\n" + 
				"								</table>\r\n" + 
				"								</td>\r\n" + 
				"								<td style=\"width: 466px; height: 101px; vertical-align:top\">\r\n" + 
				"								<table style=\"width: 404px; height: 92px; \">\r\n" + 
				"								<tbody>\r\n" + 
				"								<tr>\r\n" + 
				"								<td style=\"width: 91px; \">City</td>\r\n" + 
				"								<td style=\"padding-left:5px; width: 225px; border-right-style: solid; border-top-width: 1px; border-left-style: solid; border-right-width: 1px; border-bottom-width: 1px; border-bottom-style: solid; border-left-width: 1px; border-top-style: solid\">Chennai</td>\r\n" + 
				"								</tr>\r\n" + 
				"								<tr>\r\n" + 
				"								<td>State</td>\r\n" + 
				"								<td style=\"padding-left:5px; width: 225px; border-right-style: solid; border-top-width: 1px; border-left-style: solid; border-right-width: 1px; border-bottom-width: 1px; border-bottom-style: solid; border-left-width: 1px; border-top-style: solid\">Tamil Nadu</td>\r\n" + 
				"								</tr>\r\n" + 
				"								<tr>\r\n" + 
				"								<td>Country</td>\r\n" + 
				"								<td style=\"padding-left:5px; width: 225px; border-right-style: solid; border-top-width: 1px; border-left-style: solid; border-right-width: 1px; border-bottom-width: 1px; border-bottom-style: solid; border-left-width: 1px; border-top-style: solid\">India</td>\r\n" + 
				"								</tr>\r\n" + 
				"								<tr>\r\n" + 
				"								<td>Pincode</td>\r\n" + 
				"								<td style=\"padding-left:5px; width: 225px; border-right-style: solid; border-top-width: 1px; border-left-style: solid; border-right-width: 1px; border-bottom-width: 1px; border-bottom-style: solid; border-left-width: 1px; border-top-style: solid\">600017</td>\r\n" + 
				"								</tr>\r\n" + 
				"								</tbody>\r\n" + 
				"								</table>\r\n" + 
				"								</td>\r\n" + 
				"								</tr>\r\n" + 
				"								</tbody>\r\n" + 
				"								</table>\r\n" + 
				"</td>\r\n" + 
				"							</tr>\r\n" + 
				"						</tbody>\r\n" + 
				"					</table>\r\n" + 
				"				</td>\r\n" + 
				"</tr>\r\n" + 
				"</tbody>\r\n" + 
				"</table>";

	private static final String PDF_BASE_DIR = System.getProperty("user.dir") + File.separator;

	private static final String EKYC_FULL_PRI_FILE = PDF_BASE_DIR + "ekyc-full-pri-template.pdf";

	@Autowired
	private PDFGenerator pdfGenerator;
	
	private static String resultEKYCFullPri = "<html>\r\n" + 
			"<head>\r\n" + 
			"</head>\r\n" + 
			"<body>\r\n" + 
			EKYC_SECTION_VALUE + 
			"\r\n" + 
			"</body>\r\n" + 
			"</html>";
	
	private static String resultEKYCFullSec = "<html>\r\n" + 
			"<head>\r\n" + 
			"</head>\r\n" + 
			"<body>\r\n" + 
			EKYC_SECTION_VALUE + 
			"\r\n" + 
			"</body>\r\n" + 
			"</html>";
	
	private static String resultEKYCFullPriSec = "<html>\r\n" + 
			"<head>\r\n" + 
			"</head>\r\n" + 
			"<body>\r\n" + 
			EKYC_SECTION_VALUE + 
			"\r\n" + 
			"<br>\r\n" +
			"<br>\r\n" +
			EKYC_SECTION_VALUE + 
			"\r\n" + 
			"</body>\r\n" + 
			"</html>";
	
	private static TemplateManager templateManager;
	
	@Autowired
	Environment env;

	@BeforeClass
	public static void before() {
		templateManager = new TemplateConfigureBuilder()
				.enableCache(false)
				.resourceLoader("classpath")
				.build();
	}
	
	private Map<String, Object> getTemplateValueMap() {
		Map<String, Object> valueMap = new HashMap<>();
		valueMap.put("uin_label_pri", "UIN");
		valueMap.put("uin_pri", "1234567890");
		valueMap.put("name_label_pri", "name");
		valueMap.put("name_pri", "Sathish Kumar");
		valueMap.put("dob_label_pri", "Date of Birth");
		valueMap.put("dob_pri", "15/04/1991");
		valueMap.put("gender_label_pri", "Gender");
		valueMap.put("gender_pri", "M");
		valueMap.put("phoneNumber_label_pri", "Phone No.");
		valueMap.put("phoneNumber_pri", "988287272");
		valueMap.put("emailId_label_pri", "Email Id");
		valueMap.put("emailId_pri", "sathisk@abc.com");
		
		valueMap.put("addressLine1_label_pri", "Address Line1");
		valueMap.put("addressLine1_pri", "14, Madhan Street");
		valueMap.put("addressLine2_label_pri", "Address Line2");
		valueMap.put("addressLine2_pri", "Ram Avenue");
		valueMap.put("addressLine3_label_pri", "Address Line3");
		valueMap.put("addressLine3_pri", "T Nagar");
		valueMap.put("location1_label_pri", "City");
		valueMap.put("location1_pri", "Chennai");
		valueMap.put("location2_label_pri", "State");
		valueMap.put("location2_pri", "Tamil Nadu");
		valueMap.put("location3_label_pri", "Country");
		valueMap.put("location3_pri", "India");
		valueMap.put("pinCode_label_pri", "Pincode");
		valueMap.put("pinCode_pri", "600017");
		
		
		
		
		valueMap.put("uin_label_sec", "UIN");
		valueMap.put("uin_sec", "1234567890");
		valueMap.put("name_label_sec", "name");
		valueMap.put("name_sec", "Sathish Kumar");
		valueMap.put("dob_label_sec", "Date of Birth");
		valueMap.put("dob_sec", "15/04/1991");
		valueMap.put("gender_label_sec", "Gender");
		valueMap.put("gender_sec", "M");
		valueMap.put("phoneNumber_label_sec", "Phone No.");
		valueMap.put("phoneNumber_sec", "988287272");
		valueMap.put("emailId_label_sec", "Email Id");
		valueMap.put("emailId_sec", "sathisk@abc.com");
		
		valueMap.put("addressLine1_label_sec", "Address Line1");
		valueMap.put("addressLine1_sec", "14, Madhan Street");
		valueMap.put("addressLine2_label_sec", "Address Line2");
		valueMap.put("addressLine2_sec", "Ram Avenue");
		valueMap.put("addressLine3_label_sec", "Address Line3");
		valueMap.put("addressLine3_sec", "T Nagar");
		valueMap.put("location1_label_sec", "City");
		valueMap.put("location1_sec", "Chennai");
		valueMap.put("location2_label_sec", "State");
		valueMap.put("location2_sec", "Tamil Nadu");
		valueMap.put("location3_label_sec", "Country");
		valueMap.put("location3_sec", "India");
		valueMap.put("pinCode_label_sec", "Pincode");
		valueMap.put("pinCode_sec", "600017");
		
		return valueMap;
	}

	@Ignore
	@Test
	public void testFullKYCTemplatePri() throws IdAuthenticationBusinessException, IOException {
		Map<String, Object> valueMap = getTemplateValueMap();
		
		StringWriter stringWriter = new StringWriter();
		templateManager.merge("templates/ekyc-full-pri-template.html", stringWriter, valueMap);
		
		
		assertEquals(resultEKYCFullPri,stringWriter.toString());
	}
	@Ignore
	@Test
	public void testFullKYCTemplateSec() throws IdAuthenticationBusinessException, IOException {
		Map<String, Object> valueMap = getTemplateValueMap();
		
		StringWriter stringWriter = new StringWriter();
		templateManager.merge("templates/ekyc-full-sec-template.html", stringWriter, valueMap);
		
		
		assertEquals(resultEKYCFullSec,stringWriter.toString());
	}
	@Ignore
	@Test
	public void testFullKYCTemplatePriSec() throws IdAuthenticationBusinessException, IOException {
		Map<String, Object> valueMap = getTemplateValueMap();
		
		StringWriter stringWriter = new StringWriter();
		templateManager.merge("templates/ekyc-full-pri-sec-template.html", stringWriter, valueMap);
		
		
		assertEquals(resultEKYCFullPriSec,stringWriter.toString());
	}
	
	@Ignore
	@Test
	public void testAuthSMSTemplate() throws IdAuthenticationBusinessException, IOException {
		Map<String, Object> valueMap = getTemplateValueMap();
		
		StringWriter stringWriter = new StringWriter();
		templateManager.merge("templates/auth-sms-template.txt", stringWriter, valueMap);
		
		
		assertEquals(resultEKYCFullPriSec,stringWriter.toString());
	}
	
	@Ignore
	@Test
	public void testAuthEmailSubjectTemplate() throws IdAuthenticationBusinessException, IOException {
		Map<String, Object> valueMap = getTemplateValueMap();
		
		StringWriter stringWriter = new StringWriter();
		templateManager.merge("templates/auth-email-subject-template.txt", stringWriter, valueMap);
		
		
		assertEquals(resultEKYCFullPriSec,stringWriter.toString());
	}
	
	@Ignore
	@Test
	public void testAuthEmailContentTemplate() throws IdAuthenticationBusinessException, IOException {
		Map<String, Object> valueMap = getTemplateValueMap();
		
		StringWriter stringWriter = new StringWriter();
		templateManager.merge("templates/auth-email-content-template.txt", stringWriter, valueMap);
		
		
		assertEquals(resultEKYCFullPriSec,stringWriter.toString());
	}
	
	@Ignore
	@Test
	public void testOTPSMSTemplate() throws IdAuthenticationBusinessException, IOException {
		Map<String, Object> valueMap = getTemplateValueMap();
		
		StringWriter stringWriter = new StringWriter();
		templateManager.merge("templates/otp-sms-template.txt", stringWriter, valueMap);
		
		
		assertEquals(resultEKYCFullPriSec,stringWriter.toString());
	}
	
	@Ignore
	@Test
	public void testOTPEmailSubjectTemplate() throws IdAuthenticationBusinessException, IOException {
		Map<String, Object> valueMap = getTemplateValueMap();
		
		StringWriter stringWriter = new StringWriter();
		templateManager.merge("templates/otp-email-subject-template.txt", stringWriter, valueMap);
		
		
		assertEquals(resultEKYCFullPriSec,stringWriter.toString());
	}
	
	@Ignore
	@Test
	public void testOTPEmailContentTemplate() throws IdAuthenticationBusinessException, IOException {
		Map<String, Object> valueMap = getTemplateValueMap();
		
		StringWriter stringWriter = new StringWriter();
		templateManager.merge("templates/otp-email-content-template.txt", stringWriter, valueMap);
		
		
		assertEquals(resultEKYCFullPriSec,stringWriter.toString());
	}
	
	@Test
	public void testPdfGen() throws IOException {
		Map<String, Object> valueMap = getTemplateValueMap();
		
		StringWriter stringWriter = new StringWriter();
		templateManager.merge("templates/ekyc-full-pri-sec-template.html", stringWriter, valueMap);
		
		ByteArrayOutputStream baos = (ByteArrayOutputStream)pdfGenerator.generate(stringWriter.toString());
		byte[] byteArray = baos.toByteArray();
		
		File pdfFile = new File(EKYC_FULL_PRI_FILE);
		System.out.println(pdfFile);
		
		try(FileOutputStream fileOutputStream = new FileOutputStream(pdfFile);) {
			fileOutputStream.write(byteArray);
			fileOutputStream.flush();
		}
		
		assertTrue(pdfFile.exists());
		
		
	}
	
	@AfterClass
	public static void cleanup() {
		File pdfFile = new File(EKYC_FULL_PRI_FILE);
		if(pdfFile.exists()) {
			pdfFile.delete();
		}

	}

}
