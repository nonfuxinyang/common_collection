package com.zgd.base.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;

/**
 * Test
 *
 * @author zgd
 * @date 2019/8/15 18:45
 */
@Slf4j
public class Test {

  @org.junit.Test
  public void fun01() throws InterruptedException {
    log.trace("............trace........");
    log.debug("............debug........");
    long l = System.currentTimeMillis();
    while (true){
      Thread.sleep(RandomUtils.nextInt(100,150));
      log.info("............info........");
      log.warn("............warn........");
      log.error("............error........");
      if (System.currentTimeMillis() - l > 60000){
        System.exit(0);
      }
    }
  }
}
