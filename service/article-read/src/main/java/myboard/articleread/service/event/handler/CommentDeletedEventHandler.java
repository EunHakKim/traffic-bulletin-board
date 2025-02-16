package myboard.articleread.service.event.handler;

import lombok.RequiredArgsConstructor;
import myboard.articleread.repository.ArticleQueryModelRepository;
import myboard.common.event.Event;
import myboard.common.event.EventType;
import myboard.common.event.payload.CommentCreatedEventPayload;
import myboard.common.event.payload.CommentDeletedEventPayload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommentDeletedEventHandler implements EventHandler<CommentDeletedEventPayload> {
    private final ArticleQueryModelRepository articleQueryModelRepository;

    @Override
    public void handle(Event<CommentDeletedEventPayload> event) {
        articleQueryModelRepository.read(event.getPayload().getArticleId())
                .ifPresent(articleQueryModel -> {
                    articleQueryModel.updateBy(event.getPayload());
                    articleQueryModelRepository.update(articleQueryModel);
                });
    }

    @Override
    public boolean supports(Event<CommentDeletedEventPayload> event) {
        return EventType.COMMENT_DELETED == event.getType();
    }
}
