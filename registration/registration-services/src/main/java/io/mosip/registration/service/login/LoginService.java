package io.mosip.registration.service.login;

import java.util.List;
import java.util.Set;

import io.mosip.registration.dto.AuthorizationDTO;
import io.mosip.registration.dto.RegistrationCenterDetailDTO;
import io.mosip.registration.dto.ResponseDTO;
import io.mosip.registration.dto.UserDTO;

/**
 * The {@code LoginService} represents to fetch the information related to
 * authentication data, user data, center data, screen authorization data and
 * validating the user and updating table on every login attempt. This will also
 * handle initial sync which will happen before loading login screen.
 * 
 * @author Sravya Surampalli
 * @since 1.0.0
 *
 */
public interface LoginService {

	/**
	 * This method will fetch the list of authentication modes based on the
	 * authentication type and set of roles
	 * 
	 * @param authType
	 *            {@code String} authentication type [Login auth, Packet auth, Exception auth, EOD auth, Onboard auth]
	 * @param roleList
	 *            {@code List} list of user roles[Registration Officer, Registration supervisor, Registration Admin]
	 * 
	 * @return List of login modes
	 */
	List<String> getModesOfLogin(String authType, Set<String> roleList);

	/**
	 * This method will fetch the user details based on the entered userId
	 * 
	 * @param userId
	 *            {@code String} user id entered by the operator
	 * 
	 * @return {@code UserDTO} UserDTO which will have roles, pword, center and machine information
	 */
	UserDTO getUserDetail(String userId);

	/**
	 * This method will fetch Registration Center details based on the center id
	 * mapped to the user id entered by operator and the primary language code
	 * 
	 * @param centerId
	 *            {@code String} centerId corresponding to entered userId
	 * @param langCode
	 *            {@code String} primary language code
	 * 
	 * @return {@code RegistrationCenterDetailDTO} which contains Registration center details
	 */
	RegistrationCenterDetailDTO getRegistrationCenterDetails(String centerId, String langCode);

	/**
	 * This method will fetch Registration screen authorization details based on the
	 * list of roles mapped to the user id entered by operator
	 * 
	 * @param roleCode
	 *            {@code List} list of roles mapped to the user
	 * 
	 * @return {@code AuthorizationDTO} which contains screen authorization details
	 *         based on which screen validation will happen
	 */
	AuthorizationDTO getScreenAuthorizationDetails(List<String> roleCode);

	/**
	 * This method will update login parameters for every login attempt
	 * 
	 * @param userDTO - The {@code UserDTO} user dto which contains the parameters that has to be
	 *            updated
	 */
	void updateLoginParams(UserDTO userDTO);

	/**
	 * Execute global param, master sync, user detail, user salt detail and key
	 * policy sync's as a initial sync
	 * 
	 * <p>If Initial setup is enabled and TPM is enabled:</p>
	 * 		<p>Exceute TPM PublicKeySyncService:</p> 
	 * 			<p>Keyindex will be returned being used in other sync in case of initial setup</p>
	 * <p>Execute PublicKey Sync, Global Param Sync, Master Sync, User Detail Sync, User Salt Detail Sync:</p>
	 * 		<p>If all above sync are success:</p>
	 * 			<p>return Success as status</p>
	 * 		<p>If any sync is failed</p>
	 * 			<p>return Failure as status</p>
	 * 
	 * @return {@code List} list of sync results
	 */
	List<String> initialSync();

	/**
	 * This method will Validate login attempts to check whether operator can login
	 * or not
	 * 
	 * <p>This will validate against invalid number of attempts and time and update the
	 * 	table with the corresponding parameters in case of invalid or valid</p>
	 * 
	 * @param userDTO
	 *            {@code UserDTO} user details
	 * @param errorMessage
	 *            {@code String} error message for validation
	 * @param invalidLoginCount
	 *            {@code Integer} invalid login count against which validation will be done
	 * @param invalidLoginTime
	 *            {@code Integer} invalid login time against which validation will be done
	 * 
	 * @return {@code String} returns whether validation successful or not
	 */
	String validateInvalidLogin(UserDTO userDTO, String errorMessage, int invalidLoginCount, int invalidLoginTime);

	/**
	 * This method will Validate user id entered by operator
	 * 
	 * <p>If User exists :</p>
	 * 		<p>Fetch the center id mapped to user id entered by operator</p>
	 * 			<p>Fetch the roles mapped to user id</p>
	 * 				<p>The success response DTO will be formed and returned from this method.</p>
	 * 
	 * 			<p>If Roles not matches or exists:</p>
	 * 				<p>The failure response DTO will be formed and returned from this method.</p>
	 * 
	 * 		<p>If Center id not matches or exists</p>
	 * 			<p>The failure response DTO will be formed and returned from this method.</p>
	 * 
	 * <p>If user not exists :</p>
	 * 		<p>The failure response DTO will be formed and returned from this method.</p>
	 * 
	 * @param userId
	 *            {@code String} user-id entered by operator
	 * 
	 * @return {@code ResponseDTO} based on the result the response DTO will be
	 *         formed and return to the caller.
	 */
	ResponseDTO validateUser(String userId);

}
