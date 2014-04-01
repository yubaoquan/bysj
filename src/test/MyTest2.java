package test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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
		for (int i = 0; i < 10; i ++) {
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
	
	static class Person {
		private int number = 0;

		public Person(int number) {
			this.number = number;
		}
		
		public void sayHi() {
			System.out.println("Hi, I am person" + number);
		}

	}
}
