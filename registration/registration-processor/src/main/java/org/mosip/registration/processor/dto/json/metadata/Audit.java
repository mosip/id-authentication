package org.mosip.registration.processor.dto.json.metadata;


import lombok.Data;

@Data
public class Audit
{
    private String eventId;

    private String startTimestamp;

    private String endTimestamp;    
   
}

