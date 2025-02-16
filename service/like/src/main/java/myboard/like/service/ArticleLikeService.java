package myboard.like.service;

import jakarta.transaction.Transactional;
import myboard.common.event.EventType;
import myboard.common.event.payload.ArticleLikedEventPayload;
import myboard.common.event.payload.ArticleUnlikedEventPayload;
import myboard.common.outboxmessagerelay.OutboxEventPublisher;
import myboard.common.snowflake.Snowflake;
import lombok.RequiredArgsConstructor;
import myboard.like.entity.ArticleLike;
import myboard.like.entity.ArticleLikeCount;
import myboard.like.repository.ArticleLikeCountRepository;
import myboard.like.repository.ArticleLikeRepository;
import myboard.like.service.response.ArticleLikeResponse;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ArticleLikeService {
    private final Snowflake snowflake = new Snowflake();
    private final ArticleLikeRepository articleLikeRepository;
    private final ArticleLikeCountRepository articleLikeCountRepository;
    private final OutboxEventPublisher outboxEventPublisher;

    public ArticleLikeResponse read(Long articleId, Long userId) {
        return articleLikeRepository.findByArticleIdAndUserId(articleId, userId)
                .map(ArticleLikeResponse::from)
                .orElseThrow();
    }

    @Transactional
    public void likePessimisticLock1(Long articleId, Long userId) {
        ArticleLike articleLike = articleLikeRepository.save(
                ArticleLike.create(
                        snowflake.nextId(),
                        articleId,
                        userId
                )
        );

        int result = articleLikeCountRepository.increase(articleId);
        if (result == 0) {
            articleLikeCountRepository.save(
                    ArticleLikeCount.init(articleId, 1L)
            );
        }

        outboxEventPublisher.publish(
                EventType.ARTICLE_LIKED,
                ArticleLikedEventPayload.builder()
                        .articleLikeId(articleLike.getArticleLikeId())
                        .articleId(articleLike.getArticleId())
                        .userId(articleLike.getUserId())
                        .createdAt(articleLike.getCreatedAt())
                        .articleLikeCount(count(articleLike.getArticleId()))
                        .build(),
                articleLike.getArticleId()
        );
    }

    @Transactional
    public void unlikePessimisticLock1(Long articleId, Long userId) {
        articleLikeRepository.findByArticleIdAndUserId(articleId, userId)
                .ifPresent(articleLike -> {
                    articleLikeRepository.delete(articleLike);
                    articleLikeCountRepository.decrease(articleId);
                    outboxEventPublisher.publish(
                            EventType.ARTICLE_UNLIKED,
                            ArticleUnlikedEventPayload.builder()
                                    .articleLikeId(articleLike.getArticleLikeId())
                                    .articleId(articleLike.getArticleId())
                                    .userId(articleLike.getUserId())
                                    .createdAt(articleLike.getCreatedAt())
                                    .articleLikeCount(count(articleLike.getArticleId()))
                                    .build(),
                            articleLike.getArticleId()
                    );
                });
    }

    @Transactional
    public void likePessimisticLock2(Long articleId, Long userId) {
        articleLikeRepository.save(
                ArticleLike.create(
                        snowflake.nextId(),
                        articleId,
                        userId
                )
        );
        ArticleLikeCount articleLikeCount = articleLikeCountRepository.findLockedByArticleId(articleId)
                .orElseGet(() -> ArticleLikeCount.init(articleId, 0L));
        articleLikeCount.increase();
        articleLikeCountRepository.save(articleLikeCount);
    }

    @Transactional
    public void unlikePessimisticLock2(Long articleId, Long userId) {
        articleLikeRepository.findByArticleIdAndUserId(articleId, userId)
                .ifPresent(articleLike -> {
                    articleLikeRepository.delete(articleLike);
                    ArticleLikeCount articleLikeCount = articleLikeCountRepository.findLockedByArticleId(articleId).orElseThrow();
                    articleLikeCount.decrease();
                });
    }

    @Transactional
    public void likeOptimisticLock(Long articleId, Long userId) {
        articleLikeRepository.save(
                ArticleLike.create(
                        snowflake.nextId(),
                        articleId,
                        userId
                )
        );

        ArticleLikeCount articleLikeCount = articleLikeCountRepository.findById(articleId)
                .orElseGet(() -> ArticleLikeCount.init(articleId, 0L));
        articleLikeCount.increase();
        articleLikeCountRepository.save(articleLikeCount);
    }

    @Transactional
    public void unlikeOptimisticLock(Long articleId, Long userId) {
        articleLikeRepository.findByArticleIdAndUserId(articleId, userId)
                .ifPresent(articleLike -> {
                    articleLikeRepository.delete(articleLike);
                    ArticleLikeCount articleLikeCount = articleLikeCountRepository.findById(articleId).orElseThrow();
                    articleLikeCount.decrease();
                });
    }

    public Long count(Long articleId) {
        return articleLikeCountRepository.findById(articleId)
                .map(ArticleLikeCount::getLikeCount)
                .orElse(0L);
    }
}
