package io.mosip.registration.dao;

import java.util.List;

import io.mosip.registration.constants.DeviceTypes;
import io.mosip.registration.entity.MachineMaster;
import io.mosip.registration.entity.RegDeviceMaster;
import io.mosip.registration.entity.UserMachineMapping;
import io.mosip.registration.exception.RegBaseCheckedException;

/**
 * This DAO interface updates the mapping of users and devices to the
 * Registration Center Machine
 * 
 * @author YASWANTH S
 * @author Brahmananda Reddy
 * @since 1.0.0
 *
 */
public interface MachineMappingDAO {

	/**
	 * Get Station ID by using mac address
	 * 
	 * @param MacAddress
	 *            machine address
	 * @return station ID
	 * @throws RegBaseCheckedException Registration Base Checked Exception
	 */
	String getStationID(String MacAddress) throws RegBaseCheckedException;
	
	/**
	 * Method to check the device is valid
	 * 
	 * @param deviceType
	 *            device type
	 * @param serialNo
	 *            serial number
	 * @return It returns true when record found for the device else false
	 */
	boolean isValidDevice(DeviceTypes deviceType, String serialNo);

	/**
	 * 
	 * @param machineId
	 *            machine ID
	 * @return It returns the list of users against the machine
	 */
	List<UserMachineMapping> getUserMappingDetails(String machineId);

	/**
	 * Fetches all the devices mapped to the registration center based on the
	 * language code
	 * 
	 * @param langCode
	 *            the language code of the device
	 * @return All the devices mapped to registration center
	 */
	List<RegDeviceMaster> getDevicesMappedToRegCenter(String langCode);

	/**Find whether the user exists or not
	 * @param userId userId
	 * @return is exists or not
	 */
	boolean isExists(String userId);

	/**
	 * Get the {@link MachineMaster} based on name
	 * 
	 * @param name
	 *            the name of the machine
	 * @return the {@link MachineMaster}
	 */
	MachineMaster getMachineByName(String name);

}
