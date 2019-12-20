package com.zgd.base.spring.util.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zgd
 * @date 2019/11/26 11:15
 */
public class HttpResult {
  private static Logger log = LoggerFactory.getLogger(HttpResult.class);

  private int code;
  private String respStr;

  private boolean isError = false;

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

  public HttpResult error(){
    this.isError = true;
    return this;
  }


  public boolean is200OK() {
    return  this.getCode() == HttpStatus.SC_OK;
  }

  public  String getValue( String key) {
    if (is200OK()) {
      try {
        JSONObject jsonObject = JSON.parseObject(getRespStr());
        return jsonObject.getString(key);
      } catch (Exception e) {
        log.warn("解析失败 ", e);
      }
    }
    return "";
  }
}