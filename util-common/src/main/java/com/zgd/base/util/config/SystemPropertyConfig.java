package com.zgd.base.util.config;

import java.io.File;

/**
 * SystemProperty
 *
 * @author zgd
 * @date 2019/8/16 18:40
 */
public class SystemPropertyConfig {

  static {
    String absolutePath1 = new File("").getAbsolutePath();
    System.out.println("absolutePath1 = " + absolutePath1);
    String dirPath = System.getProperty("user.dir");
    String dir = dirPath.substring(dirPath.lastIndexOf("\\") +1);
    System.setProperty("project.dir",dir);
  }

  public static void main(String[] args) {

  }
}
