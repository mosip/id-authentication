package io.mosip.admin.integration;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import io.mosip.admin.TestBootApplication;
import io.mosip.admin.configvalidator.ProcessFlowConfigValidator;
import io.mosip.admin.configvalidator.exception.ConfigValidationException;
import io.mosip.admin.configvalidator.exception.PropertyNotFoundException;

/**
 * @author Sidhant Agarwal
 * @since 1.0.0
 *
 */
//@SpringBootTest(classes = TestBootApplication.class)
//@RunWith(SpringRunner.class)
public class ConfigValidatorTest {

	@Autowired
	ProcessFlowConfigValidator processFVal;

	@Autowired
	RestTemplate restTemplate;

	//@Test
	public void testValidateConfig() throws IOException {
		String regResponse = "mosip.registration.document_disable_flag=y";
		String regProcResponse = "registration.processor.validateApplicantDocument=false";

		MockRestServiceServer res = MockRestServiceServer.bindTo(restTemplate).build();
		res.expect(requestTo("http://104.211.212.28:51000/registration/dev/0.12.0/registration-dev.properties"))
				.andRespond(withSuccess().body(regResponse));
		res.expect(requestTo(
				"http://104.211.212.28:51000/registration-processor/dev/0.12.0/registration-processor-dev.properties"))
				.andRespond(withSuccess().body(regProcResponse));

		boolean resu = processFVal.validateDocumentProcess();
		assertTrue(resu);

	}

	//@Test
	public void testValidateConfigNegativeTest() throws IOException {
		String regResponse = "mosip.registration.document_disable_flag=n";
		String regProcResponse = "registration.processor.validateApplicantDocument=true";

		MockRestServiceServer res = MockRestServiceServer.bindTo(restTemplate).build();
		res.expect(requestTo("http://104.211.212.28:51000/registration/dev/0.12.0/registration-dev.properties"))
				.andRespond(withSuccess().body(regResponse));
		res.expect(requestTo(
				"http://104.211.212.28:51000/registration-processor/dev/0.12.0/registration-processor-dev.properties"))
				.andRespond(withSuccess().body(regProcResponse));

		boolean resu = processFVal.validateDocumentProcess();
		assertTrue(resu);

	}

	//@Test(expected = PropertyNotFoundException.class)
	public void testValidateConfigExceptionTest() throws IOException {
		String regResponse = "";

		MockRestServiceServer res = MockRestServiceServer.bindTo(restTemplate).build();
		res.expect(requestTo("http://104.211.212.28:51000/registration/dev/0.12.0/registration-dev.properties"))
				.andRespond(withServerError());

		processFVal.validateDocumentProcess();

	}

	//@Test(expected = PropertyNotFoundException.class)
	public void testValidateConfigExceptionNotFoundTest() throws IOException {
		String regResponse = "mosip.registration.document_disable_flag=y";
		String regProcResponse = "";

		MockRestServiceServer res = MockRestServiceServer.bindTo(restTemplate).build();
		res.expect(requestTo("http://104.211.212.28:51000/registration/dev/0.12.0/registration-dev.properties"))
				.andRespond(withSuccess().body(regResponse));
		res.expect(requestTo(
				"http://104.211.212.28:51000/registration-processor/dev/0.12.0/registration-processor-dev.properties"))
				.andRespond(withServerError());

		processFVal.validateDocumentProcess();

	}

	//@Test(expected = ConfigValidationException.class)
	public void testValidateConfigMismatchTest() throws IOException {
		String regResponse = "mosip.registration.document_disable_flag=n";

		MockRestServiceServer res = MockRestServiceServer.bindTo(restTemplate).build();
		res.expect(requestTo("http://104.211.212.28:51000/registration/dev/0.12.0/registration-dev.properties"))
				.andRespond(withSuccess().body(regResponse));
		res.expect(requestTo(
				"http://104.211.212.28:51000/registration-processor/dev/0.12.0/registration-processor-dev.properties"))
				.andRespond(withSuccess().body(regResponse));

		processFVal.validateDocumentProcess();

	}

}
