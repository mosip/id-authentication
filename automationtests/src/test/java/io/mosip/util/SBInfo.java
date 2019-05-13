/**
 * 
 */
package io.mosip.util;

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
	
	public SBInfoType toSBInfoType()
	{
		SBInfoType sBInfoType = new SBInfoType();
		if(getFormatType()>0)
		{
			sBInfoType.setFormatType(getFormatType());
		}
		if(getFormatOwner()>0)
		{
			sBInfoType.setFormatOwner(getFormatOwner());
		}
		return sBInfoType;
	}

}
