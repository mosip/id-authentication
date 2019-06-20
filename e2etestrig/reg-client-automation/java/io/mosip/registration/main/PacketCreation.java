package io.mosip.registration.main;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import io.mosip.registration.config.AppConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dto.RegistrationDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.service.config.GlobalParamService;
import io.mosip.registration.service.operator.UserOnboardService;
import io.mosip.registration.service.packet.PacketHandlerService;
import io.mosip.registration.util.CommonUtil;
import io.mosip.registration.util.ConstantValues;

@ContextConfiguration(classes = { AppConfig.class })
public class PacketCreation extends AbstractTestNGSpringContextTests {

	@Autowired
	PacketHandlerService packetHandlerService;

	@Autowired
	UserOnboardService userOBservice;
	@Autowired
	CommonUtil commonUtil;
	/**
	 * Declaring CenterID,StationID global
	 */
	private static String centerID = null;
	private static String stationID = null;

	@Test
	public void createPacket() throws FileNotFoundException, IOException, ParseException {
		// Pre SetUp to create Packet
		ApplicationContext.map().put(RegistrationConstants.GPS_DEVICE_DISABLE_FLAG, "true");
		SessionContext.getInstance().getMapObject();
		SessionContext.map().put(RegistrationConstants.IS_Child, true);
		centerID = userOBservice.getMachineCenterId().get(ConstantValues.CENTERIDLBL);
		stationID = userOBservice.getMachineCenterId().get(ConstantValues.STATIONIDLBL);
		// creating RegistrationDTO
		commonUtil.createRegistrationDTOObject("New", centerID, stationID);
		// Fetch value from PreId.json
		Object obj = new JSONParser().parse(new FileReader("src/main/resources/PreId.json"));
		JSONObject jo = (JSONObject) obj;
		String adultID = (String) jo.get("PrIdOfAdultWithDocs");
		// Get Pre-RegistrationDTO
		RegistrationDTO preRegistrationDTO = commonUtil.getPreRegistrationDetails(adultID);
		// Set RegistrationDTO to create packet
		commonUtil.setRegistrationClientRegDTO(preRegistrationDTO);
		// create packet using RegistrationClient
		ResponseDTO response = packetHandlerService.handle(commonUtil.setRegistrationClientRegDTO(preRegistrationDTO));
		logger.info(response.getSuccessResponseDTO().getMessage());

	}

}
