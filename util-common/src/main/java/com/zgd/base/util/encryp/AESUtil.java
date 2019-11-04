package com.zgd.base.util.encryp;

/**
 * 使用种子生成随机密钥的方式
 *
 * @Author: zgd
 * @Date: 2019/1/16 17:22
 * @Description:
 */

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Base64Utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * @version V1.0
 */
@Slf4j
public class AESUtil {

  private static final String KEY_ALGORITHM = "AES";
  /**
   * 默认的加密算法
   */
  private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";
  /**
   * 编码格式
   */
  private static final String ENCODING = "UTF-8";
  /**
   * 签名算法
   */
  private static final String SIGN_ALGORITHMS = "SHA1PRNG";

  /**
   * AES的key的位数只能是128, 192 or 256
   */
  private static final int KEY_SIZE_128 = 128;
  private static final int KEY_SIZE_192 = 192;
  private static final int KEY_SIZE_256 = 256;

  /**
   * AES 加密操作
   *
   * @param content 待加密内容
   * @param key   密钥
   * @return 返回Base64转码后的加密数据
   */
  public static byte[] encrypt(String content, Key key) {
    try {
      // 创建密码器
      Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
      byte[] byteContent = content.getBytes(StandardCharsets.UTF_8);
      // 初始化为加密模式的密码器
      cipher.init(Cipher.ENCRYPT_MODE, key);
      // 加密
      return cipher.doFinal(byteContent);
    } catch (Exception ex) {
      log.error("[AESUtil] [encrypt] 异常:", ex);
    }
    return null;
  }

  /**
   * AES 解密操作
   *
   * @param data
   * @param key
   * @return
   */
  public static byte[] decrypt(byte[] data,  Key key) {

    try {
      //实例化
      Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
      //使用密钥初始化，设置为解密模式
      cipher.init(Cipher.DECRYPT_MODE,key);
      //执行操作
      return cipher.doFinal(data);
    } catch (Exception ex) {
      log.error("[AESUtil] [decrypt] 异常:", ex);
    }
    return null;
  }



  /**
   * 加密并将数据转码成base64
   * @param content 文本
   * @param key 密钥
   * @return
   */
  public static String encryptToBase64(String content, Key key) {
    byte[] res = encrypt(content, key);
    if (res != null) {
      //通过Base64转码返回
      return Base64Utils.encodeToString(res);
    }
    return null;
  }

  /**
   * 将base64编码的文本解密
   * @param content 文本
   * @param key 密钥
   * @return
   */
  public static String decryptFromBase64(String content, Key key) {
    byte[] data = Base64Utils.decodeFromString(content);
    byte[] res = decrypt(data, key);
    if (res != null) {
      return new String(res);
    }
    return null;
  }


  /**
   * 将加密后的数据转码成十六进制
   * @param content 文本
   * @param key 密钥
   * @return
   */
  public static String encryptToHEX(String content, Key key) {
    byte[] res = encrypt(content, key);
    if (res != null) {
      //通过Base64转码返回
      return HEXUtil.byte2HexStr(res);
    }
    return null;
  }

  /**
   * 将十六进制的文本解密
   * @param content 文本
   * @param key 密钥
   * @return
   */
  public static String decryptFromHEX(String content, Key key) {
    byte[] data = HEXUtil.hexString2Bytes(content);
    byte[] res = decrypt(data, key);
    if (res != null) {
      return new String(res);
    }
    return null;
  }




  /**
   * 根据种子生成一个安全随机数,作为加密秘钥
   *
   * @return
   */
  private static SecretKeySpec getRandomKeyBySeed(final String seed)  {
    //返回生成指定算法密钥生成器的 KeyGenerator 对象
    KeyGenerator kg;
    try {
      kg = KeyGenerator.getInstance(KEY_ALGORITHM);

      //指定签名算法
      SecureRandom random = SecureRandom.getInstance(SIGN_ALGORITHMS);
      random.setSeed(seed.getBytes(ENCODING));
      //AES 要求密钥长度为 128
      kg.init(KEY_SIZE_128, random);
//      kg.init(128, new SecureRandom(keyWord.getBytes(ENCODING)));

      //生成一个16位的随机密钥
      SecretKey secretKey = kg.generateKey();
      log.debug("[AESUtil] [getSecretKey] 根据种子[{}]生成一个随机数的密钥:{}", seed, secretKey.getEncoded());
      // 转换为AES专用密钥
      return new SecretKeySpec(secretKey.getEncoded(), KEY_ALGORITHM);
    } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
      log.error("[AESUtil] [getSecretKey] 异常:", ex);
    }

    return null;
  }


  /**
   * 由字符串生成加密key
   *
   * @return
   */
  public static SecretKey getNormalKey(String selectKey) {
    // 本地的密码解码
    byte[] bytes = selectKey.getBytes();
    if (bytes.length != 16){
      throw new IllegalArgumentException("密钥长度必须是16位");
    }
    // 根据给定的字节数组使用AES加密算法构造一个密钥
    return new SecretKeySpec(bytes, "AES");
  }


  public static void main(String[] args) {
    String c = "hello world hello world hello world hello world";
    SecretKeySpec randomKeyBySeed = getRandomKeyBySeed("123");
    String s2 = encryptToBase64(c, randomKeyBySeed);
    System.out.println("s2 = " + s2);
    System.out.println("decryptFromBase64(s2,randomKeyBySeed) = " + decryptFromBase64(s2, randomKeyBySeed));
    String s3 = encryptToHEX(c, randomKeyBySeed);
    System.out.println("s3 = " + s3);
    System.out.println("decryptFromHEX(s3,randomKeyBySeed) = " + decryptFromHEX(s3, randomKeyBySeed));

    SecretKey normalKey = getNormalKey("1234567887654321");
    String s4 = encryptToBase64(c, normalKey);
    System.out.println("s4 = " + s4);
    System.out.println("decryptFromBase64(s4,randomKeyBySeed) = " + decryptFromBase64(s4, normalKey));
    String s5 = encryptToHEX(c, normalKey);
    System.out.println("s5 = " + s5);
    System.out.println("decryptFromHEX(s4,randomKeyBySeed) = " + decryptFromHEX(s5, normalKey));


//    Instant now = Instant.now();
//    for (int i = 0; i < 10000; i++) {
//      String s1 = AESUtil.encrypt("hello world hello world hello world hello world", "123");
//      System.out.println("s1:" + s1);
//      System.out.println("s2:" + AESUtil.decrypt("XL9A73DV3Dhb3UzDglFZxg==", "123"));
//    }
//    System.out.println("耗时: " +(ChronoUnit.MILLIS.between(now,Instant.now())));
  }
}