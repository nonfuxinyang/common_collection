package com.zgd.demo.thread;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author: zgd
 * @Date: 2019/4/30 09:48
 * @Description:
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ThreadPoolBuilder {

  /**
   * 核心线程池大小
   */
  private int core = 4;

  /**
   * 最大线程池大小
   */
  private int max = 10;

  /**
   * 等待队列大小
   */
  private Integer queueSize;

  /**
   * 空闲时间
   */
  private Integer keepAliveSeconds;


  public ThreadPoolExecutor get() {
    return get("demo");
  }

  public ThreadPoolExecutor get(String name) {
    max = Math.max(core, max);
    queueSize = Optional.ofNullable(queueSize).orElse(Integer.MAX_VALUE);
    return new ThreadPoolExecutor(core, max, keepAliveSeconds, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(queueSize),
            new ThreadFactoryBuilder().setNameFormat(name + "-%d").build(),
            new ThreadPoolExecutor.AbortPolicy());

  }
}
