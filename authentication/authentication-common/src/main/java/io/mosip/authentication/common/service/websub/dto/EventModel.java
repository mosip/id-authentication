package io.mosip.authentication.common.service.websub.dto;

import java.util.Map;

import lombok.Data;

@Data
public class EventModel<T> {

    private String publisher;
    private String topic;
    private String publishedOn;
    private T event;
    private Map<String, Object> data;
}
