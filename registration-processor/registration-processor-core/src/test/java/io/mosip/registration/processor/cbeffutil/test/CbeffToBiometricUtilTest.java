package io.mosip.registration.processor.cbeffutil.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;

import io.mosip.kernel.core.cbeffutil.jaxbclasses.BDBInfoType;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.BIRType;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.SingleType;
import io.mosip.kernel.core.cbeffutil.spi.CbeffUtil;
import io.mosip.registration.processor.core.util.CbeffToBiometricUtil;

/**
 * The Class CbeffToBiometricUtilTest.
 * 
 * @author M1048358 Alok
 */
@RunWith(SpringRunner.class)
public class CbeffToBiometricUtilTest {
	
	/** The util. */
	@InjectMocks
	private CbeffToBiometricUtil util;
	
	/** The cbeff util. */
	@Mock
	private CbeffUtil cbeffUtil;
	
	/**
	 * Setup.
	 *
	 * @throws Exception the exception
	 */
	@Before
	public void setup() throws Exception {
		byte[] bioBytes = "individual biometric value".getBytes();
		List<SingleType> singleList = new ArrayList<>();
		singleList.add(SingleType.FACE);
		List<String> subtypeList = new ArrayList<>();
		BIRType type = new BIRType();
		type.setBDB(bioBytes);
		BDBInfoType bdbinfotype = new BDBInfoType();
		bdbinfotype.setType(singleList);
		bdbinfotype.setSubtype(subtypeList);
		type.setBDBInfo(bdbinfotype);
		List<BIRType> birtypeList = new ArrayList<>();
		birtypeList.add(type);
		Mockito.when(cbeffUtil.getBIRDataFromXML(any())).thenReturn(birtypeList);
	}
	
	/**
	 * Test image bytes success.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void testImageBytesSuccess() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		File cbeffFile = new File(classLoader.getResource("TestCbeff.xml").getFile());
		InputStream inputStream = new FileInputStream(cbeffFile);
		String cbeff = IOUtils.toString(inputStream, "UTF-8");
		List<String> subtype = new ArrayList<>();
		byte[] photo = util.getImageBytes(cbeff, "FACE", subtype);
	
		assertTrue(photo != null);
	}
	
	/**
	 * Test image bytes failure.
	 *
	 * @throws Exception the exception
	 */
	@Test
	public void testImageBytesFailure() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		File cbeffFile = new File(classLoader.getResource("TestCbeff.xml").getFile());
		InputStream inputStream = new FileInputStream(cbeffFile);
		String cbeff = IOUtils.toString(inputStream, "UTF-8");
		List<String> subtype = new ArrayList<>();
		byte[] photo = util.getImageBytes(cbeff, "", subtype);
		
		assertFalse(photo != null);
	}
	
}
