package com.zgd.base.spring.util.http;

/**
 * @author zgd
 * @date 2019/11/26 11:15
 */
public class HttpResult {
    private int code;
    private String respStr;

    public int getCode() {
      return code;
    }

    public void setCode(int code) {
      this.code = code;
    }

    public String getRespStr() {
      return respStr;
    }

    public void setRespStr(String respStr) {
      this.respStr = respStr;
    }

    public HttpResult(int code, String respStr) {
      this.code = code;
      this.respStr = respStr;
    }

    public HttpResult() {
    }
  }