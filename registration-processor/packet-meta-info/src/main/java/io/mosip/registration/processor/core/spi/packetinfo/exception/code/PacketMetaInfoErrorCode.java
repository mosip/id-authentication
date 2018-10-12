package io.mosip.registration.processor.core.spi.packetinfo.exception.code;

public final class PacketMetaInfoErrorCode {


	private PacketMetaInfoErrorCode() {
		throw new IllegalStateException("Packet Data Save failure");
	}
	
	public static final String TABLE_NOT_ACCESSIBLE = "File not fount in DFS Location";
}
