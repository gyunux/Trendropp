package com.celebstyle.api.article.repository;

import com.celebstyle.api.article.Article;
import com.celebstyle.api.article.QArticle;
import com.celebstyle.api.article.QArticleImage;
import com.celebstyle.api.article.dto.ArticleAdminView;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ArticleRepositoryImpl implements ArticleRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<ArticleAdminView> findArticleAdminViews() {
        QArticle article = QArticle.article;
        QArticleImage articleImage = QArticleImage.articleImage;

        QArticleImage articleImageSub = new QArticleImage("articleImageSub");

        return jpaQueryFactory
                .select(Projections.constructor(ArticleAdminView.class,
                        article.id,
                        article.articleDate,
                        // --- 썸네일 URL 서브쿼리 ---
                        JPAExpressions
                                .select(articleImageSub.imageUrl.min()) // ID가 가장 낮은 이미지의 URL (혹은 max)
                                .from(articleImageSub)
                                .where(articleImageSub.article.eq(article)), // 메인 쿼리의 article과 조인
                        // -------------------------
                        article.titleKo,
                        article.articleUrl,
                        article.processed
                ))
                .from(article)
                // .orderBy(...)
                // .offset(...)
                // .limit(...)
                .fetch();
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
