package test;

import java.io.File;

import javax.swing.JFileChooser;

import util.Util;

public class MyTest2 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		 test2();
		 
	}

	private static void test1() {
		JFileChooser fc=new JFileChooser();
		 fc.setDialogTitle("选择附件");
		 int returnVal = fc.showDialog(null, "选择");
		 System.out.println(returnVal);
		 File file = fc.getSelectedFile();
		 if (file != null) {
			 System.out.println(file.getName());
		 } else {
			 System.out.println("未选择文件");
		 }
	}
	
	private static void test2() {
		Util.println(1 + " string");
	}

}
