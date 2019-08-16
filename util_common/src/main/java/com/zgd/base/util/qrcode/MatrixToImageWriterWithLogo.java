package com.zgd.base.util.qrcode;


import com.google.zxing.common.BitMatrix;
import com.zgd.base.util.qrcode.config.LogoConfig;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;

/**
 * @Author: zgd
 * @Date: 18/09/14 17:53
 * @Description: 模仿Google的MatrixToImageWriter自己编写的类
 */
public class MatrixToImageWriterWithLogo {

  /**
   * 用于设置图案的颜色
   */
  private static final int BLACK = 0xFF000000;
  /**
   * 用于背景色
   */
  private static final int WHITE = 0xFFFFFFFF;

  private MatrixToImageWriterWithLogo() {
  }

  public static BufferedImage toBufferedImage(BitMatrix matrix) {
    int width = matrix.getWidth();
    int height = matrix.getHeight();
    BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        image.setRGB(x, y,  (matrix.get(x, y) ? BLACK : WHITE));
//              image.setRGB(x, y,  (matrix.get(x, y) ? Color.YELLOW.getRGB() : Color.CYAN.getRGB()));
      }
    }
    return image;
  }

  public static void writeToPath(BitMatrix matrix, String format, Path path, String logoUri) throws IOException {
    writeToFile(matrix,format, path.toFile(),logoUri);
  }


  public static void writeToFile(BitMatrix matrix, String format, File file, String logoUri) throws IOException {
    BufferedImage image = toBufferedImage(matrix);
    //设置logo图标
    LogoConfig logoConfig = new LogoConfig();
    image = logoConfig.logoMatrix(image,logoUri);

    if (!ImageIO.write(image, format, file)) {
      throw new IOException("Could not write an image of format " + format + " to " + file);
    }else{
      System.out.println("图片生成成功！");
    }
  }

  public static void writeToStream(BitMatrix matrix, String format, OutputStream stream, String logoUri) throws IOException {
    BufferedImage image = toBufferedImage(matrix);
    //设置logo图标
    LogoConfig logoConfig = new LogoConfig();
    image = logoConfig.logoMatrix(image,logoUri);

    if (!ImageIO.write(image, format, stream)) {
      throw new IOException("Could not write an image of format " + format);
    }
  }
}
