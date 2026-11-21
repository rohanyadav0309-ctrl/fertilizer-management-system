import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

/**
 * Stock.java
 * ----------
 * Screen 6: Stock & Inventory Management Screen.
 * Displays:
 * - Current stock levels
 * - Low-stock warnings and badges (<10 bags)
 * - Quick Restock / Stock Update tool
 * - Filtering (All, Low Stock, Out of Stock)
 * Supports both MySQL and Standalone Zero-Installation mode.
 */
public class Stock extends JFrame implements ActionListener {

    private Dashboard parentDashboard;

    static class RestockItem {
        int id;
        String name;
        int currentStock;

        public RestockItem(int id, String name, int currentStock) {
            this.id = id;
            this.name = name;
            this.currentStock = currentStock;
        }

        @Override
        public String toString() {
            return id + " - " + name + " (Current: " + currentStock + " bags)";
        }
    }

    private JComboBox<RestockItem> cmbFertilizer;
    private JTextField txtAddQuantity;
    private JButton btnUpdateStock;
    private JButton btnRefresh;
    private JButton btnBack;

    private JButton btnFilterAll;
    private JButton btnFilterLow;
    private JButton btnFilterOut;
    private String currentFilter = "ALL";

    private JPanel warningBanner;
    private JLabel lblBannerMessage;

    private JTable tblStock;
    private DefaultTableModel tableModel;

    private static final Color PRIMARY_GREEN  = new Color(46, 125, 50);
    private static final Color BG_LIGHT       = new Color(245, 250, 245);
    private static final Color WARNING_RED    = new Color(211, 47, 47);
    private static final Color WARNING_ORANGE = new Color(230, 81, 0);

    public Stock(Dashboard dashboard) {
        this.parentDashboard = dashboard;

        setTitle("Fertilizer Management System - Stock Inventory & Restock");
        setSize(980, 680);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(BG_LIGHT);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                goBack();
            }
        });

        // ------------------ HEADER ------------------
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_GREEN);
        headerPanel.setBorder(new EmptyBorder(12, 20, 12, 20));

        JLabel lblTitle = new JLabel("Stock & Inventory Management Module");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);

        btnBack = new JButton("<- Back to Dashboard");
        btnBack.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnBack.setBackground(Color.WHITE);
        btnBack.setForeground(PRIMARY_GREEN);
        btnBack.setFocusPainted(false);
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBack.addActionListener(this);

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(btnBack, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // ------------------ MAIN CONTAINER ------------------
        JPanel mainContent = new JPanel(new BorderLayout(10, 12));
        mainContent.setOpaque(false);
        mainContent.setBorder(new EmptyBorder(10, 20, 10, 20));

        // 1. WARNING BANNER
        warningBanner = new JPanel(new BorderLayout());
        warningBanner.setBackground(new Color(255, 235, 238));
        warningBanner.setBorder(new LineBorder(new Color(239, 83, 80), 1, true));
        warningBanner.setPreferredSize(new Dimension(940, 38));

        lblBannerMessage = new JLabel("  [Notice] Fertilizer stock levels are being monitored in real-time.");
        lblBannerMessage.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblBannerMessage.setForeground(WARNING_RED);
        warningBanner.add(lblBannerMessage, BorderLayout.CENTER);
        mainContent.add(warningBanner, BorderLayout.NORTH);

        // 2. CENTER PANEL (Restock Bar + Filter Bar + Table)
        JPanel centerPanel = new JPanel(new BorderLayout(0, 10));
        centerPanel.setOpaque(false);

        // Restock Card
        JPanel restockCard = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        restockCard.setBackground(Color.WHITE);
        restockCard.setBorder(BorderFactory.createTitledBorder(
            new LineBorder(PRIMARY_GREEN, 1, true),
            "Quick Restock / Add Stock",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 12),
            PRIMARY_GREEN
        ));

        JLabel lblSelect = new JLabel("Select Fertilizer:");
        lblSelect.setFont(new Font("Segoe UI", Font.BOLD, 12));
        restockCard.add(lblSelect);

        cmbFertilizer = new JComboBox<>();
        cmbFertilizer.setPreferredSize(new Dimension(320, 30));
        cmbFertilizer.setBackground(Color.WHITE);
        restockCard.add(cmbFertilizer);

        JLabel lblAdd = new JLabel("Bags to Add:");
        lblAdd.setFont(new Font("Segoe UI", Font.BOLD, 12));
        restockCard.add(lblAdd);

        txtAddQuantity = new JTextField(6);
        txtAddQuantity.setPreferredSize(new Dimension(70, 30));
        restockCard.add(txtAddQuantity);

        btnUpdateStock = new JButton("Update Stock");
        btnUpdateStock.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnUpdateStock.setBackground(PRIMARY_GREEN);
        btnUpdateStock.setForeground(Color.WHITE);
        btnUpdateStock.setFocusPainted(false);
        btnUpdateStock.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnUpdateStock.addActionListener(this);
        restockCard.add(btnUpdateStock);

        btnRefresh = new JButton("Refresh");
        btnRefresh.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnRefresh.setBackground(new Color(230, 230, 230));
        btnRefresh.setFocusPainted(false);
        btnRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefresh.addActionListener(this);
        restockCard.add(btnRefresh);

        centerPanel.add(restockCard, BorderLayout.NORTH);

        // Filter Bar + Table
        JPanel tableContainer = new JPanel(new BorderLayout(0, 8));
        tableContainer.setOpaque(false);

        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        filterBar.setOpaque(false);

        JLabel lblFilter = new JLabel("Filter Inventory:");
        lblFilter.setFont(new Font("Segoe UI", Font.BOLD, 12));
        filterBar.add(lblFilter);

        btnFilterAll = new JButton("All Items");
        btnFilterAll.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnFilterAll.setBackground(PRIMARY_GREEN);
        btnFilterAll.setForeground(Color.WHITE);
        btnFilterAll.setFocusPainted(false);
        btnFilterAll.addActionListener(this);
        filterBar.add(btnFilterAll);

        btnFilterLow = new JButton("Low Stock (< 10 bags)");
        btnFilterLow.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnFilterLow.setBackground(Color.WHITE);
        btnFilterLow.setForeground(WARNING_ORANGE);
        btnFilterLow.setFocusPainted(false);
        btnFilterLow.addActionListener(this);
        filterBar.add(btnFilterLow);

        btnFilterOut = new JButton("Out of Stock (0 bags)");
        btnFilterOut.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnFilterOut.setBackground(Color.WHITE);
        btnFilterOut.setForeground(WARNING_RED);
        btnFilterOut.setFocusPainted(false);
        btnFilterOut.addActionListener(this);
        filterBar.add(btnFilterOut);

        tableContainer.add(filterBar, BorderLayout.NORTH);

        // Table
        String[] columnNames = {"ID", "Fertilizer Name", "Type", "Price (Rs.)", "Available Stock (Bags)", "Status"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblStock = new JTable(tableModel);
        tblStock.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblStock.setRowHeight(28);
        tblStock.setSelectionBackground(new Color(200, 230, 201));
        tblStock.setSelectionForeground(Color.BLACK);

        tblStock.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblStock.getTableHeader().setBackground(PRIMARY_GREEN);
        tblStock.getTableHeader().setForeground(Color.WHITE);
        tblStock.getTableHeader().setPreferredSize(new Dimension(0, 32));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tblStock.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tblStock.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        tblStock.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

        tblStock.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                setHorizontalAlignment(JLabel.CENTER);
                setFont(new Font("Segoe UI", Font.BOLD, 12));

                if (value != null) {
                    String status = value.toString();
                    if (status.contains("Out of Stock")) {
                        setForeground(WARNING_RED);
                    } else if (status.contains("Low Stock")) {
                        setForeground(WARNING_ORANGE);
                    } else {
                        setForeground(new Color(46, 125, 50));
                    }
                }
                return c;
            }
        });

        tblStock.getColumnModel().getColumn(0).setPreferredWidth(50);
        tblStock.getColumnModel().getColumn(1).setPreferredWidth(230);
        tblStock.getColumnModel().getColumn(2).setPreferredWidth(120);
        tblStock.getColumnModel().getColumn(3).setPreferredWidth(90);
        tblStock.getColumnModel().getColumn(4).setPreferredWidth(120);
        tblStock.getColumnModel().getColumn(5).setPreferredWidth(130);

        JScrollPane scrollPane = new JScrollPane(tblStock);
        scrollPane.setBorder(new LineBorder(new Color(200, 200, 200), 1));
        tableContainer.add(scrollPane, BorderLayout.CENTER);

        centerPanel.add(tableContainer, BorderLayout.CENTER);
        mainContent.add(centerPanel, BorderLayout.CENTER);

        add(mainContent, BorderLayout.CENTER);

        loadRestockDropdown();
        loadStockTable();
    }

    public void loadRestockDropdown() {
        cmbFertilizer.removeAllItems();

        if (DBConnection.isOnline()) {
            Connection conn = DBConnection.getConnection();
            if (conn != null) {
                try {
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT fertilizer_id, name, quantity FROM fertilizer ORDER BY name ASC");
                    while (rs.next()) {
                        int id = rs.getInt("fertilizer_id");
                        String name = rs.getString("name");
                        int stock = rs.getInt("quantity");
                        cmbFertilizer.addItem(new RestockItem(id, name, stock));
                    }
                    rs.close();
                    stmt.close();
                    conn.close();
                } catch (Exception ex) {
                    System.err.println("DB restock dropdown error: " + ex.getMessage());
                }
            }
        }

        if (cmbFertilizer.getItemCount() == 0) {
            // Standalone Zero-Installation mode
            List<DataStore.FertilizerRecord> list = DataStore.getFertilizers("");
            for (DataStore.FertilizerRecord f : list) {
                cmbFertilizer.addItem(new RestockItem(f.getId(), f.getName(), f.getQuantity()));
            }
        }
    }

    public void loadStockTable() {
        tableModel.setRowCount(0);
        int totalLow = 0;

        if (DBConnection.isOnline()) {
            Connection conn = DBConnection.getConnection();
            if (conn != null) {
                try {
                    String sql;
                    if ("LOW".equals(currentFilter)) {
                        sql = "SELECT * FROM fertilizer WHERE quantity > 0 AND quantity < 10 ORDER BY quantity ASC";
                    } else if ("OUT".equals(currentFilter)) {
                        sql = "SELECT * FROM fertilizer WHERE quantity <= 0 ORDER BY fertilizer_id ASC";
                    } else {
                        sql = "SELECT * FROM fertilizer ORDER BY fertilizer_id ASC";
                    }

                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(sql);

                    while (rs.next()) {
                        int id = rs.getInt("fertilizer_id");
                        String name = rs.getString("name");
                        String type = rs.getString("type");
                        double price = rs.getDouble("price");
                        int qty = rs.getInt("quantity");

                        String status;
                        if (qty <= 0) status = "Out of Stock (0)";
                        else if (qty < 10) status = "Low Stock (" + qty + ")";
                        else status = "In Stock (" + qty + ")";

                        tableModel.addRow(new Object[]{id, name, type, String.format("%.2f", price), qty, status});
                    }
                    rs.close();
                    stmt.close();

                    Statement stmtCount = conn.createStatement();
                    ResultSet rsCount = stmtCount.executeQuery("SELECT COUNT(*) AS c FROM fertilizer WHERE quantity < 10");
                    if (rsCount.next()) totalLow = rsCount.getInt("c");
                    rsCount.close();
                    stmtCount.close();
                    conn.close();

                    updateBanner(totalLow);
                    return;
                } catch (Exception ex) {
                    System.err.println("DB stock table load error: " + ex.getMessage());
                }
            }
        }

        // Standalone Zero-Installation mode
        List<DataStore.FertilizerRecord> list = DataStore.getFertilizers("");
        for (DataStore.FertilizerRecord f : list) {
            int qty = f.getQuantity();

            if ("LOW".equals(currentFilter) && (qty <= 0 || qty >= 10)) continue;
            if ("OUT".equals(currentFilter) && qty > 0) continue;

            String status;
            if (qty <= 0) status = "Out of Stock (0)";
            else if (qty < 10) status = "Low Stock (" + qty + ")";
            else status = "In Stock (" + qty + ")";

            tableModel.addRow(new Object[]{f.getId(), f.getName(), f.getType(), String.format("%.2f", f.getPrice()), qty, status});
        }

        totalLow = DataStore.getLowStockCount();
        updateBanner(totalLow);
    }

    private void updateBanner(int totalLow) {
        if (totalLow > 0) {
            lblBannerMessage.setText("  [Low Stock Warning] " + totalLow + " fertilizer item(s) have less than 10 bags in stock! Please reorder soon.");
            warningBanner.setBackground(new Color(255, 235, 238));
            lblBannerMessage.setForeground(WARNING_RED);
        } else {
            lblBannerMessage.setText("  [Stock Status Normal] All fertilizer inventory levels are healthy (>= 10 bags).");
            warningBanner.setBackground(new Color(232, 245, 233));
            lblBannerMessage.setForeground(PRIMARY_GREEN);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnUpdateStock) {
            updateStock();
        } else if (e.getSource() == btnRefresh) {
            loadRestockDropdown();
            loadStockTable();
        } else if (e.getSource() == btnFilterAll) {
            currentFilter = "ALL";
            highlightFilterButton(btnFilterAll);
            loadStockTable();
        } else if (e.getSource() == btnFilterLow) {
            currentFilter = "LOW";
            highlightFilterButton(btnFilterLow);
            loadStockTable();
        } else if (e.getSource() == btnFilterOut) {
            currentFilter = "OUT";
            highlightFilterButton(btnFilterOut);
            loadStockTable();
        } else if (e.getSource() == btnBack) {
            goBack();
        }
    }

    private void highlightFilterButton(JButton activeBtn) {
        btnFilterAll.setBackground(Color.WHITE);
        btnFilterAll.setForeground(PRIMARY_GREEN);
        btnFilterLow.setBackground(Color.WHITE);
        btnFilterLow.setForeground(WARNING_ORANGE);
        btnFilterOut.setBackground(Color.WHITE);
        btnFilterOut.setForeground(WARNING_RED);

        activeBtn.setBackground(PRIMARY_GREEN);
        activeBtn.setForeground(Color.WHITE);
    }

    private void updateStock() {
        RestockItem item = (RestockItem) cmbFertilizer.getSelectedItem();
        if (item == null) {
            JOptionPane.showMessageDialog(this, "Please select a fertilizer to restock!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String addQtyStr = txtAddQuantity.getText().trim();
        if (addQtyStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter the number of bags to add!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            txtAddQuantity.requestFocus();
            return;
        }

        int addQty;
        try {
            addQty = Integer.parseInt(addQtyStr);
            if (addQty <= 0) {
                JOptionPane.showMessageDialog(this, "Bags to add must be greater than zero!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid whole number for quantity!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (DBConnection.isOnline()) {
            Connection conn = DBConnection.getConnection();
            if (conn != null) {
                try {
                    String sql = "UPDATE fertilizer SET quantity = quantity + ? WHERE fertilizer_id = ?";
                    PreparedStatement pst = conn.prepareStatement(sql);
                    pst.setInt(1, addQty);
                    pst.setInt(2, item.id);
                    pst.executeUpdate();
                    pst.close();
                    conn.close();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error updating stock: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        } else {
            DataStore.updateStock(item.id, addQty);
        }

        int newStock = item.currentStock + addQty;
        JOptionPane.showMessageDialog(this,
            "Stock Updated Successfully!\n" +
            "Fertilizer: " + item.name + "\n" +
            "Added: +" + addQty + " bags\n" +
            "New Total Stock: " + newStock + " bags",
            "Restock Complete", JOptionPane.INFORMATION_MESSAGE);

        txtAddQuantity.setText("");
        loadRestockDropdown();
        loadStockTable();
    }

    private void goBack() {
        if (parentDashboard != null) {
            parentDashboard.loadDashboardStats();
            parentDashboard.setVisible(true);
        }
        this.dispose();
    }
}
