import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Dashboard.java
 * --------------
 * Screen 2: Main Dashboard / Home Page.
 * Displays key summary statistics:
 * - Total fertilizers count
 * - Total customers count
 * - Available stock quantity
 * - Low-stock warnings
 * Supports both MySQL and Standalone Zero-Installation mode.
 */
public class Dashboard extends JFrame implements ActionListener {

    private String currentUser;

    // Stat card labels
    private JLabel lblTotalFertilizersValue;
    private JLabel lblTotalCustomersValue;
    private JLabel lblTotalStockValue;
    private JLabel lblLowStockValue;
    private JPanel lowStockBanner;
    private JLabel lblBannerText;
    private JLabel lblStatus;

    // Navigation Buttons
    private JButton btnFertilizer;
    private JButton btnCustomer;
    private JButton btnSales;
    private JButton btnStock;
    private JButton btnRefresh;
    private JButton btnLogout;

    // Theme Colors
    private static final Color PRIMARY_GREEN   = new Color(46, 125, 50);    // #2E7D32
    private static final Color BG_LIGHT        = new Color(245, 250, 245);
    private static final Color CARD_BG         = Color.WHITE;
    private static final Color WARNING_COLOR   = new Color(211, 47, 47);    // Red for low stock

    public Dashboard(String username) {
        this.currentUser = (username != null && !username.isEmpty()) ? username : "Administrator";

        setTitle("Fertilizer Management System - Dashboard");
        setSize(920, 680);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(BG_LIGHT);

        // ------------------ TOP HEADER ------------------
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_GREEN);
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblTitle = new JLabel("Fertilizer Management System");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblWelcome = new JLabel("Welcome: " + currentUser + "  |  Dashboard Home");
        lblWelcome.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblWelcome.setForeground(new Color(230, 245, 230));

        JPanel leftTitlePanel = new JPanel(new GridLayout(2, 1));
        leftTitlePanel.setOpaque(false);
        leftTitlePanel.add(lblTitle);
        leftTitlePanel.add(lblWelcome);

        btnLogout = new JButton("Logout");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnLogout.setBackground(new Color(239, 83, 80)); // Soft Red
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(this);

        headerPanel.add(leftTitlePanel, BorderLayout.WEST);
        headerPanel.add(btnLogout, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // ------------------ MAIN CONTENT PANEL ------------------
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        // 1. Low Stock Warning Banner
        lowStockBanner = new JPanel(new BorderLayout());
        lowStockBanner.setBackground(new Color(255, 235, 238));
        lowStockBanner.setBorder(new LineBorder(new Color(239, 83, 80), 1, true));
        lowStockBanner.setPreferredSize(new Dimension(860, 40));
        lowStockBanner.setMaximumSize(new Dimension(860, 40));

        lblBannerText = new JLabel("  [Warning] Some fertilizer items have critically low stock (< 10 bags)! Please check Stock Management.");
        lblBannerText.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblBannerText.setForeground(WARNING_COLOR);
        lowStockBanner.add(lblBannerText, BorderLayout.CENTER);
        lowStockBanner.setVisible(false);

        centerPanel.add(lowStockBanner);
        centerPanel.add(Box.createVerticalStrut(15));

        // 2. Statistics Summary Cards (Grid of 4 Cards)
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.setOpaque(false);
        statsPanel.setMaximumSize(new Dimension(860, 110));
        statsPanel.setPreferredSize(new Dimension(860, 110));

        lblTotalFertilizersValue = new JLabel("0", JLabel.CENTER);
        statsPanel.add(createStatCard("Total Fertilizers", lblTotalFertilizersValue, PRIMARY_GREEN));

        lblTotalCustomersValue = new JLabel("0", JLabel.CENTER);
        statsPanel.add(createStatCard("Total Customers", lblTotalCustomersValue, new Color(2, 136, 209)));

        lblTotalStockValue = new JLabel("0", JLabel.CENTER);
        statsPanel.add(createStatCard("Available Stock (Bags)", lblTotalStockValue, new Color(0, 121, 107)));

        lblLowStockValue = new JLabel("0", JLabel.CENTER);
        statsPanel.add(createStatCard("Low Stock Alert (<10)", lblLowStockValue, WARNING_COLOR));

        centerPanel.add(statsPanel);
        centerPanel.add(Box.createVerticalStrut(25));

        // 3. Navigation Title
        JLabel lblNavHeading = new JLabel("Modules & Quick Actions");
        lblNavHeading.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblNavHeading.setForeground(PRIMARY_GREEN);
        lblNavHeading.setAlignmentX(Component.LEFT_ALIGNMENT);
        centerPanel.add(lblNavHeading);
        centerPanel.add(Box.createVerticalStrut(10));

        // 4. Navigation Buttons Grid
        JPanel navGrid = new JPanel(new GridLayout(2, 2, 20, 20));
        navGrid.setOpaque(false);
        navGrid.setMaximumSize(new Dimension(860, 260));
        navGrid.setPreferredSize(new Dimension(860, 260));

        btnFertilizer = createNavButton("1. Fertilizer Management", "Add, View, Update, Delete & Search Fertilizers", PRIMARY_GREEN);
        btnCustomer   = createNavButton("2. Customer Management", "Register and manage customer profiles & contacts", new Color(46, 125, 50));
        btnSales      = createNavButton("3. Sales & Billing", "Process customer sales, auto-calculate bills, view history", new Color(56, 142, 60));
        btnStock      = createNavButton("4. Stock & Inventory", "Monitor stock availability, low-stock warnings & restock", new Color(46, 125, 50));

        navGrid.add(btnFertilizer);
        navGrid.add(btnCustomer);
        navGrid.add(btnSales);
        navGrid.add(btnStock);

        centerPanel.add(navGrid);
        centerPanel.add(Box.createVerticalGlue());

        add(centerPanel, BorderLayout.CENTER);

        // ------------------ FOOTER / REFRESH BAR ------------------
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(Color.WHITE);
        footerPanel.setBorder(new EmptyBorder(10, 20, 10, 20));

        lblStatus = new JLabel("Mode: " + DBConnection.getModeDescription());
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStatus.setForeground(new Color(60, 60, 60));

        btnRefresh = new JButton("Refresh Statistics");
        btnRefresh.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRefresh.setBackground(PRIMARY_GREEN);
        btnRefresh.setForeground(Color.WHITE);
        btnRefresh.setFocusPainted(false);
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.addActionListener(this);

        footerPanel.add(lblStatus, BorderLayout.WEST);
        footerPanel.add(btnRefresh, BorderLayout.EAST);
        add(footerPanel, BorderLayout.SOUTH);

        // Load stats
        loadDashboardStats();
    }

    private JPanel createStatCard(String title, JLabel valueLabel, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(220, 220, 220), 1, true),
            new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel lblCardTitle = new JLabel(title, JLabel.CENTER);
        lblCardTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblCardTitle.setForeground(new Color(100, 100, 100));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        valueLabel.setForeground(accentColor);

        card.add(lblCardTitle, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    private JButton createNavButton(String title, String subtitle, Color topColor) {
        JButton btn = new JButton();
        btn.setLayout(new BorderLayout());
        btn.setBackground(Color.WHITE);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(200, 230, 200), 2, true),
            new EmptyBorder(15, 20, 15, 20)
        ));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(topColor);

        JLabel lblSub = new JLabel("<html><p style='color:#666666; font-size:11px;'>" + subtitle + "</p></html>");

        btn.add(lblTitle, BorderLayout.NORTH);
        btn.add(lblSub, BorderLayout.CENTER);
        btn.addActionListener(this);
        return btn;
    }

    /**
     * Loads live count statistics from MySQL database or Standalone DataStore.
     */
    public void loadDashboardStats() {
        int totalFertilizers = 0;
        int totalCustomers = 0;
        int totalStock = 0;
        int lowCount = 0;

        if (DBConnection.isOnline()) {
            Connection conn = DBConnection.getConnection();
            if (conn != null) {
                try {
                    Statement stmt = conn.createStatement();

                    ResultSet rs1 = stmt.executeQuery("SELECT COUNT(*) AS total FROM fertilizer");
                    if (rs1.next()) totalFertilizers = rs1.getInt("total");
                    rs1.close();

                    ResultSet rs2 = stmt.executeQuery("SELECT COUNT(*) AS total FROM customer");
                    if (rs2.next()) totalCustomers = rs2.getInt("total");
                    rs2.close();

                    ResultSet rs3 = stmt.executeQuery("SELECT COALESCE(SUM(quantity), 0) AS total_stock FROM fertilizer");
                    if (rs3.next()) totalStock = rs3.getInt("total_stock");
                    rs3.close();

                    ResultSet rs4 = stmt.executeQuery("SELECT COUNT(*) AS low_count FROM fertilizer WHERE quantity < 10");
                    if (rs4.next()) lowCount = rs4.getInt("low_count");
                    rs4.close();

                    stmt.close();
                    conn.close();
                } catch (Exception ex) {
                    System.err.println("Dashboard stats load error: " + ex.getMessage());
                }
            }
        } else {
            // Standalone Zero-Installation mode
            totalFertilizers = DataStore.getTotalFertilizers();
            totalCustomers   = DataStore.getTotalCustomers();
            totalStock       = DataStore.getTotalStock();
            lowCount         = DataStore.getLowStockCount();
        }

        lblTotalFertilizersValue.setText(String.valueOf(totalFertilizers));
        lblTotalCustomersValue.setText(String.valueOf(totalCustomers));
        lblTotalStockValue.setText(String.valueOf(totalStock));
        lblLowStockValue.setText(String.valueOf(lowCount));

        if (lowCount > 0) {
            lblBannerText.setText("  [Warning] " + lowCount + " fertilizer item(s) have low stock (< 10 bags)! Please check Stock Management.");
            lowStockBanner.setVisible(true);
        } else {
            lowStockBanner.setVisible(false);
        }

        if (lblStatus != null) {
            lblStatus.setText("Mode: " + DBConnection.getModeDescription());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnFertilizer) {
            new Fertilizer(this).setVisible(true);
            this.setVisible(false);
        } else if (e.getSource() == btnCustomer) {
            new Customer(this).setVisible(true);
            this.setVisible(false);
        } else if (e.getSource() == btnSales) {
            new Sales(this).setVisible(true);
            this.setVisible(false);
        } else if (e.getSource() == btnStock) {
            new Stock(this).setVisible(true);
            this.setVisible(false);
        } else if (e.getSource() == btnRefresh) {
            loadDashboardStats();
            JOptionPane.showMessageDialog(this, "Dashboard statistics updated successfully!", "Refreshed", JOptionPane.INFORMATION_MESSAGE);
        } else if (e.getSource() == btnLogout) {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                new Login().setVisible(true);
                this.dispose();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Dashboard("Administrator").setVisible(true);
            }
        });
    }
}
