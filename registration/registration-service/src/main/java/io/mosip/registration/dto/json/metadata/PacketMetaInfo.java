package io.mosip.registration.dto.json.metadata;

/**
 * The class to represent the flat JSON structure of PacketMetaInfo
 * 
 * @author Balaji Sridharan
 * @since 1.0.0
 */
public class PacketMetaInfo {

	private Identity identity;

	/**
	 * @return the identity
	 */
	public Identity getIdentity() {
		return identity;
	}

	/**
	 * @param identity
	 *            the identity to set
	 */
	public void setIdentity(Identity identity) {
		this.identity = identity;
	}

}
