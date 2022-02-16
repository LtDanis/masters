package ktu.masters.exception;

import lombok.EqualsAndHashCode;
import lombok.Value;

import static ktu.masters.core.utils.JsonTransformer.GSON;

@EqualsAndHashCode(callSuper = true)
public class ApiException extends RuntimeException {
    private final ErrorModel errorModel;

    public ApiException(int status, String msg) {
        this.errorModel = new ErrorModel(status, msg);
    }

    public String toJson() {
        return GSON.toJson(errorModel);
    }

    public int getStatus() {
        return errorModel.getStatus();
    }

    @Override
    public String getMessage() {
        return errorModel.getMessage();
    }

    @Value
    private static class ErrorModel {
        int status;
        String message;
    }
}

