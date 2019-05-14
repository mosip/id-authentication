package io.mosip.registration.processor.packet.service.dto.json.metadata;

import java.util.List;

import lombok.Data;

/**
 * This class contains the attributes to be displayed for flat array object in
 * PacketMetaInfo JSON
 * 
 * @author Sowmya
 * @since 1.0.0
 */
@Data
public class FieldValueArray {

	private String label;
	private List<String> value;

}
