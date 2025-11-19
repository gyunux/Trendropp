package com.celebstyle.api.article.repository;

import static org.springframework.data.jpa.repository.query.QueryUtils.applySorting;

import com.celebstyle.api.article.Article;
import com.celebstyle.api.article.QArticle;
import com.celebstyle.api.article.QArticleImage;
import com.celebstyle.api.article.dto.ArticleAdminView;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ArticleRepositoryImpl implements ArticleRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<ArticleAdminView> findArticleAdminViews(Pageable pageable) {
        QArticle article = QArticle.article;
        QArticleImage articleImage = QArticleImage.articleImage;

        QArticleImage articleImageSub = new QArticleImage("articleImageSub");

        JPAQuery<ArticleAdminView> query = jpaQueryFactory
                .select(Projections.constructor(ArticleAdminView.class,
                        article.id,
                        article.articleDate,
                        JPAExpressions
                                .select(articleImageSub.imageUrl.min())
                                .from(articleImageSub)
                                .where(articleImageSub.article.eq(article)),
                        article.titleKo,
                        article.articleUrl,
                        article.processed
                ))
                .from(article);

        applySorting(String.valueOf(query), pageable.getSort());

        List<ArticleAdminView> content = query
                .offset(pageable.getOffset()) // 시작 위치
                .limit(pageable.getPageSize()) // 페이지 크기
                .fetch(); // 쿼리 실행 및 데이터 가져오기

        Long total = jpaQueryFactory
                .select(article.count())
                .from(article)
                .fetchOne();

        // 5. Page 객체로 반환
        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Article findArticleCreateView(Long id) {
        QArticle article = QArticle.article;
        QArticleImage articleImage = QArticleImage.articleImage;

        Article result = jpaQueryFactory
                .selectFrom(article) // 'select(article).from(article)'과 동일
                .leftJoin(article.articleImages, articleImage)
                .fetchJoin() // <-- 이것이 핵심입니다.
                .where(article.id.eq(id))
                .fetchOne(); // 단 건 조회

        return result;
    }
}
