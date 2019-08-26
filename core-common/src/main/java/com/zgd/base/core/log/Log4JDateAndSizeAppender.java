package com.zgd.base.core.log;

/**
 * Log4JDateAndSizeSplit
 * Log4JDateAndSizeSplit :log4j日志支持按照日期和大小进行切割
 *
 * @author zgd
 * @date 2019/8/23 16:14
 */

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.helpers.CountingQuietWriter;
import org.apache.log4j.helpers.LogLog;
import org.apache.log4j.helpers.OptionConverter;
import org.apache.log4j.spi.LoggingEvent;

/**
 * ClassName: Log4JDateAndSizeSplit <br/>
 * Function: Log4j生成日志类重写，该类可以按照日期+指定大小分隔日志. <br/>
 * date: 2016年3月31日 下午12:44:14 <br/>
 *
 * @author lujie
 */
public class Log4JDateAndSizeAppender extends FileAppender {

  /**
   * 可以根据分、时、半日、日、周、月的周期进行切分日志
   */
  public static final int TOP_OF_TROUBLE = -1;

  public static final int TOP_OF_MINUTE = 0;

  public static final int TOP_OF_HOUR = 1;

  public static final int HALF_DAY = 2;

  public static final int TOP_OF_DAY = 3;

  public static final int TOP_OF_WEEK = 4;

  public static final int TOP_OF_MONTH = 5;

  /**
   * The default maximum file size is 10MB.
   */
  protected long maxFileSize = 10 * 1024 * 1024;

  /**
   * There is one backup file by default.
   */
  protected int maxBackupIndex = 1;

  /**
   * 最多保留日志文件数
   */
  private int maxTotalFile = -1;

  /**
   * meaning daily rollover.
   */
  private String datePattern = "'.'yyyy-MM-dd";

  /**
   * "scheduledFilename" at the beginning of the next hour.
   */
  private String scheduledFilename;

  /**
   * The next time we estimate a rollover should occur.
   */
  private long nextCheck = System.currentTimeMillis() - 1;

  private Date now = new Date();

  private SimpleDateFormat sdf;

  private RollingCalendar rc = new RollingCalendar();

  int checkPeriod = TOP_OF_TROUBLE;

  // The gmtTimeZone is used only in computeCheckPeriod() method.
  private static final TimeZone GMT_TIME_ZONE = TimeZone.getTimeZone("GMT");

  /**
   * The default constructor does nothing.
   */
  public Log4JDateAndSizeAppender() {
  }

  /**
   * ouput destination for this appender.
   */
  public Log4JDateAndSizeAppender(Layout layout, String filename, String datePattern) throws IOException {
    super(layout, filename, true);
    this.datePattern = datePattern;
    activateOptions();
  }

  /**
   * being rolled over to backup files.
   *
   * @since 1.1
   */
  public long getMaximumFileSize() {
    return maxFileSize;
  }

  /**
   * being rolled over to backup files.
   *
   * <p>
   * JavaBeans {@link java.beans.Introspector Introspector}.
   *
   * @see #setMaxFileSize(String)
   */
  public void setMaximumFileSize(long maxFileSize) {
    this.maxFileSize = maxFileSize;
  }

  /**
   * being rolled over to backup files.
   *
   * <p>
   * the value "10KB" will be interpreted as 10240.
   */
  public void setMaxFileSize(String value) {
    maxFileSize = OptionConverter.toFileSize(value, maxFileSize + 1);
  }

  /**
   * Returns the value of the <b>MaxBackupIndex</b> option.
   */
  public int getMaxBackupIndex() {
    return maxBackupIndex;
  }

  /**
   * Set the maximum number of backup files to keep around.
   *
   * <p>
   */
  public void setMaxBackupIndex(int maxBackups) {
    this.maxBackupIndex = maxBackups;
  }

  /**
   * 按日期划分,日期格式
   */
  public void setDatePattern(String pattern) {
    datePattern = pattern;
  }


  /**
   * Returns the value of the <b>DatePattern</b> option.
   */
  public String getDatePattern() {
    return datePattern;
  }

  public int getMaxTotalFile() {
    return maxTotalFile;
  }

  public void setMaxTotalFile(int maxTotalFile) {
    this.maxTotalFile = maxTotalFile;
  }

  /**
   * 小数点+索引的后缀匹配
   */
  private static final Pattern suffixCompile = Pattern.compile("^.*\\.\\d$");

  @Override
  public void activateOptions() {
    super.activateOptions();
    if (datePattern != null && fileName != null) {
      now.setTime(System.currentTimeMillis());
      sdf = new SimpleDateFormat(datePattern);
      int type = computeCheckPeriod();
      printPeriodicity(type);
      rc.setType(type);
      File file = new File(fileName);
      scheduledFilename = fileName + sdf.format(new Date(file.lastModified()));

    } else {
      LogLog.error("Either File or DatePattern options are not set for appender [" + name + "].");
    }
  }

  private void printPeriodicity(int type) {
    switch (type) {
      case TOP_OF_MINUTE:
        LogLog.debug("Appender [" + name + "] to be rolled every minute.");
        break;
      case TOP_OF_HOUR:
        LogLog.debug("Appender [" + name + "] to be rolled on top of every hour.");
        break;
      case HALF_DAY:
        LogLog.debug("Appender [" + name + "] to be rolled at midday and midnight.");
        break;
      case TOP_OF_DAY:
        LogLog.debug("Appender [" + name + "] to be rolled at midnight.");
        break;
      case TOP_OF_WEEK:
        LogLog.debug("Appender [" + name + "] to be rolled at start of week.");
        break;
      case TOP_OF_MONTH:
        LogLog.debug("Appender [" + name + "] to be rolled at start of every month.");
        break;
      default:
        LogLog.warn("Unknown periodicity for appender [" + name + "].");
    }
  }

  // This method computes the roll over period by looping over the
  // periods, starting with the shortest, and stopping when the r0 is
  // different from from r1, where r0 is the epoch formatted according
  // the datePattern (supplied by the user) and r1 is the
  // epoch+nextMillis(i) formatted according to datePattern. All date
  // formatting is done in GMT and not local format because the test
  // logic is based on comparisons relative to 1970-01-01 00:00:00
  // GMT (the epoch).

  private int computeCheckPeriod() {
    RollingCalendar rollingCalendar = new RollingCalendar(GMT_TIME_ZONE, Locale.ENGLISH);
    // set sate to 1970-01-01 00:00:00 GMT
    Date epoch = new Date(0);
    if (datePattern != null) {
      for (int i = TOP_OF_MINUTE; i <= TOP_OF_MONTH; i++) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(datePattern);
        // do all date
        simpleDateFormat.setTimeZone(GMT_TIME_ZONE);
        // formatting in GMT
        String r0 = simpleDateFormat.format(epoch);
        rollingCalendar.setType(i);
        Date next = new Date(rollingCalendar.getNextCheckMillis(epoch));
        String r1 = simpleDateFormat.format(next);
        if (!r0.equals(r1)) {
          return i;
        }
      }
    }
    // Deliberately head for trouble...
    return TOP_OF_TROUBLE;
  }

  /**
   * Implements the usual roll over behaviour.
   *
   * <p>
   * If <code>MaxBackupIndex</code> is positive, then files {
   *
   * <p>
   * If <code>MaxBackupIndex</code> is equal to zero, then the
   * <code>File</code> is truncated with no backup files created.
   */
  private// synchronization not necessary since doAppend is alreasy synched
  void sizeRollOver() {
    File target;
    File file;

    LogLog.debug("rolling over count=" + ((CountingQuietWriter) qw).getCount());
    LogLog.debug("maxBackupIndex=" + maxBackupIndex);

    String datedFilename = fileName + sdf.format(now);

    if (maxBackupIndex > 0) {
      // Delete the oldest file, to keep Windows happy.
      file = new File(datedFilename + '.' + maxBackupIndex);
      if (file.exists()) {
        file.delete();
      }

      // Map {(maxBackupIndex - 1), ..., 2, 1} to {maxBackupIndex, ..., 3,
      // 2}
      for (int i = maxBackupIndex - 1; i >= 1; i--) {
        file = new File(datedFilename + "." + i);
        if (file.exists()) {
          target = new File(datedFilename + '.' + (i + 1));
          LogLog.debug("Renaming file " + file + " to " + target);
          file.renameTo(target);
        }
      }

      // Rename fileName to datedFilename.1
      target = new File(datedFilename + "." + 1);

      this.closeFile(); // keep windows happy.

      file = new File(fileName);
      LogLog.debug("Renaming file " + file + " to " + target);
      file.renameTo(target);
    } else if (maxBackupIndex < 0) {
      // infinite number of files
      // find the max backup index
      for (int i = 1; i < Integer.MAX_VALUE; i++) {
        target = new File(datedFilename + "." + i);
        if (!target.exists()) { // Rename fileName to datedFilename.i
          this.closeFile();
          file = new File(fileName);
          file.renameTo(target);
          LogLog.debug("Renaming file " + file + " to " + target);
          break;
        }
      }
    }
    LogLog.debug("start delete old logs...");
    deleteObsoleteFiles();
    try {
      // This will also close the file. This is OK since multiple
      // close operations are safe.
      this.setFile(fileName, false, bufferedIO, bufferSize);
    } catch (IOException e) {
      LogLog.error("setFile(" + fileName + ", false) call failed.", e);
    }
    scheduledFilename = datedFilename;
  }

  @Override
  public synchronized void setFile(String fileName, boolean append, boolean bufferedIO, int bufferSize) throws IOException {
    super.setFile(fileName, append, this.bufferedIO, this.bufferSize);
    if (append) {
      File f = new File(fileName);
      ((CountingQuietWriter) qw).setCount(f.length());
    }
  }

  @Override
  protected void setQWForFiles(Writer writer) {
    this.qw = new CountingQuietWriter(writer, errorHandler);
  }

  /**
   * Rollover the current file to a new file.
   */
  void timeRollOver() throws IOException {

    /* Compute filename, but only if datePattern is specified */
    if (datePattern == null) {
      errorHandler.error("Missing DatePattern option in rollOver().");
      return;
    }

    String datedFilename = fileName + sdf.format(now);
    // It is too early to roll over because we are still within the
    // bounds of the current interval. Rollover will occur once the
    // next interval is reached.
    if (scheduledFilename.equals(datedFilename)) {
      return;
    }

    // close current file, and rename it to datedFilename
    this.closeFile();

    File target = new File(scheduledFilename);
    if (target.exists()) {
      target.delete();
    }

    File file = new File(fileName);
    boolean result = file.renameTo(target);
    if (result) {
      LogLog.debug(fileName + " -> " + scheduledFilename);
    } else {
      LogLog.error("Failed to rename [" + fileName + "] to [" + scheduledFilename + "].");
    }

    try {
      // This will also close the file. This is OK since multiple
      // close operations are safe.
      super.setFile(fileName, false, this.bufferedIO, this.bufferSize);
    } catch (IOException e) {
      errorHandler.error("setFile(" + fileName + ", false) call failed.");
    }
    scheduledFilename = datedFilename;
  }

  /**
   * <p>
   * rollover.
   */
  @Override
  protected void subAppend(LoggingEvent event) {
    long n = System.currentTimeMillis();

    if (n >= nextCheck) {
      now.setTime(n);
      nextCheck = rc.getNextCheckMillis(now);
      try {
        timeRollOver();
      } catch (IOException ioe) {
        LogLog.error("rollOver() failed.", ioe);
      }
    } else if ((fileName != null) && ((CountingQuietWriter) qw).getCount() >= maxFileSize) {
      sizeRollOver();
    }

    super.subAppend(event);

  }

  /**
   * 删除超出指定数量的日志
   */
  private void deleteObsoleteFiles() {
    if (maxTotalFile > 0) {
      File file = new File(fileName);
      File dir = file.getParentFile();

      File[] files = dir.listFiles((dir1, name) -> {
        String realFileName = file.getName();
        if (!name.startsWith(realFileName)) {
          return false;
        }
        if (suffixCompile.matcher(name).matches()) {
          name = name.substring(0, name.lastIndexOf("."));
        }
        String f = name.replaceFirst(realFileName, "");
        try {
          sdf.parse(f);
          return true;
        } catch (ParseException ignored) {
        }
        return false;
      });
      if (files == null || files.length == 0) {
        return;
      }
      //判断是否超出最大文件数
      if (files.length > maxTotalFile) {
        List<File> tlt = Arrays.asList(files);
        ArrayList<File> list = new ArrayList<>(tlt);

        list.sort((o1, o2) -> (int) (o1.lastModified() - o2.lastModified()));
        Iterator<File> iterator = list.iterator();
        int i = 0;
        int size = list.size();
//        synchronized (this) {
          while (iterator.hasNext()) {
            File next = iterator.next();
            if (size - maxTotalFile > i) {
              LogLog.debug("delete old file :" + files[i].getName());
              next.delete();
              iterator.remove();
              i++;
            } else {
              return;
            }
//          }
        }
      }
    }
  }
}