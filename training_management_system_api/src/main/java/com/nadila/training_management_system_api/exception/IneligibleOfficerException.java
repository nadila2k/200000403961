package com.nadila.training_management_system_api.exception;

import java.util.List;

public class IneligibleOfficerException extends RuntimeException {

    private final List<String> failureReasons;

    public IneligibleOfficerException(String message, List<String> failureReasons) {
        super(message);
        this.failureReasons = failureReasons;
    }

    public List<String> getFailureReasons() {
        return failureReasons;
    }
}
