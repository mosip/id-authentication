/**
 * 
 */
package io.mosip.registration.dto.cbeff;

import io.mosip.registration.dto.cbeff.jaxbclasses.BIRType;

/**
 * @author Ramadurai Pandian
 *
 */
public class BIR {

	private BIRVersion version;
	private BIRVersion cbeffversion;
	private BIRInfo birInfo;
	private BDBInfo bdbInfo;
	private byte[] bdb;
	private byte[] sb;
	private SBInfo sbInfo;

	public BIR(BIRBuilder birBuilder) {
		this.version = birBuilder.version;
		this.cbeffversion = birBuilder.cbeffversion;
		this.birInfo = birBuilder.birInfo;
		this.bdbInfo = birBuilder.bdbInfo;
		this.bdb = birBuilder.bdb;
		this.sb = birBuilder.sb;
		this.sbInfo = birBuilder.sbInfo;
	}

	public BIRVersion getVersion() {
		return version;
	}

	public BIRVersion getCbeffversion() {
		return cbeffversion;
	}

	public BIRInfo getBirInfo() {
		return birInfo;
	}

	public BDBInfo getBdbInfo() {
		return bdbInfo;
	}

	public byte[] getBdb() {
		return bdb;
	}

	public byte[] getSb() {
		return sb;
	}

	public SBInfo getSbInfo() {
		return sbInfo;
	}

	public static class BIRBuilder {
		private BIRVersion version;
		private BIRVersion cbeffversion;
		private BIRInfo birInfo;
		private BDBInfo bdbInfo;
		private byte[] bdb;
		private byte[] sb;
		private SBInfo sbInfo;

		public BIRBuilder withVersion(BIRVersion version) {
			this.version = version;
			return this;
		}

		public BIRBuilder withCbeffversion(BIRVersion cbeffversion) {
			this.cbeffversion = cbeffversion;
			return this;
		}

		public BIRBuilder withBirInfo(BIRInfo birInfo) {
			this.birInfo = birInfo;
			return this;
		}

		public BIRBuilder withBdbInfo(BDBInfo bdbInfo) {
			this.bdbInfo = bdbInfo;
			return this;
		}

		public BIRBuilder withBdb(byte[] bdb) {
			this.bdb = bdb;
			return this;
		}

		public BIRBuilder withSb(byte[] sb) {
			this.sb = sb;
			return this;
		}

		public BIRBuilder withSbInfo(SBInfo sbInfo) {
			this.sbInfo = sbInfo;
			return this;
		}

		public BIR build() {
			return new BIR(this);
		}

	}

	public BIRType toBIRType(BIR bir) {
		BIRType bIRType = new BIRType();
		if (bir.getCbeffversion() != null)
			bIRType.setVersion(bir.getCbeffversion().toVersion());
		bIRType.setBDB(getBdb());
		if (bir.getBirInfo() != null)
			bIRType.setBIRInfo(bir.getBirInfo().toBIRInfo());
		if (bir.getBdbInfo() != null)
			bIRType.setBDBInfo(bir.getBdbInfo().toBDBInfo());
		return bIRType;
	}

}
