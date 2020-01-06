/**
 * 
 */
package io.mosip.kernel.core.cbeffutil.entity;

import io.mosip.kernel.core.cbeffutil.jaxbclasses.RegistryIDType;
import io.mosip.kernel.core.cbeffutil.jaxbclasses.SBInfoType;

/**
 * @author Ramadurai Pandian
 *
 */
public class SBInfo {

	private RegistryIDType format;

	public SBInfo(SBInfoBuilder sBInfoBuilder) {
		this.format = sBInfoBuilder.format;
	}

	public RegistryIDType getFormat() {
		return format;
	}

	public static class SBInfoBuilder {
		private RegistryIDType format;

		public SBInfoBuilder setFormatOwner(RegistryIDType format) {
			this.format = format;
			return this;
		}


		public SBInfo build() {
			return new SBInfo(this);
		}
	}

	public SBInfoType toSBInfoType() {
		SBInfoType sBInfoType = new SBInfoType();
		if (getFormat() !=null) {
			sBInfoType.setFormat(getFormat());
		}
		return sBInfoType;
	}

}
