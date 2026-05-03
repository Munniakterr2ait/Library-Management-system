package library.management.system;

/**
 * SessionManager - tracks who is currently logged in.
 * Call SessionManager.login(...) after successful login.
 * Use SessionManager.isAdmin() to check role anywhere in the app.
 */
public class SessionManager {

    public static final String ROLE_ADMIN   = "admin";
    public static final String ROLE_STUDENT = "student";

    private static String currentUserId   = null;
    private static String currentUserName = null;
    private static String currentRole     = null;
    private static String currentEmail    = null;
    private static String currentStudentId = null; // only set for students

    public static void loginAsAdmin(String userId, String name, String email) {
        currentUserId    = userId;
        currentUserName  = name != null ? name : userId;
        currentRole      = ROLE_ADMIN;
        currentEmail     = email;
        currentStudentId = null;
    }

    public static void loginAsStudent(String studentId, String name, String email) {
        currentUserId    = studentId;
        currentUserName  = name;
        currentRole      = ROLE_STUDENT;
        currentEmail     = email;
        currentStudentId = studentId;
    }

    public static void logout() {
        currentUserId    = null;
        currentUserName  = null;
        currentRole      = null;
        currentEmail     = null;
        currentStudentId = null;
    }

    public static boolean isAdmin()   { return ROLE_ADMIN.equals(currentRole);   }
    public static boolean isStudent() { return ROLE_STUDENT.equals(currentRole); }
    public static boolean isLoggedIn(){ return currentRole != null; }

    public static String getUserId()    { return currentUserId;    }
    public static String getUserName()  { return currentUserName;  }
    public static String getRole()      { return currentRole;      }
    public static String getEmail()     { return currentEmail;     }
    public static String getStudentId() { return currentStudentId; }
}
