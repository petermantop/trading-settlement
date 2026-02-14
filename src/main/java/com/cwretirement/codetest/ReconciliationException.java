package com.cwretirement.codetest;

public class ReconciliationException extends RuntimeException {

    private Type type;

    public ReconciliationException(Type type, String message) {
        super(String.format("%s: %s", type, message));
        this.type = type;
    }

    public ReconciliationException(Type type) {
        super(type.toString());
        this.type = type;
    }

    public ReconciliationException(Type type, Exception e) {
        super(type.toString(), e);
        this.type = type;
    }

    public enum Type {SERVICE_UNAVAILABLE, SERVICE_TIMEOUT, CANNOT_RECONCILE}


}
