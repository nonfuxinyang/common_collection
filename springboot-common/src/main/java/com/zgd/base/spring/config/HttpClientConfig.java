package com.zgd.base.spring.config;

import com.alibaba.fastjson.JSON;
import com.zgd.base.spring.util.http.HttpClientUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @Author: zgd
 * @Date: 2019/3/25 16:53
 * @Description:
 */
@Configuration
@ConditionalOnClass(value = {RestTemplate.class, CloseableHttpClient.class})
@ConditionalOnProperty(prefix = "http.http-client",name = "enable",havingValue = "true",matchIfMissing = false)
@EnableConfigurationProperties({HttpClientPoolProperties.class})
@Data
@Slf4j
public class HttpClientConfig {

  @Autowired
  private HttpClientPoolProperties httpClientPoolProperties;

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }
  /**
   * 初始化RestTemplate,并加入spring的Bean工厂，由spring统一管理
   */
  @Bean(name = "httpClientTemplate")
  public RestTemplate restTemplate(ClientHttpRequestFactory factory) {
    return createRestTemplate(factory);
  }

  /**
   * 创建HTTP客户端工厂
   */
  @Bean(name = "clientHttpRequestFactory")
  public ClientHttpRequestFactory clientHttpRequestFactory() {
    /**
     *  maxTotalConnection 和 maxConnectionPerRoute 必须要配
     */
    if (httpClientPoolProperties.getMaxTotalConnect() <= 0) {
      throw new IllegalArgumentException("invalid maxTotalConnection: " + httpClientPoolProperties.getMaxTotalConnect());
    }
    if (httpClientPoolProperties.getMaxConnectPerRoute() <= 0) {
      throw new IllegalArgumentException("invalid maxConnectionPerRoute: " + httpClientPoolProperties.getMaxConnectPerRoute());
    }
    HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(httpClient());
    // 连接超时
    clientHttpRequestFactory.setConnectTimeout(httpClientPoolProperties.getConnectTimeout());
    // 数据读取超时时间，即SocketTimeout
    clientHttpRequestFactory.setReadTimeout(httpClientPoolProperties.getReadTimeout());
    // 从连接池获取请求连接的超时时间，不宜过长，必须设置，比如连接不够用时，时间过长将是灾难性的
    clientHttpRequestFactory.setConnectionRequestTimeout(httpClientPoolProperties.getConnectionRequestTimout());
    return clientHttpRequestFactory;
  }



  /**
   * 初始化支持异步的RestTemplate,并加入spring的Bean工厂，由spring统一管理,如果你用不到异步，则无须创建该对象
   * 这个类过时了
   * @return
   */
/*  @Bean(name = "asyncRestTemplate")
  @ConditionalOnMissingBean(AsyncRestTemplate.class)
  public AsyncRestTemplate asyncRestTemplate(RestTemplate restTemplate) {
    final Netty4ClientHttpRequestFactory factory = new Netty4ClientHttpRequestFactory();
    factory.setConnectTimeout(this.connectionTimeout);
    factory.setReadTimeout(this.readTimeout);
    return new AsyncRestTemplate(factory, restTemplate);
  }*/

  /**
   * 配置httpClient
   *
   * @return
   */
  @Bean
  public HttpClient httpClient() {
    HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
    try {
      //设置信任ssl访问
      SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, (arg0, arg1) -> true).build();

      httpClientBuilder.setSSLContext(sslContext);
      HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;
      SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
      Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
              // 注册http和https请求
              .register("http", PlainConnectionSocketFactory.getSocketFactory())
              .register("https", sslConnectionSocketFactory).build();

      //使用Httpclient连接池的方式配置(推荐)，同时支持netty，okHttp以及其他http框架
      PoolingHttpClientConnectionManager poolingHttpClientConnectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
      // 最大连接数
      poolingHttpClientConnectionManager.setMaxTotal(httpClientPoolProperties.getMaxTotalConnect());
      // 同路由并发数
      poolingHttpClientConnectionManager.setDefaultMaxPerRoute(httpClientPoolProperties.getMaxConnectPerRoute());
      //配置连接池
      httpClientBuilder.setConnectionManager(poolingHttpClientConnectionManager);
      // 重试次数 默认以下几个异常是不重试的InterruptedIOException.class, UnknownHostException.class, ConnectException.class, SSLException.class
      httpClientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(httpClientPoolProperties.getRetryTimes(), true));

      //设置默认请求头
      List<Header> headers = HttpClientUtil.getDefaultHeaders();
      httpClientBuilder.setDefaultHeaders(headers);
      //设置长连接保持策略
      httpClientBuilder.setKeepAliveStrategy(connectionKeepAliveStrategy());
      return httpClientBuilder.build();
    } catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
      log.error("初始化HTTP连接池出错", e);
    }
    return null;
  }


  /**
   * 配置长连接保持策略
   * @return
   */
  public ConnectionKeepAliveStrategy connectionKeepAliveStrategy(){
    return (response, context) -> {
      // Honor 'keep-alive' header
      HeaderElementIterator it = new BasicHeaderElementIterator(
              response.headerIterator(HTTP.CONN_KEEP_ALIVE));
      while (it.hasNext()) {
        HeaderElement he = it.nextElement();
        log.info("HeaderElement:{}", JSON.toJSONString(he));
        String param = he.getName();
        String value = he.getValue();
        if (value != null && "timeout".equalsIgnoreCase(param)) {
          try {
            return Long.parseLong(value) * 1000;
          } catch(NumberFormatException e) {
            log.error("解析长连接过期时间异常",e);
          }
        }
      }
      HttpHost target = (HttpHost) context.getAttribute(
              HttpClientContext.HTTP_TARGET_HOST);
      //如果请求目标地址,单独配置了长连接保持时间,使用该配置
      Optional<Map.Entry<String, Integer>> any = httpClientPoolProperties.getKeepAliveTargetHost().entrySet().stream().filter(
              e -> e.getKey().equalsIgnoreCase(target.getHostName())).findAny();
      //否则使用默认长连接保持时间
      return any.map(en -> en.getValue() * 1000L).orElse(httpClientPoolProperties.getKeepAliveTime() * 1000L);
    };
  }






  private RestTemplate createRestTemplate(ClientHttpRequestFactory factory) {
    RestTemplate restTemplate = new RestTemplate(factory);

    //我们采用RestTemplate内部的MessageConverter
    //重新设置StringHttpMessageConverter字符集，解决中文乱码问题
    modifyDefaultCharset(restTemplate);

    //设置错误处理器
    restTemplate.setErrorHandler(new DefaultResponseErrorHandler());

    return restTemplate;
  }

  /**
   * 修改默认的字符集类型为utf-8
   *
   * @param restTemplate
   */
  private void modifyDefaultCharset(RestTemplate restTemplate) {
    List<HttpMessageConverter<?>> converterList = restTemplate.getMessageConverters();
    HttpMessageConverter<?> converterTarget = null;
    for (HttpMessageConverter<?> item : converterList) {
      if (StringHttpMessageConverter.class == item.getClass()) {
        converterTarget = item;
        break;
      }
    }
    if (null != converterTarget) {
      converterList.remove(converterTarget);
    }
    Charset defaultCharset = Charset.forName(httpClientPoolProperties.getCharset());
    converterList.add(1, new StringHttpMessageConverter(defaultCharset));
  }
}
