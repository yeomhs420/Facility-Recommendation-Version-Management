package com.example.project

import org.springframework.boot.test.context.SpringBootTest
import org.testcontainers.containers.GenericContainer
import spock.lang.Specification

@SpringBootTest // 애플리케이션의 설정, 모든 Bean 이 모두 로드되고 실제 DB와 커넥션이 되는 상태에서 진행하는 Live 테스트 방법
abstract class AbstractIntegrationContainerBaseTest extends Specification{  // TestContainer 를 이용한 전체 통합 테스트 환경
    static final GenericContainer MY_REDIS_CONTAINER

    static{
        MY_REDIS_CONTAINER = new GenericContainer<>("redis:6")  // redis 컨테이너 생성
                .withExposedPorts(6379)

        MY_REDIS_CONTAINER.start()

        // 6379와 매핑된 호스트와 랜덤하게 매핑된 포트 설정
        System.setProperty("spring.redis.host", MY_REDIS_CONTAINER.host)
        System.setProperty("spring.redis.port",MY_REDIS_CONTAINER.getMappedPort(6379).toString())
    }
}
