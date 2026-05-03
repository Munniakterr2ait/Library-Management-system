package library.management.system;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

/**
 * Settings Panel - Configure library name, SMTP email, due days, fine
 */
public class Settings extends javax.swing.JFrame {

    private JTextField txtLibraryName, txtSmtpHost, txtSmtpPort, txtSmtpEmail, txtDueDays, txtFinePerDay;
    private JPasswordField txtSmtpPass;
    private JTextField txtCurrentUserId, txtNewPassword;

    public Settings() {
        initComponents();
        loadSettings();
        setLocationRelativeTo(null);
    }

    private void initComponents() {
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        setSize(720, 640);
        getContentPane().setBackground(new java.awt.Color(20, 20, 40));
        getContentPane().setLayout(null);

        // ── TOP BAR ──────────────────────────────────────
        JPanel topBar = new JPanel(null);
        topBar.setBackground(new java.awt.Color(80, 0, 100));
        topBar.setBounds(0, 0, 720, 55);

        JLabel title = new JLabel("  ⚙  Library Settings");
        title.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 20));
        title.setForeground(java.awt.Color.WHITE);
        title.setBounds(10, 10, 400, 35);
        topBar.add(title);

        JButton btnClose = new JButton("✖");
        btnClose.setBounds(675, 10, 40, 35);
        btnClose.setBackground(new java.awt.Color(180, 0, 0));
        btnClose.setForeground(java.awt.Color.WHITE);
        btnClose.setFocusPainted(false);
        btnClose.setBorderPainted(false);
        btnClose.addActionListener(e -> dispose());
        topBar.add(btnClose);
        add(topBar);

        // ── LIBRARY SETTINGS SECTION ──────────────────────
        addSectionLabel("📚 Library Configuration", 30, 70);

        addLabel("Library Name:", 30, 105);
        txtLibraryName = addField(200, 103, 480);

        addLabel("Loan Period (days):", 30, 145);
        txtDueDays = addField(200, 143, 100);

        addLabel("Fine Per Day (Tk):", 30, 185);
        txtFinePerDay = addField(200, 183, 100);

        // ── EMAIL / SMTP SECTION ──────────────────────────
        addSectionLabel("📧 Email (SMTP) Settings  — for overdue reminders", 30, 230);

        addLabel("SMTP Host:", 30, 265);
        txtSmtpHost = addField(200, 263, 300);

        addLabel("SMTP Port:", 30, 305);
        txtSmtpPort = addField(200, 303, 100);

        addLabel("Sender Email:", 30, 345);
        txtSmtpEmail = addField(200, 343, 300);

        addLabel("Email Password:", 30, 385);
        txtSmtpPass = new JPasswordField();
        txtSmtpPass.setBounds(200, 383, 300, 28);
        txtSmtpPass.setBackground(new java.awt.Color(40, 40, 60));
        txtSmtpPass.setForeground(java.awt.Color.WHITE);
        txtSmtpPass.setCaretColor(java.awt.Color.WHITE);
        add(txtSmtpPass);

        JLabel hint = new JLabel("(Use Gmail App Password — not your normal Gmail password)");
        hint.setFont(new java.awt.Font("Segoe UI", java.awt.Font.ITALIC, 11));
        hint.setForeground(new java.awt.Color(180, 180, 180));
        hint.setBounds(200, 413, 480, 20);
        add(hint);

        // ── CHANGE ADMIN PASSWORD SECTION ─────────────────
        addSectionLabel("🔐 Change Admin Password", 30, 440);

        addLabel("Admin User ID:", 30, 475);
        txtCurrentUserId = addField(200, 473, 200);

        addLabel("New Password:", 30, 510);
        txtNewPassword = addField(200, 508, 200);

        JButton btnChangePass = makeBtn("Update Password", new java.awt.Color(0, 120, 180), 420, 507, 190, 32);
        btnChangePass.addActionListener(e -> changePassword());
        add(btnChangePass);

        // ── SAVE / CANCEL BUTTONS ─────────────────────────
        JButton btnSave = makeBtn("💾  Save Settings", new java.awt.Color(0, 150, 80), 200, 570, 180, 40);
        btnSave.addActionListener(e -> saveSettings());
        add(btnSave);

        JButton btnCancel = makeBtn("Cancel", new java.awt.Color(100, 0, 0), 400, 570, 120, 40);
        btnCancel.addActionListener(e -> dispose());
        add(btnCancel);
    }

    private void loadSettings() {
        try {
            Connection con = connect.connection();
            ResultSet rs = con.createStatement().executeQuery("SELECT * FROM settings WHERE id=1");
            if (rs.next()) {
                txtLibraryName.setText(rs.getString("library_name"));
                txtSmtpHost.setText(rs.getString("smtp_host"));
                txtSmtpPort.setText(String.valueOf(rs.getInt("smtp_port")));
                txtSmtpEmail.setText(rs.getString("smtp_email"));
                txtSmtpPass.setText(rs.getString("smtp_password"));
                txtDueDays.setText(String.valueOf(rs.getInt("due_days")));
                txtFinePerDay.setText(rs.getString("fine_per_day"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void saveSettings() {
        try {
            Connection con = connect.connection();
            PreparedStatement ps = con.prepareStatement(
                "UPDATE settings SET library_name=?, smtp_host=?, smtp_port=?, smtp_email=?, smtp_password=?, due_days=?, fine_per_day=? WHERE id=1"
            );
            ps.setString(1, txtLibraryName.getText().trim());
            ps.setString(2, txtSmtpHost.getText().trim());
            ps.setInt(3, Integer.parseInt(txtSmtpPort.getText().trim()));
            ps.setString(4, txtSmtpEmail.getText().trim());
            ps.setString(5, new String(txtSmtpPass.getPassword()));
            ps.setInt(6, Integer.parseInt(txtDueDays.getText().trim()));
            ps.setDouble(7, Double.parseDouble(txtFinePerDay.getText().trim()));
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "✅ Settings saved successfully!");
        } catch (SQLException | NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Error saving settings: " + ex.getMessage());
        }
    }

    private void changePassword() {
        String userId = txtCurrentUserId.getText().trim();
        String newPass = txtNewPassword.getText().trim();
        if (userId.isEmpty() || newPass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter admin User ID and new password");
            return;
        }
        if (newPass.length() < 6) {
            JOptionPane.showMessageDialog(this, "Password must be at least 6 characters");
            return;
        }
        try {
            Connection con = connect.connection();
            PreparedStatement ps = con.prepareStatement("UPDATE login SET password=? WHERE userid=?");
            ps.setString(1, newPass);
            ps.setString(2, userId);
            int rows = ps.executeUpdate();
            if (rows > 0) JOptionPane.showMessageDialog(this, "✅ Password changed successfully!");
            else JOptionPane.showMessageDialog(this, "User ID not found!");
        } catch (SQLException ex) {
            Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // ── UI helpers ────────────────────────────────────────────────
    private void addSectionLabel(String text, int x, int y) {
        JLabel l = new JLabel(text);
        l.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 14));
        l.setForeground(new java.awt.Color(200, 150, 255));
        l.setBounds(x, y, 650, 24);
        add(l);
    }

    private void addLabel(String text, int x, int y) {
        JLabel l = new JLabel(text);
        l.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 14));
        l.setForeground(java.awt.Color.WHITE);
        l.setBounds(x, y, 160, 25);
        add(l);
    }

    private JTextField addField(int x, int y, int width) {
        JTextField f = new JTextField();
        f.setBounds(x, y, width, 28);
        f.setBackground(new java.awt.Color(40, 40, 60));
        f.setForeground(java.awt.Color.WHITE);
        f.setCaretColor(java.awt.Color.WHITE);
        f.setBorder(BorderFactory.createLineBorder(new java.awt.Color(100, 0, 130)));
        add(f);
        return f;
    }

    private JButton makeBtn(String text, java.awt.Color bg, int x, int y, int w, int h) {
        JButton b = new JButton(text);
        b.setBackground(bg);
        b.setForeground(java.awt.Color.WHITE);
        b.setFont(new java.awt.Font("Segoe UI", java.awt.Font.BOLD, 13));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        b.setBounds(x, y, w, h);
        return b;
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new Settings().setVisible(true));
    }
}
