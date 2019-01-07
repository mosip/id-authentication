package io.mosip.registration.processor.bio.dedupe.abis.dto;

public class GalleryDto {
	private String url;
	private ReferenceIdDto[] referenceIds;
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public ReferenceIdDto[] getReferenceIds() {
		return referenceIds;
	}
	public void setReferenceIds(ReferenceIdDto[] referenceIds) {
		this.referenceIds = referenceIds;
	}
}
