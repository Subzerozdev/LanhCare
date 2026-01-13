package com.lanhcare.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public enum BMIStatus {
    UNDERWEIGHT(
            "Cân nặng thấp (Gầy)",
            BigDecimal.ZERO, BigDecimal.valueOf(18.49),
            "Trọng lượng cơ thể thấp hơn mức khỏe mạnh. Nguy cơ thiếu hụt dinh dưỡng và suy giảm hệ miễn dịch."
    ),
    NORMAL("Bình thường (Khỏe mạnh)",
            BigDecimal.valueOf(18.50), BigDecimal.valueOf(22.9),
            "Mức cân nặng lý tưởng cho người trưởng thành. Nguy cơ mắc bệnh mãn tính thấp nhất."
    ),
    OVERWEIGHT("Thừa cân (Tiền béo phì)",
            BigDecimal.valueOf(23.0), BigDecimal.valueOf(24.9),
            "Trọng lượng vượt mức lý tưởng. Đây là giai đoạn cảnh báo, bắt đầu có nguy cơ về chuyển hóa."
    ),
    OBESE_I("Béo phì độ I",
            BigDecimal.valueOf(25.0), BigDecimal.valueOf(29.9),
            "Tích tụ mỡ thừa cao. Nguy cơ cao mắc cao huyết áp, tiểu đường loại 2 và bệnh tim mạch."
    ),
    OBESE_II("Béo phì độ II",
            BigDecimal.valueOf(30.0), BigDecimal.valueOf(204.0),
            "Béo phì mức độ nghiêm trọng. Nguy cơ rất cao dẫn đến các biến chứng sức khỏe nguy hiểm."
    ),
    UNDEFINED("Không xác định",
            BigDecimal.valueOf(-1.0), BigDecimal.valueOf(-1.0),
            "Trạng thái không xác định."
    )
    ;

    private final String name;
    private final BigDecimal minBmi;
    private final BigDecimal maxBmi;
    private final String description;
}
