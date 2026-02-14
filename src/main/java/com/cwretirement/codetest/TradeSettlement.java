package com.cwretirement.codetest;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class TradeSettlement {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private Type type;
    private Date dateEntered;
    private Date datePosted;
    private Date dateSettled;
    private Date dateTraded;
    private BigDecimal units;
    private BigDecimal unitPrice;
    private String fundName;
    private BigDecimal amount;
    private String symbolCusip;
    private long custodianReference;

    public enum Type {
        PURCHASE,
        SALE;

        private static final Map<String, Type> TRANSACTION_DESCRIPTION_TO_SETTLEMENT_TYPE = new HashMap<String, Type>() {{
            put("Purchase Cash Settlement", PURCHASE);
            put("Sale Cash Settlement", SALE);
        }};

        public static Type fromString(String string) {
            return TRANSACTION_DESCRIPTION_TO_SETTLEMENT_TYPE.get(string);
        }
    }

    public TradeSettlement(
            Type type,
            Date dateEntered,
            Date datePosted,
            Date dateSettled,
            Date dateTraded,
            BigDecimal units,
            BigDecimal unitPrice,
            String fundName,
            BigDecimal amount,
            String symbolCusip,
            long custodianReference
    ) {
        this.type = type;
        this.dateEntered = dateEntered;
        this.datePosted = datePosted;
        this.dateSettled = dateSettled;
        this.dateTraded = dateTraded;
        this.units = units;
        this.unitPrice = unitPrice;
        this.fundName = fundName;
        this.amount = amount;
        this.symbolCusip = symbolCusip;
        this.custodianReference = custodianReference;
    }

    @Override
    public String toString() {
        try {
            return OBJECT_MAPPER.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TradeSettlement that)) return false;
        return custodianReference == that.custodianReference
                && type == that.type
                && Objects.equals(dateEntered, that.dateEntered)
                && Objects.equals(datePosted, that.datePosted)
                && Objects.equals(dateSettled, that.dateSettled)
                && Objects.equals(dateTraded, that.dateTraded)
                && Objects.equals(units, that.units)
                && Objects.equals(unitPrice, that.unitPrice)
                && Objects.equals(fundName, that.fundName)
                && Objects.equals(amount, that.amount)
                && Objects.equals(symbolCusip, that.symbolCusip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, dateEntered, datePosted, dateSettled, dateTraded, units, unitPrice, fundName, amount, symbolCusip, custodianReference);
    }
}
