package com.flkj.utils;

import com.spire.doc.Document;
import com.spire.doc.FileFormat;
import com.spire.doc.documents.ImageType;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ConvertWordToOtherFormats {

    public static void main(String[] args) throws IOException {

        //创建Document对象
        Document doc = new Document();

        //加载Word文档
        doc.loadFromFile("/root/桌面/中海创科技员工手册-V1.21.pdf");

        //将指定页保存为BufferedImage
        BufferedImage image= doc.saveToImages(0, ImageType.Bitmap);

        //将图片数据保存为PNG格式文档
        File file= new File("/root/桌面/中海创科技员工手册-V1.21.png");
        ImageIO.write(image, "PNG", file);

//        //将Word保存为SVG格式
//        doc.saveToFile("D:\\Desktop\\ToSVG.svg",FileFormat.SVG);
//
//        //将Word保存为RTF格式
//        doc.saveToFile("D:\\Desktop\\ToRTF.rtf",FileFormat.Rtf);
//
//        //将Word保存为XPS格式
//        doc.saveToFile("D:\\Desktop\\ToXPS.xps",FileFormat.XPS);
//
//        //将Word保存为XML格式
//        doc.saveToFile("D:\\Desktop\\ToXML.xml",FileFormat.Xml);
//
//        //将Word保存为TXT格式
//        doc.saveToFile("D:\\Desktop\\ToTXT.txt",FileFormat.Txt);
    }
}