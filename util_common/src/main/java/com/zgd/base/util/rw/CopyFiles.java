package com.zgd.base.util.rw;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * @Author: zgd
 * @Date: 2019/3/1 15:01
 * @Description:
 */
public class CopyFiles {
  public static void main(String[] args) {
    String ref = "D:/down/file/tomcat.zip";
    String dest = "D:/down/file/tomcat2.zip";

    Instant s = Instant.now();
    copyFileByFiles(ref, dest);
    System.out.println("copyFileByFiles = " + ChronoUnit.MILLIS.between(s, Instant.now()));
    s = Instant.now();

    copyFileChannel(ref, dest);
    System.out.println("copyFileChannel = " + ChronoUnit.MILLIS.between(s, Instant.now()));
    s = Instant.now();

    copyFileUsingFileStreams(ref, dest);
    System.out.println("copyFileUsingFileStreams = " + ChronoUnit.MILLIS.between(s, Instant.now()));
    s = Instant.now();
    copyFileByBufferChannel(ref,dest);
    System.out.println("copyFileByBufferChannel = " + ChronoUnit.MILLIS.between(s, Instant.now()));

  }

  /**
   * 使用java7的Files
   *
   * @param refPath
   * @param destPath
   */
  public static void copyFileByFiles(String refPath, String destPath) {
    Path old = Paths.get(refPath);
    Path copy = Paths.get(destPath);

    try {
      //默认不会覆盖同名文件
//      Files.copy(old,copy);
      //传入第三个参数,使其可以覆盖同名文件
      Files.copy(old, copy, StandardCopyOption.REPLACE_EXISTING);
      System.out.println("拷贝完成");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }


  /**
   * Java NIO包括transferFrom方法,根据文档应该比文件流复制的速度更快
   * 45/108/43/44/4/45/46/43
   * @param refPath
   * @param destPath
   */
  public static void copyFileChannel(String refPath, String destPath) {
    FileChannel inputChannel = null;
    FileChannel outputChannel = null;
    try {
      inputChannel = new FileInputStream(refPath).getChannel();
      outputChannel = new FileOutputStream(destPath).getChannel();
//      outputChannel.transferFrom(inputChannel, 0, inputChannel.size());

      long maxSize = 1024 * 512;
      long min = Math.min(inputChannel.size(), maxSize);
      long position = 0;
      while (position < inputChannel.size()){
        position += outputChannel.transferFrom(inputChannel, position, min);
      }
      System.out.println("拷贝完成");
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      try {
        if (inputChannel != null) {
          inputChannel.close();
        }
        if (outputChannel != null) {
          outputChannel.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }


  /**
   * Java Nio 使用通道和缓冲来实现. 可以通过通道异步从缓存中读写
   * 28/36/26
   * @param source
   * @param dest
   */
  public static void copyFileByBufferChannel(String source, String dest){
    FileChannel inputChannel = null;
    FileChannel outputChannel = null;
    try {
      inputChannel = new RandomAccessFile(source, "r").getChannel();
      outputChannel = new RandomAccessFile(dest,"rw").getChannel();
//      ByteBuffer byteBuffer = ByteBuffer.allocate(1024 * 1024 * 10);
      //如果不存在高并非调用这种情况,可以适当将容量设置大些
      ByteBuffer byteBuffer = ByteBuffer.allocate(512 * 1024 );

      while (inputChannel.read(byteBuffer) != -1){
        byteBuffer.flip();
        outputChannel.write(byteBuffer);
        byteBuffer.clear();
      }
      System.out.println("拷贝完成");
    }catch (Exception e){
      e.printStackTrace();
    }finally {
      try {
        if (inputChannel != null) {
          inputChannel.close();
        }
        if (outputChannel != null) {
          outputChannel.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

  }


  /**
   * 原始的输入输出流复制法
   *
   * @param source
   * @param dest
   * @throws IOException
   */
  public static void copyFileUsingFileStreams(String source, String dest) {
    InputStream input = null;
    OutputStream output = null;
    try {
      input = new FileInputStream(source);
      output = new FileOutputStream(dest);
      byte[] buf = new byte[1024];
      int bytesRead;
      while ((bytesRead = input.read(buf)) > 0) {
        output.write(buf, 0, bytesRead);
      }
      System.out.println("拷贝完成");
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (input != null) {
        try {
          input.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      if (output != null) {
        try {
          output.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }


}
