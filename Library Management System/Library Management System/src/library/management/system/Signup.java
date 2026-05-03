package library.management.system;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

/**
 * Signup Form - Admin Registration
 * Fixed & Complete version
 */
public class Signup extends javax.swing.JFrame {

    int flagPass = 0;

    public Signup() {
        initComponents();
        setLocationRelativeTo(null);
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        jLabel3    = new javax.swing.JLabel();
        jLabel1    = new javax.swing.JLabel();
        textname   = new javax.swing.JTextField();
        jLabel2    = new javax.swing.JLabel();
        jLabel6    = new javax.swing.JLabel();
        txtemail   = new javax.swing.JTextField();
        jLabel7    = new javax.swing.JLabel();
        textpass   = new javax.swing.JPasswordField();
        lvlhide    = new javax.swing.JLabel();
        jButton3   = new javax.swing.JButton();
        jButton1   = new javax.swing.JButton();
        jLabel9    = new javax.swing.JLabel();
        jLabel4    = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        // Title
        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 22));
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Create Admin Account");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 260, 280, 35));

        // Name label & field
        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 16));
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("User ID:");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 320, 120, -1));
        getContentPane().add(textname, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 318, 220, 28));

        // Email label & field
        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 16));
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Email:");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 365, 120, -1));
        getContentPane().add(txtemail, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 362, 220, 28));

        // Password label & field
        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 16));
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Password:");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 410, 120, -1));
        getContentPane().add(textpass, new org.netbeans.lib.awtextra.AbsoluteConstraints(650, 407, 190, 28));

        // Show/hide password toggle
        lvlhide.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/hide1.png")));
        lvlhide.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lvlhide.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) { togglePassword(); }
        });
        getContentPane().add(lvlhide, new org.netbeans.lib.awtextra.AbsoluteConstraints(848, 407, 28, 28));

        // Signup button
        jButton3.setBackground(new java.awt.Color(153, 0, 153));
        jButton3.setFont(new java.awt.Font("Segoe UI", 1, 14));
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setText("Signup Now");
        jButton3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton3.addActionListener(evt -> doSignup());
        getContentPane().add(jButton3, new org.netbeans.lib.awtextra.AbsoluteConstraints(555, 465, 140, 38));

        // Back to login button
        jButton1.setBackground(new java.awt.Color(100, 0, 100));
        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 14));
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Back to Login");
        jButton1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton1.addActionListener(evt -> { new Login().setVisible(true); dispose(); });
        getContentPane().add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 465, 160, 38));

        // Close button
        jLabel9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/close icon.png")));
        jLabel9.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jLabel9.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int yes = JOptionPane.showConfirmDialog(null, "Exit application?", "Exit",
                        JOptionPane.YES_NO_OPTION);
                if (yes == JOptionPane.YES_OPTION) System.exit(0);
            }
        });
        getContentPane().add(jLabel9, new org.netbeans.lib.awtextra.AbsoluteConstraints(1220, 30, 40, 40));

        // Background
        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/login page.jpg")));
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(-30, -40, 1530, 850));

        pack();
        setLocationRelativeTo(null);
    }

    private void togglePassword() {
        if (flagPass == 0) {
            textpass.setEchoChar((char) 0);
            lvlhide.setIcon(new ImageIcon(getClass().getResource("/images/show-eye.png")));
            flagPass = 1;
        } else {
            textpass.setEchoChar('*');
            lvlhide.setIcon(new ImageIcon(getClass().getResource("/images/hide1.png")));
            flagPass = 0;
        }
    }

    private void doSignup() {
        String userId   = textname.getText().trim();
        String email    = txtemail.getText().trim();
        String password = new String(textpass.getPassword());

        // Validation
        if (userId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "User ID is required");
            textname.requestFocus(); return;
        }
        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Email is required");
            txtemail.requestFocus(); return;
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(this, "Invalid email format");
            txtemail.requestFocus(); return;
        }
        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Password is required");
            textpass.requestFocus(); return;
        }
        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this, "Password must be at least 6 characters");
            textpass.requestFocus(); return;
        }

        try {
            Connection con = connect.connection();

            // Check if userid already exists
            PreparedStatement check = con.prepareStatement("SELECT userid FROM login WHERE userid=? OR email=?");
            check.setString(1, userId);
            check.setString(2, email);
            ResultSet rs = check.executeQuery();
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "User ID or Email already exists! Please use a different one.");
                return;
            }

            // Insert new admin
            PreparedStatement insert = con.prepareStatement(
                "INSERT INTO login (userid, password, email, role) VALUES (?, ?, ?, 'admin')"
            );
            insert.setString(1, userId);
            insert.setString(2, password);
            insert.setString(3, email);
            insert.executeUpdate();

            JOptionPane.showMessageDialog(this, "Account Created Successfully!\nYou can now login with: " + userId);
            new Login().setVisible(true);
            dispose();

        } catch (SQLException ex) {
            Logger.getLogger(Signup.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new Signup().setVisible(true));
    }

    // Variables
    private javax.swing.JButton jButton1, jButton3;
    private javax.swing.JLabel jLabel1, jLabel2, jLabel3, jLabel4, jLabel6, jLabel7, jLabel9, lvlhide;
    private javax.swing.JTextField textname, txtemail;
    private javax.swing.JPasswordField textpass;
}
