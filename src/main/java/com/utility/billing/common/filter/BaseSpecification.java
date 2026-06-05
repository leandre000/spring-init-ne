package com.utility.billing.common.filter;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BaseSpecification<T> implements Specification<T> {

    private final List<SearchCriteria> list = new ArrayList<>();

    public BaseSpecification() {}

    public BaseSpecification(List<SearchCriteria> criteria) {
        if (criteria != null) {
            this.list.addAll(criteria);
        }
    }

    public void add(SearchCriteria criteria) {
        this.list.add(criteria);
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        List<Predicate> predicates = new ArrayList<>();

        for (SearchCriteria criteria : list) {
            String key = criteria.getKey();
            Object val = criteria.getValue();
            
            if (val == null) {
                continue;
            }

            jakarta.persistence.criteria.Path<?> path;
            if (key.contains(".")) {
                String[] parts = key.split("\\.");
                jakarta.persistence.criteria.Join<?, ?> join = root.join(parts[0]);
                for (int i = 1; i < parts.length - 1; i++) {
                    join = join.join(parts[i]);
                }
                path = join.get(parts[parts.length - 1]);
            } else {
                path = root.get(key);
            }

            switch (criteria.getOperation()) {
                case EQUAL:
                    predicates.add(builder.equal(path, val));
                    break;
                case LIKE:
                    predicates.add(builder.like(builder.lower(path.as(String.class)), "%" + val.toString().toLowerCase() + "%"));
                    break;
                case GREATER_THAN:
                    if (val instanceof Comparable) {
                        predicates.add(builder.greaterThanOrEqualTo((jakarta.persistence.criteria.Expression) path, (Comparable) val));
                    }
                    break;
                case LESS_THAN:
                    if (val instanceof Comparable) {
                        predicates.add(builder.lessThanOrEqualTo((jakarta.persistence.criteria.Expression) path, (Comparable) val));
                    }
                    break;
                case IN:
                    if (val instanceof Collection) {
                        predicates.add(path.in((Collection<?>) val));
                    } else if (val.getClass().isArray()) {
                        predicates.add(path.in((Object[]) val));
                    }
                    break;
            }
        }

        try {
            root.get("deleted");
            predicates.add(builder.equal(root.get("deleted"), false));
        } catch (IllegalArgumentException e) {
            // Entity does not have soft delete column
        }

        return builder.and(predicates.toArray(new Predicate[0]));
    }
}
