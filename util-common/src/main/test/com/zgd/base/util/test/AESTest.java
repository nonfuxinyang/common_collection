package com.zgd.base.util.test;

import com.zgd.base.util.encryp.AES2Util;
import com.zgd.base.util.encryp.AESUtil;
import org.testng.annotations.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

/**
 * AESTest
 *
 * @author zgd
 * @date 2019/11/6 10:09
 */
public class AESTest {

  private ThreadLocal<Instant> start = new ThreadLocal<>();

  @Test(threadPoolSize = 4,invocationCount = 10)
  public void testEncrypt() throws InterruptedException {
    String s = "加密AES,测试效果";
    String s1 = AESUtil.encrypt2Base64ByNormalKey(s, "12$,`/d4.ps5f+0-");
    System.out.println("s1 = " + s1);
    Thread.sleep(2000);
    String s2 = AESUtil.encrypt2HEXByNormalKey(s, "12$,`/d4.ps5f+0-");
    System.out.println("s2 = " + s2);
  }

  @Test
  public void fun01(){
    byte[] a = {1,34,-6,65,-75,80};
    System.out.println(new String(a));
  }


  @BeforeMethod
  public void start(){
    start.set(Instant.now());
  }

  @AfterMethod
  public void end(){
    System.out.println("耗时: " + ChronoUnit.MILLIS.between(start.get(),Instant.now()) + " ms");
    start.set(Instant.now());
  }
}
