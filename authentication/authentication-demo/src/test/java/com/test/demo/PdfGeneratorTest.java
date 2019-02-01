package com.test.demo;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.demo.authentication.service.impl.indauth.controller.PDFGenerator;


/**
 * @author Arun Bose S
 * The Class PdfGeneratorTest.
 */
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class PdfGeneratorTest {
	
	/** The pdf generator mock. */
	@InjectMocks
	private PDFGenerator pdfGeneratorMock;
	
	/**
	 * Pdf generator test.
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Test
	public void pdfGeneratorTest() throws IOException {
		pdfGeneratorMock.decode("sampleData");
	}

}
