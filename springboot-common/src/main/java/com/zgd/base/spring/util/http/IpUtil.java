package com.zgd.base.spring.util.http;

import javax.servlet.http.HttpServletRequest;

/**
 * 获取用户真实的ip地址
 */
public class IpUtil {

  public static String getIpAdrress(HttpServletRequest request) {
    String ip = null;

    //X-Forwarded-For：Squid 服务代理
    String ipAddresses = request.getHeader("X-Forwarded-For");
    String unknown = "unknown";
    if (ipAddresses == null || ipAddresses.length() == 0 || unknown.equalsIgnoreCase(ipAddresses)) {
      //Proxy-Client-IP：apache 服务代理
      ipAddresses = request.getHeader("Proxy-Client-IP");
    }

    if (ipAddresses == null || ipAddresses.length() == 0 || unknown.equalsIgnoreCase(ipAddresses)) {
      //WL-Proxy-Client-IP：weblogic 服务代理
      ipAddresses = request.getHeader("WL-Proxy-Client-IP");
    }

    if (ipAddresses == null || ipAddresses.length() == 0 || unknown.equalsIgnoreCase(ipAddresses)) {
      //HTTP_CLIENT_IP：有些代理服务器
      ipAddresses = request.getHeader("HTTP_CLIENT_IP");
    }

    if (ipAddresses == null || ipAddresses.length() == 0 || unknown.equalsIgnoreCase(ipAddresses)) {
      //X-Real-IP：nginx服务代理
      ipAddresses = request.getHeader("X-Real-IP");
    }

    //有些网络通过多层代理，那么获取到的ip就会有多个，一般都是通过逗号（,）分割开来，并且第一个ip为客户端的真实IP
    if (ipAddresses != null && ipAddresses.length() != 0) {
      ip = ipAddresses.split(",")[0];
    }

    //还是不能获取到，最后再通过request.getRemoteAddr();获取
    if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ipAddresses)) {
      ip = request.getRemoteAddr();
    }
    return ip;
  }

  public static String getIpLocation(String ip) {
    //只能支持少量低频次调用,做了限流,循环100条只有前6次成功,后面都是限流失败,4631ms
    String taobaoApi = "http://ip.taobao.com/service/getIpInfo.php?ip=" + ip;
    //已经关闭
    String sinaApi = "http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=js&ip=" + ip;
    //需要ak参数(注册账号),有并发限制,100条6357ms,普通已认证开发者账号1天3w请求,未认证1天1k,限流1秒10次,网址 http://lbsyun.baidu.com/apiconsole/auth/privilege
    //并且返回的中文是unicode编码,很坑
    String baiduApi = "http://api.map.baidu.com/location/ip?ak=xxxxxxx&ip=" + ip;
    //返回页面
    String pconlineApi = "https://whois.pconline.com.cn/?ip=" + ip;
    //速度尚可 100条4521ms
    String ws126Api = "http://ip.ws.126.net/ipquery?ip=" + ip;
    //速度很慢,一个月免费3w条,100条24808ms,apiKey需要注册账号
    String geoApi = "https://api.ipgeolocation.io/ipgeo?apiKey=xxxxxxx&ip=" + ip;
    //速度很慢,100条79133ms
    String ip_Api = "http://ip-api.com/json/?ip=" + ip;
    //一个月3w条免费, 100条耗时 37394ms
    String ipapi = "https://ipapi.co/" + ip + "/json";
    HttpResult httpResult = HttpClientUtil.get(ws126Api);
    if (HttpClientUtil.is200OK(httpResult)) {
      return httpResult.getRespStr();
    }
    return "无法确定";
  }

}