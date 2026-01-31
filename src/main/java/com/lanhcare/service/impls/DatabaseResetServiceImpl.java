package com.lanhcare.service.impls;

import com.lanhcare.entity.*;
import com.lanhcare.enums.*;
import com.lanhcare.enums.healthprofile.*;
import com.lanhcare.repository.*;
import com.lanhcare.service.DatabaseResetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DatabaseResetServiceImpl implements DatabaseResetService {

    private final JdbcTemplate jdbcTemplate;
    
    // Repositories
    private final AccountRepository accountRepository;
    private final UserHealthProfileRepository healthProfileRepository;
    private final ServicePlanRepository servicePlanRepository;
    private final FoodTypeRepository foodTypeRepository;
    private final NutrientRepository nutrientRepository;
    private final FoodItemRepository foodItemRepository;
    private final FoodNutrientRepository foodNutrientRepository;
    private final ExerciseTypeRepository exerciseTypeRepository;
    private final HospitalRepository hospitalRepository;
    private final MedicalSpecialtyRepository medicalSpecialtyRepository;
    private final ICD11ChapterRepository icd11ChapterRepository;
    private final ICD11CodeRepository icd11CodeRepository;
    private final ICD11TranslationRepository icd11TranslationRepository;
    private final DailyLogRepository dailyLogRepository;
    private final MealLogRepository mealLogRepository;
    private final MealFoodRepository mealFoodRepository;
    private final ExerciseLogRepository exerciseLogRepository;
    private final PostRepository postRepository;
    private final PostMediaRepository postMediaRepository;
    private final CommentRepository commentRepository;
    private final CommentMediaRepository commentMediaRepository;
    private final TransactionRepository transactionRepository;
    private final DietaryRestrictionRepository dietaryRestrictionRepository;
    private final FCMTokenRepository fcmTokenRepository;

    private static final String DEFAULT_PASSWORD_HASH = "$2a$12$HFxTC4chD9yiHHzesulpY.5.bZ5jFbvm5mlBfSlQ8lnsOGTnfTU/y";

    @Override
    @Transactional
    public ResetResult resetAndSeedDatabase() {
        log.info("Starting database reset and seed...");
        
        // Step 1: Delete all data except Admin accounts
        int deletedAccounts = deleteAllDataExceptAdmin();
        int deletedRecords = 0; // Will be calculated
        
        // Step 2: Seed reference data (independent tables)
        seedReferenceData();
        
        // Step 3: Seed user data (with relationships)
        int seededAccounts = seedUserData();
        int seededRecords = 0; // Will be calculated
        
        log.info("Database reset and seed completed successfully");
        
        return new ResetResult(
            deletedAccounts,
            deletedRecords,
            seededAccounts,
            seededRecords,
            "Database đã được reset và nạp lại dữ liệu mẫu thành công"
        );
    }

    /**
     * Delete all data except Admin accounts
     * Using JPA repository to avoid column name mismatch issues
     */
    private int deleteAllDataExceptAdmin() {
        log.info("Deleting all data except Admin accounts...");
        
        // Find all non-admin accounts
        List<Account> allAccounts = accountRepository.findAll();
        List<Account> nonAdminAccounts = allAccounts.stream()
            .filter(account -> account.getRole() != AccountRole.ADMIN)
            .toList();
        int countBefore = nonAdminAccounts.size();
        
        if (countBefore == 0) {
            log.info("No non-admin accounts to delete");
        } else {
            log.info("Found {} non-admin accounts to delete", countBefore);
            
            // Delete data for each non-admin account using JPA (cascade will handle relationships)
            for (Account account : nonAdminAccounts) {
                Integer accountId = account.getId();
                
                // Delete comments and their media (cascade will handle comment_media)
                List<Comment> comments = commentRepository.findByAccountId(accountId);
                if (!comments.isEmpty()) {
                    commentRepository.deleteAll(comments);
                }
                
                // Delete posts and their media (cascade will handle post_media)
                List<Post> posts = postRepository.findByAccountId(accountId);
                if (!posts.isEmpty()) {
                    postRepository.deleteAll(posts);
                }
                
                // Delete daily logs (cascade will handle meal_log, meal_food, exercise_log)
                List<DailyLog> dailyLogs = dailyLogRepository.findByAccountId(accountId);
                if (!dailyLogs.isEmpty()) {
                    // Delete meal_food first (through meal_log)
                    for (DailyLog dailyLog : dailyLogs) {
                        List<MealLog> mealLogs = mealLogRepository.findByDailyLogId(dailyLog.getId());
                        for (MealLog mealLog : mealLogs) {
                            List<MealFood> mealFoods = mealFoodRepository.findByMealLogId(mealLog.getId());
                            if (!mealFoods.isEmpty()) {
                                mealFoodRepository.deleteAll(mealFoods);
                            }
                        }
                        if (!mealLogs.isEmpty()) {
                            mealLogRepository.deleteAll(mealLogs);
                        }
                        
                        // Delete exercise_log
                        List<ExerciseLog> exerciseLogs = exerciseLogRepository.findByDailyLogId(dailyLog.getId());
                        if (!exerciseLogs.isEmpty()) {
                            exerciseLogRepository.deleteAll(exerciseLogs);
                        }
                    }
                    dailyLogRepository.deleteAll(dailyLogs);
                }
                
                // Delete transactions
                List<Transaction> transactions = transactionRepository.findByAccountIdOrderByIdDesc(accountId);
                if (!transactions.isEmpty()) {
                    transactionRepository.deleteAll(transactions);
                }
                
                // Delete FCM tokens
                List<FCMToken> fcmTokens = fcmTokenRepository.findByAccountId(accountId);
                if (!fcmTokens.isEmpty()) {
                    fcmTokenRepository.deleteAll(fcmTokens);
                }
                
                // Delete health profiles (cascade will handle dietary_restriction)
                healthProfileRepository.findByAccountId(accountId)
                    .ifPresent(healthProfileRepository::delete);
            }
            
            // Delete non-admin accounts
            accountRepository.deleteAll(nonAdminAccounts);
        }
        
        // Delete Many-to-Many join tables (these don't reference account directly)
        jdbcTemplate.execute("TRUNCATE TABLE hospital_speciality RESTART IDENTITY CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE icd11_speciality RESTART IDENTITY CASCADE");
        
        // Delete other independent tables (these don't have account references)
        jdbcTemplate.execute("TRUNCATE TABLE food_nutrient RESTART IDENTITY CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE food_item RESTART IDENTITY CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE food_type RESTART IDENTITY CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE nutrient RESTART IDENTITY CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE exercise_type RESTART IDENTITY CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE service_plan RESTART IDENTITY CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE hospital RESTART IDENTITY CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE medical_specialty RESTART IDENTITY CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE icd11_translation RESTART IDENTITY CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE icd11_code RESTART IDENTITY CASCADE");
        jdbcTemplate.execute("TRUNCATE TABLE icd11_chapter RESTART IDENTITY CASCADE");
        
        return countBefore;
    }

    /**
     * Seed reference data (independent tables)
     */
    private void seedReferenceData() {
        log.info("Seeding reference data...");
        
        seedServicePlans();
        seedFoodTypes();
        seedNutrients();
        seedExerciseTypes();
        seedHospitals();
        seedICD11Data();
        seedFoodItems();
    }

    /**
     * Seed user data (with relationships)
     */
    private int seedUserData() {
        log.info("Seeding user data...");
        
        List<Account> accounts = seedAccounts();
        seedHealthProfiles(accounts);
        seedDailyLogs(accounts);
        seedPosts(accounts);
        seedTransactions(accounts);
        seedFCMTokens(accounts);
        
        return accounts.size();
    }

    // ========== Reference Data Seeding ==========

    private void seedServicePlans() {
        List<ServicePlan> plans = List.of(
            ServicePlan.builder()
                .name("Miễn phí")
                .description("Gói miễn phí với các tính năng cơ bản")
                .price(BigDecimal.ZERO)
                .periodValue(1)
                .periodUnit(PeriodUnit.MONTH)
                .status(ServicePlanStatus.ACTIVE)
                .build(),
            ServicePlan.builder()
                .name("Cao cấp tháng")
                .description("Gói cao cấp hàng tháng với đầy đủ tính năng")
                .price(new BigDecimal("99000.00"))
                .periodValue(1)
                .periodUnit(PeriodUnit.MONTH)
                .status(ServicePlanStatus.ACTIVE)
                .build(),
            ServicePlan.builder()
                .name("Cao cấp năm")
                .description("Gói cao cấp hàng năm - Tiết kiệm 20%")
                .price(new BigDecimal("950000.00"))
                .periodValue(1)
                .periodUnit(PeriodUnit.YEAR)
                .status(ServicePlanStatus.ACTIVE)
                .build()
        );
        servicePlanRepository.saveAll(plans);
    }

    private void seedFoodTypes() {
        List<FoodType> types = List.of(
            FoodType.builder().name("Rau củ quả").isDeleted(false).build(),
            FoodType.builder().name("Thịt và sản phẩm từ thịt").isDeleted(false).build(),
            FoodType.builder().name("Cá và hải sản").isDeleted(false).build(),
            FoodType.builder().name("Trứng và sữa").isDeleted(false).build(),
            FoodType.builder().name("Ngũ cốc và tinh bột").isDeleted(false).build(),
            FoodType.builder().name("Trái cây").isDeleted(false).build(),
            FoodType.builder().name("Đồ uống").isDeleted(false).build(),
            FoodType.builder().name("Đồ ăn nhanh").isDeleted(false).build()
        );
        foodTypeRepository.saveAll(types);
    }

    private void seedNutrients() {
        List<Nutrient> nutrients = List.of(
            Nutrient.builder().name("Protein").unit("g").isDeleted(false).build(),
            Nutrient.builder().name("Carbohydrate").unit("g").isDeleted(false).build(),
            Nutrient.builder().name("Chất béo").unit("g").isDeleted(false).build(),
            Nutrient.builder().name("Chất xơ").unit("g").isDeleted(false).build(),
            Nutrient.builder().name("Đường").unit("g").isDeleted(false).build(),
            Nutrient.builder().name("Vitamin A").unit("mcg").isDeleted(false).build(),
            Nutrient.builder().name("Vitamin C").unit("mg").isDeleted(false).build(),
            Nutrient.builder().name("Vitamin D").unit("mcg").isDeleted(false).build(),
            Nutrient.builder().name("Canxi").unit("mg").isDeleted(false).build(),
            Nutrient.builder().name("Sắt").unit("mg").isDeleted(false).build(),
            Nutrient.builder().name("Natri").unit("mg").isDeleted(false).build(),
            Nutrient.builder().name("Kali").unit("mg").isDeleted(false).build()
        );
        nutrientRepository.saveAll(nutrients);
    }

    private void seedExerciseTypes() {
        List<ExerciseType> exercises = List.of(
            ExerciseType.builder()
                .activity("Đi bộ")
                .examples("Đi bộ thường, đi bộ nhanh")
                .metValue(new BigDecimal("3.5"))
                .deleted(false)
                .build(),
            ExerciseType.builder()
                .activity("Chạy bộ")
                .examples("Chạy chậm, chạy nhanh")
                .metValue(new BigDecimal("8.0"))
                .deleted(false)
                .build(),
            ExerciseType.builder()
                .activity("Đạp xe")
                .examples("Đạp xe đạp, đạp xe máy")
                .metValue(new BigDecimal("6.0"))
                .deleted(false)
                .build(),
            ExerciseType.builder()
                .activity("Bơi lội")
                .examples("Bơi tự do, bơi ếch")
                .metValue(new BigDecimal("7.0"))
                .deleted(false)
                .build(),
            ExerciseType.builder()
                .activity("Yoga")
                .examples("Yoga cơ bản, yoga nâng cao")
                .metValue(new BigDecimal("3.0"))
                .deleted(false)
                .build()
        );
        exerciseTypeRepository.saveAll(exercises);
    }

    private void seedHospitals() {
        List<Hospital> hospitals = List.of(
            Hospital.builder()
                .name("Bệnh viện Chợ Rẫy")
                .address("201B Nguyễn Chí Thanh, Phường 12, Quận 5, TP.HCM")
                .latitude(new BigDecimal("10.7546729"))
                .longitude(new BigDecimal("106.6573527"))
                .status(HospitalStatus.ACTIVE)
                .build(),
            Hospital.builder()
                .name("Bệnh viện Đại học Y Dược TP.HCM")
                .address("215 Hồng Bàng, Phường 11, Quận 5, TP.HCM")
                .latitude(new BigDecimal("10.7563847"))
                .longitude(new BigDecimal("106.6543272"))
                .status(HospitalStatus.ACTIVE)
                .build(),
            Hospital.builder()
                .name("Bệnh viện Nhân dân 115")
                .address("527 Sư Vạn Hạnh, Phường 12, Quận 10, TP.HCM")
                .latitude(new BigDecimal("10.7723742"))
                .longitude(new BigDecimal("106.6644367"))
                .status(HospitalStatus.ACTIVE)
                .build()
        );
        List<Hospital> savedHospitals = hospitalRepository.saveAll(hospitals);

        // Seed MedicalSpecialties
        List<MedicalSpecialty> specialties = List.of(
            MedicalSpecialty.builder()
                .nameVn("Nội tiết - Đái tháo đường")
                .nameEn("Endocrinology - Diabetes")
                .status(SpecialtyStatus.ACTIVE)
                .build(),
            MedicalSpecialty.builder()
                .nameVn("Tim mạch")
                .nameEn("Cardiology")
                .status(SpecialtyStatus.ACTIVE)
                .build(),
            MedicalSpecialty.builder()
                .nameVn("Tiêu hóa")
                .nameEn("Gastroenterology")
                .status(SpecialtyStatus.ACTIVE)
                .build(),
            MedicalSpecialty.builder()
                .nameVn("Dinh dưỡng - Béo phì")
                .nameEn("Nutrition - Obesity")
                .status(SpecialtyStatus.ACTIVE)
                .build()
        );
        List<MedicalSpecialty> savedSpecialties = medicalSpecialtyRepository.saveAll(specialties);

        // Link Hospitals with MedicalSpecialties (Many-to-Many)
        if (!savedHospitals.isEmpty() && !savedSpecialties.isEmpty()) {
            savedHospitals.get(0).getMedicalSpecialties().add(savedSpecialties.get(0)); // Bệnh viện Chợ Rẫy - Nội tiết
            savedHospitals.get(0).getMedicalSpecialties().add(savedSpecialties.get(1)); // Bệnh viện Chợ Rẫy - Tim mạch
            savedHospitals.get(1).getMedicalSpecialties().add(savedSpecialties.get(1)); // Bệnh viện Y Dược - Tim mạch
            savedHospitals.get(1).getMedicalSpecialties().add(savedSpecialties.get(2)); // Bệnh viện Y Dược - Tiêu hóa
            savedHospitals.get(2).getMedicalSpecialties().add(savedSpecialties.get(3)); // Bệnh viện 115 - Dinh dưỡng
            hospitalRepository.saveAll(savedHospitals);
        }

        // Link ICD11Code with MedicalSpecialties (Many-to-Many)
        List<ICD11Code> icdCodes = icd11CodeRepository.findAll();
        if (!icdCodes.isEmpty() && !savedSpecialties.isEmpty()) {
            icdCodes.get(0).getMedicalSpecialties().add(savedSpecialties.get(0)); // Diabetes - Nội tiết
            icdCodes.get(1).getMedicalSpecialties().add(savedSpecialties.get(3)); // Obesity - Dinh dưỡng
            icd11CodeRepository.saveAll(icdCodes);
        }
    }

    private void seedICD11Data() {
        // ICD11 Chapters
        ICD11Chapter chapter1 = ICD11Chapter.builder()
            .chapterUri("http://id.who.int/icd/entity/1435254666")
            .vnTitle("Bệnh nội tiết, dinh dưỡng hoặc chuyển hóa")
            .originalTitleEn("Endocrine, nutritional or metabolic diseases")
            .chapterCode("05")
            .releaseId("2024-01")
            .status(ICD11Status.ACTIVE)
            .build();
        icd11ChapterRepository.save(chapter1);

        ICD11Chapter chapter2 = ICD11Chapter.builder()
            .chapterUri("http://id.who.int/icd/entity/1294209752")
            .vnTitle("Bệnh của hệ tuần hoàn")
            .originalTitleEn("Diseases of the circulatory system")
            .chapterCode("11")
            .releaseId("2024-01")
            .status(ICD11Status.ACTIVE)
            .build();
        icd11ChapterRepository.save(chapter2);

        // ICD11 Codes
        ICD11Code code1 = ICD11Code.builder()
            .icdUri("http://id.who.int/icd/entity/2030283443")
            .chapter(chapter1)
            .icdCode("5A10")
            .originalTitleEn("Type 2 diabetes mellitus")
            .definitionEn("A metabolic disorder characterized by high blood glucose")
            .status(ICD11Status.ACTIVE)
            .build();
        icd11CodeRepository.save(code1);

        ICD11Code code2 = ICD11Code.builder()
            .icdUri("http://id.who.int/icd/entity/1881269402")
            .chapter(chapter1)
            .icdCode("5B81")
            .originalTitleEn("Obesity")
            .definitionEn("Excessive accumulation of body fat")
            .status(ICD11Status.ACTIVE)
            .build();
        icd11CodeRepository.save(code2);

        // ICD11 Translations
        ICD11Translation translation1 = ICD11Translation.builder()
            .icdCode(code1)
            .vnTitle("Đái tháo đường type 2")
            .vnDefinition("Rối loạn chuyển hóa đặc trưng bởi glucose máu cao")
            .status(TranslationStatus.PUBLISHED)
            .build();
        icd11TranslationRepository.save(translation1);

        ICD11Translation translation2 = ICD11Translation.builder()
            .icdCode(code2)
            .vnTitle("Béo phì")
            .vnDefinition("Tích tụ mỡ cơ thể quá mức")
            .status(TranslationStatus.PUBLISHED)
            .build();
        icd11TranslationRepository.save(translation2);
    }

    private void seedFoodItems() {
        List<FoodType> foodTypes = foodTypeRepository.findAll();
        FoodType ngucoc = foodTypes.stream().filter(ft -> ft.getName().equals("Ngũ cốc và tinh bột")).findFirst().orElse(null);
        FoodType thit = foodTypes.stream().filter(ft -> ft.getName().equals("Thịt và sản phẩm từ thịt")).findFirst().orElse(null);
        FoodType ca = foodTypes.stream().filter(ft -> ft.getName().equals("Cá và hải sản")).findFirst().orElse(null);
        FoodType trung = foodTypes.stream().filter(ft -> ft.getName().equals("Trứng và sữa")).findFirst().orElse(null);
        FoodType traicay = foodTypes.stream().filter(ft -> ft.getName().equals("Trái cây")).findFirst().orElse(null);

        List<FoodItem> foodItems = new ArrayList<>();
        if (ngucoc != null) {
            foodItems.add(FoodItem.builder()
                .foodType(ngucoc)
                .name("Cơm trắng")
                .description("Cơm trắng nấu chín")
                .calo(new BigDecimal("130.00"))
                .servingUnit("g")
                .standardServingSize(new BigDecimal("100.00"))
                .status(FoodItemStatus.ACTIVE)
                .dataSource("USDA")
                .build());
        }
        if (thit != null) {
            foodItems.add(FoodItem.builder()
                .foodType(thit)
                .name("Ức gà luộc")
                .description("Ức gà luộc không da")
                .calo(new BigDecimal("165.00"))
                .servingUnit("g")
                .standardServingSize(new BigDecimal("100.00"))
                .status(FoodItemStatus.ACTIVE)
                .dataSource("USDA")
                .build());
        }
        if (ca != null) {
            foodItems.add(FoodItem.builder()
                .foodType(ca)
                .name("Cá hồi nướng")
                .description("Cá hồi nướng")
                .calo(new BigDecimal("206.00"))
                .servingUnit("g")
                .standardServingSize(new BigDecimal("100.00"))
                .status(FoodItemStatus.ACTIVE)
                .dataSource("USDA")
                .build());
        }
        if (trung != null) {
            foodItems.add(FoodItem.builder()
                .foodType(trung)
                .name("Trứng gà luộc")
                .description("Trứng gà luộc chín")
                .calo(new BigDecimal("155.00"))
                .servingUnit("quả")
                .standardServingSize(new BigDecimal("1.00"))
                .status(FoodItemStatus.ACTIVE)
                .dataSource("USDA")
                .build());
        }
        if (traicay != null) {
            foodItems.add(FoodItem.builder()
                .foodType(traicay)
                .name("Chuối tiêu")
                .description("Chuối tiêu chín")
                .calo(new BigDecimal("89.00"))
                .servingUnit("g")
                .standardServingSize(new BigDecimal("100.00"))
                .status(FoodItemStatus.ACTIVE)
                .dataSource("USDA")
                .build());
        }

        List<FoodItem> savedFoodItems = foodItemRepository.saveAll(foodItems);

        // Seed FoodNutrients
        List<Nutrient> nutrients = nutrientRepository.findAll();
        Nutrient protein = nutrients.stream().filter(n -> n.getName().equals("Protein")).findFirst().orElse(null);
        Nutrient carb = nutrients.stream().filter(n -> n.getName().equals("Carbohydrate")).findFirst().orElse(null);
        Nutrient fat = nutrients.stream().filter(n -> n.getName().equals("Chất béo")).findFirst().orElse(null);

        List<FoodNutrient> foodNutrients = new ArrayList<>();
        for (FoodItem item : savedFoodItems) {
            if (protein != null && carb != null && fat != null) {
                foodNutrients.add(FoodNutrient.builder()
                    .foodItem(item)
                    .nutrient(protein)
                    .value(new BigDecimal("20.0"))
                    .build());
                foodNutrients.add(FoodNutrient.builder()
                    .foodItem(item)
                    .nutrient(carb)
                    .value(new BigDecimal("30.0"))
                    .build());
                foodNutrients.add(FoodNutrient.builder()
                    .foodItem(item)
                    .nutrient(fat)
                    .value(new BigDecimal("5.0"))
                    .build());
            }
        }
        foodNutrientRepository.saveAll(foodNutrients);
    }

    // ========== User Data Seeding ==========

    private List<Account> seedAccounts() {
        List<Account> accounts = List.of(
            Account.builder()
                .email("user1@lanhcare.com")
                .fullname("Nguyễn Văn An")
                .password(DEFAULT_PASSWORD_HASH)
                .role(AccountRole.USER)
                .status(AccountStatus.ACTIVE)
                .build(),
            Account.builder()
                .email("user2@lanhcare.com")
                .fullname("Trần Thị Bình")
                .password(DEFAULT_PASSWORD_HASH)
                .role(AccountRole.USER)
                .status(AccountStatus.ACTIVE)
                .build(),
            Account.builder()
                .email("doctor@lanhcare.com")
                .fullname("BS. Lê Văn Cường")
                .password(DEFAULT_PASSWORD_HASH)
                .role(AccountRole.STAFF)
                .status(AccountStatus.ACTIVE)
                .build(),
            Account.builder()
                .email("nutritionist@lanhcare.com")
                .fullname("CN. Phạm Thị Dung")
                .password(DEFAULT_PASSWORD_HASH)
                .role(AccountRole.STAFF)
                .status(AccountStatus.ACTIVE)
                .build()
        );
        return accountRepository.saveAll(accounts);
    }

    private void seedHealthProfiles(List<Account> accounts) {
        if (accounts.size() < 2) return;

        Account user1 = accounts.get(0);
        Account user2 = accounts.get(1);

        UserHealthProfile profile1 = UserHealthProfile.builder()
            .account(user1)
            .dateOfBirth(LocalDate.of(1990, 5, 15))
            .gender(Gender.MALE)
            .heightCm(new BigDecimal("175.00"))
            .weightKg(new BigDecimal("70.00"))
            .activityLevel(ActivityLevel.NO_EXERCISE)
            .healthGoals(HealthGoal.LOSE_WEIGHT)
            .build();
        profile1.setBmiValue(profile1.calculateBMI());
        profile1.setBmiStatus(profile1.getStatusByBMI());
        profile1.setTdeeValue(profile1.calculateTDDE());
        profile1 = healthProfileRepository.save(profile1);

        UserHealthProfile profile2 = UserHealthProfile.builder()
            .account(user2)
            .dateOfBirth(LocalDate.of(1995, 8, 20))
            .gender(Gender.FEMALE)
            .heightCm(new BigDecimal("160.00"))
            .weightKg(new BigDecimal("55.00"))
            .activityLevel(ActivityLevel.LIGHT_EXERCISE)
            .healthGoals(HealthGoal.MAINTAIN)
            .build();
        profile2.setBmiValue(profile2.calculateBMI());
        profile2.setBmiStatus(profile2.getStatusByBMI());
        profile2.setTdeeValue(profile2.calculateTDDE());
        profile2 = healthProfileRepository.save(profile2);

        // Seed DietaryRestrictions
        List<Nutrient> nutrients = nutrientRepository.findAll();
        List<ICD11Code> icdCodes = icd11CodeRepository.findAll();
        Nutrient duong = nutrients.stream().filter(n -> n.getName().equals("Đường")).findFirst().orElse(null);
        ICD11Code diabetesCode = icdCodes.stream()
            .filter(c -> c.getIcdCode().equals("5A10"))
            .findFirst()
            .orElse(null);

        if (duong != null && diabetesCode != null) {
            DietaryRestriction restriction = DietaryRestriction.builder()
                .userHealthProfile(profile1)
                .nutrient(duong)
                .icdCode(diabetesCode)
                .name("Hạn chế đường")
                .description("Hạn chế đường do tiền đái tháo đường")
                .limitType(LimitType.MAX)
                .limitValue(new BigDecimal("25.00"))
                .limitUnit("g")
                .frequency(Frequency.DAILY)
                .status(RestrictionStatus.ACTIVE)
                .sourceOfAdvice("Bác sĩ nội tiết")
                .build();
            dietaryRestrictionRepository.save(restriction);
        }
    }

    private void seedDailyLogs(List<Account> accounts) {
        if (accounts.isEmpty()) return;
        Account user1 = accounts.get(0);

        DailyLog dailyLog = DailyLog.builder()
            .account(user1)
            .loggedDate(LocalDate.now())
            .stepAmount(5000)
            .totalCaloriesIn(BigDecimal.ZERO)
            .totalCaloriesOut(BigDecimal.ZERO)
            .build();
        dailyLog = dailyLogRepository.save(dailyLog);

        // Seed MealLogs
        List<FoodItem> foodItems = foodItemRepository.findAll();
        if (!foodItems.isEmpty()) {
            MealLog mealLog = MealLog.builder()
                .dailyLog(dailyLog)
                .mealType(MealType.BREAKFAST)
                .loggedTime(LocalTime.of(7, 0))
                .notes("Bữa sáng đầy đủ")
                .totalCalories(BigDecimal.ZERO)
                .build();
            mealLog = mealLogRepository.save(mealLog);

            // Seed MealFood
            MealFood mealFood = MealFood.builder()
                .mealLog(mealLog)
                .foodItem(foodItems.get(0))
                .quantity(2)
                .calories(BigDecimal.ZERO)
                .build();
            mealFood.calculateCalories();
            mealFoodRepository.save(mealFood);

            mealLog.calculateTotalCalories();
            mealLogRepository.save(mealLog);
        }

        // Seed ExerciseLog
        List<ExerciseType> exercises = exerciseTypeRepository.findAll();
        if (!exercises.isEmpty()) {
            ExerciseLog exerciseLog = ExerciseLog.builder()
                .dailyLog(dailyLog)
                .exerciseType(exercises.get(0))
                .duration(new BigDecimal("30"))
                .caloriesOut(BigDecimal.ZERO)
                .build();
            exerciseLog.calculateEAT();
            exerciseLogRepository.save(exerciseLog);
        }

        dailyLog.calculateCaloriesIn();
        // Only calculate calories out if health profile exists
        if (user1.getHealthProfile() != null) {
            dailyLog.calculateCaloriesOut();
        }
        dailyLogRepository.save(dailyLog);
    }

    private void seedPosts(List<Account> accounts) {
        if (accounts.size() < 2) return;

        Account user1 = accounts.get(0);
        Account user2 = accounts.get(1);

        Post post1 = Post.builder()
            .account(user1)
            .content("Xin chào mọi người! Mình mới bắt đầu hành trình giảm cân. Sau 4 tuần kiên trì, mình đã giảm được 3kg rồi! 💪🥗")
            .heart(45)
            .isDeleted(false)
            .status(PostStatus.APPROVED)
            .build();
        postRepository.save(post1);

        Post post2 = Post.builder()
            .account(user2)
            .content("Chia sẻ thực đơn giảm cân trong 1 ngày của mình:\n- Sáng: Yến mạch + chuối\n- Trưa: Cơm gạo lứt + ức gà\n- Tối: Canh rau + trứng luộc")
            .heart(32)
            .isDeleted(false)
            .status(PostStatus.APPROVED)
            .build();
        postRepository.save(post2);

        // Seed Comments
        Comment comment1 = Comment.builder()
            .post(post1)
            .account(user2)
            .content("Chúc mừng bạn! Tiếp tục phấn đấu nhé! 👏")
            .isDeleted(false)
            .status(CommentStatus.APPROVED)
            .build();
        commentRepository.save(comment1);
    }

    private void seedTransactions(List<Account> accounts) {
        if (accounts.size() < 2) return;
        List<ServicePlan> plans = servicePlanRepository.findAll();
        if (plans.size() < 2) return;

        Account user1 = accounts.get(0);
        Account user2 = accounts.get(1);

        Transaction txn1 = Transaction.builder()
            .account(user1)
            .servicePlan(plans.get(1))
            .amount(plans.get(1).getPrice())
            .paymentMethod("MOMO")
            .status(TransactionStatus.COMPLETED)
            .build();
        transactionRepository.save(txn1);

        Transaction txn2 = Transaction.builder()
            .account(user2)
            .servicePlan(plans.get(2))
            .amount(plans.get(2).getPrice())
            .paymentMethod("VNPAY")
            .status(TransactionStatus.COMPLETED)
            .build();
        transactionRepository.save(txn2);
    }

    private void seedFCMTokens(List<Account> accounts) {
        if (accounts.size() < 2) return;

        Account user1 = accounts.get(0);
        Account user2 = accounts.get(1);

        FCMToken token1 = FCMToken.builder()
            .account(user1)
            .token("sample_fcm_token_android_user1")
            .build();
        fcmTokenRepository.save(token1);

        FCMToken token2 = FCMToken.builder()
            .account(user2)
            .token("sample_fcm_token_ios_user2")
            .build();
        fcmTokenRepository.save(token2);
    }
}
