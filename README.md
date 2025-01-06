![header](https://capsule-render.vercel.app/api?type=waving&color=gradient&height=300&section=header&text=ECLIPSE&fontSize=90&fontAlignY=40&desc=Elice%20Cloud%20Track%205기&descAlign=70)

## 💬 설명

> 의류 쇼핑몰 Eclipse입니다.

## ⚙️ 기술 스택

### 백엔드

<img src="https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white">
<img src="https://img.shields.io/badge/SpringBoot-6DB33F?style=for-the-badge&logo=spring&logoColor=white">
<img src="https://img.shields.io/badge/JdbcTemplate-6DB33F?style=for-the-badge&logo=spring&logoColor=white">
<img src="https://img.shields.io/badge/Spring%20Data%20JPA-6DB33F?style=for-the-badge&logo=spring&logoColor=white">
<img src="https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=spring&logoColor=white">
<img src="https://img.shields.io/badge/H2%20Database-003545?style=for-the-badge&logo=h2&logoColor=white">

### 프론트엔드

<img src="https://img.shields.io/badge/HTML-239120?style=for-the-badge&logo=html5&logoColor=white">
<img src="https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=JavaScript&logoColor=white">
<img src="https://img.shields.io/badge/HTML5-E34F26?style=for-the-badge&logo=HTML5&logoColor=white"/>
<img src="https://img.shields.io/badge/css-1572B6?style=for-the-badge&logo=css3&logoColor=white">
<img src="https://img.shields.io/badge/Bootstrap-563D7C?style=for-the-badge&logo=bootstrap&logoColor=white">
<img src="https://img.shields.io/badge/thymeleaf-005F0F?style=for-the-badge&logo=thymeleaf&logoColor=white"/>



## 🧩 ERD

![eclipseERD](./assets/eclipseERD.png)


## ⛓️ 와이어 프레임

![eclipseWAF](./assets/eclipseWAF.png)


## 🔥 주요기능

### **1. 회원**

- **회원가입**
    - 이메일 형식 아이디
    - OAUTH
        - 카카오
        - 구글
    - 일반 회원가입
        - 이메일 인증
- **마이 페이지**
    - 포인트
    - 주문 내역
        - 상품 준비중, 배송시작, 배송완료 등
    - 장바구니
    - 포인트
    - 찜한 상품
        - 관심(좋아요): 카테고리별로 저장
    - 내 정보 수정
        - 비밀번호
        - 전화번호
- 권한
    - 일반 회원
    - 관리자
        - 모든 회원의 주문 내역 조회 가능
        - 배송 상태 변경
- 맴버십 등급
    - 브론즈
        - 기본 등급
    - 실버
        - 전월 결제금액 20만원 이상
        - 매월 5000포인트 지급
    - 골드
        - 전월 결제금액 50만원 이상
        - 매월 10000포인트 지급
    - 다이아몬드
        - 전월 결제 금액 100만원 이상
        - 매월 50000포인트 지급

### **2. 카테고리**
### **3. 상품**

- **카테고리화**
    - 장소, 항목 카테고리 분류 (ex 상의-니트/스웨터)
- **상품 상세**
    - 사이즈
    - 색상
    - 제조사
- **상품 목록 정렬**
    - 최신순(default)
    - 조회수 순
    - 평점 높은/낮은순
    - 가격 높은/낮은순

### **4. 장바구니**
### **5. 결제**
### **6. 주문**

- **결제수단**
    - 카드, 계좌이체 등 선택
    - 포인트 결제
        - 만료기간이 가장 적게 남은 포인트 우선 사용
- **배송**
    - 주소는 우편번호, 도로명 주소, 상세주소로 구성
    - 배송사와 송장번호 설정
    - 요청사항은 선택사항으로 입력
- **장바구니**
    - 주문은 장바구니를 통해서만 가능
    - 수량 조정, 일부 삭제, 전체 삭제


### **7. 포인트**
- 포인트
    - 주문할 시에 포인트 사용 가능
    - 만료일자가 지날 시 자동으로 소멸
### **8. 리뷰**
- **후기**
    - 색상(어두움,같음, 밝음), 사이즈(작음, 큼) 선택
    - 회원만 등록 가능
    - 0.5 ~ 5점 점수 작성 필수
    - 내용, 이미지 작성 선택
    - 텍스트리뷰는 100포인트, 사진리뷰는 200포인트 지급
### **9. 문의**
- **문의**
    - 상품 문의
        - 상품 게시판에 문의 글 작성 및 답변
    - 서비스 문의
        - 실시간 채팅
### **10. 채팅**

# 👀 실행 화면


# 🛠️ 커밋 컨벤션

* 타입은 태그와 제목으로 구성되고, 태그는 영어로 쓰되 첫 문자는 대문자로 한다.
* "태그: 제목"의 형태이며, : 뒤에만 공백이 있음에 유의

|  태그 이름   |                설명                |
|:--------:|:--------------------------------:|
|   Feat   |          새로운 기능을 추가할 경우          |
|   Fix    |            버그를 고친 경우             |
|  Design  |       CSS 등 사용자 UI 디자인 변경        |
|  Style   | 코드 포맷 변경, 세미 콜론 누락, 코드 수정이 없는 경우 |
| Refactor |           프로덕션 코드 리팩토링           |
|   Docs   |           	문서를 수정한 경우            |
|  Rename  |     	파일 or 폴더명 수정하거나 옮기는 경우      |    
