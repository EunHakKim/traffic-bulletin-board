package myboard.articleread.service.event.handler;

import lombok.RequiredArgsConstructor;
import myboard.articleread.repository.ArticleQueryModelRepository;
import myboard.common.event.Event;
import myboard.common.event.EventType;
import myboard.common.event.payload.ArticleDeletedEventPayload;
import myboard.common.event.payload.CommentCreatedEventPayload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentCreatedEventHandler implements EventHandler<CommentCreatedEventPayload> {
    private final ArticleQueryModelRepository articleQueryModelRepository;

    @Override
    public void handle(Event<CommentCreatedEventPayload> event) {
        articleQueryModelRepository.read(event.getPayload().getArticleId())
                .ifPresent(articleQueryModel -> {
                    articleQueryModel.updateBy(event.getPayload());
                    articleQueryModelRepository.update(articleQueryModel);
                });
    }

    @Override
    public boolean supports(Event<CommentCreatedEventPayload> event) {
        return EventType.COMMENT_CREATED == event.getType();
    }
}
