# Blog Search Service

## 사용한 개발 언어, 프레임워크, 라이브러리
* Kotlin 1.9.22 (`jvmTarget = 21`)
* spring-boot-starter-web:3.2.1
* spring-boot-starter-data-jpa:3.2.1
* spring-cloud-starter-openfeign:4.1.0
  - 외부 API 호출 시 사용했습니다. (Spring Boot 3.2에 도입된 RestClient 대신 사용)
* spring-cloud-starter-circuitbreaker-resilience4j:3.2.1
  - 블로그 검색 소스가 되는 외부 API 장애 시, 서킷 브레이커를 이용해 다른 API로 우회하기 위해 사용했습니다.
* spring-restdocs-restassured:3.0.1
  - API 명세 문서를 만들기 위해 사용했습니다. 컨트롤러 테스트 코드를 통해 문서를 만들 수 있습니다.
* spring-retry:2.0.5
  - 검색 카운트 저장에 낙관적 락을 사용하는데, OptimisticLockException 발생 시 재시도를 위해 사용했습니다.
* com.h2database:h2:2.2.224
  - 키워드 검색 카운트 저장을 위해 사용했습니다.
* com.github.ben-manes.caffeine:caffeine:3.1.8
  - 인기 검색어 목록을 서버 로컬 캐싱하기 위해 사용했습니다.
* org.jasypt:jasypt:1.9.3
  - DB 접속 정보 등을 직접 하드코딩하지 않고 Vault 같은 KMS를 사용해햐는데, 제약상 그럴 수는 없어 Configuration에서 설정될 수 있는 구조만 잡기 위해 사용했습니다.
* io.mockk:mockk:1.13.0
  - 테스트 코드 작성을 위해 사용했습니다.


---
## Executable JAR 실행 방법
- [다운로드 위치 1](https://github.com/lanchodas/blog-search-service/blob/main/jar/blog-search-service.jar)
- [다운로드 위치 2](https://drive.google.com/file/d/1yPmcN8vxHe_QTgq6F9wADhGjo4QZU-Zo/view?usp=sharing)

`다운로드 위치 1`에서 받아지지 않을 경우 `다운로드 위치 2`에서 받아주세요.

다운로드가 완료되면 아래 명령으로 실행할 수 있습니다.
```zsh
  $ java -jar blog-search-service.jar
```

📌 중요! 필수!
> Java 21 버전을 사용해야 합니다.
> asdf 커맨드를 사용하고 계시다면 `asdf local java temurin-21.0.1+12.0.LTS` 명령 등으로 Java 21 버전이 사용될 수 있도록 설정 부탁드립니다.


---
## API 명세서
* [API 명세서](https://lanchodas.github.io/)
 
위 위치가 열리지 않는다면, GitHub의 [api/docs/index.html](https://github.com/lanchodas/blog-search-service/blob/main/api/docs/index.html)을 참고 부탁드립니다.


---
## 새롭게 구현한 것
* 테스트 해볼 수 있는 웹 프론트엔드를 구현했습니다.
  - Executable JAR 실행 후, http://localhost:8080/ 접속
* 검색 키워드 카운트 업데이트를 위해 일시적으로 서버내에 저장 후 주기적으로 낙관적 락을 이용해 저장하도록 구현했습니다.
* 인기 검색어 목록에 로컬 캐시를 적용했습니다.
* 서킷 브레이커를 통해 1차 검색 소스 장애 시 2차 서버로 우회하도록 구현했습니다.
* Spring REST Docs를 이용해 API 명세서를 작성했습니다.
