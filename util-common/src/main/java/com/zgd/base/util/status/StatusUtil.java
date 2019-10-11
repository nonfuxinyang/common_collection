package com.zgd.base.util.status;

import lombok.Data;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.RuntimeMXBean;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

/** @author zzzgd */
public class StatusUtil {
  public static Logger LOG = LoggerFactory.getLogger(StatusUtil.class);
  private static ClientStatus clientStatus = new ClientStatus();

  /** @return */
  public static ClientStatus getClientStatus() {
    RuntimeMXBean runtimemxbean = ManagementFactory.getRuntimeMXBean();
    MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
    Runtime runtime = Runtime.getRuntime();
    // 空闲内存

    long freeMemory = runtime.freeMemory();
    clientStatus.setFreeMemory(byteToM(freeMemory));
    // 内存总量
    long totalMemory = runtime.totalMemory();
    clientStatus.setTotalMemory(byteToM(totalMemory));
    // 最大允许使用的内存
    long maxMemory = runtime.maxMemory();
    clientStatus.setMaxMemory(byteToM(maxMemory));
    clientStatus.setUsedMemory(byteToM(totalMemory) - byteToM(freeMemory));

    // 操作系统
    clientStatus.setOsName(System.getProperty("os.name"));
    InetAddress localHost;
    try {
      localHost = InetAddress.getLocalHost();
      String hostName = localHost.getHostName();
      clientStatus.setHost(hostName);
      // ip
      clientStatus.setIpAddress(localHost.getHostAddress());
    } catch (UnknownHostException e) {
      e.printStackTrace();
      LOG.error("无法获取当前主机的主机名与Ip地址");
      clientStatus.setHost("未知");
    }

    // 程序启动时间
    long startTime = runtimemxbean.getStartTime();

    clientStatus.setStartTime(new Date(startTime));
    // 类所在路径
    clientStatus.setClassPath(runtimemxbean.getBootClassPath());
    // 程序运行时间
    clientStatus.setRuntime(runtimemxbean.getUptime());
    // 线程总数
    clientStatus.setThreadCount(ManagementFactory.getThreadMXBean().getThreadCount());
    clientStatus.setProjectPath(new File("").getAbsolutePath());
    clientStatus.setPid(getPid());
    return clientStatus;
  }

  /**
   * 把byte转换成M
   *
   * @param bytes
   * @return
   */
  public static long byteToM(long bytes) {
    long M = (bytes / 1024 / 1024);
    return M;
  }

  /**
   * 创建一个客户端ID
   *
   * @param projectName
   * @param ipAddress
   * @return
   */
  public static String makeClientId(String projectName, String ipAddress) {
    String t = projectName + ipAddress + new File("").getAbsolutePath();
    int client_id = t.hashCode();
    client_id = Math.abs(client_id);
    return String.valueOf(client_id);
  }

  /**
   * 获取进程号，适用于windows与linux
   *
   * @return
   */
  public static long getPid() {
    try {
      String name = ManagementFactory.getRuntimeMXBean().getName();
      String pid = name.split("@")[0];
      return Long.parseLong(pid);
    } catch (NumberFormatException e) {
      LOG.warn("无法获取进程Id");
      return 0;
    }
  }

  @Data
  public static class ClientStatus {
    /** 当前进程运行的主机名 */
    private String host;
    /** 当前进程所在的IP地址 */
    private String ipAddress;
    /** 空闲内存 */
    private long freeMemory;
    /** 内存总量 */
    private long totalMemory;
    /** java虚拟机允许开启的最大的内存 */
    private long maxMemory;
    /** 已使用内存 */
    private long usedMemory;
    /** 操作系统名称 */
    private String osName;
    /** 进程号 */
    private long pid;
    /** 程序启动时间 */
    private Date startTime;

    /** 类所在路径 */
    private String classPath;

    private String projectPath;

    /** 程序运行时间，单位毫秒 */
    private long runtime;
    /** 线程总量 */
    private int threadCount;

    @Override
    public String toString() {
      return "ClientStatus{\n"
          + "host='"
          + host
          + '\''
          + ", ipAddress='"
          + ipAddress
          + '\''
          + ",\n usedMemory="
          + usedMemory
          + "M , freeMemory="
          + freeMemory
          + "M , totalMemory="
          + totalMemory
          + "M , maxMemory="
          + maxMemory
          + "M ,\n osName='"
          + osName
          + '\''
          + ", 线程pid="
          + pid
          + ", threadCount="
          + threadCount
          + ",\n 启动时间startTime="
          + DateFormatUtils.ISO_8601_EXTENDED_DATETIME_FORMAT.format(startTime)
          + ", 运行时间runtime="
          + formatDuring(runtime)
          + ",\n classPath='"
          + classPath
          + '\''
          + ", projectPath='"
          + projectPath
          + '\''
          + "\n}";
    }

    /**
     * @param ms 要转换的毫秒数
     * @return 该毫秒数转换为 * days * hours * minutes * seconds 后的格式
     * @author fy.zhang
     */
    public static String formatDuring(long ms) {
      int ss = 1000;
      int mi = ss * 60;
      int hh = mi * 60;
      int dd = hh * 24;

      long day = ms / dd;
      long hour = (ms - day * dd) / hh;
      long minute = (ms - day * dd - hour * hh) / mi;
      long second = (ms - day * dd - hour * hh - minute * mi) / ss;
      //      long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;

      String strDay = day < 10 ? "0" + day : "" + day; // 天
      String strHour = hour < 10 ? "0" + hour : "" + hour; // 小时
      String strMinute = minute < 10 ? "0" + minute : "" + minute; // 分钟
      String strSecond = second < 10 ? "0" + second : "" + second; // 秒
      //      String strMilliSecond = milliSecond < 10 ? "0" + milliSecond : "" + milliSecond;//毫秒
      //      strMilliSecond = milliSecond < 100 ? "0" + strMilliSecond : "" + strMilliSecond;

      return strDay + "天-" + strHour + "小时-" + strMinute + "分钟-" + strSecond + "秒";
    }
  }
}
