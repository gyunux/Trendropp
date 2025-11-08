document.addEventListener('DOMContentLoaded', () => {

    // statsData 변수는 stats.html의 th:inline 스크립트에서 가져옵니다.
    if (typeof statsData !== 'undefined' && statsData.length > 0) {

        // 1. 데이터를 차트에 맞게 가공 (Top 10만)
        const top10Data = statsData.slice(0, 10);

        // 관리자용이므로 한국어 이름(nameKo)을 라벨로 사용
        const labels = top10Data.map(stat => stat.nameKo);
        const viewCounts = top10Data.map(stat => stat.totalViewCount);

        // 2. 차트 렌더링
        const ctx = document.getElementById('viewChart');
        if (ctx) {
            new Chart(ctx, {
                type: 'bar', // 막대 차트
                data: {
                    labels: labels,
                    datasets: [{
                        label: '총 조회수',
                        data: viewCounts,
                        backgroundColor: 'rgba(54, 162, 235, 0.6)', // 파란색
                        borderColor: 'rgba(54, 162, 235, 1)',
                        borderWidth: 1
                    }]
                },
                options: {
                    indexAxis: 'y', // [수정] 가로 막대 차트 (셀럽 이름이 길 경우)
                    scales: {
                        x: {
                            beginAtZero: true,
                            ticks: {color: '#e0e0e0'} // X축 레이블 색상
                        },
                        y: {
                            ticks: {color: '#e0e0e0'} // Y축 레이블 색상
                        }
                    },
                    plugins: {
                        legend: {
                            labels: {color: '#e0e0e0'} // 범례 색상
                        }
                    }
                }
            });
        }
    }
});