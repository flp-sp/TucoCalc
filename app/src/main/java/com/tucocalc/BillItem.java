package com.tucocalc;

import java.io.Serializable;
import java.math.BigDecimal;

public class BillItem implements Serializable {
    private final String name;
    private final BigDecimal value;

    public BillItem(String name, BigDecimal value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getValue() {
        return value;
    }
}
