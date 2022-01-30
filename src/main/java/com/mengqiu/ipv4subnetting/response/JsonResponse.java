package com.mengqiu.ipv4subnetting.response;


public class JsonResponse {

    String status;

    Object data;

    public JsonResponse(String status) {
        this.status = status;
    }

    public JsonResponse(String status, Object data) {
        this.status = status;
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public Object getData() {
        return data;
    }
}
