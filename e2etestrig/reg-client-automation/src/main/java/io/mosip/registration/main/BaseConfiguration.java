package io.mosip.registration.main;

import java.io.IOException;
import java.util.HashMap;

import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.IHookable;
import io.mosip.kernel.core.exception.ExceptionUtils;
import io.mosip.kernel.core.logger.spi.Logger;
import io.mosip.registration.config.AppConfig;
import io.mosip.registration.config.DaoConfig;
import io.mosip.registration.constants.RegistrationConstants;
import io.mosip.registration.context.ApplicationContext;
import io.mosip.registration.context.SessionContext;
import io.mosip.registration.dto.AuthenticationValidatorDTO;
import io.mosip.registration.dto.LoginUserDTO;
import io.mosip.registration.dto.RegistrationCenterDetailDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.UserDTO;
import io.mosip.registration.dto.biometric.BiometricDTO;
import io.mosip.registration.repositories.CenterMachineRepository;
import io.mosip.registration.repositories.MachineMasterRepository;
import io.mosip.registration.repositories.RegistrationCenterUserRepository;
import io.mosip.registration.repositories.UserMachineMappingRepository;
import io.mosip.registration.service.config.GlobalParamService;
import io.mosip.registration.service.login.LoginService;
import io.mosip.registration.service.operator.UserDetailService;
import io.mosip.registration.service.operator.UserOnboardService;
import io.mosip.registration.service.operator.UserSaltDetailsService;
import io.mosip.registration.service.packet.PacketHandlerService;
import io.mosip.registration.service.sync.MasterSyncService;
import io.mosip.registration.service.sync.PolicySyncService;
import io.mosip.registration.service.sync.impl.PublicKeySyncImpl;
import io.mosip.registration.util.CommonUtil;
import io.mosip.registration.util.ConstantValues;
@ContextConfiguration(classes = { AppConfig.class, DaoConfig.class })
public class BaseConfiguration extends AbstractTestNGSpringContextTests {

	@Autowired
	LoginService loginService;
	@Autowired
	PacketHandlerService packetHandlerService;
	@Autowired
	private GlobalParamService globalParamService;
	@Autowired
	UserOnboardService userOBservice;
	@Autowired
	CommonUtil commonUtil;
	@Autowired
	PublicKeySyncImpl publicKeySyncImpl;
	@Autowired
	MasterSyncService masterSyncService;
	@Autowired
	UserDetailService userDetailService;
	@Autowired
	UserSaltDetailsService userSaltDetailsService;
	@Autowired
	PolicySyncService policySyncService;
	@Autowired
	MachineMasterRepository machineMasterRepository;
	@Autowired
	CenterMachineRepository centerMachineRepository;
	@Autowired
	RegistrationCenterUserRepository centeruserRepo;

	@Autowired
	UserMachineMappingRepository userMachineMappingRepository;
	/**
	 * Declaring CenterID,StationID global
	 */
	private static String centerID = null;
	private static String stationID = null;
	HashMap<String, String> data = new HashMap<>();
	/**
	 * Instance of {@link Logger}
	 */
	private static final Logger LOGGER = AppConfig.getLogger(BaseConfiguration.class);

	// creating the instance of mosip ApplicationContext
	static {
		ApplicationContext applicationContext = ApplicationContext.getInstance();
		applicationContext.loadResourceBundle();
	}

	public void baseSetUp() {
		try {
			// Fetching the Global param values from the database
			ApplicationContext.setApplicationMap(globalParamService.getGlobalParams());
			// Set spring Application Context to SessionContext
			SessionContext.setApplicationContext(applicationContext);
			// Sync
			boolean sync_Status;
			ResponseDTO publicKeyResponse = publicKeySyncImpl
					.getPublicKey(RegistrationConstants.JOB_TRIGGER_POINT_USER);
			sync_Status = commonUtil.verifyAssertionResponseMessage("SYNC_SUCCESS",
					publicKeyResponse.getSuccessResponseDTO().getMessage());
			if (sync_Status) {
				LOGGER.info("BASE CONFIGURATION - ", "AUTOMATION - REG", "SYNC",
						"publicKeySyncImpl Synced successfully");

				ResponseDTO globalParamResponse = globalParamService.synchConfigData(false);
				sync_Status = commonUtil.verifyAssertionResponseMessage("SYNC_SUCCESS",
						globalParamResponse.getSuccessResponseDTO().getMessage());

				if (sync_Status) {
					LOGGER.info("BASE CONFIGURATION - ", "AUTOMATION - REG", "SYNC",
							"globalParamService.synchConfigData Synced successfully");

					ResponseDTO masterSyncResponse = masterSyncService.getMasterSync(
							RegistrationConstants.OPT_TO_REG_MDS_J00001, RegistrationConstants.JOB_TRIGGER_POINT_USER);
					sync_Status = commonUtil.verifyAssertionResponseMessage("Sync successful",
							masterSyncResponse.getSuccessResponseDTO().getMessage());

					if (sync_Status) {
						LOGGER.info("BASE CONFIGURATION - ", "AUTOMATION - REG", "SYNC",
								"masterSyncService.getMasterSync Synced successfully");
						ResponseDTO userDetailResponse = userDetailService
								.save(RegistrationConstants.JOB_TRIGGER_POINT_USER);
						sync_Status = commonUtil.verifyAssertionResponseMessage("Success",
								userDetailResponse.getSuccessResponseDTO().getMessage());

						if (sync_Status) {
							LOGGER.info("BASE CONFIGURATION - ", "AUTOMATION - REG", "SYNC",
									"userDetailService.save Synced successfully");

							ResponseDTO userSaltResponse = userSaltDetailsService
									.getUserSaltDetails(RegistrationConstants.JOB_TRIGGER_POINT_USER);
							sync_Status = commonUtil.verifyAssertionResponseMessage("Success",
									userSaltResponse.getSuccessResponseDTO().getMessage());

							if (sync_Status) {
								LOGGER.info("BASE CONFIGURATION - ", "AUTOMATION - REG", "SYNC",
										"userSaltDetailsService Synced successfully");

								ResponseDTO policysyncResponse = policySyncService.fetchPolicy();
								sync_Status = commonUtil.verifyAssertionResponseMessage("SYNC_SUCCESS",
										policysyncResponse.getSuccessResponseDTO().getMessage());

								if (sync_Status) {
									LOGGER.info("BASE CONFIGURATION - ", "AUTOMATION - REG", "SYNC",
											"PolicySyncService Synced successfully");
								} else {
									LOGGER.debug("BASE CONFIGURATION", "AUTOMATION - REG", "SYNC",
											"PolicySyncService Sync Failed");
									Assert.assertTrue(sync_Status);
								}
							} else {
								LOGGER.debug("BASE CONFIGURATION", "AUTOMATION - REG", "SYNC",
										"masterSyncService Sync Failed");
								Assert.assertTrue(sync_Status);
							}
						} else {
							LOGGER.debug("BASE CONFIGURATION", "AUTOMATION - REG", "SYNC",
									"userDetailService Sync Failed");
							Assert.assertTrue(sync_Status);
						}
					} else {
						LOGGER.debug("BASE CONFIGURATION", "AUTOMATION - REG", "SYNC",
								"GlobalParamService Sync Failed");
						Assert.assertTrue(sync_Status);
					}
				} else {
					LOGGER.debug("BASE CONFIGURATION", "AUTOMATION - REG", "SYNC", "GlobalParamService Sync Failed");
					Assert.assertTrue(sync_Status);
				}

			} else {
				LOGGER.debug("BASE CONFIGURATION", "AUTOMATION - REG", "SYNC", "publicKeySyncImpl Sync Failed");
				Assert.assertTrue(sync_Status);
			}
			// Get User details from User Detail table
			UserDTO userDTO = loginService.getUserDetail(System.getProperty("userID"));
			LoginUserDTO loginUserDTO = new LoginUserDTO();
			loginUserDTO.setUserId(System.getProperty("userID"));
			loginUserDTO.setPassword("mosip");
			// SetUp to create SessionContext
			AuthenticationValidatorDTO authenticationValidatorDTO = new AuthenticationValidatorDTO();
			authenticationValidatorDTO.setUserId(System.getProperty("userID"));
			authenticationValidatorDTO.setPassword("mosip");
			// Set User details UserDTO to ApllicationContext
			ApplicationContext.map().put(RegistrationConstants.USER_DTO, loginUserDTO);
			// Create SessionContext
			Boolean sessionContext_Status = SessionContext.create(userDTO, RegistrationConstants.PWORD, true, false,
					authenticationValidatorDTO);
			if (sessionContext_Status) {
				// Set CenterID
				centerID = userOBservice.getMachineCenterId().get(ConstantValues.CENTERIDLBL);
				System.out.println("---");
				// Set StationID
				stationID = userOBservice.getMachineCenterId().get(ConstantValues.STATIONIDLBL);
				System.out.println("---");
				// Set values to applicationContext
				ApplicationContext.map().put(ConstantValues.CENTERIDLBL, centerID);
				ApplicationContext.map().put(ConstantValues.STATIONIDLBL, stationID);
				ApplicationContext.map().put(RegistrationConstants.GPS_DEVICE_DISABLE_FLAG, ConstantValues.NO);
				ApplicationContext.map().put(RegistrationConstants.REG_DELETION_CONFIGURED_DAYS, "120");
				// SetUp to create Packet
				RegistrationCenterDetailDTO registrationCenter = new RegistrationCenterDetailDTO();
				registrationCenter.setRegistrationCenterId(centerID);
				SessionContext.getInstance().getUserContext().setRegistrationCenterDetailDTO(registrationCenter);
				SessionContext.userContext().setUserId(System.getProperty("userID"));

				// Checkin if User Onboarded or not
				if ((boolean) SessionContext.map().get(RegistrationConstants.ONBOARD_USER)) {
					// Onboard user
					BiometricDTO bioData = null;
					commonUtil.setFlag("N");
					String bioPath = "src/main/resources/RegClient/Resident_BiometricData.json";
					LOGGER.info("USERONBOARD SERVICE TEST - ", "AUTOMATION", "REG", "Path: " + bioPath);
					bioData = commonUtil.getBiotestData(bioPath);
					ResponseDTO actualresponse = userOBservice.validate(bioData);
					commonUtil.verifyAssertionResponse("USER_ONBOARD_SUCCESS", actualresponse);

				} else {
					// User already Onboarded
					LOGGER.info("BASE OCNFIGURATION", "AUTOMATION", "REG", "User already Onboarded");
				}

			} else {
				Assert.assertTrue(sessionContext_Status);
			}
		} catch (IOException ioException) {
			LOGGER.debug("BASE OCNFIGURATION", "AUTOMATION", "REG", ExceptionUtils.getStackTrace(ioException));

		} catch (ParseException parseException) {
			LOGGER.debug("BASE OCNFIGURATION", "AUTOMATION", "REG", ExceptionUtils.getStackTrace(parseException));

		}

	}



}
