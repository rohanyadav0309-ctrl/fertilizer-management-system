import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Login.java
 * ----------
 * Screen 1: Login Screen for Fertilizer Management System.
 * Authenticates user credentials using MySQL 'admin' table, or
 * in-memory DataStore when running in Zero-Installation terminal mode.
 */
public class Login extends JFrame implements ActionListener {

    // Swing GUI Components
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnReset;
    private JLabel lblMode;

    // Theme Colors (Green & White Combination)
    private static final Color PRIMARY_GREEN = new Color(46, 125, 50);    // #2E7D32
    private static final Color BG_LIGHT      = new Color(245, 250, 245);  // Soft light green-white
    private static final Color TEXT_DARK     = new Color(33, 33, 33);

    /**
     * Constructor: Initializes GUI components and layout.
     */
    public Login() {
        setTitle("Fertilizer Management System - Login");
        setSize(480, 440);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center window on screen
        setResizable(false);
        setLayout(new BorderLayout());

        // ------------------ TOP HEADER PANEL ------------------
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(PRIMARY_GREEN);
        headerPanel.setPreferredSize(new Dimension(480, 90));
        headerPanel.setLayout(new BorderLayout());

        JLabel lblTitle = new JLabel("Fertilizer Management System", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Second-Year Java Project - Admin Portal", JLabel.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(230, 245, 230));

        headerPanel.add(lblTitle, BorderLayout.CENTER);
        headerPanel.add(lblSub, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // ------------------ CENTER FORM PANEL ------------------
        JPanel formPanel = new JPanel();
        formPanel.setBackground(BG_LIGHT);
        formPanel.setLayout(null);

        // Instructions note
        JLabel lblHint = new JLabel("Default Login: admin / admin123");
        lblHint.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblHint.setForeground(new Color(80, 80, 80));
        lblHint.setBounds(60, 15, 360, 20);
        formPanel.add(lblHint);

        // Username Label & Field
        JLabel lblUser = new JLabel("Username:");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblUser.setForeground(TEXT_DARK);
        lblUser.setBounds(60, 45, 120, 25);
        formPanel.add(lblUser);

        txtUsername = new JTextField("admin");
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsername.setBounds(60, 75, 350, 35);
        formPanel.add(txtUsername);

        // Password Label & Field
        JLabel lblPass = new JLabel("Password:");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPass.setForeground(TEXT_DARK);
        lblPass.setBounds(60, 125, 120, 25);
        formPanel.add(lblPass);

        txtPassword = new JPasswordField("admin123");
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setBounds(60, 155, 350, 35);
        formPanel.add(txtPassword);

        // Login Button
        btnLogin = new JButton("Login");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnLogin.setBackground(PRIMARY_GREEN);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setBounds(60, 215, 165, 40);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.addActionListener(this);
        formPanel.add(btnLogin);

        // Reset Button
        btnReset = new JButton("Reset");
        btnReset.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnReset.setBackground(new Color(220, 220, 220));
        btnReset.setForeground(TEXT_DARK);
        btnReset.setFocusPainted(false);
        btnReset.setBounds(245, 215, 165, 40);
        btnReset.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnReset.addActionListener(this);
        formPanel.add(btnReset);

        add(formPanel, BorderLayout.CENTER);

        // ------------------ FOOTER PANEL ------------------
        JPanel footerPanel = new JPanel(new GridLayout(2, 1));
        footerPanel.setBackground(Color.WHITE);
        footerPanel.setPreferredSize(new Dimension(480, 45));

        lblMode = new JLabel(DBConnection.getModeDescription(), JLabel.CENTER);
        lblMode.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblMode.setForeground(DBConnection.isOnline() ? PRIMARY_GREEN : new Color(0, 120, 215));

        JLabel lblFooter = new JLabel("Core Java & Swing | 2nd Year Engineering Practical", JLabel.CENTER);
        lblFooter.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblFooter.setForeground(Color.GRAY);

        footerPanel.add(lblMode);
        footerPanel.add(lblFooter);
        add(footerPanel, BorderLayout.SOUTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnLogin) {
            handleLogin();
        } else if (e.getSource() == btnReset) {
            txtUsername.setText("");
            txtPassword.setText("");
            txtUsername.requestFocus();
        }
    }

    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        // 1. Validation check for empty fields
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your username!", "Input Error", JOptionPane.WARNING_MESSAGE);
            txtUsername.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your password!", "Input Error", JOptionPane.WARNING_MESSAGE);
            txtPassword.requestFocus();
            return;
        }

        // 2. Authentication check: MySQL or Zero-Installation DataStore
        boolean authenticated = false;
        String adminName = username;

        if (DBConnection.isOnline()) {
            Connection conn = DBConnection.getConnection();
            if (conn != null) {
                try {
                    String sql = "SELECT * FROM admin WHERE username = ? AND password = ?";
                    PreparedStatement pst = conn.prepareStatement(sql);
                    pst.setString(1, username);
                    pst.setString(2, password);

                    ResultSet rs = pst.executeQuery();
                    if (rs.next()) {
                        authenticated = true;
                        adminName = rs.getString("full_name");
                    }
                    rs.close();
                    pst.close();
                    conn.close();
                } catch (SQLException ex) {
                    System.err.println("Database query error: " + ex.getMessage());
                }
            }
        }

        // Fallback or Standalone Mode authentication
        if (!authenticated) {
            String match = DataStore.authenticate(username, password);
            if (match != null) {
                authenticated = true;
                adminName = match;
            }
        }

        // 3. Navigation upon successful authentication
        if (authenticated) {
            JOptionPane.showMessageDialog(this,
                "Login Successful! Welcome, " + adminName + ".\n" +
                "Status: " + DBConnection.getModeDescription(),
                "Success", JOptionPane.INFORMATION_MESSAGE);

            Dashboard dashboard = new Dashboard(adminName);
            dashboard.setVisible(true);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid Username or Password. Please try again.", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Login().setVisible(true);
            }
        });
    }
}
