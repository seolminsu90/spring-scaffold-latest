## Spring boot 기반 기본 프로젝트

인증, 권한, 예외처리, 로그백, 데이터소스, 쿼리매퍼, 롬복

프로젝트 스케폴딩 시 항상 신규 버전으로 구축하고, 기존 스케폴드 코드는 참조만 한다.   
신규 버전 구축 시 라이브러리, 의존성의 큰 업데이트 발생 시 메모하거나 샘플 프로젝트 작성하기.   

- Datasource Routing 예시는 3버전 이상의 최신은 [이거][ref1]참고하기. (Atomikos가 부트3 기반으로 만들ㅁㅇ어진지 얼마 안됬는데, 쓸 때 쯤이면 새 버전 나오긴 할 듯)
- Datasource Routing 필요 시 위 신규 구현과 필요시 [이전버전][ref2] 의 구현 참조하여 개발


---
## Spring scaffold authenfication gateway

게이트웨이가 모든 권한처리까지 하는 구조일 때 DB기반 동적 라우팅 로드 샘플
Spring cloud gateway + dynamic routing sample

- Spring boot 3.2
- jdk 21
- netty reactor 기반 security 샘플
- 동적 route source
- 기타 설정은 생략함
- spring data r2dbc 관련 메모

## Spring scaffold boot3

Spring 3.x 기반의 샘플 모음

- api token 인증 방식 프로젝트 샘플
- Spring boot 3.2
- jdk 21

기본 골격 프로젝트

[ref1]: https://github.com/seolminsu90/simple-atomikos-kotlin
[ref2]: https://github.com/seolminsu90/spring-scaffold-old
