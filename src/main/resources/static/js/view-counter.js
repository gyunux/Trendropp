/**
 * 콘텐츠 상세 페이지 조회수 카운터
 * 페이지 로드 시, 백엔드에 조회수 증가 API를 호출합니다.
 * 중복 확인(쿠키)은 백엔드가 알아서 처리합니다.
 */
document.addEventListener('DOMContentLoaded', () => {

    const mainContent = document.querySelector('.main-content');
    if (!mainContent) return;

    const contentId = mainContent.dataset.contentId;
    if (!contentId) return;

    // [수정] 쿠키 검사 없이, 그냥 API 호출
    callViewCountApi(contentId);
});

const callViewCountApi = async (contentId) => {
    try {
        await fetch(`/api/contents/${contentId}/view`, {
            method: 'POST'
        });

    } catch (error) {
        console.error('View count API call failed:', error);
    }
};