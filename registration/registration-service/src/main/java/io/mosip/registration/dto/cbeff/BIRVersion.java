/**
 * 
 */
package io.mosip.registration.dto.cbeff;

import io.mosip.registration.dto.cbeff.jaxbclasses.VersionType;

/**
 * @author Ramadurai Pandian
 *
 */
public class BIRVersion {

	private int minor;
	private int major;

	public BIRVersion(BIRVersionBuilder birBuilder) {
		this.major = birBuilder.major;
		this.minor = birBuilder.minor;
	}

	public int getMinor() {
		return minor;
	}

	public int getMajor() {
		return major;
	}

	public static class BIRVersionBuilder {
		private int minor;
		private int major;

		public BIRVersionBuilder withMinor(int minor) {
			this.minor = minor;
			return this;
		}

		public BIRVersionBuilder withMajor(int major) {
			this.major = major;
			return this;
		}

		public BIRVersion build() {
			return new BIRVersion(this);
		}
	}

	public VersionType toVersion() {
		VersionType version = new VersionType();
		if (getMinor() > 0)
			version.setMajor(getMinor());
		if (getMajor() > 0)
			version.setMajor(getMajor());
		return version;
	}
}
