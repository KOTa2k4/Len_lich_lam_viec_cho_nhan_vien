package view.room;

import dao.RegisterTableDAO;
import model.User;
import view.user.ManagerHomeFrm;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class EditScheduleFrm extends JFrame {
    private User user;
    private int staffId;
    private RegisterTableDAO registerTableDAO;
    private JTable tblSchedule;
    private String startDate, endDate;
    private DefaultTableModel model;
    private java.util.List<String> dateOrder; // Lưu thứ tự các ngày
    private Calendar currentWeekStart; // quản lý tuần hiện tại

    public EditScheduleFrm(int staffId, String startDate, String endDate, User user) {
        this.user = user;
        this.staffId = staffId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.registerTableDAO = new RegisterTableDAO();
        this.dateOrder = new ArrayList<>();

        // Khởi tạo currentWeekStart từ startDate
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date start = sdf.parse(startDate);
            currentWeekStart = Calendar.getInstance();
            currentWeekStart.setTime(start);
        } catch (Exception e) {
            currentWeekStart = Calendar.getInstance(); // nếu lỗi thì lấy tuần hiện tại
        }

        setTitle("Lịch làm việc tuần của nhân viên");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        initComponents();
        loadSchedule();
    }

    private void initComponents() {
        tblSchedule = new JTable();
        tblSchedule.setRowHeight(30);

        // Cấu hình model với các cột: Ngày, Ca 1, Ca 2
        model = new DefaultTableModel(new Object[] { "Ngày", "Ca 1", "Ca 2" }, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return (columnIndex == 1 || columnIndex == 2) ? Boolean.class : String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 1 || column == 2;
            }
        };
        tblSchedule.setModel(model);

        // Thay checkbox bằng ô màu có thể click
        tblSchedule.getColumnModel().getColumn(1).setCellRenderer(new ColorCellRenderer());
        tblSchedule.getColumnModel().getColumn(1).setCellEditor(new ColorCellEditor());

        tblSchedule.getColumnModel().getColumn(2).setCellRenderer(new ColorCellRenderer());
        tblSchedule.getColumnModel().getColumn(2).setCellEditor(new ColorCellEditor());

        JScrollPane scrollPane = new JScrollPane(tblSchedule);
        add(scrollPane, BorderLayout.CENTER);

        // Panel nút chuyển tuần
        JButton btnPrev = new JButton("<< Tuần trước");
        JButton btnNext = new JButton("Tuần sau >>");

        btnPrev.addActionListener(e -> {
            currentWeekStart.add(Calendar.DATE, -7);
            updateWeekDatesAndReload();
        });

        btnNext.addActionListener(e -> {
            currentWeekStart.add(Calendar.DATE, 7);
            updateWeekDatesAndReload();
        });

        JPanel panelTop = new JPanel();
        panelTop.add(btnPrev);
        panelTop.add(btnNext);
        add(panelTop, BorderLayout.NORTH);

        // Nút cập nhật
        JButton btnUpdate = new JButton("Cập nhật");
        btnUpdate.addActionListener(e -> updateScheduleToDatabase());

        JPanel panelBottom = new JPanel();
        panelBottom.add(btnUpdate);
        add(panelBottom, BorderLayout.SOUTH);
    }

    private void loadSchedule() {
        model.setRowCount(0);
        dateOrder.clear();

        // Cập nhật startDate và endDate dựa vào currentWeekStart
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        startDate = sdf.format(currentWeekStart.getTime());

        Calendar endCal = (Calendar) currentWeekStart.clone();
        endCal.add(Calendar.DATE, 6);
        endDate = sdf.format(endCal.getTime());

        // Lấy dữ liệu đăng ký thực tế từ DB
        Map<String, Map<String, Boolean>> schedule = registerTableDAO.getWeeklySchedule(staffId, startDate, endDate);

        // Lặp 7 ngày từ currentWeekStart
        Calendar cal = (Calendar) currentWeekStart.clone();
        for (int i = 0; i < 7; i++) {
            String dateStr = sdf.format(cal.getTime());
            String dayOfWeekText = convertToDayOfWeek(dateStr);
            dateOrder.add(dateStr);

            // Nếu có trong DB thì dùng, không thì mặc định là false
            Map<String, Boolean> shifts = schedule.getOrDefault(dateStr, new HashMap<>());
            Boolean ca1 = shifts.getOrDefault("Ca 1", false);
            Boolean ca2 = shifts.getOrDefault("Ca 2", false);

            model.addRow(new Object[] { dayOfWeekText, ca1, ca2 });
            cal.add(Calendar.DATE, 1);
        }
    }

    private void updateWeekDatesAndReload() {
        loadSchedule();
    }

    private void updateScheduleToDatabase() {
        boolean allSuccess = true;

        for (int row = 0; row < model.getRowCount(); row++) {
            String date = dateOrder.get(row);
            Boolean ca1 = (Boolean) model.getValueAt(row, 1);
            Boolean ca2 = (Boolean) model.getValueAt(row, 2);

            boolean updated1 = registerTableDAO.updateShiftCheck(staffId, date, "Ca 1", ca1);
            boolean updated2 = registerTableDAO.updateShiftCheck(staffId, date, "Ca 2", ca2);

            if (!updated1 || !updated2) {
                allSuccess = false;
            }
        }

        if (allSuccess) {
            JOptionPane.showMessageDialog(this, "Cập nhật thành công!");
            // Đóng cửa sổ hiện tại
            (new ManagerHomeFrm(user)).setVisible(true);
            this.dispose();
            // Mở lại màn hình ManaHomeFrm

        } else {
            JOptionPane.showMessageDialog(this, "Có lỗi xảy ra khi cập nhật. Vui lòng thử lại.");
        }

    }

    private String convertToDayOfWeek(String dateStr) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = sdf.parse(dateStr);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

            switch (dayOfWeek) {
                case Calendar.MONDAY:
                    return "Thứ 2";
                case Calendar.TUESDAY:
                    return "Thứ 3";
                case Calendar.WEDNESDAY:
                    return "Thứ 4";
                case Calendar.THURSDAY:
                    return "Thứ 5";
                case Calendar.FRIDAY:
                    return "Thứ 6";
                case Calendar.SATURDAY:
                    return "Thứ 7";
                case Calendar.SUNDAY:
                    return "Chủ nhật";
                default:
                    return "Không rõ";
            }
        } catch (Exception e) {
            return "Không rõ";
        }
    }

    // Renderer ô màu theo giá trị boolean
    class ColorCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {

            JPanel panel = new JPanel();
            panel.setOpaque(true);

            if (Boolean.TRUE.equals(value)) {
                panel.setBackground(Color.GREEN);
            } else {
                panel.setBackground(Color.WHITE);
            }

            if (isSelected) {
                panel.setBorder(BorderFactory.createLineBorder(Color.BLUE, 2));
            } else {
                panel.setBorder(null);
            }

            return panel;
        }
    }

    // Editor ô màu, click đổi trạng thái
    class ColorCellEditor extends AbstractCellEditor implements TableCellEditor {
        private JPanel panel;
        private Boolean currentValue;

        public ColorCellEditor() {
            panel = new JPanel();
            panel.setOpaque(true);

            panel.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    // Đổi trạng thái khi click
                    currentValue = !currentValue;
                    updateColor();
                    // Kết thúc editing để lưu giá trị mới
                    stopCellEditing();
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            currentValue = (Boolean) value;
            updateColor();
            return panel;
        }

        private void updateColor() {
            if (Boolean.TRUE.equals(currentValue)) {
                panel.setBackground(Color.GREEN);
            } else {
                panel.setBackground(Color.WHITE);
            }
        }

        @Override
        public Object getCellEditorValue() {
            return currentValue;
        }
    }
}
