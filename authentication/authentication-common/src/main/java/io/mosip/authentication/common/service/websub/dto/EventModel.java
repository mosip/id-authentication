package io.mosip.authentication.common.service.websub.dto;

import lombok.Data;

@Data
public class EventModel<T> {

    private String publisher;
    private String topic;
    private String publishedOn;
    private T event;
}
