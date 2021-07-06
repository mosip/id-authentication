package io.mosip.authentication.common.service.websub.dto;

import lombok.Data;

/**
 * Instantiates a new event model.
 * @author Loganathan Sekar
 */
@Data
public class EventModel<T extends EventInterface> {

    /** The publisher. */
    private String publisher;
    
    /** The topic. */
    private String topic;
    
    /** The published on. */
    private String publishedOn;
    
    /** The event. */
    private T event;
}
