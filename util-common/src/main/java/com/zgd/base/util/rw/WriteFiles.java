package com.zgd.base.util.rw;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: zgd
 * @Date: 2019/3/1 15:01
 * @Description:
 */
public class WriteFiles {
  public static void main(String[] args) {
    List<String> lines = Arrays.asList("这是第一行", "这是第二行");
    writeFileToTarget("b.txt",lines);
    writeFileToDest("E:/bb.txt",lines);
    writeBufferFileToDest("E:/aa.txt",lines);

  }


  /**
   * 写入文件到/target/classes
   *
   * @param destFile
   */
  public static void writeFileToTarget(String destFile, List<String> lines) {
    try {
      Path path = Paths.get(WriteFiles.class.getResource("/").getPath().substring(1) + destFile);
//      if (!Files.exists(path)){
//        Files.createFile(path);
//      }

      //可以选择传入第三个OpenOptions参数,不传入默认CREATE和TRUNCATE_NEW,即如果文件不存在,创建该文件,如果存在,覆盖
      //      Files.write(path, lines);
      // 如果不传第三个参数,则文件不存在的话默认会选择创建
      Files.write(path, lines);
      //还可以传入第三个参数,选择是否创建文件,是否在同名文件后拼接等
//      Files.write(path,lines, StandardOpenOption.CREATE,StandardOpenOption.APPEND );

      System.out.println("已写入完成.path = " + path);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * 写入文件到指定绝对路径
   *
   * @param destPath
   */
  public static void writeFileToDest(String destPath, List<String> lines) {
    try {
      Path path = Paths.get(destPath);
//      if (!Files.exists(path)){
//        Files.createFile(path);
//      }
      //可以选择传入第三个OpenOptions参数,不传入默认CREATE和TRUNCATE_NEW,即如果文件不存在,创建该文件,如果存在,覆盖
//      Files.write(path, lines, StandardOpenOption.CREATE,StandardOpenOption.TRUNCATE_EXISTING);
      Files.write(path, lines);
      System.out.println("已写入完成.path = " + path);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  /**
   * 字符缓冲流写入文件到指定绝对路径
   *
   * @param destPath
   */
  public static void writeBufferFileToDest(String destPath, List<String> lines) {
    try {
      Path path = Paths.get(destPath);
      if (!Files.exists(path)){
        Files.createFile(path);
      }
      BufferedWriter bufferedWriter = Files.newBufferedWriter(path);
      lines.stream().forEach(s -> {
        try {
          bufferedWriter.write(s);
          bufferedWriter.newLine();
        } catch (IOException e) {
          e.printStackTrace();
        }
      });
      bufferedWriter.close();
      System.out.println("已写入完成.path = " + path);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


}
