package test;

import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;

import util.Util;

public class MyTest2 {

	static class Person {
		private int number = 0;

		public Person(int number) {
			this.number = number;
		}

		public void sayHi() {
			System.out.println("Hi, I am person" + number);
		}

	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		test7();

	}

	private static void test1() {
		JFileChooser fc = new JFileChooser();
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

	private static void test3() {
		List<Person> persons = getPersonList();
		for (int i = 0; i < 10; i++) {
			persons.get(i).sayHi();
		}
	}

	private static List<Person> getPersonList() {
		List<Person> persons = new ArrayList<>();
		persons.add(new Person(1));
		persons.add(new Person(2));
		persons.add(new Person(3));
		return persons;
	}

	private static void test4() {
		String dateFormat = "yyyy-mm-dd hh:mm:ss";// remember, mm represents
													// minute,MM represents
													// month
		SimpleDateFormat format = new SimpleDateFormat(dateFormat);
		String str2 = "2013-04-28 05:40:02";
		Timestamp ts = Timestamp.valueOf(str2);
	}

	private static void test5() {
		List<String> strings = new ArrayList<>();
		strings.add("yu");
		strings.add("bao");
		strings.add("quan");
		for (String temp : strings) {
			Util.println(temp);
		}
	}

	private static void test6() {
		int a = 14 / 10;
		int b = 18 / 10;
		int c = 20 / 10;
		int d = 9 / 10;
		Util.println("14 / 10 = " + a);
		Util.println("18 / 10 = " + b);
		Util.println("20 / 10 = " + c);
		Util.println("9 / 10 = " + d);
		
	}
	
	private static void calculateTotalPages(int records) {
		int totalPages = 0;
		if (records <= 10) {
			totalPages = 1;
		} else {
			totalPages = records / 10;
			if (records % 10 != 0) {
				totalPages ++;
			}
		}
		Util.println("records: " + records + " pages: " + totalPages);
	}
	
	private static void test7() {
		Util.println(20 % 10);
		for (int i = 0; i < 121; i ++) {
			calculateTotalPages(i);
		}
	}
}
