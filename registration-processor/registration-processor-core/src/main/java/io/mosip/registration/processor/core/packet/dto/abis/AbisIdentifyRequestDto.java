package io.mosip.registration.processor.core.packet.dto.abis;

import java.io.Serializable;

// TODO: Auto-generated Javadoc
/**
 * The Class AbisIdentifyRequestDto.
 *
 * @author M1048860 Kiran Raj
 */
public class AbisIdentifyRequestDto extends AbisCommonRequestDto implements Serializable {

	/** The max results. */
	private Integer maxResults;

	/** The target FPIR. */
	private Integer targetFPIR;
	
	/** The reference url. */
	private String referenceUrl;

    /**
     * Gets the reference url.
     *
     * @return the reference url
     */
    public String getReferenceUrl() {
		return referenceUrl;
	}

	/**
	 * Sets the reference url.
	 *
	 * @param referenceUrl the new reference url
	 */
	public void setReferenceUrl(String referenceUrl) {
		this.referenceUrl = referenceUrl;
	}

	/** The gallery. */
	private AbisIdentifyRequestGalleryDto gallery;

	/**
	 * Gets the max results.
	 *
	 * @return the max results
	 */
	public Integer getMaxResults() {
		return maxResults;
	}

	/**
	 * Sets the max results.
	 *
	 * @param maxResults
	 *            the new max results
	 */
	public void setMaxResults(Integer maxResults) {
		this.maxResults = maxResults;
	}

	/**
	 * Gets the target FPIR.
	 *
	 * @return the target FPIR
	 */
	public Integer getTargetFPIR() {
		return targetFPIR;
	}

	/**
	 * Sets the target FPIR.
	 *
	 * @param targetFPIR
	 *            the new target FPIR
	 */
	public void setTargetFPIR(Integer targetFPIR) {
		this.targetFPIR = targetFPIR;
	}

    /**
     * Gets the gallery.
     *
     * @return the gallery
     */
    public AbisIdentifyRequestGalleryDto getGallery() {
        return gallery;
    }

    /**
     * Sets the gallery.
     *
     * @param gallery the new gallery
     */
    public void setGallery(AbisIdentifyRequestGalleryDto gallery) {
        this.gallery = gallery;
    }

}
