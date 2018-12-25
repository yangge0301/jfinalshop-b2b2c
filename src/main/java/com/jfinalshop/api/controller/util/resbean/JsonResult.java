package com.jfinalshop.api.controller.util.resbean;

public class JsonResult {
    private String resultCode="0";
    private String resultMsg="";
    private Object obj;
    private Object reserve;
    private String sessionId;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public JsonResult(String resultCode, String resultMsg, Object obj, Object reserve,String sessionId) {
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
        this.obj = obj;
        this.reserve = reserve;
        this.sessionId=sessionId;
    }

    public Object getReserve() {
        return reserve;
    }

    public void setReserve(Object reserve) {
        this.reserve = reserve;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    public Object getObj() {
        return obj;
    }

    public void setObj(Object obj) {
        this.obj = obj;
    }
}
