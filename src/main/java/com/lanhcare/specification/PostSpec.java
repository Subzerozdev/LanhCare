package com.lanhcare.specification;

import com.lanhcare.entity.Post;
import com.lanhcare.enums.PostStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class PostSpec {

    public static Specification<Post> filterByCriteria(String search) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicateList = new ArrayList<>();

            predicateList.add(criteriaBuilder.equal(root.get("status"), PostStatus.APPROVED));
            predicateList.add(criteriaBuilder.equal(root.get("isDeleted"), false));

            // Content
            String content = search;
            if (content != null) {
                content = content.toLowerCase();
                Predicate contentLike = criteriaBuilder.like(criteriaBuilder.lower(root.get("content")), "%" + content + "%");
                predicateList.add(criteriaBuilder.or(contentLike));
            }

            Predicate[] predicates = predicateList.toArray(new Predicate[0]);
            return criteriaBuilder.and(predicates);
        };
    }
}
