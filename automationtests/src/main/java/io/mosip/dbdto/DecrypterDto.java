package io.mosip.dbdto;
import java.time.LocalDateTime;



public class DecrypterDto {
	private String applicationId;
    private String referenceId;
    private LocalDateTime timeStamp;
    private String data;
    public String getApplicationId() {
          return applicationId;
    }
    public void setApplicationId(String applicationId) {
          this.applicationId = applicationId;
    }
    public String getReferenceId() {
          return referenceId;
    }
    public void setReferenceId(String referenceId) {
          this.referenceId = referenceId;
    }
    public LocalDateTime getTimeStamp() {
          return timeStamp;
    }
    public void setTimeStamp(LocalDateTime timeStamp) {
          this.timeStamp = timeStamp;
    }
    public String getData() {
          return data;
    }
    public void setData(String data) {
          this.data = data;
    }

}
