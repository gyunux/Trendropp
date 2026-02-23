# 🛍️ TrenDropp
> **AI 기반 패션 트렌드 콘텐츠 자동화 및 큐레이션 플랫폼**
> 
> 트렌디한 셀럽들의 패션 기사를 크롤링하고, Gemini AI를 활용해 콘텐츠를 자동 요약 및 번역하여 제공하는 백엔드 중심의 서비스입니다.

🔗 **서비스 접속 도메인:** [https://trendropp.xyz](https://trendropp.xyz)

---

## 🛠️ Tech Stack

### Backend
* **Language:** Java 21
* **Framework:** Spring Boot 3.5.6, Spring Data JPA
* **Build Tool:** Maven

### Database & AI
* **Database:** MySQL 8.0
* **AI Model:** Google Gemini 2.5 Flash API (콘텐츠 요약 및 번역 자동화)

### Infrastructure & Frontend
* **Server:** AWS -> Oracle Cloud (Ubuntu)
* **Container:** Docker (MySQL)
* **Frontend:** HTML, CSS, Vanilla JavaScript, Thymeleaf

---

## 🎬 핵심 기능 시연 (Admin Dashboard)

관리자 페이지에서 AI 기반 크롤링 및 데이터를 등록하는 핵심 파이프라인 시연 영상입니다.

### 1. 셀럽 데이터 추가
> 새로운 셀럽의 기본 정보를 데이터베이스에 등록합니다.
![셀럽 시연](https://github.com/user-attachments/assets/ad723e0c-b5ef-4c1c-a827-225f24c6ea83)


### 2. 브랜드 데이터 추가 및 검색
> 패션 브랜드 정보를 추가합니다.
![브랜드 시연](https://github.com/user-attachments/assets/3582575c-469d-4e6a-8999-35253abc5923)


### 3. 기사 크롤링 및 AI 자동 요약 (Gemini AI)
> 타겟 패션 기사 URL을 입력하면 크롤러가 본문을 추출하고, Gemini AI API가 실시간으로 내용을 분석하여 한국어/영어 요약본을 자동 생성합니다.
![크롤링 시연](https://github.com/user-attachments/assets/b4790649-75fc-4645-abfd-2404b46c85a9)


### 4. 최종 콘텐츠 생성 및 발행
> AI가 요약한 텍스트와 추출된 브랜드/셀럽 데이터를 조합하여 최종 서비스용 콘텐츠를 퍼블리싱합니다.
![콘텐츠 시연](https://github.com/user-attachments/assets/1d53204e-712b-46b4-95c9-08a959ef9336)


---

## 💡 Trouble Shooting (핵심 문제 해결 경험)

### 1. [Infrastructure] 크롤러 분리를 통한 EC2 OOM(Out Of Memory) 해결 및 비용 최적화
* **문제 상황:** 1GB RAM(t3.micro) 환경에서 Chrome Driver 실행 시 메모리 임계치를 초과하여 EC2 OOM으로 인한 서버 다운 현상 발생,API 서비스(Web)와 무거운 크롤링 작업(Crawler)이 좁은 메모리를 공유하는 구조가 원인.
* **해결 과정:** 아키텍처 분리 결정. 운영 서버(AWS EC2)는 Nginx와 Spring Boot API만 구동하여 조회 서비스에 집중하도록 구성. 크롤링 워커(Worker)는 로컬 환경으로 분리하여 실행 후 RDS와 S3에 데이터를 적재하도록 파이프라인 변경.
* **개선 결과:** 평균 메모리 사용량을 500MB 이하(여유 공간 50%)로 안정화하여 24시간 무중단 API 서비스 환경을 구축함. 고사양 스케일업(Scale-up) 대신 구조적 개선을 택해 프리티어 내에서 서버 비용 0원 유지 달성.

---

### 2. [Database] 엔티티 정규화 및 QueryDSL 도입으로 N+1 문제 해결 (응답속도 88% 개선)
* **문제 상황:** 관리자 페이지 목록 진입 시 간헐적 응답 대기 현상 발생. 기존 이미지 URL이 `@ElementCollection`으로 매핑되어 있어, 게시글 1개를 조회할 때마다 이미지 조회 쿼리가 추가로 실행되는 N+1 문제 확인.
* ]**해결 과정:** 이미지를 별도 엔티티로 분리(정규화)하여 OneToMany 관계로 재설계함[cite: 56]. 문자열 기반의 JPQL 대신 Type-Safe한 QueryDSL을 도입하여 동적 쿼리 관리의 안정성을 높임. QueryDSL을 활용해 목록 조회 시 필요한 썸네일 1장만 선별적으로 조회하도록 쿼리 최적화 수행.
* **개선 결과:** 기존 1+N번 발생하던 쿼리를 단 1번의 쿼리로 감소시킴. 불필요한 전체 이미지 데이터 로딩을 제거하여 메모리 효율을 개선함. API 평균 응답 시간을 2.5초에서 0.3초로 대폭 단축함.
