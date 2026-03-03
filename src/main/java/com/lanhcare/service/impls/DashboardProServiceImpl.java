package com.lanhcare.service.impls;

import com.lanhcare.dto.subscription.DashboardProResponse;
import com.lanhcare.entity.DailyLog;
import com.lanhcare.entity.ExerciseLog;
import com.lanhcare.entity.MealLog;
import com.lanhcare.entity.UserHealthProfile;
import com.lanhcare.repository.DailyLogRepository;
import com.lanhcare.repository.UserHealthProfileRepository;
import com.lanhcare.service.DashboardProService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardProServiceImpl implements DashboardProService {

    private final DailyLogRepository dailyLogRepository;
    private final UserHealthProfileRepository healthProfileRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardProResponse getDashboard(Integer accountId) {
        LocalDate today = LocalDate.now();
        LocalDate weekAgo = today.minusDays(6);

        List<DailyLog> weeklyLogs = dailyLogRepository
                .findByAccountIdAndLoggedDateBetween(accountId, weekAgo, today);

        // 1. Weekly trends
        List<DashboardProResponse.DayTrend> calorieTrend = new ArrayList<>();
        List<DashboardProResponse.DayTrend> stepsTrend = new ArrayList<>();

        // Create a map for quick lookup
        Map<LocalDate, DailyLog> logMap = weeklyLogs.stream()
                .collect(Collectors.toMap(DailyLog::getLoggedDate, l -> l, (a, b) -> a));

        for (int i = 6; i >= 0; i--) {
            LocalDate date = today.minusDays(i);
            DailyLog log = logMap.get(date);

            BigDecimal calIn = BigDecimal.ZERO;
            BigDecimal calOut = BigDecimal.ZERO;
            int steps = 0;

            if (log != null) {
                calIn = log.getTotalCaloriesIn() != null ? log.getTotalCaloriesIn() : BigDecimal.ZERO;
                calOut = log.getTotalCaloriesOut() != null ? log.getTotalCaloriesOut() : BigDecimal.ZERO;
                steps = log.getStepAmount() != null ? log.getStepAmount() : 0;
            }

            DashboardProResponse.DayTrend trend = DashboardProResponse.DayTrend.builder()
                    .date(date)
                    .caloriesIn(calIn)
                    .caloriesOut(calOut)
                    .steps(steps)
                    .build();

            calorieTrend.add(trend);
            stepsTrend.add(trend);
        }

        // 2. Streak days
        int streak = calculateStreak(accountId, today);

        // 3. Top exercises (from last 30 days)
        List<DailyLog> monthLogs = dailyLogRepository
                .findByAccountIdAndLoggedDateBetween(accountId, today.minusDays(29), today);
        List<DashboardProResponse.ExerciseRank> topExercises = getTopExercises(monthLogs);

        // 4. Nutrition breakdown (from last 7 days)
        DashboardProResponse.NutritionBreakdown nutrition = getNutritionBreakdown(weeklyLogs);

        // 5. Goal progress
        String healthGoal = null;
        BigDecimal goalProgress = BigDecimal.ZERO;
        var profileOpt = healthProfileRepository.findByAccountId(accountId);
        if (profileOpt.isPresent()) {
            UserHealthProfile hp = profileOpt.get();
            healthGoal = hp.getHealthGoals() != null ? hp.getHealthGoals().name() : null;
            goalProgress = calculateGoalProgress(weeklyLogs, hp);
        }

        return DashboardProResponse.builder()
                .streakDays(streak)
                .goalProgress(goalProgress)
                .healthGoal(healthGoal)
                .weeklyCalorieTrend(calorieTrend)
                .weeklyStepsTrend(stepsTrend)
                .topExercises(topExercises)
                .nutritionBreakdown(nutrition)
                .build();
    }

    private int calculateStreak(Integer accountId, LocalDate today) {
        int streak = 0;
        LocalDate date = today;
        while (true) {
            var logOpt = dailyLogRepository.findByAccountIdAndLoggedDate(accountId, date);
            if (logOpt.isEmpty()) break;
            streak++;
            date = date.minusDays(1);
            if (streak > 365) break; // safety limit
        }
        return streak;
    }

    private List<DashboardProResponse.ExerciseRank> getTopExercises(List<DailyLog> logs) {
        Map<String, int[]> exerciseMap = new HashMap<>(); // activity -> [count, totalCal*100]

        for (DailyLog log : logs) {
            if (log.getExerciseLogs() == null) continue;
            for (ExerciseLog ex : log.getExerciseLogs()) {
                String activity = ex.getExerciseType() != null ? ex.getExerciseType().getActivity() : "Unknown";
                exerciseMap.computeIfAbsent(activity, k -> new int[]{0, 0});
                exerciseMap.get(activity)[0]++;
                exerciseMap.get(activity)[1] += ex.getCaloriesOut() != null
                        ? ex.getCaloriesOut().multiply(BigDecimal.valueOf(100)).intValue() : 0;
            }
        }

        return exerciseMap.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue()[0], a.getValue()[0]))
                .limit(3)
                .map(e -> DashboardProResponse.ExerciseRank.builder()
                        .activity(e.getKey())
                        .count(e.getValue()[0])
                        .totalCalories(BigDecimal.valueOf(e.getValue()[1]).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP))
                        .build())
                .collect(Collectors.toList());
    }

    private DashboardProResponse.NutritionBreakdown getNutritionBreakdown(List<DailyLog> logs) {
        int breakfast = 0, lunch = 0, dinner = 0, snack = 0;

        for (DailyLog log : logs) {
            if (log.getMealLogs() == null) continue;
            for (MealLog meal : log.getMealLogs()) {
                if (meal.getMealType() == null) continue;
                switch (meal.getMealType()) {
                    case BREAKFAST -> breakfast++;
                    case LUNCH -> lunch++;
                    case DINNER -> dinner++;
                    case SNACK -> snack++;
                }
            }
        }

        return DashboardProResponse.NutritionBreakdown.builder()
                .breakfastCount(breakfast)
                .lunchCount(lunch)
                .dinnerCount(dinner)
                .snackCount(snack)
                .build();
    }

    private BigDecimal calculateGoalProgress(List<DailyLog> weeklyLogs, UserHealthProfile profile) {
        if (weeklyLogs.isEmpty() || profile.getHealthGoals() == null) return BigDecimal.ZERO;

        BigDecimal totalCalIn = weeklyLogs.stream()
                .map(l -> l.getTotalCaloriesIn() != null ? l.getTotalCaloriesIn() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalCalOut = weeklyLogs.stream()
                .map(l -> l.getTotalCaloriesOut() != null ? l.getTotalCaloriesOut() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal avgBalance = totalCalIn.subtract(totalCalOut)
                .divide(BigDecimal.valueOf(weeklyLogs.size()), 2, RoundingMode.HALF_UP);

        return switch (profile.getHealthGoals()) {
            case LOSE_WEIGHT -> {
                // Progress = how well user maintains deficit (target: -300 to -500 kcal)
                if (avgBalance.compareTo(BigDecimal.ZERO) <= 0) {
                    BigDecimal progress = avgBalance.abs().divide(BigDecimal.valueOf(500), 2, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100));
                    yield progress.min(BigDecimal.valueOf(100));
                }
                yield BigDecimal.ZERO;
            }
            case MAINTAIN -> {
                // Progress = how close to zero balance
                BigDecimal deviation = avgBalance.abs();
                BigDecimal progress = BigDecimal.valueOf(100).subtract(
                        deviation.divide(BigDecimal.valueOf(5), 2, RoundingMode.HALF_UP));
                yield progress.max(BigDecimal.ZERO).min(BigDecimal.valueOf(100));
            }
            case EXTREME_GAIN -> {
                // Progress = how well user maintains surplus
                if (avgBalance.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal progress = avgBalance.divide(BigDecimal.valueOf(500), 2, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100));
                    yield progress.min(BigDecimal.valueOf(100));
                }
                yield BigDecimal.ZERO;
            }
        };
    }

    private Optional<DailyLog> findLogByDate(Integer accountId, LocalDate date) {
        return dailyLogRepository.findByAccountIdAndLoggedDate(accountId, date);
    }
}
