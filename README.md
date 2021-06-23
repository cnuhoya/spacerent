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

# 분석/설계


## Event Storming 결과
* MSAEz 로 모델링한 이벤트스토밍 결과: http://www.msaez.io/#/storming/QFPJP8hKmlRp2MfooEVdmMfG9B72/mine/ccf667a1ea140f64e4144c2628864dfd

# 구현
분석/설계 단계에서 도출된 헥사고날 아키텍처에 따라,구현한 각 서비스를 로컬에서 실행하는 방법은 아래와 같다.
(각자의 포트넘버는 8081 ~ 8084, 8088 이다.)
```shell
cd book
mvn spring-boot:run

cd payment
mvn spring-boot:run 

cd space
mvn spring-boot:run 

cd mypage 
mvn spring-boot:run

cd gateway
mvn spring-boot:run 
```
***

## DDD 의 적용

- 각 서비스내에 도출된 핵심 Aggregate 단위로 Entity 로 선언하였다. 
- Spring Data REST의 RestRepository 적용 ( Entity / Repository Pattern 적용 위해 )

```
book 서비스 : book.java

package spacerent;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.Date;

@Entity
@Table(name="Book_table")
public class Book {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long bookid;
    private Long userid;
    private String status;
    private String spacename;

    @PostPersist
    public void onPostPersist(){
        Booked booked = new Booked();
        BeanUtils.copyProperties(this, booked);
        booked.publishAfterCommit();

        spacerent.external.Payment payment = new spacerent.external.Payment();
        payment.setBookid(booked.getBookid());
        payment.setSpacename(booked.getSpacename());
        payment.setStatus("booking");
        payment.setUserid(booked.getBookid());
 );


    }

    @PostUpdate
    public void onPostUpdate(){
         
        System.out.println("\n\n##### app onPostUpdate, getStatus() : " + getStatus() + "\n\n");
        if(getStatus().equals("cancel-booking")) {
            Bookcancelled bookcancelled = new Bookcancelled();
            BeanUtils.copyProperties(this, bookcancelled);
            bookcancelled.publishAfterCommit();
            
            spacerent.external.Payment payment = new spacerent.external.Payment();
            payment.setBookid(bookcancelled.getBookid());
            payment.setSpacename(bookcancelled.getSpacename());
            payment.setStatus("cancel-booking");
            payment.setUserid(bookcancelled.getBookid());
);            
        }        


    }


    public Long getBookid() {
        return bookid;
    }

    public void setBookid(Long bookid) {
        this.bookid = bookid;
    }
    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getSpacename() {
        return spacename;
    }

    public void setSpacename(String spacename) {
        this.spacename = spacename;
    }

    public class findByBookId {
    }

}
```
- book 서비스 : BookRepository.java
```
package spacerent;

import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel="books", path="books")
public interface BookRepository extends PagingAndSortingRepository<Book, Long>{

      Book findByBookId(Long bookid);
      
}
```

book 서비스 : PolicyHandler.java
```
package spacerent;

import spacerent.config.kafka.KafkaProcessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class PolicyHandler{
    @Autowired BookRepository bookRepository;

    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverRegistered_Updatestate(@Payload Registered registered){

        if(!registered.validate()) return;

        System.out.println("\n\n##### listener Updatestate : " + registered.toJson() + "\n\n");

        // booking 성공 상태 저장  //
        Book book = bookRepository.findByBookId(registered.getBookid());
        book.setStatus(registered.getStatus());
        bookRepository.save(book);
            
    }
    @StreamListener(KafkaProcessor.INPUT)
    public void wheneverRegistercancelled_Updatestate(@Payload Registercancelled registercancelled){

        if(!registercancelled.validate()) return;

        System.out.println("\n\n##### listener Updatestate : " + registercancelled.toJson() + "\n\n");

        // booking 취소 상태 저장  //
        Book book = bookRepository.findByBookId(registercancelled.getBookid());
        book.setStatus(registercancelled.getStatus());
        bookRepository.save(book);
            
    }


    @StreamListener(KafkaProcessor.INPUT)
    public void whatever(@Payload String eventString){}


}
```
- 적용 후 REST API 의 테스트
```
# 공간 예약(booking)
http POST http://localhost:8081/book userid="CHOI" bookid="1" spacename="numberone" status="booking"

# 결제 확인(payment)
http GET http://localhost:8084/payment/1 

# 공간 등록(register)
http PETCH http://localhost:8082/space/1 status="registered"

# 공간 예약 취소(booking)
http POST http://localhost:8081/book userid="CHOI" bookid="1" spacename="numberone" status="cancel-booking"

# 결제 확인(payment)
http GET http://localhost:8084/payment/1 

# 공간 취소(register)
http PETCH http://localhost:8082/space/1 status="registercancelled"

```

## Gateway 적용
API GateWay를 통하여 마이크로 서비스들의 진입점을 통일할 수 있다. 
아래와 같이 GateWay를 적용하여 마이크로서비스들은 http://localhost:8088/{context}로 접근 .

```
server:
  port: 8088

---

spring:
  profiles: default
  cloud:
    gateway:
      routes:
        - id: book
          uri: http://localhost:8081
          predicates:
            - Path=/books/** 
        - id: space
          uri: http://localhost:8082
          predicates:
            - Path=/spaces/** 
        - id: payment
          uri: http://localhost:8083
          predicates:
            - Path=/payments/** 
        - id: mypage
          uri: http://localhost:8084
          predicates:
            - Path= /mypages/**
      globalcors:
        corsConfigurations:
          '[/**]':
            allowedOrigins:
              - "*"
            allowedMethods:
              - "*"
            allowedHeaders:
              - "*"
            allowCredentials: true


---

spring:
  profiles: docker
  cloud:
    gateway:
      routes:
        - id: book
          uri: http://book:8080
          predicates:
            - Path=/books/** 
        - id: space
          uri: http://space:8080
          predicates:
            - Path=/spaces/** 
        - id: payment
          uri: http://payment:8080
          predicates:
            - Path=/payments/** 
        - id: mypage
          uri: http://mypage:8080
          predicates:
            - Path= /mypages/**
      globalcors:
        corsConfigurations:
          '[/**]':
            allowedOrigins:
              - "*"
            allowedMethods:
              - "*"
            allowedHeaders:
              - "*"
            allowCredentials: true

server:
  port: 8080

```

## CQRS
타 마이크로서비스의 데이터 원본에 접근없이(Composite 서비스나 조인SQL 등 없이)도 내 서비스의 공간 예약 내역 조회가 가능하게 구현해 두었다.
본 프로젝트에서 View 역할은 mypage 서비스가 수행한다.

CQRS를 구현하여 마이페이지를 통해 조회할 수 있도록 구현

## Polyglot 

타 서비스들과 다른 DB를 사용하여 각 마이크로서비스의 다양한 요구사항과 서로 다른 종류의 DB간에도 문제 없이 능동적으로 대처가능한 다형성을 만족하는지 확인

## 동기식 호출


# 운영

## Deploy / Pipeline
  
