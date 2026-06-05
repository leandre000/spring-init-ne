package com.utility.billing.common.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchCriteria {
    private String key;
    private Op operation;
    private Object value;

    public enum Op {
        EQUAL,
        LIKE,
        GREATER_THAN,
        LESS_THAN,
        IN
    }
}
