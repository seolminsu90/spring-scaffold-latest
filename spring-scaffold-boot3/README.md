## Spring Boot3 Security Token Authentication

- Spring boot 3 버전에서의 서비스 토큰 인증 방식을 구현해놓은 샘플

### 실행

```
./gradlew build
docker-compose up
```

> Image Build에 소스파일 복제, gradle빌드도 같이 하려면 별도 도커파일 참조
> docker build -f Dockerfile_WithBuild .

### 토큰 발급

GET http://localhost:8080/tokens

### 권한이 없이 가능한 요청

GET http://localhost:8080/greeting

### 권한이 필요한 요청

GET http://localhost:8080/greeting/user
X-Service-Token:
eyJhbGciOiJIUzUxMiJ9.eyJpZCI6IlNhbXBsZVVzZXIiLCJhdXRoTGlzdCI6WyJST0xFX1VTRVIiXSwiZXhwIjoxNzA4NTY0OTk2fQ._
OZt6ov_p5HKmb5efL-MrIyIoILv91TcQXIzMMbVm4KJcz6Ax7iEO_tOyMaOdJ-5wuQUQFcjfqyZvkmgpZ1inQ

#### 기타

Frontend를 백엔드와 소스통합 하고 빌드+배포에 합칠 경우

```groovy
if (project.hasProperty('frontend')) { // 루트에 frontend gradle 플젝 포함
    println "[-Pfrontend=true] 로 동작하여 프론트엔드 소스를 백엔드에 넣는 작업을 진행합니다."

    task copyFrontendBuildToBackendStatic(type: Copy) {
        description = 'Frontend빌드 결과물을 Backend에서 Host하기 위한 카피작업'

        from "frontend/dist/"
        into "src/main/resources/static"
    }

    copyFrontendBuildToBackendStatic.dependsOn(':frontend:frontendBuild')
    processResources.dependsOn(copyFrontendBuildToBackendStatic)
} else {
    println "프론트엔드 빌드를 백엔드에 포함하지 않습니다."

    task deleteStaticDirectory(type: Delete) {
        description = 'Static 폴더 삭제'

        delete "src/main/resources/static"
    }

    build.dependsOn(deleteStaticDirectory)
}
```

> 프론트 통합과 자세한 건 카뱅과제 참조
