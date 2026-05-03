package library.management.system;

import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import java.util.Date;
import java.awt.*;

/**
 * IssueBook - with search by Book ID OR Book Name, plus quantity support.
 * - Reduces available_qty by 1 on issue
 * - Prevents issuing if available_qty = 0
 * - Search book by name (shows dropdown if multiple results)
 */
public class IssueBook extends javax.swing.JFrame {

    Connection con = connect.connection();
    PreparedStatement pat = null;
    ResultSet res = null;

    // Stores the actual book ID after search by name
    private String resolvedBookId = null;

    public IssueBook() {
        initComponents();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        textissuedate.setText(sdf.format(new Date()));
        int dueDays = getDueDaysFromSettings();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, dueDays);
        textduedate.setText(sdf.format(cal.getTime()));
    }

    private int getDueDaysFromSettings() {
        try {
            ResultSet rs = connect.connection().createStatement()
                    .executeQuery("SELECT due_days FROM settings WHERE id=1");
            if (rs.next()) return rs.getInt(1);
        } catch (Exception ignored) {}
        return 14;
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        jLabel1       = new JLabel();
        btnclose      = new JButton();
        jLabel2       = new JLabel();
        jLabel3       = new JLabel();
        jLabel4       = new JLabel();
        jLabel5       = new JLabel();
        jLabel6       = new JLabel();
        jLabel8       = new JLabel();
        jLabelQtyInfo = new JLabel();
        textbookid    = new JTextField();
        textstdid     = new JTextField();
        textbookname  = new JTextField();
        textstdname   = new JTextField();
        textissuedate = new JTextField();
        textduedate   = new JTextField();
        btnissue      = new JButton();
        btnsearch     = new JButton();
        btnSearchByName = new JButton();
        btnSearchStd  = new JButton();
        jLabel7       = new JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new Font("Segoe UI", Font.BOLD, 18));
        jLabel1.setForeground(Color.WHITE);
        jLabel1.setIcon(new ImageIcon(getClass().getResource("/images/isue.jpg")));
        jLabel1.setText("Issue Book");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(65, 30, 200, -1));

        btnclose.setIcon(new ImageIcon(getClass().getResource("/images/close icon.png")));
        btnclose.setBorderPainted(false);
        btnclose.setContentAreaFilled(false);
        btnclose.addActionListener(evt -> dispose());
        getContentPane().add(btnclose, new org.netbeans.lib.awtextra.AbsoluteConstraints(904, 21, -1, -1));

        // ── Book ID row ──────────────────────────────────────────
        jLabel2.setFont(new Font("Segoe UI", Font.BOLD, 16));
        jLabel2.setForeground(Color.WHITE);
        jLabel2.setText("Book ID");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 105, 80, -1));
        getContentPane().add(textbookid, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 103, 160, 30));

        btnsearch.setBackground(new Color(153, 0, 153));
        btnsearch.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnsearch.setForeground(Color.WHITE);
        btnsearch.setText("Search ID");
        btnsearch.setFocusPainted(false);
        btnsearch.addActionListener(evt -> searchBookById());
        getContentPane().add(btnsearch, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 103, 100, 30));

        // ── Search by name row ───────────────────────────────────
        JLabel lblSearchName = new JLabel("Book Name");
        lblSearchName.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblSearchName.setForeground(Color.WHITE);
        getContentPane().add(lblSearchName, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 155, 110, -1));

        JTextField textSearchName = new JTextField();
        textSearchName.setToolTipText("Type book name to search");
        getContentPane().add(textSearchName, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 153, 160, 30));

        btnSearchByName.setBackground(new Color(0, 100, 180));
        btnSearchByName.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSearchByName.setForeground(Color.WHITE);
        btnSearchByName.setText("Search Name");
        btnSearchByName.setFocusPainted(false);
        btnSearchByName.addActionListener(evt -> searchBookByName(textSearchName.getText().trim()));
        getContentPane().add(btnSearchByName, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 153, 110, 30));

        // ── Found book name (read-only) ──────────────────────────
        jLabel4.setFont(new Font("Segoe UI", Font.BOLD, 16));
        jLabel4.setForeground(Color.WHITE);
        jLabel4.setText("Found Book");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 205, 120, -1));

        textbookname.setEditable(false);
        textbookname.setBackground(new Color(220, 220, 220));
        getContentPane().add(textbookname, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 203, 290, 30));

        // ── Quantity info label ──────────────────────────────────
        jLabelQtyInfo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        jLabelQtyInfo.setForeground(new Color(255, 220, 80));
        jLabelQtyInfo.setText("Available: —");
        getContentPane().add(jLabelQtyInfo, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 237, 290, 20));

        // ── Student ID row ───────────────────────────────────────
        jLabel3.setFont(new Font("Segoe UI", Font.BOLD, 16));
        jLabel3.setForeground(Color.WHITE);
        jLabel3.setText("Student ID");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 275, 110, -1));
        getContentPane().add(textstdid, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 273, 160, 30));

        btnSearchStd.setBackground(new Color(0, 130, 80));
        btnSearchStd.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnSearchStd.setForeground(Color.WHITE);
        btnSearchStd.setText("Verify");
        btnSearchStd.setFocusPainted(false);
        btnSearchStd.addActionListener(evt -> verifyStudent());
        getContentPane().add(btnSearchStd, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 273, 100, 30));

        // ── Student name (read-only) ─────────────────────────────
        jLabel8.setFont(new Font("Segoe UI", Font.BOLD, 16));
        jLabel8.setForeground(Color.WHITE);
        jLabel8.setText("Student Name");
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 325, 130, -1));
        textstdname.setEditable(false);
        textstdname.setBackground(new Color(220, 220, 220));
        getContentPane().add(textstdname, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 323, 290, 30));

        // ── Issue Date ───────────────────────────────────────────
        jLabel5.setFont(new Font("Segoe UI", Font.BOLD, 16));
        jLabel5.setForeground(Color.WHITE);
        jLabel5.setText("Issue Date");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 375, 110, -1));
        textissuedate.setEditable(false);
        textissuedate.setBackground(new Color(220, 220, 220));
        getContentPane().add(textissuedate, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 373, 160, 30));

        // ── Due Date ─────────────────────────────────────────────
        jLabel6.setFont(new Font("Segoe UI", Font.BOLD, 16));
        jLabel6.setForeground(Color.WHITE);
        jLabel6.setText("Due Date");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(160, 425, 110, -1));
        getContentPane().add(textduedate, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 423, 160, 30));

        // ── Issue button ─────────────────────────────────────────
        btnissue.setBackground(new Color(153, 0, 153));
        btnissue.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnissue.setForeground(Color.WHITE);
        btnissue.setText("Issue Book");
        btnissue.setFocusPainted(false);
        btnissue.addActionListener(evt -> issueBook());
        getContentPane().add(btnissue, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 490, 180, 45));

        // Background
        jLabel7.setIcon(new ImageIcon(getClass().getResource("/images/aadd book.jpg")));
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 970, 790));

        pack();
        setLocationRelativeTo(null);
    }

    /** Search book by exact Book ID */
    private void searchBookById() {
        String bookId = textbookid.getText().trim();
        if (bookId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Book ID.");
            return;
        }
        loadBookInfo(bookId);
    }

    /** Search book by partial name — shows chooser if multiple matches */
    private void searchBookByName(String namePart) {
        if (namePart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please type a book name to search.");
            return;
        }
        try {
            PreparedStatement ps = con.prepareStatement(
                "SELECT id, name, available_qty, status FROM book WHERE name LIKE ? ORDER BY name"
            );
            ps.setString(1, "%" + namePart + "%");
            ResultSet rs = ps.executeQuery();

            java.util.List<String[]> books = new java.util.ArrayList<>();
            while (rs.next()) {
                books.add(new String[]{rs.getString(1), rs.getString(2),
                    rs.getString(3), rs.getString(4)});
            }

            if (books.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No books found matching: \"" + namePart + "\"");
                return;
            }

            if (books.size() == 1) {
                // Only one result — load it directly
                textbookid.setText(books.get(0)[0]);
                loadBookInfo(books.get(0)[0]);
            } else {
                // Multiple results — show chooser
                String[] options = books.stream()
                    .map(b -> "[" + b[0] + "] " + b[1] + "  (Available: " + (b[2] != null ? b[2] : "?") + ")")
                    .toArray(String[]::new);
                String choice = (String) JOptionPane.showInputDialog(this,
                    "Multiple books found. Select one:",
                    "Choose Book", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                if (choice != null) {
                    // Extract the book ID from the chosen string
                    String chosenId = choice.substring(1, choice.indexOf("]"));
                    textbookid.setText(chosenId);
                    loadBookInfo(chosenId);
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(IssueBook.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /** Load book information from DB and populate fields */
    private void loadBookInfo(String bookId) {
        try {
            PreparedStatement ps = con.prepareStatement(
                "SELECT name, status, quantity, available_qty FROM book WHERE id=?"
            );
            ps.setString(1, bookId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String status = rs.getString("status");
                String bookName = rs.getString("name");
                int qty = rs.getInt("quantity");
                int availQty = rs.getInt("available_qty");

                // Handle old books with no quantity data (NULL → treat as 1)
                if (rs.wasNull()) { qty = 1; availQty = ("ISSUED".equalsIgnoreCase(status) ? 0 : 1); }

                textbookname.setText(bookName);
                resolvedBookId = bookId;

                if (availQty <= 0) {
                    jLabelQtyInfo.setText("Available: 0 / " + qty + "  ⛔ All copies issued");
                    jLabelQtyInfo.setForeground(new java.awt.Color(255, 80, 80));
                    textbookname.setForeground(java.awt.Color.RED);
                } else {
                    jLabelQtyInfo.setText("Available: " + availQty + " / " + qty + " cop" + (qty == 1 ? "y" : "ies"));
                    jLabelQtyInfo.setForeground(new java.awt.Color(100, 255, 100));
                    textbookname.setForeground(java.awt.Color.BLACK);
                }
            } else {
                JOptionPane.showMessageDialog(this, "❌ Book ID \"" + bookId + "\" not found.");
                textbookname.setText("");
                jLabelQtyInfo.setText("Available: —");
                resolvedBookId = null;
            }
        } catch (SQLException ex) {
            Logger.getLogger(IssueBook.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void verifyStudent() {
        String stdId = textstdid.getText().trim();
        if (stdId.isEmpty()) { JOptionPane.showMessageDialog(this, "Enter a Student ID."); return; }
        try {
            PreparedStatement ps = con.prepareStatement("SELECT name FROM student WHERE id=?");
            ps.setString(1, stdId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                textstdname.setText(rs.getString("name"));
                textstdname.setForeground(new java.awt.Color(0, 100, 0));
            } else {
                JOptionPane.showMessageDialog(this, "❌ Student ID not found. Register the student first.");
                textstdname.setText("NOT FOUND");
                textstdname.setForeground(java.awt.Color.RED);
            }
        } catch (SQLException ex) {
            Logger.getLogger(IssueBook.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void issueBook() {
        String bookId   = resolvedBookId != null ? resolvedBookId : textbookid.getText().trim();
        String bookName = textbookname.getText().trim();
        String stdId    = textstdid.getText().trim();
        String stdName  = textstdname.getText().trim();
        String dueDate  = textduedate.getText().trim();

        if (bookId.isEmpty() || bookName.isEmpty() || bookName.equals("NOT FOUND")) {
            JOptionPane.showMessageDialog(this, "Search and verify a book first."); return;
        }
        if (stdId.isEmpty() || stdName.isEmpty() || stdName.equals("NOT FOUND")) {
            JOptionPane.showMessageDialog(this, "Enter and verify a student ID first."); return;
        }
        if (dueDate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter a due date."); return;
        }

        try {
            // Re-check availability
            PreparedStatement chk = con.prepareStatement(
                "SELECT available_qty, quantity FROM book WHERE id=?"
            );
            chk.setString(1, bookId);
            ResultSet chkRs = chk.executeQuery();
            if (chkRs.next()) {
                int avail = chkRs.getInt("available_qty");
                int total = chkRs.getInt("quantity");
                if (chkRs.wasNull()) { avail = 1; total = 1; }
                if (avail <= 0) {
                    JOptionPane.showMessageDialog(this,
                        "❌ No copies available!\nAll " + total + " cop" + (total == 1 ? "y" : "ies") +
                        " of this book are already issued.");
                    return;
                }
            } else {
                JOptionPane.showMessageDialog(this, "❌ Book not found."); return;
            }

            // ✅ FIXED UPDATE with WHERE id=? — updates ONLY this book
            // Also decrements available_qty by 1
            pat = con.prepareStatement(
                "UPDATE book SET status='ISSUED', issuedate=?, duedate=?, studentid=?, " +
                "available_qty = GREATEST(COALESCE(available_qty,1) - 1, 0) WHERE id=?"
            );
            pat.setString(1, textissuedate.getText());
            pat.setString(2, dueDate);
            pat.setString(3, stdId);
            pat.setString(4, bookId);

            int rows = pat.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this,
                    "✅ Book Issued Successfully!\n\nBook  : " + bookName +
                    "\nTo    : " + stdName + " (" + stdId + ")\nDue   : " + dueDate,
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                clearFields();
            } else {
                JOptionPane.showMessageDialog(this, "❌ Issue failed.");
            }
        } catch (SQLException ex) {
            Logger.getLogger(IssueBook.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    private void clearFields() {
        textbookid.setText(""); textbookname.setText(""); textstdid.setText("");
        textstdname.setText(""); jLabelQtyInfo.setText("Available: —");
        jLabelQtyInfo.setForeground(new java.awt.Color(255, 220, 80));
        resolvedBookId = null;
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        textissuedate.setText(sdf.format(new Date()));
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, getDueDaysFromSettings());
        textduedate.setText(sdf.format(cal.getTime()));
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new IssueBook().setVisible(true));
    }

    // Variables
    private JButton btnclose, btnissue, btnsearch, btnSearchByName, btnSearchStd;
    private JLabel jLabel1, jLabel2, jLabel3, jLabel4, jLabel5, jLabel6, jLabel7, jLabel8, jLabelQtyInfo;
    private JTextField textbookid, textbookname, textstdid, textstdname, textissuedate, textduedate;
}
