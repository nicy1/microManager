package response;

import models.Error;

public abstract class BaseResponse<T> {
    public T data;
    public Error error;
    public boolean success;

    public void markAsSuccess(T data) {
        this.data = data;
        this.success = true;
    }

    public void markAsError(String message, String stack) {
        this.error = new Error();
        this.error.stack = stack;
        this.error.message = message;
        this.success = false;
    }

    public void markAsError(String message) {
        this.error = new Error();
        this.error.stack = "";
        this.error.message = message;
        this.success = false;
    }
}
