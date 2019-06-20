package io.mosip.registration.test.integrationtest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.poi.util.IOUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;

import io.mosip.kernel.core.idgenerator.spi.RidGenerator;
import io.mosip.registration.audit.AuditManagerSerivceImpl;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dao.AuditLogControlDAO;
import io.mosip.registration.dto.RegistrationCenterDetailDTO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.demographic.DemographicInfoDTO;
import io.mosip.registration.dto.demographic.DocumentDetailsDTO;
import io.mosip.registration.dto.demographic.IndividualIdentity;
import io.mosip.registration.service.config.GlobalParamService;
import io.mosip.registration.service.operator.UserOnboardService;
import io.mosip.registration.service.packet.PacketHandlerService;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AuditServiceTest extends BaseIntegrationTest{
	
	@Autowired
	private AuditManagerSerivceImpl auditServiceImpl;
	@Autowired
	private   AuditLogControlDAO repo;
	@Autowired
	private  GlobalParamService globalParamService;
	@Autowired
	UserOnboardService userOBservice;
	@Autowired
	private RidGenerator<String> ridGeneratorImpl;
	@Autowired
	PacketHandlerService packetHandlerService;
	
	@Before
	public void setGlobalConfig() {
		ApplicationContext applicationContext = ApplicationContext.getInstance();
		applicationContext.setApplicationLanguageBundle();
		applicationContext.setApplicationMessagesBundle();
		applicationContext.setLocalLanguageProperty();
		applicationContext.setLocalMessagesBundle();
		applicationContext.setApplicationMap(globalParamService.getGlobalParams());
	}
	
	@Test
	public void auditLogsDeleteTestConfiguredDaysNull() throws JsonProcessingException {
		setGlobalConfig();
		/**
		 * Create backup of value
		 */
		String value=  ApplicationContext.getInstance().map().get(RegistrationConstants.AUDIT_LOG_DELETION_CONFIGURED_DAYS).toString();		
		
		ApplicationContext.getInstance().map().put(RegistrationConstants.AUDIT_LOG_DELETION_CONFIGURED_DAYS, null);
		ResponseDTO responseDTO = auditServiceImpl.deleteAuditLogs();
		ObjectMapper mapper = new ObjectMapper();
		
		System.out.println(mapper.writer().writeValueAsString(responseDTO));
		Assert.assertEquals(io.mosip.registration.constants.RegistrationConstants.AUDIT_LOGS_DELETION_FLR_MSG, 
				responseDTO.getErrorResponseDTOs().get(0).getMessage());
		/**
		 * revert back state 
		 */
		ApplicationContext.getInstance().map().put(RegistrationConstants.AUDIT_LOG_DELETION_CONFIGURED_DAYS, value);
	}
	
	@Test
	public void testAuditLogsDelete() throws IOException {
		setGlobalConfig();
		updateDB("3");
		ResponseDTO responseDTO = auditServiceImpl.deleteAuditLogs();
		ObjectMapper mapper = new ObjectMapper();
		
		System.out.println(mapper.writer().writeValueAsString(responseDTO));
		Assert.assertEquals(io.mosip.registration.constants.RegistrationConstants.AUDIT_LOGS_DELETION_SUCESS_MSG, 
				responseDTO.getSuccessResponseDTO().getMessage());
	}
	
	@Test
	public void testAuditLogsDeleteFutureDays() throws IOException {
		setGlobalConfig();
		updateDB("-1");
		ResponseDTO responseDTO = auditServiceImpl.deleteAuditLogs();
		ObjectMapper mapper = new ObjectMapper();
		
		System.out.println(mapper.writer().writeValueAsString(responseDTO));
		Assert.assertEquals(io.mosip.registration.constants.RegistrationConstants.AUDIT_LOGS_DELETION_SUCESS_MSG, 
				responseDTO.getSuccessResponseDTO().getMessage());
	}
	
	@Test
	public void testAuditLogsDeleteCurrentDay() throws IOException {
		setGlobalConfig();
		createPacket();
		updateDB("0");
		ResponseDTO responseDTO = auditServiceImpl.deleteAuditLogs();
		ObjectMapper mapper = new ObjectMapper();
		
		System.out.println(mapper.writer().writeValueAsString(responseDTO));
		Assert.assertEquals(io.mosip.registration.constants.RegistrationConstants.AUDIT_LOGS_DELETION_SUCESS_MSG, 
				responseDTO.getSuccessResponseDTO().getMessage());
	}
	
	@Test
	public void testAuditLogsDeleteNoLogs() throws JsonProcessingException {
		updateDB("100");
		ResponseDTO responseDTO = auditServiceImpl.deleteAuditLogs();
		ObjectMapper mapper = new ObjectMapper();
		System.out.println(mapper.writer().writeValueAsString(responseDTO));
		Assert.assertEquals(io.mosip.registration.constants.RegistrationConstants.AUDIT_LOGS_DELETION_EMPTY_MSG, 
				responseDTO.getSuccessResponseDTO().getMessage());
	}
	
	@After
	public void restoreDB() {
		updateDB("3");
	}
	
	public void updateDB(String val) {
		Connection con;
		PreparedStatement pre;
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");

			con = DriverManager.getConnection("jdbc:derby:D:/Mosip_QA_080_build/mosip/registration/registration-services/reg;bootPassword=mosip12345", "", "");
			pre = con.prepareStatement("update reg.global_param set val='"+val+"' where code='AUDIT_LOG_DELETION_CONFIGURED_DAYS'");
			int count = pre.executeUpdate();

			con.commit();

			if (count == 1) {
				System.out.println("Updated AUDIT_LOG_DELETION_CONFIGURED_DAYS to "+val);
			}

			pre.close();
			con.close();
		} catch (Exception e) {
			System.out.println("Unable to update database");
		}
	}
	
	public void createPacket() throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JSR310Module());
		mapper.addMixInAnnotations(DemographicInfoDTO.class, DemographicInfoDTOMix.class);

		RegistrationDTO obj = mapper.readValue(
				new File("src/test/resources/testData/PacketHandlerServiceData/user.json"), RegistrationDTO.class);
		IndividualIdentity identity = mapper.readValue(
				new File("src/test/resources/testData/PacketHandlerServiceData/identity.json"), IndividualIdentity.class);

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
		obj.getDemographicDTO().getDemographicInfoDTO().setIdentity(identity);
		RegistrationCenterDetailDTO registrationCenter = new RegistrationCenterDetailDTO();
		registrationCenter.setRegistrationCenterId("20916");
		SessionContext.getInstance().getUserContext().setRegistrationCenterDetailDTO(registrationCenter);
		SessionContext.getInstance().getUserContext().setUserId("mosip");
		SessionContext.getInstance().setMapObject(new HashMap<String, Object>());
		String CenterID=null;
		String StatinID=null;
		Map<String,String> getres=userOBservice.getMachineCenterId();
		Set<Entry<String,String>> hashSet=getres.entrySet();
        for(Entry entry:hashSet ) {

        	if(entry.getKey().equals(IntegrationTestConstants.centerID))
        	{
        		CenterID=entry.getValue().toString();
        	}
        	else {
				StatinID=entry.getValue().toString();
			}
    
        	}
        String RandomID=ridGeneratorImpl.generateId(CenterID,StatinID);
		obj.setRegistrationId(RandomID);
		ResponseDTO response = packetHandlerService.handle(obj);

		String jsonInString = mapper.writeValueAsString(response);
		System.out.println(jsonInString);
		Assert.assertEquals(response.getSuccessResponseDTO().getCode().toString(), "0000");
		Assert.assertEquals(response.getSuccessResponseDTO().getMessage().toString(), "Success");
	}
}
