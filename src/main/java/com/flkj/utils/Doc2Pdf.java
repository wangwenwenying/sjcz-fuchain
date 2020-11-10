package com.flkj.utils;

import com.aspose.words.License;
import com.aspose.words.SaveFormat;

import java.io.*;

import com.aspose.words.*;

public class Doc2Pdf {

    /*****
     * 需要引入jar包：aspose-words-15.8.0-jdk16.jar
     * @param args
     */
    public static void main(String[] args) throws FileNotFoundException {
        File file = new File("/root/桌面/使用AIDA64查看电脑信息.docx");
        InputStream input = new FileInputStream(file);
        doc2pdf(input,"/root/桌面/pdf.jpeg");
    }

    public static boolean getLicense() {
        boolean result = false;
        try {
            File file = new File("/usr/local/idea/ideaworkpl/sjcz-fuchain/src/main/resources/license.xml"); // 新建一个空白pdf文档
            InputStream is = new FileInputStream(file); // license.xml找个路径放即可。
            License aposeLic = new License();
            aposeLic.setLicense(is);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void doc2pdf(InputStream inPath, String outPath) {
        if (!getLicense()) { // 验证License 若不验证则转化出的pdf文档会有水印产生
            return;
        }
        try {
            long old = System.currentTimeMillis();
            File file = new File(outPath); // 新建一个空白pdf文档
            FileOutputStream os = new FileOutputStream(file);
            Document doc = new Document(inPath); // Address是将要被转化的word文档
            doc.save(os, SaveFormat.JPEG);// 全面支持DOC, DOCX, OOXML, RTF HTML, OpenDocument, PDF,
            // EPUB, XPS, SWF 相互转换
            long now = System.currentTimeMillis();
            System.out.println("共耗时：" + ((now - old) / 1000.0) + "秒"); // 转化用时
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}