package com.zgd.base.restful.error;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.zgd.base.restful.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Optional;

/**
 * ErrorCache
 *
 * @author zgd
 * @date 2019/7/17 17:55
 */
@Slf4j
public class ErrorCache {

  private static final String ERROR_JSON_PATH = "json/errorCode.json";

  private static HashMap<String, ErrorDto> errorCodeCache;

  static {
    String s = FileUtil.readResourceFile(ERROR_JSON_PATH);
    if (StringUtils.isNoneBlank(s)) {
      errorCodeCache = JSON.parseObject(s, new TypeReference<HashMap<String, ErrorDto>>() {
      });
    }
  }


  /**
   * 获取错误消息
   *
   * @param code
   * @return
   */
  public static String getMsg(String code) {
    return Optional.ofNullable(errorCodeCache)
            .map(cache -> cache.get(code))
            .map(ErrorDto::getMsg).get();
  }


  /**
   * 获取内部错误消息
   *
   * @param code
   * @return
   */
  public static String getInternalMsg(String code) {
    Optional<ErrorDto> errorDto = Optional.ofNullable(errorCodeCache)
            .map(cache -> cache.get(code));
    if (errorDto.isPresent()) {
      String innerMsg = errorDto.get().getInnerMsg();
      return StringUtils.isNotBlank(innerMsg) ? innerMsg : errorDto.get().getMsg();
    }
    return null;
  }
}


