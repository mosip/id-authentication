package com.test.demo;
import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.demo.authentication.service.impl.indauth.controller.Encode;

// TODO: Auto-generated Javadoc
/**
 * @author Arun Bose S
 * The Class EncodeTest.
 */
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class EncodeTest {
	
/** The encode mock. */
@InjectMocks
private Encode encodeMock;
	
/**
 * Encode test.
 */
@Test
public void encodeTest() {
	encodeMock.encode("SampleData");
	}

}
