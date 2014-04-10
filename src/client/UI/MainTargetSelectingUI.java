package client.UI;

import java.awt.FlowLayout;
import java.awt.event.*;

import javax.swing.*;

import beans.UserBean;
import client.net.down.ReceiveMail;
import client.net.up.Transmitter;




public class MainTargetSelectingUI {

	public MainTargetSelectingUI(UserBean li, Transmitter tra) {
		super();
		user = li;
		transmitter = tra;
	}

	public static enum Selection {SEND,RECEIVE}
	private JFrame frame = new JFrame("主任务选择");
	private JPanel panel = new JPanel();
	private JLabel label = new JLabel("你想收邮件还是发邮件?");
	private JComboBox<Selection> box = new JComboBox<Selection>();
	private JButton confirmBotton = new JButton("OK");
	private FlowLayout layout = new FlowLayout();
	private MainTargetSelectingUIMonitor mainTargetSelectingUIMonitor = new MainTargetSelectingUIMonitor();
	private UserBean user;
	private Transmitter transmitter;
	
	public void initUI() {
		setAttributes();
		addComponents();
	}
	
	public void setAttributes() {
		frame.setLocation(500, 200);
		frame.setSize(250, 150);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		panel.setLayout(layout);
		box.addItem(Selection.SEND);
		box.addItem(Selection.RECEIVE);
		frame.setVisible(true);
	}
	
	public void addComponents() {
		confirmBotton.addActionListener(mainTargetSelectingUIMonitor);
		panel.add(label);
		panel.add(box);
		panel.add(confirmBotton);
		frame.add(panel);
	}
	public static void main(String[] args) {
		UserBean testLi = new UserBean();
		new MainTargetSelectingUI(testLi, null).initUI();
	}
	
	private class MainTargetSelectingUIMonitor implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			Selection selection = (Selection) box.getSelectedItem();
			switch (selection) {
				case SEND:
					onSendOptionSelected();
					break;
				case RECEIVE:
					onReceiveOptionSelected();
					break;
				default:
					System.exit(-1);
			}
		}
		
		private void onSendOptionSelected() {
			frame.dispose();
			new EditMailUI(user, transmitter).launch();
		}
		
		private void onReceiveOptionSelected() {
			System.out.println("receive");
			try {
				new ReceiveMail().loginAndReceiveMail(user, transmitter);
			} catch (Exception e) {
				e.printStackTrace();
			}
			JOptionPane.showConfirmDialog(null, (String)"邮件接收完成!", "finish", JOptionPane.CLOSED_OPTION);
			frame.dispose();
		}

	}
}
