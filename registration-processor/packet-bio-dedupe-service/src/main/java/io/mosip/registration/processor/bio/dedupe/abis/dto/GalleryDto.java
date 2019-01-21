package io.mosip.registration.processor.bio.dedupe.abis.dto;

/**
 * The Class GalleryDto.
 */
public class GalleryDto {

	/** The url. */
	private String url;

	/** The reference ids. */
	private ReferenceIdDto[] referenceIds;

	/**
	 * Gets the url.
	 *
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Sets the url.
	 *
	 * @param url
	 *            the new url
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Gets the reference ids.
	 *
	 * @return the reference ids
	 */
	public ReferenceIdDto[] getReferenceIds() {
		return referenceIds;
	}

	/**
	 * Sets the reference ids.
	 *
	 * @param referenceIds
	 *            the new reference ids
	 */
	public void setReferenceIds(ReferenceIdDto[] referenceIds) {
		this.referenceIds = referenceIds;
	}
}
