package myboard.articleread.service.event.handler;

import lombok.RequiredArgsConstructor;
import myboard.articleread.repository.ArticleIdListRepository;
import myboard.articleread.repository.ArticleQueryModelRepository;
import myboard.articleread.repository.BoardArticleCountRepository;
import myboard.common.event.Event;
import myboard.common.event.EventType;
import myboard.common.event.payload.ArticleDeletedEventPayload;
import myboard.common.event.payload.ArticleUpdatedEventPayload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArticleDeletedEventHandler implements EventHandler<ArticleDeletedEventPayload> {
    private final ArticleQueryModelRepository articleQueryModelRepository;
    private final ArticleIdListRepository articleIdListRepository;
    private final BoardArticleCountRepository boardArticleCountRepository;

    @Override
    public void handle(Event<ArticleDeletedEventPayload> event) {
        ArticleDeletedEventPayload payload = event.getPayload();
        articleIdListRepository.delete(payload.getBoardId(), payload.getArticleId());
        articleQueryModelRepository.delete(payload.getArticleId());
        boardArticleCountRepository.createOrUpdate(payload.getBoardId(), payload.getBoardArticleCount());
    }

    @Override
    public boolean supports(Event<ArticleDeletedEventPayload> event) {
        return EventType.ARTICLE_DELETED == event.getType();
    }
}
