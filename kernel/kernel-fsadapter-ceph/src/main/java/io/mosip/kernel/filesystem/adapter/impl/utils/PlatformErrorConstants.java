package io.mosip.kernel.filesystem.adapter.impl.utils;

public final class PlatformErrorConstants {

	private static final String KER_KERNEL_PREFIX = "KER-";

	public static final String KER_FILESYSTEM_ADAPTOR_CEPH_MODULE = KER_KERNEL_PREFIX + "FAC-";


	public static final String KER_SYSTEM_EXCEPTION = KER_KERNEL_PREFIX + "SYS-";


	/**
	 * Instantiates a new RPR platform error codes.
	 */
	private PlatformErrorConstants() {
		throw new IllegalStateException("Utility class");
	}

}
