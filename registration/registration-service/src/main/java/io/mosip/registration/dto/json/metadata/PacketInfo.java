package io.mosip.registration.dto.json.metadata;

import java.util.Map;
/**
 * This class is to capture the json parsing packet info data
 * 
 * @author Dinesh Asokan
 * @since 1.0.0
 *
 */
public class PacketInfo {

	private Photograph photograph;
	private BiometericData biometericData;
	private Document document;
	private MetaData metaData;
	private OSIData osiData;
	private HashSequence hashSequence;
	private Map<String, String> checkSumMap;

	/**
	 * @return the photograph
	 */
	public Photograph getPhotograph() {
		return photograph;
	}

	/**
	 * @param photograph
	 *            the photograph to set
	 */
	public void setPhotograph(Photograph photograph) {
		this.photograph = photograph;
	}

	/**
	 * @return the biometericData
	 */
	public BiometericData getBiometericData() {
		return biometericData;
	}

	/**
	 * @param biometericData
	 *            the biometericData to set
	 */
	public void setBiometericData(BiometericData biometericData) {
		this.biometericData = biometericData;
	}

	/**
	 * @return the document
	 */
	public Document getDocument() {
		return document;
	}

	/**
	 * @param document
	 *            the document to set
	 */
	public void setDocument(Document document) {
		this.document = document;
	}

	/**
	 * @return the metaData
	 */
	public MetaData getMetaData() {
		return metaData;
	}

	/**
	 * @param metaData
	 *            the metaData to set
	 */
	public void setMetaData(MetaData metaData) {
		this.metaData = metaData;
	}

	/**
	 * @return the osiData
	 */
	public OSIData getOsiData() {
		return osiData;
	}

	/**
	 * @param osiData
	 *            the osiData to set
	 */
	public void setOsiData(OSIData osiData) {
		this.osiData = osiData;
	}

	/**
	 * @return the hashSequence
	 */
	public HashSequence getHashSequence() {
		return hashSequence;
	}

	/**
	 * @param hashSequence
	 *            the hashSequence to set
	 */
	public void setHashSequence(HashSequence hashSequence) {
		this.hashSequence = hashSequence;
	}

	/**
	 * @return the checkSumMap
	 */
	public Map<String, String> getCheckSumMap() {
		return checkSumMap;
	}

	/**
	 * @param checkSumMap
	 *            the checkSumMap to set
	 */
	public void setCheckSumMap(Map<String, String> checkSumMap) {
		this.checkSumMap = checkSumMap;
	}
}
