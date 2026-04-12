package com.offnal.shifterz.domain.work.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "근무 유형", example = "D")
public enum WorkTimeType {
    DAY("D", "주간"),
    EVENING("E", "오후"),
    NIGHT("N", "야간"),
    OFF("-", "휴일");

    private final String symbol;
    private final String koreanName;

    WorkTimeType(String symbol, String koreanName) {
        this.symbol = symbol;
        this.koreanName = koreanName;
    }

    public static WorkTimeType fromSymbol(String symbol) {
        for (WorkTimeType type : WorkTimeType.values()) {
            if (type.symbol.equals(symbol)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown shift type: " + symbol);
    }

}