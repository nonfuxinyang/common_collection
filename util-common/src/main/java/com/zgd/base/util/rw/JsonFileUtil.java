package com.zgd.base.util.rw;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 解析JSON的工具类
 * @Author: zgd
 * @Date: 2019/2/28 15:35
 * @Description:
 */
@Slf4j
public class JsonFileUtil {

  private JsonFileUtil(){}

  private static Logger logger = LoggerFactory.getLogger(JsonFileUtil.class);

  public static JSONObject readJson(String resourcesName){
    Path f = Paths.get(JsonFileUtil.class.getResource("/").getPath().substring(1),resourcesName);

    try {
      List<String> strs = Files.readAllLines(f, Charset.forName("UTF-8"));
      logger.info("读取到行数:{}",strs.size());
      String str = strs.stream().collect(Collectors.joining());
      return JSON.parseObject(str);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  public static void main(String[] args) {
    JSONObject jsonObject = readJson("a.json");
    System.out.println(jsonObject.getJSONArray("RECORDS").get(0));
  }

}
