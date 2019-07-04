package io.mosip.registration.cipher;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import io.mosip.registration.config.SoftwareInstallationHandler;

@Ignore
@RunWith(PowerMockRunner.class)
@PrepareForTest({ SoftwareInstallationHandler.class })
public class RegistrationAppTest {

	@InjectMocks
	private SoftwareInstallationHandler registrationApp;

	@Rule
	public MockitoRule mockitoRule = MockitoJUnit.rule();

	File mockFile;

	Manifest localmanifest;

	@Mock
	Manifest servermanifest;

	FileInputStream fileInputStream;

	@Mock
	FileInputStream urlInputStream;

	private URL url;

	/*
	 * @Test public void hasUpdateTrueTest() throws Exception {
	 * 
	 * mockFile = PowerMockito.mock(File.class);
	 * 
	 * PowerMockito.whenNew(File.class).withAnyArguments().thenReturn(mockFile);
	 * Mockito.when(mockFile.getParentFile()).thenReturn(mockFile);
	 * 
	 * Mockito.when(mockFile.exists()).thenReturn(true);
	 * 
	 * fileInputStream = PowerMockito.mock(FileInputStream.class);
	 * PowerMockito.whenNew(FileInputStream.class).withAnyArguments().thenReturn(
	 * fileInputStream);
	 * 
	 * url = PowerMockito.mock(URL.class);
	 * PowerMockito.whenNew(URL.class).withAnyArguments().thenReturn(url);
	 * Mockito.when(url.openStream()).thenReturn(urlInputStream);
	 * 
	 * localmanifest = PowerMockito.mock(Manifest.class);
	 * PowerMockito.whenNew(Manifest.class).withArguments(fileInputStream).
	 * thenReturn(localmanifest);
	 * 
	 * PowerMockito.whenNew(Manifest.class).withArguments(urlInputStream).thenReturn
	 * (servermanifest);
	 * 
	 * Map<String, Attributes> localAttributes = new HashMap<>(); Map<String,
	 * Attributes> serverAttributes = new HashMap<>();
	 * 
	 * Attributes attributes = new Attributes();
	 * attributes.put(Attributes.Name.CONTENT_TYPE, "hashValueForFirst");
	 * localAttributes.put("firstJar", attributes);
	 * 
	 * attributes.put(Attributes.Name.CONTENT_TYPE, "hashValueForSecond");
	 * localAttributes.put("secondJar", attributes);
	 * 
	 * serverAttributes.put("secondJar", attributes);
	 * attributes.put(Attributes.Name.CONTENT_TYPE, "hashValueForThird");
	 * serverAttributes.put("thirdJar", attributes);
	 * 
	 * 
	 * Mockito.when(localmanifest.getEntries()).thenReturn(localAttributes);
	 * Mockito.when(servermanifest.getEntries()).thenReturn(serverAttributes);
	 * 
	 * Assert.assertTrue(registrationApp.hasUpdate());
	 * 
	 * }
	 */
	
/*	@Test
	public void hasUpdateFalseTest() throws Exception {
		mockFile = PowerMockito.mock(File.class);

		PowerMockito.whenNew(File.class).withAnyArguments().thenReturn(mockFile);
		Mockito.when(mockFile.getParentFile()).thenReturn(mockFile);
		
		Mockito.when(mockFile.exists()).thenReturn(false);
		
		Assert.assertFalse(registrationApp.hasUpdate());

	}*/


}
