package io.mosip.registration.processor.request.handler.service.dto.json;

import java.util.List;

import lombok.Data;

/**
 * This contains the attributes which have to be displayed in PacketMetaInfo
 * JSON
 * 
 * @author Sowmya
 * @since 1.0.0
 */
@Data
public class Identity {

	private List<FieldValue> metaData;
	private List<FieldValueArray> hashSequence1;
	private List<FieldValueArray> hashSequence2;

}
