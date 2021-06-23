# spacerent
# spacerent 공강대여 
- 체크포인트 : https://workflowy.com/s/assessment-check-po/T5YrzcMewfo4J6LW

# Table of contents
  - [서비스 시나리오](#서비스-시나리오)
  - [분석/설계](#분석설계)
    - [Event Storming 결과](#Event-Storming-결과)
    - [헥사고날 아키텍처 다이어그램 도출](#헥사고날-아키텍처-다이어그램-도출)
  - [구현:](#구현:)
    - [DDD 의 적용](#DDD-의-적용)
    - [기능적 요구사항 검증](#기능적-요구사항-검증)
    - [비기능적 요구사항 검증](#비기능적-요구사항-검증)
    - [Saga](#saga)
    - [CQRS](#cqrs)
    - [Correlation](#correlation)
    - [GateWay](#gateway)
    - [Polyglot](#polyglot)
    - [동기식 호출(Req/Resp) 패턴](#동기식-호출reqresp-패턴)
    - [비동기식 호출 / 시간적 디커플링 / 장애격리 / 최종 (Eventual) 일관성 테스트](#비동기식-호출--시간적-디커플링--장애격리--최종-eventual-일관성-테스트)
  - [운영](#운영)
    - [Deploy / Pipeline](#deploy--pipeline)
    - [Config Map](#configmap)
    - [Secret](#secret)
    - [Circuit Breaker와 Fallback 처리](#circuit-breaker와-fallback-처리)
    - [오토스케일 아웃](#오토스케일-아웃)
    - [Zero-downtime deploy (Readiness Probe) 무정지 재배포](#zero-downtime-deploy-readiness-probe-무정지-재배포)
    - [Self-healing (Liveness Probe))](#self-healing-liveness-probe)

# 서비스 시나리오

기능적 요구사항
1. 고객이 공간을 예약(booking)한다.
2. 고객 예약(booking)을 하게되고 예약(booking)에 성공하면 결제모듈(payment)에 접수된다.
3. 결제모듈(payment)에 결제를 진행하게 되고 '승인'처리 된다.
4. 결제 '승인' 처리가 되면 공간관리(space)에서 결제 승인 데이터를 받아와 공간 사용 등록(register)을 하게 해준다.
5. 고객이 마이페이지를 통해 예약 상태(booking)를 확인할 수 있다.
6. 고객이 예약(booking)을 취소할 수 있다.

비기능적 요구사항
1. 트랜잭션
    1. 결제가 되지 않은 예약건은 등록이 성립되지 않는다. - Sync 호출
2. 장애격리
    1. 등록이 수행되지 않더라도 예약과 결제는 365일 24시간 받을 수 있어야 한다  - Async(event-driven), Eventual Consistency
    2. 결제 시스템이 과중되면 예약(booking)을 잠시 후 처리하도록 유도한다  - Circuit breaker, fallback
3. 성능
    1. 마이페이지에서 예약상태(booking) 확인 가능  - CQRS
