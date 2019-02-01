package com.test.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.demo.authentication.service.impl.indauth.controller.Decode;

/**
 * 
 * @author Arun Bose S
 * The Class DecodeTest.
 */
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class DecodeTest {

	/** The decode mock. */
	@InjectMocks
	private Decode decodeMock;
	
	/**
	 * Decode test.
	 */
	@Test
	public void decodeTest() {
		decodeMock.decode("sampleData");
	}
}
