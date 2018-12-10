package io.mosip.registration.processor.core.spi.packetmanager;

import java.io.InputStream;
import java.util.List;

import io.mosip.registration.processor.core.packet.dto.FieldValue;
import io.mosip.registration.processor.core.packet.dto.RegOsiDto;
import io.mosip.registration.processor.core.packet.dto.RegistrationCenterMachineDto;
import io.mosip.registration.processor.core.packet.dto.demographicinfo.DemographicDedupeDto;

/**
 * The Interface PacketInfoManager.
 *
 * @author Horteppa (M1048399)
 * @param <T>
 *            PacketInfoDto
 * @param <A>
 *            the generic type
 */
public interface PacketInfoManager<T, /** D, M, */
		A> {

	/**
	 * Save packet data.
	 *
	 * @param packetInfo
	 *            the packet info
	 */
	public void savePacketData(T packetInfo);

	/**
	 * Save demographic data.
	 *
	 * @param demographicJsonStream
	 *            the demographic json stream
	 * @param metaData
	 *            the meta data
	 */
	public void saveDemographicInfoJson(InputStream demographicJsonStream, List<FieldValue> metaData);

	/**
	 * Gets the OsiData for QC user.
	 *
	 * @param regid
	 *            the registration id
	 * @return OsiData for the registration id
	 */
	public RegOsiDto getOsi(String regid);

	/**
	 * Gets the RegistrationCenterMachine deatils for registration id.
	 *
	 * @param regid
	 *            the registration id
	 * @return RegistrationCenterMachineDto for the registration id
	 */
	public RegistrationCenterMachineDto getRegistrationCenterMachine(String regid);

	/**
	 * Gets the packetsfor QC user.
	 *
	 * @param qcUserId
	 *            the qc user id
	 * @return the packetsfor QC user
	 */
	public List<A> getPacketsforQCUser(String qcUserId);

	/**
	 * Find demo by id.
	 *
	 * @param regId
	 *            the reg id
	 * @return the list
	 */
	public List<DemographicDedupeDto> findDemoById(String regId);

	/**
	 * Find UIN by id.
	 *
	 * @param regId
	 *            the reg id
	 * @return the string
	 */
	public String findUINById(String regId);

	/**
	 * Gets the applicant finger print image name by id.
	 *
	 * @param regId
	 *            the reg id
	 * @return the applicant finger print image name by id
	 */
	public List<String> getApplicantFingerPrintImageNameById(String regId);

	/**
	 * Gets the applicant iris image name by id.
	 *
	 * @param regId
	 *            the reg id
	 * @return the applicant iris image name by id
	 */
	public List<String> getApplicantIrisImageNameById(String regId);

}