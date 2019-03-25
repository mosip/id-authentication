package io.mosip.registration.processor.packet.service.mapper;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

import io.mosip.kernel.core.util.DateUtils;
import io.mosip.registration.processor.core.exception.util.PlatformErrorMessages;
import io.mosip.registration.processor.packet.service.dto.RegistrationDTO;
import io.mosip.registration.processor.packet.service.dto.RegistrationMetaDataDTO;
import io.mosip.registration.processor.packet.service.dto.json.metadata.FieldValue;
import io.mosip.registration.processor.packet.service.dto.json.metadata.Identity;
import io.mosip.registration.processor.packet.service.dto.json.metadata.PacketMetaInfo;
import io.mosip.registration.processor.packet.service.exception.RegBaseUnCheckedException;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;

/**
 * The custom Orika Mapper converter class for converting the
 * {@link RegistrationDTO} object to {@link PacketMetaInfo}
 * 
 * @author Sowmya
 * @since 1.0.0
 */
public class PacketMetaInfoConverter extends CustomConverter<RegistrationDTO, PacketMetaInfo> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see ma.glasnost.orika.Converter#convert(java.lang.Object,
	 * ma.glasnost.orika.metadata.Type)
	 */
	@Override
	public PacketMetaInfo convert(RegistrationDTO source, Type<? extends PacketMetaInfo> destinationType) {
		// Instantiate PacketMetaInfo object
		PacketMetaInfo packetMetaInfo = new PacketMetaInfo();
		try {
			// Initialize PacketMetaInfo object
			Identity identity = new Identity();
			packetMetaInfo.setIdentity(identity);

			// Set MetaData
			identity.setMetaData(getMetaData(source));

		} catch (RuntimeException runtimeException) {
			throw new RegBaseUnCheckedException(PlatformErrorMessages.RPR_PGS_PACKET_META_CONVERTOR_EXCEPTION.getCode(),
					PlatformErrorMessages.RPR_PGS_PACKET_META_CONVERTOR_EXCEPTION.getMessage(), runtimeException);

		}
		return packetMetaInfo;
	}

	/**
	 * Set uin updated fields.
	 *
	 * @param source
	 *            the source
	 * @param identity
	 *            the identity
	 */

	private List<FieldValue> getMetaData(RegistrationDTO registrationDTO) {
		List<FieldValue> metaData = new LinkedList<>();

		// Get RegistrationMetaDataDTO
		RegistrationMetaDataDTO metaDataDTO = registrationDTO.getRegistrationMetaDataDTO();

		// Add Registration Type
		metaData.add(buildFieldValue("registrationType", metaDataDTO.getRegistrationCategory()));

		// Add Registration ID
		metaData.add(buildFieldValue("registrationId", registrationDTO.getRegistrationId()));

		metaData.add(buildFieldValue("uin", metaDataDTO.getUin()));

		// Add Registration Creation Date
		metaData.add(buildFieldValue("creationDate", DateUtils.formatToISOString(LocalDateTime.now())));

		return metaData;
	}

	private FieldValue buildFieldValue(String label, String value) {
		FieldValue fieldValue = new FieldValue();
		fieldValue.setLabel(label);
		fieldValue.setValue(value);
		return fieldValue;
	}

}
