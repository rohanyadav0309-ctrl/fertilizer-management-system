import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
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
 * Sales.java
 * ----------
 * Screen 5: Sales Management & Billing Screen.
 * Handles customer sales transactions:
 * - Auto-fetches price & available stock
 * - Auto-calculates total price
 * - Deducts sold quantity from fertilizer stock
 * - Records sale transaction
 * - Displays sales history
 * Supports both MySQL and Standalone Zero-Installation mode.
 */
public class Sales extends JFrame implements ActionListener {

    private Dashboard parentDashboard;

    static class FertilizerItem {
        int id;
        String name;
        double price;
        int stock;

        public FertilizerItem(int id, String name, double price, int stock) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.stock = stock;
        }

        @Override
        public String toString() {
            return id + " - " + name + " (Stock: " + stock + " | Rs. " + String.format("%.2f", price) + ")";
        }
    }

    private JComboBox<FertilizerItem> cmbFertilizer;
    private JTextField txtUnitPrice;
    private JTextField txtAvailableStock;
    private JComboBox<String> cmbCustomer;
    private JTextField txtCustomerName;
    private JTextField txtQuantity;
    private JTextField txtTotalPrice;

    private JButton btnCalculate;
    private JButton btnCompleteSale;
    private JButton btnClear;
    private JButton btnBack;

    private JTable tblSales;
    private DefaultTableModel tableModel;

    private static final Color PRIMARY_GREEN = new Color(46, 125, 50);
    private static final Color BG_LIGHT      = new Color(245, 250, 245);

    public Sales(Dashboard dashboard) {
        this.parentDashboard = dashboard;

        setTitle("Fertilizer Management System - Sales & Billing");
        setSize(1040, 700);
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

        JLabel lblTitle = new JLabel("Sales & Billing Module");
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

        // ------------------ MAIN CONTENT ------------------
        JPanel mainContent = new JPanel(new BorderLayout(15, 10));
        mainContent.setOpaque(false);
        mainContent.setBorder(new EmptyBorder(10, 15, 10, 15));

        // LEFT: Form
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);
        leftPanel.setPreferredSize(new Dimension(380, 560));

        JPanel formCard = new JPanel();
        formCard.setLayout(null);
        formCard.setBackground(Color.WHITE);
        formCard.setPreferredSize(new Dimension(380, 480));
        formCard.setBorder(BorderFactory.createTitledBorder(
            new LineBorder(PRIMARY_GREEN, 1, true),
            "New Sale Transaction",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 13),
            PRIMARY_GREEN
        ));

        int y = 25;

        // Fertilizer Dropdown
        JLabel lblFert = new JLabel("Select Fertilizer *:");
        lblFert.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblFert.setBounds(20, y, 150, 20);
        formCard.add(lblFert);

        cmbFertilizer = new JComboBox<>();
        cmbFertilizer.setBounds(20, y + 22, 335, 30);
        cmbFertilizer.setBackground(Color.WHITE);
        cmbFertilizer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onFertilizerSelected();
            }
        });
        formCard.add(cmbFertilizer);

        y += 58;
        // Price and Stock
        JLabel lblPrice = new JLabel("Unit Price (Rs.):");
        lblPrice.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPrice.setBounds(20, y, 140, 20);
        formCard.add(lblPrice);

        JLabel lblStock = new JLabel("Stock Available:");
        lblStock.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblStock.setBounds(195, y, 140, 20);
        formCard.add(lblStock);

        txtUnitPrice = new JTextField();
        txtUnitPrice.setBounds(20, y + 22, 160, 30);
        txtUnitPrice.setEditable(false);
        txtUnitPrice.setBackground(new Color(245, 245, 245));
        formCard.add(txtUnitPrice);

        txtAvailableStock = new JTextField();
        txtAvailableStock.setBounds(195, y + 22, 160, 30);
        txtAvailableStock.setEditable(false);
        txtAvailableStock.setBackground(new Color(245, 245, 245));
        formCard.add(txtAvailableStock);

        y += 58;
        // Customer
        JLabel lblCust = new JLabel("Customer Name *:");
        lblCust.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblCust.setBounds(20, y, 150, 20);
        formCard.add(lblCust);

        cmbCustomer = new JComboBox<>();
        cmbCustomer.setBounds(20, y + 22, 335, 30);
        cmbCustomer.setBackground(Color.WHITE);
        cmbCustomer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object item = cmbCustomer.getSelectedItem();
                if (item != null && !item.equals("-- Select Registered Customer or Type Below --")) {
                    txtCustomerName.setText(item.toString());
                }
            }
        });
        formCard.add(cmbCustomer);

        y += 58;
        JLabel lblOrType = new JLabel("Or enter Customer Name directly:");
        lblOrType.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblOrType.setBounds(20, y, 250, 18);
        formCard.add(lblOrType);

        txtCustomerName = new JTextField();
        txtCustomerName.setBounds(20, y + 20, 335, 30);
        formCard.add(txtCustomerName);

        y += 58;
        // Quantity
        JLabel lblQty = new JLabel("Sale Quantity (Bags/Units) *:");
        lblQty.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblQty.setBounds(20, y, 220, 20);
        formCard.add(lblQty);

        txtQuantity = new JTextField();
        txtQuantity.setBounds(20, y + 22, 210, 32);
        txtQuantity.setFont(new Font("Segoe UI", Font.BOLD, 13));
        txtQuantity.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { calculateTotal(); }
            @Override
            public void removeUpdate(DocumentEvent e) { calculateTotal(); }
            @Override
            public void changedUpdate(DocumentEvent e) { calculateTotal(); }
        });
        formCard.add(txtQuantity);

        btnCalculate = new JButton("Calculate");
        btnCalculate.setBounds(240, y + 22, 115, 32);
        btnCalculate.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnCalculate.setBackground(new Color(230, 230, 230));
        btnCalculate.setFocusPainted(false);
        btnCalculate.addActionListener(this);
        formCard.add(btnCalculate);

        y += 62;
        // Total
        JLabel lblTotal = new JLabel("Total Price (Rs.):");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblTotal.setForeground(PRIMARY_GREEN);
        lblTotal.setBounds(20, y, 160, 20);
        formCard.add(lblTotal);

        txtTotalPrice = new JTextField();
        txtTotalPrice.setBounds(20, y + 22, 335, 34);
        txtTotalPrice.setFont(new Font("Segoe UI", Font.BOLD, 16));
        txtTotalPrice.setForeground(PRIMARY_GREEN);
        txtTotalPrice.setEditable(false);
        txtTotalPrice.setBackground(new Color(238, 247, 238));
        formCard.add(txtTotalPrice);

        leftPanel.add(formCard);
        leftPanel.add(Box.createVerticalStrut(10));

        // Action Buttons
        JPanel actionPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        actionPanel.setBackground(Color.WHITE);
        actionPanel.setBorder(new EmptyBorder(8, 10, 8, 10));
        actionPanel.setPreferredSize(new Dimension(380, 50));

        btnCompleteSale = new JButton("Complete Sale");
        btnCompleteSale.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCompleteSale.setBackground(PRIMARY_GREEN);
        btnCompleteSale.setForeground(Color.WHITE);
        btnCompleteSale.setFocusPainted(false);
        btnCompleteSale.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCompleteSale.addActionListener(this);

        btnClear = new JButton("Clear Form");
        btnClear.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnClear.setBackground(new Color(117, 117, 117));
        btnClear.setForeground(Color.WHITE);
        btnClear.setFocusPainted(false);
        btnClear.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClear.addActionListener(this);

        actionPanel.add(btnCompleteSale);
        actionPanel.add(btnClear);
        leftPanel.add(actionPanel);

        mainContent.add(leftPanel, BorderLayout.WEST);

        // RIGHT: Sales Table
        JPanel rightPanel = new JPanel(new BorderLayout(0, 10));
        rightPanel.setOpaque(false);

        JLabel lblTableHeading = new JLabel("Sales & Billing Records History");
        lblTableHeading.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTableHeading.setForeground(PRIMARY_GREEN);
        rightPanel.add(lblTableHeading, BorderLayout.NORTH);

        String[] columnNames = {"Sale ID", "Fertilizer", "Customer Name", "Qty", "Total (Rs.)", "Sale Date"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblSales = new JTable(tableModel);
        tblSales.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblSales.setRowHeight(26);
        tblSales.setSelectionBackground(new Color(200, 230, 201));
        tblSales.setSelectionForeground(Color.BLACK);

        tblSales.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblSales.getTableHeader().setBackground(PRIMARY_GREEN);
        tblSales.getTableHeader().setForeground(Color.WHITE);
        tblSales.getTableHeader().setPreferredSize(new Dimension(0, 32));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tblSales.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tblSales.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        tblSales.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

        tblSales.getColumnModel().getColumn(0).setPreferredWidth(60);
        tblSales.getColumnModel().getColumn(1).setPreferredWidth(170);
        tblSales.getColumnModel().getColumn(2).setPreferredWidth(130);
        tblSales.getColumnModel().getColumn(3).setPreferredWidth(50);
        tblSales.getColumnModel().getColumn(4).setPreferredWidth(90);
        tblSales.getColumnModel().getColumn(5).setPreferredWidth(140);

        JScrollPane scrollPane = new JScrollPane(tblSales);
        scrollPane.setBorder(new LineBorder(new Color(200, 200, 200), 1));
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        mainContent.add(rightPanel, BorderLayout.CENTER);
        add(mainContent, BorderLayout.CENTER);

        loadFertilizerOptions();
        loadCustomerOptions();
        loadSalesHistory();
    }

    public void loadFertilizerOptions() {
        cmbFertilizer.removeAllItems();

        if (DBConnection.isOnline()) {
            Connection conn = DBConnection.getConnection();
            if (conn != null) {
                try {
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT fertilizer_id, name, price, quantity FROM fertilizer ORDER BY name ASC");
                    while (rs.next()) {
                        int id = rs.getInt("fertilizer_id");
                        String name = rs.getString("name");
                        double price = rs.getDouble("price");
                        int stock = rs.getInt("quantity");
                        cmbFertilizer.addItem(new FertilizerItem(id, name, price, stock));
                    }
                    rs.close();
                    stmt.close();
                    conn.close();
                } catch (Exception ex) {
                    System.err.println("DB load fert options error: " + ex.getMessage());
                }
            }
        }

        if (cmbFertilizer.getItemCount() == 0) {
            // Standalone Zero-Installation mode
            List<DataStore.FertilizerRecord> list = DataStore.getFertilizers("");
            for (DataStore.FertilizerRecord f : list) {
                cmbFertilizer.addItem(new FertilizerItem(f.getId(), f.getName(), f.getPrice(), f.getQuantity()));
            }
        }

        if (cmbFertilizer.getItemCount() > 0) {
            onFertilizerSelected();
        }
    }

    public void loadCustomerOptions() {
        cmbCustomer.removeAllItems();
        cmbCustomer.addItem("-- Select Registered Customer or Type Below --");

        if (DBConnection.isOnline()) {
            Connection conn = DBConnection.getConnection();
            if (conn != null) {
                try {
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT customer_name FROM customer ORDER BY customer_name ASC");
                    while (rs.next()) {
                        cmbCustomer.addItem(rs.getString("customer_name"));
                    }
                    rs.close();
                    stmt.close();
                    conn.close();
                } catch (Exception ex) {
                    System.err.println("DB load cust options error: " + ex.getMessage());
                }
            }
        }

        if (cmbCustomer.getItemCount() <= 1) {
            // Standalone Zero-Installation mode
            List<DataStore.CustomerRecord> list = DataStore.getCustomers("");
            for (DataStore.CustomerRecord c : list) {
                cmbCustomer.addItem(c.getName());
            }
        }
    }

    private void onFertilizerSelected() {
        FertilizerItem item = (FertilizerItem) cmbFertilizer.getSelectedItem();
        if (item != null) {
            txtUnitPrice.setText(String.format("%.2f", item.price));
            txtAvailableStock.setText(String.valueOf(item.stock));
            calculateTotal();
        }
    }

    private void calculateTotal() {
        FertilizerItem item = (FertilizerItem) cmbFertilizer.getSelectedItem();
        if (item == null) {
            txtTotalPrice.setText("0.00");
            return;
        }

        String qtyText = txtQuantity.getText().trim();
        if (qtyText.isEmpty()) {
            txtTotalPrice.setText("0.00");
            return;
        }

        try {
            int qty = Integer.parseInt(qtyText);
            if (qty <= 0) {
                txtTotalPrice.setText("0.00");
                return;
            }
            double total = item.price * qty;
            txtTotalPrice.setText(String.format("%.2f", total));
        } catch (NumberFormatException ignored) {
            txtTotalPrice.setText("0.00");
        }
    }

    public void loadSalesHistory() {
        tableModel.setRowCount(0);

        if (DBConnection.isOnline()) {
            Connection conn = DBConnection.getConnection();
            if (conn != null) {
                try {
                    String sql = "SELECT s.sale_id, f.name AS fertilizer_name, s.customer_name, s.quantity, s.total_price, s.sale_date " +
                                 "FROM sales s " +
                                 "JOIN fertilizer f ON s.fertilizer_id = f.fertilizer_id " +
                                 "ORDER BY s.sale_id DESC";

                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(sql);

                    while (rs.next()) {
                        int saleId = rs.getInt("sale_id");
                        String fertName = rs.getString("fertilizer_name");
                        String custName = rs.getString("customer_name");
                        int qty = rs.getInt("quantity");
                        double total = rs.getDouble("total_price");
                        String date = rs.getString("sale_date");
                        tableModel.addRow(new Object[]{saleId, fertName, custName, qty, String.format("%.2f", total), date});
                    }

                    rs.close();
                    stmt.close();
                    conn.close();
                    return;
                } catch (Exception ex) {
                    System.err.println("DB load sales error: " + ex.getMessage());
                }
            }
        }

        // Standalone Zero-Installation mode
        List<DataStore.SaleRecord> list = DataStore.getSales();
        for (DataStore.SaleRecord s : list) {
            tableModel.addRow(new Object[]{s.getId(), s.getFertilizerName(), s.getCustomerName(), s.getQuantity(), String.format("%.2f", s.getTotalPrice()), s.getDate()});
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnCalculate) {
            calculateTotal();
        } else if (e.getSource() == btnCompleteSale) {
            processSale();
        } else if (e.getSource() == btnClear) {
            clearForm();
        } else if (e.getSource() == btnBack) {
            goBack();
        }
    }

    private void processSale() {
        FertilizerItem item = (FertilizerItem) cmbFertilizer.getSelectedItem();
        if (item == null) {
            JOptionPane.showMessageDialog(this, "Please select a fertilizer!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String customerName = txtCustomerName.getText().trim();
        if (customerName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter or select a customer name!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            txtCustomerName.requestFocus();
            return;
        }

        String qtyText = txtQuantity.getText().trim();
        if (qtyText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter sale quantity!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            txtQuantity.requestFocus();
            return;
        }

        int qty;
        try {
            qty = Integer.parseInt(qtyText);
            if (qty <= 0) {
                JOptionPane.showMessageDialog(this, "Sale quantity must be greater than zero!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid whole number for quantity!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (qty > item.stock) {
            JOptionPane.showMessageDialog(this,
                "Insufficient Stock Available!\n" +
                "Requested: " + qty + " bags\n" +
                "Available Stock: " + item.stock + " bags.",
                "Out of Stock Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double totalPrice = item.price * qty;
        int newSaleId = 0;

        if (DBConnection.isOnline()) {
            Connection conn = DBConnection.getConnection();
            if (conn != null) {
                try {
                    conn.setAutoCommit(false);

                    String insertSql = "INSERT INTO sales (fertilizer_id, customer_name, quantity, total_price) VALUES (?, ?, ?, ?)";
                    PreparedStatement pstSale = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
                    pstSale.setInt(1, item.id);
                    pstSale.setString(2, customerName);
                    pstSale.setInt(3, qty);
                    pstSale.setDouble(4, totalPrice);
                    pstSale.executeUpdate();

                    ResultSet generatedKeys = pstSale.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        newSaleId = generatedKeys.getInt(1);
                    }
                    generatedKeys.close();
                    pstSale.close();

                    String updateStockSql = "UPDATE fertilizer SET quantity = quantity - ? WHERE fertilizer_id = ?";
                    PreparedStatement pstStock = conn.prepareStatement(updateStockSql);
                    pstStock.setInt(1, qty);
                    pstStock.setInt(2, item.id);
                    pstStock.executeUpdate();
                    pstStock.close();

                    conn.commit();
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (Exception ex) {
                    try { conn.rollback(); } catch (Exception ignored) {}
                    JOptionPane.showMessageDialog(this, "Failed to complete sale: " + ex.getMessage(), "Transaction Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        } else {
            // Standalone Zero-Installation mode
            newSaleId = DataStore.processSale(item.id, customerName, qty, totalPrice);
        }

        String receipt = "=====================================\n" +
                         "       FERTILIZER SALES RECEIPT      \n" +
                         "=====================================\n" +
                         "Receipt ID    : #" + newSaleId + "\n" +
                         "Customer      : " + customerName + "\n" +
                         "Fertilizer    : " + item.name + "\n" +
                         "Unit Price    : Rs. " + String.format("%.2f", item.price) + "\n" +
                         "Quantity Sold : " + qty + " bags\n" +
                         "-------------------------------------\n" +
                         "TOTAL AMOUNT  : Rs. " + String.format("%.2f", totalPrice) + "\n" +
                         "=====================================\n" +
                         "Stock updated successfully in inventory!";

        JOptionPane.showMessageDialog(this, receipt, "Sale Completed Successfully", JOptionPane.INFORMATION_MESSAGE);

        clearForm();
        loadFertilizerOptions();
        loadSalesHistory();
    }

    private void clearForm() {
        txtCustomerName.setText("");
        txtQuantity.setText("");
        txtTotalPrice.setText("0.00");
        if (cmbCustomer.getItemCount() > 0) {
            cmbCustomer.setSelectedIndex(0);
        }
    }

    private void goBack() {
        if (parentDashboard != null) {
            parentDashboard.loadDashboardStats();
            parentDashboard.setVisible(true);
        }
        this.dispose();
    }
}
