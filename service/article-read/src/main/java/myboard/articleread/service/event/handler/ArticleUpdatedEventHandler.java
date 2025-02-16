package myboard.articleread.service.event.handler;

import lombok.RequiredArgsConstructor;
import myboard.articleread.repository.ArticleQueryModel;
import myboard.articleread.repository.ArticleQueryModelRepository;
import myboard.common.event.Event;
import myboard.common.event.EventType;
import myboard.common.event.payload.ArticleCreatedEventPayload;
import myboard.common.event.payload.ArticleUpdatedEventPayload;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class ArticleUpdatedEventHandler implements EventHandler<ArticleUpdatedEventPayload> {
    private final ArticleQueryModelRepository articleQueryModelRepository;

    @Override
    public void handle(Event<ArticleUpdatedEventPayload> event) {
        articleQueryModelRepository.read(event.getPayload().getArticleId())
                .ifPresent(articleQueryModel -> {
                    articleQueryModel.updateBy(event.getPayload());
                    articleQueryModelRepository.update(articleQueryModel);
                });
    }

    @Override
    public boolean supports(Event<ArticleUpdatedEventPayload> event) {
        return EventType.ARTICLE_UPDATED == event.getType();
    }
}
