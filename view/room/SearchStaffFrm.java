package view.room;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import dao.StaffDAO;
import model.Staff;
import model.User;

public class SearchStaffFrm extends JFrame implements ActionListener {
	private ArrayList<Staff> listRoom;
	private JTextField txtKey;
	private JButton btnSearch;
	private JTable tblResult;
	private User user;
	private SearchStaffFrm mainFrm;

	public SearchStaffFrm(User user) {
		super("Tìm nhân viên để chỉnh sửa");
		this.user = user;
		mainFrm = this;
		listRoom = new ArrayList<Staff>();

		JPanel pnMain = new JPanel();
		pnMain.setSize(this.getSize().width - 5, this.getSize().height - 20);
		pnMain.setLayout(new BoxLayout(pnMain, BoxLayout.Y_AXIS));
		pnMain.add(Box.createRigidArea(new Dimension(0, 10)));

		JLabel lblHome = new JLabel("Tìm nhân viên để chỉnh sửa");
		lblHome.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblHome.setFont(lblHome.getFont().deriveFont(20.0f));
		pnMain.add(lblHome);
		pnMain.add(Box.createRigidArea(new Dimension(0, 20)));

		JPanel pn1 = new JPanel();
		pn1.setLayout(new BoxLayout(pn1, BoxLayout.X_AXIS));
		pn1.setSize(this.getSize().width - 5, 20);
		pn1.add(new JLabel("Tên nhân viên: "));
		txtKey = new JTextField();
		pn1.add(txtKey);
		btnSearch = new JButton("Tìm");
		btnSearch.addActionListener(this);
		pn1.add(btnSearch);
		pnMain.add(pn1);
		pnMain.add(Box.createRigidArea(new Dimension(0, 10)));

		JPanel pn2 = new JPanel();
		pn2.setLayout(new BoxLayout(pn2, BoxLayout.Y_AXIS));
		tblResult = new JTable();
		JScrollPane scrollPane = new JScrollPane(tblResult);
		tblResult.setFillsViewportHeight(false);
		scrollPane.setPreferredSize(new Dimension(scrollPane.getPreferredSize().width, 250));

		tblResult.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				int row = tblResult.rowAtPoint(e.getPoint());
				if (row >= 0 && row < tblResult.getRowCount()) {
					Staff selectedStaff = listRoom.get(row);
					int staffId = selectedStaff.getId();

					// Lấy startDate và endDate của tuần hiện tại (Thứ 2 đến Chủ nhật)
					String[] weekRange = getCurrentWeekRange();
					String startDate = weekRange[0];
					String endDate = weekRange[1];

					new EditScheduleFrm(staffId, startDate, endDate, user).setVisible(true);
					mainFrm.dispose();
				}
			}
		});

		pn2.add(scrollPane);
		pnMain.add(pn2);
		this.add(pnMain);
		this.setSize(600, 300);
		this.setLocation(200, 10);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		JButton btnClicked = (JButton) e.getSource();
		if (btnClicked.equals(btnSearch)) {
			if ((txtKey.getText() == null) || (txtKey.getText().length() == 0))
				return;
			StaffDAO rd = new StaffDAO();
			listRoom = rd.searchRoom(txtKey.getText().trim());

			String[] columnNames = { "Id", "Tên", "email", "số điện thoại" };
			String[][] value = new String[listRoom.size()][4];
			for (int i = 0; i < listRoom.size(); i++) {
				value[i][0] = listRoom.get(i).getId() + "";
				value[i][1] = listRoom.get(i).getName();
				value[i][2] = listRoom.get(i).getType();
				value[i][3] = listRoom.get(i).getDes();
			}
			DefaultTableModel tableModel = new DefaultTableModel(value, columnNames) {
				@Override
				public boolean isCellEditable(int row, int column) {
					return false; // không cho sửa trực tiếp trong bảng
				}
			};
			tblResult.setModel(tableModel);
		}
	}

	// Hàm lấy tuần hiện tại (Thứ 2 - Chủ nhật) dạng String yyyy-MM-dd
	private String[] getCurrentWeekRange() {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String startDate = sdf.format(cal.getTime());

		cal.add(Calendar.DATE, 6);
		String endDate = sdf.format(cal.getTime());

		return new String[] { startDate, endDate };
	}
}
