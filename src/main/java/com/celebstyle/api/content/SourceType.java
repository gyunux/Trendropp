package com.celebstyle.api.content; // (content 패키지 또는 공통 패키지에 위치)

public enum SourceType {
    INSTAGRAM("인스타그램"),       // 인스타그램 게시물
    AIRPORT_FASHION("공항패션"), // 공항패션 기사 또는 사진
    OFFICIAL_EVENT("공식행사"),  // 패션 행사,시상식, 제작발표회 등
    BROADCAST("방송 출연"),       // TV, 유튜브 등 방송 프로그램
    ETC("기타");               // 기타 출처

    private final String description;

    SourceType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
