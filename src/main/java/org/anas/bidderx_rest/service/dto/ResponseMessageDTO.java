package org.anas.bidderx_rest.service.dto;

public class ResponseMessageDTO {
    private String message;
    private Object data;

    public ResponseMessageDTO() {
    }

    public ResponseMessageDTO(String message) {
        this.message = message;
    }

    public ResponseMessageDTO(String message, Object data) {
        this.message = message;
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
