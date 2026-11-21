import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Main.java
 * ---------
 * Entry point of the Fertilizer Management System.
 * 
 * Second-Year Java Laboratory Project
 * Technology: Core Java, Java Swing (GUI), JDBC, MySQL Database
 * 
 * Features:
 * 1. Admin Login & Authentication
 * 2. Real-time Dashboard with stock statistics
 * 3. Fertilizer CRUD & Search operations
 * 4. Customer / Farmer Management
 * 5. Sales & Billing with automatic total calculation & stock deduction
 * 6. Stock Inventory Management with low-stock warnings
 */
public class Main {

    public static void main(String[] args) {
        // Set native operating system Look and Feel for clean modern buttons & window styling
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("Using default Swing Look and Feel: " + e.getMessage());
        }

        // Run GUI construction on the Event Dispatch Thread (EDT) for thread safety
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                System.out.println("=================================================");
                System.out.println("   Fertilizer Management System Starting...     ");
                System.out.println("   Second-Year Java Engineering Laboratory      ");
                System.out.println("=================================================");
                
                // Launch the Login Screen
                Login loginScreen = new Login();
                loginScreen.setVisible(true);
            }
        });
    }
}
