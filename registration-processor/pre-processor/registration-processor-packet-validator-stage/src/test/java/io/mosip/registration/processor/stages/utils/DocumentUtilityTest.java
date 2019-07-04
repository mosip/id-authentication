package io.mosip.registration.processor.stages.utils;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.mosip.registration.processor.core.packet.dto.demographicinfo.identify.Identity;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.identify.IdentityJsonValues;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.identify.RegistrationProcessorIdentity;
import io.mosip.registration.processor.core.packet.dto.idjson.Document;
import io.mosip.registration.processor.core.spi.filesystem.manager.PacketManager;
import io.mosip.registration.processor.packet.storage.utils.Utilities;

/**
 * The Class DocumentUtilityTest.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ IOUtils.class, Utilities.class })
public class DocumentUtilityTest {

	/** The reg processor identity json. */
	@Mock
	private RegistrationProcessorIdentity regProcessorIdentityJson;

	/** The input stream. */
	@Mock
	private InputStream inputStream;

	/** The filesystem adapter impl. */
	@Mock
	private PacketManager filesystemAdapterImpl;

	/** The utility. */
	@Mock
	private Utilities utility;

	/** The document utility. */
	@InjectMocks
	DocumentUtility documentUtility = new DocumentUtility();

	/** The map identity json string to object. */
	@Mock
	ObjectMapper mapIdentityJsonStringToObject;

	/** The identitydemoinfo. */
	Identity identitydemoinfo = new Identity();

	/** The Constant CONFIG_SERVER_URL. */
	private static final String CONFIG_SERVER_URL = "url";

	private String identityMappingjsonString;

	/**
	 * Test structural validation success.
	 *
	 * @throws Exception
	 *             the exception
	 */
	@Test
	public void testDocumentUtility() throws Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		IdentityJsonValues name = new IdentityJsonValues();
		IdentityJsonValues gender = new IdentityJsonValues();
		IdentityJsonValues dob = new IdentityJsonValues();
		IdentityJsonValues pheoniticName = new IdentityJsonValues();
		IdentityJsonValues poi = new IdentityJsonValues();
		IdentityJsonValues poa = new IdentityJsonValues();
		IdentityJsonValues por = new IdentityJsonValues();
		IdentityJsonValues pob = new IdentityJsonValues();

		name.setValue("name1");
		name.setWeight(4);
		gender.setValue("male");
		dob.setValue("dob");
		pheoniticName.setValue("pheoniticName");
		poi.setValue("identity");
		poi.setWeight(20);

		poa.setValue("addredd");
		poa.setWeight(30);
		pob.setValue("birth");
		pob.setWeight(30);
		por.setValue("relation");
		por.setWeight(30);

		identitydemoinfo.setPoi(poi);
		identitydemoinfo.setPoa(poa);
		identitydemoinfo.setPor(por);
		identitydemoinfo.setPob(pob);
		identitydemoinfo.setName(name);
		identitydemoinfo.setDob(dob);
		identitydemoinfo.setGender(gender);
		identitydemoinfo.setPheoniticName(pheoniticName);

		regProcessorIdentityJson.setIdentity(identitydemoinfo);
		FileInputStream fstream = new FileInputStream("src/test/resources/ID.json");
		byte[] bytes = IOUtils.toByteArray(fstream);
		Mockito.when(filesystemAdapterImpl.getFile(anyString(), anyString())).thenReturn(inputStream);

		File identityMappingjson = new File(classLoader.getResource("RegistrationProcessorIdentity.json").getFile());
		InputStream identityMappingjsonStream = new FileInputStream(identityMappingjson);

		try {
			identityMappingjsonString = IOUtils.toString(identityMappingjsonStream, StandardCharsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		PowerMockito.mockStatic(Utilities.class);
		PowerMockito.when(Utilities.class, "getJson", CONFIG_SERVER_URL, "RegistrationProcessorIdentity.json")
				.thenReturn(identityMappingjsonString);
		Mockito.when(filesystemAdapterImpl.getFile(anyString(), anyString())).thenReturn(inputStream);
		Mockito.when(utility.getConfigServerFileStorageURL()).thenReturn(CONFIG_SERVER_URL);
		Mockito.when(utility.getGetRegProcessorDemographicIdentity()).thenReturn("identity");
		Mockito.when(utility.getGetRegProcessorIdentityJson()).thenReturn("RegistrationProcessorIdentity.json");
		Mockito.when(regProcessorIdentityJson.getIdentity()).thenReturn(identitydemoinfo);
		Mockito.when(mapIdentityJsonStringToObject.readValue(anyString(), Mockito.any(Class.class)))
				.thenReturn(regProcessorIdentityJson);

		String docCat = null;
		List<Document> documentList = documentUtility.getDocumentList(bytes);
		for (Document docObjects : documentList) {
			if (docObjects.getDocumentCategory() != null)
				docCat = docObjects.getDocumentCategory();
			break;
		}
		assertEquals("Comparing the first Document Category", "proofOfAddress", docCat);
	}

}
