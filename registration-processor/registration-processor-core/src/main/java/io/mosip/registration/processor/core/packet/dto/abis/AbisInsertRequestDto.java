package io.mosip.registration.processor.core.packet.dto.abis;

import java.io.Serializable;

/**
 * The Class AbisInsertRequestDto.
 * @author M1048860 Kiran Raj
 */
public class AbisInsertRequestDto extends AbisCommonRequestDto implements Serializable{

	private static final long serialVersionUID = -8497374150679463099L;

    /** The reference URL. */
    private String referenceURL;

    /**
     * Gets the reference URL.
     *
     * @return the reference URL
     */
    public String getReferenceURL() {
        return referenceURL;
    }

    /**
     * Sets the reference URL.
     *
     * @param referenceURL the new reference URL
     */
    public void setReferenceURL(String referenceURL) {
        this.referenceURL = referenceURL;
    }
    }
