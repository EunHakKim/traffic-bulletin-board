# Traffic Bulletin Board
대규모 데이터와 트래픽을 지탱할 수 있는 시스템 설계를 목표로 함.   
각 DB에 1200만여건의 데이터 삽입 후 테스트 진행.

## 시스템 아키텍처
```
.
├── common
│   ├── data-serializer
│   ├── event
│   ├── outbox-message-relay
│   └── snowflake
│
└── service
    ├── article
    ├── article-read
    ├── comment
    ├── hot-article
    ├── like
    └── view
```

## 핵심 설계
##### • 대규모 데이터에서의 페이징(페이지 번호, 무한 스크롤) 최적화 쿼리
##### • 동시성을 고려한 좋아요 처리
##### • 트래픽을 고려한 조회수 설계 및 개수 단위 조회수 백업
##### • 조회수 어뷰징 방지 정책
##### • 카프카를 활용한 비동기 인기글 선정
##### • 대규모 읽기 트래픽을 고려한 게시글 단건, 목록 조회 최적화(CQRS 패턴 적용)
