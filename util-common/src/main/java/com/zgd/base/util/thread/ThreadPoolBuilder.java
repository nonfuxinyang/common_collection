package com.zgd.base.util.thread;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.testng.annotations.Test;

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
  private int queueSize = Integer.MAX_VALUE;

  /**
   * 空闲时间
   */
  private int keepAliveSeconds = Integer.MAX_VALUE;


  public ThreadPoolExecutor get() {
    return get("demo");
  }

  public ThreadPoolExecutor get(String name) {
    max = Math.max(core, max);
    return new ThreadPoolExecutor(core, max, keepAliveSeconds, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(queueSize),
            new ThreadFactoryBuilder().setNameFormat(name + "-%d").build(),
            new ThreadPoolExecutor.AbortPolicy());

  }

  public ThreadPoolBuilder(int core, int max) {
    this.core = core;
    this.max = max;
  }

  @Test
    public void fun01() throws InterruptedException {
      ThreadPoolBuilder build = new ThreadPoolBuilder(4,4,10,0);
      ThreadPoolExecutor pool = build.get();

      for (int i = 0; i < 5; i++) {
        pool.submit(() -> {
          try {
            Thread.sleep(1000 * 5);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
        });
      }
      pool.shutdown();
      while (!pool.awaitTermination(1000, TimeUnit.MILLISECONDS)){

        int activeCount = pool.getActiveCount();
        System.out.println("activeCount = " + activeCount);
        long taskCount = pool.getTaskCount();
        System.out.println("taskCount = " + taskCount);
        int size = pool.getQueue().size();
        System.out.println("size = " + size);
        int i = pool.getQueue().remainingCapacity();
        System.out.println("i = " + i);
      }

    }



}
