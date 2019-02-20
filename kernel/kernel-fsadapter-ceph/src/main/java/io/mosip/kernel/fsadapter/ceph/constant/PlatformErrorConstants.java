package io.mosip.kernel.fsadapter.ceph.constant;

public final class PlatformErrorConstants {

	private static final String RPR_KERNEL_PREFIX = "KER-";

	public static final String RPR_FILESYSTEM_ADAPTOR_CEPH_MODULE = RPR_KERNEL_PREFIX + "FAC-";

	/** The Constant RPR_SYSTEM_EXCEPTION. */
	public static final String RPR_SYSTEM_EXCEPTION = RPR_KERNEL_PREFIX + "SYS-";

	/**
	 * Instantiates a new RPR platform error codes.
	 */
	private PlatformErrorConstants() {
		throw new IllegalStateException("Utility class");
	}

}
