package io.mosip.dbdto;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

public class CryptomanagerDto {
	private CryptomanagerRequestDto request;

    private String metadata;

    private LocalDateTime requesttime;

    private String id;

    private String version;

    public CryptomanagerRequestDto getRequest ()
    {
        return request;
    }

    public void setRequest (CryptomanagerRequestDto request)
    {
        this.request = request;
    }

    public String getMetadata ()
    {
        return metadata;
    }

    public void setMetadata (String metadata)
    {
        this.metadata = metadata;
    }

    public LocalDateTime getRequesttime ()
    {
        return requesttime;
    }

    public void setRequesttime (LocalDateTime requesttime)
    {
        this.requesttime = requesttime;
    }

    public String getId ()
    {
        return id;
    }

    public void setId (String id)
    {
        this.id = id;
    }

    public String getVersion ()
    {
        return version;
    }

    public void setVersion (String version)
    {
        this.version = version;
    }

  
}
