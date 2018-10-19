package io.mosip.registration.processor.core.packet.dto;


import lombok.Data;

@Data
public class Audit
{
    private String eventId;

    private String startTimestamp;

    private String endTimestamp;    
   
}

