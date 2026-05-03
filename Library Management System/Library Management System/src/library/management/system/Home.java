package library.management.system;

import javax.swing.JOptionPane;

/**
 * Home Screen
 * Admin sees: Student Reg, Add Book, Issue Book, Return Book, Dashboard, Settings, Logout
 * Student sees: only Issue Book (view), Return Book, their Dashboard, Logout
 */
public class Home extends javax.swing.JFrame {

    public Home() {
        initComponents();
        applyRoleVisibility();
        setLocationRelativeTo(null);
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        clsoe         = new javax.swing.JButton();
        btnStudentReg = new javax.swing.JButton();
        btnIssueBook  = new javax.swing.JButton();
        btnAddBook    = new javax.swing.JButton();
        btnReturnBook = new javax.swing.JButton();
        btnLogout     = new javax.swing.JButton();
        btnDashboard  = new javax.swing.JButton();
        btnSettings   = new javax.swing.JButton();
        lblWelcome    = new javax.swing.JLabel();
        jLabel2       = new javax.swing.JLabel();
        jLabel1       = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        // Close button
        clsoe.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/close icon.png")));
        clsoe.setBorderPainted(false);
        clsoe.setContentAreaFilled(false);
        clsoe.addActionListener(evt -> {
            int yes = JOptionPane.showConfirmDialog(this, "Exit application?",
                    "Exit", JOptionPane.YES_NO_OPTION);
            if (yes == JOptionPane.YES_OPTION) System.exit(0);
        });
        getContentPane().add(clsoe, new org.netbeans.lib.awtextra.AbsoluteConstraints(1190, 12, 40, 30));

        // Welcome label
        lblWelcome.setFont(new java.awt.Font("Segoe UI", 1, 16));
        lblWelcome.setForeground(new java.awt.Color(255, 255, 255));
        lblWelcome.setText("Welcome, " + SessionManager.getUserName() +
            " (" + SessionManager.getRole() + ")");
        getContentPane().add(lblWelcome, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 30, 500, 30));

        // ── BUTTONS ───────────────────────────────────────────────
        styleNavBtn(btnStudentReg, "Student Registration", new java.awt.Color(153, 0, 153));
        btnStudentReg.addActionListener(evt -> new StudentRegistration().setVisible(true));
        getContentPane().add(btnStudentReg, new org.netbeans.lib.awtextra.AbsoluteConstraints(980, 80, 182, 40));

        styleNavBtn(btnAddBook, "Add Book", new java.awt.Color(153, 0, 153));
        btnAddBook.addActionListener(evt -> new Addbook().setVisible(true));
        getContentPane().add(btnAddBook, new org.netbeans.lib.awtextra.AbsoluteConstraints(980, 150, 182, 40));

        styleNavBtn(btnIssueBook, "Issue Book", new java.awt.Color(153, 0, 153));
        btnIssueBook.addActionListener(evt -> new IssueBook().setVisible(true));
        getContentPane().add(btnIssueBook, new org.netbeans.lib.awtextra.AbsoluteConstraints(980, 220, 182, 40));

        styleNavBtn(btnReturnBook, "Return Book", new java.awt.Color(153, 0, 153));
        btnReturnBook.addActionListener(evt -> new ReturnBook().setVisible(true));
        getContentPane().add(btnReturnBook, new org.netbeans.lib.awtextra.AbsoluteConstraints(980, 290, 182, 40));

        styleNavBtn(btnDashboard, "📊 Dashboard", new java.awt.Color(0, 100, 180));
        btnDashboard.addActionListener(evt -> {
            if (SessionManager.isAdmin()) new Dashboard().setVisible(true);
            else new StudentDashboard().setVisible(true);
        });
        getContentPane().add(btnDashboard, new org.netbeans.lib.awtextra.AbsoluteConstraints(980, 370, 182, 40));

        // Settings — admin only (hidden for students in applyRoleVisibility)
        styleNavBtn(btnSettings, "⚙ Settings", new java.awt.Color(60, 60, 80));
        btnSettings.addActionListener(evt -> new Settings().setVisible(true));
        getContentPane().add(btnSettings, new org.netbeans.lib.awtextra.AbsoluteConstraints(980, 440, 182, 40));

        // Logout
        btnLogout.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/logout.png")));
        btnLogout.setBackground(new java.awt.Color(80, 0, 100));
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnLogout.addActionListener(evt -> {
            int yes = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?",
                    "Logout", JOptionPane.YES_NO_OPTION);
            if (yes == JOptionPane.YES_OPTION) {
                SessionManager.logout();
                new Login().setVisible(true);
                dispose();
            }
        });
        getContentPane().add(btnLogout, new org.netbeans.lib.awtextra.AbsoluteConstraints(1000, 620, 150, 50));

        // Logo
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/institutes_logo_1713941643770_6628ac8bbc14f.jpg")));
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 20, 160, 50));

        // Background
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/homepage.jpg")));
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1380, 810));

        pack();
    }

    /**
     * Show/hide buttons based on who is logged in.
     */
    private void applyRoleVisibility() {
        if (SessionManager.isStudent()) {
            // Students cannot add books, register students, or see settings
            btnStudentReg.setVisible(false);
            btnAddBook.setVisible(false);
            btnSettings.setVisible(false);
            // Students CAN see Issue Book (read-only view) and Return Book
            btnIssueBook.setText("My Issued Books");
        }
    }

    private void styleNavBtn(javax.swing.JButton btn, String text, java.awt.Color bg) {
        btn.setBackground(bg);
        btn.setFont(new java.awt.Font("Segoe UI", 0, 14));
        btn.setForeground(new java.awt.Color(255, 255, 255));
        btn.setText(text);
        btn.setFocusPainted(false);
        btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new Home().setVisible(true));
    }

    // Variables
    private javax.swing.JButton clsoe, btnStudentReg, btnIssueBook, btnAddBook,
                                  btnReturnBook, btnLogout, btnDashboard, btnSettings;
    private javax.swing.JLabel jLabel1, jLabel2, lblWelcome;
}
