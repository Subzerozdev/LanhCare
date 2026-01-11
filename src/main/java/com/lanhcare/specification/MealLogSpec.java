package com.lanhcare.specification;

import com.lanhcare.entity.MealLog;
import com.lanhcare.utils.ValidationUtils;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MealLogSpec {
    public static Specification<MealLog> filterByCriteria(
            Map<String, String> criteria
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();
            int accountId = Integer.parseInt(criteria.get("accountId"));
            predicateList.add(criteriaBuilder.equal(root.get("account").get("id"), accountId));

            LocalDate mealDate = ValidationUtils
                    .isValidDate(criteria.get("mealDate"));
            if (mealDate != null) {
                predicateList.add(criteriaBuilder.equal(root.get("mealDate"), mealDate));
            }

            String mealType = criteria.get("mealType");
            if (!ValidationUtils.isNullOrEmpty(mealType)) {
                predicateList.add(criteriaBuilder.equal(root.get("mealType"), mealType));
            }

            query.orderBy(criteriaBuilder.desc(root.get("mealDate")));

            Predicate[] predicates = predicateList.toArray(new Predicate[0]);
            return criteriaBuilder.and(predicates);
        };
    }

}
