package io.mosip.registration.processor.packet.service.dto.json.metadata;

import java.util.List;

/**
 * This contains the attributes which have to be displayed in PacketMetaInfo
 * JSON
 * 
 * @author Sowmya
 * @since 1.0.0
 */
public class Identity {

	private List<FieldValue> metaData;
	private List<FieldValueArray> hashSequence1;
	private List<FieldValueArray> hashSequence2;

	/**
	 * @return the metaData
	 */
	public List<FieldValue> getMetaData() {
		return metaData;
	}

	/**
	 * @param metaData
	 *            the metaData to set
	 */
	public void setMetaData(List<FieldValue> metaData) {
		this.metaData = metaData;
	}

	/**
	 * @return the hashSequence
	 */
	public List<FieldValueArray> getHashSequence1() {
		return hashSequence1;
	}

	/**
	 * @param hashSequence
	 *            the hashSequence to set
	 */
	public void setHashSequence(List<FieldValueArray> hashSequence) {
		this.hashSequence1 = hashSequence;
	}

	/**
	 * @return the hashSequence2
	 */
	public List<FieldValueArray> getHashSequence2() {
		return hashSequence2;
	}

	/**
	 * @param hashSequence2
	 *            the hashSequence2 to set
	 */
	public void setHashSequence2(List<FieldValueArray> hashSequence2) {
		this.hashSequence2 = hashSequence2;
	}

}
