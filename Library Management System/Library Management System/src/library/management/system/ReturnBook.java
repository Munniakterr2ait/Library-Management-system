package library.management.system;

import javax.swing.JOptionPane;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ReturnBook.java - with quantity restore on return
 * When a book is returned, available_qty is increased by 1
 */
public class ReturnBook extends javax.swing.JFrame {
    Connection con = connect.connection();
    PreparedStatement pat = null;
    ResultSet res = null;

    public ReturnBook() {
        initComponents();
        setLocationRelativeTo(null);
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        jLabel1       = new javax.swing.JLabel();
        btnclose      = new javax.swing.JButton();
        jLabel2       = new javax.swing.JLabel();
        jLabel3       = new javax.swing.JLabel();
        jLabel4       = new javax.swing.JLabel();
        jLabel5       = new javax.swing.JLabel();
        jLabel6       = new javax.swing.JLabel();
        jLabel7       = new javax.swing.JLabel();
        textstdid     = new javax.swing.JTextField();
        textbookid    = new javax.swing.JTextField();
        textbookname  = new javax.swing.JTextField();
        textstdname   = new javax.swing.JTextField();
        textissuedate = new javax.swing.JTextField();
        textduedate   = new javax.swing.JTextField();
        btnreturn     = new javax.swing.JButton();
        jButton1      = new javax.swing.JButton();
        jLabel8       = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18));
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/isue.jpg")));
        jLabel1.setText("Return Book");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 50, 200, -1));

        btnclose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/close icon.png")));
        btnclose.setBorderPainted(false);
        btnclose.setContentAreaFilled(false);
        btnclose.addActionListener(evt -> dispose());
        getContentPane().add(btnclose, new org.netbeans.lib.awtextra.AbsoluteConstraints(870, 30, -1, -1));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 16));
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Student ID");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 130, 100, -1));
        getContentPane().add(textstdid, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 130, 181, 32));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 16));
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Book ID");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 210, 100, -1));
        getContentPane().add(textbookid, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 210, 181, 32));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 16));
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Book Name");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 290, 100, -1));
        textbookname.setEditable(false);
        textbookname.setBackground(new java.awt.Color(220, 220, 220));
        getContentPane().add(textbookname, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 280, 200, 32));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 16));
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Student Name");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 360, 140, -1));
        textstdname.setEditable(false);
        textstdname.setBackground(new java.awt.Color(220, 220, 220));
        getContentPane().add(textstdname, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 360, 200, 32));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 16));
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Issue Date");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 430, 100, -1));
        textissuedate.setEditable(false);
        getContentPane().add(textissuedate, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 430, 181, 32));

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 16));
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Due Date");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 510, 100, -1));
        textduedate.setEditable(false);
        getContentPane().add(textduedate, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 510, 181, 32));

        // Search button
        jButton1.setBackground(new java.awt.Color(153, 0, 153));
        jButton1.setFont(new java.awt.Font("Segoe UI", 1, 14));
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Search");
        jButton1.setFocusPainted(false);
        jButton1.addActionListener(evt -> searchIssuedBook());
        getContentPane().add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 130, 92, 32));

        // Return button
        btnreturn.setBackground(new java.awt.Color(153, 0, 153));
        btnreturn.setFont(new java.awt.Font("Segoe UI", 1, 14));
        btnreturn.setForeground(new java.awt.Color(255, 255, 255));
        btnreturn.setText("Return Book");
        btnreturn.setFocusPainted(false);
        btnreturn.addActionListener(evt -> returnBook());
        getContentPane().add(btnreturn, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 590, 140, 40));

        // Background
        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/futuristic-bookshelf-editorial-photography-with-digital-style_950002-152359 (1).jpg")));
        getContentPane().add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(-3, -14, 950, 870));

        pack();
    }

    private void searchIssuedBook() {
        String stdId   = textstdid.getText().trim();
        String bookId  = textbookid.getText().trim();

        if (stdId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Student ID.");
            textstdid.requestFocus();
            return;
        }
        if (bookId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a Book ID.");
            textbookid.requestFocus();
            return;
        }

        try {
            pat = con.prepareStatement(
                "SELECT student.name, book.id, book.name, book.issuedate, book.duedate " +
                "FROM book INNER JOIN student ON book.studentid = student.id " +
                "WHERE book.studentid=? AND book.id=? AND book.status='ISSUED'"
            );
            pat.setString(1, stdId);
            pat.setString(2, bookId);
            res = pat.executeQuery();

            if (res.next()) {
                textstdname.setText(res.getString(1));
                textbookid.setText(res.getString(2));
                textbookname.setText(res.getString(3));
                textissuedate.setText(res.getString(4));
                textduedate.setText(res.getString(5));
            } else {
                JOptionPane.showMessageDialog(this,
                    "No issued book found.\n\nCheck that:\n• Student ID is correct\n• Book ID is correct\n• The book is currently issued to this student");
            }
        } catch (SQLException ex) {
            Logger.getLogger(ReturnBook.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void returnBook() {
        if (textbookid.getText().isEmpty() || textstdid.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please search first to load book details.");
            return;
        }

        try {
            // Verify still issued
            pat = con.prepareStatement("SELECT status FROM book WHERE id=?");
            pat.setString(1, textbookid.getText());
            res = pat.executeQuery();
            if (res.next() && !res.getString("status").equalsIgnoreCase("ISSUED")) {
                JOptionPane.showMessageDialog(this, "This book is not currently issued!");
                return;
            }

            // Return book: reset status, clear student info, restore available_qty
            pat = con.prepareStatement(
                "UPDATE book SET status='NOT ISSUE', issuedate=NULL, duedate=NULL, studentid=NULL, " +
                "available_qty = COALESCE(available_qty, 0) + 1 WHERE id=?"
            );
            pat.setString(1, textbookid.getText());
            int k = pat.executeUpdate();

            if (k > 0) {
                JOptionPane.showMessageDialog(this,
                    "✅ Book Returned Successfully!\n\nBook: " + textbookname.getText() +
                    "\nStudent: " + textstdname.getText());
                // Clear fields
                textbookid.setText(""); textbookname.setText("");
                textstdid.setText(""); textstdname.setText("");
                textissuedate.setText(""); textduedate.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "❌ Return failed. Please try again.");
            }
        } catch (SQLException ex) {
            Logger.getLogger(ReturnBook.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new ReturnBook().setVisible(true));
    }

    // Variables
    private javax.swing.JButton btnclose, btnreturn, jButton1;
    private javax.swing.JLabel jLabel1, jLabel2, jLabel3, jLabel4, jLabel5, jLabel6, jLabel7, jLabel8;
    private javax.swing.JTextField textbookid, textbookname, textduedate, textissuedate, textstdid, textstdname;
}
