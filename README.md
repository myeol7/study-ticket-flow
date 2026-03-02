<div align="center">

# 🎟️ TicketFlow

대규모 트래픽 상황에서의 동시성 제어 및 상태 중앙 통제 기반 티켓팅 시스템

[<img src="https://img.shields.io/badge/GitHub-TicketFlow-black?style=flat&logo=github&logoColor=white" />](https://github.com/myeol7/study-ticket-flow)
<br/>
<img src="https://img.shields.io/badge/프로젝트 기간-2025.12 ~ 진행 중-green?style=flat"/>

</div>

---

## 📝 프로젝트 개요

- 개인 사이드 프로젝트
- 100명의 유저가 동시에 하나의 좌석을 요청할 때, 그리고 사용자가 결제 전 이탈하거나 변심하여 취소할 때. **어떠한 장애 상황에서도 데이터의 정합성과 상태의 무결성을 보장하는 백엔드 시스템**을 목표로 구축했습니다.

이 프로젝트는 화려한 기술의 나열(오버엔지니어링)을 지양합니다.
대신 **"물리적 리소스(Seat)와 권리(Reservation)의 분리"**, **"도메인 엔티티 내 상태 전이 로직 캡슐화"** 등 탄탄한 기본기를 바탕으로, **실제 운영 현장에서 발생하는 복잡한 상태 관리와 동시성 이슈를 직접 통제해보는 것**에 집중했습니다.

---

## ⚙️ 맡은 역할

- 백엔드 아키텍처 및 도메인 모델 설계
- RDBMS 락(Lock)을 활용한 동시성 제어 로직 구현
- 스케줄러를 활용한 배치 처리 및 상태 환원 시스템 구축
- 운영자 관점의 실시간 좌석 현황 대시보드(UI) 구현

---

## ⚙ 기술 스택

### Back-end
![Java](https://img.shields.io/badge/Java%2017-007396?style=for-the-badge&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot%203.x-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Data JPA](https://img.shields.io/badge/Spring%20Data%20JPA-6DB33F?style=for-the-badge&logo=spring&logoColor=white)

### Database
![H2](https://img.shields.io/badge/H2%20Database-003545?style=for-the-badge) *(현재)*
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white) *(도입 예정)*

### Front-end
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-005F0F?style=for-the-badge&logo=thymeleaf&logoColor=white)
![JavaScript](https://img.shields.io/badge/JavaScript-F7DF1E?style=for-the-badge&logo=javascript&logoColor=black)

---

## 🤔 핵심 의사결정 및 트러블슈팅

### 1. 100명 동시 요청 시 발생하는 Race Condition 방어 (동시성 제어)
- **문제:** 인기 좌석에 다수의 유저가 동시에 예약(`Hold`)을 시도할 경우, 초과 예약이 발생하는 문제.
- **해결:** RDBMS의 **비관적 락(Pessimistic Lock, `SELECT ... FOR UPDATE`)**을 적용하여 트랜잭션을 직렬화.
- **선택의 이유:** 낙관적 락(`@Version`) 도입 시 충돌이 빈번한 티켓팅 도메인 특성상 수많은 예외 처리와 롤백 비용이 발생합니다. 대기열을 만들어 선착순을 확실히 보장하는 비관적 락이 비즈니스 요구사항에 더 부합한다고 판단했습니다.

### 2. 이중 취소 방지 및 Soft Delete 기반 쿼리 튜닝
- **문제:** 관리자의 동시 취소 요청 시 발생하는 이중 상태 전이(이중 환불) 위험 및 `CANCELED` 데이터 누적으로 인한 현황판 쿼리 장애 위험.
- **해결:** 취소 로직 실행 전 비관적 락을 걸어 방어하고, 현황판 조회를 부정형(`status != EXPIRED`)에서 명시적 조회(`status IN (HELD, CONFIRMED)`)로 변경하여 데이터베이스 풀 스캔 위험을 낮춤.

### 3. 미결제 이탈 사용자의 좌석 자동 환원 (스케줄러)
- **문제:** 예약(`HELD`) 후 결제를 진행하지 않고 이탈 시, 해당 좌석이 영구적으로 잠기는 현상.
- **해결:** `@Scheduled`를 활용한 시스템 배치 작업을 도입하여, 만료 시간(`expiredAt`)이 초과된 예약을 주기적으로 찾아 `EXPIRED`로 일괄 상태 전이 및 재고 환원 처리.

---

## 🚀 향후 발전 계획 (Future Work)

현재는 단일 서버와 RDBMS의 기능만으로 시스템을 안전하게 통제하고 있지만, 대규모 트래픽 확장을 대비하여 다음과 같은 아키텍처 고도화를 계획하고 있습니다.

1. **Redis 기반 분산 락(Distributed Lock) 도입:** - 다중 서버 환경(Scale-out)에서 DB 커넥션 고갈을 방지하기 위해, 락 관리의 주체를 RDBMS에서 Redis로 이관.
2. **조회 성능 최적화를 위한 캐싱(Caching) 레이어 추가:**
   - 읽기 요청(좌석 조회)이 압도적으로 많은 도메인 특성을 고려하여, Look-aside 패턴의 Redis 캐싱을 통해 DB 부하 분산.
3. **Kafka를 활용한 비동기 이벤트 처리 (Saga Pattern):**
   - 예약 확정 시 발생하는 외부 연동(결제 승인, 알림톡 발송)을 비동기 메시지 큐로 분리하여 응답 속도 향상 및 시스템 간 강결합(Coupling) 해소.

---

## 💁‍♂️ 프로젝트 팀원

| 역할 | 이름 |
|------|------|
| Backend | 김윤환 |
