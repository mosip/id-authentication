package org.mosip.registration.dto.json.metadata;


import lombok.Data;

@Data
public class Audit
{
    private String eventId;

    private String startTimestamp;

    private String endTimestamp;    
   
}

