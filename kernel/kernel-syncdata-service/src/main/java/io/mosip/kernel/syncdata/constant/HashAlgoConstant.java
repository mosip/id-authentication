package io.mosip.kernel.syncdata.constant;

public enum HashAlgoConstant {

	SHA_256_HASH_ALGO("SHA-256");
	
	private final String algoName;

	private HashAlgoConstant(String algoName) {
		this.algoName = algoName;
		
	}

	public String getAlgoName() {
		return this.algoName;
	}

}
