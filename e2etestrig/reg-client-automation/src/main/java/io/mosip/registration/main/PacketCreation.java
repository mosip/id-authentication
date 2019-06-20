package io.mosip.registration.main;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dto.RegistrationCenterDetailDTO;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.service.config.GlobalParamService;
import io.mosip.registration.service.operator.UserDetailService;
import io.mosip.registration.service.operator.UserOnboardService;
import io.mosip.registration.service.operator.UserSaltDetailsService;
import io.mosip.registration.service.packet.PacketHandlerService;
import io.mosip.registration.service.sync.MasterSyncService;
import io.mosip.registration.service.sync.PolicySyncService;
import io.mosip.registration.service.sync.impl.PublicKeySyncImpl;
import io.mosip.registration.util.CommonUtil;
import io.mosip.registration.util.ConstantValues;

@PropertySource("/RegClient/config.properties")
@ContextConfiguration(classes = { AppConfig.class })
public class PacketCreation extends AbstractTestNGSpringContextTests {

	@Autowired
	PacketHandlerService packetHandlerService;
	@Autowired
	Environment env;
	@Autowired
	UserOnboardService userOBservice;
	@Autowired
	CommonUtil commonUtil;
	@Autowired
	PublicKeySyncImpl publicKeySyncImpl;
	@Autowired
	GlobalParamService globalParamService;
	@Autowired
	UserDetailService userDetailService;
	@Autowired
	MasterSyncService masterSyncService;
	@Autowired
	UserSaltDetailsService userSaltDetailsService;
	@Autowired
	PolicySyncService policySyncService;
	/**
	 * Declaring CenterID,StationID global
	 */
	private static String centerID = null;
	private static String stationID = null;

	@Test
	public void createPacket() throws FileNotFoundException, IOException, ParseException {
		// Fetch value from PreId.json
		HashMap<String, String> preRegIDs = commonUtil.getPreRegIDs();
		// Sync
		/*
		 * PUBLIC KEY ssync globalparam mastersyn userdetai user salt
		 */
		publicKeySyncImpl
				.getPublicKey(RegistrationConstants.JOB_TRIGGER_POINT_USER);
		
		ResponseDTO responseDTO = globalParamService.synchConfigData(false);
		ResponseDTO masterResponseDTO = masterSyncService.getMasterSync(RegistrationConstants.OPT_TO_REG_MDS_J00001,
				RegistrationConstants.JOB_TRIGGER_POINT_USER);
		ResponseDTO userResponseDTO = userDetailService.save(RegistrationConstants.JOB_TRIGGER_POINT_USER);

		ResponseDTO userSaltResponse = userSaltDetailsService
				.getUserSaltDetails(RegistrationConstants.JOB_TRIGGER_POINT_USER);
		policySyncService.fetchPolicy();
		// Common PreRequisite SetUp to create Packet
		ApplicationContext.map().put(RegistrationConstants.GPS_DEVICE_DISABLE_FLAG, ConstantValues.NO);
		SessionContext.getInstance().getMapObject().put(RegistrationConstants.IS_Child, true);

		centerID = userOBservice.getMachineCenterId().get(ConstantValues.CENTERIDLBL);
		stationID = userOBservice.getMachineCenterId().get(ConstantValues.STATIONIDLBL);
		SessionContext.getInstance().getUserContext().setUserId(ConstantValues.USERID);
		RegistrationCenterDetailDTO registrationCenter = new RegistrationCenterDetailDTO();
		registrationCenter.setRegistrationCenterId(centerID);
		SessionContext.getInstance().getUserContext().setRegistrationCenterDetailDTO(registrationCenter);
		ArrayList<String> roles = new ArrayList<>();
		for (String role : ConstantValues.ROLES.split(","))
			roles.add(role);
		SessionContext.userContext().setRoles(roles);
		ApplicationContext.map().put(RegistrationConstants.PACKET_STORE_LOCATION, "src/main/resources/packets");
		ApplicationContext.map().put(RegistrationConstants.EOD_PROCESS_CONFIG_FLAG, "Y");

		if (preRegIDs.get("RegClientPacketUniqueCBEFF").equalsIgnoreCase("YES")) {
			// Set CBEFF to UNIQUE
			ApplicationContext.map().put(RegistrationConstants.CBEFF_UNQ_TAG, ConstantValues.YES);
		} else {
			// Set CBEFF to UNIQUE & DUPLICATE
			ApplicationContext.map().put(RegistrationConstants.CBEFF_UNQ_TAG, ConstantValues.NO);
		}

		for (Entry<String, String> entry : preRegIDs.entrySet()) {
			System.out.println(entry.getKey());
			RegistrationDTO preRegistrationDTO = null;
			if (!(entry.getKey().equalsIgnoreCase("RegClientPacketUniqueCBEFF"))) {
				// creating RegistrationDTO
				commonUtil.createRegistrationDTOObject(ConstantValues.REGISTRATIONCATEGORY, centerID, stationID);
				// Create packet
				preRegistrationDTO = commonUtil.getPreRegistrationDetails(entry.getValue());
			}

			commonUtil.packetCreation(preRegistrationDTO, "APPROVED",
					"src/main/resources/RegClient/Resident_BiometricData.json",
					"src/main/resources/RegClient/Resident_demographicData.json",
					"src/main/resources/RegClient/proofOfAddress.jpg", ConstantValues.USERID, centerID, stationID,
					entry.getKey());
		}

	}

}
