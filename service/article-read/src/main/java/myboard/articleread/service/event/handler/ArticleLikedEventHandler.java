package myboard.articleread.service.event.handler;

import lombok.RequiredArgsConstructor;
import myboard.articleread.repository.ArticleQueryModelRepository;
import myboard.common.event.Event;
import myboard.common.event.EventType;
import myboard.common.event.payload.ArticleLikedEventPayload;
import myboard.common.event.payload.CommentCreatedEventPayload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArticleLikedEventHandler implements EventHandler<ArticleLikedEventPayload> {
    private final ArticleQueryModelRepository articleQueryModelRepository;

    @Override
    public void handle(Event<ArticleLikedEventPayload> event) {
        articleQueryModelRepository.read(event.getPayload().getArticleId())
                .ifPresent(articleQueryModel -> {
                    articleQueryModel.updateBy(event.getPayload());
                    articleQueryModelRepository.update(articleQueryModel);
                });
    }

    @Override
    public boolean supports(Event<ArticleLikedEventPayload> event) {
        return EventType.ARTICLE_LIKED == event.getType();
    }
}
