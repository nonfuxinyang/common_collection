package com.zgd.base.spring.util.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * HttpClientUtil
 *
 * @author zgd
 * @date 2019/8/13 15:46
 */
@Slf4j
public class HttpClientUtil {

  private HttpClient httpClient;

  public HttpClientUtil(HttpClient httpClient) {
    this.httpClient = httpClient;
  }

  /**
   * 发起get请求
   *
   * @return java.lang.String
   * @author zgd
   * @date 2019/8/13 15:52
   */
  public HttpResult get(String url, List<NameValuePair> params) {
    HttpGet get = null;
    try {
      URIBuilder uriBuilder = new URIBuilder(url);
      if (CollectionUtils.isNotEmpty(params)){
        uriBuilder.setParameters(params);
      }
      get = new HttpGet(uriBuilder.build());

      log.info("请求的参数 uri:{}\treq: {}", get.getURI(), params);
      HttpResponse response = httpClient.execute(get);
      String resp = EntityUtils.toString(response.getEntity());
      int code = response.getStatusLine().getStatusCode();
      log.info("返回的信息 resp：{}", resp);
      return new HttpResult(code, resp);
    } catch (Exception e) {
      log.warn("请求异常 --  error!", e);
    } finally {
      if (get != null) {
        get.releaseConnection();
      }
    }
    return null;
  }

  /**
   * 发送POST请求（普通表单形式）
   *
   * @return java.lang.String
   * @author zgd
   * @date 2019/8/13 15:56
   */
  public HttpResult postForm(String path, List<NameValuePair> parametersBody) {
    HttpEntity entity = new UrlEncodedFormEntity(parametersBody, StandardCharsets.UTF_8);
    return postRequest(path, MediaType.APPLICATION_FORM_URLENCODED_VALUE, entity);
  }


  /**
   * 发送POST请求（JSON形式）
   *
   * @return java.lang.String
   * @author zgd
   * @date 2019/8/13 15:56
   */
  public HttpResult postJSON(String path, String json) {
    StringEntity entity = new StringEntity(json, StandardCharsets.UTF_8);
    return postRequest(path, MediaType.APPLICATION_JSON_VALUE, entity);
  }


  /**
   * 发送post请求
   *
   * @param path
   * @param mediaType
   * @param entity
   * @return
   */
  public HttpResult postRequest(String path, String mediaType, HttpEntity entity) {
    log.info("[postRequest] resourceUrl: {}", path);
    HttpPost post = new HttpPost(path);
    post.addHeader(HttpHeaders.CONTENT_TYPE, mediaType);
    post.setEntity(entity);
    try {
      HttpResponse response = httpClient.execute(post);
      String resp = EntityUtils.toString(response.getEntity());
      int code = response.getStatusLine().getStatusCode();
      log.info("返回的信息 resp：{}", resp);
      return new HttpResult(code, resp);
    } catch (Exception e) {
      log.warn("请求异常 --  error!", e);
    } finally {
      post.releaseConnection();
    }
    return null;
  }



  public static boolean is200OK(HttpResult result){
    return result != null && result.getCode() == HttpStatus.SC_OK ;
  }


  public static String getValue(HttpResult result,String key){
    if (is200OK(result)){
      try {
        JSONObject jsonObject = JSON.parseObject(result.getRespStr());
        return jsonObject.getString(key);
      } catch (Exception e) {
        log.warn("解析失败 ",e);
      }
    }
    return null;
  }


  public static class HttpResult {
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

}
