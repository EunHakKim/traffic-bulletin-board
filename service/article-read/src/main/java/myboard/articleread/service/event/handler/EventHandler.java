package myboard.articleread.service.event.handler;

import myboard.common.event.Event;
import myboard.common.event.EventPayload;

public interface EventHandler<T extends EventPayload> {
    void handle(Event<T> event);
    boolean supports(Event<T> event);
}
