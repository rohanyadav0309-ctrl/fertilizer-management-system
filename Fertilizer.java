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
import java.util.List;

/**
 * Fertilizer.java
 * ---------------
 * Screen 3: Fertilizer Management Screen.
 * Provides Add, View, Update, Delete (CRUD) and Search operations for fertilizers.
 * Supports both MySQL and Standalone Zero-Installation mode.
 */
public class Fertilizer extends JFrame implements ActionListener {

    private Dashboard parentDashboard;

    // Form Input Fields
    private JTextField txtId;
    private JTextField txtName;
    private JComboBox<String> cmbType;
    private JTextField txtPrice;
    private JTextField txtQuantity;

    // Search Fields
    private JTextField txtSearch;
    private JButton btnSearch;
    private JButton btnResetSearch;

    // Action Buttons
    private JButton btnAdd;
    private JButton btnUpdate;
    private JButton btnDelete;
    private JButton btnClear;
    private JButton btnBack;

    // JTable Components
    private JTable tblFertilizer;
    private DefaultTableModel tableModel;

    // Theme Colors
    private static final Color PRIMARY_GREEN = new Color(46, 125, 50);
    private static final Color BG_LIGHT      = new Color(245, 250, 245);
    private static final Color TEXT_DARK     = new Color(33, 33, 33);
    private static final Color DANGER_RED    = new Color(211, 47, 47);

    private static final String[] FERTILIZER_TYPES = {
        "Nitrogenous",
        "Phosphatic",
        "Potassic",
        "Complex (NPK)",
        "Organic",
        "Bio-Fertilizer",
        "Micronutrient"
    };

    public Fertilizer(Dashboard dashboard) {
        this.parentDashboard = dashboard;

        setTitle("Fertilizer Management System - Fertilizer Records");
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

        JLabel lblTitle = new JLabel("Fertilizer Management Module");
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
        JPanel mainContent = new JPanel(new BorderLayout(15, 10));
        mainContent.setOpaque(false);
        mainContent.setBorder(new EmptyBorder(10, 15, 10, 15));

        // LEFT: Form
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);
        leftPanel.setPreferredSize(new Dimension(340, 540));

        JPanel formCard = new JPanel();
        formCard.setLayout(null);
        formCard.setBackground(Color.WHITE);
        formCard.setPreferredSize(new Dimension(340, 390));
        formCard.setBorder(BorderFactory.createTitledBorder(
            new LineBorder(PRIMARY_GREEN, 1, true),
            "Fertilizer Details",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 13),
            PRIMARY_GREEN
        ));

        int y = 30;

        // Fertilizer ID
        JLabel lblId = new JLabel("Fertilizer ID:");
        lblId.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblId.setBounds(20, y, 120, 20);
        formCard.add(lblId);

        txtId = new JTextField();
        txtId.setBounds(20, y + 22, 290, 30);
        txtId.setEditable(false);
        txtId.setBackground(new Color(240, 240, 240));
        formCard.add(txtId);

        y += 60;
        // Name
        JLabel lblName = new JLabel("Fertilizer Name *:");
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblName.setBounds(20, y, 150, 20);
        formCard.add(lblName);

        txtName = new JTextField();
        txtName.setBounds(20, y + 22, 290, 30);
        formCard.add(txtName);

        y += 60;
        // Type
        JLabel lblType = new JLabel("Fertilizer Type *:");
        lblType.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblType.setBounds(20, y, 150, 20);
        formCard.add(lblType);

        cmbType = new JComboBox<>(FERTILIZER_TYPES);
        cmbType.setBounds(20, y + 22, 290, 30);
        cmbType.setBackground(Color.WHITE);
        formCard.add(cmbType);

        y += 60;
        // Price
        JLabel lblPrice = new JLabel("Price per Bag/Unit (Rs.) *:");
        lblPrice.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPrice.setBounds(20, y, 200, 20);
        formCard.add(lblPrice);

        txtPrice = new JTextField();
        txtPrice.setBounds(20, y + 22, 290, 30);
        formCard.add(txtPrice);

        y += 60;
        // Quantity
        JLabel lblQty = new JLabel("Quantity / Stock (Bags) *:");
        lblQty.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblQty.setBounds(20, y, 200, 20);
        formCard.add(lblQty);

        txtQuantity = new JTextField();
        txtQuantity.setBounds(20, y + 22, 290, 30);
        formCard.add(txtQuantity);

        leftPanel.add(formCard);
        leftPanel.add(Box.createVerticalStrut(10));

        // Form Buttons
        JPanel actionButtonsPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        actionButtonsPanel.setBackground(Color.WHITE);
        actionButtonsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        actionButtonsPanel.setPreferredSize(new Dimension(340, 95));

        btnAdd = new JButton("Add");
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnAdd.setBackground(PRIMARY_GREEN);
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);
        btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAdd.addActionListener(this);

        btnUpdate = new JButton("Update");
        btnUpdate.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnUpdate.setBackground(new Color(2, 136, 209));
        btnUpdate.setForeground(Color.WHITE);
        btnUpdate.setFocusPainted(false);
        btnUpdate.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnUpdate.addActionListener(this);

        btnDelete = new JButton("Delete");
        btnDelete.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnDelete.setBackground(DANGER_RED);
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFocusPainted(false);
        btnDelete.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDelete.addActionListener(this);

        btnClear = new JButton("Clear");
        btnClear.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnClear.setBackground(new Color(117, 117, 117));
        btnClear.setForeground(Color.WHITE);
        btnClear.setFocusPainted(false);
        btnClear.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClear.addActionListener(this);

        actionButtonsPanel.add(btnAdd);
        actionButtonsPanel.add(btnUpdate);
        actionButtonsPanel.add(btnDelete);
        actionButtonsPanel.add(btnClear);

        leftPanel.add(actionButtonsPanel);
        mainContent.add(leftPanel, BorderLayout.WEST);

        // RIGHT: Search + Table
        JPanel rightPanel = new JPanel(new BorderLayout(0, 10));
        rightPanel.setOpaque(false);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(220, 220, 220), 1, true),
            new EmptyBorder(5, 10, 5, 10)
        ));

        JLabel lblSearch = new JLabel("Search Fertilizer (Name or ID):");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 12));
        searchPanel.add(lblSearch);

        txtSearch = new JTextField(15);
        txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        searchPanel.add(txtSearch);

        btnSearch = new JButton("Search");
        btnSearch.setBackground(PRIMARY_GREEN);
        btnSearch.setForeground(Color.WHITE);
        btnSearch.setFocusPainted(false);
        btnSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSearch.addActionListener(this);
        searchPanel.add(btnSearch);

        btnResetSearch = new JButton("Show All");
        btnResetSearch.setBackground(new Color(220, 220, 220));
        btnResetSearch.setForeground(TEXT_DARK);
        btnResetSearch.setFocusPainted(false);
        btnResetSearch.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnResetSearch.addActionListener(this);
        searchPanel.add(btnResetSearch);

        rightPanel.add(searchPanel, BorderLayout.NORTH);

        // Table
        String[] columnNames = {"Fertilizer ID", "Name", "Type", "Price (Rs.)", "Quantity (Stock)"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblFertilizer = new JTable(tableModel);
        tblFertilizer.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblFertilizer.setRowHeight(26);
        tblFertilizer.setSelectionBackground(new Color(200, 230, 201));
        tblFertilizer.setSelectionForeground(Color.BLACK);

        tblFertilizer.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblFertilizer.getTableHeader().setBackground(PRIMARY_GREEN);
        tblFertilizer.getTableHeader().setForeground(Color.WHITE);
        tblFertilizer.getTableHeader().setPreferredSize(new Dimension(0, 32));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tblFertilizer.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tblFertilizer.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        tblFertilizer.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);

        tblFertilizer.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = tblFertilizer.getSelectedRow();
                if (selectedRow != -1) {
                    txtId.setText(tableModel.getValueAt(selectedRow, 0).toString());
                    txtName.setText(tableModel.getValueAt(selectedRow, 1).toString());
                    cmbType.setSelectedItem(tableModel.getValueAt(selectedRow, 2).toString());
                    txtPrice.setText(tableModel.getValueAt(selectedRow, 3).toString());
                    txtQuantity.setText(tableModel.getValueAt(selectedRow, 4).toString());
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblFertilizer);
        scrollPane.setBorder(new LineBorder(new Color(200, 200, 200), 1));
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        mainContent.add(rightPanel, BorderLayout.CENTER);
        add(mainContent, BorderLayout.CENTER);

        // Load fertilizers
        loadFertilizers("");
    }

    public void loadFertilizers(String searchQuery) {
        tableModel.setRowCount(0);

        if (DBConnection.isOnline()) {
            Connection conn = DBConnection.getConnection();
            if (conn != null) {
                try {
                    String sql;
                    PreparedStatement pst;
                    if (searchQuery == null || searchQuery.trim().isEmpty()) {
                        sql = "SELECT * FROM fertilizer ORDER BY fertilizer_id ASC";
                        pst = conn.prepareStatement(sql);
                    } else {
                        sql = "SELECT * FROM fertilizer WHERE name LIKE ? OR fertilizer_id LIKE ? ORDER BY fertilizer_id ASC";
                        pst = conn.prepareStatement(sql);
                        pst.setString(1, "%" + searchQuery.trim() + "%");
                        pst.setString(2, "%" + searchQuery.trim() + "%");
                    }

                    ResultSet rs = pst.executeQuery();
                    while (rs.next()) {
                        int id = rs.getInt("fertilizer_id");
                        String name = rs.getString("name");
                        String type = rs.getString("type");
                        double price = rs.getDouble("price");
                        int qty = rs.getInt("quantity");
                        tableModel.addRow(new Object[]{id, name, type, String.format("%.2f", price), qty});
                    }
                    rs.close();
                    pst.close();
                    conn.close();
                    return;
                } catch (Exception ex) {
                    System.err.println("DB load error: " + ex.getMessage());
                }
            }
        }

        // Standalone Zero-Installation mode
        List<DataStore.FertilizerRecord> list = DataStore.getFertilizers(searchQuery);
        for (DataStore.FertilizerRecord f : list) {
            tableModel.addRow(new Object[]{f.getId(), f.getName(), f.getType(), String.format("%.2f", f.getPrice()), f.getQuantity()});
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnAdd) {
            addFertilizer();
        } else if (e.getSource() == btnUpdate) {
            updateFertilizer();
        } else if (e.getSource() == btnDelete) {
            deleteFertilizer();
        } else if (e.getSource() == btnClear) {
            clearFields();
        } else if (e.getSource() == btnSearch) {
            loadFertilizers(txtSearch.getText().trim());
        } else if (e.getSource() == btnResetSearch) {
            txtSearch.setText("");
            loadFertilizers("");
        } else if (e.getSource() == btnBack) {
            goBack();
        }
    }

    private void addFertilizer() {
        String name = txtName.getText().trim();
        String type = (String) cmbType.getSelectedItem();
        String priceStr = txtPrice.getText().trim();
        String qtyStr = txtQuantity.getText().trim();

        if (name.isEmpty() || priceStr.isEmpty() || qtyStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields (*)", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double price;
        int quantity;
        try {
            price = Double.parseDouble(priceStr);
            if (price <= 0) {
                JOptionPane.showMessageDialog(this, "Price must be a positive number!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid Price! Please enter a valid decimal number.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            quantity = Integer.parseInt(qtyStr);
            if (quantity < 0) {
                JOptionPane.showMessageDialog(this, "Quantity cannot be negative!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid Quantity! Please enter a whole integer.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (DBConnection.isOnline()) {
            Connection conn = DBConnection.getConnection();
            if (conn != null) {
                try {
                    String sql = "INSERT INTO fertilizer (name, type, price, quantity) VALUES (?, ?, ?, ?)";
                    PreparedStatement pst = conn.prepareStatement(sql);
                    pst.setString(1, name);
                    pst.setString(2, type);
                    pst.setDouble(3, price);
                    pst.setInt(4, quantity);
                    pst.executeUpdate();
                    pst.close();
                    conn.close();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error adding fertilizer: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        } else {
            DataStore.addFertilizer(name, type, price, quantity);
        }

        JOptionPane.showMessageDialog(this, "Fertilizer added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        clearFields();
        loadFertilizers("");
    }

    private void updateFertilizer() {
        String idStr = txtId.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a fertilizer from the table to update!", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(idStr);
        String name = txtName.getText().trim();
        String type = (String) cmbType.getSelectedItem();
        String priceStr = txtPrice.getText().trim();
        String qtyStr = txtQuantity.getText().trim();

        if (name.isEmpty() || priceStr.isEmpty() || qtyStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields (*)", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double price;
        int quantity;
        try {
            price = Double.parseDouble(priceStr);
            quantity = Integer.parseInt(qtyStr);
            if (price <= 0 || quantity < 0) {
                JOptionPane.showMessageDialog(this, "Price must be > 0 and Quantity cannot be negative!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid number entered for price or quantity!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (DBConnection.isOnline()) {
            Connection conn = DBConnection.getConnection();
            if (conn != null) {
                try {
                    String sql = "UPDATE fertilizer SET name = ?, type = ?, price = ?, quantity = ? WHERE fertilizer_id = ?";
                    PreparedStatement pst = conn.prepareStatement(sql);
                    pst.setString(1, name);
                    pst.setString(2, type);
                    pst.setDouble(3, price);
                    pst.setInt(4, quantity);
                    pst.setInt(5, id);
                    pst.executeUpdate();
                    pst.close();
                    conn.close();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error updating fertilizer: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        } else {
            DataStore.updateFertilizer(id, name, type, price, quantity);
        }

        JOptionPane.showMessageDialog(this, "Fertilizer record updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        clearFields();
        loadFertilizers("");
    }

    private void deleteFertilizer() {
        String idStr = txtId.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a fertilizer from the table to delete!", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(idStr);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete Fertilizer ID #" + id + " (" + txtName.getText() + ")?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        if (DBConnection.isOnline()) {
            Connection conn = DBConnection.getConnection();
            if (conn != null) {
                try {
                    String sql = "DELETE FROM fertilizer WHERE fertilizer_id = ?";
                    PreparedStatement pst = conn.prepareStatement(sql);
                    pst.setInt(1, id);
                    pst.executeUpdate();
                    pst.close();
                    conn.close();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error deleting fertilizer: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        } else {
            DataStore.deleteFertilizer(id);
        }

        JOptionPane.showMessageDialog(this, "Fertilizer deleted successfully!", "Deleted", JOptionPane.INFORMATION_MESSAGE);
        clearFields();
        loadFertilizers("");
    }

    private void clearFields() {
        txtId.setText("");
        txtName.setText("");
        cmbType.setSelectedIndex(0);
        txtPrice.setText("");
        txtQuantity.setText("");
        tblFertilizer.clearSelection();
    }

    private void goBack() {
        if (parentDashboard != null) {
            parentDashboard.loadDashboardStats();
            parentDashboard.setVisible(true);
        }
        this.dispose();
    }
}
