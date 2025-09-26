package view.room;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import model.User;

public class ManageRoomFrm extends JFrame implements ActionListener {
	private JButton btnAdd, btnEdit, btnDel;
	private User user;

	public ManageRoomFrm(User user) {
		super("Đăng ký ca làm tuần tới");
		this.user = user;

		JPanel listPane = new JPanel();
		listPane.setLayout(new BoxLayout(listPane, BoxLayout.PAGE_AXIS));

		JPanel lblPane = new JPanel();
		lblPane.setLayout(new BoxLayout(lblPane, BoxLayout.LINE_AXIS));
		lblPane.add(Box.createRigidArea(new Dimension(450, 0)));
		JLabel lblUser = new JLabel("Loged in as: " + user.getName());
		lblUser.setAlignmentX(Component.RIGHT_ALIGNMENT);
		lblPane.add(lblUser);
		listPane.add(lblPane);
		listPane.add(Box.createRigidArea(new Dimension(0, 20)));

		JLabel lblHome = new JLabel("Đăng ký ca làm tuần tới");
		lblHome.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblHome.setFont(lblHome.getFont().deriveFont(28.0f));
		listPane.add(lblHome);
		listPane.add(Box.createRigidArea(new Dimension(0, 20)));

		{
			/*
			 * btnAdd = new JButton("Add room");
			 * btnAdd.setAlignmentX(Component.CENTER_ALIGNMENT);
			 * btnAdd.addActionListener(this);
			 * listPane.add(btnAdd);
			 * listPane.add(Box.createRigidArea(new Dimension(0, 10)));
			 */}
		btnEdit = new JButton("Tìm nhân viên");
		btnEdit.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnEdit.addActionListener(this);
		listPane.add(btnEdit);
		listPane.add(Box.createRigidArea(new Dimension(0, 10)));

		{
			/*
			 * btnDel = new JButton("Delete room");
			 * btnDel.setAlignmentX(Component.CENTER_ALIGNMENT);
			 * btnDel.addActionListener(this);
			 * listPane.add(btnDel);
			 */}

		this.setSize(600, 300);
		this.setLocation(200, 10);
		this.add(listPane, BorderLayout.CENTER);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if ((e.getSource() instanceof JButton) && (((JButton) e.getSource()).equals(btnEdit))) {
			(new SearchStaffFrm(user)).setVisible(true);
			this.dispose();
		} else {
			JOptionPane.showMessageDialog(this, "This function is under construction!");
		}
	}

}
