package io.mosip.registration.dao;

import java.sql.Timestamp;
import java.util.List;

import io.mosip.registration.entity.DeviceType;
import io.mosip.registration.entity.MachineMaster;
import io.mosip.registration.entity.RegCenterDevice;
import io.mosip.registration.entity.RegCentreMachineDevice;
import io.mosip.registration.entity.RegistrationCenter;
import io.mosip.registration.entity.RegistrationUserDetail;
import io.mosip.registration.entity.UserMachineMapping;
import io.mosip.registration.entity.UserMachineMappingID;
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
	 * @throws RegBaseCheckedException
	 */
	String getStationID(String MacAddress) throws RegBaseCheckedException;

	/**
	 * Get center ID using stationID
	 * 
	 * @param stationID
	 * @return center ID
	 * @throws RegBaseCheckedException
	 */
	String getCenterID(String stationID) throws RegBaseCheckedException;

	/**
	 * Get List of users through centerId
	 * 
	 * @param ceneterID
	 *            center id
	 * @return List of Users
	 * @throws RegBaseCheckedException
	 */
	List<RegistrationUserDetail> getUsers(String ceneterID) throws RegBaseCheckedException;

	/**
	 * save user to UserMachineMapping
	 * 
	 * @param user
	 * @return
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
	List<DeviceType> getAllDeviceTypes();

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
	 * @param deviceProvider
	 * @return
	 * 		It returns true when record found for the device else false
	 */
	boolean isValidDevice(String deviceType,String deviceProvider,Timestamp currentDate);

}
