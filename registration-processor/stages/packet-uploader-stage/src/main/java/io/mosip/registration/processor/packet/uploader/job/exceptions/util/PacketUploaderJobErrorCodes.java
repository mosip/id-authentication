package io.mosip.registration.processor.packet.uploader.job.exceptions.util;

public class PacketUploaderJobErrorCodes {
	private static final String IIS_EPP_EPV_PREFIX = "IIS_";

	private PacketUploaderJobErrorCodes() {
		throw new IllegalStateException("Utility class");
	}

	// Generic
	private static final String IIS_EPP_EPV_PREFIX_GEN_MODULE = IIS_EPP_EPV_PREFIX + "GEN_";
	public static final String IIS_EPP_EPV_DFS_NOT_ACCESSIBLE = IIS_EPP_EPV_PREFIX_GEN_MODULE + "DFS_NOT_ACCESSIBLE";
	
}
