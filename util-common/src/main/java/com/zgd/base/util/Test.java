package com.zgd.base.util;

import lombok.extern.slf4j.Slf4j;

/**
 * Test
 *
 * @author zgd
 * @date 2019/8/15 18:45
 */
@Slf4j
public class Test {

  @org.junit.Test
  public void fun01(){
    log.trace("............trace........");
    log.debug("............debug........");
    log.info("............info........");
    log.warn("............warn........");
    log.error("............error........");
    String property = System.getProperty("user.dir");
    System.out.println("property = " + property);
  }
}
