package io.mosip.registration.processor.packet.service;

public interface PacketGeneratorService {

	public String createPacket(String uin, String registrationType, String applicantType, String reason);

}
