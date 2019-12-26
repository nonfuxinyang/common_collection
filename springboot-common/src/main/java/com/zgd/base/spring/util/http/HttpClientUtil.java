package com.zgd.base.spring.util.http;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.http.*;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * HttpClientUtil
 *
 * @author zgd
 * @date 2019/8/13 15:46
 */
public class HttpClientUtil {

  private static Logger log = LoggerFactory.getLogger(HttpClientUtil.class);


  public static HttpResult get(HttpClient client, String url) {
    return get(client, url, null);
  }

  /**
   * 发起get请求
   *
   * @return java.lang.String
   * @author zgd
   * @date 2019/8/13 15:52
   */
  public static HttpResult get(HttpClient client, String url, List<NameValuePair> params) {
    HttpGet get = null;
    long s = System.currentTimeMillis();
    HttpResult result = new HttpResult();
    try {
      URIBuilder uriBuilder = new URIBuilder(url);
      uriBuilder.setCharset(StandardCharsets.UTF_8);
      if (CollectionUtils.isNotEmpty(params)) {
        uriBuilder.setParameters(params);
      }
      get = new HttpGet(uriBuilder.build());
      log.debug("请求的参数 url: {}\treq: {}", get.getURI(), params);

      HttpResponse response = client.execute(get);
      String resp = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
      int code = response.getStatusLine().getStatusCode();
      log.debug("返回的信息 resp: {}", resp);
      return new HttpResult(code, resp);
    } catch (Exception e) {
      log.warn("请求异常 --  error!", e);
      result.error().setErrMsg(e.getMessage());
    } finally {
      log.info("请求:-- {} --耗时 {} ms", url, System.currentTimeMillis() - s);
      if (get != null) {
        get.releaseConnection();
      }
    }
    return result;
  }

  /**
   * 发送POST请求（普通表单形式）
   *
   * @return java.lang.String
   * @author zgd
   * @date 2019/8/13 15:56
   */
  public static HttpResult postForm(HttpClient client, String url, Map<String, String> form) {
    List<BasicNameValuePair> params = new ArrayList<>();
    for (Map.Entry<String, String> en : form.entrySet()) {
      params.add(new BasicNameValuePair(en.getKey(), en.getValue()));
    }
    HttpEntity entity = new UrlEncodedFormEntity(params, StandardCharsets.UTF_8);
    return postRequest(client, url,null, MediaType.APPLICATION_FORM_URLENCODED_VALUE, entity);
  }

  /**
   * 发送POST请求（普通表单形式）
   *
   * @return java.lang.String
   * @author zgd
   * @date 2019/8/13 15:56
   */
  public static HttpResult postForm(HttpClient client, String url, Map<String, String> header, Map<String, String> form) {
    List<BasicNameValuePair> params = new ArrayList<>();
    for (Map.Entry<String, String> en : form.entrySet()) {
      params.add(new BasicNameValuePair(en.getKey(), en.getValue()));
    }
    HttpEntity entity = new UrlEncodedFormEntity(params, StandardCharsets.UTF_8);
    return postRequest(client, url,header, MediaType.APPLICATION_FORM_URLENCODED_VALUE, entity);
  }


  /**
   * 发送POST请求（JSON形式）
   *
   * @return java.lang.String
   * @author zgd
   * @date 2019/8/13 15:56
   */
  public static HttpResult postJSON(HttpClient client, String url, Map<String, String> header, String json) {
    StringEntity entity = new StringEntity(json, StandardCharsets.UTF_8);
    return postRequest(client, url,header, MediaType.APPLICATION_JSON_VALUE, entity);
  }

  /**
   * 发送POST请求（JSON形式）
   *
   * @return java.lang.String
   * @author zgd
   * @date 2019/8/13 15:56
   */
  public static HttpResult postJSON(HttpClient client, String url, String json) {
    StringEntity entity = new StringEntity(json, StandardCharsets.UTF_8);
    return postRequest(client, url,null, MediaType.APPLICATION_JSON_VALUE, entity);
  }


  /**
   * 发送post请求
   *
   * @param url
   * @param mediaType
   * @param entity
   * @return
   */
  public static HttpResult postRequest(HttpClient client, String url, Map<String, String> header, String mediaType, HttpEntity entity) {
    log.debug("[postRequest] resourceUrl: {}", url);
    long s = System.currentTimeMillis();
    HttpPost post = null;
    HttpResult httpResult = new HttpResult();
    try {
      URIBuilder uriBuilder = new URIBuilder(url);
      post = new HttpPost(uriBuilder.build());
      post.addHeader(HttpHeaders.CONTENT_TYPE, mediaType);
      if (MapUtils.isNotEmpty(header)){
        for (Map.Entry<String, String> entry : MapUtils.iterableMap(header).entrySet()) {
          post.addHeader(entry.getKey(),entry.getValue());
        }
      }
      post.setEntity(entity);
      HttpResponse response = client.execute(post);
      String resp = EntityUtils.toString(response.getEntity());
      int code = response.getStatusLine().getStatusCode();
      log.debug("返回的信息 resp：{}", resp);
      return new HttpResult(code, resp);
    }catch (Exception e) {
      log.warn("请求异常 --  error", e);
      httpResult.error().setErrMsg(e.getMessage());
    } finally {
      log.info("请求:-- {} --耗时 {} ms", url, System.currentTimeMillis() - s);
      if (post != null) {
        post.releaseConnection();
      }
    }
    return httpResult;
  }



  /**
   * 获取客户端
   *
   * @param connTimeoutMills    建立连接的超时时间
   * @param getConnTimeoutMills 从连接池获取连接的超时时间
   * @param readTimeoutMills    客户端从服务器读取数据的超时时间
   * @return
   */
  public static HttpClient getHttpClient(int connTimeoutMills, int getConnTimeoutMills, int readTimeoutMills) {
    RequestConfig requestConfig = RequestConfig.custom()
            //建立连接的超时时间
            .setConnectTimeout(connTimeoutMills)
            //指从连接池获取连接的timeout
            .setConnectionRequestTimeout(getConnTimeoutMills)
            //客户端从服务器读取数据的timeout
            .setSocketTimeout(readTimeoutMills).build();
    return HttpClientBuilder.create().setDefaultRequestConfig(requestConfig)
            .setMaxConnTotal(1000)
            .setMaxConnPerRoute(100)
            .setDefaultHeaders(getDefaultHeaders())
            .build();
  }

  /**
   * 获取默认的httpClient
   *
   * @return
   */
  public static HttpClient getHttpClient() {
    RequestConfig requestConfig = RequestConfig.custom()
            //建立连接的超时时间
            .setConnectTimeout(3000)
            //指从连接池获取连接的timeout
            .setConnectionRequestTimeout(3000)
            //客户端从服务器读取数据的timeout
            .setSocketTimeout(5000).build();
    return HttpClientBuilder.create().setDefaultRequestConfig(requestConfig)
            .setMaxConnTotal(1000)
            .setMaxConnPerRoute(100)
            .setDefaultHeaders(getDefaultHeaders())
            .build();
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
