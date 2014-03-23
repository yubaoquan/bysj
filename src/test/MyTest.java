package test;

import javax.mail.internet.MimeBodyPart;

import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.Test;

import client.UI.MainTargetSelectingUI;


//import org.junit.Ignore;

@RunWith(JUnit4.class)
public class MyTest {

	@Test
	public void test1() {
		MimeBodyPart[] extraItems = new MimeBodyPart[5];
		for (int i = 0;i < 5;i++) {
			if (extraItems[i] == null) {
				System.out.println("null");
			} else {
				System.out.println("not null");
			}
		}
	}
}
