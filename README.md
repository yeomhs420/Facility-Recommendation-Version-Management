## 개발 프로젝트/ 편의시설 길 찾기 서비스


### 프로젝트 소개


- 프로젝트 이름 : SQL 교육 사이트 - SQL Practice
- 프로젝트 설명 : SQL 입문자들에게 SQL의 기본 문법부터 다중 테이블 연산까지의 내용을 교육시켜주고 학습 테스트 및 Q&A 게시판을 제공한다.
- 기술 스택 : SpringBoot, JDK, JPA, Spring AOP, Validation, OpenAPI, MYSQL, SPOCK, mustache
- 사용 에디터 : IntelliJ
- 버전 관리 툴 : Git


## 화면 Architecture

<img src="img/img.png" width="500">


## Recommendation Process

<img src="img/img_2.png" width="500">

- 카카오 주소검색 API 연동하여 주소를 위도, 경도로 변환

- 공공데이터 포털로부터 시설 현황 데이터 추출

- Haversine formula 알고리즘을 통해 두 위도, 경도 사이의 거리 계산

- 추천 결과를 카카오 지도 URL로 연동하여 제공

- redis를 이용하여 성능 최적화


## 사용자 주소 입력 & 시설 위치 반환

<img src="img/img_3.png" width="500">

## 추천 결과를 카카오 지도 URL로 연동하여 제공

<img src="img/img_4.png" width="500">

## Q/A 게시판

<img src="img/img_1.png" width="500">