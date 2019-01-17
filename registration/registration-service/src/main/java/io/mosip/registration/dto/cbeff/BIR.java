/**
 * 
 */
package io.mosip.registration.dto.cbeff;

import io.mosip.registration.dto.cbeff.jaxbclasses.BIRType;
import io.mosip.registration.dto.cbeff.jaxbclasses.TestBiometricType;

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
	private TestBiometricType testFingerPrint;
	private TestBiometricType testIris;
	private TestBiometricType testFace;

	public BIR(BIRBuilder birBuilder) {
		this.version = birBuilder.version;
		this.cbeffversion = birBuilder.cbeffversion;
		this.birInfo = birBuilder.birInfo;
		this.bdbInfo = birBuilder.bdbInfo;
		this.bdb = birBuilder.bdb;
		this.sb = birBuilder.sb;
		this.sbInfo = birBuilder.sbInfo;
		this.testFingerPrint = birBuilder.testFingerPrint;
		this.testIris = birBuilder.testIris;
		this.testFace = birBuilder.testFace;
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

	public TestBiometricType getTestFingerPrint() {
		return testFingerPrint;
	}

	public TestBiometricType getTestIris() {
		return testIris;
	}

	public TestBiometricType getTestFace() {
		return testFace;
	}

	public static class BIRBuilder {
		private BIRVersion version;
		private BIRVersion cbeffversion;
		private BIRInfo birInfo;
		private BDBInfo bdbInfo;
		private byte[] bdb;
		private byte[] sb;
		private SBInfo sbInfo;
		private TestBiometricType testFingerPrint;
		private TestBiometricType testIris;
		private TestBiometricType testFace;

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

		public BIRBuilder withTestFingerPrint(TestBiometricType fingerprint) {
			this.testFingerPrint = fingerprint;
			return this;
		}

		public BIRBuilder withTestIris(TestBiometricType iris) {
			this.testIris = iris;
			return this;
		}

		public BIRBuilder withTestFace(TestBiometricType face) {
			this.testFace = face;
			return this;
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
		if(bir.getTestFingerPrint() != null) {
			bIRType.setTestFingerPrint(bir.getTestFingerPrint());
		}
		if(bir.getTestIris() != null) {
			bIRType.setTestIris(bir.getTestIris());
		}
		if(bir.getTestFace() != null) {
			bIRType.setTestFace(bir.getTestFace());
		}
		return bIRType;
	}

}
