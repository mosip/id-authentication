package io.mosip.registration.test.integrationtest;

import static io.mosip.registration.constants.RegistrationConstants.ACKNOWLEDGEMENT_TEMPLATE_PART_1;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.entity.Template;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.repositories.TemplateRepository;
import io.mosip.registration.service.template.impl.TemplateServiceImpl;

/**
 * This class contains the unit test cases for testing the methods of
 * TemplateService
 *
 * @author Priya Soni
 * @author Akshay Jain
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
public class TemplateServiceImplTest {

	@Autowired
	private TemplateServiceImpl templateServiceImpl;

	@Autowired
	private TemplateRepository<Template> templateRepo;

	@Before
	public void setup() {
		ApplicationContext context = ApplicationContext.getInstance();
		context.getApplicationLanguageBundle();
		context.getApplicationLanguagevalidationBundle();
		context.getApplicationMap();
		context.getApplicationMessagesBundle();

	}

	/**
	 * This method checks whether getTemplate method returns the Template with
	 * respect to the required input
	 * 
	 */
	@Test
	public void getTemplateValidTest() {
		Template result = templateServiceImpl.getTemplate(RegistrationConstants.EMAIL_TEMPLATE, "eng");
		System.out.println(result.getFileTxt());
		assertEquals(result.getDescr(), "Email and SMS Notification Template");
	}

	/**
	 * This method verifies that getTemplate method doen't return any output for
	 * invalid input
	 * 
	 */
	@Test
	public void getTemplateInvalidTest() {
		Template result = templateServiceImpl.getTemplate("Invalid Template", "eng");
		assertNull(result.getDescr());
	}

	/**
	 * This method verifies that getTemplate method doesn't return invalid output
	 * for a valid input
	 * 
	 */
	@Test
	public void getTemplateNegativeTest() {
		Template result = templateServiceImpl.getTemplate(RegistrationConstants.ACKNOWLEDGEMENT_TEMPLATE_PART_1, "eng");
		System.out.println(result.getFileTxt());
		assertNotEquals(result.getDescr(), "Email and SMS Notification Template");

	}

	/**
	 * This method verifies that getHTMLTemplate method returns correct file text
	 * for the required input
	 * 
	 *
	 */
	@Test
	public void getHTMLTemplateTest() throws RegBaseCheckedException {
		String dataActual = templateServiceImpl.getHtmlTemplate(RegistrationConstants.EMAIL_TEMPLATE, "eng");
		List<Template> list = templateRepo.findAll(Template.class);
		for (Template template : list) {
			if (template.getDescr().compareTo("Email and SMS Notification Template") == 0) {
				assertEquals(dataActual, new String(template.getFileTxt()));
			}
		}

	}

	/**
	 * This method checks that getHTMLTemplate method returns empty string for an
	 * invalid input
	 * 
	 */
	@Test
	public void getHTMLTemplateInvalidInputTest() throws RegBaseCheckedException {
		String dataActual = templateServiceImpl.getHtmlTemplate("invalid input", "");
		assertEquals(dataActual, "");
	}

	/**
	 * 
	 * This method verifies that getHTMLTemplate shouldn't return a valid output for
	 * null input
	 * 
	 */
	@Ignore
	@Test
	public void getHTMLTemplateNullInputTest() throws RegBaseCheckedException {
		String dataActual = templateServiceImpl.getHtmlTemplate(null, null);
		assertNull(dataActual);
	}

	/**
	 * This method verifies that getHTMLTemplate shouldn't return a valid output for
	 * empty string as input
	 * 
	 */
	@Ignore
	@Test
	public void getHTMLTemplateEmptyInputTest() throws RegBaseCheckedException {
		String dataActual = templateServiceImpl.getHtmlTemplate("", "");
		assertNull(dataActual, "");
	}

}
