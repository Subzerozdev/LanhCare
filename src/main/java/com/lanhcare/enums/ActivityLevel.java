package com.lanhcare.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public enum ActivityLevel {
    NO_EXERCISE(BigDecimal.valueOf(1.2), "Ít vận động", "Ngồi nhiều, ít đi lại, không tập thể dục"),
    LIGHT_EXERCISE(BigDecimal.valueOf(1.375), "Vận động nhẹ", "Tập thể dục nhẹ nhàng 1–3 buổi/tuần"),
    NORMAL_EXERCISE(BigDecimal.valueOf(1.55), "Vận động vừa", "Tập thể dục trung bình 3–5 buổi/tuần"),
    HIGH_EXERCISE(BigDecimal.valueOf(1.725), "Vận động nặng", "Tập thể dục cường độ cao 6–7 buổi/tuần"),
    VERY_HIGH_EXERCISE(BigDecimal.valueOf(1.9), "Rất nặng", "Vận động viên, làm công việc chân tay rất nặng");

    private final BigDecimal rFactor;
    private final String displayName;
    private final String description;
}

