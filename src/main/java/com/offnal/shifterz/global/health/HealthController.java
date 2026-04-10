package com.offnal.shifterz.global.health;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Health", description = "헬스체크 API")
@RestController
@RequiredArgsConstructor
public class HealthController {
    private final HealthEndpoint healthEndpoint;

    @Operation(
            summary = "Liveness Check",
            description = """
                    애플리케이션 프로세스가 살아있는지 확인합니다.
                    
                    - status: 전체 상태 (UP/DOWN)
                    - components.ping: 애플리케이션 자체 상태
                    """
    )
    @ApiResponse(
            responseCode = "200",
            description = "정상 응답",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            name = "Liveness Example",
                            value = """
                                    {
                                      "status": "UP",
                                      "components": {
                                        "ping": {
                                          "status": "UP"
                                        }
                                      }
                                    }
                                    """
                    )
            )
    )
    @GetMapping("/health/liveness")
    public ResponseEntity<HealthComponent> liveness() {
        return ResponseEntity.ok(healthEndpoint.healthForPath("liveness"));
    }

    @Operation(
            summary = "Readiness Check",
            description = """
                    트래픽을 받을 준비가 되었는지 확인합니다.
                    
                    - status: 전체 상태 (UP/DOWN)
                    - components.db: 데이터베이스 연결 상태
                    - components.redis: Redis 연결 상태
                    - details: 각 컴포넌트의 추가 정보
                    """
    )
    @ApiResponse(
            responseCode = "200",
            description = "정상 응답",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(
                            name = "Readiness Example",
                            value = """
                                    {
                                      "status": "UP",
                                      "components": {
                                        "db": {
                                          "status": "UP",
                                          "details": {
                                            "database": "MySQL",
                                            "validationQuery": "isValid()"
                                          }
                                        },
                                        "redis": {
                                          "status": "UP",
                                          "details": {
                                            "version": "8.2.1"
                                          }
                                        }
                                      }
                                    }
                                    """
                    )
            )
    )
    @GetMapping("/health/readiness")
    public ResponseEntity<HealthComponent> readiness() {
        return ResponseEntity.ok(healthEndpoint.healthForPath("readiness"));
    }
}
