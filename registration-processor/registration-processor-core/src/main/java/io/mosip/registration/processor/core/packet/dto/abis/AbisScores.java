package io.mosip.registration.processor.core.packet.dto.abis;

import java.io.Serializable;

import lombok.Data;

@Data
public class AbisScores implements Serializable{
private String biometricType;
private String scaledScore;

private String internalScore;
private Analytics[] analytics;


}
