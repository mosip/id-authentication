package io.mosip.registration.processor.packet.service.dto.json.metadata;

import lombok.Data;

/**
 * This class contains the attributes to be displayed for flat value object in
 * PacketMetaInfo JSON
 * 
 * @author Sowmya
 * @since 1.0.0
 */
@Data
public class FieldValue {

	private String label;
	private String value;

}
