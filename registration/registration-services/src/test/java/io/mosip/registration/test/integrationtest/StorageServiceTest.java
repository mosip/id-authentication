package io.mosip.registration.test.integrationtest;

import static io.mosip.kernel.core.util.DateUtils.formatDate;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import org.apache.poi.util.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;

import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.demographic.DemographicDTO;
import io.mosip.registration.dto.demographic.DocumentDetailsDTO;
import io.mosip.registration.exception.RegBaseCheckedException;
import io.mosip.registration.service.config.GlobalParamService;
import io.mosip.registration.service.external.StorageService;
import io.mosip.registration.service.packet.PacketCreationService;


public class StorageServiceTest extends BaseIntegrationTest{
	@Autowired
	PacketCreationService packetCreationService;
	@Autowired
	StorageService storageService;

	@Autowired
	private Environment environment;

	@Autowired
	private GlobalParamService globalParamService;

	@Before
	public void setUp() {
		ApplicationContext applicationContext = ApplicationContext.getInstance();
		applicationContext.setApplicationLanguageBundle();
		applicationContext.setApplicationMessagesBundle();
		applicationContext.setLocalLanguageProperty();
		applicationContext.setLocalMessagesBundle();
		applicationContext.setApplicationMap(globalParamService.getGlobalParams());

	}

	@Test
	public void testStorageToDisk() {
		SessionContext.getInstance().setMapObject(new HashMap<String, Object>());
		String seperator = "/";
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JSR310Module());
		RegistrationDTO registrationDTO = null;
		try {
			registrationDTO = mapper.readValue(new File("src/test/resources/testData/StorageServiceData/user.json"),
					RegistrationDTO.class);
			byte[] data = IOUtils.toByteArray(new FileInputStream(new File("PANStubbed.jpg")));
			DemographicDTO documentDetails = registrationDTO.getDemographicDTO();
			DocumentDetailsDTO documentDetailsDTO = documentDetails.getDemographicInfoDTO().getIdentity()
					.getProofOfIdentity();
			documentDetailsDTO.setDocument(data);
			documentDetailsDTO = documentDetails.getDemographicInfoDTO().getIdentity().getProofOfAddress();
			documentDetailsDTO.setDocument(data);
			documentDetailsDTO = documentDetails.getDemographicInfoDTO().getIdentity().getProofOfRelationship();
			documentDetailsDTO.setDocument(data);
			documentDetailsDTO = documentDetails.getDemographicInfoDTO().getIdentity().getProofOfDateOfBirth();
			documentDetailsDTO.setDocument(data);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			byte[] inMemoryZipFile = packetCreationService.create(registrationDTO);
			String filePath = storageService.storeToDisk(registrationDTO.getRegistrationId(), inMemoryZipFile);
			assertEquals(environment.getProperty(RegistrationConstants.PACKET_STORE_LOCATION) + seperator
					+ formatDate(new Date(), environment.getProperty(RegistrationConstants.PACKET_STORE_DATE_FORMAT))
							.concat(seperator).concat(registrationDTO.getRegistrationId()),
					filePath);
		} catch (RegBaseCheckedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
