package library.management.system;

import javax.swing.JOptionPane;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;

/**
 * Login Form - Supports Admin + Student login
 * Admins  → see full Home with Settings + Dashboard
 * Students → see student Home (no Settings)
 */
public class Login extends javax.swing.JFrame {

    int flag = 0;

    public Login() {
        initComponents();
        setLocationRelativeTo(null);
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        jButton1   = new javax.swing.JButton();
        jLabel1    = new javax.swing.JLabel();
        jLabel2    = new javax.swing.JLabel();
        textuserid = new javax.swing.JTextField();
        lvlhide    = new javax.swing.JLabel();
        textpass   = new javax.swing.JPasswordField();
        jLabel3    = new javax.swing.JLabel();
        jButton2   = new javax.swing.JButton();
        jButton3   = new javax.swing.JButton();
        jButton4   = new javax.swing.JButton();
        jLabel4    = new javax.swing.JLabel();
        lblRole    = new javax.swing.JLabel();
        cmbRole    = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        // Close button
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/close icon.png")));
        jButton1.setBorderPainted(false);
        jButton1.setContentAreaFilled(false);
        jButton1.addActionListener(evt -> {
            int yes = JOptionPane.showConfirmDialog(this, "Exit application?", "Exit", JOptionPane.YES_NO_OPTION);
            if (yes == JOptionPane.YES_OPTION) System.exit(0);
        });
        getContentPane().add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(1220, 30, 40, 40));

        // Title
        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 22));
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Library Login");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 265, 220, 35));

        // Login As label + combo
        lblRole.setFont(new java.awt.Font("Segoe UI", 1, 16));
        lblRole.setForeground(new java.awt.Color(255, 255, 255));
        lblRole.setText("Login As:");
        getContentPane().add(lblRole, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 305, 100, 28));

        cmbRole.setModel(new javax.swing.DefaultComboBoxModel<>(new String[]{"Admin", "Student"}));
        cmbRole.setFont(new java.awt.Font("Segoe UI", 0, 14));
        cmbRole.setBackground(new java.awt.Color(70, 0, 90));
        cmbRole.setForeground(new java.awt.Color(255, 255, 255));
        getContentPane().add(cmbRole, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 305, 150, 28));

        // User ID
        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 16));
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("User ID:");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 345, 100, -1));
        getContentPane().add(textuserid, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 343, 160, 28));

        // Password
        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 16));
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Password:");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 385, 100, -1));
        getContentPane().add(textpass, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 383, 160, 28));

        // Show/hide password
        lvlhide.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/hide1.png")));
        lvlhide.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lvlhide.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) { togglePassword(); }
        });
        getContentPane().add(lvlhide, new org.netbeans.lib.awtextra.AbsoluteConstraints(828, 380, 30, 36));

        // Login Now button
        jButton2.setBackground(new java.awt.Color(153, 0, 153));
        jButton2.setFont(new java.awt.Font("Segoe UI", 1, 14));
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("Login Now");
        jButton2.setFocusPainted(false);
        jButton2.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton2.addActionListener(evt -> doLogin());
        getContentPane().add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 435, 130, 40));

        // Signup button
        jButton3.setFont(new java.awt.Font("Segoe UI", 1, 14));
        jButton3.setForeground(new java.awt.Color(153, 0, 153));
        jButton3.setBackground(new java.awt.Color(255, 255, 255));
        jButton3.setText("Signup");
        jButton3.setFocusPainted(false);
        jButton3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton3.addActionListener(evt -> { new Signup().setVisible(true); dispose(); });
        getContentPane().add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(665, 435, 100, 40));

        // Forgot Password
        jButton4.setBackground(new java.awt.Color(100, 0, 100));
        jButton4.setFont(new java.awt.Font("Segoe UI", 1, 13));
        jButton4.setForeground(new java.awt.Color(255, 255, 255));
        jButton4.setText("Forgot Password?");
        jButton4.setFocusPainted(false);
        jButton4.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton4.addActionListener(evt -> forgotPassword());
        getContentPane().add(jButton4, new org.netbeans.lib.awtextra.AbsoluteConstraints(778, 435, 160, 40));

        // Background image (keep at bottom so it stays behind everything)
        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/login page.jpg")));
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(-30, -40, 1530, 850));

        pack();
    }

    private void doLogin() {
        String userId   = textuserid.getText().trim();
        String password = new String(textpass.getPassword());
        String role     = (String) cmbRole.getSelectedItem(); // "Admin" or "Student"

        if (userId.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter User ID and Password.");
            return;
        }

        try {
            try (Connection c = connect.connection()) {
                if (c == null) {
                    JOptionPane.showMessageDialog(this, "Database connection failed!\nCheck MySQL is running and credentials in connect.java");
                    return;
                }
                
                if ("Admin".equals(role)) {
                    // ── ADMIN LOGIN ──────────────────────────────────
                    PreparedStatement pre = c.prepareStatement(
                            "SELECT userid, email FROM login WHERE userid=? AND password=?"
                    );
                    pre.setString(1, userId);
                    pre.setString(2, password);
                    ResultSet res = pre.executeQuery();
                    
                    if (res.next()) {
                        SessionManager.loginAsAdmin(res.getString("userid"), res.getString("userid"), res.getString("email"));
                        dispose();
                        // Show overdue alert for admin
                        EmailReminder.showOverdueAlertOnLogin(null);
                        EmailReminder.showDueTomorrowAndSendReminder(null); // auto reminder 1 day before
                        new Home().setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(this, "❌ Incorrect Admin ID or Password.");
                    }
                    
                } else {
                    // ── STUDENT LOGIN ────────────────────────────────
                    // Students log in with their Student ID + name as password
                    // (or you can use student email as password — flexible)
                    PreparedStatement pre = c.prepareStatement(
                            "SELECT id, name, email FROM student WHERE id=? AND name=?"
                    );
                    pre.setString(1, userId);
                    pre.setString(2, password);
                    ResultSet res = pre.executeQuery();
                    
                    if (res.next()) {
                        SessionManager.loginAsStudent(res.getString("id"), res.getString("name"), res.getString("email"));
                        dispose();
                        new StudentDashboard().setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "<html>❌ Student not found.<br><br>" +
                                        "<b>Student Login Instructions:</b><br>" +
                                        "• User ID = Your Student ID (e.g. STD001)<br>" +
                                        "• Password = Your Full Name (exactly as registered)</html>");
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage() +
                "\n\nMake sure MySQL is running!");
        }
    }

    private void forgotPassword() {
        String[] options = {"Admin Account", "Student Account"};
        int choice = JOptionPane.showOptionDialog(this,
            "Which account do you need help with?", "Forgot Password",
            JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (choice == 0) {
            // Admin - look up by email
            String email = JOptionPane.showInputDialog(this, "Enter your registered admin email:");
            if (email == null || email.trim().isEmpty()) return;
            try {
                try (Connection c = connect.connection()) {
                    PreparedStatement ps = c.prepareStatement("SELECT userid FROM login WHERE email=?");
                    ps.setString(1, email.trim());
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        JOptionPane.showMessageDialog(this,
                                "✅ Account found!\nYour Admin User ID is: " + rs.getString("userid") +
                                        "\n\nContact the system admin to reset your password.",
                                "Account Found", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "❌ No admin account found with that email.");
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            // Student - remind them of login method
            JOptionPane.showMessageDialog(this,
                "Student Login uses:\n• User ID = Your Student ID\n• Password = Your Full Name\n\nContact the library admin if you need help.",
                "Student Login Help", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void togglePassword() {
        if (flag == 0) {
            textpass.setEchoChar((char) 0);
            lvlhide.setIcon(new ImageIcon(getClass().getResource("/images/show-eye.png")));
            flag = 1;
        } else {
            textpass.setEchoChar('*');
            lvlhide.setIcon(new ImageIcon(getClass().getResource("/images/hide1.png")));
            flag = 0;
        }
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new Login().setVisible(true));
    }

    // Variables
    private javax.swing.JButton jButton1, jButton2, jButton3, jButton4;
    private javax.swing.JLabel jLabel1, jLabel2, jLabel3, jLabel4, lvlhide, lblRole;
    private javax.swing.JPasswordField textpass;
    private javax.swing.JTextField textuserid;
    private javax.swing.JComboBox<String> cmbRole;
}
