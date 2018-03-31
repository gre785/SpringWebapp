
package com.fh.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;

import javax.imageio.ImageIO;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

public class Watermark
{
    private static String strFWATERM, strIWATERM;
    static {
        strFWATERM = Tools.readTxtFile(Const.FWATERM);
        strIWATERM = Tools.readTxtFile(Const.IWATERM);
    }

    public static void fushValue()
    {
        strFWATERM = Tools.readTxtFile(Const.FWATERM);
        strIWATERM = Tools.readTxtFile(Const.IWATERM);
    }

    public static void setWatemark(String imagePath)
    {
        if (null != strFWATERM && !"".equals(strFWATERM)) {
            String strFW[] = strFWATERM.split(",fh,");
            if (strFW.length == 5 && "yes".equals(strFW[0])) {
                pressText(strFW[1].toString(), imagePath, "", 1, Color.RED, Integer.parseInt(strFW[2]), Integer.parseInt(strFW[3]),
                    Integer.parseInt(strFW[4]));
            }
        }
        if (null != strIWATERM && !"".equals(strIWATERM)) {
            String strIW[] = strIWATERM.split(",fh,");
            if (strIW.length == 4 && "yes".equals(strIW[0])) {
                pressImage(PathUtil.getClasspath() + Const.FILEPATHIMG + strIW[1], imagePath, Integer.parseInt(strIW[2]), Integer.parseInt(strIW[3]));
            }
        }
    }

    public final static void pressImage(String markImageUrl, String targetImageUrl, int x, int y)
    {
        try {
            Image image = ImageIO.read(new File(targetImageUrl));
            int width = image.getWidth(null);
            int height = image.getHeight(null);
            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics graphics = bufferedImage.createGraphics();
            graphics.drawImage(image, 0, 0, width, height, null);

            Image chartImage = ImageIO.read(new File(markImageUrl));
            graphics.drawImage(chartImage, x, y, chartImage.getWidth(null), chartImage.getHeight(null), null);
            graphics.dispose();
            FileOutputStream out = new FileOutputStream(targetImageUrl);
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
            encoder.encode(bufferedImage);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void pressText(String markImageUrl, String targetImageUrl, String fontName, int fontStyle, Color color, int fontSize, int x, int y)
    {
        try {
            Image src = ImageIO.read(new File(targetImageUrl));
            int width = src.getWidth(null);
            int height = src.getHeight(null);
            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics graphics = bufferedImage.createGraphics();

            graphics.drawImage(src, 0, 0, width, height, null);
            graphics.setColor(color);
            graphics.setFont(new Font(fontName, fontStyle, fontSize));
            graphics.drawString(markImageUrl, x, y);
            graphics.dispose();
            FileOutputStream out = new FileOutputStream(targetImageUrl);
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
            encoder.encode(bufferedImage);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
