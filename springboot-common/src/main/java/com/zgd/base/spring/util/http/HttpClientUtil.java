package com.zgd.base.spring.util.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.swagger.models.Scheme;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.http.MediaType;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * HttpClientUtil
 *
 * @author zgd
 * @date 2019/8/13 15:46
 */
@Slf4j
public class HttpClientUtil {

  private static HttpClient httpClient;

  public static HttpResult get(String url) {
    return get(url, null);
  }

  static {
    getHttpClient();
  }

  /**
   * 发起get请求
   *
   * @return java.lang.String
   * @author zgd
   * @date 2019/8/13 15:52
   */
  public static HttpResult get(String url, List<NameValuePair> params) {
    HttpGet get = null;
    try {

      URIBuilder uriBuilder = new URIBuilder(url);
      uriBuilder.setCharset(StandardCharsets.UTF_8);
      if (CollectionUtils.isNotEmpty(params)) {
        uriBuilder.setParameters(params);
      }
      get = new HttpGet(uriBuilder.build());
      log.info("请求的参数 url: {}\treq: {}", get.getURI(), params);

      HttpResponse response = httpClient.execute(get);
      String resp = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
      int code = response.getStatusLine().getStatusCode();
      log.info("返回的信息 resp: {}", resp);
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
  public static HttpResult postForm(String url, HashMap<String, String> form) {
    List<BasicNameValuePair> params = form.entrySet().stream().map(en -> new BasicNameValuePair(en.getKey(), en.getValue()))
            .collect(Collectors.toList());
    HttpEntity entity = new UrlEncodedFormEntity(params, StandardCharsets.UTF_8);
    return postRequest(url, MediaType.APPLICATION_FORM_URLENCODED_VALUE, entity);
  }


  /**
   * 发送POST请求（JSON形式）
   *
   * @return java.lang.String
   * @author zgd
   * @date 2019/8/13 15:56
   */
  public static HttpResult postJSON(String url, String json) {
    StringEntity entity = new StringEntity(json, StandardCharsets.UTF_8);
    return postRequest(url, MediaType.APPLICATION_JSON_VALUE, entity);
  }


  /**
   * 发送post请求
   *
   * @param url
   * @param mediaType
   * @param entity
   * @return
   */
  public static HttpResult postRequest(String url, String mediaType, HttpEntity entity) {
    log.info("[postRequest] resourceUrl: {}", url);
    HttpPost post = null;
    try {
      URIBuilder uriBuilder = new URIBuilder(url);
      post = new HttpPost(uriBuilder.build());
      post.addHeader(HttpHeaders.CONTENT_TYPE, mediaType);
      post.setEntity(entity);
      HttpResponse response = httpClient.execute(post);
      String resp = EntityUtils.toString(response.getEntity());
      int code = response.getStatusLine().getStatusCode();
      log.info("返回的信息 resp：{}", resp);
      return new HttpResult(code, resp);
    } catch (Exception e) {
      log.warn("请求异常 --  error!", e);
    } finally {
      if (post != null) {
        post.releaseConnection();
      }
    }
    return null;
  }


  public static boolean is200OK(HttpResult result) {
    return result != null && result.getCode() == HttpStatus.SC_OK;
  }


  public static String getValue(HttpResult result, String key) {
    if (is200OK(result)) {
      try {
        JSONObject jsonObject = JSON.parseObject(result.getRespStr());
        return jsonObject.getString(key);
      } catch (Exception e) {
        log.warn("解析失败 ", e);
      }
    }
    return null;
  }


  /**
   * 获取默认的httpClient
   *
   * @return
   */
  public static HttpClient getHttpClient() {
    if (httpClient == null) {
      RequestConfig requestConfig = RequestConfig.custom()
              .setConnectTimeout(3000)
              .setConnectionRequestTimeout(3000)
              .setSocketTimeout(5000).build();
      httpClient = HttpClientBuilder.create().setDefaultRequestConfig(requestConfig)
              .setMaxConnTotal(1000)
              .setMaxConnPerRoute(100)
              .setDefaultHeaders(getDefaultHeaders())
              .build();
    }
    return httpClient;
  }


  /**
   * 设置默认的请求头
   *
   * @return
   */
  public static List<Header> getDefaultHeaders() {
    List<Header> headers = new ArrayList<>();
    headers.add(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.169 Safari/537.36"));
    headers.add(new BasicHeader("Accept-Encoding", "gzip,deflate"));
    headers.add(new BasicHeader("Accept-Language", "zh-CN"));
    headers.add(new BasicHeader("Connection", "Keep-Alive"));
    headers.add(new BasicHeader("Accept", "*/*"));
    return headers;
  }


}
