[정리한 블로그 바로가기](https://choiseonjae.github.io/spring%20boot/logback/logback/)

## logback이란?

logback이란, spring boot의 기본 로그 객체입니다. slf4j의 구현체가 logback입니다.
java.util.logging, log4j, log4j2보다 성능적으로 훌륭한 log 객체입니다.
spring boot의 기본 모델로 되어 있기 때문에 따로 의존성을 추가하지 않아도 됩니다.

> spring-boot-starter-web 안에 logback이 들어있습니다.

## logback으로 로그 관리하기

log를 통해서 application에서 일어나는 일을 기록할 수 있습니다. 이를 통해서, 실시간 혹은 과거의 일을 역으로 트래킹할 수 있습니다. 그렇기 때문에 적절한 로그 레벨 과 로깅 위치를 정하는 일이 중요합니다.

### logback으로 설정가능한 것들

logback으로는 다음과 같은 것들을 설정할 수 있습니다.

- 로그를 어떤 방식으로 출력할 것인가? (콘솔, 파일, 외부에 네트워크로 전달할지)
- 로그를 어떤 포멧으로 출력할 것인가? (로그가 출력되는 형식)
- 어떤 레벨 이상의 로그를 출력할 것인가? (INFO 레벨로 설정할 경우, TRACE & DEBUG LEVEL 로그는 무시됨)

❗ spring profile 별로 다르게 설정할 수 있습니다. (local, staging, production etc..)

위의 설정들을 한 뒤 로그를 남기면, ELK stack 등을 통해 모니터링 도구를 이용해 검색 등을 할 수도 있습니다.

## logback 따라하기

먼저, logback을 이용하기 위해서 build.gradle에 의존성을 추가해 줍니다.

```java
  implementation("org.springframework.boot:spring-boot-starter-web")

	// log
	implementation("org.slf4j:slf4j-api:1.7.30")
	implementation("org.slf4j:jcl-over-slf4j:1.7.30")

	// lombok
	compileOnly("org.projectlombok:lombok:1.18.18")
	annotationProcessor("org.projectlombok:lombok:1.18.18")
```

logback의 정보가 적혀있을 logback.xml의 파일 위치를 `application.yml`에 적어줍니다.

`logging.config` 에 적힌 path의 파일을 읽어서 logback이 설정됩니다.

```yaml
spring:
  profiles:
    active: local
logging:
  config: classpath:logback/logback-${spring.profiles.active}.xml

// 위, 혹은 아래와 같이 둘중 하나만 적습니다.
logging:
  config: classpath:logback/logback-local.xml
```

작성한 `resources/logback/logback-local.xml`의 위치에 파일을 생성합니다.

### 콘솔에 출력하기

출력되는 방식에는 여러가지가 있지만 콘솔에 출력하는 예제를 작성하겠습니다.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
  <!-- 출력되고자 하는 방식 -->
  <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
    <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
      <!-- 출력 포멧 -->
      <pattern>[%d{yyyy:MM:dd HH:mm:ss.SSS}] [%-5level] [%thread] [%C{36}] [%M] [%line] [%mdc]- %msg %n</pattern>
    </encoder>
  </appender>

  <logger name="org.springframework" level="INFO"/>
  <!--controller 패키지는 TRACE 레벨이상만 출력하겠다. -->
  <logger name="com.example.logback.controller" level="TRACE"/>

  <!--모든 패키지의 로그 중 INFO 레벨이상만 출력하겠다. -->
  <root level="INFO">
    <appender-ref ref="CONSOLE"/>
  </root>
</configuration>
```

log를 찍어보기 위해서 controller를 생성해보겠습니다.

```java
@RestController
@Slf4j
public class LogController {

  @GetMapping("/log/trace")
  public ResponseEntity logTrace(){
    MDC.put("Thread name", Thread.currentThread().getName());
    MDC.put("Thread hash", String.valueOf(Thread.currentThread().hashCode()));
    log.trace("TRACE TEST 입니다.");
    return new ResponseEntity(HttpStatus.OK);
  }

  @GetMapping("/log/debug")
  public ResponseEntity logDebug(){
    log.debug("DEBUG TEST 입니다.");
    return new ResponseEntity(HttpStatus.OK);
  }

  @GetMapping("/log/info")
  public ResponseEntity logInfo(){
    MDC.put("Thread name", Thread.currentThread().getName());
    MDC.put("Thread hash", String.valueOf(Thread.currentThread().hashCode()));
    log.info("INFO TEST 입니다.");
    return new ResponseEntity(HttpStatus.OK);
  }
}
```

이런식으로 작성하고, 호출하게 되면 

`[2021:05:24 17:16:22.044] [TRACE] [http-nio-8080-exec-1] [c.e.logback.controller.LogController] [logTrace] [19] [Thread hash=848467257, Thread name=http-nio-8080-exec-1]- TRACE TEST 입니다.`이런 포멧으로 출력되게 됩니다.

xml에 작성한 포멧대로 출력되는 것을 볼 수 있습니다.

`[%d{yyyy:MM:dd HH:mm:ss.SSS}] [%-5level] [%thread] [%C{36}] [%M] [%line] [%mdc]- %msg %n`

- %Logger{length} : Logger name을 축약할 수 있다. {length}는 최대 자리 수, ex)logger{35}
- %-5level : 로그 레벨, -5는 출력의 고정폭 값(5글자)
- %msg : - 로그 메시지 (=%message)
- ${PID:-} : 프로세스 아이디
- %d : 로그 기록시간
- %p : 로깅 레벨
- %F : 로깅이 발생한 프로그램 파일명
- %M : 로깅일 발생한 메소드의 명
- %l : 로깅이 발생한 호출지의 정보
- %L : 로깅이 발생한 호출지의 라인 수
- %thread : 현재 Thread 명
- %t : 로깅이 발생한 Thread 명
- %c : 로깅이 발생한 카테고리
- %C : 로깅이 발생한 클래스 명
- %m : 로그 메시지
- %n : 줄바꿈(new line)
- %% : %를 출력
- %r : 애플리케이션 시작 이후부터 로깅이 발생한 시점까지의 시간(ms)

❗mdc의 경우, `Mapped Diagnostic Context`의 약자로 Key, Value 쌍의 map이라고 생각하면 됩니다.
로그 출력시 지속적으로 출력해야 되는 내용에 대해서 계속 파라미터로 넘겨줄 수 없으니, 등록해 놓으면 %mdc라고 정해놓은 위치에 MDC의 내용이 출력이 됩니다.

### File 로 저장하기

```xml
<appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
    <file>${application.log.dir:-logs}/application.log</file>
    <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
      <fileNamePattern>${application.log.dir:-logs}/application.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
      <timeBasedFileNamingAndTriggeringPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
        <maxFileSize>100mb</maxFileSize>
      </timeBasedFileNamingAndTriggeringPolicy>
      <maxHistory>30</maxHistory>
    </rollingPolicy>

    <encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
        <pattern>[%d{yyyy:MM:dd HH:mm:ss.SSS}] [%-5level] [%thread] [%C{36}] [%M] [%line] [%mdc]- %msg %n
        </pattern>
    </encoder>
  </appender>

  <logger name="org.springframework" level="INFO"/>
  <logger name="com.example.logback.controller" level="TRACE"/>

  <root level="INFO">
    <appender-ref ref="CONSOLE"/>
    <appender-ref ref="FILE"/>
  </root>
```

`<file>`를 통해서 file의 위치 및 파일명을 설정할 수 있습니다.

`<rollingPolicy>`에서는 파일에 대해서 어떤식으로 rolling 할 것인지에 대한 정책을 정할 수 있습니다.
가령, logging을 날짜별로 관리하기 위해서는 `<fileNamePattern>`을 작성해서 날짜 패턴을 정해주면 됩니다.
file의 maxSize를 설정하고 싶은 경우에는 `<timeBasedFileNamingAndTriggeringPolicy>`안에 `<maxFileSize>`에 크기를 설정해 주면 됩니다.
file의 보관길이를 설정하고 싶으면 `<maxHistory>`에 기간을 적어주면 됩니다. (단위: 일)

추가적으로, logback으로 DB에 저장하는 것도 가능하지만 여기서는 다루지 않겠습니다.
