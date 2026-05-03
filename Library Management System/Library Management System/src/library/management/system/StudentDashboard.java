package library.management.system;

import java.awt.*;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Student Dashboard
 * Shows: My Issued Books | My Overdue Books | Total Library Books
 * Does NOT show other students data
 */
public class StudentDashboard extends javax.swing.JFrame {

    private final Connection con;
    private final String myStudentId;
    private final String myName;

    private JLabel lblMyIssued, lblMyOverdue, lblTotalBooks;
    private DefaultTableModel myBooksModel, overdueModel;

    public StudentDashboard() {
        con         = connect.connection();
        myStudentId = SessionManager.getStudentId();
        myName      = SessionManager.getUserName();
        initComponents();
        loadMyStats();
        loadMyBooks();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setUndecorated(true);
        setSize(1100, 720);
        getContentPane().setBackground(new Color(20, 20, 40));
        getContentPane().setLayout(null);

        // ── TOP BAR ──────────────────────────────────────────────
        JPanel topBar = new JPanel(null);
        topBar.setBackground(new Color(80, 0, 100));
        topBar.setBounds(0, 0, 1100, 60);

        JLabel title = new JLabel("  📚 Library - Student Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        title.setBounds(10, 12, 500, 36);
        topBar.add(title);

        JLabel lblWelcome = new JLabel("Welcome, " + myName + " | ID: " + myStudentId);
        lblWelcome.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblWelcome.setForeground(new Color(220, 180, 255));
        lblWelcome.setBounds(440, 18, 360, 24);
        topBar.add(lblWelcome);

        JButton btnRefresh = makeTopBtn("🔄 Refresh", new Color(0, 150, 100));
        btnRefresh.setBounds(820, 12, 110, 36);
        btnRefresh.addActionListener(e -> { loadMyStats(); loadMyBooks(); });
        topBar.add(btnRefresh);

        JButton btnHome = makeTopBtn("🏠 Home", new Color(100, 0, 150));
        btnHome.setBounds(940, 12, 90, 36);
        btnHome.addActionListener(e -> { new Home().setVisible(true); dispose(); });
        topBar.add(btnHome);

        JButton btnClose = makeTopBtn("✖ Close", new Color(180, 0, 0));
        btnClose.setBounds(1040, 12, 55, 36);
        btnClose.addActionListener(e -> {
            int r = JOptionPane.showConfirmDialog(this, "Exit application?", "Exit", JOptionPane.YES_NO_OPTION);
            if (r == JOptionPane.YES_OPTION) System.exit(0);
        });
        topBar.add(btnClose);
        add(topBar);

        // ── STAT CARDS ────────────────────────────────────────────
        lblMyIssued = addStatCard("📖 My Issued Books","0",new Color(0, 120, 200),30, 80,() -> showMyIssuedDetails());

        lblMyOverdue = addStatCard("⚠ My Overdue Books","0",new Color(180, 0, 0),370, 80,() -> showMyOverdueDetails());

        lblTotalBooks = addStatCard("📚 Total Library Books","0",new Color(0, 130, 70),710, 80,() -> showAllLibraryBooks());

        // ── MY ISSUED BOOKS TABLE ─────────────────────────────────
        JLabel lblIssued = new JLabel("📋  My Currently Issued Books");
        lblIssued.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblIssued.setForeground(Color.WHITE);
        lblIssued.setBounds(30, 230, 400, 30);
        add(lblIssued);

        String[] cols = {"Book ID", "Book Name", "Publisher", "Issue Date", "Due Date", "Status"};
        myBooksModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tableMyBooks = makeStyledTable(myBooksModel);

        // Color rows: overdue = red, normal = default
        tableMyBooks.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, focus, row, col);
                String status = (String) t.getModel().getValueAt(row, 5);
                if (!sel) {
                    if ("OVERDUE".equals(status)) {
                        setBackground(new Color(80, 10, 10));
                        setForeground(new Color(255, 120, 120));
                    } else {
                        setBackground(new Color(30, 30, 50));
                        setForeground(Color.WHITE);
                    }
                }
                return this;
            }
        });

        JScrollPane sp1 = new JScrollPane(tableMyBooks);
        styleScrollPane(sp1);
        sp1.setBounds(30, 265, 1040, 200);
        add(sp1);

        // ── OVERDUE DETAILS ───────────────────────────────────────
        JLabel lblOv = new JLabel("⚠  Overdue Details & Fine Estimate");
        lblOv.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblOv.setForeground(new Color(255, 100, 100));
        lblOv.setBounds(30, 490, 400, 30);
        add(lblOv);

        String[] ovCols = {"Book ID", "Book Name", "Due Date", "Days Overdue", "Fine (Tk)"};
        overdueModel = new DefaultTableModel(ovCols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tableOverdue = makeStyledTable(overdueModel);
        JScrollPane sp2 = new JScrollPane(tableOverdue);
        styleScrollPane(sp2);
        sp2.setBounds(30, 525, 1040, 130);
        add(sp2);

        // ── QUICK ACTIONS ─────────────────────────────────────────
        JLabel lblQuick = new JLabel("⚡  Quick Actions");
        lblQuick.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblQuick.setForeground(Color.WHITE);
        lblQuick.setBounds(30, 674, 200, 25);
        add(lblQuick);

        addQuickBtn("↩ Return a Book",    new Color(0, 130, 80),  200, 670, e -> new ReturnBook().setVisible(true));
        addQuickBtn("🚪 Logout",          new Color(120, 0, 0),   400, 670, e -> {
            SessionManager.logout();
            new Login().setVisible(true);
            dispose();
        });
    }

    private void loadMyStats() {
        try {
            // My issued books count
            PreparedStatement ps1 = con.prepareStatement(
                "SELECT COUNT(*) FROM book WHERE studentid=? AND status='ISSUED'"
            );
            ps1.setString(1, myStudentId);
            ResultSet r1 = ps1.executeQuery();
            if (r1.next()) lblMyIssued.setText(String.valueOf(r1.getInt(1)));

            // My overdue books count
            PreparedStatement ps2 = con.prepareStatement(
                "SELECT COUNT(*) FROM book WHERE studentid=? AND status='ISSUED' " +
                "AND STR_TO_DATE(duedate,'%d/%m/%Y') < CURDATE()"
            );
            ps2.setString(1, myStudentId);
            ResultSet r2 = ps2.executeQuery();
            if (r2.next()) {
                int ov = r2.getInt(1);
                lblMyOverdue.setText(String.valueOf(ov));
                if (ov > 0) lblMyOverdue.getParent().setBackground(new Color(160, 0, 0));
            }

            // Total library books (all books, not just mine)
            ResultSet r3 = con.createStatement().executeQuery("SELECT COUNT(*) FROM book");
            if (r3.next()) lblTotalBooks.setText(String.valueOf(r3.getInt(1)));

        } catch (SQLException ex) {
            Logger.getLogger(StudentDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void loadMyBooks() {
        myBooksModel.setRowCount(0);
        overdueModel.setRowCount(0);
        try {
            // Load MY issued books
            PreparedStatement ps = con.prepareStatement(
                "SELECT b.id, b.name, IFNULL(b.publisher,'—'), b.issuedate, b.duedate, " +
                "CASE WHEN STR_TO_DATE(b.duedate,'%d/%m/%Y') < CURDATE() THEN 'OVERDUE' ELSE 'ON TIME' END " +
                "FROM book b WHERE b.studentid=? AND b.status='ISSUED'"
            );
            ps.setString(1, myStudentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                myBooksModel.addRow(new Object[]{
                    rs.getString(1), rs.getString(2), rs.getString(3),
                    rs.getString(4), rs.getString(5), rs.getString(6)
                });
            }

            // Load overdue fine details
            double finePerDay = 5.0;
            try {
                ResultSet fs = con.createStatement().executeQuery("SELECT fine_per_day FROM settings WHERE id=1");
                if (fs.next()) finePerDay = fs.getDouble(1);
            } catch (SQLException ignored) {}

            PreparedStatement ps2 = con.prepareStatement(
                "SELECT b.id, b.name, b.duedate, " +
                "DATEDIFF(CURDATE(), STR_TO_DATE(b.duedate,'%d/%m/%Y')) AS days " +
                "FROM book b WHERE b.studentid=? AND b.status='ISSUED' " +
                "AND STR_TO_DATE(b.duedate,'%d/%m/%Y') < CURDATE()"
            );
            ps2.setString(1, myStudentId);
            ResultSet rs2 = ps2.executeQuery();
            final double fpd = finePerDay;
            while (rs2.next()) {
                int days = rs2.getInt(4);
                overdueModel.addRow(new Object[]{
                    rs2.getString(1), rs2.getString(2), rs2.getString(3),
                    days + " days", "Tk " + String.format("%.2f", days * fpd)
                });
            }
        } catch (SQLException ex) {
            Logger.getLogger(StudentDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // ── UI helpers ────────────────────────────────────────────────
    private JLabel addStatCard(String title, String value, Color color, int x, int y, Runnable onClick) {
        
         JPanel card = new JPanel(null);
          card.setBackground(color);
          card.setBounds(x, y, 310, 120);
          card.setBorder(BorderFactory.createLineBorder(color.brighter(), 2));
         card.setCursor(new Cursor(Cursor.HAND_CURSOR));

         JLabel lTitle = new JLabel(title);
        lTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
         lTitle.setForeground(new Color(220, 220, 220));
         lTitle.setBounds(15, 15, 280, 22);
        card.add(lTitle);

        JLabel lValue = new JLabel(value);
        lValue.setFont(new Font("Segoe UI", Font.BOLD, 42));
        lValue.setForeground(Color.WHITE);
        lValue.setBounds(15, 45, 280, 55);
        card.add(lValue);

        JLabel hint = new JLabel("Click to view details →");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        hint.setForeground(new Color(200, 200, 200));
         hint.setBounds(15, 95, 280, 20);
        card.add(hint);

    // CLICK ACTION
    card.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mouseClicked(java.awt.event.MouseEvent e) {
            if (onClick != null) onClick.run();
        }

        @Override
        public void mouseEntered(java.awt.event.MouseEvent e) {
            card.setBackground(color.brighter());
        }

        @Override
        public void mouseExited(java.awt.event.MouseEvent e) {
            card.setBackground(color);
        }
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
        b.setBounds(x, y, 170, 36);
        b.addActionListener(al);
        add(b);
    }

    private JTable makeStyledTable(DefaultTableModel model) {
         JTable t = new JTable(model);

            t.setBackground(new Color(30, 30, 50));
            t.setForeground(Color.WHITE);
             t.setFont(new Font("Segoe UI", Font.PLAIN, 13));
             t.setRowHeight(26);
             t.setGridColor(new Color(60, 60, 80));
             t.setSelectionBackground(new Color(100, 0, 130));

    // ⭐ FORCE HEADER STYLE (IMPORTANT FIX)
          t.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
           @Override
            public Component getTableCellRendererComponent(
                JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {

            JLabel lbl = new JLabel(value == null ? "" : value.toString());
            lbl.setOpaque(true);

            // Header colors
            lbl.setBackground(new Color(80, 0, 100)); // Purple
            lbl.setForeground(Color.WHITE);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lbl.setHorizontalAlignment(SwingConstants.CENTER);

            return lbl;
            }
            
     
        });
         
       t.getTableHeader().setPreferredSize(new Dimension(100, 30));

       return t;
  }
    
    private void styleScrollPane(JScrollPane sp) {
        sp.setBorder(BorderFactory.createLineBorder(new Color(80, 0, 100), 1));
        sp.getViewport().setBackground(new Color(30, 30, 50));
    }

    // ── Card popup: My Issued Books ───────────────────────────────
    private void showMyIssuedDetails() {
        String[] cols = {"Book ID", "Book Name", "Publisher", "Issue Date", "Due Date", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        try {
            PreparedStatement ps = con.prepareStatement(
                "SELECT b.id, b.name, IFNULL(b.publisher,'—'), b.issuedate, b.duedate, " +
                "CASE WHEN STR_TO_DATE(b.duedate,'%d/%m/%Y') < CURDATE() THEN 'OVERDUE' ELSE 'ON TIME' END " +
                "FROM book b WHERE b.studentid=? AND b.status='ISSUED'"
            );
            ps.setString(1, myStudentId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString(1), rs.getString(2), rs.getString(3),
                    rs.getString(4), rs.getString(5), rs.getString(6)
                });
            }
        } catch (SQLException ex) {
            Logger.getLogger(StudentDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                "You currently have no issued books.", "📖 My Issued Books", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        showPopupTable("📖 My Issued Books", model);
    }

    // ── Card popup: My Overdue Books ──────────────────────────────
    private void showMyOverdueDetails() {
        String[] cols = {"Book ID", "Book Name", "Due Date", "Days Overdue", "Fine (Tk)"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        try {
            double finePerDay = 5.0;
            try {
                ResultSet fs = con.createStatement().executeQuery("SELECT fine_per_day FROM settings WHERE id=1");
                if (fs.next()) finePerDay = fs.getDouble(1);
            } catch (SQLException ignored) {}

            PreparedStatement ps = con.prepareStatement(
                "SELECT b.id, b.name, b.duedate, " +
                "DATEDIFF(CURDATE(), STR_TO_DATE(b.duedate,'%d/%m/%Y')) AS days " +
                "FROM book b WHERE b.studentid=? AND b.status='ISSUED' " +
                "AND STR_TO_DATE(b.duedate,'%d/%m/%Y') < CURDATE()"
            );
            ps.setString(1, myStudentId);
            ResultSet rs = ps.executeQuery();
            final double fpd = finePerDay;
            while (rs.next()) {
                int days = rs.getInt(4);
                model.addRow(new Object[]{
                    rs.getString(1), rs.getString(2), rs.getString(3),
                    days + " days", "Tk " + String.format("%.2f", days * fpd)
                });
            }
        } catch (SQLException ex) {
            Logger.getLogger(StudentDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                "✅ You have no overdue books!", "⚠ My Overdue Books", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        showPopupTable("⚠ My Overdue Books", model);
    }

    // ── Card popup: All Library Books ─────────────────────────────
    private void showAllLibraryBooks() {
        String[] cols = {"Book ID", "Book Name", "Publisher", "Price", "Year", "Total Qty", "Available", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        try {
            ResultSet rs = con.createStatement().executeQuery(
                "SELECT id, name, IFNULL(publisher,'—'), IFNULL(price,'—'), IFNULL(year,'—'), " +
                "COALESCE(quantity,1), " +
                "COALESCE(available_qty, CASE WHEN status='ISSUED' THEN 0 ELSE 1 END), status " +
                "FROM book ORDER BY status DESC, id ASC"
            );
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4),
                    rs.getString(5), rs.getString(6), rs.getString(7), rs.getString(8)
                });
            }
        } catch (SQLException ex) {
            Logger.getLogger(StudentDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
        showPopupTable("📚 All Books in Library", model);
    }

    // ── Shared popup table renderer ───────────────────────────────
    private void showPopupTable(String title, DefaultTableModel model) {
        JTable table = makeStyledTable(model);
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean focus, int row, int col) {
                super.getTableCellRendererComponent(t, v, sel, focus, row, col);
                if (!sel) {
                    boolean isOverdue = false;
                    for (int c = 0; c < t.getColumnCount(); c++) {
                        Object val = t.getModel().getValueAt(row, c);
                        if ("OVERDUE".equals(val) || "ISSUED".equals(val)) { isOverdue = true; break; }
                    }
                    setBackground(isOverdue ? new Color(80, 10, 10) : new Color(30, 30, 50));
                    setForeground(isOverdue ? new Color(255, 120, 120) : Color.WHITE);
                }
                return this;
            }
        });
        table.setPreferredScrollableViewportSize(new Dimension(900, 350));
        JScrollPane sp = new JScrollPane(table);
        styleScrollPane(sp);
        JOptionPane.showMessageDialog(this, sp,
            title + " (" + model.getRowCount() + " records)", JOptionPane.PLAIN_MESSAGE);
    }

}
