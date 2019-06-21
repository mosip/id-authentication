package io.mosip.registration.dao;

import java.util.List;

import io.mosip.registration.constants.DeviceTypes;
import io.mosip.registration.entity.RegDeviceMaster;
import io.mosip.registration.entity.UserMachineMapping;
import io.mosip.registration.exception.RegBaseCheckedException;

/**
 * This class is used to update the mapping of users and devices to the
 * Registration Center Machine
 * 
 * @author YASWANTH S
 * @author Brahmananda Reddy
 * @since 1.0.0
 *
 */
public interface MachineMappingDAO {

	/**
	 * This method is used to get station id by using mac address
	 * 
	 * @param MacAddress
	 *            machine mac address
	 * @return station ID
	 * @throws RegBaseCheckedException Registration Base Checked Exception
	 */
	String getStationID(String MacAddress) throws RegBaseCheckedException;
	
	/**
	 * This method is used to check the validity of the device.
	 * 
	 * @param deviceType
	 *            device type
	 * @param serialNo
	 *            serial number of the device
	 * @return It returns true when record found for the device else false
	 */
	boolean isValidDevice(DeviceTypes deviceType, String serialNo);

	/**
	 * This method is used to get the user mapping details that are mapped to the given machine id.
	 * 
	 * @param machineId
	 *            machine ID
	 * @return It returns the list of users against the machine
	 */
	List<UserMachineMapping> getUserMappingDetails(String machineId);

	/**
	 * This method is udes to fetches all the devices mapped to the registration center based on the
	 * language code
	 * 
	 * @param langCode
	 *            the language code of the device
	 * @return All the devices mapped to registration center
	 */
	List<RegDeviceMaster> getDevicesMappedToRegCenter(String langCode);

	/**This method is used to find whether the user exists or not
	 * @param userId 
	 * 			userId
	 * @return is exists or not
	 */
	boolean isExists(String userId);

	/**
	 * This method is used to get the key index of the Machine based on MAC Id
	 * 
	 * @param macId
	 *            the MAC Id of the machine
	 * @return the key index of the machine
	 */
	String getKeyIndexByMacId(String macId);

}
