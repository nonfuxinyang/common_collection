package com.zgd.base.util.number;

/**
 * BinaryUtil
 *
 * @author zgd
 * @date 2019/9/10 11:43
 */
public class BinaryUtil {

  private BinaryUtil(){}

  /**
   * long类型转二进制
   * @param num
   * @return
   */
  public static String long2Bin(long num){
    return  toBin(num, Long.SIZE);
  }




  /**
   * int类型转二进制
   * @param num
   * @return
   */
  public static String int2Bin(int num){
    return toBin(num, Integer.SIZE);
  }


  private static String toBin(Number num, int size) {
    char[] chs = new char[size];
    for (int i = 0; i < size; i++) {
      chs[size - 1 - i] = (char) ((num.longValue() >> i & 1) + '0');
    }
    return new String(chs);
  }


  /**
   * 二进制转int
   * @param bin
   * @return
   */
  public static int bin2Int(String bin){
    return Integer.parseInt(bin,2);
  }

  /**
   * 二进制转long
   * @param bin
   * @return
   */
  public static long bin2Long(String bin){
    return Long.parseLong(bin,2);
  }

}
