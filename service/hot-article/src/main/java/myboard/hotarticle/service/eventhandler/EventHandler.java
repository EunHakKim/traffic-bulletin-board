package myboard.hotarticle.service.eventhandler;

import myboard.common.event.Event;
import myboard.common.event.EventPayload;

public interface EventHandler<T extends EventPayload> {
    void handle(Event<T> event);
    boolean supports(Event<T> event);
    Long findArticleId(Event<T> event);
}
