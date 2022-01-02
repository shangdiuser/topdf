package com.example.demo.util;

import java.io.File;

public class Main {
	public static void main(String[] args) {
		
		String res = new PdfToWord().pdftoword("C:\\Users\\admin\\Desktop\\其它文件\\zh.pdf");
		System.out.println(res+"++");
	}
	
}
