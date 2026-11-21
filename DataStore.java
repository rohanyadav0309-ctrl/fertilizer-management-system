import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * DataStore.java
 * --------------
 * Standalone In-Memory Data Store.
 * 
 * Allows the entire Fertilizer Management System to run seamlessly from the
 * terminal WITHOUT requiring any MySQL installation, external drivers, or setup.
 * Pre-loaded with full sample data matching database.sql.
 * 
 * Demonstrates Core Java concepts:
 * - ArrayList
 * - Object-Oriented Modeling (Encapsulation, Classes & Objects)
 * - CRUD operations in memory
 */
public class DataStore {

    // ------------------ DATA MODELS ------------------

    public static class FertilizerRecord {
        private int id;
        private String name;
        private String type;
        private double price;
        private int quantity;

        public FertilizerRecord(int id, String name, String type, double price, int quantity) {
            this.id = id;
            this.name = name;
            this.type = type;
            this.price = price;
            this.quantity = quantity;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }
        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }

    public static class CustomerRecord {
        private int id;
        private String name;
        private String mobile;
        private String address;

        public CustomerRecord(int id, String name, String mobile, String address) {
            this.id = id;
            this.name = name;
            this.mobile = mobile;
            this.address = address;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getMobile() { return mobile; }
        public void setMobile(String mobile) { this.mobile = mobile; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
    }

    public static class SaleRecord {
        private int id;
        private int fertilizerId;
        private String fertilizerName;
        private String customerName;
        private int quantity;
        private double totalPrice;
        private String date;

        public SaleRecord(int id, int fertilizerId, String fertilizerName, String customerName, int quantity, double totalPrice, String date) {
            this.id = id;
            this.fertilizerId = fertilizerId;
            this.fertilizerName = fertilizerName;
            this.customerName = customerName;
            this.quantity = quantity;
            this.totalPrice = totalPrice;
            this.date = date;
        }

        public int getId() { return id; }
        public int getFertilizerId() { return fertilizerId; }
        public String getFertilizerName() { return fertilizerName; }
        public String getCustomerName() { return customerName; }
        public int getQuantity() { return quantity; }
        public double getTotalPrice() { return totalPrice; }
        public String getDate() { return date; }
    }

    // ------------------ IN-MEMORY LISTS ------------------

    private static final List<FertilizerRecord> fertilizers = new ArrayList<>();
    private static final List<CustomerRecord> customers = new ArrayList<>();
    private static final List<SaleRecord> sales = new ArrayList<>();

    private static int nextFertilizerId = 1;
    private static int nextCustomerId = 1;
    private static int nextSaleId = 1;

    // Static block to preload full sample data on start
    static {
        resetToDefaultData();
    }

    /**
     * Resets data to initial sample records (matching database.sql).
     */
    public static void resetToDefaultData() {
        fertilizers.clear();
        customers.clear();
        sales.clear();

        // 1. Fertilizers
        addFertilizerDirect(1, "Neem Coated Urea (45kg)", "Nitrogenous", 266.50, 150);
        addFertilizerDirect(2, "DAP - Di-Ammonium Phosphate (50kg)", "Phosphatic", 1350.00, 80);
        addFertilizerDirect(3, "MOP - Muriate of Potash (50kg)", "Potassic", 1700.00, 45);
        addFertilizerDirect(4, "NPK 19:19:19 Complex (50kg)", "Complex (NPK)", 1450.00, 60);
        addFertilizerDirect(5, "Single Super Phosphate - SSP (50kg)", "Phosphatic", 550.00, 30);
        addFertilizerDirect(6, "Zinc Sulphate Micronutrient (10kg)", "Micronutrient", 420.00, 8); // Low stock (<10)
        addFertilizerDirect(7, "Organic Vermicompost (40kg)", "Organic", 350.00, 5);              // Low stock (<10)
        addFertilizerDirect(8, "Bio-Potash Liquid (1 Litre)", "Bio-Fertilizer", 480.00, 25);
        nextFertilizerId = 9;

        // 2. Customers
        addCustomerDirect(1, "Ramesh Kumar", "9876543210", "Village Ramgarh, Post Khair");
        addCustomerDirect(2, "Suresh Patel", "9823456789", "Plot 45, Green Valley Farm");
        addCustomerDirect(3, "Anita Sharma", "9712345678", "House 12, Krishi Mandi Road");
        addCustomerDirect(4, "Rajesh Yadav", "9934567812", "Farmhouse 7, Canal Colony");
        addCustomerDirect(5, "Vikram Singh", "9412309876", "Sector 3, Agro Hub");
        nextCustomerId = 6;

        // 3. Past Sales Records
        sales.add(new SaleRecord(1, 1, "Neem Coated Urea (45kg)", "Ramesh Kumar", 10, 2665.00, "2026-10-25 10:30:00"));
        sales.add(new SaleRecord(2, 2, "DAP - Di-Ammonium Phosphate (50kg)", "Suresh Patel", 4, 5400.00, "2026-10-26 14:15:00"));
        sales.add(new SaleRecord(3, 3, "MOP - Muriate of Potash (50kg)", "Anita Sharma", 2, 3400.00, "2026-10-28 11:00:00"));
        sales.add(new SaleRecord(4, 4, "NPK 19:19:19 Complex (50kg)", "Rajesh Yadav", 5, 7250.00, "2026-10-30 09:45:00"));
        nextSaleId = 5;
    }

    private static void addFertilizerDirect(int id, String name, String type, double price, int qty) {
        fertilizers.add(new FertilizerRecord(id, name, type, price, qty));
    }

    private static void addCustomerDirect(int id, String name, String mobile, String address) {
        customers.add(new CustomerRecord(id, name, mobile, address));
    }

    // ------------------ AUTHENTICATION ------------------

    public static String authenticate(String username, String password) {
        if ("admin".equalsIgnoreCase(username) && "admin123".equals(password)) {
            return "Store Administrator";
        }
        if ("staff".equalsIgnoreCase(username) && "staff123".equals(password)) {
            return "Inventory Manager";
        }
        return null;
    }

    // ------------------ FERTILIZER OPERATIONS ------------------

    public static List<FertilizerRecord> getFertilizers(String search) {
        if (search == null || search.trim().isEmpty()) {
            return new ArrayList<>(fertilizers);
        }
        String q = search.trim().toLowerCase();
        List<FertilizerRecord> result = new ArrayList<>();
        for (FertilizerRecord f : fertilizers) {
            if (f.getName().toLowerCase().contains(q) || String.valueOf(f.getId()).equals(q)) {
                result.add(f);
            }
        }
        return result;
    }

    public static FertilizerRecord getFertilizerById(int id) {
        for (FertilizerRecord f : fertilizers) {
            if (f.getId() == id) return f;
        }
        return null;
    }

    public static boolean addFertilizer(String name, String type, double price, int quantity) {
        fertilizers.add(new FertilizerRecord(nextFertilizerId++, name, type, price, quantity));
        return true;
    }

    public static boolean updateFertilizer(int id, String name, String type, double price, int quantity) {
        FertilizerRecord f = getFertilizerById(id);
        if (f != null) {
            f.setName(name);
            f.setType(type);
            f.setPrice(price);
            f.setQuantity(quantity);
            return true;
        }
        return false;
    }

    public static boolean deleteFertilizer(int id) {
        return fertilizers.removeIf(f -> f.getId() == id);
    }

    // ------------------ CUSTOMER OPERATIONS ------------------

    public static List<CustomerRecord> getCustomers(String search) {
        if (search == null || search.trim().isEmpty()) {
            return new ArrayList<>(customers);
        }
        String q = search.trim().toLowerCase();
        List<CustomerRecord> result = new ArrayList<>();
        for (CustomerRecord c : customers) {
            if (c.getName().toLowerCase().contains(q) || c.getMobile().contains(q)) {
                result.add(c);
            }
        }
        return result;
    }

    public static boolean addCustomer(String name, String mobile, String address) {
        customers.add(new CustomerRecord(nextCustomerId++, name, mobile, address));
        return true;
    }

    public static boolean updateCustomer(int id, String name, String mobile, String address) {
        for (CustomerRecord c : customers) {
            if (c.getId() == id) {
                c.setName(name);
                c.setMobile(mobile);
                c.setAddress(address);
                return true;
            }
        }
        return false;
    }

    public static boolean deleteCustomer(int id) {
        return customers.removeIf(c -> c.getId() == id);
    }

    // ------------------ SALES OPERATIONS ------------------

    public static List<SaleRecord> getSales() {
        return new ArrayList<>(sales);
    }

    public static int processSale(int fertilizerId, String customerName, int quantity, double totalPrice) {
        FertilizerRecord f = getFertilizerById(fertilizerId);
        if (f == null || f.getQuantity() < quantity) {
            return -1; // Insufficient stock or invalid id
        }

        // Deduct inventory stock
        f.setQuantity(f.getQuantity() - quantity);

        String dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        int saleId = nextSaleId++;
        sales.add(0, new SaleRecord(saleId, fertilizerId, f.getName(), customerName, quantity, totalPrice, dateStr));
        return saleId;
    }

    // ------------------ STOCK RESTOCK ------------------

    public static boolean updateStock(int fertilizerId, int addQuantity) {
        FertilizerRecord f = getFertilizerById(fertilizerId);
        if (f != null) {
            f.setQuantity(f.getQuantity() + addQuantity);
            return true;
        }
        return false;
    }

    // ------------------ DASHBOARD STATISTICS ------------------

    public static int getTotalFertilizers() {
        return fertilizers.size();
    }

    public static int getTotalCustomers() {
        return customers.size();
    }

    public static int getTotalStock() {
        int total = 0;
        for (FertilizerRecord f : fertilizers) {
            total += f.getQuantity();
        }
        return total;
    }

    public static int getLowStockCount() {
        int count = 0;
        for (FertilizerRecord f : fertilizers) {
            if (f.getQuantity() < 10) {
                count++;
            }
        }
        return count;
    }
}
