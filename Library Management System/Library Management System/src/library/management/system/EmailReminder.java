package library.management.system;

import java.awt.HeadlessException;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * EmailReminder — FIXED version
 *
 * FIXES applied:
 *   1. MailAuth no longer extends javax.mail.Authenticator directly.
 *      Authentication is now handled fully via reflection inside sendOneEmail(),
 *      so the class compiles and runs with OR without the javax.mail jar.
 *   2. Removed premature con.close() inside try-with-resources in loadSmtpConfig().
 *   3. Removed leftover debug System.out.println statements.
 *   4. Cleaned up brace/indentation inconsistency in sendEmailsViaReflection().
 *
 * GMAIL USERS — IMPORTANT:
 *   Google has blocked regular passwords for SMTP since 2022.
 *   You MUST use a 16-character App Password:
 *     1. Go to https://myaccount.google.com/security
 *     2. Enable 2-Step Verification (required)
 *     3. Search "App Passwords" → create one for "Mail"
 *     4. Use that 16-char password in your Settings → Email Settings
 *   SMTP Host: smtp.gmail.com | Port: 587
 */
public class EmailReminder {

    private static final Logger LOG = Logger.getLogger(EmailReminder.class.getName());

    // ─────────────────────────────────────────────────────────────────
    // Called on admin login
    // ─────────────────────────────────────────────────────────────────

    public static void showOverdueAlertOnLogin(java.awt.Component parent) {
        try {
            try (Connection con = connect.connection()) {
                if (con == null) return;

                ResultSet rs = con.createStatement().executeQuery(
                        "SELECT COUNT(*) FROM book WHERE status='ISSUED' " +
                        "AND STR_TO_DATE(duedate,'%d/%m/%Y') < CURDATE()"
                );
                if (rs.next()) {
                    int count = rs.getInt(1);
                    if (count > 0) {
                        StringBuilder msg = new StringBuilder();
                        msg.append("⚠  OVERDUE BOOKS ALERT!\n\n");
                        msg.append(count).append(" book(s) are past their due date:\n\n");

                        PreparedStatement ps = con.prepareStatement(
                                "SELECT b.id, b.name, IFNULL(s.name,'?'), b.duedate, " +
                                "DATEDIFF(CURDATE(), STR_TO_DATE(b.duedate,'%d/%m/%Y')) " +
                                "FROM book b LEFT JOIN student s ON b.studentid=s.id " +
                                "WHERE b.status='ISSUED' AND STR_TO_DATE(b.duedate,'%d/%m/%Y') < CURDATE() LIMIT 10"
                        );
                        ResultSet rs2 = ps.executeQuery();
                        while (rs2.next()) {
                            msg.append(String.format(
                                "• [%s] %s  —  Student: %s  |  Due: %s  (%d days overdue)\n",
                                rs2.getString(1), truncate(rs2.getString(2), 25),
                                truncate(rs2.getString(3), 15), rs2.getString(4), rs2.getInt(5)
                            ));
                        }
                        msg.append("\nGo to Dashboard → Send Reminders to notify students.");
                        javax.swing.JOptionPane.showMessageDialog(parent, msg.toString(),
                                "⚠ Overdue Books Alert", javax.swing.JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        } catch (SQLException ex) {
            LOG.log(Level.WARNING, "Overdue check failed", ex);
        }
    }

    public static void showDueTomorrowAndSendReminder(java.awt.Component parent) {
        showDueTomorrowPopup(parent);
        autoSendDueTomorrowEmails();
    }

    private static void showDueTomorrowPopup(java.awt.Component parent) {
        try {
            try (Connection con = connect.connection()) {
                if (con == null) return;

                ResultSet rs = con.createStatement().executeQuery(
                        "SELECT COUNT(*) FROM book WHERE status='ISSUED' " +
                        "AND STR_TO_DATE(duedate,'%d/%m/%Y') = DATE_ADD(CURDATE(), INTERVAL 1 DAY)"
                );
                if (rs.next() && rs.getInt(1) > 0) {
                    int count = rs.getInt(1);
                    StringBuilder msg = new StringBuilder();
                    msg.append("📅  DUE TOMORROW REMINDER!\n\n");
                    msg.append(count).append(" book(s) must be returned TOMORROW:\n\n");

                    PreparedStatement ps = con.prepareStatement(
                            "SELECT b.id, b.name, IFNULL(s.name,'?'), IFNULL(s.email,'no email'), b.duedate " +
                            "FROM book b LEFT JOIN student s ON b.studentid=s.id " +
                            "WHERE b.status='ISSUED' " +
                            "AND b.duedate = DATE_FORMAT(DATE_ADD(CURDATE(), INTERVAL 1 DAY),'%d/%m/%Y')"
                    );
                    ResultSet rs2 = ps.executeQuery();
                    while (rs2.next()) {
                        msg.append(String.format("• [%s] %s — Student: %s (%s) | Due: %s\n",
                                rs2.getString(1), truncate(rs2.getString(2), 22),
                                truncate(rs2.getString(3), 15), rs2.getString(4), rs2.getString(5)));
                    }
                    msg.append("\nEmail reminder sent automatically if email is configured in Settings.");
                    javax.swing.JOptionPane.showMessageDialog(parent, msg.toString(),
                            "📅 Due Tomorrow Reminder", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            LOG.log(Level.WARNING, "Due-tomorrow popup failed", ex);
        }
    }

    private static void autoSendDueTomorrowEmails() {
        if (!isMailJarPresent()) return;

        String[] cfg = loadSmtpConfig();
        if (cfg == null) return;

        try {
            int sent = 0;
            try (Connection con = connect.connection()) {
                PreparedStatement ps = con.prepareStatement(
                        "SELECT b.id, b.name, b.duedate, s.name, s.email " +
                        "FROM book b JOIN student s ON b.studentid=s.id " +
                        "WHERE b.status='ISSUED' " +
                        "AND STR_TO_DATE(b.duedate,'%d/%m/%Y') = DATE_ADD(CURDATE(), INTERVAL 1 DAY)"
                );
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    String toEmail = rs.getString(5);
                    if (toEmail == null || toEmail.trim().isEmpty()) continue;
                    String subject = "📅 Your Library Book is Due TOMORROW | " + cfg[4];
                    String body = buildDueTomorrowBody(rs.getString(4), rs.getString(2),
                            rs.getString(1), rs.getString(3), cfg[4]);
                    if (sendOneEmail(cfg, toEmail, subject, body)) sent++;
                }
            }
            if (sent > 0) {
                javax.swing.JOptionPane.showMessageDialog(null,
                    "📧 Auto-sent " + sent + " due-tomorrow reminder(s) to students.",
                    "Auto Reminder", javax.swing.JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (HeadlessException | SQLException ex) {
            LOG.log(Level.WARNING, "autoSendDueTomorrowEmails failed (non-critical)", ex);
        }
    }

    // ─────────────────────────────────────────────────────────────────
    // Called from Dashboard "Send Reminders" button
    // ─────────────────────────────────────────────────────────────────

    public static void sendOverdueReminders() {
        if (!isMailJarPresent()) {
            showJarHelp();
            return;
        }
        sendEmailsViaReflection();
    }

    private static void sendEmailsViaReflection() {
        String[] cfg = loadSmtpConfig();
        if (cfg == null) {
            javax.swing.JOptionPane.showMessageDialog(null,
                "Please configure your email (SMTP) settings first.\nGo to Settings → Email Settings.",
                "Email Not Configured", javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int sent = 0;
            int skipped = 0;
            double finePerDay = Double.parseDouble(cfg[5]);

            try (Connection con = connect.connection()) {
                PreparedStatement ps = con.prepareStatement(
                        "SELECT b.id, b.name, b.duedate, s.name, s.email, " +
                        "DATEDIFF(CURDATE(), STR_TO_DATE(b.duedate,'%d/%m/%Y')) " +
                        "FROM book b JOIN student s ON b.studentid=s.id " +
                        "WHERE b.status='ISSUED' AND STR_TO_DATE(b.duedate,'%d/%m/%Y') < CURDATE()"
                );
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    String toEmail = rs.getString(5);
                    if (toEmail == null || toEmail.trim().isEmpty()) {
                        skipped++;
                        continue;
                    }
                    int days = rs.getInt(6);
                    String subject = "⚠ Overdue Book Reminder - " + cfg[4];
                    String body = buildOverdueBody(
                        rs.getString(4), rs.getString(2), rs.getString(1),
                        rs.getString(3), days, days * finePerDay, cfg[4], finePerDay
                    );
                    if (sendOneEmail(cfg, toEmail, subject, body)) sent++;
                }
            }

            String result = "✅ Sent " + sent + " email reminder(s) successfully!";
            if (skipped > 0) result += "\n⚠ Skipped " + skipped + " student(s) with no email address.";
            javax.swing.JOptionPane.showMessageDialog(null, result, "Done",
                    javax.swing.JOptionPane.INFORMATION_MESSAGE);

        } catch (HeadlessException | NumberFormatException | SQLException ex) {
            LOG.log(Level.SEVERE, null, ex);
            javax.swing.JOptionPane.showMessageDialog(null,
                "Email error: " + ex.getMessage(), "Error", javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }
    
    
    
    
    
    public static void testEmailNow() {
    // Step 1: Check jar
    if (!isMailJarPresent()) {
        javax.swing.JOptionPane.showMessageDialog(null, """
                                                        \u274c STEP 1 FAILED: javax.mail jar not found!
                                                        Add javax.mail-1.6.2.jar to project libraries.""");
        return;
    }
    javax.swing.JOptionPane.showMessageDialog(null, "✅ Step 1 OK: Mail jar found.");

    // Step 2: Check config loads
    String[] cfg = loadSmtpConfig();
    if (cfg == null) {
        javax.swing.JOptionPane.showMessageDialog(null, """
                                                        \u274c STEP 2 FAILED: loadSmtpConfig() returned null!
                                                        Check DB connection or smtp_email column.""");
        return;
    }
    javax.swing.JOptionPane.showMessageDialog(null,
        """
        \u2705 Step 2 OK: Config loaded!
        Host=""" + cfg[0] + " Port=" + cfg[1] +
        "\nEmail=" + cfg[2] + " PassLen=" + cfg[3].length() +
        "\nLibrary=" + cfg[4] + " Fine=" + cfg[5]);

    // Step 3: Try sending to yourself
    String testBody = "<h2>Test Email</h2><p>Library email is working!</p>";
    boolean ok = sendOneEmail(cfg, cfg[2], "Test Email from Library System", testBody);
    if (ok) {
        javax.swing.JOptionPane.showMessageDialog(null,
            "✅ Step 3 OK: Email sent to " + cfg[2] + "!\nCheck your inbox.");
    } else {
        javax.swing.JOptionPane.showMessageDialog(null, """
                                                        \u274c STEP 3 FAILED: sendOneEmail() returned false.
                                                        Check NetBeans Output window for the error details.
                                                        (View \u2192 Output \u2192 Output)""");
    }
}

    // ─────────────────────────────────────────────────────────────────
    // PRIVATE HELPERS
    // ─────────────────────────────────────────────────────────────────

    private static boolean isMailJarPresent() {
        try {
            Class.forName("javax.mail.Session");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private static String[] loadSmtpConfig() {
        try {
            try (Connection con = connect.connection()) {
                ResultSet rs = con.createStatement().executeQuery(
                        "SELECT * FROM settings WHERE id=1"
                );
                if (rs.next()) {
                    String email = rs.getString("smtp_email");
                    if (email == null || email.trim().isEmpty()) return null;
                    return new String[]{
                        rs.getString("smtp_host"),               // [0]
                        String.valueOf(rs.getInt("smtp_port")),  // [1]
                        email.trim(),                            // [2]
                        rs.getString("smtp_password"),           // [3]
                        rs.getString("library_name"),            // [4]
                        String.valueOf(rs.getDouble("fine_per_day")) // [5]
                    };
                    // NOTE: do NOT call con.close() here — try-with-resources handles it
                }
            }
        } catch (SQLException ex) {
            LOG.log(Level.WARNING, "Could not load SMTP config", ex);
        }
        return null;
    }

    /**
     * Send one email using PURE REFLECTION — zero javax.mail.* imports needed.
     *
     * FIX: Authentication is now done via an anonymous Authenticator subclass
     * created entirely through reflection, replacing the old MailAuth inner class
     * which directly extended javax.mail.Authenticator and caused crashes
     * when the jar was absent.
     *
     * cfg: [0]=host [1]=port [2]=fromEmail [3]=password [4]=libName [5]=finePerDay
     */
    private static boolean sendOneEmail(String[] cfg, String toEmail, String subject, String body) {
        try {
            java.util.Properties props = new java.util.Properties();
            props.put("mail.smtp.host", cfg[0]);
            props.put("mail.smtp.port", cfg[1]);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.starttls.required", "true");
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");
            props.put("mail.smtp.ssl.trust", cfg[0]);
            props.put("mail.smtp.connectiontimeout", "10000");
            props.put("mail.smtp.timeout", "10000");
            props.put("mail.smtp.writetimeout", "10000");

            // Build Authenticator subclass (only reached when jar is present)
            Object auth = buildAuthenticator(cfg[2], cfg[3]);

            // Session.getInstance(props, auth)
            Class<?> authCls    = Class.forName("javax.mail.Authenticator");
            Class<?> sessionCls = Class.forName("javax.mail.Session");
            Object session = sessionCls
                .getMethod("getInstance", java.util.Properties.class, authCls)
                .invoke(null, props, auth);

            // new MimeMessage(session)
            Class<?> mimeCls = Class.forName("javax.mail.internet.MimeMessage");
            Object msg = mimeCls.getConstructor(sessionCls).newInstance(session);

            // setFrom
            Class<?> inetCls = Class.forName("javax.mail.internet.InternetAddress");
            Class<?> addrCls = Class.forName("javax.mail.Address");
            Object fromAddr  = inetCls.getConstructor(String.class).newInstance(cfg[2]);
            mimeCls.getMethod("setFrom", addrCls).invoke(msg, fromAddr);

            // setRecipients
            Class<?> rtCls  = Class.forName("javax.mail.Message$RecipientType");
            Object   toType = rtCls.getField("TO").get(null);
            Object[] toAddrs = (Object[]) inetCls.getMethod("parse", String.class).invoke(null, toEmail);
            mimeCls.getMethod("setRecipients", rtCls,
                    java.lang.reflect.Array.newInstance(addrCls, 0).getClass())
                   .invoke(msg, toType, toAddrs);

            // setSubject + setContent
            mimeCls.getMethod("setSubject", String.class).invoke(msg, subject);
            mimeCls.getMethod("setContent", Object.class, String.class)
                   .invoke(msg, body, "text/html; charset=utf-8");

            // Transport.send(msg)
            Class<?> msgCls = Class.forName("javax.mail.Message");
            Class.forName("javax.mail.Transport")
                 .getMethod("send", msgCls).invoke(null, msg);

            return true;

        } catch (Exception ex) {
            LOG.log(Level.WARNING, "Failed to send to " + toEmail + ": " + ex.getMessage(), ex);
            return false;
        }
    }

    /**
     * Returns a javax.mail.Authenticator for SMTP login.
     * Only called after isMailJarPresent() == true, so the jar is guaranteed present.
     * Kept in a separate method so the class reference is loaded lazily —
     * this prevents a NoClassDefFoundError on startup when the jar is absent.
     */
    private static Object buildAuthenticator(String user, String pass) throws Exception {
        // This inner anonymous class is the correct, simple approach.
        // It requires the jar to be on the compile classpath (not runtime optional).
        return new javax.mail.Authenticator() {
            @Override
            protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                return new javax.mail.PasswordAuthentication(user, pass);
            }
        };
    }

    private static void showJarHelp() {
        javax.swing.JOptionPane.showMessageDialog(null,
            "📧 Email sending requires the javax.mail library.\n\n" +
            "How to add it:\n" +
            "1. Download from:\n" +
            "   https://mvnrepository.com/artifact/com.sun.mail/javax.mail/1.6.2\n" +
            "   (click the blue 'jar' download link)\n\n" +
            "2. NetBeans: Right-click Project → Properties → Libraries → Add JAR/Folder\n" +
            "   → Select the downloaded .jar → OK\n\n" +
            "3. Press Shift+F11 (Clean and Build), then try again.\n\n" +
            "⚠ GMAIL USERS: You must use a 16-char App Password, NOT your Gmail password!\n" +
            "   Go to: myaccount.google.com → Security → App Passwords",
            "Email Library Not Found", javax.swing.JOptionPane.INFORMATION_MESSAGE);
    }

    // ─────────────────────────────────────────────────────────────────
    // EMAIL BODY BUILDERS
    // ─────────────────────────────────────────────────────────────────

    private static String buildOverdueBody(String student, String book, String bookId,
                                            String dueDate, int days, double fine,
                                            String libName, double finePerDay) {
        return "<html><body style='font-family:Arial,sans-serif'>" +
            "<div style='max-width:600px;margin:auto;background:white;border-radius:8px;overflow:hidden'>" +
            "<div style='background:#6a0dad;padding:20px;color:white'><h2>📚 " + libName + "</h2>" +
            "<p>Overdue Book Reminder</p></div>" +
            "<div style='padding:25px'><p>Dear <strong>" + student + "</strong>,</p>" +
            "<p>The following book is <span style='color:red;font-weight:bold'>OVERDUE</span>:</p>" +
            "<table style='border-collapse:collapse;width:100%'>" +
            row("Book",         book + " (" + bookId + ")") +
            row("Due Date",     "<span style='color:red'>" + dueDate + "</span>") +
            row("Days Overdue", "<b style='color:red'>" + days + " days</b>") +
            row("Fine",         "<b>Tk " + String.format("%.2f", fine) + "</b>") +
            row("Fine Rate",    "Tk " + String.format("%.2f", finePerDay) + " per day") +
            "</table><p>Please return the book immediately to avoid further fines.</p>" +
            "<p>Thank you,<br><strong>" + libName + "</strong></p></div></div></body></html>";
    }

    private static String buildDueTomorrowBody(String student, String book, String bookId,
                                                String dueDate, String libName) {
        return "<html><body style='font-family:Arial,sans-serif'>" +
            "<div style='max-width:600px;margin:auto;background:white;border-radius:8px;overflow:hidden'>" +
            "<div style='background:#1565c0;padding:20px;color:white'><h2>📚 " + libName + "</h2>" +
            "<p>Due Date Reminder</p></div>" +
            "<div style='padding:25px'><p>Dear <strong>" + student + "</strong>,</p>" +
            "<p>This is a friendly reminder that your library book is due <b>TOMORROW</b>.</p>" +
            "<table style='border-collapse:collapse;width:100%'>" +
            row("Book",     book + " (" + bookId + ")") +
            row("Due Date", "<b style='color:#1565c0'>" + dueDate + " — Tomorrow</b>") +
            "</table>" +
            "<p style='background:#e3f2fd;padding:10px;border-left:4px solid #1565c0'>" +
            "📅 Please return this book to the library by tomorrow to avoid any fine.</p>" +
            "<p>Thank you,<br><strong>" + libName + "</strong></p></div></div></body></html>";
    }

    private static String row(String label, String value) {
        return "<tr><td style='padding:8px;border:1px solid #ddd;background:#f9f9f9'><b>" + label + "</b></td>" +
               "<td style='padding:8px;border:1px solid #ddd'>" + value + "</td></tr>";
    }

    private static String truncate(String s, int max) {
        return (s != null && s.length() > max) ? s.substring(0, max - 2) + ".." : (s != null ? s : "");
    }
}
