package com.test.demo;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.WebApplicationContext;

import io.mosip.demo.authentication.service.impl.indauth.controller.FingerPrint;


/**
 * @author Arun Bose S
 * The Class FingerPrintTest.
 */
@RunWith(SpringRunner.class)
@WebMvcTest
@ContextConfiguration(classes = { TestContext.class, WebApplicationContext.class })
public class FingerPrintTest {
	
/** The finger print mock. */
@InjectMocks
private FingerPrint fingerPrintMock;

//private FingerPrintD

/**
 * Finger print test.
 *
 * @throws KeyManagementException the key management exception
 * @throws RestClientException the rest client exception
 * @throws NoSuchAlgorithmException the no such algorithm exception
 * @throws JSONException the JSON exception
 */
@Test
 public void fingerPrintTest() throws KeyManagementException, RestClientException, NoSuchAlgorithmException, JSONException {
	//Mockito.mock(classToMock)
	fingerPrintMock.fingerprint();
}


}
