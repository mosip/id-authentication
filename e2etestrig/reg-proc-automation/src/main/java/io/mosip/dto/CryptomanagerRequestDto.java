package io.mosip.dto;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public class CryptomanagerRequestDto {
	    private LocalDateTime timeStamp;

	    private String data;

	    private String applicationId;

	    private String referenceId;

	    public LocalDateTime getTimeStamp ()
	    {
	        return timeStamp;
	    }

	    public void setTimeStamp (LocalDateTime timeStamp)
	    {
	        this.timeStamp = timeStamp;
	    }

	    public String getData ()
	    {
	        return data;
	    }

	    public void setData (String data)
	    {
	        this.data = data;
	    }

	    public String getApplicationId ()
	    {
	        return applicationId;
	    }

	    public void setApplicationId (String applicationId)
	    {
	        this.applicationId = applicationId;
	    }

	    public String getReferenceId ()
	    {
	        return referenceId;
	    }

	    public void setReferenceId (String referenceId)
	    {
	        this.referenceId = referenceId;
	    }

		@Override
		public String toString() {
			return "timeStamp=" + timeStamp + ", data=" + data + ", applicationId="
					+ applicationId + ", referenceId=" + referenceId;
		}

	
}
