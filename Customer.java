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
 * Customer.java
 * -------------
 * Screen 4: Customer Management Screen.
 * Provides Add, View, Update, Delete (CRUD) and Search operations for customers/farmers.
 * Supports both MySQL and Standalone Zero-Installation mode.
 */
public class Customer extends JFrame implements ActionListener {

    private Dashboard parentDashboard;

    // Form Fields
    private JTextField txtId;
    private JTextField txtName;
    private JTextField txtMobile;
    private JTextArea txtAddress;

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

    // Table
    private JTable tblCustomer;
    private DefaultTableModel tableModel;

    // Theme Colors
    private static final Color PRIMARY_GREEN = new Color(46, 125, 50);
    private static final Color BG_LIGHT      = new Color(245, 250, 245);
    private static final Color TEXT_DARK     = new Color(33, 33, 33);
    private static final Color DANGER_RED    = new Color(211, 47, 47);

    public Customer(Dashboard dashboard) {
        this.parentDashboard = dashboard;

        setTitle("Fertilizer Management System - Customer Management");
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

        JLabel lblTitle = new JLabel("Customer / Farmer Management Module");
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
        leftPanel.setPreferredSize(new Dimension(340, 540));

        JPanel formCard = new JPanel();
        formCard.setLayout(null);
        formCard.setBackground(Color.WHITE);
        formCard.setPreferredSize(new Dimension(340, 400));
        formCard.setBorder(BorderFactory.createTitledBorder(
            new LineBorder(PRIMARY_GREEN, 1, true),
            "Customer Details",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 13),
            PRIMARY_GREEN
        ));

        int y = 25;

        // Customer ID
        JLabel lblId = new JLabel("Customer ID:");
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
        JLabel lblName = new JLabel("Customer Name *:");
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblName.setBounds(20, y, 150, 20);
        formCard.add(lblName);

        txtName = new JTextField();
        txtName.setBounds(20, y + 22, 290, 30);
        formCard.add(txtName);

        y += 60;
        // Mobile
        JLabel lblMobile = new JLabel("Mobile Number (10 Digits) *:");
        lblMobile.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblMobile.setBounds(20, y, 200, 20);
        formCard.add(lblMobile);

        txtMobile = new JTextField();
        txtMobile.setBounds(20, y + 22, 290, 30);
        formCard.add(txtMobile);

        y += 60;
        // Address
        JLabel lblAddress = new JLabel("Address / Village *:");
        lblAddress.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblAddress.setBounds(20, y, 150, 20);
        formCard.add(lblAddress);

        txtAddress = new JTextArea();
        txtAddress.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtAddress.setLineWrap(true);
        txtAddress.setWrapStyleWord(true);
        JScrollPane addrScroll = new JScrollPane(txtAddress);
        addrScroll.setBounds(20, y + 22, 290, 80);
        formCard.add(addrScroll);

        leftPanel.add(formCard);
        leftPanel.add(Box.createVerticalStrut(10));

        // Action Buttons
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

        JLabel lblSearch = new JLabel("Search Customer (Name or Mobile):");
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
        String[] columnNames = {"Customer ID", "Customer Name", "Mobile Number", "Address"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblCustomer = new JTable(tableModel);
        tblCustomer.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tblCustomer.setRowHeight(26);
        tblCustomer.setSelectionBackground(new Color(200, 230, 201));
        tblCustomer.setSelectionForeground(Color.BLACK);

        tblCustomer.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tblCustomer.getTableHeader().setBackground(PRIMARY_GREEN);
        tblCustomer.getTableHeader().setForeground(Color.WHITE);
        tblCustomer.getTableHeader().setPreferredSize(new Dimension(0, 32));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        tblCustomer.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tblCustomer.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);

        tblCustomer.getColumnModel().getColumn(0).setPreferredWidth(80);
        tblCustomer.getColumnModel().getColumn(1).setPreferredWidth(160);
        tblCustomer.getColumnModel().getColumn(2).setPreferredWidth(110);
        tblCustomer.getColumnModel().getColumn(3).setPreferredWidth(230);

        tblCustomer.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int selectedRow = tblCustomer.getSelectedRow();
                if (selectedRow != -1) {
                    txtId.setText(tableModel.getValueAt(selectedRow, 0).toString());
                    txtName.setText(tableModel.getValueAt(selectedRow, 1).toString());
                    txtMobile.setText(tableModel.getValueAt(selectedRow, 2).toString());
                    txtAddress.setText(tableModel.getValueAt(selectedRow, 3).toString());
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(tblCustomer);
        scrollPane.setBorder(new LineBorder(new Color(200, 200, 200), 1));
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        mainContent.add(rightPanel, BorderLayout.CENTER);
        add(mainContent, BorderLayout.CENTER);

        loadCustomers("");
    }

    public void loadCustomers(String searchQuery) {
        tableModel.setRowCount(0);

        if (DBConnection.isOnline()) {
            Connection conn = DBConnection.getConnection();
            if (conn != null) {
                try {
                    String sql;
                    PreparedStatement pst;
                    if (searchQuery == null || searchQuery.trim().isEmpty()) {
                        sql = "SELECT * FROM customer ORDER BY customer_id ASC";
                        pst = conn.prepareStatement(sql);
                    } else {
                        sql = "SELECT * FROM customer WHERE customer_name LIKE ? OR mobile_number LIKE ? ORDER BY customer_id ASC";
                        pst = conn.prepareStatement(sql);
                        pst.setString(1, "%" + searchQuery.trim() + "%");
                        pst.setString(2, "%" + searchQuery.trim() + "%");
                    }

                    ResultSet rs = pst.executeQuery();
                    while (rs.next()) {
                        int id = rs.getInt("customer_id");
                        String name = rs.getString("customer_name");
                        String mobile = rs.getString("mobile_number");
                        String address = rs.getString("address");
                        tableModel.addRow(new Object[]{id, name, mobile, address});
                    }
                    rs.close();
                    pst.close();
                    conn.close();
                    return;
                } catch (Exception ex) {
                    System.err.println("DB load customers error: " + ex.getMessage());
                }
            }
        }

        // Standalone Zero-Installation mode
        List<DataStore.CustomerRecord> list = DataStore.getCustomers(searchQuery);
        for (DataStore.CustomerRecord c : list) {
            tableModel.addRow(new Object[]{c.getId(), c.getName(), c.getMobile(), c.getAddress()});
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnAdd) {
            addCustomer();
        } else if (e.getSource() == btnUpdate) {
            updateCustomer();
        } else if (e.getSource() == btnDelete) {
            deleteCustomer();
        } else if (e.getSource() == btnClear) {
            clearFields();
        } else if (e.getSource() == btnSearch) {
            loadCustomers(txtSearch.getText().trim());
        } else if (e.getSource() == btnResetSearch) {
            txtSearch.setText("");
            loadCustomers("");
        } else if (e.getSource() == btnBack) {
            goBack();
        }
    }

    private void addCustomer() {
        String name = txtName.getText().trim();
        String mobile = txtMobile.getText().trim();
        String address = txtAddress.getText().trim();

        if (name.isEmpty() || mobile.isEmpty() || address.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all customer fields!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!mobile.matches("^[0-9]{10}$")) {
            JOptionPane.showMessageDialog(this, "Mobile number must be exactly 10 digits!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (DBConnection.isOnline()) {
            Connection conn = DBConnection.getConnection();
            if (conn != null) {
                try {
                    String sql = "INSERT INTO customer (customer_name, mobile_number, address) VALUES (?, ?, ?)";
                    PreparedStatement pst = conn.prepareStatement(sql);
                    pst.setString(1, name);
                    pst.setString(2, mobile);
                    pst.setString(3, address);
                    pst.executeUpdate();
                    pst.close();
                    conn.close();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error registering customer: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        } else {
            DataStore.addCustomer(name, mobile, address);
        }

        JOptionPane.showMessageDialog(this, "Customer registered successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        clearFields();
        loadCustomers("");
    }

    private void updateCustomer() {
        String idStr = txtId.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a customer from the table to update!", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(idStr);
        String name = txtName.getText().trim();
        String mobile = txtMobile.getText().trim();
        String address = txtAddress.getText().trim();

        if (name.isEmpty() || mobile.isEmpty() || address.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all customer fields!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!mobile.matches("^[0-9]{10}$")) {
            JOptionPane.showMessageDialog(this, "Mobile number must be exactly 10 digits!", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (DBConnection.isOnline()) {
            Connection conn = DBConnection.getConnection();
            if (conn != null) {
                try {
                    String sql = "UPDATE customer SET customer_name = ?, mobile_number = ?, address = ? WHERE customer_id = ?";
                    PreparedStatement pst = conn.prepareStatement(sql);
                    pst.setString(1, name);
                    pst.setString(2, mobile);
                    pst.setString(3, address);
                    pst.setInt(4, id);
                    pst.executeUpdate();
                    pst.close();
                    conn.close();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error updating customer: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        } else {
            DataStore.updateCustomer(id, name, mobile, address);
        }

        JOptionPane.showMessageDialog(this, "Customer details updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        clearFields();
        loadCustomers("");
    }

    private void deleteCustomer() {
        String idStr = txtId.getText().trim();
        if (idStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a customer from the table to delete!", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = Integer.parseInt(idStr);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete Customer ID #" + id + " (" + txtName.getText() + ")?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        if (DBConnection.isOnline()) {
            Connection conn = DBConnection.getConnection();
            if (conn != null) {
                try {
                    String sql = "DELETE FROM customer WHERE customer_id = ?";
                    PreparedStatement pst = conn.prepareStatement(sql);
                    pst.setInt(1, id);
                    pst.executeUpdate();
                    pst.close();
                    conn.close();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error deleting customer: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        } else {
            DataStore.deleteCustomer(id);
        }

        JOptionPane.showMessageDialog(this, "Customer deleted successfully!", "Deleted", JOptionPane.INFORMATION_MESSAGE);
        clearFields();
        loadCustomers("");
    }

    private void clearFields() {
        txtId.setText("");
        txtName.setText("");
        txtMobile.setText("");
        txtAddress.setText("");
        tblCustomer.clearSelection();
    }

    private void goBack() {
        if (parentDashboard != null) {
            parentDashboard.loadDashboardStats();
            parentDashboard.setVisible(true);
        }
        this.dispose();
    }
}
