package com.zgd.base.util.rw;


import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: zgd
 * @Date: 2019/3/1 15:01
 * @Description:
 */
public class ReadFiles {
  public static void main(String[] args) {
    String s = "a.json";
    readFile(s);

    System.out.println("------------");
    System.out.println("getFileToString(s).size() = " + getFileToString(s).size());

    System.out.println("------------");
    System.out.println("getFileBufferToString(s).size() = " + getFileBufferToString(s).size());

  }


  /**
   * 读取文件
   * @param fileName
   */
  public static void readFile(String fileName){
    try {
      Files.lines(Paths.get(ReadFiles.class.getResource("/"+fileName).getPath().substring(1))).forEach(System.out::println);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * 获取文件为字符串
   * @param fileName
   */
  public static List<String> getFileToString(String fileName){
    try {
      return Files.readAllLines(Paths.get(ReadFiles.class.getResource("/"+fileName).getPath().substring(1)));
    } catch (IOException e) {
      e.printStackTrace();
    }
    return Collections.emptyList();
  }

  /**
   * 使用字符缓冲流 获取文件为字符串
   * @param fileName
   */
  public static List<String> getFileBufferToString(String fileName){
    try {
      BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(ReadFiles.class.getResource("/" + fileName).getPath().substring(1)));
      //方式一,直接使用java8的lambda,一条语句
      return bufferedReader.lines().collect(Collectors.toList());
      /*
      //第二种方式
      List<String> s = Collections.emptyList();
      String line ;
      while ((line = bufferedReader.readLine()) != null){
        s.add(line);
      }
      if (bufferedReader != null){
        bufferedReader.close();
      }
      return s;
      */

    } catch (IOException e) {
      e.printStackTrace();
    }
    return Collections.emptyList();
  }


}
