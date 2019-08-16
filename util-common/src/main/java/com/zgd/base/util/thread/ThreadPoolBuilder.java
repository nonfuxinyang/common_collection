package com.zgd.base.util.thread;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.Optional;
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

  private Integer core ;

  private int max ;

  private Integer queueSize ;


  public ThreadPoolExecutor get(){
    return get("demo");
  }

  public ThreadPoolExecutor get(String name){
    core = Optional.ofNullable(core).orElse(4);
    max = Math.max(core,max);
    queueSize = Optional.ofNullable(queueSize).orElse(Integer.MAX_VALUE);
    return new ThreadPoolExecutor(core, max, 0, TimeUnit.MILLISECONDS
            , new LinkedBlockingQueue<>(queueSize), new ThreadFactoryBuilder().setNameFormat(name + "-%d").build()
            , new ThreadPoolExecutor.AbortPolicy());

  }
}
