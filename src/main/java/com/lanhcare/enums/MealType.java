package com.lanhcare.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MealType {
    BREAKFAST("Bữa Sáng"),
    LUNCH("Bữa Trưa"),
    DINNER("Bữa Tối"),
    SNACK("Bữa Phụ")
    ;

    private final String name;
}
