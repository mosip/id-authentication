/**
 * 
 */
package io.mosip.registration.dto.cbeff;

/**
 * @author Ramadurai Pandian
 *
 */
public class SBInfo {

	private Long formatOwner;
	private Long formatType;

	public SBInfo(SBInfoBuilder sBInfoBuilder) {
		this.formatOwner = sBInfoBuilder.formatOwner;
		this.formatType = sBInfoBuilder.formatType;
	}

	public Long getFormatOwner() {
		return formatOwner;
	}

	public Long getFormatType() {
		return formatType;
	}

	public static class SBInfoBuilder {
		private Long formatOwner;
		private Long formatType;

		public SBInfoBuilder setFormatOwner(Long formatOwner) {
			this.formatOwner = formatOwner;
			return this;
		}

		public SBInfoBuilder setFormatType(Long formatType) {
			this.formatType = formatType;
			return this;
		}

		public SBInfo build() {
			return new SBInfo(this);
		}
	}

}
