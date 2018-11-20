package io.mosip.registration.dao;

import java.util.List;

import io.mosip.registration.entity.DeviceType;
import io.mosip.registration.entity.RegCenterDevice;
import io.mosip.registration.entity.RegCentreMachineDevice;
import io.mosip.registration.entity.RegCentreMachineDeviceId;
import io.mosip.registration.entity.RegistrationUserDetail;
import io.mosip.registration.entity.UserMachineMapping;
import io.mosip.registration.entity.UserMachineMappingID;
import io.mosip.registration.exception.RegBaseCheckedException;

/**
 * DAO class for Maching mapping
 * 
 * @author YASWANTH S
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
	/** Get List of users through centerId
	 * @param ceneterID center id 
	 * @return List of Users
	 * @throws RegBaseCheckedException 
	 */
	List<RegistrationUserDetail> getUsers(String ceneterID) throws RegBaseCheckedException;
	
	/**
	 * save user to UserMachineMapping
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
	 * get all active devicetypes
	 * 
	 * @return list of DeviceType
	 */
	List<DeviceType> getAllDeviceTypes();

	/**
	 * get All the devices for that center
	 * 
	 * @param centerId
	 * @param deviceType
	 * @return list of {@link RegCenterDevice}
	 */
	List<RegCenterDevice> getAllDeviceBasedOnCenterId(String centerId);

	/**
	 * get all mapped devices
	 * 
	 * @param centerId
	 * @param machineId
	 * @param deviceType
	 * @return list of {@link RegCentreMachineDevice}
	 */

	List<RegCentreMachineDevice> getAllMappedDevices(String centerId, String machineId);
	
	void deleteUnMappedDevice(RegCentreMachineDeviceId regCentreMachineDeviceId);
	
	void addedMappedDevice(RegCentreMachineDevice regCentreMachineDevice);



}
