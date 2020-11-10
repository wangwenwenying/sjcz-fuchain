package com.flkj.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.itextpdf.text.pdf.PdfReader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

 
public class PDF2IMAGE {
	public static void main(String[] args) {
//		pdf2Image("/root/桌面/中海创科技员工手册-V1.21.pdf", "/root/桌面", 300);
	}
 
	/***
	 * PDF文件转PNG图片，全部页数
	 * 
//	 * @param  PdfFilePath pdf完整路径
//	 * @param imgFilePath 图片存放的文件夹
	 * @param dpi dpi越大转换后越清晰，相对转换速度越慢
	 * @return
	 */
	public static void pdf2Image(InputStream inputStream,File file, String dstImgFolder, int dpi) {
//		File file = new File(PdfFilePath);
		PDDocument pdDocument;
		try {
//			String imgPDFPath = file.getParent();
			String imgPDFPath = "/root/桌面";
			int dot = file.getName().lastIndexOf('.');
			String imagePDFName = file.getName().substring(0, dot); // 获取图片文件名
			String imgFolderPath = null;
			if (dstImgFolder.equals("")) {
				imgFolderPath = imgPDFPath + File.separator + imagePDFName;// 获取图片存放的文件夹路径
			} else {
				imgFolderPath = dstImgFolder + File.separator + imagePDFName;
			}
 
			if (createDirectory(imgFolderPath)) {
 
				pdDocument = PDDocument.load(inputStream);

				System.out.println("123");

				PDFRenderer renderer = new PDFRenderer(pdDocument);
				/* dpi越大转换后越清晰，相对转换速度越慢 */
//				PdfReader reader = new PdfReader(PdfFilePath);
//				int pages = reader.getNumberOfPages();
				StringBuffer imgFilePath = null;
				for (int i = 0; i < 1; i++) {
					String imgFilePathPrefix = imgFolderPath + File.separator + imagePDFName;
					imgFilePath = new StringBuffer();
					imgFilePath.append(imgFilePathPrefix);
					imgFilePath.append("_");
					imgFilePath.append(String.valueOf(i + 1));
					imgFilePath.append(".png");
					File dstFile = new File(imgFilePath.toString());
					BufferedImage image = renderer.renderImageWithDPI(i, dpi);
					ImageIO.write(image, "png", dstFile);
				}
				System.out.println("PDF文档转PNG图片成功！");
 
			} else {
				System.out.println("PDF文档转PNG图片失败：" + "创建" + imgFolderPath + "失败");
			}
 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
 
	private static boolean createDirectory(String folder) {
		File dir = new File(folder);
		if (dir.exists()) {
			return true;
		} else {
			return dir.mkdirs();
		}
	}
 
}