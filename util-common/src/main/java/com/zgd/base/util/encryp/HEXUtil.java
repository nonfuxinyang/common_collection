package com.zgd.base.util.encryp;

import org.apache.commons.lang3.StringUtils;

/**
 * HEXUtil
 *
 * @author zgd
 * @date 2019/11/4 17:24
 */
public class HEXUtil {
  /**
   * 16进制中的字符集
   */
  private static final String HEX_CHAR = "0123456789ABCDEF";

  /**
   * 16进制中的字符集对应的字节数组
   */
  private static final byte[] HEX_STRING_BYTE = HEX_CHAR.getBytes();

  /**
   * 10进制字节数组转换为16进制字节数组
   * <p>
   * byte用二进制表示占用8位，16进制的每个字符需要用4位二进制位来表示，则可以把每个byte 转换成两个相应的16进制字符，即把byte的高4位和低4位分别转换成相应的16进制字符，再取对应16进制字符的字节
   *
   * @param b 10进制字节数组
   * @return 16进制字节数组
   */
  public static byte[] byte2hex(byte[] b) {
    int length = b.length;
    byte[] b2 = new byte[length << 1];
    int pos;
    for (int i = 0; i < length; i++) {
      pos = 2 * i;
      b2[pos] = HEX_STRING_BYTE[(b[i] & 0xf0) >> 4];
      b2[pos + 1] = HEX_STRING_BYTE[b[i] & 0x0f];
    }
    return b2;
  }

  /**
   * 16进制字节数组转换为10进制字节数组
   * <p>
   * 两个16进制字节对应一个10进制字节，则将第一个16进制字节对应成16进制字符表中的位置(0~15)并向左移动4位， 再与第二个16进制字节对应成16进制字符表中的位置(0~15)进行或运算，则得到对应的10进制字节
   *
   * @param b 10进制字节数组
   * @return 16进制字节数组
   */
  public static byte[] hex2byte(byte[] b) {
    if (b.length % 2 != 0) {
      throw new IllegalArgumentException("byte array length is not even!");
    }

    int length = b.length >> 1;
    byte[] b2 = new byte[length];
    int pos;
    for (int i = 0; i < length; i++) {
      pos = i << 1;
      b2[i] = (byte) (HEX_CHAR.indexOf(b[pos]) << 4 | HEX_CHAR.indexOf(b[pos + 1]));
    }
    return b2;
  }

  /**
   * 将16进制字节数组转成10进制字符串
   *
   * @param b 16进制字节数组
   * @return 10进制字符串
   */
  public static String hex2Str(byte[] b) {
    return new String(hex2byte(b));
  }

  /**
   * 将10进制字节数组转成16进制字符串
   *
   * @param b 10进制字节数组
   * @return 16进制字符串
   */
  public static String byte2HexStr(byte[] b) {
    StringBuilder sb = new StringBuilder(b.length);
    String sTemp;
    for (int i = 0; i < b.length; i++) {
      sTemp = Integer.toHexString(0xFF & b[i]);
      if (sTemp.length() < 2) {
        sTemp="0"+sTemp;
      }
      sb.append(sTemp.toUpperCase());
    }
    return sb.toString();
  }


  public static byte[] hexString2Bytes(String hex) {

    if (StringUtils.isEmpty(hex)) {
      return null;
    } else if (hex.length() % 2 != 0) {
      return null;
    } else {
      hex = hex.toUpperCase();
      int len = hex.length() / 2;
      byte[] b = new byte[len];
      char[] hc = hex.toCharArray();
      for (int i = 0; i < len; i++) {
        int p = 2 * i;
        b[i] = (byte) (charToByte(hc[p]) << 4 | charToByte(hc[p + 1]));
      }
      return b;
    }
  }

  /**
   * 字符转换为字节
   */
  private static byte charToByte(char c) {
    return (byte) "0123456789ABCDEF".indexOf(c);
  }




  /**
   * 十六进制字符串装十进制
   *
   * @param hex 十六进制字符串
   * @return 十进制数值
   */
  public static int hexStringToTen(String hex) {
    hex = hex.toUpperCase();
    int max = hex.length();
    int result = 0;
    for (int i = max; i > 0; i--) {
      char c = hex.charAt(i - 1);
      int algorism;
      if (c >= '0' && c <= '9') {
        algorism = c - '0';
      } else {
        algorism = c - 55;
      }
      result += Math.pow(16, max - i) * algorism;
    }
    return result;
  }

}
