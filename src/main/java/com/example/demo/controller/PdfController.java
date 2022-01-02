package com.example.demo.controller;

import com.example.demo.util.PdfToWord;
import com.spire.pdf.FileFormat;
import com.spire.pdf.PdfDocument;

import cn.hutool.http.HttpUtil;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.spi.FileSystemProvider;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@RestController
// @ConfigurationProperties(prefix = "spring.servlet.multipart")
public class PdfController {
	private static final long serialVersionUID = 1L;
	 
	static String fileLocation = "/restaurantRes/";// 图片资源访问路径

	// 上传的本地存储路径
	@Value("${spring.servlet.multipart.location}")
	private String path;

	// 服务器域名
	@Value("${yuming}")
	private String yuming;

	// 服务器映射路径
	@Value("${WebServer}")
	private String WebServer;
	

	/**
	 * 文件上传
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 */
	@PostMapping("/upload")
	@ResponseBody

	public Map<String, Object> PdfTransform(@RequestParam("file") MultipartFile file) throws IOException {
		Map<String, Object> result = new HashMap<>();

		if (file.isEmpty()) {
			result.put("code", 500);
			result.put("data", null);
			result.put("msg", "该文件是空文件");
			return result;
		}
		// 文件名称
		String filename = file.getOriginalFilename();
		long time = new Date().getTime();

		// 本地文件路径
		String filePath = path + time + "\\";
		File newFile1 = new File(path + time + "\\");
		// 创建路径
		newFile1.mkdir();

		deleteFile(filePath + filename);

		// 在创建的路径下创建文件
		File newFile = new File(filePath + filename);
		// 创建生成的Word路径
		String wordPath = filePath + filename;
		
		//link路径
		String link = yuming + WebServer + time +"/"+ filename;
		String relink = link.replaceAll(".pdf", ".docx");
		try {
			file.transferTo(newFile);

			new PdfToWord().pdftoword(wordPath);
			//System.out.println(yuming + WebServer + time+"/" + filename);
			result.put("code", 200);
			result.put("data", filePath + filename);
			result.put("relink", relink);
			result.put("msg", "转换成功");
			return result;
		} catch (IOException e) {
			e.printStackTrace();
			result.put("code", 500);
			result.put("data", null);
			result.put("msg", "IO异常");
			return result;
		}
	}

	/**
	 * 文件下载
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	/*@GetMapping("/download")
	   public ResponseEntity<InputStreamResource> downloadFile2(@RequestParam(value = "FILE_PATH") String FILE_PATH) throws IOException {

		File file = new File(FILE_PATH);
	      InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

	      return ResponseEntity.ok()
	            .header(HttpHeaders.CONTENT_DISPOSITION,
	                  "attachment;filename=" + file.getName())
	            .contentType(MediaType.APPLICATION_PDF).contentLength(file.length())
	            .body(resource);
	   }*/
	
	@RequestMapping(value = "/download", method = RequestMethod.GET)
	public ResponseEntity<Object> downloadFile(@RequestParam(value = "fileName") String fileName) throws FileNotFoundException {
	    //String fileName = "C:/Users/wds/Desktop/test.txt";
	    String fileName1 = "E:\\UploadFile\\1640793482212\\xrsnOAKqy7Bz88a7f740c2426b3ebdf3e58e05f01e97.docx";
	    File file = new File(fileName1);
	    InputStreamResource resource = new InputStreamResource(new FileInputStream((file)));

	     HttpHeaders headers = new HttpHeaders();
	     headers.add("Content-Disposition",String.format("attachment;filename=\"%s\"",file.getName()));
	     headers.add("Cache-Control","no-cache,no-store,must-revalidate");
	     headers.add("Pragma","no-cache");
	     headers.add("Expires","0");
//xrsnOAKqy7Bz88a7f740c2426b3ebdf3e58e05f01e97.docx
	     ResponseEntity<Object> responseEntity = ResponseEntity.ok()
	                                            .headers(headers)
	                                            .contentLength(file.length())
	                                            .contentType(MediaType.parseMediaType("application/text"))
	                                            .body(resource);
	      return responseEntity;
	    }


	


	

	/*public String downloadFile(HttpServletRequest request, HttpServletResponse response)
			throws UnsupportedEncodingException {

		String filePath = request.getParameter("path");
		int index = filePath.lastIndexOf(".pdf");
		String newPath = filePath.substring(0, index) + ".docx";
		PdfDocument pdf = new PdfDocument(filePath);
		pdf.saveToFile(newPath, FileFormat.DOCX);

		File file = new File(newPath);
		
		// 如果文件名存在，则进行下载
		if (file.exists()) {
			// 配置文件下载
			response.setHeader("content-type", "application/octet-stream");
			response.setContentType("application/octet-stream");
			// 下载文件能正常显示中文
			response.setHeader("Content-Disposition",
					"attachment;filename=" + URLEncoder.encode(file.getName(), "UTF-8"));

			// 实现文件下载
			byte[] buffer = new byte[1024];
			FileInputStream fis = null;
			BufferedInputStream bis = null;
			try {
				fis = new FileInputStream(file);
				bis = new BufferedInputStream(fis);
				OutputStream os = response.getOutputStream();
				int i = bis.read(buffer);
				while (i != -1) {
					os.write(buffer, 0, i);
					i = bis.read(buffer);
				}
				System.out.println("Download the song successfully!");
			} catch (Exception e) {
				System.out.println("Download the song failed!");
			} finally {
				if (bis != null) {
					try {
						bis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (fis != null) {
					try {
						fis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return null;
	}*/

	/**
	 * 删除路径下的所有文件
	 * 
	 * @param path
	 *            需要遍历的路径
	 * @return 路径下文件的名称集合
	 */
	public ArrayList<String> getFile(String path) {
		// 获得指定文件对象
		File file = new File(path);
		// 获得该文件夹内的所有文件
		File[] array = file.listFiles();
		ArrayList<String> list = new ArrayList<String>();
		int n = 0;
		for (int i = 0; i < Objects.requireNonNull(array).length; i++) {
			if (array[i].isFile())// 如果是文件
			{
				// 只输出文件名字
				list.add(array[i].getName());
			}
		}
		return list;
	}

	/**
	 * 删除单个文档
	 * 
	 * @param fileName
	 *            文件名
	 * @return Boolean
	 */
	public boolean deleteFile(String fileName) {
		File file = new File(fileName);
		// 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
		if (file.exists() && file.isFile()) {
			if (file.delete()) {
				System.out.println("删除单个文件" + fileName + "成功！");
				return true;
			} else {
				System.out.println("删除单个文件" + fileName + "失败！");
				return false;
			}
		} else {
			System.out.println("删除单个文件失败：" + fileName + "不存在！");
			return false;
		}
	}

}
