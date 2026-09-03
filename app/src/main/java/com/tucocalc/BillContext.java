package com.tucocalc;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class BillContext {

    private final int peopleCount;
    private final BigDecimal totalWithTip;
    private final BigDecimal tipPercent;

    private final List<List<BillItem>> personItems;

    public BillContext(int peopleCount, BigDecimal totalWithTip, BigDecimal tipPercent) {
        this.peopleCount = peopleCount;
        this.totalWithTip = totalWithTip;
        this.tipPercent = tipPercent;
        this.personItems = new ArrayList<>();
        for (int i = 0; i < peopleCount; i++) {
            personItems.add(new ArrayList<>());
        }
    }

    public int getPeopleCount() {
        return peopleCount;
    }

    public BigDecimal getTotalWithTip() {
        return totalWithTip;
    }

    public BigDecimal getTipPercent() {
        return tipPercent;
    }

    public List<BillItem> getPersonItems(int personIndex) {
        return personItems.get(personIndex);
    }

    public void addPersonItem(int personIndex, BillItem item) {
        personItems.get(personIndex).add(item);
    }

    public BigDecimal getIndividualSum(int personIndex) {
        BigDecimal sum = BigDecimal.ZERO;
        for (BillItem item : personItems.get(personIndex)) {
            sum = sum.add(item.getValue());
        }
        return sum;
    }

    public BigDecimal getTotalIndividualSum() {
        BigDecimal sum = BigDecimal.ZERO;
        for (int i = 0; i < peopleCount; i++) {
            sum = sum.add(getIndividualSum(i));
        }
        return sum;
    }

    public BigDecimal getTotalWithoutTip() {
        return totalWithTip.divide(BigDecimal.valueOf(100).add(tipPercent), 20, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    public BigDecimal getSharedTotal() {
        BigDecimal shared = getTotalWithoutTip().subtract(getTotalIndividualSum());
        return shared.max(BigDecimal.ZERO);
    }

    public BigDecimal getSharedPerPerson() {
        if (peopleCount == 0) {
            return BigDecimal.ZERO;
        }
        return getSharedTotal().divide(BigDecimal.valueOf(peopleCount), 2, RoundingMode.HALF_UP);
    }

    public BigDecimal getPersonSubtotal(int personIndex) {
        return getSharedPerPerson().add(getIndividualSum(personIndex));
    }

    public BigDecimal getPersonSubtotalWithTip(int personIndex) {
        BigDecimal subtotal = getPersonSubtotal(personIndex);
        BigDecimal tip = subtotal.multiply(tipPercent).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        return subtotal.add(tip);
    }

    public BigDecimal getSharedPerPersonWithTip() {
        BigDecimal shared = getSharedPerPerson();
        BigDecimal tip = shared.multiply(tipPercent).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        return shared.add(tip);
    }

    public BigDecimal getTotalOfPersonTotals() {
        BigDecimal sum = BigDecimal.ZERO;
        for (int i = 0; i < peopleCount; i++) {
            sum = sum.add(getPersonSubtotalWithTip(i));
        }
        return sum;
    }
}
