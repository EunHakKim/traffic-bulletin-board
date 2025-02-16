package myboard.hotarticle.consumer;

import myboard.common.event.Event;
import myboard.common.event.EventPayload;
import myboard.common.event.EventType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import myboard.hotarticle.service.HotArticleService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class HotArticleEventConsumer {
    private final HotArticleService hotArticleService;

    @KafkaListener(topics = {
            EventType.Topic.KUKE_BOARD_ARTICLE,
            EventType.Topic.KUKE_BOARD_COMMENT,
            EventType.Topic.KUKE_BOARD_LIKE,
            EventType.Topic.KUKE_BOARD_VIEW,
    })
    public void listen(String message, Acknowledgment ack) {
        log.info("[HotArticleEventConsumer.listen] received message: {}", message);
        Event<EventPayload> event = Event.fromJson(message);
        if (event != null) {
            hotArticleService.handleEvent(event);
        }
        ack.acknowledge();
    }
}
