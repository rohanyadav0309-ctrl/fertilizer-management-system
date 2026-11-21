import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnection.java
 * -----------------
 * Handles database connectivity using JDBC.
 * 
 * Smart Dual-Mode Feature:
 * 1. MySQL Mode: If MySQL server is installed & running on localhost:3306,
 *    it connects to MySQL using JDBC.
 * 2. Zero-Installation Mode: If MySQL is NOT installed or offline,
 *    it automatically falls back to DataStore.java (pure Java in-memory),
 *    allowing the project to run from terminal with 100% complete data!
 */
public class DBConnection {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/fertilizer_management?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    // Cache the connection check status to prevent repeated connection timeouts
    private static Boolean isConnected = null;

    /**
     * Checks if MySQL database is reachable.
     */
    public static boolean isOnline() {
        if (isConnected != null) {
            return isConnected;
        }

        try {
            // Check if MySQL driver is present
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                Class.forName("com.mysql.jdbc.Driver");
            }

            // Quick test connection with 2-second timeout
            DriverManager.setLoginTimeout(2);
            Connection testConn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            if (testConn != null && !testConn.isClosed()) {
                testConn.close();
                isConnected = true;
                return true;
            }
        } catch (Throwable t) {
            // MySQL is not installed or not running
            isConnected = false;
        }

        isConnected = false;
        return false;
    }

    /**
     * Returns an active MySQL JDBC Connection if available, or null.
     */
    public static Connection getConnection() {
        if (!isOnline()) {
            return null;
        }

        try {
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
        } catch (SQLException e) {
            return null;
        }
    }

    /**
     * Returns a human-friendly string of the current mode.
     */
    public static String getModeDescription() {
        if (isOnline()) {
            return "MySQL Connected (localhost:3306/fertilizer_management)";
        } else {
            return "Standalone Mode (Zero Installation - Preloaded Data Active)";
        }
    }

    /**
     * Resets connection status (e.g. after starting MySQL).
     */
    public static void refreshStatus() {
        isConnected = null;
        isOnline();
    }
}
