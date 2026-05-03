package library.management.system;

import java.awt.*;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Admin Dashboard - with quantity/availability display
 */
public class Dashboard extends javax.swing.JFrame {

    private final Connection con;
    private JLabel lblTotalBooks, lblTotalStudents, lblIssuedBooks, lblOverdueBooks;
    private DefaultTableModel overdueModel, issuedModel;

    public Dashboard() {
        con = connect.connection();
        initComponents();
        loadStats();
        loadIssuedBooks();
        loadOverdueBooks();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setUndecorated(true);
        setSize(1300, 820);
        getContentPane().setBackground(new Color(20, 20, 40));
        getContentPane().setLayout(null);

        // ── TOP BAR ──────────────────────────────────────────────
        JPanel topBar = new JPanel(null);
        topBar.setBackground(new Color(80, 0, 100));
        topBar.setBounds(0, 0, 1300, 60);

        JLabel title = new JLabel("  📊 Admin Dashboard — Library Management System");
        title.setFont(new Font("Segoe UI", Font.BOLD, 19));
        title.setForeground(Color.WHITE);
        title.setBounds(10, 12, 650, 36);
        topBar.add(title);

        JButton btnRefresh = makeTopBtn("🔄 Refresh", new Color(0, 150, 100));
        btnRefresh.setBounds(870, 12, 110, 36);
        btnRefresh.addActionListener(e -> { loadStats(); loadIssuedBooks(); loadOverdueBooks(); });
        topBar.add(btnRefresh);

        JButton btnHome = makeTopBtn("🏠 Home", new Color(100, 0, 150));
        btnHome.setBounds(990, 12, 95, 36);
        btnHome.addActionListener(e -> { new Home().setVisible(true); dispose(); });
        topBar.add(btnHome);

        JButton btnSettings = makeTopBtn("⚙ Settings", new Color(60, 60, 80));
        btnSettings.setBounds(1095, 12, 105, 36);
        btnSettings.addActionListener(e -> new Settings().setVisible(true));
        topBar.add(btnSettings);

        JButton btnClose = makeTopBtn("✖ Close", new Color(180, 0, 0));
        btnClose.setBounds(1210, 12, 80, 36);
        btnClose.addActionListener(e -> {
            int r = JOptionPane.showConfirmDialog(this, "Exit application?", "Exit", JOptionPane.YES_NO_OPTION);
            if (r == JOptionPane.YES_OPTION) System.exit(0);
        });
        topBar.add(btnClose);
        add(topBar);

        // ── STAT CARDS ────────────────────────────────────────────
        JLabel hint = new JLabel("(Click any card to see details)");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        hint.setForeground(new Color(180, 180, 180));
        hint.setBounds(40, 68, 250, 18);
        add(hint);

        lblTotalBooks    = addStatCard("📚 Total Books",    "0", new Color(0, 120, 200),   40, 88, () -> showAllBooksDialog());
        lblTotalStudents = addStatCard("🎓 Total Students", "0", new Color(0, 150, 80),   340, 88, () -> showAllStudentsDialog());
        lblIssuedBooks   = addStatCard("📖 Issued Books",   "0", new Color(180, 120, 0),  640, 88, () -> showIssuedBooksDialog());
        lblOverdueBooks  = addStatCard("⚠ Overdue Books",  "0", new Color(180, 0,   0),  940, 88, () -> showOverdueBooksDialog());

        // ── ISSUED BOOKS TABLE ────────────────────────────────────
        JLabel lblIssued = new JLabel("📋  Currently Issued Books");
        lblIssued.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblIssued.setForeground(new Color(128, 0, 128));
        lblIssued.setBounds(40, 230, 300, 26);
        add(lblIssued);

        String[] issuedCols = {"Book ID", "Book Name", "Student ID", "Student Name", "Issue Date", "Due Date"};
        issuedModel = new DefaultTableModel(issuedCols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tableIssued = makeStyledTable(issuedModel);
        JScrollPane spIssued = new JScrollPane(tableIssued);
        styleScrollPane(spIssued);
        spIssued.setBounds(40, 258, 590, 160);
        add(spIssued);

        // ── OVERDUE BOOKS TABLE ───────────────────────────────────
        JLabel lblOvTitle = new JLabel("⚠  Overdue Books");
        lblOvTitle.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblOvTitle.setForeground(new Color(255, 100, 100));
        lblOvTitle.setBounds(660, 230, 300, 26);
        add(lblOvTitle);

        String[] ovCols = {"Book ID", "Book Name", "Student ID", "Student Name", "Due Date", "Days Overdue"};
        overdueModel = new DefaultTableModel(ovCols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tableOverdue = makeStyledTable(overdueModel);
        JScrollPane spOv = new JScrollPane(tableOverdue);
        styleScrollPane(spOv);
        spOv.setBounds(660, 258, 600, 160);
        add(spOv);

        // ── QUICK ACTIONS ─────────────────────────────────────────
        JLabel lblQ = new JLabel("⚡  Quick Actions");
        lblQ.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblQ.setForeground(Color.WHITE);
        lblQ.setBounds(40, 435, 200, 26);
        add(lblQ);

        addQuickBtn("➕ Add Book",        new Color(153, 0, 153),  40, 463, e -> new Addbook().setVisible(true));
        addQuickBtn("📖 Issue Book",       new Color(0, 100, 180), 215, 463, e -> new IssueBook().setVisible(true));
        addQuickBtn("↩ Return Book",       new Color(0, 130, 80),  390, 463, e -> new ReturnBook().setVisible(true));
        addQuickBtn("🎓 Register Student", new Color(140, 80, 0),  565, 463, e -> new StudentRegistration().setVisible(true));
        addQuickBtn("📧 Send Reminders",   new Color(160, 0, 80),  740, 463, e -> {
            int c = JOptionPane.showConfirmDialog(this,
                "Send overdue email reminders to all students with overdue books?",
                "Confirm", JOptionPane.YES_NO_OPTION);
            if (c == JOptionPane.YES_OPTION) EmailReminder.sendOverdueReminders();
        });

        // ── ALL BOOKS TABLE (with quantity) ───────────────────────
        JLabel lblAll = new JLabel("📚  All Books in Library");
        lblAll.setFont(new Font("Segoe UI", Font.BOLD, 15));
        lblAll.setForeground(Color.WHITE);
        lblAll.setBounds(40, 520, 300, 26);
        add(lblAll);

        // ── quantity columns included ─────────────────────────────
        String[] bookCols = {"Book ID", "Book Name", "Publisher", "Price", "Year", "Total Qty", "Available", "Status", "Issued To"};
        DefaultTableModel bookModel = new DefaultTableModel(bookCols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tableAllBooks = makeStyledTable(bookModel);
        tableAllBooks.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, focus, row, col);
                String status = String.valueOf(t.getModel().getValueAt(row, 7));
                if (!sel) {
                    setBackground("ISSUED".equalsIgnoreCase(status) ? new Color(60, 20, 20) : new Color(30, 30, 50));
                    setForeground("ISSUED".equalsIgnoreCase(status) ? new Color(255, 150, 150) : Color.WHITE);
                }
                // Highlight available qty = 0 in red
                if (col == 6 && "0".equals(String.valueOf(v)) && !sel) {
                    setForeground(new Color(255, 80, 80));
                }
                return this;
            }
        });

        try {
            ResultSet rs = con.createStatement().executeQuery(
                "SELECT b.id, b.name, IFNULL(b.publisher,'—'), IFNULL(b.price,'—'), IFNULL(b.year,'—'), " +
                "COALESCE(b.quantity,1), COALESCE(b.available_qty, CASE WHEN b.status='ISSUED' THEN 0 ELSE 1 END), " +
                "b.status, IFNULL(CONCAT(b.studentid,' - ',s.name),'Not Issued') " +
                "FROM book b LEFT JOIN student s ON b.studentid=s.id " +
                "ORDER BY b.status DESC, b.id ASC"
            );
            while (rs.next()) {
                bookModel.addRow(new Object[]{
                    rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
                    rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8), rs.getString(9)
                });
            }
        } catch (SQLException ex) {
            Logger.getLogger(Dashboard.class.getName()).log(Level.SEVERE, null, ex);
        }

        JScrollPane spAll = new JScrollPane(tableAllBooks);
        styleScrollPane(spAll);
        spAll.setBounds(40, 550, 1220, 230);
        add(spAll);
    }

    // ── Load stats ────────────────────────────────────────────────
    private void loadStats() {
        try {
            ResultSet r1 = con.createStatement().executeQuery("SELECT COUNT(*) FROM book");
            if (r1.next()) lblTotalBooks.setText(String.valueOf(r1.getInt(1)));

            ResultSet r2 = con.createStatement().executeQuery("SELECT COUNT(*) FROM student");
            if (r2.next()) lblTotalStudents.setText(String.valueOf(r2.getInt(1)));

            ResultSet r3 = con.createStatement().executeQuery(
                "SELECT COUNT(*) FROM book WHERE status='ISSUED'"
            );
            if (r3.next()) lblIssuedBooks.setText(String.valueOf(r3.getInt(1)));

            ResultSet r4 = con.createStatement().executeQuery(
                "SELECT COUNT(*) FROM book WHERE status='ISSUED' " +
                "AND STR_TO_DATE(duedate,'%d/%m/%Y') < CURDATE()"
            );
            if (r4.next()) {
                int ov = r4.getInt(1);
                lblOverdueBooks.setText(String.valueOf(ov));
                if (ov > 0) lblOverdueBooks.getParent().setBackground(new Color(180, 0, 0));
            }
        } catch (SQLException ex) {
            Logger.getLogger(Dashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loadIssuedBooks() {
        issuedModel.setRowCount(0);
        try {
            ResultSet rs = con.createStatement().executeQuery(
                "SELECT b.id, b.name, b.studentid, IFNULL(s.name,'Unknown'), b.issuedate, b.duedate " +
                "FROM book b LEFT JOIN student s ON b.studentid=s.id WHERE b.status='ISSUED'"
            );
            while (rs.next()) {
                issuedModel.addRow(new Object[]{
                    rs.getString(1), rs.getString(2), rs.getString(3),
                    rs.getString(4), rs.getString(5), rs.getString(6)
                });
            }
        } catch (SQLException ex) {
            Logger.getLogger(Dashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loadOverdueBooks() {
        overdueModel.setRowCount(0);
        try {
            ResultSet rs = con.createStatement().executeQuery(
                "SELECT b.id, b.name, b.studentid, IFNULL(s.name,'Unknown'), b.duedate, " +
                "DATEDIFF(CURDATE(), STR_TO_DATE(b.duedate,'%d/%m/%Y')) " +
                "FROM book b LEFT JOIN student s ON b.studentid=s.id " +
                "WHERE b.status='ISSUED' AND STR_TO_DATE(b.duedate,'%d/%m/%Y') < CURDATE()"
            );
            while (rs.next()) {
                overdueModel.addRow(new Object[]{
                    rs.getString(1), rs.getString(2), rs.getString(3),
                    rs.getString(4), rs.getString(5), rs.getInt(6) + " days"
                });
            }
        } catch (SQLException ex) {
            Logger.getLogger(Dashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // ── Detail popups ─────────────────────────────────────────────
    private void showAllBooksDialog() {
        showDetailTable("📚 All Books",
            new String[]{"Book ID", "Name", "Publisher", "Price", "Year", "Total Qty", "Available", "Status"},
            "SELECT id, name, IFNULL(publisher,'—'), IFNULL(price,'—'), IFNULL(year,'—'), " +
            "COALESCE(quantity,1), COALESCE(available_qty, CASE WHEN status='ISSUED' THEN 0 ELSE 1 END), status FROM book ORDER BY status DESC"
        );
    }

    private void showAllStudentsDialog() {
        showDetailTable("🎓 All Registered Students",
            new String[]{"Student ID", "Name", "Course", "Semester", "Section", "Email"},
            "SELECT id, name, IFNULL(course,'—'), IFNULL(semester,'—'), IFNULL(section,'—'), IFNULL(email,'—') FROM student ORDER BY id"
        );
    }

    private void showIssuedBooksDialog() {
        showDetailTable("📖 All Issued Books",
            new String[]{"Book ID", "Book Name", "Student ID", "Student Name", "Issue Date", "Due Date"},
            "SELECT b.id, b.name, b.studentid, IFNULL(s.name,'?'), b.issuedate, b.duedate " +
            "FROM book b LEFT JOIN student s ON b.studentid=s.id WHERE b.status='ISSUED'"
        );
    }

    private void showOverdueBooksDialog() {
        showDetailTable("⚠ Overdue Books",
            new String[]{"Book ID", "Book Name", "Student ID", "Student Name", "Due Date", "Days Overdue"},
            "SELECT b.id, b.name, b.studentid, IFNULL(s.name,'?'), b.duedate, " +
            "DATEDIFF(CURDATE(), STR_TO_DATE(b.duedate,'%d/%m/%Y')) " +
            "FROM book b LEFT JOIN student s ON b.studentid=s.id " +
            "WHERE b.status='ISSUED' AND STR_TO_DATE(b.duedate,'%d/%m/%Y') < CURDATE()"
        );
    }

    private void showDetailTable(String title, String[] columns, String sql) {
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        try {
            ResultSet rs = con.createStatement().executeQuery(sql);
            while (rs.next()) {
                Object[] row = new Object[columns.length];
                for (int i = 0; i < columns.length; i++) row[i] = rs.getString(i + 1);
                model.addRow(row);
            }
        } catch (SQLException ex) {
            Logger.getLogger(Dashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
        JTable table = makeStyledTable(model);
        table.setPreferredScrollableViewportSize(new Dimension(900, 350));
        JScrollPane sp = new JScrollPane(table);
        styleScrollPane(sp);
        JOptionPane.showMessageDialog(this, sp, title + " (" + model.getRowCount() + " records)",
            JOptionPane.PLAIN_MESSAGE);
    }

    // ── UI helpers ────────────────────────────────────────────────
    private JLabel addStatCard(String title, String value, Color color, int x, int y, Runnable onClick) {
        JPanel card = new JPanel(null);
        card.setBackground(color);
        card.setBounds(x, y, 280, 120);
        card.setBorder(BorderFactory.createLineBorder(color.brighter(), 2));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel lTitle = new JLabel(title);
        lTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lTitle.setForeground(new Color(220, 220, 220));
        lTitle.setBounds(12, 10, 256, 22);
        card.add(lTitle);

        JLabel lValue = new JLabel(value);
        lValue.setFont(new Font("Segoe UI", Font.BOLD, 42));
        lValue.setForeground(Color.WHITE);
        lValue.setBounds(12, 35, 256, 55);
        card.add(lValue);

        JLabel lClick = new JLabel("Click to view details →");
        lClick.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lClick.setForeground(new Color(200, 200, 200));
        lClick.setBounds(12, 97, 256, 16);
        card.add(lClick);

        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) { onClick.run(); }
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) { card.setBackground(color.brighter()); }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e)  { card.setBackground(color); }
        });
        add(card);
        return lValue;
    }

    private JButton makeTopBtn(String text, Color bg) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return b;
    }

    private void addQuickBtn(String text, Color bg, int x, int y, java.awt.event.ActionListener al) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(new Cursor(Cursor.HAND_CURSOR));
        b.setBounds(x, y, 163, 38);
        b.addActionListener(al);
        add(b);
    }

    private JTable makeStyledTable(DefaultTableModel model) {
        JTable t = new JTable(model);

    t.setBackground(new Color(30, 30, 50));
    t.setForeground(Color.WHITE);
    t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    t.setRowHeight(24);
    t.setGridColor(new Color(60, 60, 80));
    t.setSelectionBackground(new Color(100, 0, 130));

    JTableHeader header = t.getTableHeader();
    header.setFont(new Font("Segoe UI", Font.BOLD, 13));
    header.setPreferredSize(new Dimension(100, 30));

    // FORCE HEADER COLORS
    header.setDefaultRenderer(new DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            JLabel lbl = new JLabel(value.toString());
            lbl.setOpaque(true);
            lbl.setBackground(new Color(80, 0, 100)); // Purple
            lbl.setForeground(Color.WHITE);           // White text
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setBorder(BorderFactory.createLineBorder(new Color(120, 0, 150)));

            return lbl;
        }
    });

    return t;
}

    private void styleScrollPane(JScrollPane sp) {
        sp.setBorder(BorderFactory.createLineBorder(new Color(80, 0, 100), 1));
        sp.getViewport().setBackground(new Color(30, 30, 50));
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new Dashboard().setVisible(true));
    }
}
