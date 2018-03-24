
package com.fh.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import jp.sourceforge.qrcode.QRCodeDecoder;
import jp.sourceforge.qrcode.exception.DecodingFailedException;

import com.swetake.util.Qrcode;

public class TwoDimensionCode
{
    public static void encoderQRCode(String content, String imgPath)
    {
        encoderQRCode(content, imgPath, "png", 2);
    }

    public static void encoderQRCode(String content, OutputStream output)
    {
        encoderQRCode(content, output, "png", 2);
    }

    public static void encoderQRCode(String content, String imgPath, String imgType)
    {
        encoderQRCode(content, imgPath, imgType, 2);
    }

    public static void encoderQRCode(String content, OutputStream output, String imgType)
    {
        encoderQRCode(content, output, imgType, 2);
    }

    public static void encoderQRCode(String content, String imgPath, String imgType, int size)
    {
        try {
            BufferedImage bufImg = qRCodeCommon(content, imgType, size);
            File imgFile = new File(imgPath);
            ImageIO.write(bufImg, imgType, imgFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void encoderQRCode(String content, OutputStream output, String imgType, int size)
    {
        try {
            BufferedImage bufImg = qRCodeCommon(content, imgType, size);
            ImageIO.write(bufImg, imgType, output);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static BufferedImage qRCodeCommon(String content, String imgType, int size)
    {
        BufferedImage bufImg = null;
        size = 10;
        try {
            Qrcode qrcodeHandler = new Qrcode();
            qrcodeHandler.setQrcodeErrorCorrect('M');
            qrcodeHandler.setQrcodeEncodeMode('B');
            qrcodeHandler.setQrcodeVersion(size);
            byte[] contentBytes = content.getBytes("utf-8");
            int imgSize = 67 + 12 * (size - 1);
            bufImg = new BufferedImage(imgSize, imgSize, BufferedImage.TYPE_INT_RGB);
            Graphics2D gs = bufImg.createGraphics();
            gs.setBackground(Color.WHITE);
            gs.clearRect(0, 0, imgSize, imgSize);

            gs.setColor(Color.BLACK);
            int pixoff = 2;
            if (contentBytes.length > 0 && contentBytes.length < 800) {

                boolean[][] codeOut = qrcodeHandler.calQrcode(contentBytes);
                for (int i = 0; i < codeOut.length; i++) {
                    for (int j = 0; j < codeOut.length; j++) {
                        if (codeOut[j][i]) {
                            gs.fillRect(j * 3 + pixoff, i * 3 + pixoff, 3, 3);
                        }
                    }
                }
            } else {
                throw new Exception("QRCode content bytes length = " + contentBytes.length + " not in [0, 800].");
            }
            gs.dispose();
            bufImg.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bufImg;
    }

    public static String decoderQRCode(String imgPath)
        throws Exception
    {
        File imageFile = new File(imgPath);
        BufferedImage bufImg = null;
        String content = null;
        try {
            bufImg = ImageIO.read(imageFile);
            QRCodeDecoder decoder = new QRCodeDecoder();
            content = new String(decoder.decode(new TwoDimensionCodeImage(bufImg)), "utf-8");
        } catch (IOException e) {
        } catch (DecodingFailedException dfe) {
             dfe.printStackTrace();
        }
        return content;
    }

    public static String decoderQRCode(InputStream input)
    {
        BufferedImage bufImg = null;
        String content = null;
        try {
            bufImg = ImageIO.read(input);
            QRCodeDecoder decoder = new QRCodeDecoder();
            content = new String(decoder.decode(new TwoDimensionCodeImage(bufImg)), "utf-8");
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        } catch (DecodingFailedException dfe) {
            System.out.println("Error: " + dfe.getMessage());
            dfe.printStackTrace();
        }
        return content;
    }

    public static void main(String[] args)
    {
        String imgPath = "F:/a.png";

        String encoderContent = "http://www.baidu.com";
        TwoDimensionCode handler = new TwoDimensionCode();
        handler.encoderQRCode(encoderContent, imgPath, "png");
        System.out.println("========encoder success");
        System.out.println("解析结果如下：");
        System.out.println("========decoder success!!!");
    }
}
