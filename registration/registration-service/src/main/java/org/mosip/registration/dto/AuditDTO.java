package org.mosip.registration.dto;


import lombok.Data;

/**
 * AuditDTO is to capture the time duration for each event
 * @author M1047595
 *
 */
@Data
public class AuditDTO
{
	private String applicationID;
    private String eventId;
    private String startTimestamp;
    private String endTimestamp;    
   
}

