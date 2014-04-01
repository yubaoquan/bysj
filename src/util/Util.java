package util;

public class Util {
	public static void println(Object obj) {
		System.out.println(obj);
		return;
	}
	
	public static void print(Object obj) {
		System.out.print(obj);
	}
	
	public static String getShortStringWithEllipsis(String input, int limit) {
		int ellipsisLength = 3;
		int limitWithoutEllipsisLength = limit - ellipsisLength;
		int preLength = input.length();
		
		if (preLength > limitWithoutEllipsisLength) {
			input = input.substring(0, limitWithoutEllipsisLength) + "...";
		}
		return input;
	}
}
