package com.lanhcare.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum HealthGoal {
    LOSE_WEIGHT("Giảm cân", "Ăn ít hơn TDEE - Thường nên giảm khoảng 200–500 calo mỗi ngày."),
    MAINTAIN("Giữ cân", "Ăn bằng với lượng TDEE"),
    EXTREME_GAIN("Tăng cân", "Ăn nhiều hơn TDEE - Có thể thêm khoảng 200–500 calo mỗi ngày.");

    private final String displayName;
    private final String description;
}
