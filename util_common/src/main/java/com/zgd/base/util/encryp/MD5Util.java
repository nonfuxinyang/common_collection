package com.zgd.base.util.encryp;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Objects;

/**
 * MD5工具类，加盐
 *
 * @author daniel
 * @email 576699909@qq.com
 * @time 2016-6-11 下午7:57:36
 */
public class MD5Util {

  public static void main(String[] args) {
    System.out.println("md5WithRandomSalt(\"123\") = " + md5WithRandomSalt("123"));
    System.out.println("verify(\"123\",\"b4d74f25a854d95058336b60877485b66797b7a34048ed90\") = " + verify("123", "b4d74f25a854d95058336b60877485b66797b7a34048ed90"));
  }


  private static String byteToHex(byte[] md5Bytes) {
    StringBuilder hexValue = new StringBuilder();
    for (byte md5Byte : md5Bytes) {
      int val = ((int) md5Byte) & 0xff;
      if (val < 16) {
        hexValue.append("0");
      }
      hexValue.append(Integer.toHexString(val));
    }
    return hexValue.toString();
  }


  /**
   * 加随机盐MD5 ,就是先生成随机16位字符串的盐,利用MD5Hex算法加盐生成36位字符串,夹杂16位随机盐得到最终字符串
   *
   * @param password
   * @return
   * @author daniel
   * @time 2016-6-11 下午8:45:04
   */
  public static String md5WithRandomSalt(String password) {
    String salt = RandomStringUtils.randomAlphanumeric(16);
    System.out.println("salt = " + salt);
    String decodedPwd = DigestUtils.md5Hex(password + salt);

    char[] cs = new char[48];
    for (int i = 0; i < 16; i++) {
      cs[i * 3] = Objects.requireNonNull(decodedPwd).charAt(i * 2);
      cs[i * 3 + 1] = salt.charAt(i);
      cs[i * 3 + 2] = decodedPwd.charAt(i * 2 + 1);
    }
    return new String(cs);
  }

  /**
   * 校验加盐后是否和原文一致
   *
   * @param password
   * @param md5Pwd
   * @return
   * @author daniel
   * @time 2016-6-11 下午8:45:39
   */
  public static boolean verify(String password, String md5Pwd) {
    char[] cs1 = new char[32];
    char[] cs2 = new char[16];
    for (int i = 0; i < 16; i++) {
      cs1[i * 2] = md5Pwd.charAt(i * 3);
      cs2[i] = md5Pwd.charAt(i * 3 + 1);
      cs1[i * 2 + 1] = md5Pwd.charAt(i * 3 + 2);
    }
    String salt = new String(cs2);
    return Objects.equals(DigestUtils.md5Hex(password + salt), new String(cs1));
  }


}