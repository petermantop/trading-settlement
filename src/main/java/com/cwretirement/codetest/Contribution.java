package com.cwretirement.codetest;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Contribution {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private long memberAccountId;
    private String transactionId;
    private Date dateEntered;
    private Date datePosted;
    private BigDecimal amount;
    private long custodianReference;

    public Contribution(long memberAccountId, String transactionId, Date dateEntered, Date datePosted, BigDecimal amount, long custodianReference) {
        this.memberAccountId = memberAccountId;
        this.transactionId = transactionId;
        this.dateEntered = dateEntered;
        this.datePosted = datePosted;
        this.amount = amount;
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
        if (o == null || getClass() != o.getClass()) return false;
        Contribution that = (Contribution) o;
        return memberAccountId == that.memberAccountId &&
                custodianReference == that.custodianReference &&
                transactionId.equals(that.transactionId) &&
                dateEntered.equals(that.dateEntered) &&
                datePosted.equals(that.datePosted) &&
                amount.equals(that.amount);
    }

    @Override
    public int hashCode() {
        return Objects.hash(memberAccountId, transactionId, dateEntered, datePosted, amount, custodianReference);
    }
}
