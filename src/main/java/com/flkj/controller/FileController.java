package com.flkj.controller;

import com.flkj.utils.Doc2Pdf;
import com.flkj.utils.PDF2IMAGE;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author ：www
 * @date ：Created in 20-9-17 上午11:01
 * @description：
 * @modified By：
 * @version:
 */
@Api(value = "/swagger", tags = "我的接口模块file")
@RestController
@RequestMapping(value = "file")
public class FileController {
	@ApiOperation("doc->jpeg文件类数据服务")
	@RequestMapping(value = "/uploadFile", method = RequestMethod.POST )
	@ResponseBody
	public String uploadfile(@RequestParam("file") MultipartFile file) throws IOException {
		InputStream inputStream = file.getInputStream();
		Doc2Pdf.doc2pdf(inputStream,"/root/桌面/pdfss.jpeg");
		return "success";
	}

	@ApiOperation("pdf->png文件类数据服务")
	@RequestMapping(value = "/uploadFiles", method = RequestMethod.POST )
	@ResponseBody
	public String uploadfiles(@RequestParam("file") MultipartFile file) throws IOException {

		File f = null;//MultipartFile转File  因为后面获取Excel内容的方法需要File类型文件
		InputStream ins = file.getInputStream();
		f=new File(file.getOriginalFilename());

		PDF2IMAGE.pdf2Image(ins,f,"/root/桌面",100);
		return "success";
	}
}
