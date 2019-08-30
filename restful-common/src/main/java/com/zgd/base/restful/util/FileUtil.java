package com.zgd.base.restful.util;


import com.zgd.base.restful.error.ErrorCache;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * FileUtil
 *
 * @author zgd
 * @date 2019/7/17 18:11
 */
@Slf4j
public class FileUtil {
  public static String readResourceFile(String path) {
    StringBuilder sb = new StringBuilder();
    try {
      InputStream is = ErrorCache.class.getClassLoader().getResourceAsStream(path);
      if (is != null) {
        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        String line;
        while ((line = br.readLine()) != null) {
          sb.append(line);
        }
      }

    } catch (Exception e) {
      log.debug("[读取文件失败] ", e);
    }
    return sb.toString();
  }
}
