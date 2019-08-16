package com.zgd.base.util.qrcode;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

/**
 * @Author: zgd
 * @Date: 18/09/14 16:39
 * @Description: 用Zxing 生成二维码
 */
public class ZxingQrcodeUtil {

  private final static int WIDTH_300_PX = 300;
  private final static int HEIGHT_300_PX = 300;

  private final static String FILE_FORMAT = "png";

  public static void createQrcode(String content){
    HashMap<EncodeHintType,Object> hits=new HashMap<>(3);
    hits.put(EncodeHintType.CHARACTER_SET, "utf-8");
    //纠错等级，纠错等级越高存储信息越少
    hits.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
    //边距
    hits.put(EncodeHintType.MARGIN, 2);
    try {
      BitMatrix bitMatrix=new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, WIDTH_300_PX, HEIGHT_300_PX,hits);
      //如果做网页版输出可以用输出到流
//      MatrixToImageWriter.writeToStream(matrix, format, stream);

      Path path = Paths.get("D:/zxingQRCode.png");
      MatrixToImageWriter.writeToPath(bitMatrix, FILE_FORMAT, path);

//      String logoPath = "C:/Users/Admin/Desktop/壁纸/1.png";
//      MatrixToImageWriterWithLogo.writeToPath(bitMatrix, format, path,logoPath);
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println("that is all");
  }

  public static void main(String[] args) {
    createQrcode("https://www.baidu.com/");
  }

}
