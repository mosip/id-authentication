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

import io.mosip.kernel.core.idgenerator.spi.RidGenerator;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.demographic.DemographicDTO;
import io.mosip.registration.dto.demographic.DemographicInfoDTO;
import io.mosip.registration.dto.demographic.DocumentDetailsDTO;
import io.mosip.registration.dto.demographic.IndividualIdentity;
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
	private RidGenerator<String> ridGeneratorImpl;
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
		mapper.addMixInAnnotations(DemographicInfoDTO.class, DemographicInfoDTOMix.class);
		RegistrationDTO registrationDTO = null;
		try {
			
			
			registrationDTO = mapper.readValue(new File("src/test/resources/testData/PacketHandlerServiceData/user.json"), RegistrationDTO.class);
			IndividualIdentity identity = mapper.readValue(new File("src/test/resources/testData/PacketHandlerServiceData/identity.json"), IndividualIdentity.class);
			
			byte[] data = IOUtils.toByteArray(
					new FileInputStream(new File("src/test/resources/testData/PacketHandlerServiceData/PANStubbed.jpg")));
			DocumentDetailsDTO documentDetailsDTOIdentity = new DocumentDetailsDTO();
			documentDetailsDTOIdentity.setType("POI");
			documentDetailsDTOIdentity.setFormat("format");
			documentDetailsDTOIdentity.setOwner("owner");
			documentDetailsDTOIdentity.setValue("ProofOfIdentity");

			DocumentDetailsDTO documentDetailsDTOAddress = new DocumentDetailsDTO();
			documentDetailsDTOAddress.setType("POA");
			documentDetailsDTOAddress.setFormat("format");
			documentDetailsDTOAddress.setOwner("owner");
			documentDetailsDTOAddress.setValue("ProofOfAddress");
			
			DocumentDetailsDTO documentDetailsDTORelationship = new DocumentDetailsDTO();
			documentDetailsDTORelationship.setType("POR");
			documentDetailsDTORelationship.setFormat("format");
			documentDetailsDTORelationship.setOwner("owner");
			documentDetailsDTORelationship.setValue("ProofOfRelationship");
			
			DocumentDetailsDTO documentDetailsDTODOB = new DocumentDetailsDTO();
			documentDetailsDTODOB.setType("POB");
			documentDetailsDTODOB.setFormat("format");
			documentDetailsDTODOB.setOwner("owner");
			documentDetailsDTODOB.setValue("DateOfBirthProof");

			identity.setProofOfIdentity(documentDetailsDTOIdentity);
			identity.setProofOfAddress(documentDetailsDTOAddress);
			identity.setProofOfRelationship(documentDetailsDTORelationship);
			identity.setProofOfDateOfBirth(documentDetailsDTODOB);
			
			DocumentDetailsDTO documentDetailsDTO = identity.getProofOfIdentity();
			documentDetailsDTO.setDocument(data);
			documentDetailsDTO = identity.getProofOfAddress();
			
			documentDetailsDTO.setDocument(data);
			documentDetailsDTO = identity.getProofOfRelationship();
			documentDetailsDTO.setDocument(data);
			documentDetailsDTO = identity.getProofOfDateOfBirth();
			documentDetailsDTO.setDocument(data);
			registrationDTO.getDemographicDTO().getDemographicInfoDTO().setIdentity(identity);
			registrationDTO.setRegistrationId(ridGeneratorImpl.generateId(
					ApplicationContext.getInstance().map().get(RegistrationConstants.REGISTARTION_CENTER).toString(),
					"10011"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		try {
			byte[] inMemoryZipFile = packetCreationService.create(registrationDTO);
			String filePath = storageService.storeToDisk(registrationDTO.getRegistrationId(), inMemoryZipFile);
			assertEquals(ApplicationContext.getInstance().getApplicationMap().get(RegistrationConstants.PACKET_STORE_LOCATION) + seperator
					+ formatDate(new Date(), environment.getProperty(RegistrationConstants.PACKET_STORE_DATE_FORMAT))
							.concat(seperator).concat(registrationDTO.getRegistrationId()),
					filePath);
		} catch (RegBaseCheckedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
