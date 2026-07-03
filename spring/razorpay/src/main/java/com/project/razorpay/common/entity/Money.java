package com.project.razorpay.common.entity;

import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
public class Money {
    private int amountUnits;
    private String currency;

    private Money(int amountUnits, String currency) {
        this.amountUnits = amountUnits;
        this.currency = currency;
    }

    public Money of(int amountUnits, String currency) {
        return new Money(amountUnits, currency);
    }

    public Money inr(int amountUnits) {
        return new Money(amountUnits, "INR");
    }

    public Money add(Money money) {
        if(!this.currency.equals(money.currency)) {
            throw new IllegalArgumentException("Cannot add Money with different currency");
        }

        return new Money(this.amountUnits + money.amountUnits, this.currency);
    }

    public Money subtract(Money money) {
        if(!this.currency.equals(money.currency)) {
            throw new IllegalArgumentException("Cannot subtract Money with different currency");
        }

        return new Money(this.amountUnits - money.amountUnits, this.currency);
    }

}
