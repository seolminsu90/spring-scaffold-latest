## Spring boot 기반 기본 프로젝트

---
## Spring scaffold authenfication gateway

게이트웨이가 모든 권한처리까지 하는 구조일 때 DB기반 동적 라우팅 로드 샘플
Spring cloud gateway + dynamic routing sample

- netty reactor 기반 security 샘플
- 동적 route source
- 기타 설정은 생략함
- spring data r2dbc 관련 메모

## Spring scaffold boot3

Spring 3.x 기반의 샘플 모음

- token 인증 방식 프로젝트 샘플

## Spring scaffold

Spring 최신 버전 (2.7) 기준으로 이것 저것 만들어봄
게임 운영툴 백앤드 구축을 위한 여러가지 사항들 개발

- 기본 골격
- Spring security rest + jwt
- Route datasource
- Xa Atomikos Transaction
- Mybatis 기반 프로젝트 
- SPA oauth login 예시
- readonly시 _slave 바라보도록 설정

*boot3 기준 풀스택구현은 카뱅과제 참조

### 사용 케이스

1. 보통의 게임 운영툴 페이지는 단일 공통 DB + 여러 하위 게임 DB로 구성되어 있음.
2. 여러 하위 게임 DB의 경우 동적으로 변동되는 경우가 많으니 routedatasource로 구현
3. 변동이 없는 공통 DB의 경우 별도로 분리해서 제공 (별도로 구현해도, xa datasource class로 설정하기만 하면 같은 트랜젝션 참여 하는 것 확인)

--- 

### SPA Oauth 연동 흐름 정리 []는 제 3자의 리소스

SPA의 경우 외부 Oauth 연동 방식이 조금 달라 예시로 개발
```
1. Frontend -> [Oauth Server]
- 로그인 과정을 통해 AuthorizationCode 인가 코드를 발급받는다.(대체로 수명이 짧거나 1회성으로발급)
- *Frontend의 별도 oauth 로그인 처리용 빈 페이지를 redirect_url로 등록한다.

2. Frontend -> Backend
- AuthorizationCode를 백엔드로 전달한다.

3. Backend -> [Oauth Server]
- AuthorizationCode를 이용하여 AccessToken 을 가져온다. 이때 redirect_url은 1에서 설정한 Frontend의 리다이렉트이다.(검증)
- AccessToken를 이용하여 Oauth Server그룹의 [Resource(유저 정보 등)] 을 가져온다.
- [Resource(유저 정보)]를 가지고 서비스 전용 로그인 처리 및 권한 등을 담은 JWT를 발급한다.

4. Backend -> Frontend
- 생성한 JWT를 프론트엔드로 전달한다.
- 설정한 oauth 처리용 빈 페이지에서 JWT관련 저장 처리 후 홈 페이지로 location 이동한다.

Frontend
- 별도의 JWT 저장 및 api에 사용
```

### XA datasource Mysql의 경우 추가 옵션 필요
```
pinGlobalTxToPhysicalConnection=true 매개변수를 jdbc URL에 추가하거나 com.atomikos.jdbc.AtomikosDataSourceBean Spring 정의 내의 매개변수로 추가

<bean id="dataSource" class="com.atomikos.jdbc.AtomikosDataSourceBean"
        init-method="init" destroy-method="close">
...
<property name="xaProperties">
   <props>
      <prop key="pinGlobalTxToPhysicalConnection">true</prop>  
   </props>     
</property>
</bean>
```
*scaffold2에 옵션 안줬는데 잘 되는 것 같은데 뭐지..*


### 메모

- H2 대소문자 구분 처리 
```yml
jdbc:h2:mem:testdb;MODE=MySQL;DATABASE_TO_UPPER=false
```
---

## Spring scaffold2

Mybatis가 아닌 JPA 기반의 분산트랜젝션 테스트
JPA로 분산트랜젝션은 잘 되나, RouteDatasource로 JPA를 썼을 때 잘 안됨.
Lookup Key를 변경해줘도 처음 가져온 Datasource를 재사용 해버림.
open-in-view 설정을 false 처리하면 재사용 안하긴 하는데, 이럼 또 트랜젝션이 안됨

- JPA(Datasource - 단일) + Mybatis(RouteDataSource - 다중)으로 구성
- JPA LocalContainerEntityManagerFactoryBean 가 분리되야 정상적으로 트랜젝션이나 datasource switching을 유도할 것 같은데 routeDatasource를 쓰면 잘 안된다. (자꼬 처음 맺은 커넥션 재사용 해버림)
- 실무에선 단일 DB들(CommonDB같은)은 JPA로 관리하고 다중 DB(GameDB)는 Mybatis로 해야 할거같다. (지금 기준으로는)
  - 찾아봐도 JPA + JTA(Atomikos) + RouteDataSource 쓰는 명확한 해답도 없는듯. (별로 일반적이지 않나 봄.)

### 테스트용 db

- 보기 편하려고 메모리db 말고 로컬에 db 구축해서 사용함
```bash
CREATE TABLE `test` (
`seq` INT(10) NOT NULL AUTO_INCREMENT,
`name` VARCHAR(50) NULL DEFAULT NULL COLLATE 'utf8mb4_unicode_ci',
PRIMARY KEY (`seq`) USING BTREE
)
;
```

---

### atomikos properties 메모
```bash
spring.jta.atomikos.connectionfactory.borrow-connection-timeout=30 # 풀에서 연결을 빌리기 위한 시간 초과(초)
spring.jta.atomikos.connectionfactory.ignore-session-transacted-flag=true # 세션 생성 시 트랜잭션 플래그 무시 여부
spring.jta.atomikos.connectionfactory.local-transaction-mode=false # 로컬 트랜잭션을 원하는지 여부
spring.jta.atomikos.connectionfactory.maintenance-interval=60 # 풀의 유지 관리 스레드 실행 사이의 시간(초)
spring.jta.atomikos.connectionfactory.max-idle-time=60 # 연결이 풀에서 정리된 후의 시간(초)
spring.jta.atomikos.connectionfactory.max-lifetime=0 # 연결이 끊어지기 전에 풀링될 수 있는 시간(초). 0은 제한이 없음을 나타냅니다.
spring.jta.atomikos.connectionfactory.max-pool-size=1 # 풀의 최대 크기
spring.jta.atomikos.connectionfactory.min-pool-size=1 # 풀의 최소 크기
spring.jta.atomikos.connectionfactory.reap-timeout=0 # 빌린 연결에 대한 수확 시간 초과(초). 0은 제한이 없음을 나타냅니다.
spring.jta.atomikos.connectionfactory.unique-resource-name=jmsConnectionFactory # 복구 중 리소스를 식별하는 데 사용되는 고유 이름
spring.jta.atomikos.datasource.borrow-connection-timeout=30 # 풀에서 연결을 빌리기 위한 시간 초과(초)
spring.jta.atomikos.datasource.default-isolation-level= # 풀에서 제공하는 연결의 기본 격리 수준
spring.jta.atomikos.datasource.login-timeout= # 데이터베이스 연결을 설정하기 위한 시간 초과(초)
spring.jta.atomikos.datasource.maintenance-interval=60 # 풀의 유지 관리 스레드 실행 사이의 시간(초)
spring.jta.atomikos.datasource.max-idle-time=60 # 연결이 풀에서 정리된 후의 시간(초)
spring.jta.atomikos.datasource.max-lifetime=0 # 연결이 끊어지기 전에 풀링될 수 있는 시간(초). 0은 제한이 없음을 나타냅니다.
spring.jta.atomikos.datasource.max-pool-size=1 # 풀의 최대 크기
spring.jta.atomikos.datasource.min-pool-size=1 # 풀의 최소 크기
spring.jta.atomikos.datasource.reap-timeout=0 # 빌린 연결에 대한 수확 제한 시간(초). 0은 제한이 없음을 나타냅니다.
spring.jta.atomikos.datasource.test-query= # 연결을 반환하기 전에 연결을 확인하는 데 사용되는 SQL 쿼리 또는 문
spring.jta.atomikos.datasource.unique-resource-name=dataSource # 복구하는 동안 리소스를 식별하는 데 사용되는 고유한 이름
```


#### scaffold3

- Spring boot 3.0 
- jdk 17 버전
- QueryDSL 
- JUnit5 mokito 기본 테스트 포함

기본 골격 프로젝트
