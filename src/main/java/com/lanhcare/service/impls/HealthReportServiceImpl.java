package com.lanhcare.service.impls;

import com.lanhcare.dto.subscription.HealthReportResponse;
import com.lanhcare.entity.DailyLog;
import com.lanhcare.entity.UserHealthProfile;
import com.lanhcare.repository.DailyLogRepository;
import com.lanhcare.repository.UserHealthProfileRepository;
import com.lanhcare.service.HealthReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HealthReportServiceImpl implements HealthReportService {

    private final DailyLogRepository dailyLogRepository;
    private final UserHealthProfileRepository healthProfileRepository;

    @Override
    @Transactional(readOnly = true)
    public HealthReportResponse getWeeklyReport(Integer accountId) {
        LocalDate to = LocalDate.now();
        LocalDate from = to.minusDays(6); // last 7 days
        return buildReport(accountId, from, to, "WEEKLY", false);
    }

    @Override
    @Transactional(readOnly = true)
    public HealthReportResponse getFullReport(Integer accountId, LocalDate from, LocalDate to) {
        if (from == null) from = LocalDate.now().minusDays(29); // default 30 days
        if (to == null) to = LocalDate.now();
        return buildReport(accountId, from, to, "FULL", true);
    }

    private HealthReportResponse buildReport(Integer accountId, LocalDate from, LocalDate to,
                                              String period, boolean includeDetails) {
        List<DailyLog> logs = dailyLogRepository.findByAccountIdAndLoggedDateBetween(accountId, from, to);

        int daysLogged = logs.size();
        BigDecimal totalCalIn = BigDecimal.ZERO;
        BigDecimal totalCalOut = BigDecimal.ZERO;
        int totalSteps = 0;
        int totalMeals = 0;
        int totalExercises = 0;

        List<HealthReportResponse.DailyDetail> dailyDetails = new ArrayList<>();

        for (DailyLog log : logs) {
            BigDecimal calIn = log.getTotalCaloriesIn() != null ? log.getTotalCaloriesIn() : BigDecimal.ZERO;
            BigDecimal calOut = log.getTotalCaloriesOut() != null ? log.getTotalCaloriesOut() : BigDecimal.ZERO;
            int steps = log.getStepAmount() != null ? log.getStepAmount() : 0;
            int meals = log.getMealLogs() != null ? log.getMealLogs().size() : 0;
            int exercises = log.getExerciseLogs() != null ? log.getExerciseLogs().size() : 0;

            totalCalIn = totalCalIn.add(calIn);
            totalCalOut = totalCalOut.add(calOut);
            totalSteps += steps;
            totalMeals += meals;
            totalExercises += exercises;

            if (includeDetails) {
                dailyDetails.add(HealthReportResponse.DailyDetail.builder()
                        .date(log.getLoggedDate())
                        .caloriesIn(calIn)
                        .caloriesOut(calOut)
                        .steps(steps)
                        .mealCount(meals)
                        .exerciseCount(exercises)
                        .build());
            }
        }

        BigDecimal avgCalIn = daysLogged > 0
                ? totalCalIn.divide(BigDecimal.valueOf(daysLogged), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        BigDecimal avgCalOut = daysLogged > 0
                ? totalCalOut.divide(BigDecimal.valueOf(daysLogged), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;
        int avgSteps = daysLogged > 0 ? totalSteps / daysLogged : 0;

        // Health profile
        BigDecimal weightKg = null;
        BigDecimal bmiValue = null;
        String bmiStatus = null;
        String healthGoal = null;

        var profileOpt = healthProfileRepository.findByAccountId(accountId);
        if (profileOpt.isPresent()) {
            UserHealthProfile hp = profileOpt.get();
            weightKg = hp.getWeightKg();
            bmiValue = hp.getBmiValue();
            bmiStatus = hp.getBmiStatus() != null ? hp.getBmiStatus().name() : null;
            healthGoal = hp.getHealthGoals() != null ? hp.getHealthGoals().name() : null;
        }

        // Health tips (only for FULL)
        List<String> tips = includeDetails ? generateHealthTips(avgCalIn, avgCalOut, bmiStatus, healthGoal, avgSteps) : null;

        return HealthReportResponse.builder()
                .period(period)
                .startDate(from)
                .endDate(to)
                .daysLogged(daysLogged)
                .avgCaloriesIn(avgCalIn)
                .avgCaloriesOut(avgCalOut)
                .calorieBalance(avgCalIn.subtract(avgCalOut))
                .avgSteps(avgSteps)
                .totalMeals(totalMeals)
                .totalExercises(totalExercises)
                .weightKg(weightKg)
                .bmiValue(bmiValue)
                .bmiStatus(bmiStatus)
                .healthGoal(healthGoal)
                .healthTips(tips)
                .dailyDetails(includeDetails ? dailyDetails : null)
                .build();
    }

    private List<String> generateHealthTips(BigDecimal avgCalIn, BigDecimal avgCalOut,
                                             String bmiStatus, String healthGoal, int avgSteps) {
        List<String> tips = new ArrayList<>();

        // Calorie balance tips
        BigDecimal balance = avgCalIn.subtract(avgCalOut);
        if ("LOSE_WEIGHT".equals(healthGoal)) {
            if (balance.compareTo(BigDecimal.ZERO) > 0) {
                tips.add("Bạn đang thặng dư " + balance.setScale(0, RoundingMode.HALF_UP) +
                        " kcal/ngày. Để giảm cân, cần tạo thâm hụt calories bằng cách ăn ít hơn hoặc tập nhiều hơn.");
            } else {
                tips.add("Tốt lắm! Bạn đang thâm hụt " + balance.abs().setScale(0, RoundingMode.HALF_UP) +
                        " kcal/ngày, phù hợp với mục tiêu giảm cân.");
            }
        } else if ("EXTREME_GAIN".equals(healthGoal)) {
            if (balance.compareTo(BigDecimal.valueOf(300)) < 0) {
                tips.add("Để tăng cân, bạn nên ăn thêm ít nhất 300-500 kcal/ngày so với lượng tiêu thụ.");
            }
        }

        // BMI tips
        if ("UNDERWEIGHT".equals(bmiStatus)) {
            tips.add("BMI cho thấy bạn đang thiếu cân. Hãy tăng khẩu phần ăn giàu protein và carbohydrate.");
        } else if ("OVERWEIGHT".equals(bmiStatus) || "OBESE_I".equals(bmiStatus) || "OBESE_II".equals(bmiStatus)) {
            tips.add("BMI cho thấy bạn đang thừa cân. Hãy tập trung vào rau xanh, protein nạc và tăng vận động.");
        }

        // Steps tips
        if (avgSteps < 5000) {
            tips.add("Trung bình " + avgSteps + " bước/ngày. WHO khuyến nghị ít nhất 7,000-10,000 bước/ngày.");
        } else if (avgSteps >= 10000) {
            tips.add("Xuất sắc! Bạn đạt trung bình " + avgSteps + " bước/ngày, vượt mục tiêu khuyến nghị.");
        }

        if (tips.isEmpty()) {
            tips.add("Bạn đang duy trì lối sống lành mạnh. Hãy tiếp tục phát huy!");
        }

        return tips;
    }
}
