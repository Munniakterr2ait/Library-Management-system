package library.management.system;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 * Addbook.java - WITH QUANTITY SUPPORT
 * - Adds 'quantity' and 'available_qty' columns
 * - Shows quantity field in the form
 */
public class Addbook extends javax.swing.JFrame {

    public Addbook() {
        initComponents();
        setLocationRelativeTo(null);
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        jLabel1       = new javax.swing.JLabel();
        jLabel2       = new javax.swing.JLabel();
        jLabel3       = new javax.swing.JLabel();
        jLabel4       = new javax.swing.JLabel();
        jLabel5       = new javax.swing.JLabel();
        jLabel6       = new javax.swing.JLabel();
        jLabelQty     = new javax.swing.JLabel();
        textid        = new javax.swing.JTextField();
        textname      = new javax.swing.JTextField();
        textpublisher = new javax.swing.JTextField();
        textprice     = new javax.swing.JTextField();
        textpubyear   = new javax.swing.JTextField();
        textqty       = new javax.swing.JTextField();
        btnsave       = new javax.swing.JButton();
        jButton2      = new javax.swing.JButton();
        jLabel7       = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 18));
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/isue.jpg")));
        jLabel1.setText("Add Book Details");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 40, 240, -1));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 16));
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Book Id");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 130, 96, 31));
        getContentPane().add(textid, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 130, 230, 31));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 16));
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Book Name");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 200, 110, 31));
        getContentPane().add(textname, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 200, 230, 31));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 16));
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Publisher");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 270, 110, 30));
        getContentPane().add(textpublisher, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 270, 230, 31));

        jLabel5.setFont(new java.awt.Font("Segoe UI", 1, 16));
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Price");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 340, 80, 31));
        getContentPane().add(textprice, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 340, 230, 31));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 16));
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("Publish Year");
        getContentPane().add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 410, 120, 32));
        getContentPane().add(textpubyear, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 410, 230, 31));

        // ── NEW: Quantity field ──────────────────────────
        jLabelQty.setFont(new java.awt.Font("Segoe UI", 1, 16));
        jLabelQty.setForeground(new java.awt.Color(255, 255, 0)); // yellow to stand out
        jLabelQty.setText("Quantity");
        getContentPane().add(jLabelQty, new org.netbeans.lib.awtextra.AbsoluteConstraints(120, 480, 110, 32));

        textqty.setText("1");
        getContentPane().add(textqty, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 480, 100, 31));

        // Save button
        btnsave.setBackground(new java.awt.Color(153, 0, 153));
        btnsave.setFont(new java.awt.Font("Segoe UI", 1, 16));
        btnsave.setForeground(new java.awt.Color(255, 255, 255));
        btnsave.setText("Save");
        btnsave.setFocusPainted(false);
        btnsave.addActionListener(evt -> btnsaveActionPerformed());
        getContentPane().add(btnsave, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 560, 110, 40));

        // Close button
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/close icon.png")));
        jButton2.setBorderPainted(false);
        jButton2.setContentAreaFilled(false);
        jButton2.addActionListener(evt -> dispose());
        getContentPane().add(jButton2, new org.netbeans.lib.awtextra.AbsoluteConstraints(891, 27, -1, -1));

        // Background
        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/aadd book.jpg")));
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 970, 790));

        pack();
    }

    private void btnsaveActionPerformed() {
        String id        = textid.getText().trim();
        String name      = textname.getText().trim();
        String publisher = textpublisher.getText().trim();
        String price     = textprice.getText().trim();
        String year      = textpubyear.getText().trim();
        String qtyStr    = textqty.getText().trim();

        if (id.isEmpty() || name.isEmpty() || publisher.isEmpty()
                || price.isEmpty() || year.isEmpty() || qtyStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!");
            return;
        }

        int qty;
        try {
            qty = Integer.parseInt(qtyStr);
            if (qty < 1) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Quantity must be a whole number (e.g. 1, 5, 10)");
            textqty.requestFocus();
            return;
        }

        try {
            Connection con = connect.connection();

            // Check if book ID already exists
            PreparedStatement chk = con.prepareStatement("SELECT id FROM book WHERE id=?");
            chk.setString(1, id);
            ResultSet rs = chk.executeQuery();
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Book ID already exists! Use a different ID.");
                textid.requestFocus();
                return;
            }

            // Insert book — status 'NOT ISSUE', quantity = total, available_qty = total
            PreparedStatement pa = con.prepareStatement(
                "INSERT INTO book(id, name, publisher, price, year, status, quantity, available_qty) " +
                "VALUES(?,?,?,?,?,?,?,?)"
            );
            pa.setString(1, id);
            pa.setString(2, name);
            pa.setString(3, publisher);
            pa.setString(4, price);
            pa.setString(5, year);
            pa.setString(6, "NOT ISSUE");
            pa.setInt(7, qty);
            pa.setInt(8, qty);
            pa.executeUpdate();

            JOptionPane.showMessageDialog(this,
                "✅ Book saved!\n\nBook: " + name + "\nQuantity: " + qty + " cop" + (qty == 1 ? "y" : "ies"));
            textid.setText(""); textname.setText(""); textpublisher.setText("");
            textprice.setText(""); textpubyear.setText(""); textqty.setText("1");
            con.close();

        } catch (SQLException ex) {
            Logger.getLogger(Addbook.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> new Addbook().setVisible(true));
    }

    // Variables
    private javax.swing.JButton btnsave, jButton2;
    private javax.swing.JLabel jLabel1, jLabel2, jLabel3, jLabel4, jLabel5, jLabel6, jLabel7, jLabelQty;
    private javax.swing.JTextField textid, textname, textpublisher, textprice, textpubyear, textqty;
}
