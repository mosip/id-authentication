package io.mosip.registration.dao;

import java.util.List;

import io.mosip.registration.constants.DeviceTypes;
import io.mosip.registration.entity.MachineMaster;
import io.mosip.registration.entity.RegCenterDevice;
import io.mosip.registration.entity.RegCentreMachineDevice;
import io.mosip.registration.entity.RegDeviceMaster;
import io.mosip.registration.entity.RegDeviceType;
import io.mosip.registration.entity.RegistrationCenter;
import io.mosip.registration.entity.UserDetail;
import io.mosip.registration.entity.UserMachineMapping;
import io.mosip.registration.entity.id.UserMachineMappingID;
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
	 * Get center ID using stationID
	 * 
	 * @param stationID station ID
	 * @return center ID 
	 * @throws RegBaseCheckedException Registration Base Checked Exception
	 */
	String getCenterID(String stationID) throws RegBaseCheckedException;

	/**
	 * Get List of users through centerId
	 * 
	 * @param ceneterID
	 *            center id
	 * @return List of Users
	 * @throws RegBaseCheckedException Registration Base Checked Exception
	 */
	List<UserDetail> getUsers(String ceneterID) throws RegBaseCheckedException;

	/**
	 * save user to UserMachineMapping
	 * 
	 * @param user
	 *            user machine mapping
	 * @return created
	 */
	String save(UserMachineMapping user);

	/**
	 * update user to UserMachineMapping
	 * 
	 * @param user
	 *            entity
	 * @return update information
	 */
	String update(UserMachineMapping user);

	/**
	 * UserMachineMapping exists or not
	 * 
	 * @param userID
	 *            entity
	 * @return user
	 */
	UserMachineMapping findByID(UserMachineMappingID userID);

	/**
	 * Get all the active device types
	 * 
	 * @return list of DeviceType
	 */
	List<RegDeviceType> getAllDeviceTypes();

	/**
	 * Get all the devices associated with the given centerId
	 * 
	 * @param centerId
	 *            the id of {@link RegistrationCenter} for which devices have to be
	 *            fetched
	 * @return list of {@link RegCenterDevice}
	 */
	List<RegCenterDevice> getAllValidDevicesByCenterId(String centerId);

	/**
	 * Get all the devices associated with the given centerId and machineId
	 * 
	 * @param centerId
	 *            the id of {@link RegistrationCenter} for which devices have to be
	 *            fetched
	 * @param machineId
	 *            the id of {@link MachineMaster} for which devices have to be
	 *            fetched
	 * @return list of {@link RegCentreMachineDevice}
	 */
	List<RegCentreMachineDevice> getAllMappedDevices(String centerId, String machineId);

	/**
	 * Saves all the devices that are mapped to the machine
	 * 
	 * @param regCentreMachineDevices
	 *            the list of devices mapped to the machine
	 */
	void addedMappedDevice(List<RegCentreMachineDevice> regCentreMachineDevices);

	/**
	 * Deletes the device mappings from the machine
	 * 
	 * @param regCentreMachineDevices
	 *            the list of devices to be removed from the machine
	 */
	void deleteUnMappedDevice(List<RegCentreMachineDevice> regCentreMachineDevices);

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

}
